
package test;

import fv3.Bounds;
import fv3.Camera;
import fv3.font.FontOptions;
import fv3.font.Glyph;
import fv3.font.GlyphVector;
import fv3.font.HersheyFont;
import fv3.math.Color;
import fv3.model.Debugger;
import fv3.model.Disable;
import fv3.model.Enable;
import fv3.model.Material;
import fv3.model.Model;
import fv3.model.PolygonMode;
import fv3.model.ShadeModel;
import fv3.nui.Light;

import javax.media.opengl.GL2;

/**
 * 
 */
public class Hello
    extends fv3.World
{

    public static void main(String[] argv){
        try {
            new Hello().show();
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



    public Hello()
        throws java.io.IOException
    {
        super();
        this.setBgColor(Color.White);

        this.add(new Light());

        Model hello = new Model();
        {
            hello.add(new ShadeModel(GL2.GL_FLAT));
            hello.add(new Enable(GL2.GL_DEPTH_TEST));
            {
                hello.add(new Material(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, Color.Blue));
                GlyphVector a = new GlyphVector(new HersheyFont.Futural(new FontOptions(22,20)));
                hello.add(a);

                a.println("Hello, Futural!");
                a.println("It's great to see you.");
                a.println();
                {
                    GlyphVector b = new GlyphVector(a,new HersheyFont.Futural(new FontOptions(10,10)));
                    b.println(fv3.Version.Name+' '+fv3.Version.Number);
                    a.add(b);
                }
                a.fit(10,300,10,300);
            }
        }
        this.add(hello);

        this.setFv3Bounds();

        this.defineCamera('A').orthoFront(this);
        this.defineCamera('B').orthoBack(this);
        this.defineCamera('C').orthoTop(this);
        this.defineCamera('D').orthoBottom(this);
        this.defineCamera('E').orthoLeft(this);
        this.defineCamera('F').orthoRight(this);
    }
}
