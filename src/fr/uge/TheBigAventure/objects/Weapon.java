package fr.uge.TheBigAventure.objects;

import java.util.Map;

import fr.uge.TheBigAventure.general.Position;

public class Weapon extends Item {
  private final int damage;

  public Weapon(String name, ItemSkins skin, Position position, int damage) {
    super(name, skin, position);
    this.damage = damage;
  }

  public Weapon(Map<String, String> attributes) {
    super(attributes);
    this.damage = Integer.parseInt(attributes.get("damage"));
  }

  public int getDamage() {
    return damage;
  }
}
