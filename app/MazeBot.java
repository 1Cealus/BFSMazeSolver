import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MazeBot {

    private static final char WALL = '#';
    private static final char EMPTY_SPACE = '.';
    private static final char START = 'S';
    private static final char GOAL = 'G';
    private static final int[][] DIRS = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

    private final int size;
    private final char[][] maze;
    private final int startX;
    private final int startY;
    private final int goalX;
    private final int goalY;
    
    int numStatesExplored = 0;

    public MazeBot(int size, char[][] maze, int startX, int startY, int goalX, int goalY) {
        this.size = size;
        this.maze = maze;
        this.startX = startX;
        this.startY = startY;
        this.goalX = goalX;
        this.goalY = goalY;
    }

    public void search() throws IOException {
        List<List<Integer>> explored = new ArrayList<>();
        boolean[][] visited = new boolean[size][size];
        int[][] parent = new int[size][size];
        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(startX * size + startY);
        visited[startX][startY] = true;
        int stateCount = 1; // Initialize the counter to 1 for the starting state
        List<List<Integer>> statesVisited = new ArrayList<>();

        int choice = 0, valid = 1;
        System.out.println("== Maze Bot ==\n\nYour maze configuration:\n");
        printMaze(explored);
        System.out.println("\n(1) Start search");
        System.out.println("(2) Exit program");
        Scanner scan = new Scanner(System.in);
        while(valid == 1)
        {
            System.out.print("Input your choice: ");
            try {
                choice = scan.nextInt();
                if(choice >= 1 && choice <=2)
                    valid = 0;
                else
                    System.out.println("\nInvalid input!\n");
            } catch (InputMismatchException e) {
                System.out.println("\nInvalid input!\n");
                scan.nextLine();
            }
        }
        scan.close();

        if(choice == 1){
            while (!queue.isEmpty()) {
                int curr = queue.poll();
                int x = curr / size;
                int y = curr % size;
                //If Solution is Found
                if (x == goalX && y == goalY) {
                    List<Integer> path = new ArrayList<>();
                    int i = goalX;
                    int j = goalY;
                    while (i != startX || j != startY) {
                        path.add(i * size + j);
                        int temp = parent[i][j];
                        i = temp / size;
                        j = temp % size;
                    }
                    path.add(startX * size + startY);
                    for (int k = path.size() - 1; k >= 0; k--) {
                        int pos = path.get(k);
                        int r = pos / size;
                        int c = pos % size;
                        maze[r][c] = '+';
                    }
                    
                    System.out.println("\n\nYour navigated maze:\n");
                    printMaze(explored);
                    System.out.println("\nSolution found!");

                    System.out.println("\nThe + path indicates the shortest path from S to G.");
                    System.out.println("The * indicates the states the bot has been to.");
                    if(stateCount<100) {
                        System.out.println("\nNumber of states explored: " + stateCount);
                        System.out.println("The order of states/coordinates which were visited by the bot:");
                        for (List<Integer> pos : statesVisited) {
                            int r = pos.get(0);
                            int c = pos.get(1);
                            System.out.println("(" + r + ", " + c + ")");
                        }
                        return;
                    }
                    // If stateCount is more than a 100 then Output to SolutionOutput.txt
                    String outputFilename = "SolutionOutput.txt";
                    if(stateCount>=100) {
                        try (PrintWriter out = new PrintWriter(new FileWriter(outputFilename))) {
                            out.println("Number of states explored: " + stateCount);
                            out.println("The order of states/coordinates which were visited by the bot:");
                            for (List<Integer> pos : statesVisited) {
                                int r = pos.get(0);
                                int c = pos.get(1);
                                out.println("(" + r + ", " + c + ")");
                            }
                        }
                    }
                    System.out.println("\nNumber of states explored and order is written to " + outputFilename);
                    
                    return;
                }
                List<Integer> neighbors = getNeighbors(x, y);
                for (int neighbor : neighbors) {
                    int r = neighbor / size;
                    int c = neighbor % size;
                    if (maze[r][c] != WALL && !visited[r][c]) {
                        visited[r][c] = true;
                        queue.offer(neighbor);
                        parent[r][c] = x * size + y;
                        stateCount++; // Increment the counter for each explored state
                    }
                }
                List<Integer> currPos = new ArrayList<>();
                currPos.add(x);
                currPos.add(y);
                explored.add(currPos);
                statesVisited.add(currPos);
            }
            // If no Solution Found
            System.out.println("\n\nYour navigated maze:\n");
            printMaze(explored);
            System.out.println("\nSolution cannot be found.");
            System.out.println("\nThe * indicates the states the bot has been to.");
            if(stateCount<100) {
                System.out.println("\nNumber of states explored: " + stateCount);
                System.out.println("The order of states/coordinates which were visited by the bot:");
                for (List<Integer> pos : statesVisited) {
                    int r = pos.get(0);
                    int c = pos.get(1);
                    System.out.println("(" + r + ", " + c + ")");
                }
                return;
            }
            // If stateCount is more than a 100 then Output to SolutionOutput.txt
            String outputFilename = "SolutionOutput.txt";
            if(stateCount>=100) {
                try (PrintWriter out = new PrintWriter(new FileWriter(outputFilename))) {
                    out.println("Number of states explored: " + stateCount);
                    out.println("The order of states/coordinates which were visited by the bot:");
                    for (List<Integer> pos : statesVisited) {
                        int r = pos.get(0);
                        int c = pos.get(1);
                        out.println("(" + r + ", " + c + ")");
                    }
                }
            }
            System.out.println("\nNumber of states explored and order is written to " + outputFilename);
        }
        else{
            System.out.println("Exiting Program.");
            return;
        }
    }

    private List<Integer> getNeighbors(int x, int y) {
        List<Integer> neighbors = new ArrayList<>();
        for (int[] dir : DIRS) {
            int r = x + dir[0];
            int c = y + dir[1];
            if (r >= 0 && r < size && c >= 0 && c < size) {
                neighbors.add(r * size + c);
            }
        }
        return neighbors;
    }

    private void printMaze(List<List<Integer>> explored) {
    Set<Integer> exploredSet = new HashSet<>();
    for (List<Integer> pos : explored) {
        int r = pos.get(0);
        int c = pos.get(1);
        exploredSet.add(r * size + c);
    }

    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            if (maze[i][j] == WALL) {
                System.out.print(WALL);
            } else if (i == startX && j == startY) {
                System.out.print(START);
            } else if (i == goalX && j == goalY) {
                System.out.print(GOAL);
            } else if (maze[i][j] == '+') {
                System.out.print('+');
            } else if (exploredSet.contains(i * size + j)) {
                System.out.print('*');
            } else {
                System.out.print(EMPTY_SPACE);
            }
        }
        System.out.println();
    }
    }
    public static void main(String[] args) {
    try (BufferedReader br = new BufferedReader(new FileReader("maze.txt"))) {
        int size = Integer.parseInt(br.readLine());
        char[][] maze = new char[size][size];
        int startX = -1;
        int startY = -1;
        int goalX = -1;
        int goalY = -1;
        for (int i = 0; i < size; i++) {
            String line = br.readLine();
            for (int j = 0; j < size; j++) {
                maze[i][j] = line.charAt(j);
                if (maze[i][j] == START) {
                    startX = i;
                    startY = j;
                } else if (maze[i][j] == GOAL) {
                    goalX = i;
                    goalY = j;
                }
            }
        }
        MazeBot bot = new MazeBot(size, maze, startX, startY, goalX, goalY);
        bot.search();
    } catch (IOException e) {
        System.out.println("Error on load. Exiting Program.");
    }
}
}