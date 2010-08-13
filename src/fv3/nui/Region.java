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
package fv3.nui;

import lxl.List;
import lxl.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.sun.javafx.newt.KeyEvent;
import com.sun.javafx.newt.MouseEvent;

/**
 * Contains child components, and applies their respective
 * transformation matrices on behalf of child components.
 * 
 * <h3>Operation</h3>
 * 
 * The component graph is populated in the constructors of subclasses,
 * before the Fv3 Component "init" event occurs, before the Fv3 tk
 * Animator thread has started.
 * 
 * 
 * @see fv3.Region
 */
public class Region
    extends Component
    implements fv3.Region
{


    /**
     * Target of input events
     */
    protected volatile fv3.Component current;

    private volatile List<fv3.Component> children;

    protected volatile boolean pushMatrix;


    public Region(){
        super();
    }


    public void destroy(){
        super.destroy();
        this.current = null;

        List<fv3.Component> children = this.children;
        if (null != children){
            this.children = null;

            for (fv3.Component child: children){
                
                child.destroy();
            }
        }
    }
    public boolean pushFv3Matrix(){
        return false;
    }
    public void init(GL2 gl){
        super.init(gl);

        List<fv3.Component> children = this.children;
        if (null != children && children.isNotEmpty()){

            boolean visibility = false;

            Object[] childrenAry = children.array();
            int count = ((null == childrenAry)?(0):(childrenAry.length));

            for (int cc = 0; cc < count; cc++){

                fv3.Component child = (fv3.Component)childrenAry[cc];

                if (child.isVisible()){
                    visibility = true;
                }
                child.init(gl);
            }
            this.visible = visibility;
            this.pushMatrix = (visibility && (null != this.matrix));
        }
    }
    public void display(GL2 gl){

        boolean ps = this.pushMatrix;

        if (ps){
            gl.glPushMatrix();
            gl.glMultMatrixd(this.getFv3MatrixBuffer());
        }

        try {
            List<fv3.Component> children = this.children;
            if (null != children){
                Object[] childrenAry = children.array();
                int count = ((null == childrenAry)?(0):(childrenAry.length));
                boolean cps;

                for (int cc = 0; cc < count; cc++){

                    fv3.Component child = (fv3.Component)childrenAry[cc];

                    if (child.needsReinit()){

                        child.init(gl);
                    }

                    if (child.isVisible()){

                        cps = (child.pushFv3Matrix());

                        if (cps){
                            gl.glPushMatrix();
                            gl.glMultMatrixd(child.getFv3MatrixBuffer());
                        }
                        try {
                            child.display(gl);
                        }
                        finally {
                            if (cps)
                                gl.glPopMatrix();
                        }
                    }
                }
            }
        }
        finally {
            if (ps)
                gl.glPopMatrix();
        }
    }
    public fv3.Component setFv3Bounds(){

        for (fv3.Component child: this.children){
            try {
                child.setFv3Bounds();
            }
            catch (RuntimeException ignore){
            }
        }
        return super.setFv3Bounds();
    }
    public void keyPressed(KeyEvent e){
        fv3.Component current = this.current;
        if (null != current)
            current.keyPressed(e);
        else
            super.keyPressed(e);
    }
    public void keyReleased(KeyEvent e){
        fv3.Component current = this.current;
        if (null != current)
            current.keyReleased(e);
        else
            super.keyReleased(e);
    }
    public void keyTyped(KeyEvent e) {
        fv3.Component current = this.current;
        if (null != current)
            current.keyTyped(e);
        else
            super.keyTyped(e);
    }
    public void mouseClicked(MouseEvent e){
        fv3.Component current = this.current;
        if (null != current)
            current.mouseClicked(e);
        else
            super.mouseClicked(e);
    }
    public void mouseEntered(MouseEvent e){
        fv3.Component current = this.current;
        if (null != current)
            current.mouseEntered(e);
        else
            super.mouseEntered(e);
    }
    public void mouseExited(MouseEvent e){
        fv3.Component current = this.current;
        if (null != current)
            current.mouseExited(e);
        else
            super.mouseExited(e);
    }
    public void mousePressed(MouseEvent e){
        fv3.Component current = this.current;
        if (null != current)
            current.mousePressed(e);
        else
            super.mousePressed(e);
    }
    public void mouseReleased(MouseEvent e){
        fv3.Component current = this.current;
        if (null != current)
            current.mouseReleased(e);
        else
            super.mouseReleased(e);
    }
    public void mouseMoved(MouseEvent e){
        fv3.Component current = this.current;
        if (null != current)
            current.mouseMoved(e);
        else
            super.mouseMoved(e);
    }
    public void mouseDragged(MouseEvent e){
        fv3.Component current = this.current;
        if (null != current)
            current.mouseDragged(e);
        else
            super.mouseDragged(e);
    }
    public void mouseWheelMoved(MouseEvent e){
        fv3.Component current = this.current;
        if (null != current)
            current.mouseWheelMoved(e);
        else
            super.mouseWheelMoved(e);
    }

    public final fv3.Component getCurrent(){
        return this.current;
    }
    public final Region setCurrent(fv3.Component c){
        this.current = c;
        return this;
    }
    public final List<fv3.Component> getFv3Children(){
        return this.children;
    }
    protected final List<fv3.Component> children(){
        List<fv3.Component> c = this.children;
        if (null == c){
            c = new lxl.ArrayList<fv3.Component>();
            this.children = c;
        }
        return c;
    }
    public final Region setFv3Children(List<fv3.Component> c){
        this.children = c;
        return this;
    }
    public final Region add(fv3.Component c){
        if (null != c){
            List<fv3.Component> children = this.children;
            if (null != children)
                children.add(c);
            else
                this.children().add(c);

            c.setFv3Parent(this);
        }
        return this;
    }
    public final fv3.Component get(int idx){
        List<fv3.Component> children = this.children;
        if (null != children)
            return children.get(idx);
        else
            return null;
    }
    public void clear(){

        this.dropFv3Children();
    }
    public final Region dropFv3Children(){

        List<fv3.Component> children = this.children;
        if (null != children){
            this.children = null;
            for (fv3.Component child: children){
                if (child instanceof fv3.Region){
                    fv3.Region childRegion = (fv3.Region)child;
                    childRegion.dropFv3Children();
                }
                child.destroy();
            }
        }
        this.pushMatrix = false;
        return this;
    }
}
