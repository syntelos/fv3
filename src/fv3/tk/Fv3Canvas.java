/*
 * fv3tk
 * Copyright (C) 2009  John Pritchard, jdp@syntelos.org
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fv3.tk;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.sun.javafx.newt.KeyEvent;
import com.sun.javafx.newt.MouseEvent;

/**
 * This class is an example implementation of {@link Fv3Component}.  
 * 
 * <h3>Application</h3>
 * 
 * An application creates an {@link Fv3Component} with a main
 * function.  The main function instaniates {@link Animator} with an
 * instance of the subclass, and starts the animator.
 * 
 * @see Fv3Component
 */
public class Fv3Canvas
    extends Object
    implements Fv3Component
{
    /**
     * This can be called from any of the {@link Fv3Component}
     * methods, including the input event listeners.  
     */
    public static GL2 GL(){
        return (GL2)javax.media.opengl.GLContext.getCurrentGL();
    }
    static {
        /*
         * Using NEWT not AWT
         */
        System.setProperty("java.awt.headless","true");
    }

    public final static double PI2 = (Math.PI * 2.0);


    protected volatile GLU glu;


    public Fv3Canvas(){
        super();
    }


    public void init(GL2 gl){
    }
    public final void setGLU(GLU glu){
        this.glu = glu;
    }
    public void display(GL2 gl){
    }
    protected final String[] errorStrings(GL2 gl){
        String[] re = null;
        do {
            int er = gl.glGetError();
            if (0 != er){
                String ers = this.glu.gluErrorString(er);
                if (null != ers){
                    if (null == re)
                        re = new String[]{ers};
                    else {
                        int len = re.length;
                        String[] copier = new String[len+1];
                        System.arraycopy(re,0,copier,0,len);
                        copier[len] = ers;
                        re = copier;
                    }
                }
                else
                    throw new IllegalStateException("glu Error String");
            }
            else
                break;
        }
        while (true);

        return re;
    }
    protected final void checkErrors(GL2 gl){
        String[] errors = this.errorStrings(gl);
        if (null != errors){
            StringBuilder string = new StringBuilder();

            for (int cc = 0, count = errors.length; cc < count; cc++){
                if (0 != cc)
                    string.append(", ");
                string.append(errors[cc]);
            }
            throw new IllegalStateException(string.toString());
        }
    }
    /*
     * Newt input events
     */
    public void keyPressed(KeyEvent e){
    }
    public void keyReleased(KeyEvent e){
        switch (e.getKeyCode()){
        case KeyEvent.VK_ESCAPE:
            Animator.currentAnimator().halt();
            return;
        case 0x12:
        case KeyEvent.VK_F4:
            if (e.isAltDown())
                Animator.currentAnimator().halt();
            return;
        }
    }
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyCode()){
        case KeyEvent.VK_ESCAPE:
            Animator.currentAnimator().halt();
            return;
        case KeyEvent.VK_F4:
            if (e.isAltDown())
                Animator.currentAnimator().halt();
            return;
        }
    }
    public void mouseClicked(MouseEvent e){
    }
    public void mouseEntered(MouseEvent e){
    }
    public void mouseExited(MouseEvent e){
    }
    public void mousePressed(MouseEvent e){
    }
    public void mouseReleased(MouseEvent e){
    }
    public void mouseMoved(MouseEvent e){
    }
    public void mouseDragged(MouseEvent e){
    }
    public void mouseWheelMoved(MouseEvent e){
    }

}
