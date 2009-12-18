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

import fv3.math.Vector;

/**
 * Bounds of a geometric object as a minimum point and a maximum
 * point.
 */
public class Bounds
    extends Object
{

    public final Vector min, max;


    public Bounds(double[][] b){
        this(new Vector(b[0]),new Vector(b[1]));
    }
    public Bounds(Vector min, Vector max){
        super();
        if (null != min && null != max){
            this.min = min;
            this.max = max;
        }
        else
            throw new IllegalArgumentException();
    }


    public final Vector getMin(){
        return this.min;
    }
    public final Vector getMax(){
        return this.max;
    }

}
