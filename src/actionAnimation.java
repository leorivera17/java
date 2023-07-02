public class actionAnimation implements Action{


    private final AnimationPeriod entity;
    private final int repeatCount;


    public actionAnimation(AnimationPeriod entity, int repeatCount) {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }




    ///MAYBE IT MIGHT WORK




    public Action createAnimationAction(int repeatCount) {
        return new actionAnimation(entity, repeatCount);
    }



    // it might be this which is the problem
    @Override
    public void executeAction(EventScheduler scheduler) {
        entity.nextImage();
        if (repeatCount != 1) {
            scheduler.scheduleEvent(entity, this.createAnimationAction(Math.max(repeatCount - 1, 0)), entity.getAnimationPeriod());
        }
    }





}
