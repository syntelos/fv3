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
 * 
 * @see AxisAngle
 * @see Matrix
 * @see Quat
 * @see Vector
 */
public abstract class AbstractDouble
    extends Abstract
    implements Fv3.Double
{
    /**
     * Unique buffer for array assigned to null when subclass array
     * (ref) changes under cloning.
     */
    protected volatile DoubleBuffer b;


    protected AbstractDouble(){
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
