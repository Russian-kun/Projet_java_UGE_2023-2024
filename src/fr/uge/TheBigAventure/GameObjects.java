package fr.uge.TheBigAventure;

import java.util.Objects;

/**
 * Class representant les objets du jeu.
 */
public class GameObjects implements Items {
  private final Type skin;
  private final String name;

  public GameObjects(String objectName, Type skinName) {
    Objects.requireNonNull(objectName);
    Objects.requireNonNull(skinName);
    skin = skinName;
    name = objectName;
  }
}
