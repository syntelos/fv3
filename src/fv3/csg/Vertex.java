/*
 * Fv3 CSG
 * Copyright (C) 2010  John Pritchard, jdp@syntelos.org
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
package fv3.csg;

import fv3.math.Vector;

/**
 * 
 */
public final class Vertex
    extends java.lang.Object
    implements fv3.csg.Notation,
               java.lang.Comparable<Vertex>,
               java.lang.Cloneable
{

    public final double x, y, z;

    public final int hashCode;

    protected State.Vertex status = State.Vertex.Unknown;

    private Face[] membership;

    private String string;

    private Vector normal;


    public Vertex(double[] array){
        this(array[X],array[Y],array[Z]);
    }
    public Vertex(double x, double y, double z){
        super();
        this.x = x;
        this.y = y;
        this.z = z;
        {
            long hashX = Double.doubleToLongBits(x);
            hashX ^= (hashX>>>32);

            long hashY = Double.doubleToLongBits(y);
            hashY ^= (hashY>>>32);

            long hashZ = Double.doubleToLongBits(z);
            hashZ ^= (hashZ>>>32);

            long LhashCode = (hashX^hashY^hashZ);
            this.hashCode = (int)(LhashCode & Integer.MAX_VALUE);
        }
    }
    public Vertex(double[] array, int ofs){
        this(array[ofs+X],array[ofs+Y],array[ofs+Z]);
    }
    public Vertex(Vector pos, State.Vertex status){
        this(pos.array());
        this.status = status;
    }


    public Vector getPosition(){
        return new Vector(this.x,this.y,this.z);
    }
    public Vector getVector(){
        return new Vector(this.x,this.y,this.z);
    }
    public double distance(Face face){

        double[] n = face.getNxyzd();

		double x = n[0];
		double y = n[1];
		double z = n[2];
		double d = n[3];

		return (x*this.x + y*this.y + z*this.z + d);
    }
    public boolean is(State.Vertex s){
        return (s == this.status);
    }
    public State.Vertex con(State.Vertex... set){
        for (State.Vertex s: set){
            if (s == this.status)
                return s;
        }
        return null;
    }
    public boolean isUnknown(){
        return (State.Vertex.Unknown == this.status);
    }
    public boolean isInside(){
        return (State.Vertex.Inside == this.status);
    }
    public boolean isOutside(){
        return (State.Vertex.Outside == this.status);
    }
    public boolean isBoundary(){
        return (State.Vertex.Boundary == this.status);
    }
    public Vertex setUnknown(){
        this.status = State.Vertex.Unknown;
        return this;
    }
    public Vertex setInside(){
        this.status = State.Vertex.Inside;
        return this;
    }
    public Vertex setOutside(){
        this.status = State.Vertex.Outside;
        return this;
    }
    public Vertex setBoundary(){
        this.status = State.Vertex.Boundary;
        return this;
    }
    public void destroy(){
        this.membership = null;
    }
    public Vertex clone(){
        try {
            Vertex clone = (Vertex)super.clone();
            clone.status = State.Vertex.Unknown;
            clone.membership = null;
            return clone;
        }
        catch (CloneNotSupportedException exc){
            throw new InternalError();
        }
    }
    public boolean isMemberOf(Face face){
        final Face[] m = this.membership;
        if (null == m)
            return false;
        else {
            final int count = m.length;
            for (int cc = 0; cc < count; cc++){
                if (face == m[cc])
                    return true;
            }
            return false;
        }
    }
    public boolean isNotMemberOf(Face face){
        final Face[] m = this.membership;
        if (null == m)
            return true;
        else {
            final int count = m.length;
            for (int cc = 0; cc < count; cc++){
                if (face == m[cc])
                    return false;
            }
            return true;
        }
    }
    public Vertex memberOf(Face face){
        if (null != face && this.isNotMemberOf(face))
            this.membership = Face.Add(this.membership,face);
        return this;
    }
    public boolean hasMembership(){
        return (null != this.membership);
    }
    public boolean hasNotMembership(){
        return (null == this.membership);
    }
    public boolean dropMember(Face face){
        final Face[] m = this.membership;
        if (null == m)
            return true;
        else {
            int index = -1;
            final int count = m.length;
            for (int cc = 0; cc < count; cc++){
                if (face == m[cc]){
                    index = cc;
                    break;
                }
            }
            if (1 == count){
                this.membership = null;
                return true;
            }
            else {
                final int term = (count-1);
                Face[] copy = new Face[term];

                if (0 == index){
                    System.arraycopy(m,1,copy,0,term);
                }
                else if (term == index){
                    System.arraycopy(m,0,copy,0,term);
                }
                else {
                    System.arraycopy(m,0,copy,0,index);
                    System.arraycopy(m,(index+1),copy,index,(term-index));
                }
                this.membership = copy;
                return false;
            }
        }
    }
    public Vector normal(Vertex b, Vertex c){
        Vector n = this.normal;
        if (null == n){
            Vector va = new Vector(this.x,this.y,this.z);
            Vector vb = new Vector(b.x,b.y,b.z);
            Vector vc = new Vector(c.x,c.y,c.z);

            n = new Vector(va.normal(vb,vc).array());
            this.normal = n;
        }
        return n;
    }
    public double[] copy(){
        return new double[]{x,y,z};
    }
    public double[] copy(double[] a, int ofs){
        a[ofs++] = this.x;
        a[ofs++] = this.y;
        a[ofs] = this.z;
        return a;
    }
    public int hashCode(){
        return this.hashCode;
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
    public boolean equals(Vertex that){
        if (this == that)
            return true;

        else if (null != that){

            return (this.x == that.x && this.y == that.y && this.z == that.z);
        }
        else
            return false;
    }
    public int compareTo(Vertex that){
        if (this == that)
            return 0;
        else if (this.x < that.x)
            return -1;
        else if (this.x == that.x){
            if (this.y < that.y)
                return -1;
            else if (this.y == that.y){
                if (this.z < that.z)
                    return -1;
                else if (this.z == that.z)
                    return 0;
                else
                    return 1;
            }
            else
                return 1;
        }
        else
            return 1;
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
