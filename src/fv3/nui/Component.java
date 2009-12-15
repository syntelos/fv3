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

import fv3.Bounds3f;
import java.nio.FloatBuffer;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * @see fv3.Component
 */
public class Component 
    extends fv3tk.Fv3Canvas
    implements fv3.Component
{
    protected final static Class Type = fv3.Component.class;


    private volatile boolean alive;

    private volatile Matrix4f matrix;

    private volatile FloatBuffer matrixBuffer;

    private volatile Bounds3f bounds;

    private volatile FloatBuffer boundsBuffer;

    protected volatile boolean visible = true;


    public Component(){
        super();
    }


    public void init(fv3.Region parent){
    }
    public final boolean alive(){
        return this.alive;
    }
    public void destroy(){
        this.alive = false;
    }
    public final boolean hasFv3Matrix(){
        return (null != this.matrix);
    }
    public final boolean hasNotFv3Matrix(){
        return (null == this.matrix);
    }
    protected final Matrix4f matrix(){
        Matrix4f m = this.matrix;
        if (null == m){
            m = new Matrix4f();
            m.setIdentity();
            this.matrix = m;
        }
        return m;
    }
    public final Matrix4f getFv3Matrix(){
        return this.matrix;
    }
    public final FloatBuffer getFv3MatrixBuffer(){
        FloatBuffer fb = this.matrixBuffer;
        if (null == fb){
            Matrix4f m = this.matrix;
            if (null != m){
                fb = FloatBuffer.wrap(m.array());
                this.matrixBuffer = fb;
            }
        }
        return fb;
    }
    protected final Matrix4f setFv3Matrix(Matrix4f m){
        this.matrix = m;
        this.matrixBuffer = null;
        return m;
    }
    protected final Matrix4f setFv3Matrix(){
        Matrix4f m = new Matrix4f();
        m.setIdentity();
        return this.setFv3Matrix(m);
    }
    protected final Matrix4f setFv3Matrix(float[] m){

        return this.setFv3Matrix(new Matrix4f(m));
    }
    public final boolean hasFv3Bounds(){
        return (null != this.bounds);
    }
    public final boolean hasNotFv3Bounds(){
        return (null == this.bounds);
    }
    public final Bounds3f getFv3Bounds(){
        return this.bounds;
    }
    public final FloatBuffer getFv3BoundsBuffer(){
        FloatBuffer fb = this.boundsBuffer;
        if (null == fb){
            Bounds3f b = this.bounds;
            if (null != b){
                fb = FloatBuffer.wrap(b.array());
                this.boundsBuffer = fb;
            }
        }
        return fb;
    }
    protected final Bounds3f setFv3Bounds(Bounds3f b){
        this.bounds = b;
        this.boundsBuffer = null;
        return b;
    }
    public final boolean isVisible(){
        return this.visible;
    }
    public final fv3.Component setVisible(boolean b){
        this.visible = b;
        return this;
    }
    public final fv3.Component translate(float x, float y, float z){
        this.matrix().translate(x,y,z);
        return this;
    }
    public final fv3.Component translate(Vector3f v){
        this.matrix().translate(v);
        return this;
    }
    public final fv3.Component scale(float s){
        this.matrix().scale(s);
        return this;
    }
    public final fv3.Component rotate(Quat4f q){
        this.matrix().rotate(q);
        return this;
    }
    public final fv3.Component rotate(AxisAngle4f a){
        this.matrix().rotate(a);
        return this;
    }
}
