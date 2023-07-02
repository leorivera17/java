import processing.core.PImage;

import java.util.List;

public class WarpedTree implements Entity, Execute, Transform, Plant, Schedule, AnimationPeriod{

    public final double animationPeriod;

    private final int healthLimit;
    public int getHealthLimit(){
        return healthLimit;
    }


    private int health;
    private String id;
    private Point position;
    private List<PImage> images;
    private final double actionPeriod;
    private int resourceCount;
    private final int resourceLimit;

    public void nextImage() {
        this.setImageIndex(getImageIndex() + 1);
    }



    public WarpedTree(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount,
                double actionPeriod, double animationPeriod, int health, int healthLimit) {
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
        if (!transform(world, scheduler, imageStore)) {

            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
        }
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent(this, this.createAnimationAction(0), getAnimationPeriod());
    }

    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        System.out.println(health);
        if (health <= 0) {
            Stump stump = this.position.createStump(Functions.STUMP_KEY + "_" + id, imageStore.getImageList(Functions.STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        }

        return false;
    }

    private Action createActivityAction(WorldModel world, ImageStore imageStore) {
        return new actionActivity(this, world, imageStore);
    }


    public Action createAnimationAction(int repeatCount){
        return new actionAnimation(this, repeatCount);
    }
}
