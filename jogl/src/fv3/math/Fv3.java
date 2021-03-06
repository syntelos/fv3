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
import java.nio.FloatBuffer;

/**
 * A buffer reflects the state of the instance.  An instance is the
 * mutable state of a variable.
 * 
 * @see AxisAngle
 * @see Color
 * @see Matrix
 * @see Quat
 * @see Vector
 */
public interface Fv3
    extends fv3.math.Notation
{

    public interface Float 
        extends Fv3
    {
        public float[] array();

        public FloatBuffer buffer();
    }

    public interface Double 
        extends Fv3
    {
        public double[] array();

        public DoubleBuffer buffer();
    }

}
