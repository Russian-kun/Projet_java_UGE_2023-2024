package fr.uge.TheBigAventure.characters;

import java.util.ArrayList;
import java.util.Map;

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
    if (key == null)
      throw new IllegalArgumentException("key must not be null");
    previousPosition = new Position(position.getX(), position.getY());
    Position newPosition = null;
    boolean moved = false;
    switch (key) {
      case UP -> newPosition = new Position(position.getX(), position.getY() - 1);
      case DOWN -> newPosition = new Position(position.getX(), position.getY() + 1);
      case LEFT -> newPosition = new Position(position.getX() - 1, position.getY());
      case RIGHT -> newPosition = new Position(position.getX() + 1, position.getY());
      default -> throw new IllegalArgumentException("key must be a movement key");
    }
    moved = moveIfFree(world, newPosition.getX(), newPosition.getY());
    if (moved)
      getWorldItem(world);
    else if (canOpenDoor(world, newPosition)) {
      openDoor(world, newPosition);
      moved = true;
    }

    return moved;
  }

  private void openDoor(World world, Position newPosition) {
    removeFirstItem(ItemSkin.KEY);
    world.removeObjectPosition(newPosition);
  }

  private void removeFirstItem(ItemSkin itemSkin) {
    inventory.getItems().stream()
        .filter(item -> item.getSkin().equals(itemSkin))
        .findFirst()
        .ifPresent(item -> {
          inventory.removeItem(item);
        });
  }

  private void getWorldItem(World world) {
    Item item = world.getItemPosition(position);
    if (item != null) {
      System.out.println(item.getName());
      inventory.addItem(item);
      world.removeItemPosition(position);
    }
  }

  private boolean canOpenDoor(World world, Position newPosition) {
    return world.doorAt(newPosition)
        && inventory.getItems().stream().anyMatch(item -> item.getSkin().equals(ItemSkin.KEY));
  }

  public boolean moveIfFree(World world, int x, int y) {
    return Position.moveIfFree(world, position, x, y);
  }

  public void attack(Enemy enemy) {
    int damage = equipedItem == null ? 1 : equipedItem.getDamage();
    enemy.setHealth(enemy.getHealth() - damage);
  }

  public void heal(int quantity) {
    health += quantity;
  }

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

}
