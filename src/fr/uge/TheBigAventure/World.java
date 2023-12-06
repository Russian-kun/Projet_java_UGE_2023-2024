package fr.uge.TheBigAventure;

import fr.uge.lexer.Lexer;
import fr.uge.lexer.Result;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class World {
  private final int[][] arrayWorld;
  private final int height;
  private final int width;
  private final HashMap<String, String> encodings = new HashMap<>();
  private final ArrayList<Element> existingItems;

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    encodings.forEach((key, value) -> sb.append(key + " : " + value + "\n"));
    for (int i = 0; i < height; i++) {
      sb.append("[");
      for (int j = 0; j < width; j++) {
        sb.append(arrayWorld[i][j]);
        if (j != width - 1)
          sb.append(", ");
      }
      sb.append("]\n");
    }
    return sb.toString();
  }

  public World(int x, int y, Map<String, String> encodings, ArrayList<Element> itemList) {
    Objects.requireNonNull(encodings);
    Objects.requireNonNull(itemList);
    arrayWorld = new int[x][y];
    height = x;
    width = y;
    encodings.forEach((key, value) -> this.encodings.put(key, value));
    existingItems = itemList;
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
    String currentName = "", currentEncoding = "";
    HashMap<String, String> encodings = new HashMap<String, String>();

    Result result;
    while ((result = lexer.nextResult()) != null) {
      currentName = result.content();
      if (result.token().name().equals("IDENTIFIER")) {
        while ((result = lexer.nextResult()) != null &&
            !result.token().name().equals("IDENTIFIER")) {
        }
        currentEncoding = result.content();
        var res = encodings.putIfAbsent(currentEncoding, currentName);
        if (!(res == null)) {
          throw new IOException("Encoding already exist : " + currentEncoding + " -> " + currentName);
        }

      } else if (result.token().name().equals("HEADER")) {
        break;
      }
    }

    return Map.copyOf(encodings);
  }

  public static String[][] readData(Lexer lexer) throws IOException {
    Result result;
    int width = 0, height = 0;
    // On cherche le debut de la map
    while ((result = lexer.nextResult()) != null && !result.token().name().equals("QUOTE")) {
    }

    var split = result.content().split("\\n");
    height = split.length - 2;
    width = split[1].strip().length();
    String[][] map = new String[height][width];

    for (int currHeight = 1; currHeight < height + 1; currHeight++) {
      String s = split[currHeight].strip();

      if (width != s.length())
        throw new IOException("Inconsistant map width");

      String[] individualChars = s.split("");
      int currWidth = 0;

      for (String tmp2 : individualChars) {
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
    Result result;
    HashMap<String, String> attributes = new HashMap<String, String>();

    while ((result = lexer.nextResult()) != null) {
      if (!result.token().name().equals("IDENTIFIER"))
        break;
      String name = result.content();
      while ((result = lexer.nextResult()) != null && result.token().name().equals("COLON")) {
      }
      String value = result.content();
      if (value.equals(""))
        throw new IOException("Error while reading element");
      attributes.put(name, value);

    }

    if (attributes.containsKey("player")) {
      attributes.put("kind", "player");
    }

    switch (attributes.get("kind")) {
      case "player":
        return new Player(attributes);
      case "item":
        if (attributes.get("damage") != null)
          return new Weapon(attributes);
        return new Item(attributes);
      case "obstacle":
        return new Obstacle(attributes);
      case "enemy":
        return new Enemy(attributes);
      default:
        return null;
    }
  }

  public static World readMap(Path file) throws IOException {
    int height = 0, width = 0;
    HashMap<String, String> encodings = new HashMap<String, String>();
    ArrayList<Element> existingItems = new ArrayList<Element>();

    Path path = Path.of("maps/").resolve(file);
    String text = Files.readString(path);
    Lexer lexer = new Lexer(text);
    String[][] map = null;
    Result result;

    while ((result = lexer.nextResult()) != null) {
      System.out.println(result);
      if (!result.token().name().equals("HEADER"))
        continue;
      switch (result.content()) {
        case "size:":
          int[] tmp;
          try {
            tmp = readSize(lexer);
          } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error while reading size");
          }
          width = tmp[0];
          height = tmp[1];

          break;

        case "encodings:":
          try {
            encodings.putAll(readEncoding(lexer));
            lexer = new Lexer(text.substring(lexer.lastResult().start()));
          } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error while reading encodings");
          }
          break;

        case "data:":
          try {
            map = readData(lexer);
            if (map.length != height || map[0].length != width)
              throw new IOException("Inconsistant map size");
          } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error while reading map data");
          }
          break;

        case "[element]":
          try {
            Element tmp2 = readElement(lexer);
            existingItems.add(tmp2);
            lexer = new Lexer(lexer.text().substring(lexer.lastResult().start()));
          } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error while reading map elements");
          }
          break;
      }

    }
    return new World(height, width, encodings, existingItems);
  }
}
