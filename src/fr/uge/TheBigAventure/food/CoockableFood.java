package fr.uge.TheBigAventure.food;

import java.util.Arrays;

import fr.uge.TheBigAventure.gameObjects.InventoryObjectSkin;
import fr.uge.TheBigAventure.general.Position;

public class CoockableFood extends GeneralFood {
  private boolean isCooked = false;

  public enum CoockableFoodType implements InventoryObjectSkin {
    BUNNY(GeneralFoodType.BUNNY),
    CRAB(GeneralFoodType.CRAB),
    FISH(GeneralFoodType.FISH),
    FROG(GeneralFoodType.FROG),
    SNAIL(GeneralFoodType.SNAIL);

    private GeneralFoodType skin;

    CoockableFoodType(GeneralFoodType type) {
      this.skin = type;
    }

    public GeneralFoodType getFoodSkin() {
      return skin;
    }

    public static CoockableFoodType convert(GeneralFoodType old) {
      return Arrays.stream(CoockableFoodType.values()).filter(type -> type.getFoodSkin() == old).findFirst()
          .orElse(null);
    }
  }

  public CoockableFood(String name, CoockableFoodType skin, Position position, int health) {
    super(name, skin.getFoodSkin(), position, health);
  }

  public boolean isCooked() {
    return isCooked;
  }

  public void cook() {
    isCooked = true;
    setHealth(getHealth() * 2);
  }

}
