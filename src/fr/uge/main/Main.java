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

  static boolean isInventoryVisible = false;
	
  public static void main(String[] args) throws IOException {
    int frequency = 1000 / 120;
    
    Application.run(Color.BLACK, context -> {
      try {
        World world = World.readMap(Path.of("fun.map"));

        Display show = new Display(world, context);
        HashMap<String, BufferedImage> cachedImages = new HashMap<>();
        Event key = null;
        // Boolean moved = false;

        context.renderFrame(graphics -> {
          graphics.setColor(Color.WHITE);
          world.draw(graphics, show, cachedImages);
        });

        while (true) {
          // moved = false;
          var start = System.currentTimeMillis();
          key = context.pollEvent();
          if (key != null) {
            world.player().move(world, key);
            
            if (!isInventoryVisible && key.getKey() == KeyboardKey.I) {
            	// Afficher l'inventaire
              isInventoryVisible = true;
              show.displayInventory(world, context, cachedImages);
              int pressCount = 0;

              do {
                  key = context.pollEvent();
                  if (key != null && key.getKey() == KeyboardKey.I) {
                      pressCount++;
                  }
              } while (pressCount < 2);

            	
            	// Cacher l'inventaire
              isInventoryVisible = false;
              show.hideInventory(world, context, show, cachedImages);
              
            }
            else if (key.getKey() == KeyboardKey.Q) {
              break;
            }            	
          }
          if (key != null)
            context.renderFrame(graphics -> {
              // graphics.clearRect(0, 0, (int) context.getScreenInfo().getWidth(), (int)
              // context.getScreenInfo().getHeight());
              graphics.setColor(Color.WHITE);
              world.draw(graphics, show, cachedImages);
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
            break;
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
