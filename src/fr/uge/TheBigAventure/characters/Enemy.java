package fr.uge.TheBigAventure.characters;

import java.util.Map;

import fr.uge.TheBigAventure.gameObjects.Element;
import fr.uge.TheBigAventure.general.Position;
import fr.uge.TheBigAventure.general.World;
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
    AGRESSIVE;

  }

  public Enemy(String name, CharacterSkin skin, Position position, int health, Zone zone, String behavior, int damage) {
    super(name, skin, health, health, position, Element.Kind.ENEMY);
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
        Position.valueOf(attributes.get("position")), Integer.parseInt(attributes.get("health")),
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

  public int getDamage() {
    return damage;
  }

  public boolean update(World world) {
    Position previousPosition = new Position(getPosition().getX(), getPosition().getY());
    if (world.player().getPosition().distance(getPosition()) <= 5) {
      activeBehavior(this, world.player());
    } else {
      passiveBehavior(this);
    }
    return previousPosition.getX() != getPosition().getX() || previousPosition.getY() != getPosition().getY();
  }

  public void move(int x, int y) {
    getPosition().setX(x);
    getPosition().setY(y);
  }

  public void approach(Position target) {
    if (getPosition().getX() < target.getX())
      move(getPosition().getX() + 1, getPosition().getY());
    else if (getPosition().getX() > target.getX())
      move(getPosition().getX() - 1, getPosition().getY());
    else if (getPosition().getY() < target.getY())
      move(getPosition().getX(), getPosition().getY() + 1);
    else if (getPosition().getY() > target.getY())
      move(getPosition().getX(), getPosition().getY() - 1);
  }

  public void flee(Position target) {
    approach(new Position(-target.getX(), -target.getY()));
  }

  public void passiveBehavior(Enemy enemy) {
    switch (enemy.getBehavior()) {
      case SHY -> passiveShyBehavior(enemy);
      case STROLL -> passiveStrollBehavior(enemy);
      case AGRESSIVE -> passiveShyBehavior(enemy);
      default -> throw new IllegalArgumentException("behavior not found");
    }
  }

  public void activeBehavior(Enemy enemy, Player player) {
    switch (enemy.getBehavior()) {
      case SHY -> shyBehavior(enemy, player.getPosition());
      case STROLL -> passiveStrollBehavior(enemy);
      case AGRESSIVE -> agressiveBehavior(enemy, player);
      default -> throw new IllegalArgumentException("behavior not found");
    }
  }

  public void passiveShyBehavior(Enemy enemy) {
    if (enemy.zone != new Zone(enemy.getPosition(), 1, 1))
      passiveStrollBehavior(enemy);
  }

  public void shyBehavior(Enemy enemy, Position player) {
    // We flee from the player
    flee(player);
  }

  public void passiveStrollBehavior(Enemy enemy) {
    // 1/2 times, we move somewhere in the zone
    if (Math.random() < 0.5) {
      int x = enemy.getPosition().getX();
      int y = enemy.getPosition().getY();
      // int newX = x + (int) (Math.random() * 1) - 1;
      // int newY = y + (int) (Math.random() * 3) - 1;
      int newX = x, newY = y;
      if (Math.random() < 0.5)
        newX = x + (int) (Math.random() * 3 - 0.5) % 2;
      else
        newY = y + (int) (Math.random() * 3 - 0.5) % 2;
      if (enemy.getZone().contains(new Position(newX, newY)))
        enemy.move(newX, newY);
    }
  }

  public void agressiveBehavior(Enemy enemy, Player player) {
    // We approach the player and stay one square away
    approach(player.getPosition());
    if (enemy.getPosition().distance(player.getPosition()) <= 1)
      attack(player);
  }

  public void attack(GameCharacter character) {
    character.setHealth(character.getHealth() - damage);
  }

  public void takeDamage(int damage) {
    health -= damage;
  }

  public static void die(Enemy enemy, World world) {
    world.enemies().remove(enemy);
  }
}