/**
 * Created by sami- on 28/09/2017.
 */
public class Edge {
    private Node nodeA;
    private Node nodeB;

    public Edge(Node nodeA, Node nodeB) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
    }
    public Edge() {
    }


    public Node getNodeA() {
        return nodeA;
    }

    public void setNodeA(Node nodeA) {
        this.nodeA = nodeA;
    }

    public Node getNodeB() {
        return nodeB;
    }

    public void setNodeB(Node nodeB) {
        this.nodeB = nodeB;
    }

}
