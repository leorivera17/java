import java.util.List;
import java.util.Random;
import processing.core.PImage;

import java.util.*;

import processing.core.PImage;
public class Sapling implements Entity, Execute, Transform, Plant, Schedule, AnimationPeriod{



    public void nextImage() {
        this.setImageIndex(getImageIndex() + 1);
    }







    private double SAPLING_ACTION_ANIMATION_PERIOD = 1.000;
    // have to be in sync since grows and gains health at same time

    private int SAPLING_HEALTH_LIMIT = 5;









    private static final int TREE_HEALTH_MAX = 3;
    private static final int TREE_HEALTH_MIN = 1;
    private static final double TREE_ACTION_MIN = 1.000;
    private static final double TREE_ACTION_MAX = 1.400;
    private static final double TREE_ANIMATION_MIN = 0.050;
    private static final double TREE_ANIMATION_MAX = 0.600;



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

    }




    private int resourceCount;
    private int resourceLimit;




    private int healthLimit;
    public int getHealthLimit(){
        return healthLimit;
    }




    public final double animationPeriod;

    @Override
    public double getAnimationPeriod() {
        return animationPeriod;
    }






    public Sapling(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod,
                   double animationPeriod, double SAPLING_ACTION_ANIMATION_PERIOD, int health, int SAPLING_HEALTH_LIMIT, int healthLimit) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.SAPLING_ACTION_ANIMATION_PERIOD = SAPLING_ACTION_ANIMATION_PERIOD;
        this.health = health;
        this.SAPLING_HEALTH_LIMIT = SAPLING_HEALTH_LIMIT;
        this.healthLimit = healthLimit;
    }











    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent(this, this.createAnimationAction(0), getAnimationPeriod());
    }





//////////this is what i added because it was needed in this class

    private Action createActivityAction(WorldModel world, ImageStore imageStore) {
        return new actionActivity(this, world, imageStore);
    }


    public Action createAnimationAction(int repeatCount) {
        return new actionAnimation(this, repeatCount);
    }








    // MAYBE BECAUSE I'M NOT DONE WITH STUMP

    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.health <= 0) {
            Stump stump = this.position.createStump(Functions.STUMP_KEY + "_" + this.id, imageStore.getImageList(Functions.STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            // this is true but its false
            return true;
        } else if (this.health >= this.healthLimit) {
            Tree tree = this.position.createTree(Functions.TREE_KEY + "_" + this.id,
                    getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN), getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN),
                    getIntFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN), imageStore.getImageList(Functions.TREE_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(tree);
            tree.scheduleActions(scheduler, world, imageStore);
            // this was true but it was false
            return true;
        }
        // this was false but it was true
        return false;
    }








    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.health++;

        // also need to be transformPlant not transform for sapling
        // this is what scares me to be honest
        if (!transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
        }
    }

//    private boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
//        if (kind instanceof Tree.class) {
//            return transform(world, scheduler, imageStore);
//        } else if (kind instanceof Sapling.class) {
//            return transformSapling(world, scheduler, imageStore);
//        } else {
//            throw new UnsupportedOperationException(String.format("transformPlant not supported for %s", this));
//        }
//    }



    public boolean transformSapling(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.health <= 0) {
            Stump stump = this.position.createStump(Functions.STUMP_KEY + "_" + this.id, imageStore.getImageList(Functions.STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            // this is true but its false
            return true;
        } else if (this.health >= this.getHealthLimit()) {
            Tree tree = this.position.createTree(Functions.TREE_KEY + "_" + this.id,
                    getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN), getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN),
                    getIntFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN), imageStore.getImageList(Functions.TREE_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(tree);
            tree.scheduleActions(scheduler, world, imageStore);
            // this was true but it was false
            return true;
        }
        // this was false but it was true
        return false;
    }










//////////this is what I added because it was needed in this class
    private int getIntFromRange(int max, int min) {
        Random rand = new Random();
        return min + rand.nextInt(max-min);
    }

    private double getNumFromRange(double max, double min) {
        Random rand = new Random();
        return min + rand.nextDouble() * (max - min);
    }













}