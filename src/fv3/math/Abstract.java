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

    public final static boolean IsZero(double value){
        return (EPSILON > Math.abs(value));
    }
    public final static double Z(double value){
        if (EPSILON > Math.abs(value))
            return 0.0;
        else
            return value;
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
