package fr.uge.main;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.HashMap;

import fr.uge.TheBigAventure.display.Display;
import fr.uge.TheBigAventure.display.Image;
import fr.uge.TheBigAventure.display.ImageCache;
import fr.uge.TheBigAventure.gameObjects.GeneralSkin;
import fr.uge.TheBigAventure.general.World;
import fr.uge.parser.Parser;
import fr.umlv.zen5.Application;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.Event.Action;
import fr.umlv.zen5.KeyboardKey;

public class Main {

  static boolean isInventoryVisible = false;

  public static void main(String[] args) throws IOException {
    final int frequency = 1000 / 120;
    var filePath = Path.of("fun.map");
    try {
      final World world = Parser.readMap(filePath);
      System.out.println(world);
      final ImageCache cachedImages = new ImageCache(new HashMap<GeneralSkin, Image>());

      Application.run(Color.BLACK, context -> {
        Display display = Display.createDisplay(world.worldMap().width(), world.worldMap().height(),
            (int) context.getScreenInfo().getWidth(),
            (int) context.getScreenInfo().getHeight());

        Event key = null;
        while (true) {
          var start = LocalTime.now().toNanoOfDay();
          key = context.pollEvent();
          if (key != null && key.getAction() == Action.KEY_PRESSED) {
            if (KeyboardKey.UP == key.getKey() || KeyboardKey.DOWN == key.getKey() || KeyboardKey.LEFT == key.getKey()
                || KeyboardKey.RIGHT == key.getKey())
              world.player().move(world, key);

            else if (!isInventoryVisible && key.getKey() == KeyboardKey.I) {
              // Afficher l'inventaire
              isInventoryVisible = true;
              display.displayInventory(world, context, cachedImages);
              int pressCount = 0;

              do {
                key = context.pollEvent();
                if (key != null && key.getKey() == KeyboardKey.I) {
                  pressCount++;
                }
              } while (pressCount < 2);

              // Cacher l'inventaire
              isInventoryVisible = false;
              display.hideInventory(world, context, display, cachedImages);

            } else if (key.getKey() == KeyboardKey.Q) {
              break;
            }
          }
          if (key != null)
            context.renderFrame(graphics -> {
              // graphics.clearRect(0, 0, (int) context.getScreenInfo().getWidth(), (int)
              // context.getScreenInfo().getHeight());
              graphics.setColor(Color.WHITE);
              Display.drawWorld(graphics, display, cachedImages, world);
            });
          while (context.pollEvent() != null) {
          }
          key = null;
          var end = LocalTime.now().toNanoOfDay();
          var delta = (end - start) / 1000000;
          if (delta < frequency) {
            try {
              Thread.sleep(frequency - delta);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
        context.exit(0);
        System.out.println(world);
      });

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
