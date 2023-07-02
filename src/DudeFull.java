import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DudeFull implements Entity, Move, Position, Execute, Schedule, AnimationPeriod{

    public void nextImage() {
        this.setImageIndex(getImageIndex() + 1);
    }





    private Point position;

    private final String id;

    @Override
    public String getId(){
        return id;
    }




    public Point getPosition(){
        return position;
    }






    public void setPosition(Point position){
        this.position = position;
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
    private final int resourceLimit;




    private final List<PImage> images;

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



    private final double animationPeriod;





    private int healthLimit;

    public void setHealthLimit(int healthLimit){
        this.healthLimit = healthLimit;
    }
    public int getHealthLimit(){
        return healthLimit;
    }











    public DudeFull(String id, Point position, List<PImage> images, int resourceLimit,
                    int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        this.id = id;
        this.actionPeriod = actionPeriod;
        this.position = position;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.images = images;
        this.animationPeriod = animationPeriod;
        this.health = health;
        this.healthLimit = healthLimit;
    }




















    @Override
    public double getAnimationPeriod(){
        return animationPeriod;
    }




    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent(this, this.createAnimationAction(0), getAnimationPeriod());
    }








//////////this is what I added because it was needed in this class

    private Action createActivityAction(WorldModel world, ImageStore imageStore) {
        return new actionActivity(this, world, imageStore);
    }




    public Action createAnimationAction(int repeatCount) {
        return new actionAnimation(this, repeatCount);
    }






    //////////this is what I added because it was needed in this class
    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.getPosition())) {
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }



    //LOOK AT THIS bc the transformfull and not transform
    private void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        DudeNot dude = this.position.createDudeNotFull(this.id, this.actionPeriod, this.animationPeriod, this.resourceLimit, this.images);

        world.removeEntity(scheduler, this);

        world.addEntity(dude);
        dude.scheduleActions(scheduler, world, imageStore);
    }








    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(this.position, new ArrayList<>(List.of(House.class)));

        if (fullTarget.isPresent() && this.moveTo(world, fullTarget.get(), scheduler)) {
            this.transformFull(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
        }

    }









    @Override
    public Point nextPosition(WorldModel world, Point destPos) {

        PathingStrategy strategy = new AStarPathingStrategy();

        List<Point> path = strategy.computePath(this.position, destPos, (p1) -> !(world.isOccupied(p1)
                        && !(world.getOccupancyCell(p1) instanceof Stump && world.withinBounds(p1))),
                        (p1, p2) -> p1.adjacent(p2), PathingStrategy.CARDINAL_NEIGHBORS);

        if (path.isEmpty()){
            return getPosition();
        }

        return path.get(0);
    }



}
