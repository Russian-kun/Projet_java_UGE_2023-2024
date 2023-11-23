package fr.uge.main;

import java.io.IOException;
import fr.uge.TheBigAventure.World;
import java.nio.file.Path;

public class Main {
  public static void main(String[] args) throws IOException {
    World world = World.readMap(Path.of("demo1.map"));
    System.out.println("\n" + world);
  }

}
