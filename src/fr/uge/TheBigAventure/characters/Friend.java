package fr.uge.TheBigAventure.characters;

import java.util.Map;

import fr.uge.TheBigAventure.general.Position;
import fr.uge.TheBigAventure.general.World;

public class Friend extends GameCharacter {
  private final TradeList trades;
  private final String text;

  public Friend(String name, CharacterSkin skin, Position position, int health, String text, TradeList trades) {
    super(name, skin, health, health, position, Kind.FRIEND);
    this.text = text;
    this.trades = trades;
  }

  public static Friend valueOf(Map<String, String> attributes) {
    if (CharacterSkin.valueOf(attributes.get("skin").toUpperCase()) == null)
      throw new IllegalArgumentException("skin must be a character");
    TradeList trades = attributes.containsKey("trade") ? TradeList.valueOf(attributes.get("trade")) : new TradeList();
    String text = attributes.containsKey("text") ? attributes.get("text") : "";
    return new Friend(
        attributes.get("name"),
        CharacterSkin.valueOf(attributes.get("skin").toUpperCase()),
        Position.valueOf(attributes.get("position")),
        Integer.parseInt(attributes.get("health")),
        text, trades);
  }

  public boolean update(World world) {
    // TODO
    return false;
  }

  public String getText() {
    return text;
  }

  public TradeList getTrades() {
    return trades;
  }

}
