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
package fv3;

import fv3.math.Vector;
import fv3.math.Matrix;
import fv3.tk.Fv3Screen;

import lxl.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * The {@link World} manages a set of cameras, and can dynamically
 * change cameras by name.  Cameras perform both perspective and
 * modelview operations.
 * 
 * Perspective operations are performed in the "init" method, and
 * modelview operations are performed in the "display" method.  These
 * methods are named for the events of the same names that invoke them
 * from {@link World}.
 * 
 * This class endeavors to provide an essential feature set while
 * remaining amenable to modification in subclasses.
 * 
 * <h3>Operation</h3>
 * 
 * As called from {@link World}, the root of the scene- event tree,
 * the camera loads both the projection and modelview matrices in its
 * init and display methods.
 * 
 * Because the Camera modelview matrix is loaded first on the
 * modelview matrix stack, it is effectively pre-multiplied (M * C)
 * into the modelview matrix and therefore operates as a screen matrix
 * -- rather than post-multiplied (C * M) and operating as an object
 * matrix.
 * 
 * <h3>Lifecycle</h3>
 * 
 * Cameras are defined in world and component constructors, before the
 * init event for a primary camera, and before a camera's first use
 * for any secondary camera's.
 * 
 */
public class Camera
    extends java.lang.Object
{
    public enum Projection {
        Frustrum, Ortho, Perspective;
    }

    public final char name;

    public final int index;

    protected volatile double left, right, bottom = -1, top = 1, near, far, fovy = 50, vpAspect;

    protected volatile boolean vp = false;

    protected volatile int vpX, vpY, vpWidth, vpHeight;

    protected volatile Projection projection = Projection.Frustrum;

    protected volatile Matrix screenMatrix;

    private volatile boolean once = true;


    public Camera(char name){
        super();
        if ('A' <= name && 'Z' >= name){
            this.name = name;
            this.index = (name - 'A');
        }
        else
            throw new IllegalArgumentException(String.format("0x%x",(int)name));
    }
    public Camera(char name, Camera copy){
        this(name);
        if (null != copy){
            this.left = copy.left;
            this.right = copy.right;
            this.bottom = copy.bottom;
            this.top = copy.top; 
            this.near = copy.near; 
            this.far = copy.far; 
            this.fovy = copy.fovy;
            this.vpAspect = copy.vpAspect; 
            this.vp = copy.vp;
            this.vpX = copy.vpX;
            this.vpY = copy.vpY;
            this.vpWidth = copy.vpWidth;
            this.vpHeight = copy.vpHeight;
            this.projection = copy.projection;

            if (null != copy.screenMatrix)
                this.screenMatrix = new Matrix(copy.screenMatrix);

            //this.once = true//
        }
    }


    public Camera.Projection getProjection(){
        return this.projection;
    }
    public Camera setProjection(Camera.Projection p){
        if (null != p){
            this.projection = p;
            return this;
        }
        else
            throw new IllegalArgumentException();
    }
    public boolean hasScreenMatrix(){
        return (null != this.screenMatrix);
    }
    public boolean hasNotScreenMatrix(){
        return (null == this.screenMatrix);
    }
    public Matrix getScreenMatrix(){
        Matrix screenMatrix = this.screenMatrix;
        if (null == screenMatrix){
            screenMatrix = new Matrix();
            this.screenMatrix = screenMatrix;
        }
        return screenMatrix;
    }
    public Vector getEye(){

        Matrix screenMatrix = this.screenMatrix;
        if (null == screenMatrix)
            return new Vector();
        else
            return screenMatrix.getTranslation();
    }
    public Camera clear(){
        this.screenMatrix = null;
        return this;
    }
    public Camera translate(double x, double y, double z){
        this.getScreenMatrix().translate(x,y,z);
        return this;
    }
    public Camera scale(double x, double y, double z){
        this.getScreenMatrix().scale(x,y,z);
        return this;
    }
    public Camera rotate(double x, double y, double z){
        this.getScreenMatrix().rotate(x,y,z);
        return this;
    }
    public Camera frustrum(double left, double right, double bottom, double top, double near, double far){
        if (0.0 < near){
            this.projection = Projection.Frustrum;
            this.left = left;
            this.right = right;
            this.bottom = bottom;
            this.top = top;
            this.near = near;
            this.far = far;

            return this;
        }
        else
            throw new IllegalArgumentException("Near must be positive.");
    }
    public Camera frustrum(double near, double far){
        if (0.0 < near){
            this.projection = Projection.Frustrum;
            this.near = near;
            this.far = far;
            if (0 != this.vpAspect){
                this.left = -(vpAspect);
                this.right = +(vpAspect);
            }
            else {
                this.left = 0;
                this.right = 0;
            }
            this.bottom = -1.0;
            this.top = +1.0;

            return this;
        }
        else
            throw new IllegalArgumentException("Near must be positive.");
    }
    public Camera ortho(double left, double right, double bottom, double top, double near, double far){
        if (0.0 < near){
            this.projection = Projection.Ortho;
            this.left = left;
            this.right = right;
            this.bottom = bottom;
            this.top = top;
            this.near = near;
            this.far = far;

            return this;
        }
        else
            throw new IllegalArgumentException("Near must be positive.");
    }
    public Camera ortho(double near, double far){
        if (0.0 < near){
            this.projection = Projection.Ortho;
            this.near = near;
            this.far = far;
            if (0 != this.vpAspect){
                this.left = -(vpAspect);
                this.right = +(vpAspect);
            }
            this.bottom = -1.0;
            this.top = +1.0;

            return this;
        }
        else
            throw new IllegalArgumentException("Near must be positive.");
    }
    public Camera orthoFront(Component c){
        Bounds.CircumSphere s = new Bounds.CircumSphere(c);

        double x = s.midX;
        double y = s.midY;
        double z = s.midZ+s.diameter;

        return this.translate(x,y,z).ortho(1,(s.diameter+1));
    }
    public Camera orthoTop(Component c){
        Bounds.CircumSphere s = new Bounds.CircumSphere(c);

        double x = s.midX;
        double y = s.midY+s.diameter;
        double z = s.midZ;

        return this.translate(x,y,z).ortho(1,(s.diameter+1));
    }
    public Camera orthoLeft(Component c){
        Bounds.CircumSphere s = new Bounds.CircumSphere(c);

        double x = s.midX-s.diameter;
        double y = s.midY;
        double z = s.midZ;

        return this.translate(x,y,z).ortho(1,(s.diameter+1));
    }
    public Camera orthoRight(Component c){
        Bounds.CircumSphere s = new Bounds.CircumSphere(c);

        double x = s.midX+s.diameter;
        double y = s.midY;
        double z = s.midZ;

        return this.translate(x,y,z).ortho(1,(s.diameter+1));
    }
    /**
     * @param fovy Field of view (degrees) in Y
     */
    public Camera perspective(double fovy){
        if (0.0 < fovy){
            this.projection = Projection.Perspective;
            this.fovy = fovy;
            return this;
        }
        else
            throw new IllegalArgumentException("Field of view must be positive");
    }
    public String getName(){
        return String.valueOf(this.name);
    }
    public void init(GL2 gl, GLU glu){
        {

            if (this.vp){
                double vpw = this.vpWidth;
                double vph = this.vpHeight;
                this.vpAspect = (vpw / vph);

                gl.glViewport(this.vpX,this.vpY,this.vpWidth,this.vpHeight);
                System.out.printf("glViewport(%g,%g,%g,%g)\n",this.vpX,this.vpY,this.vpWidth,this.vpHeight);
            }
            else {

                Fv3Screen fv3s = Fv3Screen.Current();

                this.vpAspect = (fv3s.width / fv3s.height);

                this.vpWidth = (int)fv3s.width;
                this.vpHeight = (int)fv3s.height;
            }


            if (0 == this.left && 0 == this.right){

                this.left = -(vpAspect);
                this.right = +(vpAspect);
            }
            else if ( this.vpAspect < 1.0 ) {
                this.bottom /= this.vpAspect;
                this.top /= this.vpAspect;
            }
            else {
                this.left *= this.vpAspect; 
                this.right *= this.vpAspect;
            }
        }

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        switch (this.projection){
        case Frustrum:
            gl.glFrustum(this.left, this.right, this.bottom, this.top, this.near, this.far);
            break;
        case Ortho:
            gl.glOrtho(this.left, this.right, this.bottom, this.top, this.near, this.far);
            break;
        case Perspective:
            glu.gluPerspective(this.fovy,this.vpAspect,this.near,this.far);
            break;
        default:
            throw new IllegalStateException();
        }
    }
    public void display(GL2 gl, GLU glu){

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        
        Matrix screenMatrix = this.screenMatrix;

        if (null == screenMatrix)
            gl.glLoadIdentity();
        else
            gl.glLoadMatrixd(screenMatrix.buffer());

    }

    public String toString(){
        Vector eye = this.getEye();

        switch (this.projection){
        case Frustrum:

            return String.format("%c (%g, %g, %g) F(%g,%g,%g,%g,%g,%g)",this.name,eye.x(),eye.y(),eye.z(),
                                 this.left,this.right,this.bottom,
                                 this.top, this.near, this.far);

        case Ortho:

            return String.format("%c (%g, %g, %g) O(%g,%g,%g,%g,%g,%g)",this.name,eye.x(),eye.y(),eye.z(),
                                 this.left,this.right,this.bottom,
                                 this.top, this.near, this.far);

        case Perspective:

            return String.format("%c (%g, %g, %g) P(%g,%g,%g,%g)",this.name,eye.x(),eye.y(),eye.z(),
                                 this.fovy,this.vpAspect,this.near,this.far);

        default:
            throw new IllegalStateException();
        }
    }
}
