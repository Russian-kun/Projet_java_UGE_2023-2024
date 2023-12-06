package fr.uge.TheBigAventure;

import java.util.Map;

class Enemy extends Element {
  private int health;
  private Zone zone;
  private String behavior;
  private int damage;

  public Enemy(String name, String skin, Position position, int health, Zone zone, String behavior, int damage) {
    super(name, skin, position, Element.Kind.ENEMY);
    this.health = health;
    this.zone = zone;
    this.behavior = behavior;
    this.damage = damage;
  }

  public Enemy(Map<String, String> attributes) {
    super(attributes);
    this.health = Integer.parseInt(attributes.get("health"));
    this.zone = Zone.valueOf(attributes.get("zone"));
    this.behavior = attributes.get("behavior");
    this.damage = Integer.parseInt(attributes.get("damage"));
  }
}