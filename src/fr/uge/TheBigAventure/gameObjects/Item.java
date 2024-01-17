package fr.uge.TheBigAventure.gameObjects;

import java.util.Map;

import fr.uge.TheBigAventure.general.Position;

public class Item extends Element {
  private String name;

  // Class car les items ont des positions qui peuvent être modifiées
  public enum ItemSkin implements InventoryObjectSkin {
    BOOK, BOLT, BOX, CASH, CLOCK, COG, CRYSTAL, CUP, DRUM, FLAG, GEM, GUITAR, HIHAT, KEY, LAMP, LEAF, MIRROR, MOON, ORB,
    PANTS, PAPER, PLANET, RING, ROSE, SAX, SCISSORS, SEED, SHIRT, SHOVEL, STAR, STICK, SUN, SWORD, TRUMPET, VASE,
    BANANA, BOBA, BOTTLE, BURGER, CAKE, CHEESE, DONUT, DRINK, EGG, FRUIT, FUNGUS, FUNGI, LOVE, PIZZA, POTATO, PUMPKIN,
    TURNIP, BUNNY, CRAB, FISH, FROG, SNAIL;
  }

  public Item(String name, ItemSkin skin, Position position) {
    super(skin, position, Element.Kind.ITEM);
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Item))
      return false;
    Item item = (Item) o;
    return item.getName().equals(this.getName()) && item.getPosition().equals(this.getPosition());
  }

  public static Element valueOf(Map<String, String> attributes) {
    ItemSkin skinEnum = null;
    try {
      skinEnum = ItemSkin.valueOf(attributes.get("skin").toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Skin must be an item");
    }
    return new Item(attributes.get("name"), skinEnum,
        Position.valueOf(attributes.get("position")));
  }

  public String getName() {
    return name;
  }

  public int getHealth() {
    return 0;
  }

}
