
package test;

import fv3.Bounds;
import fv3.Camera;
import fv3.csg.Solid;
import fv3.math.Color;
import fv3.math.VertexArray;
import fv3.model.Disable;
import fv3.model.Enable;
import fv3.model.Material ;
import fv3.model.Model;
import fv3.model.ShadeModel ;
import fv3.nui.Light;
import fv3.tk.Animator;

import lxl.List;

import javax.media.opengl.GL2;

/**
 * 
 */
public class Cylinder
    extends fv3.World
{

    public static void main(String[] argv){
        try {
            Cylinder cylinder = new Cylinder();
            if (null != argv && 0 != argv.length){
                String arg;
                for (int argc = argv.length, cc = 0; cc < argc; cc++){
                    arg = argv[cc];
                    if ("-dim".equals(arg)){
                        System.out.println(cylinder.getClass().getName());
                        System.out.println(((Bounds.CircumSphere)cylinder.getFv3Bounds()).toString("\t"));

                        Model model = (Model)cylinder.get(1);
                        System.out.println(model.getClass().getName());
                        System.out.println(model.toString("\t"));
                    }
                    else if ("-geom".equals(arg)){
                        System.out.println(cylinder.getClass().getName());
                        System.out.println(((Bounds.CircumSphere)cylinder.getFv3Bounds()).toString("\t"));

                        Model model = (Model)cylinder.get(1);
                        Solid s;

                        s = (Solid)model.get(4);
                        System.out.println();
                        System.out.println(s.toString("\t"));
                    }
                }
                System.exit(0);
            }
            else {
                Animator animator = new Animator(cylinder);
                animator.start();
            }
        }
        catch (Exception exc){
            exc.printStackTrace();
            System.exit(1);
        }
    }

    public enum CylinderType {
        XY, ZY, ZX;
    }


    public Cylinder(){
        this(CylinderType.ZY);
    }
    public Cylinder(CylinderType type){
        super();
        this.setBgColor(Color.White);

        this.add(new Light());

        Model cylinder = new Model();
        {
            cylinder.add(new Material(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Color.Blue));
            cylinder.add(new ShadeModel(GL2.GL_FLAT));
            cylinder.add(new Enable(GL2.GL_CULL_FACE));
            cylinder.add(new Enable(GL2.GL_NORMALIZE));
            switch(type){
            case XY:
                cylinder.add(new fv3.csg.Cylinder.XY(10,10).compile());
                break;
            case ZX:
                cylinder.add(new fv3.csg.Cylinder.ZX(10,10).compile());
                break;
            case ZY:
                cylinder.add(new fv3.csg.Cylinder.ZY(10,10).compile());
                break;
            default:
                throw new IllegalStateException();
            }
        }
        this.add(cylinder);

        this.setFv3Bounds();

        this.defineCamera('A').orthoFront(this);
        this.defineCamera('B').orthoBack(this);
        this.defineCamera('C').orthoTop(this);
        this.defineCamera('D').orthoBottom(this);
        this.defineCamera('E').orthoLeft(this);
        this.defineCamera('F').orthoRight(this);
    }
}
