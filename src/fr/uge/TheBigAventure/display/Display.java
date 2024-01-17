package fr.uge.TheBigAventure.display;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import fr.uge.TheBigAventure.characters.Friend;
import fr.uge.TheBigAventure.characters.Inventory;
import fr.uge.TheBigAventure.characters.Player;
import fr.uge.TheBigAventure.characters.Trade;
import fr.uge.TheBigAventure.characters.TradeList;
import fr.uge.TheBigAventure.food.GeneralFood;
import fr.uge.TheBigAventure.gameObjects.Element;
import fr.uge.TheBigAventure.gameObjects.Item;
import fr.uge.TheBigAventure.gameObjects.Weapon;
import fr.uge.TheBigAventure.general.Position;
import fr.uge.TheBigAventure.general.World;
import fr.uge.TheBigAventure.general.WorldMap;
import fr.uge.TheBigAventure.keys.AcceptedKeys;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.Event.Action;
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
    // The item must be offset from the player and tilted (like a sword)
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

  public static void inventoryLoop(final World world, final ImageCache cachedImages, ApplicationContext context,
      Display display) {
    Event event;
    KeyboardKey key;
    Position cursorPosition = new Position(0, 0);
    // Afficher l'inventaire
    display.displayInventory(world, context, cachedImages, cursorPosition);

    while (true) {
      // event = context.pollEvent();
      event = context.pollOrWaitEvent(1000 / 30);
      if (event != null && event.getAction() == Action.KEY_PRESSED) {
        if (AcceptedKeys.isAcceptedKey(key = event.getKey())) {
          if (key == KeyboardKey.I || key == KeyboardKey.Q)
            break;
          else {
            display.interpretKey(context, world.player(), key, cursorPosition);
            display.displayInventory(world, context, cachedImages, cursorPosition);
          }
        }
      }
    }
    Display.hideInventory(world, context, display, cachedImages);
  }

  public void displayInventory(World world, ApplicationContext context, ImageCache cachedImages,
      Position cursorPosition) {
    context.renderFrame(graphics -> {
      drawInventoryRectangle(graphics, context, Color.DARK_GRAY, Color.WHITE, 2.0f);
      drawItemsInInventory(graphics, context, world.player().getInventory(), cachedImages);
      drawInventoryCursor(graphics, context, cursorPosition);
      drawHealthBar(graphics, world.player());
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
    Inventory inventory = player.getInventory();
    switch (key) {
      case UP -> cursorPosition.setY(cursorPosition.getY() <= 0 ? 0 : cursorPosition.getY() - 1);
      case DOWN -> cursorPosition.setY(cursorPosition.getY() + 1);
      case LEFT -> cursorPosition.setX(cursorPosition.getX() <= 0 ? 0 : cursorPosition.getX() - 1);
      case RIGHT -> cursorPosition.setX(cursorPosition.getX() + 1);
      case SPACE -> {
        Item item = inventory.getItems().get(cursorPosition.getY() * 5 + cursorPosition.getX());
        switch (item) {
          case Weapon w -> {
            if (player.getWeapon() != w)
              player.equipItem(item);
            else
              player.unequipItem();
          }
          case GeneralFood f -> {
            inventory.removeItem(item);
            player.heal(f.getHealth());
          }
          default -> {
          }
        }
      }
      default -> {
      }
    }
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

  public static void hideInventory(World world, ApplicationContext context, Display show,
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
    int healthBarX = 20, healthBarY = 20;
    int healthBarWidth = 100, healthBarHeight = 10;
    graphics.setColor(Color.GRAY);
    graphics.fill(new Rectangle2D.Float(healthBarX - 1, healthBarY - 1, healthBarWidth + 2, healthBarHeight + 2));
    graphics.setColor(Color.RED);
    graphics.fill(new Rectangle2D.Float(healthBarX, healthBarY,
        (int) (healthBarWidth * ((float) player.getHealth() / player.getMaxHealth())), healthBarHeight));
  }

  public static void gameOverLoop(World world, ImageCache cachedImages, ApplicationContext context, Display display) {
    Event event;
    KeyboardKey key;
    context.renderFrame(graphics -> {
      graphics.setColor(Color.BLACK);
      graphics.fillRect(0, 0, (int) context.getScreenInfo().getWidth(), (int) context.getScreenInfo().getHeight());
      graphics.setColor(Color.WHITE);
      graphics.drawString("GAME OVER", (int) context.getScreenInfo().getWidth() / 2 - 50,
          (int) context.getScreenInfo().getHeight() / 2);
    });
    while (true) {
      event = context.pollOrWaitEvent(1000 / 30);
      if (event != null && event.getAction() == Action.KEY_PRESSED) {
        if (AcceptedKeys.isAcceptedKey(key = event.getKey())) {
          if (key == KeyboardKey.Q)
            break;
        }
      }
    }
  }

  public static void tradeOverlay(ImageCache cachedImages, ApplicationContext context, Display display,
      Friend friend, int cursorPosition) {
    int inventoryX = (int) context.getScreenInfo().getWidth() / 2 - 250;
    int inventoryY = (int) context.getScreenInfo().getHeight() / 2 - 250;
    TradeList trades = friend.getTrades();
    context.renderFrame(graphics -> {
      display.drawInventoryRectangle(graphics, context, Color.DARK_GRAY, Color.WHITE, 2.0f);
      Display.tradeCursor(cachedImages, context, display, trades, cursorPosition, graphics);
      graphics.setColor(Color.BLACK);
      ArrayList<Trade> tradeList = trades.getTrades();
      for (int i = 0; i < tradeList.size(); i++) {
        Display.drawTrade(graphics, cachedImages, context, display, tradeList.get(i),
            inventoryX + 50,
            inventoryY + 50 + i * 60);
      }
      Display.speak(friend, display, graphics, context);
    });
  }

  public static void tradeCursor(ImageCache cachedImages, ApplicationContext context, Display display,
      TradeList trades, int cursorPosition, Graphics2D graphics) {
    int inventoryX = (int) context.getScreenInfo().getWidth() / 2 - 250;
    int inventoryY = (int) context.getScreenInfo().getHeight() / 2 - 250;
    graphics.setColor(Color.RED);
    graphics.drawRect(inventoryX + 50, inventoryY + 45 + cursorPosition * 60, 200, 60);
  }

  public static void drawTrade(Graphics2D graphics, ImageCache cachedImages, ApplicationContext context,
      Display display, Trade trade, int x, int y) {
    graphics.setColor(Color.WHITE);
    graphics.drawImage(cachedImages.getImage(trade.getItems()[0]).getData(), x, y, 50, 50, null);

    graphics.drawString("->", x + 75, y + 30);

    graphics.drawImage(cachedImages.getImage(trade.getItems()[1]).getData(), x + 100, y, 50, 50, null);
    if (trade.getPriceName() != null)
      graphics.drawString(trade.getPriceName(), x + 160, y + 30);
  }

  public static void speak(Friend friend, Display display, Graphics2D graphics, ApplicationContext context) {
    graphics.setColor(Color.BLACK);
    graphics.fillRect(20, 20, 2 * friend.getText().length(), 10);
    graphics.setColor(Color.WHITE);
    graphics.drawString(friend.getText(), 20, 20);
  }

  public static void tradeLoop(World world, ImageCache cachedImages, ApplicationContext context, Display display,
      Friend friend) {
    Event event;
    KeyboardKey key;
    int cursorPosition = 0;
    TradeList trades = friend.getTrades();
    Inventory inventory = world.player().getInventory();
    tradeOverlay(cachedImages, context, display, friend, cursorPosition);
    boolean continueTrade = true;
    while (continueTrade) {
      event = context.pollOrWaitEvent(1000 / 30);
      if (event != null && event.getAction() == Action.KEY_PRESSED) {
        if (AcceptedKeys.isAcceptedKey(key = event.getKey())) {
          switch (key) {
            case Q -> continueTrade = false;
            case UP ->
              cursorPosition = cursorPosition <= 0 ? 0 : cursorPosition - 1;
            case DOWN ->
              cursorPosition = cursorPosition >= trades.size() - 1 ? trades.size() - 1 : cursorPosition + 1;
            case SPACE -> {
              Item price = trades.getTrades().get(cursorPosition).getItems()[0];
              if (inventory.contains(price)) {
                Item item = trades.getTrades().get(cursorPosition).getItems()[1];

                inventory.removeItem(price);
                inventory.addItem(item);
                item.setPosition(world.player().getPosition());
              }
            }
            default -> {
            }
          }
          tradeOverlay(cachedImages, context, display, friend, cursorPosition);
        }
      }
    }
    hideInventory(world, context, display, cachedImages);
  }
}