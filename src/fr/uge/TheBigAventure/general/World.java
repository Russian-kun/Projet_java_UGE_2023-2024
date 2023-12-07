package fr.uge.TheBigAventure.general;

import fr.uge.TheBigAventure.objects.Element;
import fr.uge.TheBigAventure.objects.Item;
import fr.uge.TheBigAventure.objects.Obstacles;
import fr.uge.TheBigAventure.objects.Weapon;
import fr.uge.TheBigAventure.personnages.Enemy;
import fr.uge.TheBigAventure.personnages.Player;
import fr.uge.lexer.Lexer;
import fr.uge.lexer.Result;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record World(int height, int width, String[][] map, Map<String, String> encodings,
    ArrayList<Element> existingItems) {

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    encodings.forEach((key, value) -> sb.append(key + " : " + value + "\n"));
    for (int i = 0; i < height; i++) {
      sb.append("[");
      for (int j = 0; j < width; j++) {
        sb.append(map[i][j]);
        if (j != width - 1)
          sb.append(", ");
      }
      sb.append("]\n");
    }
    return sb.toString();
  }

  public World {
    Objects.requireNonNull(encodings);
    Objects.requireNonNull(existingItems);
    Objects.requireNonNull(map);
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
          throw new IOException("Encoding already exist : " + result.content() + " -> " + currentName);
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
        throw new IOException("Inconsistant map width");
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

  public static World readMap(Path file) throws IOException {
    Path path = Path.of("maps/").resolve(file);
    String text = Files.readString(path);
    Lexer lexer = new Lexer(text);
    Result result;

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
          if (map.length != dimensions[1] || map[0].length != dimensions[0])
            throw new IOException("Inconsistant map size");
          break;
        case "[element]":
          Element tmp2 = readElement(lexer);
          existingItems.add(tmp2);
          lexer = new Lexer(lexer.text().substring(lexer.lastResult().start()));
          break;
      }
    }
    return new World(dimensions[1], dimensions[0], map, encodings, existingItems);
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
