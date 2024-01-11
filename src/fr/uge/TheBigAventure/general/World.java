package fr.uge.TheBigAventure.general;

import java.util.ArrayList;
import java.util.Objects;

import fr.uge.TheBigAventure.characters.Enemy;
import fr.uge.TheBigAventure.characters.Player;
import fr.uge.TheBigAventure.gameObjects.Item;
import fr.uge.TheBigAventure.gameObjects.Obstacle;

/**
 * Main class of the game, it contains all the information about the world,
 * characters, objects, etc.
 */
public record World(Player player, WorldMap worldMap, Encoding encoding, ArrayList<Enemy> enemies,
    ArrayList<Item> items, ArrayList<Obstacle> obstacles) {

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    encoding.encodings().forEach((key, value) -> sb.append(key + " : " + value + "\n"));
    for (int y = 0; y < worldMap.height(); y++) {
      // sb.append("[");
      for (int x = 0; x < worldMap.width(); x++) {
        if (worldMap.map()[y][x] == null)
          sb.append(" ");
        else
          sb.append(worldMap.map()[y][x].getName());
        // if (x != worldMap.width() - 1)
        // sb.append(", ");
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  public World {
    Objects.requireNonNull(encoding);
    Objects.requireNonNull(items);
    Objects.requireNonNull(worldMap);
    Objects.requireNonNull(enemies);
    Objects.requireNonNull(player);
    Objects.requireNonNull(obstacles);
  }

  public boolean isFree(int x, int y) {
    if (x < 0 || y < 0 || x >= worldMap.width() || y >= worldMap.height())
      return false;
    return Obstacle.isPassable(worldMap.map()[y][x]);
  }

  public Item getItemPosition(Position position) {
    for (Item item : items) {
      System.out.println(item.getPosition());
      if (item.getPosition().getX() == position.getX() && item.getPosition().getY() == position.getY()) {
        return item;
      }
    }
    return null;
  }

  public void removeItemPosition(Position position) {
    for (Item item : items) {
      if (item.getPosition().getX() == position.getX() && item.getPosition().getY() == position.getY()) {
        items.remove(item);
        return;
      }
    }
  }

}
