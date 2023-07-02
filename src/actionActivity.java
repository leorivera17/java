public class actionActivity implements Action {

    private final Execute entity;
    private final WorldModel world;
    private final ImageStore imageStore;


    public actionActivity(Execute entity, WorldModel world, ImageStore imageStore) {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }


    @Override
    public void executeAction(EventScheduler scheduler){
        entity.executeActivity(this.world, this.imageStore, scheduler);
    }

}
