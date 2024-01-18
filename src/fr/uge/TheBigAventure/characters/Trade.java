package fr.uge.TheBigAventure.characters;

import java.util.Arrays;
import java.util.Objects;

import fr.uge.TheBigAventure.gameObjects.Item;

public record Trade(Item item, Item price) {

  public Trade {
    Objects.requireNonNull(item);
    Objects.requireNonNull(price);
  }

  @Override
  public String toString() {
    return item + " for " + price;
  }

  public static Trade valueOf(String string) {
    // either object -> object
    // or object -> object name ie. "CASH -> KEY red"
    String[] parts = string.split("->");
    parts = Arrays.stream(parts).map(String::trim).toArray(String[]::new);
    if (parts.length == 1)
      throw new IllegalArgumentException("Trade must be of the form \"object -> object name\"");
    Item item = Item.valueOf(parts[0]), price = Item.valueOf(parts[1]);

    return new Trade(item, price);
  }

  public Item[] getItems() {
    return new Item[] { item, price };
  }

  public String getPriceName() {
    if (price.getSkin().toString().equals(price.getName()))
      return null;
    return price.getName();
  }

}
