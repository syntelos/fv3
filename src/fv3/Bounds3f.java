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
package fv3;

import javax.vecmath.Point3f;

public class Bounds3f
    extends Object
{

    public final Point3f min, max;


    public Bounds3f(float[][] b){
        this(new Point3f(b[0]),new Point3f(b[1]));
    }
    public Bounds3f(Point3f min, Point3f max){
        super();
        if (null != min && null != max){
            this.min = min;
            this.max = max;
        }
        else
            throw new IllegalArgumentException();
    }


    public final Point3f getMin(){
        return this.min;
    }
    public final Point3f getMax(){
        return this.max;
    }
    public final float[] array(){
        return new float[]{
            this.min.x,
            this.min.y,
            this.min.z,
            this.max.x,
            this.max.y,
            this.max.z
        };
    }
}
