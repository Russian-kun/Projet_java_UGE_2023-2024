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
import fr.uge.TheBigAventure.keys.AcceptedKeys;
import fr.uge.parser.Parser;
import fr.umlv.zen5.Application;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.Event.Action;
import fr.umlv.zen5.KeyboardKey;

public class Main {

  static boolean isInventoryVisible = false;

  public static void main(String[] args) throws IOException {
    final var frequency = 1000 / 60;
    var filePath = Path.of("fun.map");
    final World world;
    try {
      world = Parser.readMap(filePath);
      if (world == null) {
        System.err.println("Error while parsing map");
        return;
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
      return;
    }
    try {
      System.out.println(world);
      final ImageCache cachedImages = new ImageCache(new HashMap<GeneralSkin, Image>());

      Application.run(Color.BLACK, context -> {
        Display display = Display.createDisplay(world.worldMap().width(), world.worldMap().height(),
            (int) context.getScreenInfo().getWidth(),
            (int) context.getScreenInfo().getHeight());

        Event event = null;
        var start = LocalTime.now().toNanoOfDay();
        var end = LocalTime.now().toNanoOfDay();
        var delta = (end - start) / 1000000;
        while (true) {
          start = LocalTime.now().toNanoOfDay();
          event = context.pollEvent();
          KeyboardKey key = null;
          if (event != null && event.getAction() == Action.KEY_PRESSED) {
            if (AcceptedKeys.isMovementKey((key = event.getKey())))
              world.player().move(world, key);

            else if (key == KeyboardKey.I && event.getAction() == Action.KEY_PRESSED) {
              isInventoryVisible = true;
              // Afficher l'inventaire
              display.displayInventory(world, context, cachedImages);
              int pressCount = 0;

              do {
                event = context.pollEvent();
                if (event != null && event.getKey() == KeyboardKey.I) {
                  pressCount++;
                }
              } while (pressCount < 2);

              // Cacher l'inventaire
              isInventoryVisible = false;
              display.hideInventory(world, context, display, cachedImages);

            } else if (key == KeyboardKey.Q) {
              break;
            }
            event = null;
          }
          context.renderFrame(graphics -> {
            graphics.setColor(Color.WHITE);
            Display.drawWorld(graphics, display, cachedImages, world);
          });
          end = LocalTime.now().toNanoOfDay();
          delta = (end - start) / 1000000;
          if (delta < frequency) {
            try {
              Thread.sleep(frequency - delta); // sleep in ms
            } catch (InterruptedException e) {
              System.err.println(e.getMessage());
            }
          }
        }
        context.exit(0);
      });
    } catch (Exception e) {
      e.printStackTrace(); // TODO: Replace with system.err
      System.err.println("Error while running Zen5");
    }

  }

}
