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

import javax.media.opengl.GL2;

public final class Vertex
    extends fv3.model.Object
{

    public final double x, y, z;

    private String string;

    private int hashCode;


    public Vertex(double x, double y, double z){
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


    public double[] copy(){
        return new double[]{x,y,z};
    }
    public void apply(GL2 gl){
        gl.glVertex3d(this.x,this.y,this.z);
    }
    public Object.Type getObjectType(){
        return Object.Type.Vertex;
    }
    public int hashCode(){
        int hashCode = this.hashCode;
        if (0 == hashCode &&(0.0 != this.x || 0.0 != this.y || 0.0 != this.z)){

            long hashX = Double.doubleToLongBits(x);
            hashX ^= (hashX>>>32);
            long hashY = Double.doubleToLongBits(y);
            hashY ^= (hashY>>>32);
            long hashZ = Double.doubleToLongBits(z);
            hashZ ^= (hashZ>>>32);

            long LhashCode = (hashX^hashY^hashZ);
            hashCode = (int)(LhashCode & Integer.MAX_VALUE);
            this.hashCode = hashCode;
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


    public final static Vertex[] Add(Vertex[] list, Vertex item){
        if (null == item)
            return list;
        else if (null == list)
            return new Vertex[]{item};
        else {
            int len = list.length;
            Vertex[] copier = new Vertex[len+1];
            System.arraycopy(list,0,copier,0,len);
            copier[len] = item;
            return copier;
        }
    }
    public final static Vertex[][] Add(Vertex[][] list, Vertex[] item){
        if (null == item)
            return list;
        else if (null == list)
            return new Vertex[][]{item};
        else {
            int len = list.length;
            Vertex[][] copier = new Vertex[len+1][];
            System.arraycopy(list,0,copier,0,len);
            copier[len] = item;
            return copier;
        }
    }
}
