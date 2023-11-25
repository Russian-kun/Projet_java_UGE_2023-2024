package fr.uge.TheBigAventure;

import fr.uge.lexer.Lexer;
import fr.uge.lexer.Result;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class World {

  private final int[][] arrayWorld;
  // private final int[] arrayPow = { 0, 0 };
  private final int height;
  private final int width;
  // private final char[] block;
  private final HashMap<String, String> encodings = new HashMap<String, String>();

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

  public World(int x, int y, Map<String, String> encodings) {
    Objects.requireNonNull(encodings);
    arrayWorld = new int[x][y];
    height = x;
    width = y;
    // block = null;
    encodings.forEach((key, value) -> this.encodings.put(key, value));
  }

  static private int[] readSize(String line) {
    int height = 0, width = 0;
    String[] split = line.split("\\(")[1].split("\\)");
    height = Integer.parseInt(split[0].split("x")[0].split(" ")[0]);
    width = Integer.parseInt(split[0].split("x")[1].split(" ")[1]);
    int[] tmp = { height, width };
    return tmp;
  }

  static private int[] readSize(Lexer lexer) throws IOException {
    int height = 0, width = 0;

    Result result;
    for (int i = 0; i < 2; i++) {
      while ((result = lexer.nextResult()) != null && !result.token().name().equals("NUMBER")) {
      }
      if (result.token().name().equals("NUMBER")) {
        if (i == 0)
          height = Integer.parseInt(result.content());
        else
          width = Integer.parseInt(result.content());
      }
    }
    if (height == 0 || width == 0)
      throw new IOException("Error while reading size");
    int[] tmp = { height, width };
    return tmp;

  }

  private static Map<String, String> readEncoding(String line) {
    HashMap<String, String> encodings = new HashMap<String, String>();

    String encodingsString = line.split("encodings: ")[1];
    String currentName = "", currentEncoding = "";
    String values[] = encodingsString.split("\\) ");

    for (int i = 0; i < values.length; i++) {
      String tmpString[] = values[i].split(" ");
      currentName = tmpString[0];
      currentEncoding = tmpString[1].replaceAll("(\\(|\\))", "");
      encodings.put(currentName, currentEncoding);
    }
    return Map.copyOf(encodings);
  }

  private static Map<String, String> readEncoding(Lexer lexer) throws IOException {
    String currentName = "", currentEncoding = "";
    HashMap<String, String> encodings = new HashMap<String, String>();

    Result result;
    while ((result = lexer.nextResult()) != null) {
      if (result.token().name().equals("IDENTIFIER")) {
        currentName = result.content();
        while ((result = lexer.nextResult()) != null &&
            !result.token().name().equals("IDENTIFIER")) {
        }
        currentEncoding = result.content();
        if (!(encodings.putIfAbsent(currentEncoding, currentName) == null)) {
          throw new IOException("Encoding already exist : " + currentEncoding + " -> " + currentName);
        }

      } else if (result.token().name().equals("NEWLINE")) {
        break;
      }
    }

    return Map.copyOf(encodings);
  }

  public static World readMap(String file) throws IOException {
    int height = 0, width = 0;
    HashMap<String, String> encodings = new HashMap<String, String>();

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
        } else if (result.content().equals("encodings")) {
          try {
            encodings.putAll(readEncoding(lexer));
          } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error while reading encodings");
          }
        } else if (result.content().equals("data")) {
          break;
        }
      }
    }
    return new World(height, width, encodings);
  }

}
