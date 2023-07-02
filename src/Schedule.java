public interface Schedule extends Entity{
    void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);
}
