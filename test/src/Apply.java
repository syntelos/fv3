
import fv3.math.Path;
import path.Parser;

public class Apply {

    public final static String D = "M-3000,0 L3000,0 M0,-3000 L0,3000";

    public final static void main(String[] argv){



        if (0 < argv.length){
            int cc = 1;
            for (String d : argv){
                Path path = new Path();
                try {
                    path = new Parser(d).apply(path);
                    System.out.printf("[%3d] In: '%s'%n",cc,d);
                    System.out.printf("[%3d] Out: '%s'%n",cc,path.toString());
                    System.exit(0);
                }
                catch (Exception exc){
                    System.err.printf("[%3d] In: '%s', Error: '%s'%n",cc,d,exc);
                    exc.printStackTrace();
                    System.exit(1);
                }
                cc++;
            }
        }
        else {
            Path path = new Path();
            try {
                path = new Parser(D).apply(path);
                System.out.printf("In: '%s'%n",D);
                System.out.printf("Out: '%s'%n",path.toString());
                System.exit(0);
            }
            catch (Exception exc){
                System.err.printf("In: '%s', Error: '%s'%n",D,exc);
                exc.printStackTrace();
                System.exit(1);
            }
        }
    }
}
