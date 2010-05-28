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

import lxl.List;

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

    protected volatile double eyeX = 0, eyeY = 0, eyeZ = 6;
    protected volatile double centerX = 0, centerY = 0, centerZ = 1;
    protected volatile double upX = 0, upY = 1, upZ = 0;
    protected volatile double diameter = 0;

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


    public void view(double x, double y, double z, double d){
        this.eyeX = 0;
        this.eyeY = 0;
        this.eyeZ = (d/2);
        this.centerX = x;
        this.centerY = y;
        this.centerZ = z;
        this.upX = 0;
        this.upY = 1;
        this.upZ = 0;
        this.diameter = d;
    }
    public void view(Bounds bounds){
        double dx = bounds.getBoundsMaxX()-bounds.getBoundsMinX();
        double dy = bounds.getBoundsMaxY()-bounds.getBoundsMinY();
        double dz = bounds.getBoundsMaxZ()-bounds.getBoundsMinZ();
        double d = Math.max(dx,Math.max(dy,dz));
        this.view(bounds.getBoundsMidX(),bounds.getBoundsMidY(),bounds.getBoundsMidZ(),d);
    }
    public void view(Component component){
        if (component.hasFv3Bounds())
            this.view(component.getFv3Bounds());

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
                double dX = (maxX - minX);
                double dY = (maxY - minY);
                double dZ = (maxZ - minZ);
                double midX = (dX/2)+minX;
                double midY = (dY/2)+minY;
                double midZ = (dZ/2)+minZ;

                double d = Math.max(dX,Math.max(dY,dZ));

                this.view(midX,midY,midZ,d);
            }
            else
                throw new IllegalStateException("No bounds found in region.");
        }
        else
            throw new IllegalArgumentException("Component has no bounds and is not region.");
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

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        
        gl.glLoadIdentity();

        glu.gluLookAt(this.eyeX,this.eyeY,this.eyeZ,
                      this.centerX, this.centerY, this.centerZ,
                      this.upX, this.upY, this.upZ);

        if (this.once){
            this.once = false;
            System.out.printf("gluLookAt(%g,%g,%g,%g,%g,%g,%g,%g,%g)\n",this.eyeX,this.eyeY,this.eyeZ,
                              this.centerX, this.centerY, this.centerZ,
                              this.upX, this.upY, this.upZ);
        }
    }
    public String toString(){
        return String.format("%c (%g,%g,%g)->(%g,%g,%g);(%g,%g,%g)",this.name,
                             this.eyeX,this.eyeY,this.eyeZ,
                             this.centerX,this.centerY,this.centerZ,
                             this.upX,this.upY,this.upZ);
    }
    public boolean hasDiameter(){
        return (0 != this.diameter);
    }
    public double getDiameter(){
        return this.diameter;
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
}
