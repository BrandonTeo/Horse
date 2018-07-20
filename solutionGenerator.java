import java.io.*;
import java.lang.Integer;
import java.util.*;

//This program generates a solution for all the .in files given into one .out file
public class solutionGenerator {

    //This is a Horse object class
    public static class Horse {
        private int horseID;
        private int horseRating;
        private int numFriends;

        public Horse(int i, int j) {
            this.horseID = i;
            this.horseRating = j;
        }

        public int getHorseID() {
            return this.horseID;
        }

        public int getHorseRating() {
            return this.horseRating;
        }

        public void setNumFriends(int i) {
            this.numFriends = i;
        }

        public int getNumFriends() {
            return this.numFriends;
        }

        @Override
        public Horse clone() {
            Horse h = new Horse(this.horseID,this.horseRating);
            return h;
        }
    }

    //Comparator used to compare Horse objects based on performance rating
    public static class HorseComparator implements Comparator<Horse> {
        public int compare(Horse h1, Horse h2) {
            return h1.getHorseRating() - h2.getHorseRating();
        }
    }

    //Pair object to wrap the return string and the score of the assignment
    public static class Pair {
        private String wholeString;
        private int score;
        private int numPaths;

        public Pair(String s, int i, int j) {
            this.wholeString = s;
            this.score = i;
            this.numPaths = j;

        }

        public String getString() {
            return this.wholeString;
        }

        public int getScore() {
            return this.score;
        }

        public int getNumPaths() {
            return this.numPaths;
        }
    }

    //Pair comparator to compare Pair objects based on the score of the assignment
    public static class PairComparator implements Comparator<Pair> {
        public int compare(Pair p1, Pair p2) {
            return p1.getScore() - p2.getScore();
        }
    }


    //This solver randomly picks the first node and then greedily picks the next one if applicable
    public static Pair greedySolver(String filename) {
        try {
            //This block preps a reader to read in filename.in
            BufferedReader r = new BufferedReader(new FileReader(filename));
            String s = r.readLine();
            int numHorses = Integer.parseInt(s);

            //We can fill everything from the .in file into matrix
            int matrix[][] = new int[numHorses][numHorses];
            for(int j = 0; j < numHorses; j++) {
                s = r.readLine();
                String[] s2 = s.split(" ");
                for(int k = 0; k < numHorses; k++){
                    matrix[j][k] = Integer.parseInt(s2[k]);
                }
            }


            //We create a graph of horses, the performance index list and a set of all horses using the data we have in matrix
            //graph is our representation of the horses and their relationships with other horses
            //pIndex holds the performance index of each horse
            //horseList keeps a intact list of all the REAL horse objects that are in the graphs (don't modify it)
            //shuffledHorseList is a shuffled version of horseList that helps us randomly pick vertices
            HashMap<Integer, HashSet<Horse>> graph = new HashMap<Integer, HashSet<Horse>>();
            HashMap<Integer, Integer> pIndex = new HashMap<Integer, Integer>();
            ArrayList<Horse> horseList = new ArrayList<Horse>(); //For reference
            ArrayList<Horse> shuffledHorseList = new ArrayList<Horse>(); //For doing actual work

            //In one for loop, we can populate pIndex and horseList using matrix
            for(int j = 0; j < numHorses; j++){
                pIndex.put(j,matrix[j][j]);
                horseList.add(j,new Horse(j,pIndex.get(j)));
            }

            //Here, we clone the EXACT Horse objects from horseList into a new list and shuffle it
            for(Horse h : horseList){
                shuffledHorseList.add(h.clone());
            }
            Collections.shuffle(shuffledHorseList);


            //This for loop initializes the graph by placing vertices without edges
            for(int i = 0; i < numHorses; i++) {
                graph.put(i,new HashSet<Horse>());
            }

            //This double for loop loops through matrix to fill in the edges of the graph
            //Note: we fill in the graph with the Horse objects from horseList
            for(int j = 0; j < numHorses; j++){
                for(int k = 0; k < numHorses; k++) {
                    if (matrix[j][k] == 1 && j!=k) {
                        HashSet<Horse> adjacentHorses = graph.get(j);
                        adjacentHorses.add(horseList.get(k));
                        graph.put(j,adjacentHorses);
                    }
                }
            }

            //This string is used to store the output for this .in
            String toReturn = "";
            //int counter = 0; //Uncomment to debug; To check if we used all horses


            //This arraylist of arraylists stores the paths that we obtain
            ArrayList<ArrayList<Integer>> allPaths = new ArrayList<ArrayList<Integer>>();
            //We use this keepTrackSet as a safety check to keep track of which horses still haven't been assigned to a team
            HashSet<Integer> keepTrackSet = new HashSet<Integer>();
            for (int i = 0; i < numHorses; i++) { //We only store their horseIDs
                keepTrackSet.add(i);
            }


            //Initializations
            Horse removedHorse;
            ArrayList<Integer> currPath;
            Comparator<Horse> comp = new HorseComparator();


            //We keep on repeating this process until all the horses have been assigned teams
            while (keepTrackSet.size() != 0) {

                //This while(true) loops makes sure that we get a horse from shuffledHorseList that is TRULY still in the graph based on keepTrackSet
                while (true) {
                    //Remove a horse
                    removedHorse = shuffledHorseList.remove(0);
                    //counter++; //Uncomment to debug; To check if we used all horses
                    if (keepTrackSet.contains(removedHorse.getHorseID())) {
                        //If we enter here it means the removed horse can be removed
                        keepTrackSet.remove(removedHorse.getHorseID());
                        break;
                    } else {
                        //If we enter here it means that the removed horse is not there in the first place
                        //So we have to re-loop to pick another
                        //counter--; //Uncomment to debug; To check if we used all horses
                        continue;
                    }
                }

                //Start removal process on the graph
                //First we remove all edges to the removedHorse
                //We need an iterator through all the horses
                Iterator<Integer> horseIterator = graph.keySet().iterator();
                while (horseIterator.hasNext()) {
                    int currHorseID = horseIterator.next();

                    //Here we used a trick to check if the adjacentHorseList contains removedHorse
                    //We use the "immutable" horseList to get the REAL Horse objects
                    if (graph.get(currHorseID).contains(horseList.get(removedHorse.getHorseID()))) {
                        HashSet<Horse> adjacentHorseSet = graph.get(currHorseID);
                        adjacentHorseSet.remove(horseList.get(removedHorse.getHorseID()));
                        //System.out.println("Removed the edge:"+currHorseID+"->"+removedHorse.getHorseID()); //Uncomment to debug; Prints out which edge is removed
                        graph.put(currHorseID, adjacentHorseSet);
                    }
                }
                //Finished removal of edges to removedHorse

                //Initialize an empty team and add this first removedHorse to the team
                currPath = new ArrayList<Integer>();
                currPath.add(removedHorse.getHorseID());

                //This while(true) loop will DFS through the horses until we reach a horse that has no friends
                //In this solver we pick the adjacent horse with best performance rating
                while(true) {
                    //System.out.print("Neighbors of:" + removedHorse.getHorseID()+"--->"); //Uncomment to debug; To list out the adjacent horses
                    HashSet<Horse> adjacentHorseSet = graph.get(removedHorse.getHorseID());
    
                    //This case means that the current horse has no friends so we have to end the path add it to allPaths and renew the path
                    //Then, we jump out of the while(true) loop to get the next new first vertex
                    if (adjacentHorseSet.size() == 0) {
                        allPaths.add(currPath);
                        currPath = new ArrayList<Integer>();
                        graph.remove(removedHorse.getHorseID());
                        break;
                    } else { //In this case, we pick the adjacent horse with best performance rating
                        graph.remove(removedHorse.getHorseID());
                        //Pick the best horse here
                        Horse maxHorse = Collections.max(adjacentHorseSet,comp);
                        //System.out.println("The chosen one is:" + maxHorse.getHorseID()); //Uncomment to debug; Prints out the horse that is chosen, i.e. the one with best performance rating
                        //counter++; //Uncomment to debug; Used to check if we used all the horses
                        removedHorse = maxHorse; //Treat the best horse as the current horse that is to be removed
                        currPath.add(removedHorse.getHorseID());
                        keepTrackSet.remove(removedHorse.getHorseID());
                        //Start Removal process of removedHorse from graph, same deal as before
                        Iterator<Integer> horseIterator1 = graph.keySet().iterator();
                        while (horseIterator1.hasNext()) {
                            int currHorseID = horseIterator1.next();
                            if (graph.get(currHorseID).contains(horseList.get(removedHorse.getHorseID()))) {
                                HashSet<Horse> adjacentHorseSet1 = graph.get(currHorseID);
                                adjacentHorseSet1.remove(horseList.get(removedHorse.getHorseID()));
                                //System.out.println("Removed the edge:"+currHorseID+"->"+removedHorse.getHorseID()); //Uncomment to debug; Prints out which edge is removed
                                graph.put(currHorseID, adjacentHorseSet1);
                            }
                        }
                        //End Removal of removedHorse from graph

                        //Re-loop to continue the DFS for next horse
                        continue;
                    }
                }

            }

            //System.out.println("did we use all horses?:" + (counter == numHorses)); //Uncomment to debug; Used to check if all horses are used
            //System.out.println("totalpaths:"+allPaths.size()); //Uncomment to debug; Used to check how many paths there are in our assignment

            //Iterator to give all the paths that we found
            Iterator<ArrayList<Integer>> allPathsIterator = allPaths.iterator();
            int totalScore = 0; //The total score for this .in file
            while (allPathsIterator.hasNext()) { //Loops through all the paths we found
                //This is the current path on hand
                ArrayList<Integer> cPath = allPathsIterator.next();
                //This counts the number of horses we have in this path and the sum of their performance ratings
                int cPathCount = 0;
                int cPathAccumulate = 0;
                Iterator<Integer> cPathIter = cPath.iterator();
                while (cPathIter.hasNext()) { //Loops through all the horses in this path/team
                    int h = cPathIter.next();
                    cPathCount++;
                    cPathAccumulate = cPathAccumulate + pIndex.get(h);
                    toReturn = toReturn.concat(h+" ");
                }
                totalScore = totalScore + (cPathAccumulate*cPathCount);
                toReturn = toReturn.substring(0,toReturn.length()-1);
                toReturn = toReturn.concat("; ");
            }

            //Return the final result as a Pair object
            return new Pair(toReturn.substring(0,toReturn.length()-2),totalScore,allPaths.size());


        } catch (IOException e) {
            //error reading file
            System.out.println(filename + " could not be read");
            return null;
        }
    }


    //This solver randomly picks the first node and then randomly picks the next one if applicable
    public static Pair randomSolver(String filename) {
        try {
            //This block preps a reader to read in filename.in
            BufferedReader r = new BufferedReader(new FileReader(filename));
            String s = r.readLine();
            int numHorses = Integer.parseInt(s);

            //We can fill everything from the .in file into matrix
            int matrix[][] = new int[numHorses][numHorses];
            for(int j = 0; j < numHorses; j++) {
                s = r.readLine();
                String[] s2 = s.split(" ");
                for(int k = 0; k < numHorses; k++){
                    matrix[j][k] = Integer.parseInt(s2[k]);
                }
            }


            //We create a graph of horses, the performance index list and a set of all horses using the data we have in matrix
            //graph is our representation of the horses and their relationships with other horses
            //pIndex holds the performance index of each horse
            //horseList keeps a intact list of all the REAL horse objects that are in the graphs (don't modify it)
            //shuffledHorseList is a shuffled version of horseList that helps us randomly pick vertices
            HashMap<Integer, HashSet<Horse>> graph = new HashMap<Integer, HashSet<Horse>>();
            HashMap<Integer, Integer> pIndex = new HashMap<Integer, Integer>();
            ArrayList<Horse> horseList = new ArrayList<Horse>(); //For reference
            ArrayList<Horse> shuffledHorseList = new ArrayList<Horse>(); //For doing actual work

            //In one for loop, we can populate pIndex and horseList using matrix
            for(int j = 0; j < numHorses; j++){
                pIndex.put(j,matrix[j][j]);
                horseList.add(j,new Horse(j,pIndex.get(j)));
            }

            //Here, we clone the EXACT Horse objects from horseList into a new list and shuffle it
            for(Horse h : horseList){
                shuffledHorseList.add(h.clone());
            }
            Collections.shuffle(shuffledHorseList);


            //This for loop initializes the graph by placing vertices without edges
            for(int i = 0; i < numHorses; i++) {
                graph.put(i,new HashSet<Horse>());
            }

            //This double for loop loops through matrix to fill in the edges of the graph
            //Note: we fill in the graph with the Horse objects from horseList
            for(int j = 0; j < numHorses; j++){
                for(int k = 0; k < numHorses; k++) {
                    if (matrix[j][k] == 1 && j!=k) {
                        HashSet<Horse> adjacentHorses = graph.get(j);
                        adjacentHorses.add(horseList.get(k));
                        graph.put(j,adjacentHorses);
                    }
                }
            }

            //This string is used to store the output for this .in
            String toReturn = "";
            //int counter = 0; //Uncomment to debug; To check if we used all horses


            //This arraylist of arraylists stores the paths that we obtain
            ArrayList<ArrayList<Integer>> allPaths = new ArrayList<ArrayList<Integer>>();
            //We use this keepTrackSet as a safety check to keep track of which horses still haven't been assigned to a team
            HashSet<Integer> keepTrackSet = new HashSet<Integer>();
            for (int i = 0; i < numHorses; i++) { //We only store their horseIDs
                keepTrackSet.add(i);
            }


            //Initializations
            Horse removedHorse;
            ArrayList<Integer> currPath;
            Comparator<Horse> comp = new HorseComparator();


            //We keep on repeating this process until all the horses have been assigned teams
            while (keepTrackSet.size() != 0) {

                //This while(true) loops makes sure that we get a horse from shuffledHorseList that is TRULY still in the graph based on keepTrackSet
                while (true) {
                    //Remove a horse
                    removedHorse = shuffledHorseList.remove(0);
                    //counter++; //Uncomment to debug; To check if we used all horses
                    if (keepTrackSet.contains(removedHorse.getHorseID())) {
                        //If we enter here it means the removed horse can be removed
                        keepTrackSet.remove(removedHorse.getHorseID());
                        break;
                    } else {
                        //If we enter here it means that the removed horse is not there in the first place
                        //So we have to re-loop to pick another
                        //counter--; //Uncomment to debug; To check if we used all horses
                        continue;
                    }
                }

                //Start removal process on the graph
                //First we remove all edges to the removedHorse
                //We need an iterator through all the horses
                Iterator<Integer> horseIterator = graph.keySet().iterator();
                while (horseIterator.hasNext()) {
                    int currHorseID = horseIterator.next();

                    //Here we used a trick to check if the adjacentHorseList contains removedHorse
                    //We use the "immutable" horseList to get the REAL Horse objects
                    if (graph.get(currHorseID).contains(horseList.get(removedHorse.getHorseID()))) {
                        HashSet<Horse> adjacentHorseSet = graph.get(currHorseID);
                        adjacentHorseSet.remove(horseList.get(removedHorse.getHorseID()));
                        //System.out.println("Removed the edge:"+currHorseID+"->"+removedHorse.getHorseID()); //Uncomment to debug; Prints out which edge is removed
                        graph.put(currHorseID, adjacentHorseSet);
                    }
                }
                //Finished removal of edges to removedHorse

                //Initialize an empty team and add this first removedHorse to the team
                currPath = new ArrayList<Integer>();
                currPath.add(removedHorse.getHorseID());

                //This while(true) loop will DFS through the horses until we reach a horse that has no friends
                //In this solver we randomly pick a horse from the adjacent ones
                while(true) {
                    //System.out.print("Neighbors of:" + removedHorse.getHorseID()+"--->"); //Uncomment to debug; To list out the adjacent horses
                    HashSet<Horse> adjacentHorseSet = graph.get(removedHorse.getHorseID());
    
                    //This case means that the current horse has no friends so we have to end the path add it to allPaths and renew the path
                    //Then, we jump out of the while(true) loop to get the next new first vertex
                    if (adjacentHorseSet.size() == 0) {
                        allPaths.add(currPath);
                        currPath = new ArrayList<Integer>();
                        graph.remove(removedHorse.getHorseID());
                        break;
                    } else { //In this case, we pick a random horse
                        graph.remove(removedHorse.getHorseID());
                        //Pick the random horse here
                        Horse randomHorse = getRandomHorse(adjacentHorseSet);
                        //System.out.println("The chosen one is:" + maxHorse.getHorseID()); //Uncomment to debug; Prints out the horse that is chosen, i.e. the one with best performance rating
                        //counter++; //Uncomment to debug; Used to check if we used all the horses
                        removedHorse = randomHorse; //Treat the best horse as the current horse that is to be removed
                        currPath.add(removedHorse.getHorseID());
                        keepTrackSet.remove(removedHorse.getHorseID());
                        //Start Removal process of removedHorse from graph, same deal as before
                        Iterator<Integer> horseIterator1 = graph.keySet().iterator();
                        while (horseIterator1.hasNext()) {
                            int currHorseID = horseIterator1.next();
                            if (graph.get(currHorseID).contains(horseList.get(removedHorse.getHorseID()))) {
                                HashSet<Horse> adjacentHorseSet1 = graph.get(currHorseID);
                                adjacentHorseSet1.remove(horseList.get(removedHorse.getHorseID()));
                                //System.out.println("Removed the edge:"+currHorseID+"->"+removedHorse.getHorseID()); //Uncomment to debug; Prints out which edge is removed
                                graph.put(currHorseID, adjacentHorseSet1);
                            }
                        }
                        //End Removal of removedHorse from graph

                        //Re-loop to continue the DFS for next horse
                        continue;
                    }
                }

            }

            //System.out.println("did we use all horses?:" + (counter == numHorses)); //Uncomment to debug; Used to check if all horses are used
            //System.out.println("totalpaths:"+allPaths.size()); //Uncomment to debug; Used to check how many paths there are in our assignment

            //Iterator to give all the paths that we found
            Iterator<ArrayList<Integer>> allPathsIterator = allPaths.iterator();
            int totalScore = 0; //The total score for this .in file
            while (allPathsIterator.hasNext()) { //Loops through all the paths we found
                //This is the current path on hand
                ArrayList<Integer> cPath = allPathsIterator.next();
                //This counts the number of horses we have in this path and the sum of their performance ratings
                int cPathCount = 0;
                int cPathAccumulate = 0;
                Iterator<Integer> cPathIter = cPath.iterator();
                while (cPathIter.hasNext()) { //Loops through all the horses in this path/team
                    int h = cPathIter.next();
                    cPathCount++;
                    cPathAccumulate = cPathAccumulate + pIndex.get(h);
                    toReturn = toReturn.concat(h+" ");
                }
                totalScore = totalScore + (cPathAccumulate*cPathCount);
                toReturn = toReturn.substring(0,toReturn.length()-1);
                toReturn = toReturn.concat("; ");
            }

            //Return the final result as a Pair object
            return new Pair(toReturn.substring(0,toReturn.length()-2),totalScore,allPaths.size());


        } catch (IOException e) {
            //error reading file
            System.out.println(filename + " could not be read");
            return null;
        }
    }


    //This method will return a random REAL Horse object given an adjacentHorseSet
    private static Horse getRandomHorse(HashSet<Horse> adjacentHorseSet) {
        Random rnd = new Random();
        int i = rnd.nextInt(adjacentHorseSet.size());
        return (Horse) adjacentHorseSet.toArray()[i];
    }



    //This method is the control sequence that calls both the greedy solver and random solver
    public static String solve(String filename) {
        //Get the number of horses in this .in file
        int numHorsesOfIn = getV(filename);

        //This pairList will keep track of all the results that the solvers return
        HashSet<Pair> pairList = new HashSet<Pair>();
        Comparator<Pair> comp = new PairComparator();
        //Used to check if we have the best assignment, i.e. all horses in one team
        boolean breakIndicator = false;


        //We start of by running fewer iterations
        for (int i = 0; i < Math.max(100,(numHorsesOfIn/4)); i++) {
            Pair p = greedySolver(filename);
            pairList.add(p);
            if (p.getNumPaths() == 1) { //Used to check if we have best assignment, if we have then we're done
                breakIndicator = true;
                break;
            }
            //System.out.println("1.Score of instance"+i+": "+p.getScore()); //Uncomment to debug; Prints out the score of this iteration of greedySolver
        }

        //Run fewer iterations
        if (breakIndicator != true) { //Used to check if we have best assignment, if we have then we don't even need to step in here
            for (int i = 0; i < Math.max(100,(numHorsesOfIn/4)); i++) {
                Pair p = randomSolver(filename);
                pairList.add(p);
                if (p.getNumPaths() == 1) { //Used to check if we have best assignment, if we have then we're done
                    breakIndicator = true;
                    break;
                }
                //System.out.println("2.Score of instance"+i+": "+p.getScore()); //Uncomment to debug; Prints out the score of this iteration of randomSolver
            }
        }


        //Check if we have best assignment
        Pair maxPair = Collections.max(pairList,comp);
        if (maxPair.getNumPaths() == 1) {
            // System.out.println("---This is first run---"); //Uncomment to debug; Prints out maximum score and the number of paths the first pass has to offer
            // System.out.println("Max score of this .in: "+maxPair.getScore()); //Uncomment to debug; Prints out maximum score and the number of paths the first pass has to offer
            // System.out.println("Number of Paths in MaxScore: "+maxPair.getNumPaths()); //Uncomment to debug; Prints out maximum score and the number of paths the first pass has to offer
            return maxPair.getString();
        }


        //If we reach here it means we don't have the best assignment and we can try to do better so we perform hardcore iterations
        for (int i = 0; i < numHorsesOfIn; i++) {
            Pair p = greedySolver(filename);
            pairList.add(p);
            if (p.getNumPaths() == 1) { //Used to check if we have best assignment, if we have then we're done
                breakIndicator = true;
                break;
            }
            //System.out.println("1.Score of instance"+i+": "+p.getScore()); //Uncomment to debug; Prints out the score of this iteration of greedySolver
        }

        if (breakIndicator != true) { //Used to check if we have best assignment, if we have then we don't even need to step into here
            for (int i = 0; i < numHorsesOfIn; i++) { 
                Pair p = randomSolver(filename);
                pairList.add(p);
                if (p.getNumPaths() == 1) { //Used to check if we have best assignment, if we have then we're done
                    breakIndicator = true;
                    break;
                }
                //System.out.println("2.Score of instance"+i+": "+p.getScore()); //Uncomment to debug; Prints out the score of this iteration of randomSolver
            }
        }

        //Get the best assignment we can provide
        maxPair = Collections.max(pairList,comp);
        // System.out.println("---This is second run---"); //Uncomment to debug; Prints out maximum score and the number of paths the first and second pass has to offer
        // System.out.println("Max score of this .in: "+maxPair.getScore()); //Uncomment to debug; Prints out maximum score and the number of paths the first and second pass has to offer
        // System.out.println("Number of Paths in MaxScore: "+maxPair.getNumPaths()); //Uncomment to debug; Prints out maximum score and the number of paths the first and second pass has to offer
        return maxPair.getString();

    }


    //This method gets the number of horses in filename.in
    public static int getV(String filename) {
        try {
            BufferedReader r = new BufferedReader(new FileReader(filename));
            String s = r.readLine();
            int numHorses = Integer.parseInt(s);
            return numHorses;
        } catch (IOException e) {
            //error reading file
            System.out.println(filename + " could not be read");
            return 0;
        }
    }


    public static void main(String[] args) {

        //Creates an array with 1 - 600 in it to get filenames
        int[] arr = new int[600];
        for (int i = 1; i < 601; i++) {
            arr[i-1] = i;
        }

        //Gets the folder name that has all the .in files
        String dir = "";
        if (args.length != 0) {
            dir = args[0];
        }

        try {
            //Instatiates a filewriter
            FileWriter writer = new FileWriter("output.out", true);

            //Loops through all the .in filenames
            for(int i = 0; i < arr.length; i++) {
                String fn = dir + "/" + arr[i] + ".in";
                //System.out.println("@@@@Start "+arr[i]+".in@@@@"); //Uncomment to debug; Used to display progress on terminal
                String str = solve(fn); //This string is the assignment we have for the current .in file
                //System.out.println("@@@@End "+arr[i]+".in@@@@"); //Uncomment to debug; Used to display progress on terminal
                writer.write(str);
                writer.write("\r\n");
            }
            //Remember to close writer
            writer.close();

        } catch (IOException e) {
            System.out.println("Oops, something wrong happened");
        } 
    } 

}