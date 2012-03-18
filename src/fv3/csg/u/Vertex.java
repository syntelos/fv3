/*
 * fv3
 * Copyright (C) 2012  John Pritchard, all rights reserved.
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
package fv3.csg.u;

import fv3.math.Vector;

/**
 * 
 * @author John Pritchard
 */
public class Vertex
    extends fv3.math.Abstract
    implements fv3.csg.u.Notation,
               java.lang.Comparable<Vertex>,
               java.lang.Iterable<Face>,
               java.lang.Cloneable
{

    public final float x, y, z;

    public final int hashCode;

    public State status = State.Unknown;

    private Face[] membership;

    private Vector normal;


    public Vertex(Vector v){
        this(v.array());
    }
    public Vertex(float[] array){
        this(array[X],array[Y],array[Z]);
    }
    public Vertex(float x, float y, float z){
        super();
        this.x = Z(x);
        this.y = Z(y);
        this.z = Z(z);
        {
            final int hashX = Float.floatToIntBits(this.x);

            final int hashY = Float.floatToIntBits(this.y);

            final int hashZ = Float.floatToIntBits(this.z);

            this.hashCode = (hashX^hashY^hashZ);
        }
    }
    public Vertex(float[] array, int ofs){
        this(array[ofs+X],array[ofs+Y],array[ofs+Z]);
    }
    public Vertex(Vector pos, State status){
        this(pos.array());
        if (null != status)
            this.status = status;
        else
            throw new IllegalArgumentException();
    }


    public void init(){
        this.status = State.Unknown;
    }
    public Vector getPosition(){
        return new Vector(this.x,this.y,this.z);
    }
    public Vector getVector(){
        return new Vector(this.x,this.y,this.z);
    }
    public float distance(Vertex that){

        final float dx = (this.x - that.x);
        final float dy = (this.y - that.y);
        final float dz = (this.z - that.z);

        return (float)Math.sqrt( (dx*dx) + (dy*dy) + (dz*dz));
    }
    public float distance(Face face){

        final float[] n = face.getNxyzd();

        final float nx = n[0];
        final float ny = n[1];
        final float nz = n[2];
        final float fd = n[3];

        return (nx*this.x + ny*this.y + nz*this.z + fd);
    }
    public int sdistance(Face face){

        final float d = this.distance(face);
        if (d > EPS)
            return 1;
        else if (d < -EPS)
            return -1;
        else
            return 0;
    }
    public State sclass(Face that){
        return State.Classify(this.sdistance(that));
    }
    public boolean is(State s){
        return (s == this.status);
    }
    public boolean or(State s1, State s2){
        return (s1 == this.status || s2 == this.status);
    }
    public State con(State... set){
        for (State s: set){
            if (s == this.status)
                return s;
        }
        return null;
    }
    public boolean isUnknown(){
        return (State.Unknown == this.status);
    }
    public boolean isNotUnknown(){
        return (State.Unknown != this.status);
    }
    public boolean isInside(){
        return (State.Inside == this.status);
    }
    public boolean isOutside(){
        return (State.Outside == this.status);
    }
    public boolean isBoundary(){
        return (State.Boundary == this.status);
    }
    public void destroy(){
        this.membership = null;
    }
    /**
     * Called in creating CSG product (A.r) and in copying a vertex
     * from one CSG solid to another.
     */
    public Vertex clone(){
        try {
            Vertex clone = (Vertex)super.clone();
            /*
             * Clear status for copying vertex from one CSG operand
             * to another
             */
            clone.status = State.Unknown;
            /*
             * Vertex cloning needs to be part of a process of
             * rebuilding vertex face membership lists 
             */
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
            if (-1 != index){
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
            else
                return false;
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
    public Vertex midpoint(Vertex b){

        final float x = Z((this.x + b.x) / 2.0f);
        final float y = Z((this.y + b.y) / 2.0f);
        final float z = Z((this.z + b.z) / 2.0f);

        return new Vertex(x,y,z);
    }
    public float[] copy(){
        return new float[]{x,y,z};
    }
    public float[] copy(float[] a, int ofs){
        a[ofs++] = this.x;
        a[ofs++] = this.y;
        a[ofs] = this.z;
        return a;
    }
    /**
     * Initial vertex classification
     */
    public Vertex with(Vertex v){

        this.status = v.status;

        return this;
    }
    /**
     * Initial vertex classification
     */
    public Vertex boundary(){

        this.status = State.Boundary;

        return this;
    }
    /**
     * Vertex classification propagation
     */
    public Vertex classify(Vertex v){

        return this.classify(v.status,true);
    }
    /**
     * Vertex classification propagation
     */
    public Vertex classify(State s){

        return this.classify(s,true);
    }
    /**
     * Vertex classification propagation
     */
    public Vertex classify(State s, boolean fwd){

        if (State.Unknown == this.status){

            this.status = s;

            if (State.Boundary != s && null != this.membership){

                for (Face face: this){

                    face.classify(this,s,true);
                }
            }
        }
        return this;
    }
    public int hashCode(){
        return this.hashCode;
    }
    public String toString(){

        return String.format("(%5.3g, %5.3g, %5.3g, %s)",x,y,z,this.status);
    }
    public boolean equals(Object that){
        if (this == that)
            return true;

        else if (that instanceof Vertex)

            return this.equals( (Vertex)that);
        else
            return false;
    }
    public boolean equals(Vertex that){
        if (this == that)
            return true;

        else if (null != that)

            return this.getVector().equals(that.getVector());
        else
            return false;
    }
    /**
     * Path order comparison
     */
    public int compareTo(Vertex that){
        if (null == that)
            return 1;
        else {
            Vector a = this.getVector();
            Vector b = that.getVector();
            if (a.equals(b))
                return 0;
            else {
                float angle = a.normalize().angle(b.normalize());
                if (ZERO > angle)
                    return -1;
                else
                    return 1;
            }
        }
    }
    public Face.Iterator iterator(){
        return new Face.Iterator(this.membership);
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

    public static class Iterator
        extends java.lang.Object
        implements java.util.Iterator<Vertex>
    {

        public final int length;

        private final Vertex[] list;

        private int index;

        public Iterator(Vertex[] list){
            super();
            if (null == list){
                this.list = null;
                this.length = 0;
            }
            else {
                this.list = list.clone();
                this.length = this.list.length;
            }
        }
        public Iterator(Vertex a, Vertex b, Vertex c){
            super();
            this.list = new Vertex[]{a,b,c};
            this.length = 3;
        }

        public boolean hasNext(){
            return (this.index < this.length);
        }
        public Vertex next(){
            return this.list[this.index++];
        }
        public void remove(){
            throw new UnsupportedOperationException();
        }
    }
}
