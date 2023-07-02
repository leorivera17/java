public interface AnimationPeriod extends Entity{
    double getAnimationPeriod();



    default void nextImage() {
        this.setImageIndex(getImageIndex() + 1);
    }







//    obstable needs to be finished!!!!!!!!!



//    public double getAnimationPeriod() {
//        switch (kind) {
//            case DUDE_FULL:
//            case DUDE_NOT_FULL:
//            case OBSTACLE:
//            case FAIRY:
//            case SAPLING:
//            case TREE:
//                return animationPeriod;
//            default:
//                throw new UnsupportedOperationException(String.format("getAnimationPeriod not supported for %s", kind));
//        }
//    }

}
