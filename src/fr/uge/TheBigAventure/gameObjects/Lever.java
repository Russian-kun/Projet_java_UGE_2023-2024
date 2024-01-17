package fr.uge.TheBigAventure.gameObjects;

import java.util.Map;

import fr.uge.TheBigAventure.general.Position;
import fr.uge.TheBigAventure.general.World;

public class Lever extends Obstacle {
  private boolean isOpen = false;
  private Door linkedDoor = null;

  public Lever(String name, ObstacleType skin, Position position) {
    super(name, skin, position);
  }

  public static Lever valueOf(Map<String, String> attributes) {
    return new Lever(attributes.get("name"), getObstacleType(attributes.get("skin")),
        Position.valueOf(attributes.get("position")));
  }

  public String getKeyColor() {
    return getName();
  }

  public void openDoor(World world) {
    isOpen = true;

    if (linkedDoor == null)
      linkedDoor = (Door) world.obstacles().stream().filter(Door.class::isInstance)
          .filter(obstacle -> ((Door) obstacle).getKeyColor().equals(getKeyColor())).findFirst().orElse(null);
    world.obstacles().remove(linkedDoor);
  }

  public void closeDoor(World world) {
    isOpen = false;
    world.obstacles().add(linkedDoor);
  }

  public boolean isOpen() {
    return isOpen;
  }
}
