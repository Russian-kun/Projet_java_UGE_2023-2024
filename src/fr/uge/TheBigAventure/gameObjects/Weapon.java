package fr.uge.TheBigAventure.gameObjects;

import java.util.Map;

import fr.uge.TheBigAventure.general.Position;

public class Weapon extends Item {
  private final int damage;

  public Weapon(String name, ItemSkins skin, Position position, int damage) {
    super(name, skin, position);
    this.damage = damage;
  }

  public static Weapon valueOf(Map<String, String> attributes) {
    if (ItemSkins.valueOf(attributes.get("skin").toUpperCase()) == null)
      throw new IllegalArgumentException("skin must be a weapon");
    return new Weapon(attributes.get("name"), ItemSkins.valueOf(attributes.get("skin").toUpperCase()),
        Position.valueOf(attributes.get("position")), Integer.parseInt(attributes.get("damage")));
  }

  public int getDamage() {
    return damage;
  }
}
