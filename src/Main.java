/**
 * Created by sami- on 23/09/2017.
 */
public class Main {


    public static void main(String[] args) {
        long measuredTime[][]=new long[16][10];
        int nbNodes[][]=new int[16][10];

          Graph graphTest=new Graph("randTSP/"+10+"/instance_"+1+".txt",true,true);

        for (int i = 1; i <= 16; i++) {
            System.out.println("\n");
            System.out.print(i+" ---- :  ");
            for (int j = 1; j <= 10; j++) {
                /*
                *
                *
                *Change these parameters to choose the outputs of the algorithm
                *
                *
                 */
                Graph graph=new Graph("randTSP/"+i+"/instance_"+j+".txt",true,false);

                measuredTime[i-1][j-1]=graph.getDuration();
                nbNodes[i-1][j-1]=graph.getNbNodes();
                System.out.print(nbNodes[i-1][j-1]+" ; ");
            }
        }
        System.out.println();


//       Graph graph=new Graph("test.txt");

    }

}