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
 * the camera loads both the projection and modelview matrices to
 * identity in its init and display methods.
 * 
 * <h3>Lifecycle</h3>
 * 
 * Cameras have end of life event like "destroy".
 * 
 */
public class Camera
    extends java.lang.Object
{
    public enum Projection {
        Frustrum, Ortho, Perspective;
    }
    public enum ModelView {
        LookAt, None;
    }

    public final char name;

    public final int index;

    protected volatile double eyeX = 0, eyeY = 0, eyeZ = 6;
    protected volatile double centerX = 0, centerY = 0, centerZ = 1;
    protected volatile double upX = 0, upY = 1, upZ = 0;
    protected volatile double diameter = 0;

    protected volatile double left, right, bottom, top, near, far, fovy = 50, vpAspect;

    protected volatile boolean vp = false;

    protected volatile int vpX, vpY, vpWidth, vpHeight;

    protected volatile Projection projection = Projection.Frustrum;

    protected volatile ModelView modelView = ModelView.LookAt;

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
            this.eyeX = copy.eyeX;
            this.eyeY = copy.eyeY;
            this.eyeZ = copy.eyeZ;
            this.centerX = copy.centerX;
            this.centerY = copy.centerY;
            this.centerZ = copy.centerZ;
            this.upX = copy.upX;
            this.upY = copy.upY;
            this.upZ = copy.upZ;
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
            this.modelView = copy.modelView;
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
    public Camera.ModelView getModelView(){
        return this.modelView;
    }
    public Camera setModelView(Camera.ModelView p){
        if (null != p){
            this.modelView = p;
            return this;
        }
        else
            throw new IllegalArgumentException();
    }
    public Camera modelViewNone(){
        this.modelView = Camera.ModelView.None;
        return this;
    }
    public Camera modelViewLookAt(){
        this.modelView = Camera.ModelView.LookAt;
        return this;
    }
    public double[] getEye(){
        return new double[]{this.eyeX,this.eyeY,this.eyeZ};
    }
    public double[] getCenter(){
        return new double[]{this.centerX,this.centerY,this.centerZ};
    }
    public double[] getUp(){
        return new double[]{this.upX,this.upY,this.upZ};
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

        if (component.hasFv3Bounds())

            return this.diameter(component.getFv3Bounds());

        else if (component instanceof Region){
            double minX = 0, maxX = 0;
            double minY = 0, maxY = 0;
            double minZ = 0, maxZ = 0;

            boolean once = true;

            Region region = (Region)component;
            for (Component child : region.getChildren()){
                if (child.hasFv3Bounds()){
                    Bounds bounds = child.getFv3Bounds();
                    if (once){
                        once = false;
                        minX = bounds.getBoundsMinX();
                        maxX = bounds.getBoundsMaxX();
                        minY = bounds.getBoundsMinY();
                        maxY = bounds.getBoundsMaxY();
                        minZ = bounds.getBoundsMinZ();
                        maxZ = bounds.getBoundsMaxZ();
                    }
                    else {
                        minX = Math.min(minX,bounds.getBoundsMinX());
                        maxX = Math.max(maxX,bounds.getBoundsMaxX());
                        minY = Math.min(minY,bounds.getBoundsMinY());
                        maxY = Math.max(maxY,bounds.getBoundsMaxY());
                        minZ = Math.min(minZ,bounds.getBoundsMinZ());
                        maxZ = Math.max(maxZ,bounds.getBoundsMaxZ());
                    }
                }
            }
            if (!once){

                double d = Vector.Diameter(minX, maxX,
                                           minY, maxY,
                                           minZ, maxZ);

                return this.setDiameter(d);
            }
            else
                throw new IllegalStateException("No bounds found in region");
        }
        else
            throw new IllegalArgumentException("Component has no bounds and is not region");
    }
    /**
     * Called before "view" 
     */
    public Camera moveto(double x, double y, double z){
        this.eyeX = x;
        this.eyeY = y;
        this.eyeZ = z;
        return this.target();
    }
    public Camera moveby(double dx, double dy, double dz){
        this.eyeX += dx;
        this.eyeY += dy;
        this.eyeZ += dz;
        return this.target();
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

        if (component.hasFv3Bounds())

            return this.view(component.getFv3Bounds());

        else if (component instanceof Region){
            double minX = 0, maxX = 0;
            double minY = 0, maxY = 0;
            double minZ = 0, maxZ = 0;

            boolean once = true;

            Region region = (Region)component;
            for (Component child : region.getChildren()){
                if (child.hasFv3Bounds()){
                    Bounds bounds = child.getFv3Bounds();
                    if (once){
                        once = false;
                        minX = bounds.getBoundsMinX();
                        maxX = bounds.getBoundsMaxX();
                        minY = bounds.getBoundsMinY();
                        maxY = bounds.getBoundsMaxY();
                        minZ = bounds.getBoundsMinZ();
                        maxZ = bounds.getBoundsMaxZ();
                    }
                    else {
                        minX = Math.min(minX,bounds.getBoundsMinX());
                        maxX = Math.max(maxX,bounds.getBoundsMaxX());
                        minY = Math.min(minY,bounds.getBoundsMinY());
                        maxY = Math.max(maxY,bounds.getBoundsMaxY());
                        minZ = Math.min(minZ,bounds.getBoundsMinZ());
                        maxZ = Math.max(maxZ,bounds.getBoundsMaxZ());
                    }
                }
            }
            if (!once){
                double midX = ((maxX - minX)/2)+minX;
                double midY = ((maxY - minY)/2)+minY;
                double midZ = ((maxZ - minZ)/2)+minZ;

                double d = Vector.Diameter(minX, maxX,
                                           minY, maxY,
                                           minZ, maxZ);

                return this.view(midX,midY,midZ,d);
            }
            else
                throw new IllegalStateException("No bounds found in region");
        }
        else
            throw new IllegalArgumentException("Component has no bounds and is not region");
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
        this.centerX = x;
        this.centerY = y;
        this.centerZ = z;
        return this.target();
    }
    public Camera lookby(double dx, double dy, double dz){
        return this.lookto((this.centerX + dx),(this.centerY + dy),(this.centerZ + dz));
    }
    public Camera upto(double x, double y, double z){
        this.upX = x;
        this.upY = y;
        this.upZ = z;
        return this;
    }
    public Camera target(){

        if (0 != this.diameter){

            double radius = (this.diameter/2);
            double target = new Vector(this.eyeX,this.eyeY,this.eyeZ).distance(new Vector(this.centerX,this.centerY,this.centerZ));

            this.left = this.centerX - radius;
            this.right = this.centerX + radius;
            this.bottom = this.centerY - radius;
            this.top = this.centerY + radius;
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
        
        gl.glLoadIdentity();

        switch (this.modelView){
        case LookAt:
            glu.gluLookAt(this.eyeX,this.eyeY,this.eyeZ,
                          this.centerX, this.centerY, this.centerZ,
                          this.upX, this.upY, this.upZ);

            if (this.once){
                this.once = false;
                System.out.printf("gluLookAt(%g,%g,%g,%g,%g,%g,%g,%g,%g)\n",this.eyeX,this.eyeY,this.eyeZ,
                                  this.centerX, this.centerY, this.centerZ,
                                  this.upX, this.upY, this.upZ);
            }
            break;
        case None:
            break;
        default:
            throw new IllegalStateException();
        }
    }

    public String toString(){
        return String.format("%c (%g,%g,%g)->(%g,%g,%g);(%g,%g,%g)",this.name,
                             this.eyeX,this.eyeY,this.eyeZ,
                             this.centerX,this.centerY,this.centerZ,
                             this.upX,this.upY,this.upZ);
    }
}
