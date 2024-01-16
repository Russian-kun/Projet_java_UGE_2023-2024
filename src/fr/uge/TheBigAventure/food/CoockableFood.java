package fr.uge.TheBigAventure.food;

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
