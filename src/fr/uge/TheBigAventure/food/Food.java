package fr.uge.TheBigAventure.food;

public class Food extends GeneralFood {

  public enum FoodType implements GeneralFoodType {
    BANANA, BOBA, BOTTLE, BURGER, CAKE, CHEESE, DONUT, DRINK, EGG, FRUIT, FUNGUS, FUNGI, LOVE, PIZZA, POTATO, PUMPKIN,
    TURNIP
  }

  public Food(FoodType type) {
    super(type);
  }

  public FoodType type() {
    return FoodType.valueOf(type.toString());
  }

}
