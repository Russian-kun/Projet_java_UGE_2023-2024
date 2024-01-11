package fr.uge.TheBigAventure.gameObjects;

import java.util.Map;

import fr.uge.TheBigAventure.general.Position;

public class Item extends Element {
  private String name;

  // Class car les items ont des positions qui peuvent être modifiées
  public enum ItemSkins implements GeneralSkin {
    BOOK, BOLT, BOX, CASH, CLOCK, COG, CRYSTAL, CUP, DRUM, FLAG, GEM, GUITAR, HIHAT, KEY, LAMP, LEAF, MIRROR, MOON, ORB,
    PANTS, PAPER, PLANET, RING, ROSE, SAX, SCISSORS, SEED, SHIRT, SHOVEL, STAR, STICK, SUN, SWORD, TRUMPET, VASE
  }

  public Item(String name, ItemSkins skin, Position position) {
    super(skin, position, Element.Kind.ITEM);
    this.name = name;
  }

  public static Item valueOf(String name, String skin, Position position) {
    if (ItemSkins.valueOf(skin.toUpperCase()) == null)
      throw new IllegalArgumentException("skin must be an item");
    return new Item(name, ItemSkins.valueOf(skin.toUpperCase()), position);
  }

  // return new Item(attributes.get("name"), attributes.get("skin"), new
  // Position(attributes.get("position")));
  // }

  public static Element valueOf(Map<String, String> attributes) {
    if (ItemSkins.valueOf(attributes.get("skin").toUpperCase()) == null)
      throw new IllegalArgumentException("skin must be an item");
    return new Item(attributes.get("name"), ItemSkins.valueOf(attributes.get("skin").toUpperCase()),
        Position.valueOf(attributes.get("position")));
  }

  public String getName() {
    return name;
  }

}
