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
  private int movementTentative = 0;

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
      activeBehavior(world);
    } else {
      passiveBehavior(world);
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

  public void passiveBehavior(World world) {
    switch (getBehavior()) {
      case SHY -> passiveShyBehavior(world);
      case STROLL -> passiveStrollBehavior(world);
      case AGRESSIVE -> passiveShyBehavior(world);
      default -> throw new IllegalArgumentException("behavior not found");
    }
  }

  public void activeBehavior(World world) {
    switch (getBehavior()) {
      case SHY -> shyBehavior(world.player().getPosition());
      case STROLL -> passiveStrollBehavior(world);
      case AGRESSIVE -> agressiveBehavior(world.player());
      default -> throw new IllegalArgumentException("behavior not found");
    }
  }

  public void passiveShyBehavior(World world) {
    if (this.zone != new Zone(this.getPosition(), 1, 1))
      passiveStrollBehavior(world);
  }

  public void shyBehavior(Position player) {
    // We flee from the player
    flee(player);
  }

  public void passiveStrollBehavior(World world) {
    // 1/2 times, we move somewhere in the zone
    if (Math.random() <= 0.5 + movementTentative / 20) {
      int x = getPosition().getX();
      int y = getPosition().getY();

      // Randomly decide whether to move in the x or y direction
      int newX = x;
      int newY = y;
      do {
        newX = x;
        newY = y;
        if (Math.random() < 0.5) {
          if (Math.random() < 0.5)
            newX = x + 1;
          else
            newX = x - 1;
        } else {
          if (Math.random() < 0.5)
            newY = y + 1;
          else
            newY = y - 1;
        }
        // Check if the new position is within the zone
      } while (!getZone().contains(new Position(newX, newY)));
      moveIfEmpty(newX, newY, world);
      movementTentative = -2;
    } else
      movementTentative++;

  }

  public void agressiveBehavior(Player player) {
    // We approach the player and stay one square away
    approach(player.getPosition());
    if (getPosition().distance(player.getPosition()) <= 1)
      attack(player);
  }

  public void attack(GameCharacter character) {
    character.takeDamage(damage);
  }

  public void takeDamage(int damage) {
    health -= damage;
  }

  public static void die(Enemy enemy, World world) {
    world.enemies().remove(enemy);
  }

  public void moveIfEmpty(int x, int y, World world) {
    boolean player = world.player().getPosition().equals(new Position(x, y));
    if (player)
      attack(world.player());
    else if (world.isFree(x, y))
      move(x, y);
  }
}