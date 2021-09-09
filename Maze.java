package hw5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Maze {

    private int width;
    
    private int height;
    
    private int nodes;
    
    private Stack<Node> dfsStack;
    
    private List<Node> graph;
    
    private String[][] visualGraph;
    
    private int[] lastNode = new int[2];
    
    private Stack<Node> solStack;
    
    private boolean debug;
    
    public Random r = new Random();
    
    public Maze(int theWidth, int theHeight, boolean isDebug) {
        width = theWidth;
        height = theHeight;
        debug = isDebug;
        nodes = theWidth * theHeight;
        lastNode[0] = height * 2 - 1;
        lastNode[1] = width * 2 - 1;
        dfsStack = new Stack<Node>();
        graph = new ArrayList<>(nodes);
        visualGraph = new String[height * 2 + 1][width * 2 + 1];
        buildVisualGraph();
        buildGraph();
        buildMaze();
        clearVisited();
        markSolution();
        solveMaze(); 
    }
    
    private void buildVisualGraph() {
        for (int i = 0; i < height * 2 + 1; i++) {
            for (int j = 0; j < width * 2 + 1; j++) {
                if (i % 2 == 0) {
                        visualGraph[i][j] = "X";
                } else {
                    if (j % 2 == 0) {
                        visualGraph[i][j] = "X";
                    } else {
                        visualGraph[i][j] = " ";
                    }
                }
            }
        }
        visualGraph[0][1] = " ";
        visualGraph[height * 2][width * 2 - 1] = " ";
    }
    
    private void clearVisited() {
        for (int i = 0; i < height * 2 + 1; i++) {
            for (int j = 0; j < width * 2 + 1; j++) {
                if (i % 2 != 0 && j % 2 != 0) {
                    visualGraph[i][j] = " ";
                }
            }
        }
    }
    
    private void buildGraph() {
        for (int i = 0; i < nodes; i++) {
            graph.add(new Node());
        }
        for (int i = 0; i < nodes; i++) {
            ArrayList<Node> adjacents = new ArrayList<Node>();
            if (i % width < width - 1) {
                adjacents.add(graph.get(i + 1));
            }
            if (i % width > 0) {
                adjacents.add(graph.get(i - 1));
            }
            if (i / width < height - 1) {
                adjacents.add(graph.get(i + width));
            }
            if (i / width >= 1) {
                adjacents.add(graph.get(i - width));
            }
            graph.get(i).adjacent = adjacents;
        }
    }
    
    private void buildMaze() {
        dfsStack.push(graph.get(0));
        while (!dfsStack.isEmpty()) {
            Node poppedNode = dfsStack.pop();
            ArrayList<Node> unvisited = poppedNode.getUnvisitedNodes();
            setVisited(poppedNode);
            if (getVerticalIndex(poppedNode) == lastNode[0] && getHorizontalIndex(poppedNode) == lastNode[1]) {
                solStack = (Stack<Node>) dfsStack.clone();
                solStack.push(poppedNode);
            }
            if (unvisited.size() > 0) {
                dfsStack.push(poppedNode);
                Node nextNode = unvisited.get(r.nextInt(unvisited.size()));
                removeWall(poppedNode, nextNode);
                unvisited.remove(nextNode);
                dfsStack.push(nextNode);
            }
        }
    }
    
    private void markSolution() {
        while (!solStack.isEmpty()) {
            Node theNode = solStack.pop();
            int horIndex = getHorizontalIndex(theNode);
            int verIndex = getVerticalIndex(theNode);
            visualGraph[verIndex][horIndex] = "O";
        }
    }
    
    private void setVisited(Node theNode) {
        theNode.visited = true;
        int horIndex = getHorizontalIndex(theNode);
        int verIndex = getVerticalIndex(theNode);
        if (!visualGraph[verIndex][horIndex].equals("V")) {
            visualGraph[verIndex][horIndex] = "V";
            if (debug) {
                System.out.println(toString());
            }
        }
    }
    
    private int getVerticalIndex(Node theNode) {
        return graph.indexOf(theNode) / width * 2 + 1;
    }
    
    private int getHorizontalIndex(Node theNode) {
        return graph.indexOf(theNode) % width * 2 + 1;
    }
    
    private void removeWall(Node theNode, Node theOtherNode) {
        int verIndex = getVerticalIndex(theNode);
        int horIndex = getHorizontalIndex(theNode);
        int otherVerIndex = getVerticalIndex(theOtherNode);
        int otherHorIndex = getHorizontalIndex(theOtherNode);
        if (verIndex == otherVerIndex) {
            visualGraph[verIndex][(horIndex + otherHorIndex) / 2] = " ";
        } else if (horIndex == otherHorIndex) {
            visualGraph[(verIndex + otherVerIndex) / 2][horIndex] = " ";
        }
    }
    
    private void solveMaze() {
        System.out.println(toString());
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String[] row: visualGraph) {
            sb.append(Arrays.toString(row));
            sb.append("\n");
        }
        return sb.toString();
    }
    
    private class Node {
        
        private boolean visited;
        
        private ArrayList<Node> adjacent;
        
        public Node() {
            adjacent = new ArrayList<>();
            visited = false;
        }
        
        public ArrayList<Node> getUnvisitedNodes() {
            ArrayList<Node> result = new ArrayList<Node>();
            for (int i = 0; i < adjacent.size(); i++) {
                if (!adjacent.get(i).visited) {
                    result.add(adjacent.get(i));
                }
            }
            return result;
        }
    }
}
