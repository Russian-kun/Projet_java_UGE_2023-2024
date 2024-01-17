package fr.uge.TheBigAventure.gameObjects;

import java.util.Map;

import fr.uge.TheBigAventure.general.Position;

public class Obstacle extends Element {
  private String name;

  public enum ImpassableType implements ObstacleType {
    BED, BOG, BOMB, BRICK, CHAIR, CLIFF, DOOR, FENCE, FORT, GATE, HEDGE, HOUSE, HUSK, HUSKS, LOCK, MONITOR, PIANO,
    PILLAR, PIPE, ROCK, RUBBLE, SHELL, SIGN, SPIKE, STATUE, STUMP, TABLE, TOWER, TREE, TREES, WALL,
    ICE, LAVA, WATER
  }

  public enum PassableType implements ObstacleType {
    ALGAE, CLOUD, FLOWER, FOLIAGE, GRASS, LADDER, LILY, PLANK, REED, ROAD, SPROUT, TILE, TRACK, VINE
  }

  public Obstacle(String name, ObstacleType skin, Position position) {
    super(skin, position, Element.Kind.OBSTACLE);
    this.name = name;
  }

  public static Obstacle valueOf(Map<String, String> attributes) {
    return new Obstacle(attributes.get("name"), getObstacleType(attributes.get("skin")),
        Position.valueOf(attributes.get("position")));
  }

  public String getName() {
    return name;
  }

  public ObstacleType skin() {
    return (ObstacleType) skin;
  }

  public static boolean isPassable(Obstacle obstacle) {
    if (obstacle == null)
      return true;
    return switch (obstacle.skin()) {
      case ImpassableType it -> false;
      case Door.DoorType dt -> false;
      case PassableType pt -> true;
    };
  }

  public static ObstacleType getObstacleType(String skin) {
    try {
      return ImpassableType.valueOf(skin.toUpperCase());
    } catch (Exception e) {
      try {
        return PassableType.valueOf(skin.toUpperCase());
      } catch (Exception e2) {
        return null;
      }
    }
  }
}