package fr.uge.TheBigAventure.characters;

import java.util.Map;

import fr.uge.TheBigAventure.general.Position;

public class Friend extends GameCharacter {

  public Friend(String name, CharacterSkin skin, Position position, int health) {
    super(name, skin, health, health, position, Kind.FRIEND);
  }

  public static Friend valueOf(Map<String, String> attributes) {
    if (CharacterSkin.valueOf(attributes.get("skin").toUpperCase()) == null)
      throw new IllegalArgumentException("skin must be a character");
    return new Friend(
        attributes.get("name"),
        CharacterSkin.valueOf(attributes.get("skin").toUpperCase()),
        Position.valueOf(attributes.get("position")),
        Integer.parseInt(attributes.get("health")));
  }

}
