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

import javax.media.opengl.GL2;

public class Frustrum 
    extends Component
{

    protected volatile double left, right, bottom, top, near, far;


    public Frustrum(){
        super();
    }
    public Frustrum(double left, double right, double bottom, double top, double near, double far){
        super();
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
        this.near = near;
        this.far = far;
    }

    public void init(GL2 gl) {

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustum(this.left, this.right, this.bottom, this.top, this.near, this.far);
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
        this.near = near;
    }
    public final double getFar(){
        return this.far;
    }
    public final void setFar(double far){
        this.far = far;
    }
}
