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
  // private final int[] arrayPow = { 0, 0 };
  private final int height;
  private final int width;
  private final HashMap<String, String> encodings = new HashMap<>();
  private final ArrayList<Items> existingItems;

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

  public World(int x, int y, Map<String, String> encodings, ArrayList<Items> itemList) {
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
        if (currentName.equals("data")) {
          break;
        }
        while ((result = lexer.nextResult()) != null &&
            !result.token().name().equals("IDENTIFIER")) {
        }
        currentEncoding = result.content();
        var res = encodings.putIfAbsent(currentEncoding, currentName);
        if (!(res == null)) {
          throw new IOException("Encoding already exist : " + currentEncoding + " -> " + currentName);
        }

      }
    }

    return Map.copyOf(encodings);
  }

  public static World readMap(String file) throws IOException {
    try {
      return readMap(Path.of(file));
    } catch (Exception e) {
      e.printStackTrace();
      throw new IOException("Error while reading map");
    }
  }

    try (var reader = Files.newBufferedReader(Path.of("maps/").resolve(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("size")) {
          int[] tmp;
          tmp = readSize(line.split("\n")[0]);
          height = tmp[0];
          width = tmp[1];
        } else if (line.startsWith("encodings"))
          encodings.putAll(readEncoding(line.split("\n")[0]));
        else if (line.startsWith("data: "))
          break;
      }

    }
    return new World(height, width, encodings);
  }

  public static World readMap(Path file) throws IOException {
    int height = 0, width = 0;
    HashMap<String, String> encodings = new HashMap<String, String>();

    Path path = Path.of("maps/").resolve(file);
    String text = Files.readString(path);
    Lexer lexer = new Lexer(text);
    Result result;

    // TODO remplacer par un switch
    while ((result = lexer.nextResult()) != null) {
      System.out.println(result);
      if (result.token().name().equals("IDENTIFIER")) {
        if (result.content().equals("size")) {
          int[] tmp;
          try {
            tmp = readSize(lexer);
          } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error while reading size");
          }
          height = tmp[0];
          width = tmp[1];

        }
        if (result.content().equals("encodings") ||
            lexer.lastResult().content().equals("encodings")) {
          try {
            encodings.putAll(readEncoding(lexer));
          } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error while reading encodings");
          }

        }
        if (result.content().equals("data") || lexer.lastResult().content().equals("data")) {
          try {
            // var res =
            readData(lexer);

          } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error while reading map data");
          }

      }
    }
    return new World(height, width, encodings);
  }

}
