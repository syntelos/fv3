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
package fv3.model;

public final class Vertex
    extends fv3.model.Object
{

    public final float x, y, z;

    private String string;

    private int hashCode;


    public Vertex(float x, float y, float z){
        super();

        if (x != x)
            throw new IllegalStateException();
        else
            this.x = x;

        if (y != y)
            throw new IllegalStateException();
        else
            this.y = y;

        if (z != z)
            throw new IllegalStateException();
        else
            this.z = z;
    }



    public float[] copy(){
        return new float[]{x,y,z};
    }
    public int hashCode(){
        int hashCode = this.hashCode;
        if (0 == hashCode &&(ZERO != this.x || ZERO != this.y || ZERO != this.z)){

            final int hashX = Float.floatToIntBits(x);

            final int hashY = Float.floatToIntBits(y);

            final int hashZ = Float.floatToIntBits(z);

            this.hashCode = (hashX^hashY^hashZ);
        }
        return hashCode;
    }
    public String toString(){
        String string = this.string;
        if (null == string){
            string = String.format("(%g, %g, %g)",x,y,z);
            this.string = string;
        }
        return string;
    }
    public boolean equals(Object that){
        if (this == that)
            return true;

        else if (that instanceof Vertex){
            Vertex thatV = (Vertex)that;
            return (this.x == thatV.x && this.y == thatV.y && this.z == thatV.z);
        }
        else
            return false;
    }
}
