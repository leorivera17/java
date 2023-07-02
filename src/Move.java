public interface Move extends Entity{
    boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler);
}
