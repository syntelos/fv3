
package test;

import fv3.Bounds;
import fv3.Camera;
import fv3.csg.Solid;
import fv3.csg.u.Illustrator;
import fv3.math.Color;
import fv3.math.VertexArray;
import fv3.model.Debugger;
import fv3.model.Disable;
import fv3.model.Enable;
import fv3.model.Material;
import fv3.model.Model;
import fv3.model.PolygonMode;
import fv3.model.ShadeModel;
import fv3.nui.Light;
import fv3.tk.Animator;

import lxl.List;

import javax.media.opengl.GL2;
import com.jogamp.newt.event.KeyEvent;

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
        catch (Debugger deb){
            deb.printStackTrace();
            deb.println();
            deb.show();
        }
        catch (Exception exc){
            exc.printStackTrace();
            System.exit(1);
        }
    }


    private fv3.csg.Cylinder cxy10, cxy05;
    private Illustrator illustrator;


    public Cylinder(){
        super();
        this.setBgColor(Color.White);

        this.add(new Light());

        this.cxy10 = new fv3.csg.Cylinder.XY(10,10,1);
        this.cxy05 = new fv3.csg.Cylinder.XY(5,12,1);

        Model cylinder = new Model();
        {
            cylinder.add(new Material(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, Color.Blue));
            cylinder.add(new ShadeModel(GL2.GL_FLAT));
            cylinder.add(new PolygonMode.Line(GL2.GL_FRONT_AND_BACK));
            cylinder.add(new Enable(GL2.GL_DEPTH_TEST));

//             Solid s = new fv3.csg.Cylinder.XY(10,10,1)
//                 .difference(new fv3.csg.Cylinder.XY(5,12,1));

//             cylinder.add(s.compile());

            cylinder.add(this.cxy10.compile());
            cylinder.add(this.cxy05.compile());
        }
        this.add(cylinder);
        {
            this.illustrator = new Illustrator(this.cxy10,this.cxy05);
        }
        this.add(this.illustrator);

        this.setFv3Bounds();

        this.defineCamera('A').orthoFront(this);
        this.defineCamera('B').orthoBack(this);
        this.defineCamera('C').orthoTop(this);
        this.defineCamera('D').orthoBottom(this);
        this.defineCamera('E').orthoLeft(this);
        this.defineCamera('F').orthoRight(this);
    }

    public void keyTyped(KeyEvent e) {

        char ch = e.getKeyChar();

        if (('a' <= ch && ch <= 'z')||('A' <= ch && ch <= 'Z'))
            super.keyTyped(e);
        else {
            this.illustrator.next();
        }
    }
}
