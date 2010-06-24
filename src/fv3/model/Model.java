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

import fv3.math.Matrix;

import javax.media.opengl.GL2;

public class Model
    extends fv3.nui.Component
    implements fv3.Model
{
    public static class Bounds
        extends java.lang.Object
        implements fv3.Bounds
    {
        public final double minX, maxX, minY, maxY, minZ, maxZ, midX, midY, midZ;

        public Bounds(Model m){
            super();
            double minX = 0, maxX = 0;
            double minY = 0, maxY = 0;
            double minZ = 0, maxZ = 0;

            Matrix mm = m.composeFv3Matrix();
            if (null != mm){

                for (int idx = 0, cnt = m.size(); idx < cnt; idx++){

                    fv3.model.Object object = m.get(idx);

                    if (fv3.model.Object.Type.Vertex == object.getObjectType()){

                        double[] v = mm.transform(((Vertex)object).copy());

                        double x = v[X];
                        double y = v[Y];
                        double z = v[Z];

                        if (x != x)
                            throw new IllegalStateException();
                        else {
                            minX = Math.min(minX, x);
                            maxX = Math.max(maxX, x);
                        }
                        if (y != y)
                            throw new IllegalStateException();
                        else {
                            minY = Math.min(minY, y);
                            maxY = Math.max(maxY, y);
                        }
                        if (z != z)
                            throw new IllegalStateException();
                        else {
                            minZ = Math.min(minZ, z);
                            maxZ = Math.max(maxZ, z);
                        }
                    }
                }
            }
            else {

                for (int idx = 0, cnt = m.size(); idx < cnt; idx++){

                    fv3.model.Object object = m.get(idx);

                    if (fv3.model.Object.Type.Vertex == object.getObjectType()){

                        Vertex v = (Vertex)object;

                        double x = v.x;
                        double y = v.y;
                        double z = v.z;

                        if (x != x)
                            throw new IllegalStateException();
                        else {
                            minX = Math.min(minX, x);
                            maxX = Math.max(maxX, x);
                        }
                        if (y != y)
                            throw new IllegalStateException();
                        else {
                            minY = Math.min(minY, y);
                            maxY = Math.max(maxY, y);
                        }
                        if (z != z)
                            throw new IllegalStateException();
                        else {
                            minZ = Math.min(minZ, z);
                            maxZ = Math.max(maxZ, z);
                        }
                    }
                }
            }

            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;

            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;

            this.midX = (minX + maxX)/2.0;
            this.midY = (minY + maxY)/2.0;
            this.midZ = (minZ + maxZ)/2.0;
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
        public String toString(){
            return this.toString("","\n");
        }
        public String toString(String pr){
            return this.toString(pr,"\n");
        }
        public String toString(String pr, String in){
            if (null == pr)
                pr = "";
            if (null == in)
                in = "";

            return String.format("%s%30.26f %30.26f %30.26f%s%s%30.26f %30.26f %30.26f%s%s%30.26f %30.26f %30.26f", 
                                 pr, minX, minY, minZ, 
                                 in, pr, midX, midY, midZ, 
                                 in, pr, maxX, maxY, maxZ);
        }
    }

    protected volatile Object[] model;

    protected volatile int lid = -1;


    public Model(){
        super();
    }
    public Model(Object[] model){
        super();
        this.add(model);
    }


    public final boolean hasFv3Bounds(){
        return (0 != this.size());
    }
    public final boolean hasNotFv3Bounds(){
        return (0 == this.size());
    }
    public final fv3.Bounds getFv3Bounds(){
        fv3.Bounds bounds = this.bounds;
        if (null == bounds){
            bounds = new Model.Bounds(this);
            this.bounds = bounds;
        }
        return bounds;
    }
    public final fv3.Component setFv3Bounds(){
        this.bounds = new Model.Bounds(this);
        return this;
    }
    public final int size(){
        Object[] model = this.model;
        if (null == model)
            return 0;
        else
            return model.length;
    }
    public final fv3.model.Object get(int idx){
        return this.model[idx];
    }
    public final Model add(fv3.model.Object object){
        if (null != object)
            this.model = Object.Add(this.model,object);

        return this;
    }
    public final Model add(fv3.model.Object[] list){
        if (null != list)
            this.model = Object.Add(this.model,list);

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

        super.init(gl);

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

        super.display(gl);

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
    public String toString(){
        return this.toString("","\n");
    }
    public String toString(String pr){
        return this.toString(pr,"\n");
    }
    public String toString(String pr, String in){

        fv3.Bounds bounds = this.bounds;
        if (bounds instanceof Model.Bounds)

            return ((Model.Bounds)bounds).toString(pr,in);

        else if (bounds instanceof fv3.Bounds.CircumSphere)

            return ((fv3.Bounds.CircumSphere)bounds).toString(pr,in);
        else
            return this.getClass().getName();
    }
}
