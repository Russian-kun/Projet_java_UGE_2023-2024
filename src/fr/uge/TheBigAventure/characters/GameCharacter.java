package fr.uge.TheBigAventure.characters;

import java.util.Map;

import fr.uge.TheBigAventure.gameObjects.Element;
import fr.uge.TheBigAventure.general.Position;

public class GameCharacter extends Element {
  int health;
  final int maxHealth;
  final String name;
  Position previousPosition = null;

  public enum CharacterSkin implements GeneralCharacterSkin {
    BABA, BADBAD, BAT, BEE, BIRD, BUG, BUNNY, CAT, CRAB, DOG, FISH, FOFO, FROG, GHOST, IT, JELLY, JIJI, KEKE, LIZARD,
    ME, MONSTER, ROBOT, SNAIL, SKULL, TEETH, TURTLE, WORM
  }

  public GameCharacter(String name, CharacterSkin skin, int health, int maxHealth, Position position, Kind kind) {
    super(skin, position, kind);
    if (health < 0)
      throw new IllegalArgumentException("health must be positive");

    this.name = name;
    this.health = health;
    this.maxHealth = maxHealth;
  }

  public static GameCharacter valueOf(Map<String, String> attributes) {
    if (CharacterSkin.valueOf(attributes.get("skin").toUpperCase()) == null)
      throw new IllegalArgumentException("skin must be a character");
    return new GameCharacter(attributes.get("name"), CharacterSkin.valueOf(attributes.get("skin").toUpperCase()),
        Integer.parseInt(attributes.get("health")), Integer.parseInt(attributes.get("health")),
        Position.valueOf(attributes.get("position")),
        Kind.valueOf(attributes.get("kind").toUpperCase()));
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

  public int getMaxHealth() {
    return maxHealth;
  }

  public void moveUp() {
    previousPosition.setY(position.getY());
    position.setY(position.getY() - 1);
  }

  public void moveDown() {
    previousPosition.setY(position.getY());
    position.setY(position.getY() + 1);
  }

  public void moveLeft() {
    previousPosition.setX(position.getX());
    position.setX(position.getX() - 1);
  }

  public void moveRight() {
    previousPosition.setX(position.getX());
    position.setX(position.getX() + 1);
  }

  public void moveBack() {
    var tmp = new Position(position.getX(), position.getY());

    if (previousPosition != null)
      position = previousPosition;
    previousPosition.setX(tmp.getX());
    previousPosition.setY(tmp.getY());
  }

  public Position getPreviousPosition() {
    return previousPosition;
  }

}
