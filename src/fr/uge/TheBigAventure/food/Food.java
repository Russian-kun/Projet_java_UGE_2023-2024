package fr.uge.TheBigAventure.food;

import java.util.Arrays;

import fr.uge.TheBigAventure.gameObjects.InventoryObjectSkin;
import fr.uge.TheBigAventure.general.Position;

public class Food extends GeneralFood {

  public enum FoodType implements InventoryObjectSkin {
    BANANA(GeneralFoodType.BANANA), BOBA(GeneralFoodType.BOBA), BOTTLE(GeneralFoodType.BOTTLE),
    BURGER(GeneralFoodType.BURGER), CAKE(GeneralFoodType.CAKE), CHEESE(GeneralFoodType.CHEESE),
    DONUT(GeneralFoodType.DONUT), DRINK(GeneralFoodType.DRINK), EGG(GeneralFoodType.EGG), FRUIT(GeneralFoodType.FRUIT),
    FUNGUS(GeneralFoodType.FUNGUS), FUNGI(GeneralFoodType.FUNGI), LOVE(GeneralFoodType.LOVE),
    PIZZA(GeneralFoodType.PIZZA), POTATO(GeneralFoodType.POTATO), PUMPKIN(GeneralFoodType.PUMPKIN),
    TURNIP(GeneralFoodType.TURNIP);

    private final GeneralFoodType type;

    FoodType(GeneralFoodType skin) {
      this.type = skin;
    }

    public GeneralFoodType getFoodSkin() {
      return type;
    }

    public static FoodType convert(GeneralFoodType old) {
      return Arrays.stream(FoodType.values()).filter(type -> type.getFoodSkin() == old).findFirst().orElse(null);
    }
  }

  public Food(String name, FoodType skin, Position position, int health) {
    super(name, skin.getFoodSkin(), position, health);
  }

}
