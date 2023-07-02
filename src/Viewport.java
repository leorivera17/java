import processing.core.PApplet;
import processing.core.PImage;

import java.util.*;

public final class Viewport {
    private int row;

    public int getRow() {
        return row;
    }


    private int col;

    public int getCol() {
        return col;
    }



    private final int numRows;


    public int getNumRows() {
        return numRows;
    }



    private final int numCols;



    public int getNumCols() {
        return numCols;
    }



    public Viewport(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
    }

    public boolean contains(Point p) {
        return p.getY() >= this.row && p.getY() < this.row + this.numRows && p.getX() >= this.col && p.getX() < this.col + this.numCols;
    }

    public void shift(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public Point worldToViewport(int col, int row) {
        return new Point(col - this.col, row - this.row);
    }

    public Point viewportToWorld(int col, int row) {
        return new Point(col + this.col, row + this.row);
    }









}
