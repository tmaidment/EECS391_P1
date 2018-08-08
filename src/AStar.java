import java.util.*;

public class AStar {

    private final Heuristic.Type heuristic;
    private EightPuzzle current;
    private final EightPuzzle goal;

    PriorityQueue<Node> priorityQueue;

    //public int nodeCount;
    //public int moves;

    //public static Map<Integer, List<Integer>> searchCost = new HashMap<>();

    public AStar(Heuristic.Type heuristic, EightPuzzle initial, EightPuzzle goal) throws EightPuzzle.NodeLimitReached {
        priorityQueue = new PriorityQueue<>(new Node());
        this.heuristic = heuristic;
        this.current = initial;
        this.goal = goal;

        //add the starting state
        priorityQueue.add(new Node(null, initial, 0, Heuristic.calculateHeuristic(heuristic, initial, goal)));

        while(!priorityQueue.isEmpty()){
            Node current = priorityQueue.remove();

            if(current.eightPuzzle.equals(goal)) {
                System.out.println(printMoves(current));
                //add(moves, nodeCount, searchCost);
                break;
            }

            for (EightPuzzle e : current.getEightPuzzle().listMoves()) {
                if(e.getMove() != EightPuzzle.Move.INVALID){
                    //add all of them to the queue and repeat until goal state is reached
                    Node node = new Node(current, e, current.getPath_cost() + 1, Heuristic.calculateHeuristic(heuristic, e, goal));
                    if(!priorityQueue.contains(node)) {
                        priorityQueue.add(node);
                    }

                    if(priorityQueue.size() > EightPuzzle.getMaxNodes()){
                        throw new EightPuzzle.NodeLimitReached();
                    }
                }
            }
        }
    }

    //from stackoverflow
    public static <K, V> void add(final K key, final V value, final Map<K, List<V>> map)
    {
        if (map.get(key) == null) {
            map.put(key, new ArrayList<V>());
        }

        map.get(key).add(value);
    }



    private String printMoves(Node node){
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;

        while(node.parent != null){
            i++;
            stringBuilder.insert(0, node.getEightPuzzle().getMove().toString() + " ");
            node = node.parent;
        }
        stringBuilder.insert(0, "A* || " + heuristic.toString() + " (" + i + ") steps = ");
        //moves = i;
        return stringBuilder.toString();
    }

    class Node implements Comparator<Node>{

        private Node parent;

        private final EightPuzzle eightPuzzle;

        private final int path_cost, heuristic_cost;

        public Node () {

            eightPuzzle = null;
            path_cost = 0;
            heuristic_cost = 0;
        }

        //TODO: fill in these functions
        public Node (Node parent, EightPuzzle eightPuzzle, int path_cost, int heuristic_cost){
            //nodeCount++;
            this.path_cost = path_cost;
            this.heuristic_cost = heuristic_cost;
            this.eightPuzzle = eightPuzzle;
            this.parent = parent;
        }

        public EightPuzzle getEightPuzzle() {
            return eightPuzzle;
        }

        public int getPath_cost() {
            return path_cost;
        }

        public int getHeuristic_cost() {
            return heuristic_cost;
        }

        @Override
        public int compare(Node o1, Node o2) {
            return (o1.getPath_cost() + o1.getHeuristic_cost()) - (o2.getPath_cost() + o2.getHeuristic_cost());
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            if (getPath_cost() != node.getPath_cost()) return false;
            return getEightPuzzle().equals(node.getEightPuzzle());
        }

        @Override
        public int hashCode() {
            int result = getEightPuzzle().hashCode();
            result = 31 * result + getPath_cost();
            return result;
        }
    }
}
