import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by sami- on 23/09/2017.
 */
public class Graph {
    private String filePath;
    private Node[] nodes;
    private int graphOrder;
    private int nbNodes;
    private long duration;

    public int getNbNodes() {
        return nbNodes;
    }



    public long getDuration() {
        return duration;
    }



    public Graph(String filePath){
        this.filePath=filePath;
        List<String> lines = null;
        try {
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


    public void aStar(){
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
            if (activeNode.getF()>minDistance){
                //System.out.println(activeNode.getF());
                //System.out.println(activeNode.getParent()+" "+activeNode.getName());
                //System.out.println(minDistance);
                terminate=true;
            }else {
                if (activeNode.getParent().size() == graphOrder - 1) {//complete cycle : calculate distance from start node (a) to start node(a)

                } else {
                    for (Node nodeT : nodes) {
                        Node node = new Node(nodeT);
                        if (!contains(activeNode, node) && !node.getName().equals(nodes[0].getName())) {//
                            double gTest = activeNode.getG() + distance(activeNode, node);
                            //if(gTest<=minDistance){ //gTest<=node.getG()
                            node.setParent(activeNode.getParent());
                            node.addParent(activeNode.getName());
                            node.setG(gTest);
                            if(node.getParent().size() <= (graphOrder - 2) ){
                                node.setH(Math.max(heurisitc(node),heurisitcSimple(node)));//heuristic mst

                            }
                            else {
                                node.setH(heurisitcSimple(node));//heuristic closest neighbour
                            }

                            node.setF(gTest + node.getH());
                            openList.add(node);
                            //System.out.println(node.getParent()+ "  "+" s "+node.getParent().size()+"  " +node.getName()+ node.getF());
                            //}
                        }
                        if (node.getParent().size() == (graphOrder - 1) ){
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

            Collections.sort(openList,comparator);
           // disp();
        }
        //System.out.println("min path:");
        minPath.addParent(minPath.getName());
        minPath.addParent(nodes[0].getName());
        //System.out.print(minPath.getParent());
        //System.out.println(minPath.getF());
        this.nbNodes=openList.size()+closedList.size();
    }

    private boolean contains(Node activeNode, Node node) {
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

    private double distance(Node node1, Node node2) {
        return Math.sqrt(
                      Math.pow(node1.getX()-node2.getX(),2)
                    + Math.pow(node1.getY()-node2.getY(),2)
                );
    }

    private double heurisitcSimple(Node node){
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
        double h=0;
        List<Edge>mst=new ArrayList<>();
        List<Node>mstNodes=new ArrayList<>();
        List<Node>subGraph=new ArrayList<>();//Nodes left to add to mst
        subGraph.add(nodes[0]);
        for (Node nodeT:nodes) {
            if (!contains(node,nodeT)){
                subGraph.add(nodeT);
            }
        }
        mstNodes.add(node);

        double minlength=Double.POSITIVE_INFINITY;
        Edge shortestEdge=new Edge(new Node("void"),new Node("void"));
        while (!shortestEdge.getNodeA().getName().equals(nodes[0].getName())){
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
            subGraph.remove(shortestEdge.getNodeA());
            shortestEdge.getNodeA().setPreviousbis(previousNode);
            mstNodes.add(shortestEdge.getNodeA());
        }

        return heurisitcSize(mstNodes, nodes[0],node,0);

        /*Stack<Node> stack=new Stack();
        stack.add(node);
        //List<Boolean>visited=new ArrayList<>(mstNodes.size());
        for (int i = 0; i <mstNodes.size(); i++) {
            mstNodes.get(i).setPrevious(null);
            mstNodes.get(i).setVisited(false);
        }
        //visited.set(0,false);
        while (!stack.isEmpty())
        {
            Node element=stack.pop();

            ArrayList<Node> neighbours=neighbours(mst,element);
            for (int i = 0; i < neighbours.size(); i++) {
                if(neighbours.get(i)!=null && !neighbours.get(i).isVisited())
                {
                    neighbours.get(i).setPrevious(element);
                    stack.add(neighbours.get(i));
                    neighbours.get(i).setVisited(true);

                }
            }
        }*/


        /*Node nodeA=new Node(node);
        nodeA.setParent(node.getParent());
        Node closestNode=new Node();
        closestNode.setName("none");
        while (!closestNode.getName().equals(nodes[0].getName())){
            closestNode=new Node();
            double minlength=Double.POSITIVE_INFINITY;
            for (Node nodeT : nodes) { //choose the closes en add to parent
                Node nodeB = new Node(nodeT);
                if (nodeB.getName().equals(nodes[0].getName())||!contains(nodeA,nodeB)){
                    if (distance(nodeA,nodeB)<minlength){
                        minlength=distance(nodeA,nodeB);
                        closestNode=nodeB;
                    }
                }
            }
            closestNode.setParent(nodeA.getParent());
            nodeA=new Node(closestNode);
            nodeA.setParent(closestNode.getParent());
            nodeA.addParent(closestNode.getName());
            h+=minlength;
        }*/
    }

    private double heurisitcSize(List<Node> mstNodes, Node start, Node end,double size) {
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
    }

    private ArrayList neighbours(List<Edge> mst, Node element) {
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
    }


    public void disp(){
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

}

