package fr.uge.TheBigAventure.display;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import fr.uge.TheBigAventure.characters.GameCharacter;
import fr.uge.TheBigAventure.characters.Inventory;
import fr.uge.TheBigAventure.characters.Player;
import fr.uge.TheBigAventure.food.GeneralFood;
import fr.uge.TheBigAventure.gameObjects.Element;
import fr.uge.TheBigAventure.gameObjects.Item;
import fr.uge.TheBigAventure.gameObjects.Weapon;
import fr.uge.TheBigAventure.general.Position;
import fr.uge.TheBigAventure.general.World;
import fr.uge.TheBigAventure.general.WorldMap;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.KeyboardKey;

/**
 * Classe permettant de calculer et stocker les dimensions de divers éléments de
 * l'interface graphique.
 */
public record Display(int caseSize, int shiftX, int shiftY, int viewSize) {

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
    int viewSize = 11;
    int caseSize = max(min(screenWidth / viewSize, screenHeight / viewSize), 24); // min(, 24)
    int shiftX = max((screenWidth - viewSize * caseSize) / 2, 0);
    int shiftY = max((screenHeight - viewSize * caseSize) / 2, 0);
    return new Display(caseSize, shiftX, shiftY, viewSize);
  }

  public static void drawWorld(Graphics2D graphics, Display display, ImageCache cachedImages, World world) {
    clearPreviousPosition(graphics, display, world.player());
    for (int i = 0; i < world.enemies().size(); i++) {
      clearPreviousPosition(graphics, display, world.enemies().get(i));
    }

    Position center = getCenter(display, world);

    drawCenteredWorldMap(graphics, display, cachedImages, world.worldMap(), center);
    drawCenteredList(graphics, display, cachedImages, world.obstacles(), center);
    drawCenteredList(graphics, display, cachedImages, world.items(), center);
    drawCenteredList(graphics, display, cachedImages, world.enemies(), center);
    drawCenteredList(graphics, display, cachedImages, world.friends(), center);

    drawCenteredElement(graphics, display, cachedImages, world.player(), center);
    if (world.player().getWeapon() != null)
      drawEquipedItem(graphics, display, cachedImages, world.player().getWeapon(), center);
    if (world.player().getHealth() < world.player().getMaxHealth())
      drawHealthBar(graphics, world.player());
  }

  private static Position getCenter(Display display, World world) {
    Position center = Position.getPositionCopy(world.player().getPosition());
    if (center.getX() - ((display.viewSize() - 1) / 2) <= 0)
      center.setX((display.viewSize() - 1) / 2);
    else if (center.getX() + ((display.viewSize() - 1) / 2) >= world.worldMap().width())
      center.setX(world.worldMap().width() - ((display.viewSize() - 1) / 2));
    if (center.getY() - ((display.viewSize() - 1) / 2) <= 0)
      center.setY((display.viewSize() - 1) / 2);
    else if (center.getY() + ((display.viewSize() - 1) / 2) >= world.worldMap().height())
      center.setY(world.worldMap().height() - ((display.viewSize() - 1) / 2));
    return center;
  }

  private static void drawCenteredWorldMap(Graphics2D graphics, Display display, ImageCache cachedImages,
      WorldMap worldMap, Position center) {
    int x = (int) center.getX(), y = (int) center.getY();
    int width = worldMap.width(), height = worldMap.height();
    int xMin = max(0, x - (display.viewSize - 1) / 2), xMax = min(width, x + (display.viewSize - 1) / 2 + 1);
    int yMin = max(0, y - (display.viewSize - 1) / 2), yMax = min(height, y + (display.viewSize - 1) / 2 + 1);
    for (int i = yMin; i < yMax; i++) {
      for (int j = xMin; j < xMax; j++) {
        if (worldMap.map()[i][j] != null) {
          drawCenteredElement(graphics, display, cachedImages, worldMap.map()[i][j], center);
        }
      }
    }
  }

  private static <T> void drawCenteredList(Graphics2D graphics, Display display, ImageCache cachedImages, List<T> list,
      Position center) {
    for (int i = 0; i < list.size(); i++) {
      Element tmp = (Element) list.get(i);
      if (isInView(center, tmp, display))
        drawCenteredElement(graphics, display, cachedImages, tmp, center);
    }
  }

  private static void drawCenteredElement(Graphics2D graphics, Display display, ImageCache cachedImages,
      Element element, Position center) {
    Image image = cachedImages.getImage(element);
    graphics.drawImage(image.getData(),
        (int) ((element.getPosition().getX() - center.getX() + ((display.viewSize() - 1) / 2)) * display.caseSize()
            + display.shiftX()),
        (int) ((element.getPosition().getY() - center.getY() + ((display.viewSize() - 1) / 2)) * display.caseSize()
            + display.shiftY()),
        display.caseSize(),
        display.caseSize(),
        null);
  }

  private static void drawEquipedItem(Graphics2D graphics, Display display, ImageCache cachedImages, Element item,
      Position center) {
    // L'item doit etre decalé par rapport au joueur et incliné (comme une épée)
    Image image = cachedImages.getImage(item);
    graphics.drawImage(image.rotatedData(-30),
        (int) ((item.getPosition().getX() - center.getX() + ((display.viewSize() - 1) / 2)) * display.caseSize()
            + display.shiftX() + display.caseSize() / 3),
        (int) ((item.getPosition().getY() - center.getY() + ((display.viewSize() - 1) / 2)) * display.caseSize()
            + display.shiftY() + display.caseSize() / 8),
        display.caseSize(),
        display.caseSize(),
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

  public static void clearPosition(ApplicationContext context, Display display, Position position) {
    context.renderFrame(graphics -> {
      graphics.clearRect(
          (int) (position.getX() * display.caseSize() + display.shiftX()),
          (int) (position.getY() * display.caseSize() + display.shiftY()),
          (int) display.caseSize(),
          (int) display.caseSize());
    });
  }

  public void displayInventory(World world, ApplicationContext context, ImageCache cachedImages,
      Position cursorPosition) {
    context.renderFrame(graphics -> {
      drawInventoryRectangle(graphics, context, Color.DARK_GRAY, Color.WHITE, 2.0f);
      drawItemsInInventory(graphics, context, world.player().getInventory(), cachedImages);
      drawInventoryCursor(graphics, context, cursorPosition);
    });
  }

  private void drawInventoryRectangle(Graphics2D graphics, ApplicationContext context, Color fillColor,
      Color borderColor, float borderThickness) {
    graphics.setColor(fillColor);
    float inventoryX = context.getScreenInfo().getWidth() / 2 - 250;
    float inventoryY = context.getScreenInfo().getHeight() / 2 - 250;
    float inventoryWidth = 500;
    float inventoryHeight = 500;

    graphics.fill(new Rectangle2D.Float(inventoryX, inventoryY, inventoryWidth, inventoryHeight));

    graphics.setColor(borderColor);
    graphics.setStroke(new BasicStroke(borderThickness));
    graphics.draw(new Rectangle2D.Float(inventoryX, inventoryY, inventoryWidth, inventoryHeight));
  }

  private void drawItemsInInventory(Graphics2D graphics, ApplicationContext context, Inventory items,
      ImageCache cachedImages) {
    int inventoryX = (int) (context.getScreenInfo().getWidth() / 2 - 250 + 20), resetX = inventoryX;
    int inventoryY = (int) (context.getScreenInfo().getHeight() / 2 - 250 + 20);

    int inventoryCaseSize = 24;
    for (Item item : items.getItems()) {
      Image image = cachedImages.getImage(item);
      graphics.drawImage(image.getData(), inventoryX, inventoryY, inventoryCaseSize, inventoryCaseSize, null);
      inventoryX += inventoryCaseSize + 10;
      if (inventoryX >= context.getScreenInfo().getWidth() / 2 + 250 - 20) {
        inventoryX = resetX;
        inventoryY += inventoryCaseSize + 10;
      }
    }
  }

  private void drawInventoryCursor(Graphics2D graphics, ApplicationContext context, Position cursorPosition) {
    graphics.setColor(Color.RED);
    int inventoryX = (int) (context.getScreenInfo().getWidth() / 2 - 250 + 20);
    int inventoryY = (int) (context.getScreenInfo().getHeight() / 2 - 250 + 20);

    int inventoryCaseSize = 24;
    graphics.drawRect(
        inventoryX + cursorPosition.getX() * (inventoryCaseSize + 10),
        inventoryY + cursorPosition.getY() * (inventoryCaseSize + 10),
        inventoryCaseSize, inventoryCaseSize);
  }

  public void interpretKey(ApplicationContext context, Player player, KeyboardKey key, Position cursorPosition) {
    switch (key) {
      case UP -> cursorPosition.setY(cursorPosition.getY() <= 0 ? 0 : cursorPosition.getY() - 1);
      case DOWN -> cursorPosition.setY(cursorPosition.getY() + 1);
      case LEFT -> cursorPosition.setX(cursorPosition.getX() <= 0 ? 0 : cursorPosition.getX() - 1);
      case RIGHT -> cursorPosition.setX(cursorPosition.getX() + 1);
      case SPACE -> {
        Item item = player.getInventory().getItems().get(cursorPosition.getY() * 5 + cursorPosition.getX());
        switch (item) {
          case Weapon w -> {
            if (player.getWeapon() != w)
              player.equipItem(item);
            else
              player.unequipItem();
          }
          case GeneralFood f -> {
            player.getInventory().removeItem(item);
            player.setHealth(max(player.getHealth() + f.getHealth(), player.getMaxHealth()));
          }
          default -> {
          }
        }
      }
      default -> {
      }
    }
    Inventory inventory = player.getInventory();
    int itemPerLine = (int) (500 / (24 + 10));
    if (itemPerLine == 0)
      itemPerLine = 1;
    if (cursorPosition.getX() < 0)
      cursorPosition.setX(itemPerLine - 1);
    if (cursorPosition.getX() >= itemPerLine || cursorPosition.getX() >= inventory.size() % itemPerLine)
      cursorPosition.setX(0);
    if (cursorPosition.getY() < 0)
      cursorPosition.setY(inventory.size() / itemPerLine);
    if (cursorPosition.getY() > inventory.size() / itemPerLine)
      cursorPosition.setY(0);

  }

  public void hideInventory(World world, ApplicationContext context, Display show,
      ImageCache cachedImages) {
    context.renderFrame(graphics -> {
      graphics.clearRect(0, 0, (int) context.getScreenInfo().getWidth(), (int) context.getScreenInfo().getHeight());
      graphics.setColor(Color.WHITE);
      Display.drawWorld(graphics, show, cachedImages, world);
    });
  }

  private static boolean isInView(Position center, Element tmp, Display display) {
    return tmp.getPosition().getX() >= center.getX() - ((display.viewSize() - 1) / 2)
        && tmp.getPosition().getX() <= center.getX() + ((display.viewSize() - 1) / 2)
        && tmp.getPosition().getY() >= center.getY() - ((display.viewSize() - 1) / 2)
        && tmp.getPosition().getY() <= center.getY() + ((display.viewSize() - 1) / 2);
  }

  public static void clearScreen(Graphics2D graphics, ApplicationContext context, Display display) {
    graphics.clearRect(
        display.shiftX(),
        display.shiftY(),
        display.caseSize() * display.viewSize(),
        display.caseSize() * display.viewSize());

  }

  private static void drawHealthBar(Graphics2D graphics, Player player) {
    int healthBarX = 20;
    int healthBarY = 20;
    int healthBarWidth = 100;
    int healthBarHeight = 10;
    graphics.setColor(Color.GRAY);
    graphics.fill(new Rectangle2D.Float(healthBarX - 1, healthBarY - 1, healthBarWidth + 1, healthBarHeight + 1));
    graphics.setColor(Color.WHITE);
    graphics.drawRect(healthBarX - 1, healthBarY - 1, healthBarWidth + 1, healthBarHeight + 1);
    graphics.setColor(Color.RED);
    graphics.fill(new Rectangle2D.Float(healthBarX, healthBarY,
        healthBarWidth * (player.getHealth() / player.getMaxHealth()), healthBarHeight));
  }

}