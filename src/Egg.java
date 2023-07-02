import processing.core.PImage;

import java.util.List;

public class Egg implements Entity, Execute, Schedule, AnimationPeriod {
    private final String id;

    public String BABY_SPIDER_KEY = "babyspider";
    private Point position;
    private List<PImage> images;
    private int resourceLimit;
    private int resourceCount;
    private final double actionPeriod;
    private final double animationPeriod;
    private int health;
    private final int healthLimit;


    public void nextImage() {
        this.setImageIndex(getImageIndex() + 1);

    }
    public Egg (String id, Point position, List<PImage> images, int resourceLimit, int resourceCount,
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



    public String EGG_KEY = "egg";
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
       if (this.health >= this.healthLimit){
            BabySpider babyspider = new BabySpider(BABY_SPIDER_KEY + "_" + this.id, this.position, imageStore.getImageList("babyspider"), this.resourceLimit, this.resourceCount, this.actionPeriod,
                    this.animationPeriod, this.health, this.healthLimit);
            world.removeEntity(scheduler,this);
            world.addEntity(babyspider);
            babyspider.scheduleActions(scheduler, world, imageStore);
            return true;
        }
        return false;
    }


    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.health++;
        System.out.println("Egg health: " + this.health);
        if (!transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
        }
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent(this, this.createAnimationAction(0), getAnimationPeriod());
    }

    public double getAnimationPeriod() {
        return animationPeriod;
    }

    public Action createAnimationAction(int repeatCount) {
        return new actionAnimation( this, repeatCount);
    }

    private Action createActivityAction(WorldModel world, ImageStore imageStore) {
        return new actionActivity(this, world, imageStore);
    }
}
