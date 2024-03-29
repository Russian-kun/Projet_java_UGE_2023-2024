package fr.uge.TheBigAventure.characters;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

import fr.uge.TheBigAventure.gameObjects.Item;
import fr.uge.TheBigAventure.general.Position;
import fr.uge.TheBigAventure.general.World;

public record Inventory(ArrayList<Item> items) {

  public Inventory {
    Objects.requireNonNull(items);
  }

  public void addItem(Item item) {
    items.add(item);
  }

  public void removeItem(Item item) {
    items.remove(find(item));
  }

  public ArrayList<Item> getItems() {
    return items;
  }

  public boolean contains(Item item) {
    return find(item) != null;
  }

  public Item find(Item item) {
    if (item.getSkin().toString().equals(item.getName()) || item.getName() == null)
      return items.stream().filter(i -> i.getSkin() == item.getSkin()).findFirst().orElse(null);
    return items.stream().filter(i -> i.getSkin() == item.getSkin() && i.getName().equals(item.getName())).findFirst()
        .orElse(null);
  }

  public int size() {
    return items.size();
  }

  public void pickupWorldItem(World world, Position player, Position position) {
    Item item = world.getItemPosition(position);
    if (item != null) {
      item.setPosition(player);
      addItem(item);
      world.removeItemPosition(position);
    }
  }

  // parameter of type : item -> {item ...}
  public void removeFirstItem(Predicate<? super Item> filter) {
    items.removeIf(filter);
  }
}
