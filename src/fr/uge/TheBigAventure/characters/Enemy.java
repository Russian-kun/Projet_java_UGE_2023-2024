package fr.uge.TheBigAventure.characters;

import java.util.Map;

import fr.uge.TheBigAventure.gameObjects.Element;
import fr.uge.TheBigAventure.general.Position;
import fr.uge.TheBigAventure.general.Zone;

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
public class Enemy extends GameCharacter {
  private Zone zone;
  private Behavior behavior;
  private int damage;

  // shy, stroll et agressive
  public enum Behavior {
    SHY,
    STROLL,
    AGRESSIVE
  }

  public Enemy(String name, CharacterSkin skin, Position position, int health, Zone zone, String behavior, int damage) {
    super(name, skin, health, position, Element.Kind.ENEMY);
    if (damage < 0)
      throw new IllegalArgumentException("damage must be positive");
    if (zone == null)
      zone = new Zone(position, 1, 1);
    else
      this.zone = zone;
    if (behavior == null)
      this.behavior = Behavior.STROLL;
    else
      this.behavior = Behavior.valueOf(behavior.toUpperCase());
    this.damage = damage;
  }

  public static Enemy valueOf(Map<String, String> attributes) {
    if (GameCharacter.CharacterSkin.valueOf(attributes.get("skin").toUpperCase()) == null)
      throw new IllegalArgumentException("skin must be a character");
    return new Enemy(attributes.get("name"), GameCharacter.CharacterSkin.valueOf(attributes.get("skin").toUpperCase()),
        new Position(attributes.get("position")), Integer.parseInt(attributes.get("health")),
        Zone.valueOf(attributes.get("zone")), attributes.get("behavior"), Integer.parseInt(attributes.get("damage")));
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getHealth() {
    return health;
  }

  public Zone getZone() {
    return zone;
  }

  public Behavior getBehavior() {
    return behavior;
  }
}