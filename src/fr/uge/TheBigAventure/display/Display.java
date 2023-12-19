
package fr.uge.TheBigAventure.display;

import static java.lang.Math.min;

/**
 * Classe permettant de calculer et stocker les dimensions de divers éléments de
 * l'interface graphique.
 */
public record Display(int caseSize, int shiftX, int shiftY) {

  public Display {
  }

  public static Display createDisplay(int worldWidth, int worldHeight, int screenWidth, int screenHeight) {
    int caseSize = min(screenWidth / worldWidth, screenHeight / worldHeight);
    int shiftX = (screenWidth - worldWidth * caseSize) / 2;
    int shiftY = (screenHeight - worldHeight * caseSize) / 2;
    return new Display(caseSize, shiftX, shiftY);
  }
}