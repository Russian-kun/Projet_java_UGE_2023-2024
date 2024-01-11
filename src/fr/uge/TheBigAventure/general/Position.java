package fr.uge.TheBigAventure.general;

public class Position {
  private int x, y;

  public Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public String toString() {
    return "(" + x + "," + y + ")";
  }

  public static Position valueOf(String position) {
    String[] tmp = position.replaceAll("[\\(\\) ]", "").split(",");
    int x = Integer.parseInt(tmp[0]);
    int y = Integer.parseInt(tmp[1]);
    if (x < 0 || y < 0)
      throw new IllegalArgumentException("x and y must be positive");
    return new Position(x, y);
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public void setX(int x) {
    if (x < 0)
      throw new IllegalArgumentException("x must be positive");
    this.x = x;
  }

  public void setY(int y) {
    if (y < 0)
      throw new IllegalArgumentException("y must be positive");
    this.y = y;
  }
}
