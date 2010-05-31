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

    protected volatile Vector eye, center;

    protected volatile double diameter = 0;

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
            this.eye = copy.eye;
            this.center = copy.center;
            this.diameter = copy.diameter;
            this.left = copy.left;
            this.right = copy.right;
            this.bottom = copy.bottom;
            this.top = copy.top; 
            this.near = copy.near; 
            this.far = copy.far; 
            this.fovy = copy.fovy;
            this.vp = copy.vp;
            this.vpX = copy.vpX;
            this.vpY = copy.vpY;
            this.vpWidth = copy.vpWidth;
            this.vpHeight = copy.vpHeight;
            this.vpAspect = copy.vpAspect; 
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
        return this.eye;
    }
    public Vector getCenter(){
        return this.center;
    }
    public boolean hasDiameter(){
        return (0 != this.diameter);
    }
    public double getDiameter(){
        return this.diameter;
    }
    public Camera setDiameter(double d){
        if (0.0 < d){
            this.diameter = d;
            return this;
        }
        else
            throw new IllegalArgumentException("Diameter must be positive");
    }
    public Camera diameter(Bounds bounds){

        return this.setDiameter(Vector.Diameter(bounds));
    }
    public Camera diameter(Component component){

        Bounds.CircumSphere s = new Bounds.CircumSphere(component);

        return this.setDiameter(s.diameter);
    }
    public Camera diameter(Bounds.CircumSphere s){

        return this.setDiameter(s.diameter);
    }
    /**
     * Called before "view" 
     */
    public Camera moveto(double x, double y, double z){
        this.eye = new Vector(x,y,z);
        return this.project();
    }
    public Camera moveby(double dx, double dy, double dz){
        if (null == this.eye)
            this.eye = new Vector(dx,dy,dz);
        else
            this.eye.add( dx, dy, dz);

        return this.project();
    }
    public Camera view(double x, double y, double z, double d){
        this.diameter = d;
        return this.lookto(x,y,z);
    }
    public Camera view(Bounds bounds){

        double d = Vector.Diameter(bounds);

        return this.view(bounds.getBoundsMidX(),bounds.getBoundsMidY(),bounds.getBoundsMidZ(),d);
    }
    public Camera view(Component component){

        return this.view(new Bounds.CircumSphere(component));
    }
    public Camera view(Bounds.CircumSphere s){

        return this.view(s.midX,s.midY,s.midZ,s.diameter);
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
    /**
     * Called after "view", updates the projection.
     */
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
        }
        else
            throw new IllegalArgumentException("Near must be positive.");
        return this;
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
    /**
     * Called after "view", updates the projection.
     */
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
        }
        else
            throw new IllegalArgumentException("Near must be positive.");
        return this;
    }
    public Camera orthoFront(Component c){
        Bounds.CircumSphere s = new Bounds.CircumSphere(c);
        ///////////////////////////////////
        ///////////////////////////////////
        ///////////////////////////////////
        return this.ortho(1,(s.diameter+1));
    }
    public Camera orthoTop(Component c){
        Bounds.CircumSphere s = new Bounds.CircumSphere(c);
        ///////////////////////////////////
        ///////////////////////////////////
        ///////////////////////////////////
        return this.ortho(1,(s.diameter+1));
    }
    public Camera orthoLeft(Component c){
        Bounds.CircumSphere s = new Bounds.CircumSphere(c);
        ///////////////////////////////////
        ///////////////////////////////////
        ///////////////////////////////////
        return this.ortho(1,(s.diameter+1));
    }
    public Camera orthoRight(Component c){
        Bounds.CircumSphere s = new Bounds.CircumSphere(c);
        ///////////////////////////////////
        ///////////////////////////////////
        ///////////////////////////////////
        return this.ortho(1,(s.diameter+1));
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
    /**
     * Called after "setDiameter".
     */
    public Camera lookto(double x, double y, double z){

        this.center = new Vector(x,y,z);

        return this.project();
    }
    public Camera lookby(double dx, double dy, double dz){
        if (null == this.center)
            this.center = new Vector(dx,dy,dz);
        else
            this.center.add(dx,dy,dz);

        return this.project();
    }
    public Camera project(){

        if (0 != this.diameter && null != this.eye && null != this.center){

            double radius = (this.diameter/2);
            double target = this.eye.distance(this.center);

            double cx = this.center.x();
            double cy = this.center.y();

            this.left = cx - radius;
            this.right = cx + radius;
            this.bottom = cy - radius;
            this.top = cy + radius;
            this.near = 1;
            this.far = Math.max( (this.diameter+1), (target+radius+1));
        }
        return this;
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
            System.out.printf("glFrustrum(%g,%g,%g,%g,%g,%g)\n",this.left,this.right,this.bottom,
                              this.top, this.near, this.far);
            break;
        case Ortho:
            gl.glOrtho(this.left, this.right, this.bottom, this.top, this.near, this.far);
            System.out.printf("glOrtho(%g,%g,%g,%g,%g,%g)\n",this.left,this.right,this.bottom,
                              this.top, this.near, this.far);
            break;
        case Perspective:
            glu.gluPerspective(this.fovy,this.vpAspect,this.near,this.far);
            System.out.printf("gluPerspective(%g,%g,%g,%g)\n",this.fovy,this.vpAspect,this.near,this.far);
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
        return String.valueOf(this.name);
    }
}
