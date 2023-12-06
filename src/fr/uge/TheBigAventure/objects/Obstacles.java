package fr.uge.TheBigAventure.objects;

import java.util.Map;

import fr.uge.TheBigAventure.general.Position;

public class Obstacles extends Element {
  private String name;

  public enum ObstacleType {
    BED, BOG, BOMB, BRICK, CHAIR, CLIFF, DOOR, FENCE, FORT, GATE, HEDGE, HOUSE, HUSK, HUSKS, LOCK, MONITOR, PIANO,
    PILLAR, PIPE, ROCK, RUBBLE, SHELL, SIGN, SPIKE, STATUE, STUMP, TABLE, TOWER, TREE, TREES, WALL
  }

  public Obstacles(String name, ObstacleType skin, Position position) {
    super(skin.toString(), position, Element.Kind.OBSTACLE);
    this.name = name;
  }

  public Obstacles(Map<String, String> attributes) {
    super(attributes);
  }

  public String getName() {
    return name;
  }
}