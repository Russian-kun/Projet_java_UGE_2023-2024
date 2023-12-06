package fr.uge.TheBigAventure;

// Classe représentant la zone d'un ennemi
class Zone {
  private Position topLeft;
  private int width;
  private int height;

  public Zone(Position topLeft, int width, int height) {
    this.topLeft = topLeft;
    this.width = width;
    this.height = height;
  }

  public static Zone valueOf(String zone) {
    // "(38, 24) (10 x 8)"
    String[] tot = zone.split("\\) *\\(");
    String[] dim = tot[1].replaceAll("[\\(\\) ]", "").split("x");
    return new Zone(new Position(tot[0]), Integer.parseInt(dim[0]), Integer.parseInt(dim[1]));
  }
}