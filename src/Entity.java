import java.util.*;
import processing.core.PImage;


/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */


interface Entity {




    String getId();

    Point getPosition();

    void setPosition(Point position);

    List<PImage> getImages();

    int getImageIndex();

    void setImageIndex(int imageIndex);

    int getHealth();

    void setHealth(int health);






    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    default String log(){
        return getId().isEmpty() ? null :
                String.format("%s %d %d %d", this.getId(), this.getPosition().getX(),
                        this.getPosition().getY(), this.getImageIndex());
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
