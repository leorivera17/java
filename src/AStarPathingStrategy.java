import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy{

    public Comparator<Node> fVal = (n1, n2) -> (n1.getF() - n2.getF());
    public PriorityQueue<Node> openQ = new PriorityQueue<>(fVal);
    public HashMap<Point, Node> openMap = new HashMap<>();
    public HashMap<Point, Node> closeMap = new HashMap<>();



    @Override
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors){


        List<Point> path = new LinkedList<>();
        openQ = new PriorityQueue<>(fVal);
        openMap = new HashMap<>();
        closeMap = new HashMap<>();


        Node current = new Node(start, null, 0, Node.computeH(start, end));
        openQ.add(current);


        while(!withinReach.test(current.getPosition(), end) && !openQ.isEmpty()){
            current = openQ.poll();
            closeMap.put(current.getPosition(), current);
            openMap.remove(current.getPosition());
            addAjacent(current, end, canPassThrough, potentialNeighbors);
        }




        if (openQ.isEmpty()){
            return path;
        }




        while(current.getPrevious() != null){
            path.add(current.getPosition());
            current = closeMap.get(current.getPrevious());
        }



        List<Point> answer = new LinkedList<Point>();



        int temporary = path.size();



        for (int x = 0; x < temporary; x++){
            answer.add(path.remove(temporary - x -1));
        }



        return answer;
    }


    private void addAjacent(Node current, Point end,
                            Predicate<Point> canPassThrough,
                            Function<Point, Stream<Point>> potentialNeighbors){
        Consumer<Point> addPoints = point -> {
            if (openMap.containsKey(point)){
                if(openMap.get(point).getG() > current.getG() + 1){
                    openMap.get(point).setG(current.getG() + 1);
                }
            }
            else if (!closeMap.containsKey(point) && canPassThrough.test(point)){
                Node recent = new Node(point, current.getPosition(), current.getG() + 1, Node.computeH(point, end));
                openQ.add(recent);
                openMap.put(point, recent);
            }
        };

        Point nextOne = current.getPosition();

        potentialNeighbors.apply(nextOne).forEach(addPoints);






    }
}
