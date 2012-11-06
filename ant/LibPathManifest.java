

import java.util.StringTokenizer;

public class LibPathManifest {

    public final static String UserDir = System.getProperty("user.dir");

    public final static int UserDirLen = UserDir.length();


    public static void main(String[] argv){
        boolean once = true;

        for (String arg: argv){
            StringTokenizer strtok = new StringTokenizer(arg,":;");
            final int count = strtok.countTokens();
            for (int cc = 0; cc < count; cc++){

                String pel = Rel(strtok.nextToken());

                if (once)
                    once = false;
                else
                    System.out.print(" ");

                System.out.print(pel);
            }
        }
        System.out.println();
    }

    public final static String Rel(String dirpath){
        dirpath = dirpath.replace('\\','/');
        int idx0 = dirpath.indexOf(UserDir);
        if (-1 < idx0)
            return dirpath.substring(idx0+UserDirLen+1);
        else if (dirpath.startsWith("./"))
            return dirpath.substring(2);
        else
            return dirpath;
    }
}
