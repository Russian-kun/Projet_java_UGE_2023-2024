package fr.uge.TheBigAventure.keys;

import java.util.Arrays;

import fr.umlv.zen5.KeyboardKey;

public enum AcceptedKeys {
  UP, DOWN, LEFT, RIGHT, I, Q, SPACE;

  public enum MovementKeys {
    UP, DOWN, LEFT, RIGHT
  }

  public static boolean isMovementKey(AcceptedKeys key) {
    return Arrays.stream(MovementKeys.values())
        .anyMatch(movementKey -> movementKey.toString().equals(key.toString()));
  }

  public static boolean isMovementKey(KeyboardKey key) {
    return Arrays.stream(MovementKeys.values())
        .anyMatch(movementKey -> movementKey.toString().equals(key.toString()));
  }

  public static boolean isAcceptedKey(KeyboardKey key) {
    return Arrays.stream(AcceptedKeys.values())
        .anyMatch(acceptedKey -> acceptedKey.toString().equals(key.toString()));
  }
}
