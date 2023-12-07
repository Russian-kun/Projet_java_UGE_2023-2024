package fr.uge.TheBigAventure.general;

import fr.uge.lexer.Lexer;
import fr.uge.lexer.Result;
import fr.uge.TheBigAventure.characters.*;
import fr.uge.TheBigAventure.display.Display;
import fr.uge.TheBigAventure.display.Image;
import fr.uge.TheBigAventure.objects.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class World {
  private int height;
  private int width;
  // private String[][] map;
  private Obstacles[][] worldMap;
  private Map<String, String> encodings;
  private Player player;
  private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
  private ArrayList<Item> items = new ArrayList<Item>();
  private ArrayList<Obstacles> obstacles = new ArrayList<Obstacles>();

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    encodings.forEach((key, value) -> sb.append(key + " : " + value + "\n"));
    var tmp = encodings.entrySet();
    for (int i = 0; i < height; i++) {
      sb.append("[");
      for (int j = 0; j < width; j++) {
        for (var tmp2 : tmp)
          if (tmp2.getValue().equals(worldMap[i][j].skin())) {
            sb.append(tmp2.getKey());
            break;
          }
        if (j != width - 1)
          sb.append(", ");
      }
      sb.append("]\n");
    }
    return sb.toString();
  }

  public World(int height, int width, String[][] map, Map<String, String> encodings, ArrayList<Element> existingItems) {
    Objects.requireNonNull(encodings);
    Objects.requireNonNull(existingItems);
    Objects.requireNonNull(map);
    boolean crash = extractObjects(existingItems);
    this.height = height;
    this.width = width;

    this.encodings = encodings;
    crash = createWorldMap(height, width, map) || crash;
    Objects.requireNonNull(player);
    if (crash) {
      System.err.println("Crash while creating world");
      System.exit(1);
    }
  }

  private boolean createWorldMap(int height, int width, String[][] map) {
    this.worldMap = new Obstacles[height][width];
    HashSet<String> tmp = new HashSet<String>();
    boolean crash = false;
    for (int i = 0; i < height; i++)
      for (int j = 0; j < width; j++)
        if (map[i][j] != null) {
          try {
            if (tmp.add(map[i][j]))
              this.worldMap[i][j] = new Obstacles(map[i][j].toUpperCase(), encodings.get(map[i][j]),
                  new Position(j, i));
          } catch (Exception e) {
            e.printStackTrace();
            crash = true;
          }
        }
    return crash;
  }

  private boolean extractObjects(ArrayList<Element> existingItems) {
    Boolean crash = false;
    for (Element tmp : existingItems)
      try {
        if (tmp.getKind() == Element.Kind.PLAYER) {
          if (this.player != null)
            throw new IllegalArgumentException("There can only be one player");
          this.player = (Player) tmp;
        } // player: true | false, il devrait y avoir qu'un seul player
        else if (tmp.getKind() == Element.Kind.ENEMY)
          enemies.add((Enemy) tmp);
        else if (tmp.getKind() == Element.Kind.ITEM)
          this.items.add((Item) tmp);
        else if (tmp.getKind() == Element.Kind.OBSTACLE)
          this.obstacles.add((Obstacles) tmp);
        else
          throw new IllegalArgumentException("Element must be a player, an enemy, an item or an obstacle");
      } catch (Exception e) {
        System.err.println(e.getMessage());
        crash = true;
      }
    return crash;
  }

  public int height() {
    return height;
  }

  public int width() {
    return width;
  }

  public Obstacles[][] worldMap() {
    return worldMap;
  }

  public Map<String, String> encodings() {
    return encodings;
  }

  public Player player() {
    return player;
  }

  public boolean isFree(int x, int y) {
    if (x < 0 || y < 0 || x >= width || y >= height)
      return false;
    return Obstacles.isPassable(worldMap[y][x]);
  }

  static private int[] readSize(Lexer lexer) throws IOException {
    int[] tmp = { 0, 0 };
    Result result;
    for (int i = 0; i < 2; i++) {
      while ((result = lexer.nextResult()) != null && !result.token().name().equals("NUMBER")) {
      }
      tmp[i] = Integer.parseInt(result.content());
    }
    if (tmp[0] <= 0 || tmp[1] <= 0)
      throw new IOException("Error while reading size");
    return tmp;
  }

  private static Map<String, String> readEncoding(Lexer lexer) throws IOException {
    HashMap<String, String> encodings = new HashMap<String, String>();

    Result result;
    while ((result = lexer.nextResult()) != null) {
      String currentName = result.content();
      if (result.token().name().equals("IDENTIFIER")) {
        while ((result = lexer.nextResult()) != null && !result.token().name().equals("IDENTIFIER")) {
        }
        var res = encodings.putIfAbsent(result.content(), currentName);
        if (!(res == null)) {
          throw new IOException("Encoding already exist : " + result.content() + " -> " + currentName + " and "
              + result.content() + " -> " + res);
        }

      } else if (result.token().name().equals("HEADER")) {
        break;
      }
    }
    return Map.copyOf(encodings);
  }

  public static String[][] readData(Lexer lexer) throws IOException {
    Result result;
    while ((result = lexer.nextResult()) != null && !result.token().name().equals("QUOTE")) {
    }
    String[] split = result.content().split("\\n");
    int width = split[1].strip().length(), height = split.length - 2;

    String[][] map = new String[height][width];

    return extractData(split, width, height, map);
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

  private static Element readElement(Lexer lexer) throws IOException {
    // player, item, enemy, obstacle, vehicle
    // Pour l'instant: player, item, obstacle, enemy
    HashMap<String, String> attributes = new HashMap<String, String>();
    addAttributes(lexer, attributes);

    return determineElement(attributes);
  }

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

  private static Element determineElement(HashMap<String, String> attributes) {
    if (attributes.containsKey("player"))
      attributes.put("kind", "player");
    switch (attributes.get("kind")) {
      case "player":
        return new Player(attributes);
      case "item":
        if (attributes.get("damage") != null)
          return new Weapon(attributes);
        return new Item(attributes);
      case "obstacle":
        return new Obstacles(attributes);
      case "enemy":
        return new Enemy(attributes);
      default:
        return null;
    }
  }

  public static World readMap(Path file) throws Exception {
    String text = Files.readString(Path.of("maps/").resolve(file));
    Lexer lexer = new Lexer(text);
    Result result;
    boolean crash = false;

    int[] dimensions = { 0, 0 };
    HashMap<String, String> encodings = new HashMap<String, String>();
    ArrayList<Element> existingItems = new ArrayList<Element>();
    String[][] map = null;

    while ((result = lexer.nextResult()) != null) {
      switch (result.content()) {
        case "size:":
          dimensions = readSize(lexer);
          break;
        case "encodings:":
          encodings.putAll(readEncoding(lexer));
          lexer = new Lexer(text.substring(lexer.lastResult().start()));
          break;
        case "data:":
          map = readData(lexer);
          break;
        case "[element]":
          Element tmp2 = readElement(lexer);
          existingItems.add(tmp2);
          lexer = new Lexer(lexer.text().substring(lexer.lastResult().start()));
          break;
      }
    }
    crash = errorCheck(dimensions, encodings, map);
    if (crash) {
      throw new IOException("Error while reading map");
    }
    return new World(dimensions[1], dimensions[0], map, encodings, existingItems);
  }

  private static boolean errorCheck(int[] dimensions, HashMap<String, String> encodings, String[][] map) {
    boolean crash = false;
    if (dimensions[0] == 0 || dimensions[1] == 0) {
      new IOException("Missing dimensions").printStackTrace();
      crash = true;
    }
    if (encodings.isEmpty()) {
      new IOException("Missing encodings").printStackTrace();
      crash = true;
    } else {
      for (var tmp : encodings.entrySet())
        if (tmp.getKey().length() != 1) {
          new IOException("Invalid encoding : " + tmp.getKey() + " is not a letter").printStackTrace();
          crash = true;
        }
    }
    if (map == null) {
      new IOException("Missing map data").printStackTrace();
      crash = true;
    } else if (map.length != dimensions[1] || map[0].length != dimensions[0]) {
      new IOException("Invalid map dimensions : " + map.length + "x" + map[0].length + " instead of announced "
          + dimensions[1] + "x" + dimensions[0]).printStackTrace();
      crash = true;
    }
    return crash || mapValidation(encodings, map);
  }

  private static boolean mapValidation(HashMap<String, String> encodings, String[][] map) {
    boolean crash = false;
    for (String[] tmp : map)
      for (String tmp2 : tmp)
        if (tmp2 != null && !encodings.containsKey(tmp2)) {
          new IOException("Unknown encoding : " + tmp2).printStackTrace();
          crash = true;
        }
    return crash;
  }

  public void draw(Graphics2D graphics, Display display, HashMap<String, BufferedImage> cachedImages) {
    var start = System.currentTimeMillis();
    clearPreviousPosition(graphics, display, player);
    drawWorldMap(graphics, display, cachedImages);

    drawElement(graphics, display, cachedImages, player);
    drawObstacles(graphics, display, cachedImages);
    drawItems(graphics, display, cachedImages);
    drawEnemies(graphics, display, cachedImages);
    var end = System.currentTimeMillis();
    System.out.println("Draw time : " + (end - start) + "ms");
  }

  public void drawWorldMap(Graphics2D graphics, Display display, HashMap<String, BufferedImage> cachedImages) {
    var start = System.currentTimeMillis();
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (worldMap[i][j] != null) {
          // On va chercher l'image correspondante dans le dossier images si elle n'est
          // pas déjà dans le cache
          // Les images sont nommées comme suit : NOM_0.webp
          // String skin = worldMap[i][j].skin();
          // var image = cachedImages.computeIfAbsent(skin, k -> Image.getImage(skin));
          drawElement(graphics, display, cachedImages, worldMap[i][j]);
        }
      }
    }
    var end = System.currentTimeMillis();
    System.out.println("Draw world map time : " + (end - start) + "ms");
  }

  private void drawObstacles(Graphics2D graphics, Display display, HashMap<String, BufferedImage> cachedImages) {
    var start = System.currentTimeMillis();
    for (int i = 0; i < obstacles.size(); i++) {
      Element tmp = obstacles.get(i);
      drawElement(graphics, display, cachedImages, tmp);
    }
    var end = System.currentTimeMillis();
    System.out.println("Draw obstacles time : " + (end - start) + "ms");
  }

  private void drawItems(Graphics2D graphics, Display display, HashMap<String, BufferedImage> cachedImages) {
    var start = System.currentTimeMillis();
    for (int i = 0; i < items.size(); i++) {
      Element tmp = items.get(i);
      drawElement(graphics, display, cachedImages, tmp);
    }
    var end = System.currentTimeMillis();
    System.out.println("Draw items time : " + (end - start) + "ms");
  }

  private void drawEnemies(Graphics2D graphics, Display display, HashMap<String, BufferedImage> cachedImages) {
    var start = System.currentTimeMillis();
    for (int i = 0; i < enemies.size(); i++) {
      Enemy tmp = enemies.get(i);
      clearPreviousPosition(graphics, display, tmp);
      drawElement(graphics, display, cachedImages, tmp);
    }
    var end = System.currentTimeMillis();
    System.out.println("Draw enemies time : " + (end - start) + "ms");
  }

  private static void drawElement(Graphics2D graphics, Display display, HashMap<String, BufferedImage> cachedImages,
      Element element) {
    var start = System.currentTimeMillis();
    BufferedImage image = cachedImages.computeIfAbsent(element.getSkin(), k -> Image.getImage(element.getSkin()));
    graphics.drawImage(image,
        (int) (element.getPosition().getX() * display.caseSize() + display.shiftX()),
        (int) (element.getPosition().getY() * display.caseSize() + display.shiftY()),
        null);
    var end = System.currentTimeMillis();
    System.out.println("Draw player time : " + (end - start) + "ms");
  }

  private static void clearPreviousPosition(Graphics2D graphics, Display display, Characters character) {
    if (character.getPreviousPosition() != null) {
      Position prev = character.getPreviousPosition();
      graphics.clearRect(
          (int) (prev.getX() * display.caseSize() + display.shiftX()),
          (int) (prev.getY() * display.caseSize() + display.shiftY()),
          (int) display.caseSize(),
          (int) display.caseSize());
    }
  }
}
