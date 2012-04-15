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
 * 
 * @see Color
 */
public abstract class AbstractFloat
    extends Abstract
    implements Fv3
{

    protected AbstractFloat(){
        super();
    }


    public abstract float[] array();

    public final void copy(float[] dst){
        float[] src = this.array();
        if (null == src){
            if (null == dst)
                return;
            else
                throw new IllegalStateException();
        }
        else if (null == dst)
            throw new IllegalArgumentException();
        else if (dst.length != src.length)
            throw new IllegalArgumentException(String.format("%d/%d",dst.length,src.length));
        else
            System.arraycopy(src,0,dst,0,src.length);
    }

}
