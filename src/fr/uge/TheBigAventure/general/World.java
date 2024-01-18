package fr.uge.TheBigAventure.general;

import java.util.ArrayList;
import java.util.Objects;

import fr.uge.TheBigAventure.characters.Enemy;
import fr.uge.TheBigAventure.characters.Friend;
import fr.uge.TheBigAventure.characters.Player;
import fr.uge.TheBigAventure.gameObjects.Door;
import fr.uge.TheBigAventure.gameObjects.Element;
import fr.uge.TheBigAventure.gameObjects.Item;
import fr.uge.TheBigAventure.gameObjects.Obstacle;
import fr.uge.TheBigAventure.gameObjects.Obstacle.ImpassableType;

/**
 * Main class of the game, it contains all the information about the world,
 * characters, objects, etc.
 */
public record World(Player player, WorldMap worldMap, Encoding encoding, ArrayList<Enemy> enemies,
    ArrayList<Friend> friends, ArrayList<Item> items, ArrayList<Obstacle> obstacles) {

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
    boolean free = false;
    if (x < 0 || y < 0 || x >= worldMap.width() || y >= worldMap.height())
      return free;
    free = Obstacle.isPassable(worldMap.map()[y][x])
        && enemies.stream().noneMatch(enemy -> enemy.getPosition().getX() == x && enemy.getPosition().getY() == y)
        && friends.stream().noneMatch(friend -> friend.getPosition().getX() == x && friend.getPosition().getY() == y)
        && obstacles.stream()
            .noneMatch(obstacle -> obstacle.getPosition().getX() == x && obstacle.getPosition().getY() == y);
    return free;
  }

  public Item getItemPosition(Position position) {
    for (Item item : items) {
      if (item.getPosition().getX() == position.getX() && item.getPosition().getY() == position.getY()) {
        return item;
      }
    }
    return null;
  }

  public static Element getElementPosition(ArrayList<? extends Element> elements, Position position) {
    for (Element element : elements) {
      if (element.getPosition().getX() == position.getX() && element.getPosition().getY() == position.getY()) {
        return element;
      }
    }
    return null;
  }

  public Door doorAt(Position position) {
    return (Door) obstacles.stream().filter(obstacle -> obstacle.getPosition().getX() == position.getX()
        && obstacle.getPosition().getY() == position.getY()
        && (obstacle.skin().equals(ImpassableType.DOOR) || obstacle.skin().equals(ImpassableType.GATE))).findFirst()
        .orElse(null);
  }

  public void removeItemPosition(Position position) {
    for (Item item : items) {
      if (item.getPosition().getX() == position.getX() && item.getPosition().getY() == position.getY()) {
        items.remove(item);
        return;
      }
    }
  }

  public static void removeElementPosition(ArrayList<? extends Element> elements, Position position) {
    for (Element element : elements) {
      if (element.getPosition().getX() == position.getX() && element.getPosition().getY() == position.getY()) {
        elements.remove(element);
        return;
      }
    }
  }

  public void removeObjectPosition(Position position) {
    worldMap.map()[position.getY()][position.getX()] = null;
  }

  public boolean update() {
    return updateEnemies() ||
        updateFriends();
  }

  public boolean updateEnemies() {
    boolean result = false;
    for (Enemy enemy : enemies) {
      result = result || enemy.update(this);
    }
    return result;
  }

  public boolean updateFriends() {
    boolean result = false;
    for (Friend friend : friends) {
      result = result || friend.update(this);
    }
    return result;
  }

  public Enemy enemyAt(Position position) {
    return enemies.stream().filter(enemy -> enemy.getPosition().getX() == position.getX()
        && enemy.getPosition().getY() == position.getY()).findFirst().orElse(null);
  }

  public Friend friendAt(Position position) {
    return friends.stream().filter(friend -> friend.getPosition().getX() == position.getX()
        && friend.getPosition().getY() == position.getY()).findFirst().orElse(null);
  }

  public boolean isInteractable(Position position) {
    // Levers, plants, vehicles, etc.
    return obstacles.stream().anyMatch(obstacle -> obstacle.getPosition().getX() == position.getX()
        && obstacle.getPosition().getY() == position.getY() && obstacle.skin().equals(ImpassableType.LEVER));
  }

  public Obstacle interactableObstacleAt(Position position) {
    return obstacles.stream().filter(obstacle -> obstacle.getPosition().getX() == position.getX()
        && obstacle.getPosition().getY() == position.getY() && obstacle.skin().equals(ImpassableType.LEVER))
        .findFirst().orElse(null);
  }
}
