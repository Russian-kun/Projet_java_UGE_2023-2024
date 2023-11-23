package fr.uge.main;

import java.io.IOException;
import fr.uge.TheBigAventure.World;

public class Main {
  public static void main(String[] args) throws IOException {
    World world = World.readMap("demo1.map");
    System.out.println("\n" + world);
  }

}
