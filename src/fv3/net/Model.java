/*
 * Fv3
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
package fv3.net;

import fv3.Component;
import fv3.math.AxisAngle;
import fv3.math.Matrix;
import fv3.math.Vector;
import fv3.tk.Animator;

import java.net.URL;
import java.nio.DoubleBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.sun.javafx.newt.KeyEvent;
import com.sun.javafx.newt.MouseEvent;


/**
 * 
 */
public abstract class Model 
    extends lxl.net.ContentLoader
    implements fv3.Model
{
    /**
     * This can be called from any of the {@link Fv3Component}
     * methods, including the input event listeners.  
     */
    public static GL2 GL(){
        return (GL2)javax.media.opengl.GLContext.getCurrentGL();
    }




    protected volatile fv3.Component parent;

    protected volatile boolean alive = true, visible = true;

    protected volatile Matrix matrix;

    protected volatile GLU glu;


    protected Model(URL source, boolean lazy){
        super(source,lazy);
    }
    protected Model(String codebase, String path, boolean lazy){
        super(codebase,path,lazy);
    }
    protected Model(String url, boolean lazy){
        super(url,lazy);
    }


    public final boolean alive(){
        return this.alive;
    }
    public void destroy(){
        this.alive = false;
        this.parent = null;
        this.matrix = null;
    }
    public boolean needsReinit(){
        return false;
    }
    public void init(GL2 gl){
    }
    public final void setGLU(GLU glu){
        this.glu = glu;
    }
    public void display(GL2 gl){
    }
    public final fv3.Component getFv3Parent(){
        return this.parent;
    }
    public final fv3.Component setFv3Parent(fv3.Component p){
        this.parent = p;
        return this;
    }
    public boolean isVisible(){
        return this.visible;
    }
    public Component setVisible(boolean b){
        this.visible = b;
        return this;
    }
    public final boolean hasFv3Matrix(){
        return (null != this.matrix);
    }
    public final boolean hasNotFv3Matrix(){
        return (null == this.matrix);
    }
    public boolean pushFv3Matrix(){
        return (null != this.matrix);
    }
    public final Matrix getFv3Matrix(){
        return this.matrix;
    }
    public final DoubleBuffer getFv3MatrixBuffer(){
        Matrix matrix = this.matrix;
        if (null != matrix)
            return matrix.buffer();
        else
            return null;
    }
    public final Matrix composeFv3Matrix(){
        Matrix m = this.matrix;
        fv3.Component parent = this.parent;
        if (null == parent){
            if (null == m)
                return null;
            else 
                return new Matrix(m);
        }
        else {
            Matrix p = parent.composeFv3Matrix();
            if (null == m)
                return p;
            else if (null == p)
                return new Matrix(m);
            else 
                return p.mul(m);
        }
    }
    protected Matrix matrix(){
        Matrix matrix = this.matrix;
        if (null == matrix){
            matrix = new Matrix();
            this.matrix = matrix;
        }
        return matrix;
    }
    public Component translate(double x, double y, double z){
        this.matrix().translate(x,y,z);
        return this;
    }
    public Component translate(Vector v){
        this.matrix().translate(v);
        return this;
    }
    public Component scale(double s){
        this.matrix().scale(s);
        return this;
    }
    public Component scale(double x, double y, double z){
        this.matrix().scale(x,y,z);
        return this;
    }
    public Component rotate(AxisAngle a){
        this.matrix().rotate(a);
        return this;
    }
    public Component rotateX(double a){
        this.matrix().rotateX(a);
        return this;
    }
    public Component rotateY(double a){
        this.matrix().rotateY(a);
        return this;
    }
    public Component rotateZ(double a){
        this.matrix().rotateZ(a);
        return this;
    }
    public Component rotate(double ax, double ay, double az){
        this.matrix().rotate(ax,ay,az);
        return this;
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
