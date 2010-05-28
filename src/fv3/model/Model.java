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
package fv3.model;

import javax.media.opengl.GL2;

public class Model
    extends fv3.nui.Model
    implements fv3.Bounds
{

    protected volatile Object[] model;

    protected volatile int lid = -1;

    private volatile double minX, maxX, minY, maxY, minZ, maxZ, midX, midY, midZ;


    public Model(){
        super();
    }
    public Model(Object[] model){
        super();
        this.add(model);
    }


    public final boolean hasFv3Bounds(){
        return true;
    }
    public final boolean hasNotFv3Bounds(){
        return false;
    }
    public final fv3.Bounds getFv3Bounds(){
        return this;
    }
    public double getBoundsMinX(){
        return this.minX;
    }
    public double getBoundsMidX(){
        return this.midX;
    }
    public double getBoundsMaxX(){
        return this.maxX;
    }
    public double getBoundsMinY(){
        return this.minY;
    }
    public double getBoundsMidY(){
        return this.midY;
    }
    public double getBoundsMaxY(){
        return this.maxY;
    }
    public double getBoundsMinZ(){
        return this.minZ;
    }
    public double getBoundsMidZ(){
        return this.midZ;
    }
    public double getBoundsMaxZ(){
        return this.maxZ;
    }
    public final Model add(Object object){
        if (null != object){
            this.model = Object.Add(this.model,object);
            if (Object.Type.Vertex == object.getObjectType()){

                Vertex v = (Vertex)object;
                this.minX = Math.min(this.minX, v.x);
                this.maxX = Math.max(this.maxX, v.x);
                this.minY = Math.min(this.minY, v.y);
                this.maxY = Math.max(this.maxY, v.y);
                this.minZ = Math.min(this.minZ, v.z);
                this.maxZ = Math.max(this.maxZ, v.z);
            }
            this.midX = (this.maxX - this.minX);
            this.midY = (this.maxY - this.minY);
            this.midZ = (this.maxZ - this.minZ);
        }
        return this;
    }
    public final Model add(Object[] list){
        if (null != list){
            this.model = Object.Add(this.model,list);
            for (Object object: list){
                if (Object.Type.Vertex == object.getObjectType()){

                    Vertex v = (Vertex)object;
                    this.minX = Math.min(this.minX, v.x);
                    this.maxX = Math.max(this.maxX, v.x);
                    this.minY = Math.min(this.minY, v.y);
                    this.maxY = Math.max(this.maxY, v.y);
                    this.minZ = Math.min(this.minZ, v.z);
                    this.maxZ = Math.max(this.maxZ, v.z);
                }
            }
            this.midX = (this.maxX - this.minX);
            this.midY = (this.maxY - this.minY);
            this.midZ = (this.maxZ - this.minZ);
        }
        return this;
    }
    public final int getGlListCount(){
        if (-1 != this.lid)
            return 1;
        else
            return 0;
    }
    public final int getGlListId(int idx)
        throws java.lang.ArrayIndexOutOfBoundsException
    {
        if (0 == idx)
            return this.lid;
        else
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    public void init(GL2 gl){

        if (-1 == this.lid){
            this.lid = gl.glGenLists(1);
            gl.glNewList(this.lid, GL2.GL_COMPILE);

            Object[] model = this.model;
            for (int cc = 0, count = model.length; cc < count; cc++){
                model[cc].apply(gl);
            }
            gl.glEndList();

            gl.glEnable(GL2.GL_NORMALIZE);
        }
    }
    public void display(GL2 gl){
        int lid = this.lid;
        if (-1 != lid)
            gl.glCallList(lid);
    }
    public void destroy(){
        int lid = this.lid;
        if (-1 != lid){
            this.lid = -1;
            GL().glDeleteLists(lid,1);
        }
        super.destroy();
    }
}
