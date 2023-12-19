package fr.uge.TheBigAventure.general;

import java.util.Objects;

public record MapData(String[][] data) {

  public MapData {
    Objects.requireNonNull(data);
  }

}
