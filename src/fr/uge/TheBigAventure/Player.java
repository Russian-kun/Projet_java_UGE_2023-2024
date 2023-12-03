package fr.uge.TheBigAventure;

import java.util.ArrayList;

public class Player {
  private final String name;
  private final String skin;
  private int[] position = { 0, 0 };
  private int health;

  private final ArrayList<Items> inventory = new ArrayList<Items>();

  public Player(String nameString, String skinString, int x, int y, int playerHealth) {
    this.name = nameString;
    this.skin = skinString;
    this.position[0] = x;
    this.position[1] = y;
    this.health = playerHealth;
  }

  public String getName() {
    return name;
  }

  public String getSkin() {
    return skin;
  }

  public int getHealth() {
    return health;
  }
}
