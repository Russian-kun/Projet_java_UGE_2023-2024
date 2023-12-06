
package fr.uge.TheBigAventure;

import static java.lang.Math.min;

import java.awt.Graphics2D;
import java.util.Map;
import java.util.Objects;

import fr.umlv.zen5.ApplicationContext;

/**
 * Affichage
 */
public class Affichage {
  private final World world;
  ApplicationContext context;
  private final float caseSize;
  private final float shiftX;
  private final float shiftY;
  private final Map<String, String> encodings;

  public Affichage(World world, ApplicationContext context) {
    Objects.requireNonNull(context);
    Map<String, String> encodings = world.encodings();
    float screenWidth = context.getScreenInfo().getWidth(), screenHeight = context.getScreenInfo().getHeight();
    int width = world.width(), height = world.height();

    // On calcul la taille des cases
    float caseSize = min(screenWidth / width, screenHeight / height);
    this.world = world;
    this.encodings = encodings;
    this.caseSize = caseSize;
    this.shiftX = (screenWidth - width * caseSize) / 2;
    this.shiftY = (screenHeight - height * caseSize) / 2;
  }

  public static void draw(World world, Graphics2D graphics) {
    String[][] map = world.map();
    Map<String, String> encodings = world.encodings();
    for (int i = 0; i < world.height(); i++) {
      for (int j = 0; j < world.width(); j++)
        if (map[i][j] != null)
          // graphics.drawString(encodings.get(map[i][j]), j * 20, i * 20);
          // On va chercher l'image correspondante dans le dossier images
          // Les images sont nommées comme suit : NOM_0.webp
          graphics.drawImage(Image.getImage(encodings.get(map[i][j])), j * 20, i * 20, null);
    }
  }

  public void draw(Graphics2D graphics) {
    String[][] map = world.map();
    for (int i = 0; i < world.height(); i++) {
      for (int j = 0; j < world.width(); j++)
        if (map[i][j] != null)
          // graphics.drawString(encodings.get(map[i][j]), j * 20, i * 20);
          // On va chercher l'image correspondante dans le dossier images
          // Les images sont nommées comme suit : NOM_0.webp
          graphics.drawImage(Image.getImage(encodings.get(map[i][j])),
              (int) (j * caseSize + shiftX),
              (int) (i * caseSize + shiftY),
              null);
    }
  }
}