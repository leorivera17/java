import processing.core.PImage;

import java.util.List;

public class House implements Entity, Position, AnimationPeriod {



    // image index, position, id

    private final String id;

    @Override
    public String getId(){
        return id;
    }





    private Point position;

    @Override
    public Point getPosition(){
        return position;
    }

    @Override
    public void setPosition(Point position){
        this.position = position;
    }

    @Override
    public Point nextPosition(WorldModel world, Point destPos) {
        return null;
    }






    @Override
    public double getAnimationPeriod() {
        return animationPeriod;
    }




    private final List<PImage> images;

    @Override
    public List<PImage> getImages() {
        return images;
    }

    private int imageIndex;

    @Override
    public int getImageIndex(){
        return imageIndex;
    }

    @Override
    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }




    private double animationPeriod;



    private int health;

    public void setHealth(int health){
        this.health = health;
    }

    public int getHealth(){
        return health;
    }



    private int resourceCount;
    private final int resourceLimit;



    private final double actionPeriod;
    public double getActionPeriod(){
        return actionPeriod;
    }




    private final int healthLimit;
    public int getHealthLimit(){
        return healthLimit;
    }









    public House(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount,
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









}
