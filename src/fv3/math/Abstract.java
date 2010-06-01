/*
 * fv3.math
 * Copyright (c) 2009 John Pritchard, all rights reserved.
 *     
 * This program is free  software: you can redistribute it and/or modify 
 * it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program  is  distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU Lesser General Public License for more details.
 * 
 * You should  have received a copy of the GNU Lesser General Public License
 * along with  this program; If not, see <http://www.gnu.org/licenses/>. 
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
{
    public final static double EPSILON = (1e-8);
    public final static double PI = Math.PI;
    public final static double PI2 = (Math.PI/2.0);

    public final static boolean IsZero(double value){
        return (EPSILON > Math.abs(value));
    }
    public final static double Z(double value){
        if (EPSILON > Math.abs(value))
            return 0.0;
        else
            return value;
    }


    private volatile DoubleBuffer b;


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
