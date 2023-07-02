import processing.core.PImage;

import java.util.*;

public class DudeNot implements Entity, Move, Position, Execute, Transform, Schedule, AnimationPeriod{



    public void nextImage() {
        this.setImageIndex(getImageIndex() + 1);
    }





    private final String id;

    @Override
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




    private final double actionPeriod;
    public double getActionPeriod(){
        return actionPeriod;
    }





    private int health;

    public void setHealth(int health){
        this.health = health;
    }

    public int getHealth(){
        return health;
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



    private final int healthLimit;
    public int getHealthLimit(){
        return healthLimit;
    }









    public DudeNot(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount,
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






//////////this is what I added because it was needed in this class

    private Action createActivityAction(WorldModel world, ImageStore imageStore) {
        return new actionActivity(this, world, imageStore);
    }





    public Action createAnimationAction(int repeatCount) {
        return new actionAnimation(this,repeatCount);
    }





    //////////this is what I added because it was needed in this class
    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.getPosition())) {
            this.resourceCount += 1;


            ((Plant)target).setHealth(((Plant)target).getHealth() - 1);

            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }






    //////////this is what I added because it was needed in this class
    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.resourceCount >= this.resourceLimit) {
            DudeFull dude = this.position.createDudeFull(this.getId(), this.actionPeriod,
                    this.animationPeriod, this.resourceLimit, this.images);

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            dude.scheduleActions(scheduler, world, imageStore);


            // for some reason these return boolean were switched
            return true;
        }
        String tile = world.getBackgroundCell(this.position).getId();
        if (Objects.equals(tile, "nether")){
            WarpedDude warpedDude = this.position.createWarpedDude("catcher_" + this.getId(), this.actionPeriod,
                    this.animationPeriod, imageStore.getImageList("catcher"));
            world.removeEntity(scheduler, this);
            world.addEntity(warpedDude);
            warpedDude.scheduleActions(scheduler, world, imageStore);
            return true;
        }

        return false;
    }











    // COME BACK TO THISSSSSSSSSSSSSSSSSSSSSSS
    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(this.position, new ArrayList<>(Arrays.asList(Tree.class, Sapling.class, WarpedTree.class)));

        if (target.isEmpty() || !this.moveTo(world, target.get(), scheduler) || !this.transform(world, scheduler, imageStore)) {
            if (target.get().getHealth() <= 0) {
//                Background grass = new Background("grass", imageStore.getImageList("grass"));
                Point Pos = target.get().getPosition();
//                world.setBackgroundCell(Pos, grass);
            }
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
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




}