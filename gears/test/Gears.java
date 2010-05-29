
package test;

import fv3.Camera;

import fv3.nui.Light;
import fv3.nui.Viewport;

import fv3.tk.Animator;
import fv3.tk.Fv3Screen;

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
            Animator animator = new Animator(gears);
            animator.start();
        }
        catch (Exception exc){
            exc.printStackTrace();
            System.exit(1);
        }
    }


    private final static int LightNum = 0;;
    private final static float LightPos[] = { 5.0f, 5.0f, 10.0f, 0.0f };

    static float Angle = 0.0f;


    private double prevMouseX, prevMouseY;

    private double screenW, screenH;


    public Gears(){
        super();
        this.translate(0,0,-40);

        this.add(new Light(LightNum,LightPos));
        this.add(new Gear1());
        this.add(new Gear2());
        this.add(new Gear3());
        this.addEnd();

        this.getCamera().moveto(3,0,6).view(this).frustrum(5.0,60.0);
    }


    public void init(GL2 gl){

        Fv3Screen fv3s = Fv3Screen.Current();
        this.screenW = fv3s.width;
        this.screenH = fv3s.height;

        super.init(gl);
    }

    public void mousePressed(MouseEvent e){
        this.prevMouseX = e.getX();
        this.prevMouseY = e.getY();
    }
    public void mouseDragged(MouseEvent e){
        double x = e.getX();
        double y = e.getY();
        double dx = (x-this.prevMouseX);
        double dy = (y-this.prevMouseY);

        Camera camera = this.getCamera();
        camera.moveby(dx,dy,0);

        this.prevMouseX = x;
        this.prevMouseY = y;
    }

    public void display(GL2 gl){
        Angle += 1.0f;
        super.display(gl);
    }
}
