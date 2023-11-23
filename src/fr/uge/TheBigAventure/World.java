package fr.uge.TheBigAventure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;

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

  static private int[] readSize(String Line) {
    String[] split;
    int height;
    int width;
    split = Line.split("\\(")[1].split("\\)");
    height = Integer.parseInt(split[0].split("x")[0].split(" ")[0]);
    width = Integer.parseInt(split[0].split("x")[1].split(" ")[1]);
    int[] tmp = { height, width };
    return tmp;

  }

  private static Map<String, String> readEncoding(String Line) {
    String encodingsString = Line.split("encodings: ")[1];

    String currentName = "", currentEncoding = "";
    String values[] = encodingsString.split("\\) ");

    HashMap<String, String> encodings = new HashMap<String, String>();

    for (int i = 0; i < values.length; i++) {
      String tmpString[] = values[i].split(" ");
      currentName = tmpString[0];
      currentEncoding = tmpString[1].replaceAll("(\\(|\\))", "");

      encodings.put(currentName, currentEncoding);
    }
    return Map.copyOf(encodings);
  }

  public static World readMap(String file) throws IOException {
    int height = 0, width = 0;
    HashMap<String, String> encodings = new HashMap<String, String>();

    try (var reader = Files.newBufferedReader(Paths.get("maps/" + file))) {
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

}
