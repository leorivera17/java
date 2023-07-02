import processing.core.PImage;

import java.util.List;
import java.util.Optional;

/**
 * A simple class representing a location in 2D space.
 */
public final class Point {

    private static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000;
    // have to be in sync since grows and gains health at same time

    private static final int SAPLING_HEALTH_LIMIT = 5;

    private final int x;

    public int getX() {
        return x;
    }

    private final int y;

    public int getY() {
        return y;
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public Optional<Entity> nearestEntity(List<Entity> entities) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = nearest.getPosition().distanceSquared(this);

            for (Entity other : entities) {
                int otherDistance = other.getPosition().distanceSquared(this);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }


    // don't technically need resource count ... full
    public DudeFull createDudeFull(String id, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new DudeFull(id, this, images, resourceLimit, 0, actionPeriod, animationPeriod, 0, 0);
    }


    // need resource count, though it always starts at 0
    public DudeNot createDudeNotFull(String id, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new DudeNot(id, this, images, resourceLimit, 0, actionPeriod, animationPeriod, 0, 0);
    }

    public WarpedDude createWarpedDude(String id, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new WarpedDude(id, this, images, 0, 0, actionPeriod, animationPeriod, 0, 0);
    }

    public Fairy createFairy(String id, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Fairy(id, this, images, 0, 0, actionPeriod, animationPeriod, 0, 0);
    }


    public Spider createSpider(String id, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Spider(id, this, images, 0, 0, actionPeriod, animationPeriod, 0, 0);
    }

    public Entity createBabySpider(String id, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new BabySpider(id, this, images, 0, 0, .5, .5, 0, 3);
    }


    // health starts at 0 and builds up until ready to convert to Tree, may need to add this
    public Sapling createSapling(String id, List<PImage> images, int health) {
        return new Sapling(id, this, images, 0, 0, SAPLING_ACTION_ANIMATION_PERIOD,
                SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, 0, SAPLING_HEALTH_LIMIT, 1);
    }

    // i changed this healthlimit to 0 instead of 3
    public Egg createEgg(String id, List<PImage> images, int health) {
        return new Egg(id,this, images, 0, 0, SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, health, 3);
    }


    public Stump createStump(String id, List<PImage> images) {
        return new Stump(id, this, images, 0, 0, 0, 0, 0, 0);
    }


    public Tree createTree(String id, double actionPeriod, double animationPeriod, int health, List<PImage> images) {
        return new Tree(id, this, images, 0, 0, actionPeriod, animationPeriod, health, 0);
    }

    public WarpedTree createWarpedTree(String id, double actionPeriod, double animationPeriod, int health, List<PImage> images) {
        return new WarpedTree(id, this, images, 0, 0, actionPeriod, animationPeriod, health, 0);
    }

    public Obstacle createObstacle(String id, double animationPeriod, List<PImage> images) {
        return new Obstacle(id, this, images, 0, 0, 0, animationPeriod, 0, 0);
    }

    public House createHouse(String id, List<PImage> images) {
        return new House(id, this, images, 0, 0, 0, 0, 0, 0);
    }

    private int distanceSquared(Point p2) {
        int deltaX = this.x - p2.x;
        int deltaY = this.y - p2.y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    public boolean adjacent(Point p2) {
        return (this.x == p2.x && Math.abs(this.y - p2.y) == 1) || (this.y == p2.y && Math.abs(this.x - p2.x) == 1);
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public boolean equals(Object other) {
        return other instanceof Point && ((Point) other).x == this.x && ((Point) other).y == this.y;
    }

    public int hashCode() {
        int result = 17;
        result = result * 31 + x;
        result = result * 31 + y;
        return result;
    }
}