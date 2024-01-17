package fr.uge.TheBigAventure.gameObjects;

import java.util.Map;

import fr.uge.TheBigAventure.food.CoockableFood;
import fr.uge.TheBigAventure.food.Food;
import fr.uge.TheBigAventure.food.GeneralFood;
import fr.uge.TheBigAventure.food.CoockableFood.CoockableFoodType;
import fr.uge.TheBigAventure.food.GeneralFood.GeneralFoodType;
import fr.uge.TheBigAventure.gameObjects.Weapon.WeaponType;
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
    if (item == this)
      return true;
    if (item.getName() == null || this.getName() == null)
      return item.getPosition().equals(this.getPosition());
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

  public static Item valueOf(String givenString) {
    String[] parts = givenString.split(" ");
    String skin = parts[0];
    String name = parts.length > 1 ? parts[1] : skin;

    ItemSkin skinEnum = getItemSkin(skin);
    Position position = new Position(0, 0);

    if (Weapon.WeaponType.convert(skinEnum) != null) {
      return createWeapon(name, skinEnum, position);
    } else if (GeneralFood.GeneralFoodType.convert(skinEnum) != null) {
      return createGeneralFood(name, skinEnum, position);
    } else {
      return createItem(name, skinEnum, position);
    }
  }

  private static ItemSkin getItemSkin(String skin) {
    try {
      return ItemSkin.valueOf(skin.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Skin must be an item");
    }
  }

  private static Item createWeapon(String name, ItemSkin skin, Position position) {
    WeaponType weaponSkin = WeaponType.convert(skin);
    return new Weapon(name, weaponSkin, position, 2);
  }

  private static Item createGeneralFood(String name, ItemSkin skin, Position position) {
    GeneralFoodType foodSkin = GeneralFoodType.convert(skin);
    if (CoockableFoodType.convert(foodSkin) != null)
      return new CoockableFood(name, CoockableFoodType.convert(foodSkin), position, 5);
    else if (Food.FoodType.convert(foodSkin) != null)
      return new Food(name, Food.FoodType.convert(foodSkin), position, 5);

    return new GeneralFood(name, foodSkin, position, 5);
  }

  private static Item createItem(String name, ItemSkin skin, Position position) {
    return new Item(name, skin, position);
  }

  public static Item valueOf(Item item) {
    return new Item(item.getName(), (ItemSkin) item.getSkin(), item.getPosition());
  }

  public String getName() {
    return name;
  }

  public int getHealth() {
    return 0;
  }

}
