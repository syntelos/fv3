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

import fv3.Camera;
import fv3.World;
import fv3.tk.Fv3Screen;

import javax.media.opengl.GL2;

public class Frustrum 
    extends Component
{

    protected volatile boolean auto;

    protected volatile double left, right, bottom, top, near, far;

    protected World world;


    public Frustrum(){
        super();
    }
    public Frustrum(double left, double right, double bottom, double top, double near, double far){
        super();
        if (0.0 < near){
            this.left = left;
            this.right = right;
            this.bottom = bottom;
            this.top = top;
            this.near = near;
            this.far = far;
        }
        else
            throw new IllegalArgumentException("Near must be positive.");
    }
    public Frustrum(double near, double far){
        super();
        this.auto = true;
        if (0.0 < near){
            this.near = near;
            this.far = far;
        }
        else
            throw new IllegalArgumentException("Near must be positive.");
    }
    public Frustrum(World world){
        super();
        this.auto = true;
        this.world = world;
    }

    public void init(GL2 gl) {

        if (this.auto){

            Fv3Screen fv3s = Fv3Screen.Current();
            double aspect = (fv3s.width / fv3s.height);

            if (null != this.world){
                Camera camera = world.getCamera();
                camera.view(world);
                double[] c = camera.getCenter();
                double d = camera.getDiameter();
                double r = (d/2);

                this.left = c[0] - r;
                this.right = c[0] + r;
                this.bottom = c[1] - r;
                this.top = c[1] + r;
                this.near = 1;
                this.far = d+1;

                if ( aspect < 1.0 ) {
                    this.bottom /= aspect;
                    this.top /= aspect;
                }
                else {
                    this.left *= aspect; 
                    this.right *= aspect;
                }
            }
            else {
                this.left = -(aspect);
                this.bottom = -1.0;
                this.top = +1.0;
                this.right = +(aspect);
            }
        }

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustum(this.left, this.right, this.bottom, this.top, this.near, this.far);
        System.out.printf("glFrustum(%g,%g,%g,%g,%g,%g)\n",this.left, this.right, this.bottom, this.top, this.near, this.far);
    }

    public final double getLeft(){
        return this.left;
    }
    public final void setLeft(double left){
        this.left = left;
    }
    public final double getRight(){
        return this.right;
    }
    public final void setRight(double right){
        this.right = right;
    }
    public final double getBottom(){
        return this.bottom;
    }
    public final void setBottom(double bottom){
        this.bottom = bottom;
    }
    public final double getTop(){
        return this.top;
    }
    public final void setTop(double top){
        this.top = top;
    }
    public final double getNear(){
        return this.near;
    }
    public final void setNear(double near){
        if (0.0 < near)
            this.near = near;
        else
            throw new IllegalArgumentException("Near must be positive.");
    }
    public final double getFar(){
        return this.far;
    }
    public final void setFar(double far){
        this.far = far;
    }
}
