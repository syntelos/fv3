
package test;

import fv3.Component;

import fv3.nui.View;

import fv3tk.Animator;
import fv3tk.Fv3Screen;

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
 * @author jdp@syntelos
 * @version 1.2 1999/10/21
 */
public class Gears
    extends fv3.World
{
    public static void main(String[] argv){
        try {
            Gears gears = new Gears();
            Animator animator = new Animator(gears);
            animator.start();
        }
        catch (Exception exc){
            exc.printStackTrace();
            System.exit(1);
        }
    }


    private final static float LightPos[] = { 5.0f, 5.0f, 10.0f, 0.0f };


    private View view;

    private double prevMouseX, prevMouseY;

    private double screenW, screenH;


    public Gears(){
        super();
        this.matrix();
        this.add(this.view = new View(200,200,600,600));
        this.add(new fv3.nui.Light(0,LightPos));
        this.add(new Gear1());
        this.add(new Gear2());
        this.add(new Gear3());
        this.add(new fv3.nui.Frustrum(-1.0, 1.0, -1.0, +1.0, 5.0, 60.0));
        this.addEnd();
    }


    public void init(GL2 gl){

        Fv3Screen fv3s = Fv3Screen.Current();
        this.screenW = fv3s.width;
        this.screenH = fv3s.height;

        this.view.centerVer(fv3s);

        super.init(gl);
    }

    public void mousePressed(MouseEvent e){
        this.prevMouseX = e.getX();
        this.prevMouseY = e.getY();
    }
    public void mouseDragged(MouseEvent e){
        double x = e.getX();
        double y = e.getY();
        double rx = (Math.PI * ((x-this.prevMouseX)/this.screenW));
        double ry = (Math.PI * ((this.prevMouseY-y)/this.screenH));

        this.rotateXY( rx, ry);

        this.prevMouseX = x;
        this.prevMouseY = y;
    }

}
