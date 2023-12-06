package fr.uge.TheBigAventure;

import java.util.Map;

class Obstacle extends Element {

  public enum ObstacleType {
    BED, BOG, BOMB, BRICK, CHAIR, CLIFF, DOOR, FENCE, FORT, GATE, HEDGE, HOUSE, HUSK, HUSKS, LOCK, MONITOR, PIANO,
    PILLAR, PIPE, ROCK, RUBBLE, SHELL, SIGN, SPIKE, STATUE, STUMP, TABLE, TOWER, TREE, TREES, WALL
  }

  public Obstacle(String name, ObstacleType skin, Position position) {
    super(name, skin.toString(), position, Element.Kind.OBSTACLE);
  }

  public Obstacle(Map<String, String> attributes) {
    super(attributes);
  }
}