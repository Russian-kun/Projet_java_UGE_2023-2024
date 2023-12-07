package fr.uge.TheBigAventure.characters;

import java.util.Map;

import fr.uge.TheBigAventure.general.Position;
import fr.uge.TheBigAventure.objects.Element;

public class Characters extends Element {
  protected int health;
  protected String name;
  protected Position previousPosition = null;

  public enum CharacterSkin {
    BABA, BADBAD, BAT, BEE, BIRD, BUG, BUNNY, CAT, CRAB, DOG, FISH, FOFO, FROG, GHOST, IT, JELLY, JIJI, KEKE, LIZARD,
    ME, MONSTER, ROBOT, SNAIL, SKULL, TEETH, TURTLE, WORM
  }

  public Characters(String name, String skin, int health, Position position, Kind kind) {
    super(skin, position, kind);
    if (health < 0)
      throw new IllegalArgumentException("health must be positive");

    this.name = name;
    this.health = health;
  }

  public Characters(Map<String, String> attributes) {
    super(attributes);
    if (Characters.CharacterSkin.valueOf(skin.toUpperCase()) == null)
      throw new IllegalArgumentException("skin must be a character");
    this.health = Integer.parseInt(attributes.get("health"));
    this.name = attributes.get("name");
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

  public void moveUp() {
    previousPosition = new Position(position.getX(), position.getY());
    position.setY(position.getY() - 1);
  }

  public void moveDown() {
    previousPosition = new Position(position.getX(), position.getY());
    position.setY(position.getY() + 1);
  }

  public void moveLeft() {
    previousPosition = new Position(position.getX(), position.getY());
    position.setX(position.getX() - 1);
  }

  public void moveRight() {
    previousPosition = new Position(position.getX(), position.getY());
    position.setX(position.getX() + 1);
  }

  public void moveBack() {
    if (previousPosition != null)
      position = previousPosition;
  }

  public Position getPreviousPosition() {
    return previousPosition;
  }

}
