/*
 * fv3
 * Copyright (C) 2010, John Pritchard, all rights reserved.
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package fv3.math;

/**
 * This class selects all of the points in the designated plane for
 * the plane coordinate and epsilon.
 * 
 * @see QuickSort
 * @author John Pritchard
 */
public abstract class VertexArrayProfile
    extends VertexArray
{
    public static class XY
        extends VertexArrayProfile
    {
        public XY(VertexArray va, double z){
            this(va,z,EPSILON);
        }
        public XY(VertexArray va, double pz, double pe){
            super();
            if (va.hasFaces()){

                final double[] src = va.vertices;
                double[] tgt = null;

                for (int vertex = 0, count = src.length; vertex < count; ){
                    double vx = src[vertex++];
                    double vy = src[vertex++];
                    double vz = src[vertex++];
                    if (EEQ(pz,vz,pe))
                        tgt = Add(tgt,new double[]{vx,vy,vz});
                }
                this.define(Sort(tgt,Z));
            }
            else
                throw new IllegalArgumentException();
        }
    }
    public static class YZ
        extends VertexArrayProfile
    {
        public YZ(VertexArray va, double x){
            this(va,x,EPSILON);
        }
        public YZ(VertexArray va, double px, double pe){
            super();
            if (va.hasFaces()){

                final double[] src = va.vertices;
                double[] tgt = null;

                for (int vertex = 0, count = src.length; vertex < count; ){
                    double vx = src[vertex++];
                    double vy = src[vertex++];
                    double vz = src[vertex++];
                    if (EEQ(px,vx,pe))
                        tgt = Add(tgt,new double[]{vx,vy,vz});
                }
                this.define(Sort(tgt,X));
            }
            else
                throw new IllegalArgumentException();
        }
    }
    public static class ZX
        extends VertexArrayProfile
    {
        public ZX(VertexArray va, double y){
            this(va,y,EPSILON);
        }
        public ZX(VertexArray va, double py, double pe){
            super();
            if (va.hasFaces()){

                final double[] src = va.vertices;
                double[] tgt = null;

                for (int vertex = 0, count = src.length; vertex < count; ){
                    double vx = src[vertex++];
                    double vy = src[vertex++];
                    double vz = src[vertex++];
                    if (EEQ(py,vy,pe))
                        tgt = Add(tgt,new double[]{vx,vy,vz});
                }
                this.define(Sort(tgt,Y));
            }
            else
                throw new IllegalArgumentException();
        }
    }


    protected VertexArrayProfile(){
        super(VertexArray.Type.Points);
    }



    protected VertexArrayProfile define(double[] vertices){
        if (null != vertices){
            int count = (vertices.length/3);
            this.countVertices(count);
            this.setVertices(0,vertices,0,count);
        }
        return this;
    }
}
