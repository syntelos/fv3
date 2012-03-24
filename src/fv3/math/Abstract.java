/*
 * fv3.math
 * Copyright (C) 2012, John Pritchard, all rights reserved.
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
 * The classes in this package implement two principles.  First, a
 * float buffer reflects the state of the instance.  Second, an
 * instance is the mutable state of a variable.
 * 
 * <h3>Epsilon scalar radii</h3>
 * 
 * Because floating point numeric values are not uniformly distributed
 * on the real number domain
 * 
 * <pre>
 * V = s * 1.f * 2**(e*-127)
 * </pre>
 * 
 * for sign 's', exponent 'e' and fraction 'f', rounding error checks
 * must be scaled to the range of the value under test.
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
    /**
     * Scale argument epsilon.  A valid argument epsilon is larger
     * (EPS &lt; e &lt; 1.0) than the internally defined FP epsilon.
     */
    public final static float E(float v, float e){
        return (e * Math.max(1.0f,Math.abs(v)));
    }
    /**
     * Scale argument epsilon.  A valid argument epsilon is larger
     * (EPS &lt; e &lt; 1.0) than the internally defined FP epsilon.
     */
    public final static float E(float u, float v, float e){
        return (e * Math.max(1.0f,Math.max(Math.abs(u),Math.abs(v))));
    }
    /**
     * Scale FP epsilon
     */
    public final static float EPSILON(float v){
        return (EPSILON * Math.max(1.0f,Math.abs(v)));
    }
    public final static float EPSILON(float u, float v){
        return (EPSILON * Math.max(1.0f,Math.max(Math.abs(u),Math.abs(v))));
    }
    public final static boolean IZ(float v){
        return (EPS > Math.abs(v));
    }
    public final static float Z(float v){
        if (EPS > Math.abs(v))
            return ZERO;
        else
            return v;
    }
    public final static float Z1(float v){

        float av = Math.abs(v);

        if (EPS > av)
            return ZERO;
        else {
            av -= EPS_1D2;

            if (ZERO < av && EPS_D2 > av){
                if (ZERO > v)
                    return -1.0f;
                else
                    return 1.0f;
            }
        }
        return v;
    }
    public final static float Zf(float value){
        float vf = (float)value;
        if (vf == vf){
            if (EPSILON > Math.abs(vf))
                return ZERO;
            else
                return vf;
        }
        else
            throw new IllegalArgumentException(String.valueOf(value));
    }
    public final static boolean EEQ(float a, float b){

        final float d = (a - b);

        if (Float.isNaN(d))

            return false;
        else
            return (Math.abs(d) <= EPSILON(a,b));
    }
    public final static boolean NEQ(float a, float b){

        final float d = (a - b);

        if (Float.isNaN(d))

            return true;
        else
            return (Math.abs(d) > EPSILON(a,b));
    }
    public final static boolean EEQ(float a, float b, float e){

        final float d = (a - b);

        return (Math.abs(a - b) <= E(a,b,e));
    }
    public final static boolean ELE(float a, float b){

        final float d = (a - b);

        if (Math.abs(d) <= EPSILON(a,b))
            return true;
        else if (ZERO > d)
            return true;
        else
            return false;
    }
    public final static boolean ELE(float a, float b, float e){

        final float d = (a - b);

        if (Math.abs(d) <= E(a,b,e))
            return true;
        else if (ZERO > d)
            return true;
        else
            return false;
    }
    public final static boolean ELT(float a, float b, float e){

        final float d = (a - b);

        if (Math.abs(d) <= E(a,b,e))
            return false;
        else if (ZERO > d)
            return true;
        else
            return false;
    }
    public final static boolean ELT(float a, float b){

        final float d = (a - b);

        if (Math.abs(d) <= EPSILON(a,b))
            return false;
        else if (ZERO > d)
            return true;
        else
            return false;
    }
    public final static boolean EGE(float a, float b, float e){

        final float d = (a - b);

        if (Math.abs(d) <= E(a,b,e))
            return true;
        else if (ZERO < d)
            return true;
        else
            return false;
    }
    public final static boolean EGE(float a, float b){

        final float d = (a - b);

        if (Math.abs(d) <= EPSILON(a,b))
            return true;
        else if (ZERO < d)
            return true;
        else
            return false;
    }
    public final static boolean EGT(float a, float b, float e){

        final float d = (a - b);

        if (Math.abs(d) <= E(a,b,e))
            return false;
        else if (ZERO < d)
            return true;
        else
            return false;
    }
    public final static boolean EGT(float a, float b){

        final float d = (a - b);

        if (Math.abs(d) <= EPSILON(a,b))
            return false;
        else if (0.0 < d)
            return true;
        else
            return false;
    }
    public final static float DE(float d, float e){
        if (Math.abs(d) <= e)
            return ZERO;
        else
            return d;
    }
    /**
     * @param v Some real value (not NaN)
     * 
     * @return Negative one for a value less than zero, or positive
     * one for a value greater than or equal to zero.
     * 
     * @see java.lang.Math#signum(float)
     */
    public final static int Sign(float v){
        if (v != v)
            throw new IllegalArgumentException(String.valueOf(v));

        else if (ZERO > v)
            return -1;
        else
            return 1;
    }
    public final static float[] Add(float[] vertices, float[] vertex){
        if (-1 == IndexOf(vertices,vertex))
            return Cat(vertices,vertex);
        else
            return vertices;
    }
    public final static float[] Cat(float[] vertices, float[] vertex){
        if (null == vertices)
            return vertex;
        else if (null == vertex)
            return vertices;
        else {
            int len1 = vertices.length;
            int len2 = vertex.length;
            float[] copier = new float[len1+len2];
            System.arraycopy(vertices,0,copier,0,len1);
            System.arraycopy(vertex,0,copier,len1,len2);
            return copier;
        }
    }
    public final static int IndexOf(float[] vertices, float[] vertex){
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
    public final static float[] Sort(float[] vertices, int fix){
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
     * @param degrees A number of degrees
     * @return Radians, given degrees
     */
    public final static float Degrees(float degrees){
        return (degrees * Degrees);
    }



    protected Abstract(){
        super();
    }
}
