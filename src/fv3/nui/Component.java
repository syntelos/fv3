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

import fv3.Bounds;
import java.nio.DoubleBuffer;

import fv3.math.AxisAngle;
import fv3.math.Matrix;
import fv3.math.Vector;

import javax.media.opengl.GL2;

/**
 * The transformation matrix of a component is applied by a containing
 * {@link fv3.Region} (see also {@link fv3.World}).
 * 
 * <h3>Operation</h3>
 * 
 * The component graph is populated in the constructors of subclasses,
 * before the Fv3 Component "init" event occurs, before the Fv3 tk
 * Animator thread has started.
 * 
 * @see fv3.Component
 */
public class Component 
    extends fv3.tk.Fv3Canvas
    implements fv3.Component
{
    protected final static Class Type = fv3.Component.class;


    public final static boolean Debug;
    static {
        boolean value = false;
        try {
            String config = System.getProperty("fv3.Component.Debug");
            value = (null != config && "true".equals(config));
        }
        catch (Exception ignore){
        }
        Debug = value;
    }


    private volatile fv3.Component parent;

    private volatile boolean alive;

    protected volatile Matrix matrix;

    protected volatile boolean visible = true;

    protected volatile Bounds bounds;


    public Component(){
        super();
        this.alive = true;
    }


    public final boolean alive(){
        return this.alive;
    }
    public void destroy(){
        this.alive = false;
        this.parent = null;
        this.matrix = null;
    }
    public final fv3.Component getFv3Parent(){
        return this.parent;
    }
    public final fv3.Component setFv3Parent(fv3.Component p){
        this.parent = p;
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
    protected final Matrix matrix(){
        Matrix m = this.matrix;
        if (null == m){
            m = new Matrix();
            this.matrix = m;
        }
        return m;
    }
    public final Matrix getFv3Matrix(){
        return this.matrix;
    }
    public final DoubleBuffer getFv3MatrixBuffer(){

        Matrix m = this.matrix;
        if (null != m)
            return m.buffer();
        else
            throw new IllegalStateException("Fv3 Matrix not found");
    }
    protected final Matrix setFv3Matrix(Matrix m){
        this.matrix = m;
        return m;
    }
    protected final Matrix setFv3Matrix(){

        return this.setFv3Matrix(new Matrix());
    }
    protected final Matrix setFv3Matrix(double[] m){

        return this.setFv3Matrix(new Matrix(m));
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
    public boolean hasFv3Bounds(){
        return (null != this.bounds);
    }
    public boolean hasNotFv3Bounds(){
        return (null == this.bounds);
    }
    public Bounds getFv3Bounds(){
        return this.bounds;
    }
    public fv3.Component setFv3Bounds(Bounds b){
        this.bounds = b;
        return this;
    }
    public fv3.Component setFv3Bounds(){
        this.bounds = new Bounds.CircumSphere(this);
        return this;
    }
    public final boolean isVisible(){
        return this.visible;
    }
    public final fv3.Component setVisible(boolean b){
        this.visible = b;
        return this;
    }
    public final fv3.Component translate(double x, double y, double z){
        this.matrix().translate(x,y,z);
        return this;
    }
    public final fv3.Component translate(Vector v){
        this.matrix().translate(v);
        return this;
    }
    public final fv3.Component scale(double s){
        this.matrix().scale(s);
        return this;
    }
    public final fv3.Component scale(double x, double y, double z){
        this.matrix().scale(x,y,z);
        return this;
    }
    public final fv3.Component rotate(AxisAngle a){
        this.matrix().rotate(a);
        return this;
    }
    /**
     * @param a Angle in radians
     */
    public final fv3.Component rotateX(double a){
        this.matrix().rotateX(a);
        return this;
    }
    /**
     * @param a Angle in radians
     */
    public final fv3.Component rotateY(double a){
        this.matrix().rotateY(a);
        return this;
    }
    /**
     * @param a Angle in radians
     */
    public final fv3.Component rotateZ(double a){
        this.matrix().rotateZ(a);
        return this;
    }
    /**
     * Angles in radians
     */
    public final fv3.Component rotate(double ax, double ay, double az){
        this.matrix().rotate(ax,ay,az);
        return this;
    }
    public void init(GL2 gl){

        if (Debug){
            Matrix m = this.matrix;
            if (null != m){
                System.out.printf("%s\n%s\n",this.getClass().getName(),m.toString("\t"));
            }
        }
    }
    public String toString(){
        return this.toString("","\n");
    }
    public String toString(String pr){
        return this.toString(pr,"\n");
    }
    public String toString(String pr, String in){

        fv3.Bounds bounds = this.bounds;

        if (bounds instanceof fv3.Bounds.CircumSphere)

            return ((fv3.Bounds.CircumSphere)bounds).toString(pr,in);
        else
            return this.getClass().getName();
    }
}
