package fr.uge.TheBigAventure.gameObjects;

import java.util.Map;
import java.util.Arrays;

import fr.uge.TheBigAventure.general.Position;

public class Weapon extends Item {
  private final int damage;

  public enum WeaponType implements InventoryObjectSkin {
    SWORD(ItemSkin.SWORD), SHOVEL(ItemSkin.SHOVEL), BOLT(ItemSkin.BOLT), STICK(ItemSkin.STICK);

    private final ItemSkin itemSkin;

    WeaponType(ItemSkin itemSkin) {
      this.itemSkin = itemSkin;
    }

    public ItemSkin getItemSkin() {
      return itemSkin;
    }

    public static WeaponType convert(ItemSkin old) {
      return Arrays.stream(WeaponType.values()).filter(type -> type.getItemSkin() == old).findFirst().orElse(null);
    }
  }

  public Weapon(String name, WeaponType skin, Position position, int damage) {
    super(name, skin.getItemSkin(), position);
    this.damage = damage;
  }

  public static Weapon valueOf(Map<String, String> attributes) {
    if (ItemSkin.valueOf(attributes.get("skin").toUpperCase()) == null)
      throw new IllegalArgumentException("skin must be a weapon");
    return new Weapon(attributes.get("name"), WeaponType.valueOf(attributes.get("skin").toUpperCase()),
        Position.valueOf(attributes.get("position")), Integer.parseInt(attributes.get("damage")));
  }

  public int getDamage() {
    return damage;
  }
}
