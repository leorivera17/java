public class Node {
    private Point position;

    public Point getPosition() {
        return position;
    }




    private Point previous;

    public Point getPrevious(){
        return previous;
    }



    private int f;

    public int getF() {
        return f;
    }





    private int g;

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
        this.f = g + h;
    }





    private int h;

    public int getH() {
        return h;
    }





    public Node (Point position, Point previous, int g, int h){
        this.position = position;
        this.previous = previous;
        this.g = g;
        this.h = h;
        this.f = g + h;
    }



    public static int computeH(Point p1, Point p2){
        int answer = Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
        return answer;
    }


}
