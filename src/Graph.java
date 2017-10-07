import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by sami- on 23/09/2017.
 *
 *This is core TSP solver
 */
public class Graph {
    private String filePath;
    private Node[] nodes;
    private int graphOrder;
    private int nbNodes;
    private long duration;
    private boolean dispPath;
    private boolean heuristic;

    public int getNbNodes() {
        return nbNodes;
    }



    public long getDuration() {
        return duration;
    }



    public Graph(String filePath,boolean heuristic, boolean dispPath){
        this.heuristic=heuristic;
        this.dispPath=dispPath;
        this.filePath=filePath;
        List<String> lines = null;
        try {//parser
            lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        graphOrder=Integer.parseInt(lines.get(0));
        nodes=new Node[graphOrder];
        for (int i = 0; i < graphOrder; i++) {
            String[] node = lines.get(i+1).split(" ");
            nodes[i]=new Node(node[0],Integer.parseInt(node[1]),Integer.parseInt(node[2]));
        }
        long start = System.nanoTime();
        aStar();
        duration = System.nanoTime() - start;
    }


    final Comparator<Node> comparator = new Comparator<Node>() {
        /*
        *
        * this comparator is used to order the openlist on their F value
        * (f=g+h)
        *
         */
        public int compare(Node node1, Node node2) {
            if (node1.getF() < node2.getF()){
                return -1;
            }
            if (node2.getF() < node1.getF()){
                return 1;
            }
            return 0;
        }
    };


    public void aStar(){//Cor A* algorithm
        //System.out.println("order "+graphOrder);
        Node minPath=new Node();
        List<Node> closedList = new ArrayList<>(graphOrder);
        List<Node> openList= new ArrayList(graphOrder);
        openList.add(new Node(nodes[0]));
        openList.get(0).setG(0);
        openList.get(0).setF(0);

        Node activeNode = openList.get(0);
        Double minDistance = Double.POSITIVE_INFINITY;

        boolean terminate=false;
        while (!openList.isEmpty() && !terminate){
            activeNode = openList.get(0);
            openList.remove(0);
            closedList.add(activeNode);
            if (activeNode.getF()>minDistance){//we found the shortest path
                //System.out.println(activeNode.getF());
                //System.out.println(activeNode.getParent()+" "+activeNode.getName());
                //System.out.println(minDistance);
                terminate=true;
            }else {
                if (activeNode.getParent().size() != graphOrder - 1) {


                    for (Node nodeT : nodes) {
                        Node node = new Node(nodeT);
                        if (!contains(activeNode, node) && !node.getName().equals(nodes[0].getName())) {//add the best node (smallest f)
                            double gTest = activeNode.getG() + distance(activeNode, node);
                            node.setParent(activeNode.getParent());
                            node.addParent(activeNode.getName());
                            node.setG(gTest);
                            if(heuristic){
                                node.setH(heurisitc(node));
                            }
                            else {
                                node.setH(0);
                            }

                            node.setF(gTest + node.getH());
                            openList.add(node);
                            //System.out.println(node.getParent()+ "  "+" s "+node.getParent().size()+"  " +node.getName()+ node.getF());
                            //}
                        }
                        if (node.getParent().size() == (graphOrder - 1) ){//complete cycle : calculate distance from start node (a) to start node(a)
                            node.setG(node.getG() + distance(node, nodes[0]));
                            node.setF(node.getG());
                            if(node.getF()<minDistance){
                                minPath = node;
                                minDistance = node.getF();
                                //System.out.println("mindist : "+ minDistance);
                            }
                        }
                    }
                }
            }

            Collections.sort(openList,comparator);//we order the list according to the F values of its elements
           // disp();
        }
        //System.out.println("min path:");
        minPath.addParent(minPath.getName());
        minPath.addParent(nodes[0].getName());
        if(dispPath){
            System.out.println();
            System.out.print(minPath.getParent());
            System.out.print(" total distance : "+minPath.getF()+"  nodes : ");
        }

        this.nbNodes=openList.size()+closedList.size();
    }

    private boolean contains(Node activeNode, Node node) {//Check if the salesman already passed by Node node
        if (activeNode.getName().equals(node.getName())){
            return true;
        }
        for (int i = 0; i < activeNode.getParent().size(); i++) {
            if(activeNode.getParent().get(i).equals(node.getName())){
                return true;
            }
        }
        return false;
    }

    private double distance(Node node1, Node node2) {//calculates the euclidean distance between two nodes
        return Math.sqrt(
                      Math.pow(node1.getX()-node2.getX(),2)
                    + Math.pow(node1.getY()-node2.getY(),2)
                );
    }

    private double heurisitcSimple(Node node){
        /*
        *
        * A simple heuristic that calculate
        * the distance between the last discover node and the most remote uncovered node
        *   + the distance between this uncovered node and the starting node (A)
        *
         */
        Node closestNode=new Node();
        double minlength=Double.POSITIVE_INFINITY;
        for (Node nodeB : nodes) { //choose the closes en add to parent
            if (!contains(node,nodeB)){
                if (distance(node,nodeB)<minlength){
                    minlength=distance(node,nodeB);
                    closestNode=nodeB;
                }
            }
        }
        return distance(node,closestNode)+distance(closestNode,nodes[0]);
    }


    private double heurisitc(Node node) {
        /*
         *
         * A more complicated heuristic that calculate the total length of the edges of
         * the MST of the remaining nodes plus starting node + last uncovered node.
         *
         */
        int heuristicSize=0;
        double h=0;
        List<Edge>mst=new ArrayList<>();
        List<Node>mstNodes=new ArrayList<>();
        List<Node>subGraph=new ArrayList<>();//Nodes left to add to mst
        subGraph.add(nodes[0]);
        for (Node nodeT:nodes) {
            if (!contains(node,nodeT)){//&&!nodeT.getName().equals(nodes[0].getName())
                subGraph.add(nodeT);
            }
        }
        mstNodes.add(node);

        double minlength=Double.POSITIVE_INFINITY;
        Edge shortestEdge=new Edge(new Node("void"),new Node("void"));
        while (subGraph.size()>0){//!shortestEdge.getNodeA().getName().equals(nodes[0].getName())
            minlength=Double.POSITIVE_INFINITY;
            shortestEdge=new Edge(new Node("void"),new Node("void"));
            Node previousNode=new Node();
            for (Node nodeG:subGraph) {//add the closest node to the mst
                for (Node nodeMst:mstNodes) {
                    if (distance(nodeG,nodeMst)<minlength && distance(nodeG,nodeMst)!=distance(node,nodes[0])){//don't take path between active node and T
                        minlength=distance(nodeG,nodeMst);
                        previousNode=new Node(nodeMst);
                        shortestEdge=new Edge(nodeG,nodeMst);
                    }
                }
            }
            mst.add(shortestEdge);
            heuristicSize+=distance(shortestEdge.getNodeA(),shortestEdge.getNodeB());
            subGraph.remove(shortestEdge.getNodeA());
            shortestEdge.getNodeA().setPreviousbis(previousNode);
            mstNodes.add(shortestEdge.getNodeA());
        }

        return heuristicSize;

    }


    public void disp(){//display the path taken
        System.out.println();
        for (int i = 0; i < nodes.length; i++) {
            System.out.println();
            System.out.println();
            System.out.println(nodes[i].getName()+" "+nodes[i].getX()+" "+nodes[i].getY());
            try {
                System.out.print(" parent : "+nodes[i].getParent());
            }catch (Exception e){
                System.out.print("Null");
            }
            System.out.print( " f : "+nodes[i].getF());

        }
    }

    /*private double heurisitcSize(List<Node> mstNodes, Node start, Node end,double size) {
        if(start.getName().equals(end.getName())){
            return size;
        }
        int i=0;
        boolean found=false;
        while (!found){
            if(mstNodes.get(i).getName().equals(start.getName())){
                found=true;
                size+=distance(start,mstNodes.get(i).getPreviousbis());

            }
            i++;
        }
        return heurisitcSize(mstNodes,mstNodes.get(i-1).getPreviousbis(),end,size);
    }*/

    /*private ArrayList neighbours(List<Edge> mst, Node element) {
        ArrayList<Node>neighbours=new ArrayList<>();
        for (int i = 0; i < mst.size(); i++) {
            if (mst.get(i).getNodeA().getName().equals(element.getName()) ){//xor
                neighbours.add(mst.get(i).getNodeB());
            }
            if (mst.get(i).getNodeB().getName().equals(element.getName()) ){
                neighbours.add(mst.get(i).getNodeA());
            }
        }
        return neighbours;
    }*/
}

