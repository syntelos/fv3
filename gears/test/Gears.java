
package test;

import fv3.Bounds;
import fv3.Camera;
import fv3.model.CircumCube;
import fv3.model.Material ;
import fv3.model.Model;
import fv3.model.ShadeModel ;
import fv3.nui.Light;

import fv3.tk.Animator;
import fv3.tk.Fv3Screen;

import lxl.List;

import javax.media.opengl.GL2;

import com.sun.javafx.newt.MouseEvent;

/**
 * <a
 * href="http://www.opengl.org/resources/code/samples/glut_examples/mesademos/gears.c">Brian
 * Paul's popular GL Gears demo</a> in Fv3.
 * 
 * @author Brian Paul
 * @author Ron Cemer
 * @author Sven Goethel
 * @author John Pritchard
 */
public class Gears
    extends fv3.World
{
    public static void main(String[] argv){
        try {
            Gears gears = new Gears();
            if (null != argv && 0 != argv.length){
                String arg;
                for (int argc = argv.length, cc = 0; cc < argc; cc++){
                    arg = argv[cc];
                    if ("-dim".equals(arg)){
                        System.out.println(gears.getClass().getName());
                        System.out.println(((Bounds.CircumSphere)gears.getFv3Bounds()).toString("\t"));
                        List<fv3.Component> children = gears.getFv3Children();
                        for (fv3.Component child: children){
                            if (child instanceof Model){
                                System.out.println(child.getClass().getName());
                                System.out.println(((Model)child).toString("\t"));
                            }
                        }
                    }
                }
                System.exit(0);
            }
            else {
                Animator animator = new Animator(gears);
                animator.start();
            }
        }
        catch (Exception exc){
            exc.printStackTrace();
            System.exit(1);
        }
    }



    private final static float LightPos[] = { 5.0f, 5.0f, 10.0f, 0.0f };


    static float Angle = 0.0f;



    public Gears(){
        super();

        this.add(new Light(0,LightPos));
        this.add(new GearRed());
        this.add(new GearGreen());
        this.add(new GearBlue());

        this.setFv3Bounds();

        this.defineCamera('A').orthoFront(this);
        this.defineCamera('B').orthoBack(this);
        this.defineCamera('C').orthoTop(this);
        this.defineCamera('D').orthoBottom(this);
        this.defineCamera('E').orthoLeft(this);
        this.defineCamera('F').orthoRight(this);
    }


    public void display(GL2 gl){
        Angle += 1.0f;
        super.display(gl);
    }
}
