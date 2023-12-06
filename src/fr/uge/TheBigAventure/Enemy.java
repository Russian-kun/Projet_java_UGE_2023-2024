package fr.uge.TheBigAventure;

import java.util.Map;

// un ennemi est défini comme cela
//                   [element]
//                     name: Waldo
//                     skin: CRAB
//                     position: (1, 3)
//                     kind: enemy
//                     health: 10
//                     zone: (1, 1) (5 x 3)
//                     behavior: agressive
//                     damage: 10
class Enemy extends Element {
  private String name;
  private int health;
  private Zone zone;
  private Behavior behavior;
  private int damage;

  // shy, stroll et agressive
  public enum Behavior {
    SHY,
    STROLL,
    AGRESSIVE
  }

  public Enemy(String name, String skin, Position position, int health, Zone zone, String behavior, int damage) {
    super(skin, position, Element.Kind.ENEMY);
    if (damage < 0)
      throw new IllegalArgumentException("damage must be positive");
    if (health < 0)
      throw new IllegalArgumentException("health must be positive");
    this.name = name;
    this.health = health;
    this.zone = zone;
    this.behavior = Behavior.valueOf(behavior.toUpperCase());
    this.damage = damage;
  }

  public Enemy(Map<String, String> attributes) {
    super(attributes);
    this.health = Integer.parseInt(attributes.get("health"));
    this.damage = Integer.parseInt(attributes.get("damage"));
    if (damage < 0)
      throw new IllegalArgumentException("damage must be positive");
    if (health < 0)
      throw new IllegalArgumentException("health must be positive");
    this.zone = Zone.valueOf(attributes.get("zone"));
    this.behavior = Behavior.valueOf(attributes.get("behavior").toUpperCase());
  }
}