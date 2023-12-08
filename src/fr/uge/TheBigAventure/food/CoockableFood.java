package fr.uge.TheBigAventure.food;

public class CoockableFood extends GeneralFood {
  private boolean isCooked = false;

  public enum CoockableFoodType implements GeneralFoodType {
    BUNNY, CRAB, FISH, FROG, SNAIL
  }

  public CoockableFood(CoockableFoodType type) {
    super(type);
    this.type = type;
  }

  public CoockableFoodType type() {
    return CoockableFoodType.valueOf(type.toString());
  }

  public boolean isCooked() {
    return isCooked;
  }

  public void cook() {
    isCooked = true;
  }
}
