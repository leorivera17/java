import processing.core.PImage;

import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel {


    private static final int TREE_ACTION_PERIOD = 1;
    private static final int TREE_NUM_PROPERTIES = 3;
    public static final int TREE_HEALTH = 2;
    private static final int TREE_ANIMATION_PERIOD = 0;


    private static final int HOUSE_NUM_PROPERTIES = 0;
    private static final String HOUSE_KEY = "house";




    private static final int DUDE_NUM_PROPERTIES = 3;
    private static final int DUDE_LIMIT = 2;
    private static final int DUDE_ANIMATION_PERIOD = 1;
    private static final int DUDE_ACTION_PERIOD = 0;
    private static final String DUDE_KEY = "dude";

    private static final String WARPED_DUDE_KEY = "catcher";


    private static final int OBSTACLE_NUM_PROPERTIES = 1;
    private static final int OBSTACLE_ANIMATION_PERIOD = 0;
    private static final String OBSTACLE_KEY = "obstacle";



    private static final int SAPLING_NUM_PROPERTIES = 1;
    private static final int SAPLING_HEALTH = 0;
    private static final int STUMP_NUM_PROPERTIES = 0;
    private static final int ENTITY_NUM_PROPERTIES = 4;



    private static final int PROPERTY_ROW = 3;
    private static final int PROPERTY_COL = 2;
    private static final int PROPERTY_ID = 1;
    private static final int PROPERTY_KEY = 0;
    private int numRows;





    private static final String FAIRY_KEY = "fairy";

    private static final int FAIRY_NUM_PROPERTIES = 2;

    private static final int FAIRY_ACTION_PERIOD = 1;

    private static final int FAIRY_ANIMATION_PERIOD = 0;





    public static final String SPIDER_KEY = "spider";

    private static final int SPIDER_NUM_PROPERTIES = 2;

    private static final int SPIDER_ACTION_PERIOD = 1;

    private static final int SPIDER_ANIMATION_PERIOD = 0;



    private static final String BABY_SPIDER_KEY = "babyspider";
    private static final int BABY_SPIDER_NUM_PROPERTIES = 2;
    private static final int BABY_SPIDER_ACTION_PERIOD = 0;
    private static final int BABY_SPIDER_ANIMATION_PERIOD = 1;

    private static final String WARPED_TREE_KEY = "warpedtree";
    private static final int WARPED_TREE_NUM_PROPERTIES = 2;
    private static final int WARPED_TREE_ACTION_PERIOD = 1;
    private static final int WARPED_TREE_ANIMATION_PERIOD = 0;

    public int numRows(){
        return numRows;
    }

    private int numCols;

    public int numCols(){
        return numCols;
    }

    private Background[][] background;

    private Entity[][] occupancy;


    private Set<Entity> entities;

    public Set<Entity> getEntities() {
        return entities;
    }

    public WorldModel() {

    }

    public Optional<PImage> getBackgroundImage(Point pos) {
        if (withinBounds(pos)) {
            return Optional.of(Functions.getCurrentImage(getBackgroundCell(pos)));
        } else {
            return Optional.empty();
        }
    }

    public void setBackgroundCell(Point pos, Background background) {
        this.background[pos.getY()][pos.getX()] = background;
    }

    public Background getBackgroundCell(Point pos) {
        return this.background[pos.getY()][pos.getX()];
    }

    private void parseEntity(String line, ImageStore imageStore) {
        String[] properties = line.split(" ", ENTITY_NUM_PROPERTIES + 1);
        if (properties.length >= ENTITY_NUM_PROPERTIES) {
            String key = properties[PROPERTY_KEY];
            String id = properties[PROPERTY_ID];
            Point pt = new Point(Integer.parseInt(properties[PROPERTY_COL]), Integer.parseInt(properties[PROPERTY_ROW]));

            properties = properties.length == ENTITY_NUM_PROPERTIES ?
                    new String[0] : properties[ENTITY_NUM_PROPERTIES].split(" ");

            switch (key) {
                case BABY_SPIDER_KEY -> parseBabySpider(properties, pt, id, imageStore);
                case OBSTACLE_KEY -> parseObstacle(properties, pt, id, imageStore);
                case DUDE_KEY -> parseDude(properties, pt, id, imageStore);
                case WARPED_DUDE_KEY -> parseWarpedDude(properties, pt, id, imageStore);
                case FAIRY_KEY -> parseFairy(properties, pt, id, imageStore);
                case HOUSE_KEY -> parseHouse(properties, pt, id, imageStore);
                case SPIDER_KEY -> parseSpider(properties, pt, id, imageStore);
                case WARPED_TREE_KEY -> parseWarpedTree(properties, pt, id, imageStore);
                case Functions.TREE_KEY -> parseTree(properties, pt, id, imageStore);
                case Functions.SAPLING_KEY -> parseSapling(properties, pt, id, imageStore);
                case Functions.STUMP_KEY -> parseStump(properties, pt, id, imageStore);
                default -> throw new IllegalArgumentException("Entity key is unknown");
            }
        }else{
            throw new IllegalArgumentException("Entity must be formatted as [key] [id] [x] [y] ...");
        }
    }


    private void parseBackgroundRow(String line, int row, ImageStore imageStore) {
        String[] cells = line.split(" ");
        if(row < this.numRows){
            int rows = Math.min(cells.length, this.numCols);
            for (int col = 0; col < rows; col++){
                this.background[row][col] = new Background(cells[col], imageStore.getImageList(cells[col]));
            }
        }
    }

    private void parseSaveFile(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        String lastHeader = "";
        int headerLine = 0;
        int lineCounter = 0;
        while(saveFile.hasNextLine()){
            lineCounter++;
            String line = saveFile.nextLine().strip();
            if(line.endsWith(":")){
                headerLine = lineCounter;
                lastHeader = line;
                switch (line){
                    case "Backgrounds:" -> this.background = new Background[this.numRows][this.numCols];
                    case "Entities:" -> {
                        this.occupancy = new Entity[this.numRows][this.numCols];
                        this.entities = new HashSet<>();
                    }
                }
            }else{
                switch (lastHeader){
                    case "Rows:" -> this.numRows = Integer.parseInt(line);
                    case "Cols:" -> this.numCols = Integer.parseInt(line);
                    case "Backgrounds:" -> this.parseBackgroundRow(line, lineCounter-headerLine-1, imageStore);
                    case "Entities:" -> this.parseEntity(line, imageStore);
                }
            }
        }
    }

    public void load(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        this.parseSaveFile(saveFile, imageStore, defaultBackground);
        if(this.background == null){
            this.background = new Background[this.numRows][this.numCols];
            for (Background[] row : this.background)
                Arrays.fill(row, defaultBackground);
        }
        if(this.occupancy == null){
            this.occupancy = new Entity[this.numRows][this.numCols];
            this.entities = new HashSet<>();
        }
    }

    private void setOccupancyCell(Point pos, Entity entity) {
        this.occupancy[pos.getY()][pos.getX()] = entity;
    }

    public Entity getOccupancyCell(Point pos) {
        return this.occupancy[pos.getY()][pos.getX()];
    }

    public Optional<Entity> getOccupant(Point pos) {
        if (isOccupied(pos)) {
            return Optional.of(this.getOccupancyCell(pos));
        } else {
            return Optional.empty();
        }
    }

    private void removeEntityAt(Point pos) {
        if (withinBounds(pos) && this.getOccupancyCell(pos) != null) {
            Entity entity = this.getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */

            entity.setPosition(new Point(-1,1));
            this.entities.remove(entity);
            this.setOccupancyCell(pos, null);
        }
    }

    public void removeEntity(EventScheduler scheduler, Entity entity) {
        scheduler.unscheduleAllEvents(entity);
        this.removeEntityAt(entity.getPosition());
    }

    public void moveEntity(EventScheduler scheduler, Entity entity, Point pos) {
        Point oldPos = entity.getPosition();
        if (withinBounds(pos) && !pos.equals(oldPos)) {
            this.setOccupancyCell(oldPos, null);
            Optional<Entity> occupant = this.getOccupant(pos);
            occupant.ifPresent(target -> this.removeEntity(scheduler, target));
            this.setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }

    /*
           Assumes that there is no entity currently occupying the
           intended destination cell.
        */
    public void addEntity(Entity entity) {
        if (withinBounds(entity.getPosition())) {
            this.setOccupancyCell(entity.getPosition(), entity);
            this.entities.add(entity);
        }
    }




    // I change this make sure you know
    public Optional<Entity> findNearest(Point pos, List<Class> kinds) {
        List<Entity> ofType = new LinkedList<>();
        for (Class kind : kinds) {
            for (Entity entity : this.entities) {
                if (entity.getClass() == kind) {
                    ofType.add(entity);
                }
            }
        }

        return pos.nearestEntity(ofType);
    }

    public boolean isOccupied(Point pos) {
        return withinBounds(pos) && this.getOccupancyCell(pos) != null;
    }

    public boolean withinBounds(Point pos) {
        return pos.getY() >= 0 && pos.getY() < this.numRows && pos.getX() >= 0 && pos.getX() < this.numCols;
    }

    public void tryAddEntity(Entity entity) {
        if (this.isOccupied(entity.getPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        this.addEntity(entity);
    }
    private void parseBabySpider(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == BABY_SPIDER_NUM_PROPERTIES) {
            Entity entity = pt.createBabySpider(id, Integer.parseInt(properties[BABY_SPIDER_ACTION_PERIOD]), Integer.parseInt(properties[BABY_SPIDER_ANIMATION_PERIOD]), imageStore.getImageList(BABY_SPIDER_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", BABY_SPIDER_KEY, BABY_SPIDER_NUM_PROPERTIES));
        }
    }
    private void parseWarpedTree(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == WARPED_TREE_NUM_PROPERTIES) {
            Entity entity = pt.createWarpedTree(id,Double.parseDouble(properties[TREE_ACTION_PERIOD]), Double.parseDouble(properties[TREE_ANIMATION_PERIOD]), Integer.parseInt(properties[TREE_HEALTH]), imageStore.getImageList(WARPED_TREE_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", WARPED_TREE_KEY, WARPED_TREE_NUM_PROPERTIES));
        }
    }

    private void parseStump(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == STUMP_NUM_PROPERTIES) {
            Entity entity = pt.createStump(id, imageStore.getImageList(Functions.STUMP_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.STUMP_KEY, STUMP_NUM_PROPERTIES));
        }
    }

    private void parseHouse(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == HOUSE_NUM_PROPERTIES) {
            Entity entity = pt.createHouse(id, imageStore.getImageList(HOUSE_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", HOUSE_KEY, HOUSE_NUM_PROPERTIES));
        }
    }

    private void parseObstacle(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Entity entity = pt.createObstacle(id, Double.parseDouble(properties[OBSTACLE_ANIMATION_PERIOD]), imageStore.getImageList(OBSTACLE_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", OBSTACLE_KEY, OBSTACLE_NUM_PROPERTIES));
        }
    }

    private void parseTree(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == TREE_NUM_PROPERTIES) {
            Entity entity = pt.createTree(id, Double.parseDouble(properties[TREE_ACTION_PERIOD]), Double.parseDouble(properties[TREE_ANIMATION_PERIOD]), Integer.parseInt(properties[TREE_HEALTH]), imageStore.getImageList(Functions.TREE_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.TREE_KEY, TREE_NUM_PROPERTIES));
        }
    }

    private void parseFairy(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == FAIRY_NUM_PROPERTIES) {
            Entity entity = pt.createFairy(id, Double.parseDouble(properties[FAIRY_ACTION_PERIOD]), Double.parseDouble(properties[FAIRY_ANIMATION_PERIOD]), imageStore.getImageList(FAIRY_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", FAIRY_KEY, FAIRY_NUM_PROPERTIES));
        }
    }


    private void parseSpider(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == SPIDER_NUM_PROPERTIES) {
            Entity entity = pt.createSpider(id, Double.parseDouble(properties[SPIDER_ACTION_PERIOD]), Double.parseDouble(properties[SPIDER_ANIMATION_PERIOD]), imageStore.getImageList(SPIDER_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", SPIDER_KEY, SPIDER_NUM_PROPERTIES));
        }
    }

    private void parseDude(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == DUDE_NUM_PROPERTIES) {
            Entity entity = pt.createDudeNotFull(id, Double.parseDouble(properties[DUDE_ACTION_PERIOD]), Double.parseDouble(properties[DUDE_ANIMATION_PERIOD]), Integer.parseInt(properties[DUDE_LIMIT]), imageStore.getImageList(DUDE_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", DUDE_KEY, DUDE_NUM_PROPERTIES));
        }
    }

    private void parseWarpedDude(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == DUDE_NUM_PROPERTIES) {
            Entity entity = pt.createWarpedDude(id, Double.parseDouble(properties[DUDE_ACTION_PERIOD]), Double.parseDouble(properties[DUDE_ANIMATION_PERIOD]), imageStore.getImageList(WARPED_DUDE_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", WARPED_DUDE_KEY, DUDE_NUM_PROPERTIES));
        }
    }

    private void parseSapling(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == SAPLING_NUM_PROPERTIES) {
            int health = Integer.parseInt(properties[SAPLING_HEALTH]);
            Entity entity = pt.createSapling(id, imageStore.getImageList(Functions.SAPLING_KEY), health);
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.SAPLING_KEY, SAPLING_NUM_PROPERTIES));
        }
    }






    /**
     * Helper method for testing. Don't move or modify this method.
     */
    public List<String> log(){
        List<String> list = new ArrayList<>();
        for (Entity entity : entities) {
            String log = entity.log();
            if(log != null) list.add(log);
        }
        return list;
    }
}
