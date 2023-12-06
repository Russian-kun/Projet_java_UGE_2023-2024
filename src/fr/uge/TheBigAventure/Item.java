package fr.uge.TheBigAventure;

import java.util.Map;

public class Item extends Element {
  private String name;

  // Class car les items ont des positions qui peuvent être modifiées
  public enum ItemSkins {
    BOOK, BOLT, BOX, CASH, CLOCK, COG, CRYSTAL, CUP, DRUM, FLAG, GEM, GUITAR, HIHAT, KEY, LAMP, LEAF, MIRROR, MOON, ORB,
    PANTS, PAPER, PLANET, RING, ROSE, SAX, SCISSORS, SEED, SHIRT, SHOVEL, STAR, STICK, SUN, SWORD, TRUMPET
  }

  public Item(String name, ItemSkins skin, Position position) {
    super(skin.toString(), position, Element.Kind.ITEM);
    this.name = name;
  }

  public Item(Map<String, String> attributes) {
    super(attributes);
  }

  public String getName() {
    return name;
  }

}
