package fr.uge.TheBigAventure;

import java.util.Map;
import java.util.Objects;

abstract class Element {
  protected String name;
  protected String skin;
  protected Position position;
  protected Kind kind;

  public enum Kind {
    PLAYER,
    ITEM,
    ENEMY,
    OBSTACLE
  }

  public Element(String name, String skin, Position position, Kind kind) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(skin);
    Objects.requireNonNull(position);
    Objects.requireNonNull(kind);
    this.name = name;
    this.skin = skin;
    this.position = position;
    this.kind = kind;
  }

  public Element(Map<String, String> attributes) {
    Objects.requireNonNull(attributes);
    this.name = attributes.get("name");
    this.skin = attributes.get("skin");
    this.position = new Position(attributes.get("position"));
    this.kind = Kind.valueOf(attributes.get("kind").toUpperCase());
  }

  public String getName() {
    return name;
  }

  public String getSkin() {
    return skin;
  }

  // Retourne la position du joueur
  public Position getPosition() {
    return position;
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  public Kind getKind() {
    return kind;
  }

}