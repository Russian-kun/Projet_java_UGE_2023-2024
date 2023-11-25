package fr.uge.TheBigAventure;

/**
 * Class representant les objets du jeu.
 */
public class Objects {
  private final Type type;

  public enum Type {
    BOOK, BOLT, BOX, CASH, CLOCK, COG, CRYSTAL, CUP, DRUM, FLAG, GEM, GUITAR, HIHAT, KEY, LAMP, LEAF, MIRROR, MOON, ORB,
    PANTS, PAPER, PLANET, RING, ROSE, SAX, SCISSORS, SEED, SHIRT, SHOVEL, STAR, STICK, SUN, SWORD, TRUMPET
  }

  public Objects(Type name) {
    this.type = name;
  }
}
