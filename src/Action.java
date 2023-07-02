/**
 * An TREE_ACTION_PERIODaction that can be taken by an entity
 */
public interface Action {
    default void executeAction(EventScheduler scheduler) {}
}
