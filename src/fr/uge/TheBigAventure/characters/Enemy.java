package fr.uge.TheBigAventure.characters;

import java.util.Map;

import fr.uge.TheBigAventure.general.Position;
import fr.uge.TheBigAventure.general.Zone;
import fr.uge.TheBigAventure.objects.Element;

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
public class Enemy extends Characters {
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
    super(name, skin, health, position, Element.Kind.ENEMY);
    if (damage < 0)
      throw new IllegalArgumentException("damage must be positive");
    if (Characters.CharacterSkin.valueOf(skin.toUpperCase()) == null)
      throw new IllegalArgumentException("skin must be a character");
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

  public Enemy(Map<String, String> attributes) {
    super(attributes);
    if (Characters.CharacterSkin.valueOf(skin.toUpperCase()) == null)
      throw new IllegalArgumentException("skin must be a character");
    this.damage = Integer.parseInt(attributes.get("damage"));
    if (damage < 0)
      throw new IllegalArgumentException("damage must be positive");
    if (!(attributes.get("zone") == null))
      this.zone = Zone.valueOf(attributes.get("zone"));
    else
      this.zone = new Zone(this.position, 1, 1);
    if (!(attributes.get("behavior") == null))
      this.behavior = Behavior.valueOf(attributes.get("behavior").toUpperCase());
    else
      this.behavior = Behavior.STROLL;
  }

  public String getName() {
    return name;
  }

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