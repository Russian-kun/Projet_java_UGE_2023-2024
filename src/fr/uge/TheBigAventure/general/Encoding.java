package fr.uge.TheBigAventure.general;

import java.util.Map;
import java.util.Objects;

public record Encoding(Map<String, String> encodings) {

  public Encoding {
    Objects.requireNonNull(encodings);
  }
}
