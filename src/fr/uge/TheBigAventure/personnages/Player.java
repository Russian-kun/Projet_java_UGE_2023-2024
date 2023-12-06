package fr.uge.TheBigAventure.personnages;

import java.util.ArrayList;
import java.util.Map;

import fr.uge.TheBigAventure.general.Position;
import fr.uge.TheBigAventure.objects.Element;
import fr.uge.TheBigAventure.objects.Item;

public class Player extends Element {
  private String name;
  private int health;
  private ArrayList<Item> inventory = new ArrayList<>();

  // Constructeur
  public Player(String name, String skin, Position position, int health) {
    super(skin, position, Element.Kind.PLAYER);
    if (Characters.valueOf(skin.toUpperCase()) == null)
      throw new IllegalArgumentException("skin must be a character");
    this.health = health;
    this.name = name;
    if (health < 0)
      throw new IllegalArgumentException("health must be positive");
  }

  public Player(Map<String, String> attributes) {
    super(attributes);
    if (Characters.valueOf(skin.toUpperCase()) == null)
      throw new IllegalArgumentException("skin must be a character");
    this.health = Integer.parseInt(attributes.get("health"));
    if (health < 0)
      throw new IllegalArgumentException("health must be positive");
  }

  public String getName() {
    return name;
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
