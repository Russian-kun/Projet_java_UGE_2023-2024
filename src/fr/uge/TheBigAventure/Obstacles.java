package fr.uge.TheBigAventure;

import java.util.Map;

class Obstacle extends Element {
  private String name;

  public enum ObstacleType {
    BED, BOG, BOMB, BRICK, CHAIR, CLIFF, DOOR, FENCE, FORT, GATE, HEDGE, HOUSE, HUSK, HUSKS, LOCK, MONITOR, PIANO,
    PILLAR, PIPE, ROCK, RUBBLE, SHELL, SIGN, SPIKE, STATUE, STUMP, TABLE, TOWER, TREE, TREES, WALL
  }

  public Obstacle(String name, ObstacleType skin, Position position) {
    super(skin.toString(), position, Element.Kind.OBSTACLE);
    this.name = name;
  }

  public Obstacle(Map<String, String> attributes) {
    super(attributes);
  }

  public String getName() {
    return name;
  }
}