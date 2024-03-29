package fr.uge.TheBigAventure.food;

import java.util.Map;
import java.util.Objects;
import java.util.Arrays;

import fr.uge.TheBigAventure.food.CoockableFood.CoockableFoodType;
import fr.uge.TheBigAventure.gameObjects.InventoryObjectSkin;
import fr.uge.TheBigAventure.gameObjects.Item;
import fr.uge.TheBigAventure.general.Position;

public class GeneralFood extends Item {
  private int health;

  public enum GeneralFoodType implements InventoryObjectSkin {
    BANANA(ItemSkin.BANANA), BOBA(ItemSkin.BOBA), BOTTLE(ItemSkin.BOTTLE), BURGER(ItemSkin.BURGER), CAKE(ItemSkin.CAKE),
    CHEESE(ItemSkin.CHEESE), DONUT(ItemSkin.DONUT), DRINK(ItemSkin.DRINK), EGG(ItemSkin.EGG), FRUIT(ItemSkin.FRUIT),
    FUNGUS(ItemSkin.FUNGUS), FUNGI(ItemSkin.FUNGI), LOVE(ItemSkin.LOVE), PIZZA(ItemSkin.PIZZA), POTATO(ItemSkin.POTATO),
    PUMPKIN(ItemSkin.PUMPKIN), TURNIP(ItemSkin.TURNIP), BUNNY(ItemSkin.BUNNY), CRAB(ItemSkin.CRAB), FISH(ItemSkin.FISH),
    FROG(ItemSkin.FROG), SNAIL(ItemSkin.SNAIL);

    private final ItemSkin itemSkin;

    GeneralFoodType(ItemSkin itemSkin) {
      this.itemSkin = itemSkin;
    }

    public ItemSkin getItemSkin() {
      return itemSkin;
    }

    public static GeneralFoodType convert(ItemSkin old) {
      GeneralFoodType tmp = Arrays.stream(GeneralFoodType.values()).filter(type -> type.getItemSkin() == old)
          .findFirst()
          .orElse(null);
      if (tmp != null) {
        return (Food.FoodType.convert(tmp) != null
            ? CoockableFoodType.convert(tmp) != null
                ? CoockableFoodType.convert(tmp).getFoodSkin()
                : Food.FoodType.convert(tmp).getFoodSkin()
            : Food.FoodType.convert(tmp).getFoodSkin());
      }

      return null;
    }
  }

  public GeneralFood(String name, GeneralFoodType skin, Position position, int health) {
    super(name, skin.getItemSkin(), position);
    this.health = health;
  }

  public static GeneralFood valueOf(Map<String, String> attributes) {
    Objects.requireNonNull(attributes);
    var name = attributes.get("name");
    GeneralFoodType skin = GeneralFoodType.valueOf(attributes.get("skin"));
    Position position = Position.valueOf(attributes.get("position"));
    var health = Integer.parseInt(attributes.get("health"));
    return new GeneralFood(name, skin, position, health);
  }

  @Override
  public int getHealth() {
    return health;
  }

  public void setHealth(int health) {
    this.health = health;
  }
}
