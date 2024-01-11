package fr.uge.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.uge.TheBigAventure.characters.Enemy;
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
    String text = Files.readString(Path.of("maps/").resolve(file));
    Lexer lexer = new Lexer(text);
    Result result;

    int[] dimensions = { 0, 0 };
    Map<String, String> encodings = null;
    String[][] data = null;
    WorldMap map = null;
    final ArrayList<Element> foundElements = new ArrayList<>();
    final ArrayList<Exception> exceptions = new ArrayList<>();
    final ArrayList<Enemy> enemies = new ArrayList<>();
    final ArrayList<Item> items = new ArrayList<>();
    final ArrayList<Obstacle> obstacles = new ArrayList<>();
    Player player = null;

    while ((result = lexer.nextResult()) != null) {
      try {
        switch (result.content()) {
          case "size:":
            dimensions = parseSize(lexer);
            break;
          case "encodings:":
            encodings = parseEncoding(lexer);
            lexer = new Lexer(text.substring(lexer.lastResult().start()));
            break;
          case "data:":
            data = parseData(lexer);
            break;
          case "[element]":
            Element tmp2 = parseElement(lexer);
            switch (tmp2) {
              case Enemy e -> enemies.add(e);
              case Item i -> items.add(i);
              case Obstacle o -> obstacles.add(o);
              case Player p -> player = p;
              default -> throw new IllegalArgumentException("Unknown element");
            }
            foundElements.add(tmp2);
            lexer = new Lexer(lexer.text().substring(lexer.lastResult().start()));
            break;
        }
      } catch (Exception e) {
        exceptions.add(e);
      }
    }

    Encoding encoding = new Encoding(encodings);
    map = WorldMap.interpretMap(data, encoding);
    exceptions.addAll(errorCheck(dimensions, encodings, map));
    if (exceptions.size() != 0) {
      for (var e : exceptions)
        System.err.println(e.getMessage());
      System.err.println("Error while parsing map");
      return null;
    }
    return new World(player, map, encoding, enemies, items, obstacles);
  }

  private static Element parseElement(Lexer lexer) throws IOException {
    HashMap<String, String> attributes = new HashMap<String, String>();
    addAttributes(lexer, attributes);

    return Element.valueOf(attributes);
  }

  /**
   * Fill the attributes map with the attributes found by the lexer
   * 
   * @param lexer
   * @param attributes
   * @throws IOException
   */
  private static void addAttributes(Lexer lexer, HashMap<String, String> attributes) throws IOException {
    Result result;
    while ((result = lexer.nextResult()) != null) {
      if (!result.token().name().equals("IDENTIFIER"))
        break;
      String name = result.content();
      while ((result = lexer.nextResult()) != null && result.token().name().equals("COLON")) {
      }
      if (result.content().equals(""))
        throw new IOException("Error while reading element");
      attributes.put(name, result.content());
    }
  }

  private static int[] parseSize(Lexer lexer) throws IOException {
    int[] tmp = { 0, 0 };
    Result result;
    for (int i = 0; i < 2; i++) {
      while ((result = lexer.nextResult()) != null && !result.token().name().equals("NUMBER")) {
      }
      tmp[i] = Integer.parseInt(result.content());
    }
    if (tmp[0] <= 0 || tmp[1] <= 0)
      throw new IOException("Error while reading size: line " + lexer.line());
    return tmp;
  }

  private static Map<String, String> parseEncoding(Lexer lexer) throws IOException {
    HashMap<String, String> encodings = new HashMap<String, String>();

    Result result;
    while ((result = lexer.nextResult()) != null) {
      String currentName = result.content();
      if (result.token().name().equals("IDENTIFIER")) {
        while ((result = lexer.nextResult()) != null && !result.token().name().equals("IDENTIFIER")) {
        }
        var res = encodings.putIfAbsent(result.content(), currentName);
        if (!(res == null)) {
          throw new IOException(
              "Encoding already exist line " + lexer.line() + " : " + result.content() + " -> " + currentName + " and "
                  + result.content() + " -> " + res);
        }

      } else if (result.token().name().equals("HEADER")) {
        break;
      }
    }
    return encodings;
  }

  private static String[][] parseData(Lexer lexer) throws IOException {
    Result result;
    while ((result = lexer.nextResult()) != null && !result.token().name().equals("QUOTE")) {
    }
    String[] split = result.content().split("\\n");
    int width = split[1].strip().length(), height = split.length - 2;

    return extractData(split, width, height, new String[height][width]);
  }

  private static String[][] extractData(String[] split, int width, int height, String[][] map) throws IOException {
    for (int currHeight = 1; currHeight < height + 1; currHeight++) {
      String s = split[currHeight].strip();
      if (width != s.length())
        throw new IOException("Inconsistant map width : line " + currHeight + " is " + s.length() + " long");
      int currWidth = 0;

      for (String tmp2 : s.split("")) {
        if (!tmp2.equals(" "))
          map[currHeight - 1][currWidth] = tmp2;
        currWidth++;
      }
    }
    return map;
  }

  private static List<Exception> errorCheck(int[] dimensions, Map<String, String> encodings, WorldMap map) {
    ArrayList<Exception> list = new ArrayList<>();
    if (dimensions[0] == 0 || dimensions[1] == 0)
      list.add(new IOException("Missing dimensions"));
    if (encodings.isEmpty())
      list.add(new IOException("Missing encodings"));
    else {
      for (var tmp : encodings.entrySet())
        if (tmp.getKey().length() != 1)
          list.add(new IOException("Invalid encoding : " + tmp.getKey() + " is not a letter"));
    }
    if (map == null)
      list.add(new IOException("Missing map data"));
    else if (map.height() != dimensions[1] || map.width() != dimensions[0])
      list.add(new IOException("Invalid map dimensions : " + map.height() + "x" + map.width() + " instead of announced "
          + dimensions[1] + "x" + dimensions[0]));

    list.addAll(mapValidation(encodings, map.map()));
    return list;
  }

  private static List<Exception> mapValidation(Map<String, String> encodings, Obstacle[][] map) {
    ArrayList<Exception> list = new ArrayList<>();
    for (var col : map)
      for (var lin : col)
        if (lin != null && !encodings.containsKey(lin.getName()))
          list.add(new IOException("Unknown encoding : " + lin.getName()));
    return list;
  }
}
