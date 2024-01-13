package fr.uge.TheBigAventure.keys;

import fr.umlv.zen5.KeyboardKey;

public enum AcceptedKeys {
  UP, DOWN, LEFT, RIGHT, I, Q;

  public enum MovementKeys {
    UP, DOWN, LEFT, RIGHT
  }

  public static boolean isMovementKey(AcceptedKeys key) {
    try {
      MovementKeys.valueOf(key.name());
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean isMovementKey(KeyboardKey key) {
    try {
      MovementKeys.valueOf(key.name());
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
