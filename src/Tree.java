import java.util.List;
import java.util.Objects;
import java.util.Random;
import processing.core.PImage;

public class Tree implements Entity, Execute, Transform, Plant, Schedule, AnimationPeriod{


    private static final String WARPED_TREE_KEY = "warpedtree";

    public void nextImage() {
        this.setImageIndex(getImageIndex() + 1);
    }










    private final String id;

    public String getId(){
        return id;
    }

    private Point position;

    public Point getPosition(){
        return position;
    }


    public void setPosition(Point position){
        this.position = position;
    }

    private final List<PImage> images;

    public List<PImage> getImages() {
        return images;
    }

    private int imageIndex;

    public int getImageIndex(){
        return imageIndex;
    }

    @Override
    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    private final double actionPeriod;
    public double getActionPeriod(){
        return actionPeriod;
    }
    private int health;


    public int getHealth(){
        return health;
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }


    public final double animationPeriod;


    private final int healthLimit;
    public int getHealthLimit(){
        return healthLimit;
    }



    private int resourceCount;
    private final int resourceLimit;






    public Tree(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount,
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
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent(this, this.createAnimationAction(0), getAnimationPeriod());
    }







    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (health <= 0) {
            Stump stump = this.position.createStump(Functions.STUMP_KEY + "_" + id,
                    imageStore.getImageList(Functions.STUMP_KEY));

            world.removeEntity(scheduler, this);
            world.addEntity(stump);
            return true;
        }

        String tile = world.getBackgroundCell(this.position).getId();
        if (Objects.equals(tile, "nether")){
            WarpedTree warpedTree = this.position.createWarpedTree(WARPED_TREE_KEY + "_" + id,actionPeriod,
                    animationPeriod,health ,imageStore.getImageList(WARPED_TREE_KEY));

            world.removeEntity(scheduler, this);
            world.addEntity(warpedTree);
            warpedTree.scheduleActions(scheduler, world, imageStore);
            return true;
        }
        return false;
    }

    private Action createActivityAction(WorldModel world, ImageStore imageStore) {
        return new actionActivity(this, world, imageStore);
    }


    public Action createAnimationAction(int repeatCount) {
        return new actionAnimation(this, repeatCount);
    }


    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        // this is supposed to be transform plant not for tree
        if (!transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
        }
    }

    private int getIntFromRange(int max, int min) {
        Random rand = new Random();
        return min + rand.nextInt(max-min);
    }

    private double getNumFromRange(double max, double min) {
        Random rand = new Random();
        return min + rand.nextDouble() * (max - min);
    }



}