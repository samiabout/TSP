import java.util.ArrayList;
import java.util.List;

/**
 * Created by sami- on 23/09/2017.
 *
 * This class describes one node of the graph
 */
public class Node {
    private String name;
    private int x;
    private int y;
    private double g;
    private double h;
    private double f;
    private List<String> parent;
    private Node previous;//used for mst heuristic

    public Node getPreviousbis() {
        return previousbis;
    }

    public void setPreviousbis(Node previousbis) {
        this.previousbis = previousbis;
    }

    private Node previousbis;//used for mst heuristic
    private boolean visited;//used for mst heuristic

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }


    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }


    public Node(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.g=Double.POSITIVE_INFINITY;
        this.h=0;
        this.f=Double.POSITIVE_INFINITY;
        this.parent=new ArrayList<>();
    }

    public Node(Node nodebis) {//my a clone of the node
        this.name = nodebis.name;
        this.x = nodebis.getX();
        this.y = nodebis.getY();
        this.g = nodebis.getG();
        this.h = nodebis.getH();
        this.f = nodebis.getF();
        this.parent=new ArrayList<>();
    }
    public Node(){

    }
    public Node(String name){
        this.name=name;
    }


    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public List<String> getParent() {
        return parent;
    }

    public void addParent(String parent1) {
        try {
            this.parent.add(parent1);
        }catch (Exception e){

        }
    }
    public void setParent(List<String> parent) {
        for (int i = 0; i < parent.size(); i++) {
            this.addParent(parent.get(i));
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


}
