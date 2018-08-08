import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


//EightPuzzle is the object used to represent a single state.  It contains static methods for manipulating states, as well as functions used to return successive states.
public class EightPuzzle {

    private final Move move;
    private final Location blank;
    private final int[][] state;
    public static Random random = new Random(1748L);

    private static int maxNodes = Integer.MAX_VALUE;

    public EightPuzzle(String[] args){
        state = new int[3][3];
        move = Move.INITIAL;
        char[][] chars = new char[3][3];
        for (int i = 0; i < 3; i++) {
            chars[i] = args[i].toCharArray();
        }
        //I convert to an int[][] because I don't like dealing with characters.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(chars[j][i] == 'b' || chars[j][i] == 'B'){
                    this.state[j][i] = 0;
                } else {
                    this.state[j][i] = chars[j][i] - '0';
                }
            }
        }
        blank = getBlank();
    }

    public EightPuzzle(int[][] state, Move move){
        this.state = state;
        this.move = move;
        blank = getBlank();
    }

    //In order to easily keep track of moves, each state contains the move used to reach it.
    public enum Move {
        INITIAL,
        LEFT,
        RIGHT,
        UP,
        DOWN,
        INVALID;
    }

    //These functions return states after a move is completed, or the same state with the invalid tag if the move can not be completed.
    public EightPuzzle left(){
        if(validLocation(blank.getLeft())){
            return new EightPuzzle(stateSwap(this.state, blank, blank.getLeft()), Move.LEFT);
        }
        return new EightPuzzle(state, Move.INVALID);
    }

    public EightPuzzle right(){
        if(validLocation(blank.getRight())){
            return new EightPuzzle(stateSwap(this.state, blank, blank.getRight()), Move.RIGHT);
        }
        return new EightPuzzle(state, Move.INVALID);
    }

    public EightPuzzle up(){
        if(validLocation(blank.getUp())){
            return new EightPuzzle(stateSwap(this.state, blank, blank.getUp()), Move.UP);
        }
        return new EightPuzzle(state, Move.INVALID);
    }

    public EightPuzzle down(){
        if(validLocation(blank.getDown())){
            return new EightPuzzle(stateSwap(state, blank, blank.getDown()), Move.DOWN);
        }
        return new EightPuzzle(state, Move.INVALID);
    }

    public ArrayList<EightPuzzle> listMoves(){
        ArrayList<EightPuzzle> moves = new ArrayList<>();
        moves.add(left());
        moves.add(right());
        moves.add(up());
        moves.add(down());
        return moves;
    }

    public static EightPuzzle randomizeState(EightPuzzle eightPuzzle, int n){
        if(n == 0){
            return eightPuzzle;
        }
        ArrayList<EightPuzzle> moves = eightPuzzle.listMoves();
        EightPuzzle randomMove = moves.get(random.nextInt(moves.size()));
        if(randomMove.getMove() != Move.INVALID) {
            return randomizeState(randomMove, n - 1);
        }
        return randomizeState(eightPuzzle, n);

    }

    private int[][] stateSwap(int[][] state, Location blank, Location move) {
        int[][] new_state = new int[3][3];
        for (int y = 0; y <= 2; y++) {
            for (int x = 0; x <= 2; x++) {
                new_state[y][x] = state[y][x];
            }
        }
        int value = move.getValue();
        new_state[move.getY()][move.getX()] = blank.getValue(); //always 0, but do this to ensure only legal moves can be completed.
        new_state[blank.getY()][blank.getX()] = value;
        return new_state;
    }

    public boolean validLocation(Location location){
        return location.x <= 2 && location.x >= 0
                && location.y <= 2 && location.y >= 0;
    }

    public Location getBlank(){
        for (int y = 0; y <= 2; y++) {
            for (int x = 0; x <= 2; x++) {
                if(state[y][x] == 0){
                    return new Location(y, x);
                }
            }
        }
        return null;
    }

    //Heuristic functions are below
    public int misplacedTiles(EightPuzzle eightPuzzle){
        int hvalue = 0;
        for (int y = 0; y <= 2; y++) {
            for (int x = 0; x <= 2; x++) {
                if(state[y][x] != eightPuzzle.state[y][x] && state[y][x] != 0)
                    hvalue++;
            }
        }
        return hvalue;
    }

    //This can be optimized, but it works.
    public int cumulativeDistFrom(EightPuzzle eightPuzzle){
        int hvalue = 0;
        int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        for (int i = 1; i <= 9; i++) {
            for (int y = 0; y <= 2; y++) {
                for (int x = 0; x <= 2; x++) {
                    if(eightPuzzle.state[y][x] == i){
                        x1 = x;
                        y1 = y;
                    }
                }
            }
            for (int y = 0; y <= 2; y++) {
                for (int x = 0; x <= 2; x++) {
                    if(state[y][x] == i){
                        x2 = x;
                        y2 = y;
                    }
                }
            }
            hvalue += Math.abs(x1 - x2) + Math.abs(y1 - y2);
        }
        return hvalue;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int y = 0; y <= 2; y++) {
            for (int x = 0; x <= 2; x++) {
                stringBuilder.append(state[y][x])
                        .append(" ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString().replace('0', 'b');
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EightPuzzle that = (EightPuzzle) o;

        return Arrays.deepEquals(state, that.state);
    }

    public static int getMaxNodes() {
        return maxNodes;
    }

    public static void setMaxNodes(int maxNodes) {
        EightPuzzle.maxNodes = maxNodes;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(state);
    }

    public void printState(){
        System.out.println(toString());
    }

    public Move getMove() {
        return move;
    }

    //I wrapped anything related to indices in the int[][] so I don't have to think about it.  Probably makes everything slower.
    class Location {

        private final int y, x;

        public Location(int y, int x){
            this.y = y;
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public int getX() {
            return x;
        }

        public Location getUp() {
            return new Location(y - 1, x);
        }

        public Location getDown() {
            return new Location(y + 1, x);
        }

        public Location getLeft() {
            return new Location(y,x - 1);
        }

        public Location getRight() {
            return new Location(y,x + 1);
        }

        public int getValue() {
            return state[y][x];
        }

    }

    static class NodeLimitReached extends Exception{

    }
}
