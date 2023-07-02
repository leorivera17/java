import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
public class Fairy implements Entity, Move, Position, Execute, Schedule, AnimationPeriod{

    public void nextImage() {
        this.setImageIndex(getImageIndex() + 1);
    }








    private final double actionPeriod;


    public double getActionPeriod(){
        return actionPeriod;
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





    private List<PImage> images;

    public List<PImage> getImages() {
        return images;
    }

    public void setPImage(List<PImage> images){
        this.images = images;
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





    private int health;

    public void setHealth(int health){
        this.health = health;
    }

    public int getHealth(){
        return health;
    }





    private final double animationPeriod;



    private int resourceCount;
    private int resourceLimit;


    private int healthLimit;

    public void setHealthLimit(int healthLimit){
        this.healthLimit = healthLimit;
    }
    public int getHealthLimit(){
        return healthLimit;
    }







    public Fairy(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount,
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










    private Action createActivityAction(WorldModel world, ImageStore imageStore) {
        return new actionActivity(this, world, imageStore);
    }




    public Action createAnimationAction(int repeatCount) {
        return new actionAnimation(this, repeatCount);
    }




    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(this.position, new ArrayList<>(List.of(Stump.class)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (this.moveTo(world, fairyTarget.get(), scheduler)) {
                // look into this
                Sapling sapling = tgtPos.createSapling(Functions.SAPLING_KEY + "_" + fairyTarget.get().getId(),
                        imageStore.getImageList(Functions.SAPLING_KEY), 0);

                world.addEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
    }

















    // make this a common function, override
    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }








    // override also
    @Override
    public Point nextPosition(WorldModel world, Point destPos) {
        PathingStrategy strategy = new AStarPathingStrategy();

        List<Point> path = strategy.computePath(this.position, destPos, (p1) -> !world.isOccupied(p1) && world.withinBounds(p1),
                (p1, p2) -> p1.adjacent(p2), PathingStrategy.CARDINAL_NEIGHBORS);

//        System.out.println(path);

        if (path.isEmpty()){
            return getPosition();
        }

        return path.get(0);
    }




//    @Override
//    public Point nextPosition(WorldModel world, Point destPos) {
//        int horiz = Integer.signum(destPos.getX() - this.position.getX());
//        Point newPos = new Point(this.position.getX() + horiz, this.position.getY());
//
//        if (horiz == 0 || world.isOccupied(newPos)) {
//            int vert = Integer.signum(destPos.getY() - this.position.getY());
//            newPos = new Point(this.position.getX(), this.position.getY() + vert);
//
//            if (vert == 0 || world.isOccupied(newPos)) {
//                newPos = this.position;
//            }
//        }
//
//        return newPos;
//    }




}