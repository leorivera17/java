import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class WarpedDude implements Entity, Move, Position, Execute, Transform, Schedule, AnimationPeriod{

    private final String id;
    private Point position;
    private List<PImage> images;
    private int resourceLimit;
    private int resourceCount;
    private final double actionPeriod;
    private final double animationPeriod;
    private int health;
    private final int healthLimit;
    public WarpedDude(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount,
                      double actionPeriod, double animationPeriod, int health, int healthLimit){
        this.id = id;
        this.position = position;
        this.images = images;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
        this.healthLimit = healthLimit;
    }
    @Override
    public double getAnimationPeriod() {
        return animationPeriod;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public void setPosition(Point position) {
        this.position = position;
    }

    @Override
    public List<PImage> getImages() {
        return images;
    }

    private int imageIndex;
    @Override
    public int getImageIndex() {
        return imageIndex;
    }

    @Override
    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(this.position, new ArrayList<>(Arrays.asList(BabySpider.class)));
        if (target.isEmpty() || !this.moveTo(world, target.get(), scheduler) || !this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
        }
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.getPosition())) {
            world.removeEntity(scheduler,target);

            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    @Override
    public Point nextPosition(WorldModel world, Point destPos) {
        PathingStrategy strategy = new AStarPathingStrategy();

        List<Point> path = strategy.computePath(this.position, destPos, (p1) -> !(world.isOccupied(p1)
                        && !(world.getOccupancyCell(p1) instanceof Stump && world.withinBounds(p1))),
                (p1, p2) -> p1.adjacent(p2), PathingStrategy.CARDINAL_NEIGHBORS);

//        System.out.println(path);
        if (path.isEmpty()){
            return getPosition();
        }

        return path.get(0);
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent(this, this.createAnimationAction(0), getAnimationPeriod());
    }

    private Action createActivityAction(WorldModel world, ImageStore imageStore) {
        return new actionActivity(this, world, imageStore);
    }



    public Action createAnimationAction(int repeatCount) {
        return new actionAnimation(this,repeatCount);
    }

    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        return false;
    }
}
