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

import javax.media.opengl.GL2;

import javax.media.opengl.glu.GLU;

/**
 * 
 */
public class Camera
    extends java.lang.Object
{

    public final char name;

    public final int index;

    protected volatile double eyeX = 0, eyeY = 0, eyeZ = 0;
    protected volatile double centerX = 0, centerY = 0, centerZ = 1;
    protected volatile double upX = 0, upY = 1, upZ = 0;


    public Camera(char name){
        super();
        if ('A' <= name && 'Z' >= name){
            this.name = name;
            this.index = (name - 'A');
        }
        else
            throw new IllegalArgumentException(String.format("0x%x",(int)name));
    }


    public void moveto(double x, double y, double z){
        this.eyeX = x;
        this.eyeY = y;
        this.eyeZ = z;
    }
    public void moveby(double dx, double dy, double dz){
        this.eyeX += dx;
        this.eyeY += dy;
        this.eyeZ += dz;
    }
    public void lookto(double x, double y, double z){
        this.centerX = x;
        this.centerY = y;
        this.centerZ = z;
    }
    public void lookby(double dx, double dy, double dz){
        this.centerX += dx;
        this.centerY += dy;
        this.centerZ += dz;
    }
    public void upto(double x, double y, double z){
        this.upX = x;
        this.upY = y;
        this.upZ = z;
    }
    public String getName(){
        return String.valueOf(this.name);
    }
    public void apply(GL2 gl, GLU glu){

        gl.glLoadIdentity();

        glu.gluLookAt(this.eyeX,this.eyeY,this.eyeZ,
                      this.centerX, this.centerY, this.centerZ,
                      this.upX, this.upY, this.upZ);
    }
    public String toString(){
        return String.format("%c",this.name);
    }
}
