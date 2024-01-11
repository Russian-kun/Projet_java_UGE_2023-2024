package fr.uge.TheBigAventure.characters;

import java.util.ArrayList;
import java.util.Map;

import fr.uge.TheBigAventure.gameObjects.Element;
import fr.uge.TheBigAventure.gameObjects.Item;
import fr.uge.TheBigAventure.general.Position;
import fr.uge.TheBigAventure.general.World;
import fr.umlv.zen5.Event;

public class Player extends GameCharacter {
  private final ArrayList<Item> inventory = new ArrayList<>();

  public enum PlayerSkin implements GeneralCharacterSkin {
    BABA(CharacterSkin.BABA),
    BADBAD(CharacterSkin.BADBAD),
    FOFO(CharacterSkin.FOFO),
    IT(CharacterSkin.IT);

    private final CharacterSkin characterSkin;

    PlayerSkin(CharacterSkin characterSkin) {
      this.characterSkin = characterSkin;
    }

    public CharacterSkin getCharacterSkin() {
      return this.characterSkin;
    }
  }

  // Constructeur
  public Player(String name, PlayerSkin skin, Position position, int health) {
    super(name, skin.getCharacterSkin(), health, position, Element.Kind.PLAYER);
    if (health < 0)
      throw new IllegalArgumentException("health must be positive");
  }

  public static Player valueOf(Map<String, String> attributes) {
    if (PlayerSkin.valueOf(attributes.get("skin").toUpperCase()) == null)
      throw new IllegalArgumentException("skin must be a player");
    return new Player(attributes.get("name"), PlayerSkin.valueOf(attributes.get("skin").toUpperCase()),
        new Position(attributes.get("position")), Integer.parseInt(attributes.get("health")));
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
