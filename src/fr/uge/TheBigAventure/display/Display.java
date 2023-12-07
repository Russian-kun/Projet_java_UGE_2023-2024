
package fr.uge.TheBigAventure.display;

import static java.lang.Math.min;
import java.util.Objects;

import fr.uge.TheBigAventure.general.World;
import fr.umlv.zen5.ApplicationContext;

/**
 * Affichage
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

    // On calcul la taille des cases
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
}