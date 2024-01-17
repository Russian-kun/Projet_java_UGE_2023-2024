package fr.uge.TheBigAventure.characters;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import fr.uge.TheBigAventure.gameObjects.Door;
import fr.uge.TheBigAventure.gameObjects.Element;
import fr.uge.TheBigAventure.gameObjects.Item;
import fr.uge.TheBigAventure.gameObjects.Weapon;
import fr.uge.TheBigAventure.gameObjects.Item.ItemSkin;
import fr.uge.TheBigAventure.general.Position;
import fr.uge.TheBigAventure.general.World;
import fr.umlv.zen5.KeyboardKey;

public class Player extends GameCharacter {
  private final Inventory inventory = new Inventory(new ArrayList<>());
  private Weapon equipedItem = null;
  private Direction lastDirection = Direction.DOWN;

  public enum Direction {
    UP, DOWN, LEFT, RIGHT
  }

  public enum PlayerSkin implements GeneralCharacterSkin {
    BABA(CharacterSkin.BABA),
    BADBAD(CharacterSkin.BADBAD),
    FOFO(CharacterSkin.FOFO),
    IT(CharacterSkin.IT);

    private final CharacterSkin characterSkin;

    PlayerSkin(CharacterSkin characterSkin) {
      this.characterSkin = characterSkin;
    }

    public CharacterSkin getCharacterSkin() {
      return characterSkin;
    }
  }

  public Player(String name, PlayerSkin skin, Position position, int health) {
    super(name, skin.getCharacterSkin(), health, health, position, Element.Kind.PLAYER);
  }

  public static Player valueOf(Map<String, String> attributes) {
    if (PlayerSkin.valueOf(attributes.get("skin").toUpperCase()) == null)
      throw new IllegalArgumentException("skin must be a player");
    return new Player(attributes.get("name"), PlayerSkin.valueOf(attributes.get("skin").toUpperCase()),
        Position.valueOf(attributes.get("position")), Integer.parseInt(attributes.get("health")));
  }

  public Inventory getInventory() {
    return inventory;
  }

  public boolean move(World world, KeyboardKey key) {
    Objects.requireNonNull(key);
    previousPosition = new Position(getPosition().getX(), getPosition().getY());
    Position newPosition = null;
    boolean moved = false;
    switch (key) {
      case UP -> newPosition = new Position(getPosition().getX(), getPosition().getY() - 1);
      case DOWN -> newPosition = new Position(getPosition().getX(), getPosition().getY() + 1);
      case LEFT -> newPosition = new Position(getPosition().getX() - 1, getPosition().getY());
      case RIGHT -> newPosition = new Position(getPosition().getX() + 1, getPosition().getY());
      default -> throw new IllegalArgumentException("key must be a movement key");
    }
    lastDirection = getDirection(newPosition, getPosition());
    moved = moveIfFree(world, newPosition.getX(), newPosition.getY());
    if (moved)
      getInventory().pickupWorldItem(world, getPosition(), newPosition);
    else if ((moved = canOpenDoor(world, newPosition)))
      ((Door) world.doorAt(newPosition)).openDoor(world, inventory);
    else
      moved = interactCharacterAt(world, newPosition, moved);
    return moved;
  }

  private boolean interactCharacterAt(World world, Position newPosition, boolean moved) {
    Enemy enemy;
    Friend friend;
    if ((enemy = world.enemyAt(newPosition)) != null) {
      attack(enemy, world);
      moved = true;
    } else if ((friend = world.friendAt(newPosition)) != null) {
      friend.update(world);
      moved = true;
    }
    return moved;
  }

  private Direction getDirection(Position curr, Position prev) {
    if (curr.getX() > prev.getX())
      return Direction.RIGHT;
    else if (curr.getX() < prev.getX())
      return Direction.LEFT;
    else if (curr.getY() > prev.getY())
      return Direction.DOWN;
    else if (curr.getY() < prev.getY())
      return Direction.UP;
    else
      return lastDirection;
  }

  private boolean canOpenDoor(World world, Position newPosition) {
    Door door = world.doorAt(newPosition);
    if (door == null)
      return false;
    Item key = correspondingKey(door);
    return key != null;
  }

  private Item correspondingKey(Door door) {
    return inventory.getItems().stream()
        .filter(item -> item.getSkin().equals(ItemSkin.KEY)
            && ((Item) item).getName().equals(door.getKeyColor()))
        .findFirst()
        .orElse(null);
  }

  public boolean moveIfFree(World world, int x, int y) {
    return Position.moveIfFree(world, getPosition(), x, y);
  }

  public void attack(Enemy enemy, World world) {
    int damage = equipedItem == null ? 1 : equipedItem.getDamage();
    enemy.takeDamage(damage);
    if (enemy.getHealth() <= 0)
      Enemy.die(enemy, world);
  }

  public void heal(int quantity) {
    health += quantity;
    if (health > maxHealth)
      health = maxHealth;
  }

  @Override
  public void takeDamage(int quantity) {
    health -= quantity;
  }

  public void equipItem(Item item) {
    if (!inventory.contains(item))
      throw new IllegalArgumentException("item must be in inventory");
    equipedItem = (Weapon) item;
  }

  public void unequipItem() {
    equipedItem = null;
  }

  public Weapon getWeapon() {
    return equipedItem;
  }

}
