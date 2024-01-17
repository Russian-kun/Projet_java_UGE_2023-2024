package fr.uge.TheBigAventure.gameObjects;

import java.util.Map;
import java.util.Objects;

import fr.uge.TheBigAventure.characters.Inventory;
import fr.uge.TheBigAventure.gameObjects.Item.ItemSkin;
import fr.uge.TheBigAventure.general.Position;
import fr.uge.TheBigAventure.general.World;

public class Door extends Obstacle {
  private boolean isOpen = false;
  private Item doorKey;

  enum DoorType implements ObstacleType {
    DOOR, GATE;
  }

  public Door(String name, ObstacleType skin, Position position, Item key, boolean isOpen) {
    super(name, skin, position);
    Objects.requireNonNull(key);
    this.doorKey = key;
    this.isOpen = isOpen;
  }

  public static Door valueOf(Map<String, String> attributes) {
    Item newKey = new Item(
        attributes.get("locked").split(" ")[1],
        ItemSkin.valueOf(attributes.get("locked").split(" ")[0]),
        new Position(0, 0));
    return new Door(attributes.get("name"), getObstacleType(attributes.get("skin")),
        Position.valueOf(attributes.get("position")), newKey,
        Boolean.valueOf(attributes.get("isOpen")));
  }

  public String getKeyColor() {
    return doorKey.getName();
  }

  public boolean canOpen(Item key) {
    if (!key.getSkin().equals(Item.ItemSkin.KEY))
      return false;

    if (!isOpen() && key.getName().equals(getKeyColor())) {
      isOpen = true;
      return true;
    }
    return false;
  }

  public void openDoor(World world, Inventory inventory) {
    inventory.removeFirstItem(this::canOpen);
    World.removeElementPosition(world.obstacles(), getPosition());
  }

  public boolean isOpen() {
    return isOpen;
  }

}
