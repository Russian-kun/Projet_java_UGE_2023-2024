package fr.uge.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.uge.TheBigAventure.characters.Enemy;
import fr.uge.TheBigAventure.characters.Friend;
import fr.uge.TheBigAventure.characters.Player;
import fr.uge.TheBigAventure.gameObjects.Element;
import fr.uge.TheBigAventure.gameObjects.Item;
import fr.uge.TheBigAventure.gameObjects.Obstacle;
import fr.uge.TheBigAventure.general.Encoding;
import fr.uge.TheBigAventure.general.World;
import fr.uge.TheBigAventure.general.WorldMap;

public class Parser {

  /**
   * Read a map from a file and return a World object
   * 
   * @param file
   * @return a World object
   * @throws IOException if the file is not found or if there is an error while
   *                     reading it
   */
  public static World readMap(Path file) throws IOException {
    String text = Files.readString(Path.of("resources/maps/").resolve(file));
    Lexer lexer = new Lexer(text);
    ParsingException exceptions = new ParsingException(null);

    int[] dimensions = parseSize(lexer, exceptions);
    Map<String, String> encodings = parseEncoding(lexer, exceptions);
    String[][] data = parseData(lexer, exceptions);
    WorldMap map = parseMap(data, encodings);
    ArrayList<Element> foundElements = parseElements(lexer, exceptions);
    ArrayList<Enemy> enemies = new ArrayList<>();
    ArrayList<Item> items = new ArrayList<>();
    ArrayList<Obstacle> obstacles = new ArrayList<>();
    ArrayList<Friend> friends = new ArrayList<>();
    Player player = separateElements(foundElements, enemies, friends, items, obstacles, exceptions);

    Encoding encoding = new Encoding(encodings);
    exceptions.addAllException(errorCheck(dimensions, encodings, map));
    if (exceptions.size() != 0) {
      System.err.println(exceptions);
      return null;
    }
    return new World(player, map, encoding, enemies, friends, items, obstacles);
  }

  /**
   * Parse the encodings of the map
   * 
   * @param lexer
   * @return
   * @throws IOException
   */
  private static Map<String, String> parseEncoding(Lexer lexer, ParsingException pe) {
    HashMap<String, String> encodings = new HashMap<String, String>();

    Result result;
    while ((result = lexer.nextResult()) != null) {
      String currentName = result.content();
      if (result.token().name().equals("IDENTIFIER")) {
        result = findNextIdentifier(lexer, "IDENTIFIER");
        var res = encodings.putIfAbsent(result.content(), currentName);
        if (!(res == null)) {
          pe.addException(new IOException(
              "Encoding already exist line " + lexer.line() + " : " + result.content() + " -> " + currentName + " and "
                  + result.content() + " -> " + res));
        }

      } else if (result.token().name().equals("HEADER") && !result.content().equals("encodings:")) {
        break;
      }
    }
    return encodings;
  }

  private static Result findNextIdentifier(Lexer lexer, String identifier) {
    Result result;
    while ((result = lexer.nextResult()) != null && !result.token().name().equals(identifier)) {
    }
    return result;
  }

  /**
   * Parse the data of the map
   * 
   * @param lexer
   * @return
   * @throws IOException
   */
  private static String[][] parseData(Lexer lexer, ParsingException pe) {
    Result result = findNextIdentifier(lexer, "QUOTE");
    String[] split = result.content().split("\\n");
    int width = split[1].strip().length(), height = split.length - 2;

    return extractData(split, width, height, new String[height][width], pe);
  }

  private static String[][] extractData(String[] split, int width, int height, String[][] map, ParsingException pe) {
    for (int currHeight = 1; currHeight < height + 1; currHeight++) {
      String s = split[currHeight].strip();
      if (width != s.length())
        pe.addException(new IOException("Inconsistent map width : line " + currHeight + " is " + s.length() + " long"));
      int currWidth = 0;

      for (String tmp2 : s.split("")) {
        if (!tmp2.equals(" "))
          map[currHeight - 1][currWidth] = tmp2;
        currWidth++;
      }
    }
    return map;
  }

  private static ArrayList<Element> parseElements(Lexer lexer, ParsingException exceptions) {
    ArrayList<Element> elements = new ArrayList<>();
    Result result;
    while ((result = lexer.nextResult()) != null && result.content().equals("[element]")) {
      Element element = parseElement(lexer, exceptions);
      elements.add(element);
      lexer = new Lexer(lexer.text().substring(lexer.lastResult().start()));
    }
    return elements;
  }

  private static WorldMap parseMap(String[][] data, Map<String, String> encodings) {
    Encoding encoding = new Encoding(encodings);
    return WorldMap.interpretMap(data, encoding);
  }

  private static Player separateElements(List<Element> elements, ArrayList<Enemy> enemies, ArrayList<Friend> friends,
      ArrayList<Item> items, ArrayList<Obstacle> obstacles, ParsingException pe) {
    Player player = null;
    for (Element element : elements) {
      switch (element) {
        case Enemy e -> enemies.add(e);
        case Item i -> items.add(i);
        case Obstacle o -> obstacles.add(o);
        case Player p -> player = p;
        case Friend f -> friends.add(f);
        default -> pe.addException(new IOException("Unknown element : " + element.getSkin()));
      }
    }
    return player;
  }

  /**
   * Parse an element from the lexer
   * 
   * @param lexer
   * @return
   * @throws IOException
   */
  private static Element parseElement(Lexer lexer, ParsingException pe) {
    HashMap<String, String> attributes = new HashMap<String, String>();
    addAttributes(lexer, attributes, pe);

    return Element.valueOf(attributes);
  }

  /**
   * Fill the attributes map with the attributes found by the lexer
   * 
   * @param lexer
   * @param attributes
   * @throws IOException
   */
  private static void addAttributes(Lexer lexer, HashMap<String, String> attributes, ParsingException pe) {
    Result result;
    while ((result = lexer.nextResult()) != null) {
      if (!result.token().name().equals("IDENTIFIER"))
        break;
      String key = result.content();
      result = findNextIdentifier(lexer, "COLON");
      result = lexer.nextResult();
      if (result.content().equals(""))
        pe.addException(new IOException("Error while reading element"));
      attributes.put(key, result.content());
    }
  }

  /**
   * Parse the size of the map
   * 
   * @param lexer
   * @return
   * @throws IOException
   */
  private static int[] parseSize(Lexer lexer, ParsingException pe) {
    int[] tmp = { 0, 0 };
    Result result;
    for (int i = 0; i < 2; i++) {
      result = findNextIdentifier(lexer, "NUMBER");
      tmp[i] = Integer.parseInt(result.content());
    }
    if (tmp[0] <= 0 || tmp[1] <= 0)
      pe.addException(new IOException("Error while reading size: line " + lexer.line()));
    return tmp;
  }

  private static List<Exception> errorCheck(int[] dimensions, Map<String, String> encodings, WorldMap map) {
    ArrayList<Exception> list = new ArrayList<>();
    checkDimensions(dimensions, list);
    checkEncodings(encodings, list);
    checkMapData(dimensions, map, list);
    list.addAll(mapValidation(encodings, map.map()));
    return list;
  }

  private static void checkDimensions(int[] dimensions, List<Exception> list) {
    if (dimensions[0] == 0 || dimensions[1] == 0)
      list.add(new IOException("Missing dimensions"));
  }

  private static void checkEncodings(Map<String, String> encodings, List<Exception> list) {
    if (encodings.isEmpty())
      list.add(new IOException("Missing encodings"));
    else {
      for (var tmp : encodings.entrySet())
        if (tmp.getKey().length() != 1)
          list.add(new IOException("Invalid encoding : " + tmp.getKey() + " is not a letter"));
    }
  }

  private static void checkMapData(int[] dimensions, WorldMap map, List<Exception> list) {
    if (map == null)
      list.add(new IOException("Missing map data"));
    else if (map.height() != dimensions[1] || map.width() != dimensions[0])
      list.add(new IOException("Invalid map dimensions : " + map.height() + "x" + map.width() + " instead of announced "
          + dimensions[1] + "x" + dimensions[0]));
  }

  private static List<Exception> mapValidation(Map<String, String> encodings, Obstacle[][] map) {
    ArrayList<Exception> list = new ArrayList<>();
    for (var col : map)
      for (var line : col)
        if (line != null && !encodings.containsKey(line.getName()))
          list.add(new IOException("Unknown encoding : " + line.getName()));
    return list;
  }
}
