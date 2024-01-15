package fr.uge.TheBigAventure.characters;

import java.util.ArrayList;
import java.util.Objects;

import fr.uge.TheBigAventure.gameObjects.Item;

public record Inventory(ArrayList<Item> items) {

  public Inventory {
    Objects.requireNonNull(items);
  }

  public void addItem(Item item) {
    items.add(item);
  }

  public void removeItem(Item item) {
    items.remove(item);
  }

  public ArrayList<Item> getItems() {
    return items;
  }

  public boolean contains(Item item) {
    return items.contains(item);
  }

  public int size() {
    return items.size();
  }

}
