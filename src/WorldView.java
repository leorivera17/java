import processing.core.PApplet;
import processing.core.PImage;

import java.util.Optional;



public final class WorldView {
    private final PApplet screen;
    private final WorldModel world;
    private final int tileWidth;
    private final int tileHeight;
    private final Viewport viewport;


    public Viewport getViewport() {
        return viewport;
    }



    public WorldView(int numRows, int numCols, PApplet screen, WorldModel world, int tileWidth, int tileHeight) {
        this.screen = screen;
        this.world = world;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.viewport = new Viewport(numRows, numCols);
    }

    private void drawBackground() {
        for (int row = 0; row < this.viewport.getNumRows(); row++) {
            for (int col = 0; col < this.viewport.getNumCols(); col++) {
                Point worldPoint = this.viewport.viewportToWorld(col, row);
                Optional<PImage> image = this.world.getBackgroundImage(worldPoint);
                if (image.isPresent()) {
                    this.screen.image(image.get(), col * this.tileWidth, row * this.tileHeight);
                }
            }
        }
    }

    private void drawEntities() {
        for (Entity entity : this.world.getEntities()) {
            Point pos = entity.getPosition();

            if (this.viewport.contains(pos)) {
                Point viewPoint = this.viewport.worldToViewport(pos.getX(), pos.getY());
                this.screen.image(Functions.getCurrentImage(entity), viewPoint.getX() * this.tileWidth, viewPoint.getY() * this.tileHeight);
            }
        }
    }

    public void drawViewport() {
        this.drawBackground();
        this.drawEntities();
    }


    private int clamp(int value, int low, int high) {
        return Math.min(high, Math.max(value, low));
    }
    public void shiftView(int colDelta, int rowDelta) {
        int newCol = clamp(this.viewport.getCol() + colDelta, 0, this.world.numCols() - this.viewport.getNumCols());
        int newRow = clamp(this.viewport.getRow() + rowDelta, 0, this.world.numRows() - this.viewport.getNumRows());

        this.viewport.shift(newCol, newRow);
    }
}
