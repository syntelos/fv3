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
 * If the region itself will be using a transformation matrix -- as
 * for translation or rotation, ensure that it is defined before
 * calling <code>"addEnd()"</code>.  One way to do this is by calling
 * the protected method named <code>"matrix()"</code>, defined in the
 * {@link Component} class in this package.  The Gears demo
 * demonstrates this usage.
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


    public boolean pushFv3Matrix(){
        return false;
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
    public void display(GL2 gl){

        gl.glMatrixMode(GL2.GL_MODELVIEW);

        boolean ps = this.pushSpace;

        if (ps){
            gl.glPushMatrix();
            gl.glLoadMatrixd(this.getFv3MatrixBuffer());
        }

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        try {
            List<fv3.Component> children = this.children;
            if (null != children){
                Object[] list = children.array();
                for (int cc = 0, count = ((null == list)?(0):(list.length)); cc < count; cc++){
                    fv3.Component co = (fv3.Component)list[cc];
                    if (co.isVisible()){
                        boolean cp = co.pushFv3Matrix();
                        if (cp){
                            gl.glPushMatrix();
                            gl.glMultMatrixd(co.getFv3MatrixBuffer());
                        }
                        try {
                            co.display(gl);
                        }
                        finally {
                            if (cp){
                                gl.glPopMatrix();
                            }
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
    public int indexOfView(){
        List<fv3.Component> c = this.children;
        if (null != c){
            Object[] list = c.array();
            for (int cc = 0, count = ((null != list)?(list.length):(0)); cc < count; cc++){
                if (list[cc] instanceof fv3.View)
                    return cc;
            }
        }
        return -1;
    }
    public boolean hasView(){
        return (-1 != this.indexOfView());
    }
    public fv3.View getView(){
        int idx = this.indexOfView();
        if (-1 != idx)
            return (fv3.View)this.children.get(idx);
        else
            return null;
    }
    public Region setView(fv3.View view){
        int idx = this.indexOfView();
        if (-1 != idx)
            this.children.update(idx,view);
        else 
            this.children().insert(view,0);

        return this;
    }
    public final fv3.Component getCurrent(){
        return this.current;
    }
    public final Region setCurrent(fv3.Component c){
        this.current = c;
        return this;
    }
    public final fv3.Component getParent(){
        return this.parent;
    }
    public final Region setParent(fv3.Component p){
        this.parent = p;
        return this;
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
    protected final Region addEnd(){
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
        return this;
    }
    public final Region setChildren(List<fv3.Component> c){
        this.children = c;
        return this.addEnd();
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
    public final Region dropChildren(){
        this.children = null;
        this.pushSpace = false;
        return this;
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
