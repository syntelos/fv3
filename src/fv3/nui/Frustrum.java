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

    protected volatile float left, right, bottom, top, near, far;


    public Frustrum(){
        super();
    }
    public Frustrum(float left, float right, float bottom, float top, float near, float far){
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

    public final float getLeft(){
        return this.left;
    }
    public final void setLeft(float left){
        this.left = left;
    }
    public final float getRight(){
        return this.right;
    }
    public final void setRight(float right){
        this.right = right;
    }
    public final float getBottom(){
        return this.bottom;
    }
    public final void setBottom(float bottom){
        this.bottom = bottom;
    }
    public final float getTop(){
        return this.top;
    }
    public final void setTop(float top){
        this.top = top;
    }
    public final float getNear(){
        return this.near;
    }
    public final void setNear(float near){
        this.near = near;
    }
    public final float getFar(){
        return this.far;
    }
    public final void setFar(float far){
        this.far = far;
    }
}
