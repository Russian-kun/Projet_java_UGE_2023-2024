package fr.uge.TheBigAventure.characters;

import java.util.ArrayList;
import java.util.Map;

import fr.uge.TheBigAventure.general.Position;
import fr.uge.TheBigAventure.general.World;
import fr.uge.TheBigAventure.objects.Element;
import fr.uge.TheBigAventure.objects.Item;
import fr.umlv.zen5.Event;

public class Player extends Characters {
  private ArrayList<Item> inventory = new ArrayList<>();

  public enum PlayerSkin {
    BABA, BADBAD, FOFO, IT
  }

  // Constructeur
  public Player(String name, String skin, Position position, int health) {
    super(name, skin, health, position, Element.Kind.PLAYER);
    if (PlayerSkin.valueOf(skin.toUpperCase()) == null)
      throw new IllegalArgumentException("skin must be a player");

    if (health < 0)
      throw new IllegalArgumentException("health must be positive");
  }

  public Player(Map<String, String> attributes) {
    super(attributes);
    if (PlayerSkin.valueOf(attributes.get("skin").toUpperCase()) == null)
      throw new IllegalArgumentException("skin must be a player");
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

  public boolean move(World world, Event event) {
    if (event == null)
      throw new IllegalArgumentException("event must not be null");
    previousPosition = new Position(position.getX(), position.getY());
    boolean moved = false;
    switch (event.getKey()) {
      case UP:
        if ((moved = world.isFree(position.getX(), position.getY() - 1)))
          moveUp();
        break;
      case DOWN:
        if ((moved = world.isFree(position.getX(), position.getY() + 1)))
          moveDown();
        break;
      case LEFT:
        if ((moved = world.isFree(position.getX() - 1, position.getY())))
          moveLeft();
        break;
      case RIGHT:
        if ((moved = world.isFree(position.getX() + 1, position.getY())))
          moveRight();
        break;
      default:
        break;
    }
    if(moved) {
	  	Item item = world.getItemPosition(position);
	    System.out.println(item);
	    if (item != null) {
	        addItem(item);
	        world.removeItemPosition(position);
	    }
    }
    return moved;
  }
  
  
  public void attack(Enemy enemy) {
    enemy.setHealth(enemy.getHealth() - 1);
  }

  public void heal(int quantity) {
    health += quantity;
  }

  public void takeDamage(int quantity) {
    health -= quantity;
  }

}
