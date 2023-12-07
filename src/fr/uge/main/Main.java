package fr.uge.main;

import java.io.IOException;

import fr.uge.TheBigAventure.display.Display;
import fr.uge.TheBigAventure.general.World;

import java.nio.file.Path;
import java.util.HashMap;
import java.awt.Color;
import java.awt.image.BufferedImage;

import fr.umlv.zen5.*;

public class Main {
  public static void main(String[] args) throws IOException {
    int frequency = 1000 / 120;
    Application.run(Color.BLACK, context -> {
      try {
        World world = World.readMap(Path.of("badGridDataEncodingDefinedTwice.map"));

        Display affichage = new Display(world, context);
        HashMap<String, BufferedImage> cachedImages = new HashMap<>();
        Event key = null;
        // Boolean moved = false;

        context.renderFrame(graphics -> {
          graphics.setColor(Color.WHITE);
          world.draw(graphics, affichage, cachedImages);
        });

        while (true) {
          // moved = false;
          var start = System.currentTimeMillis();
          key = context.pollEvent();
          if (key != null) {
            world.player().move(world, key);
            if (key.getKey() == KeyboardKey.Q)
              break;
          }
          if (key != null)
            context.renderFrame(graphics -> {
              // graphics.clearRect(0, 0, (int) context.getScreenInfo().getWidth(), (int)
              // context.getScreenInfo().getHeight());
              graphics.setColor(Color.WHITE);
              world.draw(graphics, affichage, cachedImages);
            });
          while (context.pollEvent() != null) {
          }
          key = null;
          var end = System.currentTimeMillis();
          try {
            Thread.sleep(Math.max(0, frequency - (end - start)));
            // System.out.println("FPS: " + (1000 / (end - start)));
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        context.exit(0);
        System.out.println(world);
      } catch (Exception e) {
        e.printStackTrace();
        context.exit(1);
      }
    });
  }

}
