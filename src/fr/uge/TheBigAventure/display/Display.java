package fr.uge.TheBigAventure.display;

import static java.lang.Math.min;

import java.awt.Graphics2D;
import java.util.List;

import fr.uge.TheBigAventure.characters.GameCharacter;
import fr.uge.TheBigAventure.gameObjects.Element;
import fr.uge.TheBigAventure.general.Position;
import fr.uge.TheBigAventure.general.World;
import fr.uge.TheBigAventure.general.WorldMap;

/**
 * Classe permettant de calculer et stocker les dimensions de divers éléments de
 * l'interface graphique.
 */
public record Display(int caseSize, int shiftX, int shiftY) {

  public Display {
    if (caseSize <= 0) {
      throw new IllegalArgumentException("caseSize must be positive");
    }
    if (shiftX < 0) {
      throw new IllegalArgumentException("shiftX must be positive");
    }
    if (shiftY < 0) {
      throw new IllegalArgumentException("shiftY must be positive");
    }
  }

  public static Display createDisplay(int worldWidth, int worldHeight, int screenWidth, int screenHeight) {
    int caseSize = min(screenWidth / worldWidth, screenHeight / worldHeight);
    int shiftX = (screenWidth - worldWidth * caseSize) / 2;
    int shiftY = (screenHeight - worldHeight * caseSize) / 2;
    return new Display(caseSize, shiftX, shiftY);
  }

  public static void drawWorld(Graphics2D graphics, Display display, ImageCache cachedImages, World world) {
    clearPreviousPosition(graphics, display, world.player());
    for (int i = 0; i < world.enemies().size(); i++) {
      clearPreviousPosition(graphics, display, world.enemies().get(i));
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