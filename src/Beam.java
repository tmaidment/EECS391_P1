import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Beam {

    private BeamSlave winner;
    private int k;
    private boolean complete;
    private final Heuristic.Type heuristic;
    private final EightPuzzle goal;
    private List<BeamSlave> slaveHashMap;

    public Beam(Heuristic.Type heuristic, EightPuzzle eightPuzzle, EightPuzzle goal, int k) throws EightPuzzle.NodeLimitReached {
        this.goal = goal;
        this.heuristic = heuristic;
        this.k = k;

        //due to access errors, use a write protected array
        slaveHashMap = new CopyOnWriteArrayList<>();

        //init the first state. it will make new states
        new BeamSlave(eightPuzzle, new Node(null, EightPuzzle.Move.INITIAL));

        while (!complete) {
            for (BeamSlave beam: slaveHashMap) {
                beam.executeIteration();
                if(slaveHashMap.size() > EightPuzzle.getMaxNodes()){
                        throw new EightPuzzle.NodeLimitReached();
                }
            }
        }
        System.out.print(winner.printMoves(winner.node));

    }

    class BeamSlave {
        final Node node;
        final EightPuzzle current;

        public BeamSlave(EightPuzzle current, Node node) {
            slaveHashMap.add(this);
            this.current = current;
            this.node = node;
            if (Heuristic.calculateHeuristic(heuristic, current, goal) == 0) {
                complete = true;
                winner = this;
            }
        }

        public void executeIteration() {
            boolean executedSlave = false;
            int totalDirection = 0;
            int badDirection = 0;
            //get all successor moves
            ArrayList<EightPuzzle> moves = current.listMoves();
            //randomize it so that we dont have any loops
            Collections.shuffle(moves, EightPuzzle.random);
            for (EightPuzzle e : moves) {
                if (e.getMove() != EightPuzzle.Move.INVALID) {
                    totalDirection++;
                    //if its a good move, do it!
                    if (Heuristic.calculateHeuristic(heuristic, e, goal)
                            < Heuristic.calculateHeuristic(heuristic, current, goal)) {
                        if (!executedSlave) {
                            //remove the old object and re-initialize it.  this is important due to the logic in the constructor. this isn't efficient.
                            slaveHashMap.remove(this);
                            new BeamSlave(e, new Node(node, e.getMove()));
                            executedSlave = true;
                        } else {
                            if(slaveHashMap.size() <= k) {
                                new BeamSlave(e, new Node(node, e.getMove()));
                            }
                        }
                    } else {
                        //if its bad, but theres space to add it, try it anyway.  this will get us out of local maxes and short plateaus.
                        badDirection++;
                        if(slaveHashMap.size() <= k){
                            new BeamSlave(e, new Node(node, e.getMove()));
                        }
                    }
                }
            }
            if (badDirection == totalDirection) {
                //but if we're really stuck, try elsewhere
                slaveHashMap.remove(this);
            }
        }

        private String printMoves(Node tempNode){
            StringBuilder stringBuilder = new StringBuilder();
            int i = 0;
            while(tempNode.parent != null){
                i++;
                stringBuilder.insert(0, tempNode.getMove().toString() + " ");
                tempNode = tempNode.parent;
            }
            stringBuilder.insert(0, "BEAM || " + heuristic.toString() + " (" + i + ") steps = ");
            //return stringBuilder.append("\n").toString();
            return i + "\n";
        }

    }

    class Node {

        private final EightPuzzle.Move move;
        private Node parent;

        public Node(Node parent, EightPuzzle.Move move) {
            this.parent = parent;
            this.move = move;
        }

        public EightPuzzle.Move getMove() {
            return move;
        }

    }
}

