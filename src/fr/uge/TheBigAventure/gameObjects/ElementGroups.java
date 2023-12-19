package fr.uge.TheBigAventure.gameObjects;

import java.util.ArrayList;

public final record ElementGroups(ArrayList<Enemy> enemies, ArrayList<Item> items, ArrayList<Obstacle> obstacles,
        Player player) {
}
