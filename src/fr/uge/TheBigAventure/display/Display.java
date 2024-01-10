
package fr.uge.TheBigAventure.display;

import static java.lang.Math.min;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import fr.uge.TheBigAventure.general.World;
import fr.uge.TheBigAventure.objects.Item;
import fr.umlv.zen5.ApplicationContext;

/**
 * Classe permettant de calculer et stocker les dimensions de divers éléments de
 * l'interface graphique.
 */
public class Display {
  ApplicationContext context;
  private final float caseSize;
  private final float shiftX;
  private final float shiftY;

  public Display(World world, ApplicationContext context) {
    Objects.requireNonNull(context);
    float screenWidth = context.getScreenInfo().getWidth(), screenHeight = context.getScreenInfo().getHeight();
    int width = world.width(), height = world.height();

    float caseSize = min(screenWidth / width, screenHeight / height);
    this.caseSize = caseSize;
    this.shiftX = (screenWidth - width * caseSize) / 2;
    this.shiftY = (screenHeight - height * caseSize) / 2;
  }

  public float shiftX() {
    return shiftX;
  }

  public float shiftY() {
    return shiftY;
  }

  public float caseSize() {
    return caseSize;
  }
/*
  public void displayInventory(World world, ApplicationContext context, HashMap<String, BufferedImage> cachedImages) {
    context.renderFrame(graphics -> {
        // Draw the inventory rectangle with a border
        graphics.setColor(Color.DARK_GRAY);
        float inventoryX = context.getScreenInfo().getWidth() / 2 - 250;
        float inventoryY = context.getScreenInfo().getHeight() / 2 - 250;
        float inventoryWidth = 500;
        float inventoryHeight = 500;
        float borderThickness = 2.0f;

        // Draw the filled rectangle
        graphics.fill(new Rectangle2D.Float(inventoryX, inventoryY, inventoryWidth, inventoryHeight));

        // Draw the border
        graphics.setColor(Color.WHITE);
        graphics.setStroke(new BasicStroke(borderThickness));
        graphics.draw(new Rectangle2D.Float(inventoryX, inventoryY, inventoryWidth, inventoryHeight));

        // Draw the items in the inventory
        inventoryX += 20;
        inventoryY += 20;

        for (Item item : world.player().getInventory()) {
            if (cachedImages.containsKey(item.getSkin())) {
                BufferedImage itemImage = cachedImages.get(item.getSkin());
                graphics.drawImage(itemImage, (int) inventoryX, (int) inventoryY, (int) caseSize, (int) caseSize, null);
                inventoryX += caseSize + 10;
            }
        }
    });
  }
  */
  public void displayInventory(World world, ApplicationContext context, HashMap<String, BufferedImage> cachedImages) {
    context.renderFrame(graphics -> {
      drawInventoryRectangle(graphics, context, Color.DARK_GRAY, Color.WHITE, 2.0f);
      drawItemsInInventory(graphics, context, world.player().getInventory(), cachedImages);
    });
  }

	private void drawInventoryRectangle(Graphics2D graphics, ApplicationContext context, Color fillColor, Color borderColor, float borderThickness) {
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
	
	private void drawItemsInInventory(Graphics2D graphics, ApplicationContext context, List<Item> items, HashMap<String, BufferedImage> cachedImages) {
    float inventoryX = context.getScreenInfo().getWidth() / 2 - 250 + 20;
    float inventoryY = context.getScreenInfo().getHeight() / 2 - 250 + 20;

    for (Item item : items) {
      if (cachedImages.containsKey(item.getSkin())) {
        BufferedImage itemImage = cachedImages.get(item.getSkin());
        graphics.drawImage(itemImage, (int) inventoryX, (int) inventoryY, (int) caseSize, (int) caseSize, null);
        inventoryX += caseSize + 10;
      }
    }
	}


  
  public void hideInventory(World world, ApplicationContext context, Display show, HashMap<String, BufferedImage> cachedImages) {
  	context.renderFrame(graphics -> {
      graphics.clearRect(0, 0, (int) context.getScreenInfo().getWidth(), (int) context.getScreenInfo().getHeight());
      graphics.setColor(Color.WHITE);
      world.draw(graphics, show, cachedImages);
  	});
  }
  
}