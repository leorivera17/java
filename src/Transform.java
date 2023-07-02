public interface Transform extends Entity{
    boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore);
}
