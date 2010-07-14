/*
 * fv3.math
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

import java.nio.DoubleBuffer;

/**
 * The classes in this package implement two principles.  First, a
 * float buffer reflects the state of the instance.  Second, an
 * instance is the mutable state of a variable.
 * 
 * @see AxisAngle
 * @see Color
 * @see Matrix
 * @see Quat
 * @see Vector
 */
public abstract class Abstract
    extends Object
    implements fv3.math.Notation
{

    public final static boolean IZ(double v){
        return (EPS > Math.abs(v));
    }
    public final static double Z(double v){
        if (EPS > Math.abs(v))
            return 0.0;
        else
            return v;
    }
    public final static double Z1(double v){

        double av = Math.abs(v);

        if (EPS > av)
            return 0.0;
        else {
            av -= EPS_1D2;

            if (0.0 < av && EPS_D2 > av){
                if (0.0 > v)
                    return -1.0;
                else
                    return 1.0;
            }
        }
        return v;
    }
    public final static float Zf(double value){
        float vf = (float)value;
        if (vf == vf){
            if (EPSILON > Math.abs(vf))
                return 0.0f;
            else
                return vf;
        }
        else
            throw new IllegalArgumentException(String.valueOf(value));
    }
    public final static boolean EEQ(double a, double b){

        double d = (a - b);

        if (Double.isNaN(d))

            return false;
        else
            return (Math.abs(d) <= EPSILON);
    }
    public final static boolean EEQ(double a, double b, double e){

        return (Math.abs(a - b) <= e);
    }
    public final static boolean ELE(double a, double b, double e){

        double d = (a - b);

        if (Math.abs(d) <= e)
            return true;
        else if (0.0 > d)
            return true;
        else
            return false;
    }
    public final static boolean ELT(double a, double b, double e){

        double d = (a - b);

        if (Math.abs(d) <= e)
            return false;
        else if (0.0 > d)
            return true;
        else
            return false;
    }
    public final static boolean EGE(double a, double b, double e){

        double d = (a - b);

        if (Math.abs(d) <= e)
            return true;
        else if (0.0 < d)
            return true;
        else
            return false;
    }
    public final static boolean EGT(double a, double b, double e){

        double d = (a - b);

        if (Math.abs(d) <= e)
            return false;
        else if (0.0 < d)
            return true;
        else
            return false;
    }
    public final static double DE(double d, double e){
        if (Math.abs(d) <= e)
            return 0.0;
        else
            return d;
    }
    /**
     * @param v Some real value (not NaN)
     * 
     * @return Negative one for a value less than zero, or positive
     * one for a value greater than or equal to zero.
     * 
     * @see java.lang.Math#signum(double)
     */
    public final static int Sign(double v){
        if (v != v)
            throw new IllegalArgumentException(String.valueOf(v));

        else if (0.0 > v)
            return -1;
        else
            return 1;
    }
    public final static double[] Add(double[] vertices, double[] vertex){
        if (-1 == IndexOf(vertices,vertex))
            return Cat(vertices,vertex);
        else
            return vertices;
    }
    public final static double[] Cat(double[] vertices, double[] vertex){
        if (null == vertices)
            return vertex;
        else if (null == vertex)
            return vertices;
        else {
            int len1 = vertices.length;
            int len2 = vertex.length;
            double[] copier = new double[len1+len2];
            System.arraycopy(vertices,0,copier,0,len1);
            System.arraycopy(vertex,0,copier,len1,len2);
            return copier;
        }
    }
    public final static int IndexOf(double[] vertices, double[] vertex){
        if (null == vertices)
            return -1;
        else if (null == vertex)
            return -1;
        else {
            int len1 = vertices.length;
            int len2 = vertex.length;
            if (len1 > len2){
                scan:
                for (int c1 = 0, c2 = 0; c1 < len1; ){
                    if (vertices[c1] == vertex[c2]){
                        final int index = c1;
                        c1 += 1;
                        c2 += 1;
                        while (c2 < len2){
                            if (vertices[c1] != vertex[c2]){
                                c1 = (index+len2);
                                continue scan;
                            }
                        }
                        return index;
                    }
                    else
                        c1 += len2;
                }
            }
            return -1;
        }
    }
    /**
     * Sort vertices on any two dimensions for rotational order.
     * 
     * @param vertices Vertex list in (X,Y,Z)+
     * @param fix Fixed or excluded dimension from {@link fv3.math.Notation} 
     * 
     * @return Argument 'vertices'
     */
    public final static double[] Sort(double[] vertices, int fix){
        if (null == vertices)
            return null;
        else {
            int len = vertices.length;
            if (0 == (len%3)){

                new QuickSort(vertices,fix);

                return vertices;
            }
            else
                throw new IllegalArgumentException(String.valueOf(len));
        }
    }


    /**
     * Unique buffer for array assigned to null when subclass array
     * (ref) changes under cloning.
     */
    protected volatile DoubleBuffer b;


    protected Abstract(){
        super();
    }


    public abstract double[] array();

    public DoubleBuffer buffer(){
        DoubleBuffer b = this.b;
        if (null == b){
            b = DoubleBuffer.wrap(this.array());
            this.b = b;
        }
        return b;
    }
}
