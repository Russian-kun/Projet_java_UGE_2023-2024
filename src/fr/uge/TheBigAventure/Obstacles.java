package fr.uge.TheBigAventure;

import java.util.Objects;

/**
 * Class representant les obstacles du jeu.
 * 
 * @param name      type de l'obstacle.
 * @param character character sous lequel l'obstacle est represente sur la map.
 */
public class Obstacles {
  private final Type type;
  private final Characters character;

  /**
   * Enum des noms possibles des obstacles.
   */
  public enum Type {
    BED, BOG, BOMB, BRICK, CHAIR, CLIFF, DOOR, FENCE, FORT, GATE, HEDGE, HOUSE, HUSK, HUSKS, LOCK, MONITOR, PIANO,
    PILLAR, PIPE, ROCK, RUBBLE, SHELL, SIGN, SPIKE, STATUE, STUMP, TABLE, TOWER, TREE, TREES, WALL
  }

  public Obstacles(Type name, Characters character) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(character);
    this.type = name;
    this.character = character;
  }

}