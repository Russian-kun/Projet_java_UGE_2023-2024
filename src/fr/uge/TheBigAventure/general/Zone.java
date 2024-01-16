package fr.uge.TheBigAventure.general;

// Classe représentant la zone d'un ennemi
public class Zone {
  private Position topLeft;
  private int width;
  private int height;

  public Zone(Position topLeft, int width, int height) {
    this.topLeft = topLeft;
    this.width = width;
    this.height = height;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Zone))
      return false;
    Zone zone = (Zone) obj;
    return topLeft.equals(zone.topLeft) && width == zone.width && height == zone.height;
  }

  public static Zone valueOf(String zone) {
    // "(38, 24) (10 x 8)"
    String[] tot = zone.split("\\) *\\(");
    String[] dim = tot[1].replaceAll("[\\(\\) ]", "").split("x");
    return new Zone(Position.valueOf(tot[0]), Integer.parseInt(dim[0]), Integer.parseInt(dim[1]));
  }

  public Position getTopLeft() {
    return topLeft;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public boolean contains(Position position) {
    return position.getX() >= topLeft.getX() && position.getX() < topLeft.getX() + width
        && position.getY() >= topLeft.getY() && position.getY() < topLeft.getY() + height;
  }
}