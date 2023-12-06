package fr.uge.TheBigAventure;

import java.util.Map;
import java.util.Objects;

// Les éléments de la carte sont définie par la section [element] qui définie des sous-sections [element] qui 
// définissent les éléments. Par exemple, le joueur est défine comme ceci
//                   [element]
//                     name: John
//                     player: true
//                     skin: BABA
//                     position: (1,1)
//                     health: 10

// une épée qui traine et qui peut être ramassée par un joueur est définie comme cela,
//                   [element]
//                     name: Durendale
//                     skin: SWORD
//                     position: (3,1)
//                     kind: item
//                     damage: 15

// le courant d'une rivière est défini comme cela,
//                   [element]
//                     skin: WATER
//                     zone: (10, 10) (5 x 5)
//                     flow: NORTH

// et un ennemi est défini comme cela
//                   [element]
//                     name: Waldo
//                     skin: CRAB
//                     position: (1, 3)
//                     kind: enemy
//                     health: 10
//                     zone: (1, 1) (5 x 3)
//                     behavior: agressive
//                     damage: 10
abstract class Element {
  protected String skin;
  protected Position position;
  protected Kind kind;

  public enum Kind {
    PLAYER,
    ITEM,
    ENEMY,
    OBSTACLE
  }

  public Element(String skin, Position position, Kind kind) {
    Objects.requireNonNull(skin);
    Objects.requireNonNull(position);
    Objects.requireNonNull(kind);
    this.skin = skin;
    this.position = position;
    this.kind = kind;
  }

  public Element(Map<String, String> attributes) {
    Objects.requireNonNull(attributes);
    this.skin = attributes.get("skin");
    this.position = new Position(attributes.get("position"));
    this.kind = Kind.valueOf(attributes.get("kind").toUpperCase());
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