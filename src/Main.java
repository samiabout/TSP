/**
 * Created by sami- on 23/09/2017.
 */
public class Main {


    public static void main(String[] args) {
        System.out.println("Sami About");
        long measuredTime[][]=new long[16][10];
        int nbNodes[][]=new int[16][10];
        /*for (int j = 1; j <= 10; j++) {//warm up, the values are not stored
            Graph graph=new Graph("randTSP/"+3+"/instance_"+j+".txt");
        }*/
        for (int i = 11; i <= 12; i++) {
            System.out.println();
            //System.out.print(i+" :  ");
            for (int j = 1; j <= 10; j++) {
                Graph graph=new Graph("randTSP/"+i+"/instance_"+j+".txt");
                measuredTime[i-1][j-1]=graph.getDuration();
                nbNodes[i-1][j-1]=graph.getNbNodes();
                System.out.print(nbNodes[i-1][j-1]+" ; ");
            }
        }
        System.out.println();


//       Graph graph=new Graph("test.txt");

    }

}