package fr.uge.TheBigAventure.gameObjects;

import java.util.Map;
import java.util.Objects;

import fr.uge.TheBigAventure.characters.Enemy;
import fr.uge.TheBigAventure.characters.Friend;
import fr.uge.TheBigAventure.characters.Player;
import fr.uge.TheBigAventure.food.GeneralFood;
import fr.uge.TheBigAventure.general.Position;

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
public abstract class Element {
  public final GeneralSkin skin;
  public final Kind kind;
  private Position position;

  public enum Kind {
    PLAYER,
    FRIEND,
    ITEM,
    ENEMY,
    OBSTACLE
  }

  public Element(GeneralSkin skin, Position position, Kind kind) {
    Objects.requireNonNull(skin);
    Objects.requireNonNull(position);
    Objects.requireNonNull(kind);
    this.skin = skin;
    this.position = position;
    this.kind = kind;
  }

  public GeneralSkin getSkin() {
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

  public static Element valueOf(Map<String, String> attributes) {
    Objects.requireNonNull(attributes);
    if (attributes.containsKey("player"))
      attributes.put("kind", "player");
    return switch (attributes.get("kind")) {
      case "player" -> Player.valueOf(attributes);
      case "item" -> {
        if (attributes.containsKey("damage"))
          yield Weapon.valueOf(attributes);
        else if (attributes.containsKey("health"))
          yield GeneralFood.valueOf(attributes);
        else
          yield Item.valueOf(attributes);
      }
      case "obstacle" -> {
        if (attributes.containsKey("locked"))
          yield Door.valueOf(attributes);
        else if (attributes.get("skin").equals("LEVER"))
          yield Lever.valueOf(attributes);
        else
          yield Obstacle.valueOf(attributes);
      }
      case "enemy" -> Enemy.valueOf(attributes);
      case "friend" -> Friend.valueOf(attributes);
      default -> throw new IllegalArgumentException("Unknown element");
    };
  }
}