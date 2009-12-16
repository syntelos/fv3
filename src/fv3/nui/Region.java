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
 * Ensure that a matrix is defined before setting children for the
 * correct application of the matrix to the scene.  This practice will
 * ensure correct operations with respect to coordinate space and
 * visibility.
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

    private volatile fv3.Component parent;

    private volatile List<fv3.Component> children;

    private volatile boolean pushSpace;

    private volatile Map<Class<lxl.Component>,lxl.Component> graphParent;

    private volatile Map<Class<lxl.Component>,List<lxl.Component>> graphChildren;


    public Region(){
        super();
    }


    public void init(fv3.Region region){
        List<fv3.Component> children = this.children;
        if (null != children){
            Object[] list = children.array();
            for (int cc = 0, count = ((null == list)?(0):(list.length)); cc < count; cc++){
                fv3.Component co = (fv3.Component)list[cc];
                co.init(region);
            }
        }
    }
    public void init(GL2 gl){

        List<fv3.Component> children = this.children;
        if (null != children){
            Object[] list = children.array();
            for (int cc = 0, count = ((null == list)?(0):(list.length)); cc < count; cc++){
                fv3.Component co = (fv3.Component)list[cc];
                co.init(gl);
            }
        }
    }
    public void step(long time, long dt){
        List<fv3.Component> children = this.children;
        if (null != children){
            Object[] list = children.array();
            for (int cc = 0, count = ((null == list)?(0):(list.length)); cc < count; cc++){
                fv3.Component co = (fv3.Component)list[cc];
                co.step(time,dt);
            }
        }
    }
    public void display(GL2 gl){

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        if (this.pushSpace){
            gl.glPushMatrix();
            gl.glLoadMatrixf(this.getFv3MatrixBuffer());
        }

        try {
            List<fv3.Component> children = this.children;
            if (null != children){
                Object[] list = children.array();
                for (int cc = 0, count = ((null == list)?(0):(list.length)); cc < count; cc++){
                    fv3.Component co = (fv3.Component)list[cc];
                    if (co.isVisible()){
                        boolean cm = co.hasFv3Matrix();
                        if (cm){
                            gl.glPushMatrix();
                            gl.glLoadMatrixf(co.getFv3MatrixBuffer());
                        }
                        try {
                            co.display(gl);
                        }
                        finally {
                            if (cm){
                                gl.glPopMatrix();
                            }
                        }
                    }
                }
            }
        }
        finally {
            if (this.pushSpace)
                gl.glPopMatrix();
        }
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
    public final void setCurrent(fv3.Component c){
        this.current = c;
    }
    public final fv3.Component getParent(){
        return this.parent;
    }
    public final void setParent(fv3.Component p){
        this.parent = p;
    }
    public final List<fv3.Component> getChildren(){
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
    protected final List<fv3.Component> addBegin(){
        return this.children();
    }
    protected final void addEnd(){
        List<fv3.Component> c = this.children;
        this.pushSpace = (null != c && (!c.isEmpty()) && this.hasFv3Matrix());
        if (this.pushSpace){
            boolean visibility = false;
            for (fv3.Component child : this.children){
                if (child.isVisible()){
                    visibility = true;
                    break;
                }
            }
            this.visible = visibility;
        }
    }
    public final void setChildren(List<fv3.Component> c){
        this.children = c;
        this.addEnd();
    }
    /**
     * After adding, call {@link #addEnd()}
     */
    protected final Region add(fv3.Component c){
        if (null != c){
            List<fv3.Component> children = this.children;
            if (null != children)
                children.add(c);
            else
                this.children().add(c);
        }
        return this;
    }
    public final void dropChildren(){
        this.children = null;
        this.pushSpace = false;
    }
    /*
     * lxl graph
     */
    protected final Map<Class<lxl.Component>,lxl.Component> graphParent(){
        Map<Class<lxl.Component>,lxl.Component> graphParent = this.graphParent;
        if (null == graphParent){
            graphParent = new Map<Class<lxl.Component>,lxl.Component>();
            this.graphParent = graphParent;
        }
        return graphParent;
    }
    protected final Map<Class<lxl.Component>,List<lxl.Component>> graphChildren(){
        Map<Class<lxl.Component>,List<lxl.Component>> graphChildren = this.graphChildren;
        if (null == graphChildren){
            graphChildren = new Map<Class<lxl.Component>,List<lxl.Component>>();
            this.graphChildren = graphChildren;
        }
        return graphChildren;
    }
    public final lxl.Component getHierParent(Class<lxl.Component> in){
        if (Component.Type == in)
            return this.parent;
        else {
            Map<Class<lxl.Component>,lxl.Component> graphParent = this.graphParent;
            if (null != graphParent)
                return (lxl.Component)graphParent.get(in);
            else
                return null;
        }
    }
    public final void setHierParent(Class<lxl.Component> in, lxl.Component next){
        if (Component.Type == in)
            this.parent = (fv3.Component)next;
        else
            this.graphParent().put(in,next);
    }
    public final void dropHierParent(Class<lxl.Component> in){
        if (Component.Type == in)
            this.parent = null;
        else {
            Map<Class<lxl.Component>,lxl.Component> graphParent = this.graphParent;
            if (null != graphParent)
                graphParent.remove(in);
        }
    }
    public final void dropHierParent(){
        Map<Class<lxl.Component>,lxl.Component> graphParent = this.graphParent;
        if (null != graphParent)
            graphParent.clear();
    }
    public final List<lxl.Component> getHierChildren(Class<lxl.Component> in){
        if (Component.Type == in){
            List children = this.children;
            return (List<lxl.Component>)children;
        }
        else {
            Map<Class<lxl.Component>,List<lxl.Component>> graphChildren = this.graphChildren;
            if (null != graphChildren)
                return (List<lxl.Component>)graphChildren.get(in);
            else
                return null;
        }
    }
    public final void setHierChildren(Class<lxl.Component> in, List<lxl.Component> next){
        if (Component.Type == in){
            List children = next;
            this.children = (List<fv3.Component>)children;
            this.addEnd();
        }
        else
            this.graphChildren().put(in,next);
    }
    public final void dropHierChildren(Class<lxl.Component> in){
        if (Component.Type == in){
            this.children = null;
            this.pushSpace = false;
        }
        else {
            Map<Class<lxl.Component>,List<lxl.Component>> graphChildren = this.graphChildren;
            if (null != graphChildren)
                graphChildren.remove(in);
        }
    }
    public final void dropHierChildren(){
        this.children = null;
        this.pushSpace = false;
        Map<Class<lxl.Component>,List<lxl.Component>> graphChildren = this.graphChildren;
        if (null != graphChildren)
            graphChildren.clear();
    }
}
