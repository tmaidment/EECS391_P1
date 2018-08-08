public class Heuristic {

    public enum Type{
        H1,
        H2;
    }

    public static int calculateHeuristic(Type heuristic, EightPuzzle current, EightPuzzle goal){
        switch (heuristic) {
            case H1:
                return current.misplacedTiles(goal);
            case H2:
                return current.cumulativeDistFrom(goal);
            default:
                throw new UnsupportedOperationException();
        }
    }
}
