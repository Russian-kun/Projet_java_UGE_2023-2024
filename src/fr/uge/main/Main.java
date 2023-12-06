package fr.uge.main;

import java.io.IOException;

import fr.uge.TheBigAventure.Affichage;
import fr.uge.TheBigAventure.World;
import java.nio.file.Path;
import java.awt.Color;
import fr.umlv.zen5.*;

public class Main {
  public static void main(String[] args) throws IOException {
    Application.run(Color.BLACK, context -> {
      try {
        World world = World.readMap(Path.of("monster_house.map"));

        Affichage affichage = new Affichage(world, context);

        // On redimensionne les images
        context.renderFrame(graphics -> {
          graphics.setColor(Color.WHITE);
          affichage.draw(graphics);
        });
        context.pollOrWaitEvent(5 * 1000);
        context.exit(0);
        System.out.println(world);
      } catch (IOException e) {
        e.printStackTrace();
      }
      // context.renderFrame(graphics -> {
      // world.draw(graphics);
      // });
    });
  }

}
