package fr.uge.TheBigAventure.general;

import java.util.Objects;

import fr.uge.TheBigAventure.gameObjects.Obstacle;

public record WorldMap(int width, int height, Obstacle[][] map) {

  public WorldMap {
    Objects.requireNonNull(map);
    if (map.length != height)
      throw new IllegalArgumentException("The height of the map is not correct");
    if (map[0].length != width)
      throw new IllegalArgumentException("The width of the map is not correct");
  }

  public static WorldMap interpretMap(String[][] stringMap, Encoding encoding) {
    Obstacle[][] map = new Obstacle[stringMap.length][stringMap[0].length];
    for (int y = 0; y < stringMap.length; y++) {
      for (int x = 0; x < stringMap[0].length; x++) {
        if (stringMap[y][x] != null && !encoding.encodings().containsKey(stringMap[y][x]))
          throw new IllegalArgumentException("The encoding " + stringMap[y][x] + " is not defined");
        if (stringMap[y][x] == null)
          map[y][x] = null;
        else
          map[y][x] = new Obstacle(stringMap[y][x], Obstacle.getObstacleType(encoding.encodings().get(stringMap[y][x])),
              new Position(x, y));
      }
    }
    return new WorldMap(stringMap[0].length, stringMap.length, map);
  }
}
