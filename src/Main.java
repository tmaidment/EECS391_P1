import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


public class Main {



    public static void main(String[] args){
        if(args.length == 1){
            String fileName = args[0];
            List<String> list = new ArrayList<>();

            try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {

                list = br.lines().collect(Collectors.toList());

            } catch (IOException e) {
                e.printStackTrace();
            }
            int i = 1;
            for(String s : list){
                if(!s.startsWith("//")){
                    System.out.println(" ---- START TEST #" + i + " ---- ");
                    String[] values = s.split(",");
                    try {
                        execute(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]), new EightPuzzle("b12 345 678".split(" ")));
                    } catch (EightPuzzle.NodeLimitReached nodeLimitReached) {
                        nodeLimitReached.printStackTrace();
                    }
                    System.out.println(" ---- END TEST #" + i + " ---- \n\n");
                    i++;
                } else {
                    System.out.println("$ "+ s.replace("//", "").toUpperCase());
                }
            }
        } else {
            System.out.println(" ---- START TEST with INPUT ---- ");
            try {
                execute(0, 10000, 200, new EightPuzzle(args));
            } catch (EightPuzzle.NodeLimitReached nodeLimitReached) {
                nodeLimitReached.printStackTrace();
            }
            System.out.println(" ---- END TEST # ---- \n\n");
        }

        //kvalueComparison();
    }

    public static void execute(int random, int maxNodes, int k, EightPuzzle eightPuzzle2) throws EightPuzzle.NodeLimitReached{
        System.out.println("Rand:" + random + " | maxNodes: " + maxNodes + " | k: " + k);

        EightPuzzle goal = new EightPuzzle("b12 345 678".split(" "));
        EightPuzzle eightPuzzle = EightPuzzle.randomizeState(eightPuzzle2, random);
        System.out.println(">> INITIAL");
        eightPuzzle.printState();
        System.out.println(">> GOAL");
        goal.printState();

        EightPuzzle.setMaxNodes(maxNodes);

        AStar aStar = new AStar(Heuristic.Type.H1, eightPuzzle, goal);
        AStar aStar2 = new AStar(Heuristic.Type.H2, eightPuzzle, goal);

        Beam beam = new Beam(Heuristic.Type.H1, eightPuzzle, goal, k);
        Beam beam2 = new Beam(Heuristic.Type.H2, eightPuzzle, goal, k);

        System.out.println(">> COMPLETE");

    }


    //The following functions were used for retrieving statistics, and are not used during execution

    public static void maxNodeStatistics(){
        EightPuzzle goal = new EightPuzzle("b12 345 678".split(" "));
        EightPuzzle eightPuzzle;
        int iaStar;
        int tests = 100;
        for (int i = 0; i < 1000; i++) {
            EightPuzzle.setMaxNodes(i);
            iaStar = 0;
            for (int j = 0; j < tests; j++) {
                try {
                    eightPuzzle = EightPuzzle.randomizeState(goal, 50);
                    //AStar aStar2 = new AStar(Heuristic.Type.H2, eightPuzzle, goal);
                    AStar aStar2 = new AStar(Heuristic.Type.H1, eightPuzzle, goal);
                } catch (EightPuzzle.NodeLimitReached nodeLimitReached) {
                    iaStar++;
                }
            }
            System.out.println((float) iaStar/tests);
        }
    }

    public static void AstarComparison(){
        EightPuzzle goal = new EightPuzzle("b12 345 678".split(" "));
        EightPuzzle eightPuzzle;

        for (int i = 0; i < 1000; i++) {
            eightPuzzle = EightPuzzle.randomizeState(goal, 50);
            Instant start = Instant.now();
            try {
                AStar aStar2 = new AStar(Heuristic.Type.H1, eightPuzzle, goal);
                //AStar aStar2 = new AStar(Heuristic.Type.H2, eightPuzzle, goal);
            } catch (EightPuzzle.NodeLimitReached nodeLimitReached) {
                nodeLimitReached.printStackTrace();
            }
            Instant end = Instant.now();
            System.out.println(Duration.between(start, end).toString().replace("PT","").replace("S",""));
        }
    }

/*public static void calcSearchCost(){
    EightPuzzle goal = new EightPuzzle("b12 345 678".split(" "));
    EightPuzzle eightPuzzle;

    for (int i = 0; i < 100; i++) {
        eightPuzzle = EightPuzzle.randomizeState(goal, 50);
        try {
            AStar aStar2 = new AStar(Heuristic.Type.H2, eightPuzzle, goal);
            //AStar aStar2 = new AStar(Heuristic.Type.H2, eightPuzzle, goal);
        } catch (EightPuzzle.NodeLimitReached nodeLimitReached) {
            nodeLimitReached.printStackTrace();
        }
    }

    for(Map.Entry<Integer, List<Integer>> entry : AStar.searchCost.entrySet()) {
        Integer key = entry.getKey();
        List<Integer> value = entry.getValue();
        int i = 0;
        for(Integer l : value){
            i += l.intValue();
        }
        i = i / value.size();
        System.out.println(key.toString() + " " + i);
    }

}*/

    public static void kvalueComparison(){
        EightPuzzle goal = new EightPuzzle("b12 345 678".split(" "));
        EightPuzzle eightPuzzle;

        for (int i = 10; i < 1000; i++) {
            eightPuzzle = EightPuzzle.randomizeState(goal, 20);
            try {
                //AStar aStar2 = new AStar(Heuristic.Type.H1, eightPuzzle, goal);
                //AStar aStar2 = new AStar(Heuristic.Type.H2, eightPuzzle, goal);
                Beam beam2 = new Beam(Heuristic.Type.H2, eightPuzzle, goal, i);
            } catch (EightPuzzle.NodeLimitReached nodeLimitReached) {
                nodeLimitReached.printStackTrace();
            }

        }
    }
}
