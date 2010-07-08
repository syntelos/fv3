
package test;

import fv3.Bounds;
import fv3.Camera;
import fv3.csg.Solid;
import fv3.math.Color;
import fv3.math.Matrix;
import fv3.math.VertexArray;
import fv3.model.Enable;
import fv3.model.Material ;
import fv3.model.Model;
import fv3.model.ShadeModel ;
import fv3.nui.Light;
import fv3.tk.Animator;

import lxl.List;

import javax.media.opengl.GL2;

/**
 * Perform construction on CSG Torus.
 */
public class Torus
    extends fv3.World
{

    public static void main(String[] argv){
        try {
            Torus torus = new Torus();
            if (null != argv && 0 != argv.length){
                String arg;
                for (int argc = argv.length, cc = 0; cc < argc; cc++){
                    arg = argv[cc];
                    if ("-dim".equals(arg)){
                        System.out.println(torus.getClass().getName());
                        System.out.println(((Bounds.CircumSphere)torus.getFv3Bounds()).toString("\t"));

                        Model model = (Model)torus.get(1);
                        System.out.println(model.getClass().getName());
                        System.out.println(model.toString("\t"));
                    }
                    else if ("-geom".equals(arg)){
                        System.out.println(torus.getClass().getName());
                        System.out.println(((Bounds.CircumSphere)torus.getFv3Bounds()).toString("\t"));

                        Model model = (Model)torus.get(1);
                        Solid s;

                        s = (Solid)model.get(2);
                        System.out.println();
                        System.out.println(s.toString("\t"));
                    }
                }
                System.exit(0);
            }
            else {
                Animator animator = new Animator(torus);
                animator.start();
            }
        }
        catch (Exception exc){
            exc.printStackTrace();
            System.exit(1);
        }
    }



    public Torus(){
        super();
        this.setBgColor(Color.White);

        this.add(new Light());

        Model torus = new Model();
        {
            torus.add(new Material(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, Color.Blue));
            torus.add(new ShadeModel(GL2.GL_FLAT));

            Solid a = new fv3.csg.Torus.XY(5,10);
            {
                Matrix m = new Matrix();
                m.translate(-5,-5,-3);
                a.transform(m);
            }
            {
                Solid b = new fv3.csg.Torus.XY(5,10);
                a = a.union(b).compile();
            }

            torus.add(a);
        }
        this.add(torus);

        this.setFv3Bounds();

        this.defineCamera('A').orthoFront(this);
        this.defineCamera('B').orthoBack(this);
        this.defineCamera('C').orthoTop(this);
        this.defineCamera('D').orthoBottom(this);
        this.defineCamera('E').orthoLeft(this);
        this.defineCamera('F').orthoRight(this);
    }
}
