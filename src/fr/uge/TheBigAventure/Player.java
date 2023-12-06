package fr.uge.TheBigAventure;

import java.util.ArrayList;
import java.util.Map;

public class Player extends Element {
  private int health;
  private ArrayList<Item> inventory = new ArrayList<>();

  // Constructeur
  public Player(String name, String skin, Position position, int health) {
    super(name, skin, position, Element.Kind.PLAYER);
    this.health = health;
  }

  public Player(Map<String, String> attributes) {
    super(attributes);
    this.health = Integer.parseInt(attributes.get("health"));
  }

  public int getHealth() {
    return health;
  }

  public void setHealth(int health) {
    this.health = health;
  }

  public void addItem(Item item) {
    inventory.add(item);
  }

  public void removeItem(Item item) {
    inventory.remove(item);
  }

  public ArrayList<Item> getInventory() {
    return inventory;
  }

}
