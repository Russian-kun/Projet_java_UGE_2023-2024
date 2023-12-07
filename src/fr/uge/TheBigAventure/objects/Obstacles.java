package fr.uge.TheBigAventure.objects;

import java.util.Map;

import fr.uge.TheBigAventure.general.Position;

public class Obstacles extends Element {
  private String name;

  public enum ImpassableType implements ObstacleType {
    BED, BOG, BOMB, BRICK, CHAIR, CLIFF, DOOR, FENCE, FORT, GATE, HEDGE, HOUSE, HUSK, HUSKS, LOCK, MONITOR, PIANO,
    PILLAR, PIPE, ROCK, RUBBLE, SHELL, SIGN, SPIKE, STATUE, STUMP, TABLE, TOWER, TREE, TREES, WALL,
    ICE, LAVA, WATER
  }

  public enum PassableType implements ObstacleType {
    ALGAE, CLOUD, FLOWER, FOLIAGE, GRASS, LADDER, LILY, PLANK, REED, ROAD, SPROUT, TILE, TRACK, VINE
  }

  public Obstacles(String name, ObstacleType skin, Position position) {
    super(skin.toString(), position, Element.Kind.OBSTACLE);
    this.name = name;
  }

  public Obstacles(String name, String skin, Position position) {
    super(skin.toString(), position, Element.Kind.OBSTACLE);
    try {
      ImpassableType.valueOf(skin.toUpperCase());
    } catch (IllegalArgumentException e) {
      try {
        PassableType.valueOf(skin.toUpperCase());
      } catch (IllegalArgumentException e2) {
        throw new IllegalArgumentException("skin must be an obstacle : " + skin + " is not an obstacle type");
      }
    }
    this.name = name;
  }

  public Obstacles(Map<String, String> attributes) {
    super(attributes);
  }

  public String getName() {
    return name;
  }

  public String skin() {
    return skin;
  }

  public static boolean isPassable(Obstacles obstacle) {
    if (obstacle == null)
      return true;
    try {
      return PassableType.valueOf(obstacle.skin().toUpperCase()) != null;
    } catch (Exception e) {
      return false;
    }
  }
}