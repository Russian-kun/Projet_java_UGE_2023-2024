package fr.uge.TheBigAventure;

class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(String position) {
        String[] tmp = position.replaceAll("[\\(\\)\\ ]", "").split(",");
        this.x = Integer.parseInt(tmp[0]);
        this.y = Integer.parseInt(tmp[1]);
    }

    public static Position valueOf(String position) {
        String[] tmp = position.replaceAll("[\\(\\) ]", "").split(",");
        int x = Integer.parseInt(tmp[0]);
        int y = Integer.parseInt(tmp[1]);
        return new Position(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
