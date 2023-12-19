package fr.uge.TheBigAventure.general;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.uge.TheBigAventure.display.Display;
import fr.uge.TheBigAventure.display.Image;
import fr.uge.TheBigAventure.display.ImageCache;
import fr.uge.TheBigAventure.gameObjects.Element;
import fr.uge.TheBigAventure.gameObjects.Enemy;
import fr.uge.TheBigAventure.gameObjects.GameCharacter;
import fr.uge.TheBigAventure.gameObjects.Item;
import fr.uge.TheBigAventure.gameObjects.Obstacle;
import fr.uge.TheBigAventure.gameObjects.Player;

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

  public Player player() {
    return player;
  }

  public boolean isFree(int x, int y) {
    if (x < 0 || y < 0 || x >= worldMap.width() || y >= worldMap.height())
      return false;
    return Obstacle.isPassable(worldMap.map()[y][x]);
  }

  public static void draw(Graphics2D graphics, Display display, ImageCache cachedImages, World world) {
    clearPreviousPosition(graphics, display, world.player());
    for (int i = 0; i < world.enemies.size(); i++) {
      clearPreviousPosition(graphics, display, world.enemies.get(i));
    }

    drawWorldMap(graphics, display, cachedImages, world.worldMap());
    drawElement(graphics, display, cachedImages, world.player());
    drawList(graphics, display, cachedImages, world.obstacles());
    drawList(graphics, display, cachedImages, world.items());
    drawList(graphics, display, cachedImages, world.enemies());
  }

  public static void drawWorldMap(Graphics2D graphics, Display display, ImageCache cachedImages, WorldMap worldMap) {
    for (int y = 0; y < worldMap.height(); y++) {
      for (int x = 0; x < worldMap.width(); x++) {
        if (worldMap.map()[y][x] != null) {
          // We will look for the corresponding image in the images folder if it is not
          // not already in cache
          // The images are named as follows: NAME_0.webp
          drawElement(graphics, display, cachedImages, worldMap.map()[y][x]);
        }
      }
    }
  }

  private static <T> void drawList(Graphics2D graphics, Display display, ImageCache cachedImages, List<T> list) {
    for (int i = 0; i < list.size(); i++) {
      drawElement(graphics, display, cachedImages, (Element) list.get(i));
    }
  }

  private static void drawElement(Graphics2D graphics, Display display, ImageCache cachedImages,
      Element element) {
    Image image = cachedImages.getImage(element);
    graphics.drawImage(image.getData(),
        (int) (element.getPosition().getX() * display.caseSize() + display.shiftX()),
        (int) (element.getPosition().getY() * display.caseSize() + display.shiftY()),
        null);
  }

  private static void clearPreviousPosition(Graphics2D graphics, Display display, GameCharacter character) {
    if (character.getPreviousPosition() != null) {
      Position prev = character.getPreviousPosition();
      graphics.clearRect(
          (int) (prev.getX() * display.caseSize() + display.shiftX()),
          (int) (prev.getY() * display.caseSize() + display.shiftY()),
          (int) display.caseSize(),
          (int) display.caseSize());
    }
  }
}
