package fr.uge.TheBigAventure.gameObjects;

public sealed interface ObstacleType extends GeneralSkin
    permits Obstacle.ImpassableType, Obstacle.PassableType, Door.DoorType {
}