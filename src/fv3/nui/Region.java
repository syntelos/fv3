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
     * This is as close as we can get to a Comparable Class of
     * lxl.Component for lxl.Map.
     */
    public final static class ClassComponent
        extends java.lang.Object
        implements java.lang.Comparable<ClassComponent>
    {

        public final Class<lxl.Component> jclass;


        public ClassComponent(Class<lxl.Component> jclass){
            super();
            if (null != jclass)
                this.jclass = jclass;
            else
                throw new IllegalArgumentException();
        }


        public String getName(){
            return this.jclass.getName();
        }
        public String toString(){
            return this.jclass.toString();
        }
        public int hashCode(){
            return this.jclass.hashCode();
        }
        public boolean equals(Object that){
            if (this == that)
                return true;
            else if (null == that)
                return false;
            else 
                return this.toString().equals(that.toString());
        }
        public int compareTo(ClassComponent that){
            return this.toString().compareTo(that.toString());
        }
    }

    /**
     * Target of input events
     */
    protected volatile fv3.Component current;

    private volatile fv3.Component parent;

    private volatile List<fv3.Component> children;

    private volatile boolean pushMatrix;

    private volatile Map<ClassComponent,lxl.Component> graphParent;

    private volatile Map<ClassComponent,List<lxl.Component>> graphChildren;


    public Region(){
        super();
    }


    public boolean pushFv3Matrix(){
        return false;
    }
    public void init(GL2 gl){

        List<fv3.Component> children = this.children;
        if (null != children && children.isNotEmpty()){

            this.pushMatrix = this.hasFv3Matrix();

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
        }
    }
    public void display(GL2 gl){

        gl.glMatrixMode(GL2.GL_MODELVIEW);

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

                for (int cc = 0; cc < count; cc++){

                    fv3.Component child = (fv3.Component)childrenAry[cc];

                    if (child.isVisible()){

                        child.display(gl);
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
    public final Region setChildren(List<fv3.Component> c){
        this.children = c;
        return this;
    }
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
        this.pushMatrix = false;
        return this;
    }
    /*
     * lxl graph
     */
    protected final Map<ClassComponent,lxl.Component> graphParent(){
        Map<ClassComponent,lxl.Component> graphParent = this.graphParent;
        if (null == graphParent){
            graphParent = new Map<ClassComponent,lxl.Component>();
            this.graphParent = graphParent;
        }
        return graphParent;
    }
    protected final Map<ClassComponent,List<lxl.Component>> graphChildren(){
        Map<ClassComponent,List<lxl.Component>> graphChildren = this.graphChildren;
        if (null == graphChildren){
            graphChildren = new Map<ClassComponent,List<lxl.Component>>();
            this.graphChildren = graphChildren;
        }
        return graphChildren;
    }
    public final lxl.Component getHierParent(Class<lxl.Component> in){
        if (Component.Type == in)
            return this.parent;
        else {
            Map<ClassComponent,lxl.Component> graphParent = this.graphParent;
            if (null != graphParent)
                return (lxl.Component)graphParent.get(new ClassComponent(in));
            else
                return null;
        }
    }
    public final void setHierParent(Class<lxl.Component> in, lxl.Component next){
        if (Component.Type == in)
            this.parent = (fv3.Component)next;
        else
            this.graphParent().put(new ClassComponent(in),next);
    }
    public final void dropHierParent(Class<lxl.Component> in){
        if (Component.Type == in)
            this.parent = null;
        else {
            Map<ClassComponent,lxl.Component> graphParent = this.graphParent;
            if (null != graphParent)
                graphParent.remove(new ClassComponent(in));
        }
    }
    public final void dropHierParent(){
        Map<ClassComponent,lxl.Component> graphParent = this.graphParent;
        if (null != graphParent)
            graphParent.clear();
    }
    public final List<lxl.Component> getHierChildren(Class<lxl.Component> in){
        if (Component.Type == in){
            List children = this.children;
            return (List<lxl.Component>)children;
        }
        else {
            Map<ClassComponent,List<lxl.Component>> graphChildren = this.graphChildren;
            if (null != graphChildren)
                return (List<lxl.Component>)graphChildren.get(new ClassComponent(in));
            else
                return null;
        }
    }
    public final void setHierChildren(Class<lxl.Component> in, List<lxl.Component> next){
        if (Component.Type == in){
            List children = next;
            this.children = (List<fv3.Component>)children;
        }
        else
            this.graphChildren().put(new ClassComponent(in),next);
    }
    public final void dropHierChildren(Class<lxl.Component> in){
        if (Component.Type == in){
            this.children = null;
            this.pushMatrix = false;
        }
        else {
            Map<ClassComponent,List<lxl.Component>> graphChildren = this.graphChildren;
            if (null != graphChildren)
                graphChildren.remove(new ClassComponent(in));
        }
    }
    public final void dropHierChildren(){
        this.children = null;
        this.pushMatrix = false;
        Map<ClassComponent,List<lxl.Component>> graphChildren = this.graphChildren;
        if (null != graphChildren)
            graphChildren.clear();
    }
}
