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

import java.nio.FloatBuffer;

/**
 * 
 * @see Color
 */
public abstract class AbstractFloat
    extends Abstract
    implements Fv3.Float
{
    /**
     * Unique buffer for array assigned to null when subclass array
     * (ref) changes under cloning.
     */
    protected volatile FloatBuffer b;


    protected AbstractFloat(){
        super();
    }


    public abstract float[] array();

    public FloatBuffer buffer(){
        FloatBuffer b = this.b;
        if (null == b){
            b = FloatBuffer.wrap(this.array());
            this.b = b;
        }
        return b;
    }
}
