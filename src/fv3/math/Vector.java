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

/**
 * A point or vertex in geometric three- space.
 * 
 * @see Abstract
 * @author jdp
 */
public class Vector
    extends Abstract
    implements java.lang.Comparable<Vector>,
               java.lang.Cloneable
{
    /**
     * Principal magnitude classes.
     */
    public enum Magnitude1 {

        MX, MY, MZ;

        public static Magnitude1 For(double[] n){

            final double nX = n[X];
            final double nY = n[Y];
            final double nZ = n[Z];

            final double anX = Math.abs(nX);
            final double anY = Math.abs(nY);
            final double anZ = Math.abs(nZ);

            if (anX > anY){

                if (anX > anZ)

                    return MX;
                else
                    return MZ;
            }
            else if (anY > anZ)

                return MY;
            else
                return MZ;
        }
    }
    /**
     * Positive and negative direction classes.
     */
    public enum Direction1 {

        DXP, DXN, DYP, DYN, DZP, DZN;

        /**
         * @param that Comparison direction
         * 
         * @return Zero for same direction, one for same axis,
         * negative one for different axes.
         */
        public int colinear(Direction1 that){
            if (this == that)
                return 0;
            else {
                switch (this){
                case DXP:
                case DXN:
                    switch (that){
                    case DXP:
                    case DXN:
                        return 1;
                    default:
                        return -1;
                    }
                case DYP:
                case DYN:
                    switch (that){
                    case DYP:
                    case DYN:
                        return 1;
                    default:
                        return -1;
                    }
                case DZP:
                case DZN:
                    switch (that){
                    case DZP:
                    case DZN:
                        return 1;
                    default:
                        return -1;
                    }
                default:
                    throw new IllegalStateException();
                }
            }
        }

        public static Direction1 For(double[] n){

            final double nX = n[X];
            final double nY = n[Y];
            final double nZ = n[Z];

            final double anX = Math.abs(nX);
            final double anY = Math.abs(nY);
            final double anZ = Math.abs(nZ);

            if (anX > anY){

                if (anX > anZ){
                    if (0.0 > nX)
                        return DXN;
                    else
                        return DXP;
                }
                else if (0.0 > nZ)
                    return DZN;
                else
                    return DZP;
            }
            else if (anY > anZ){

                if (0.0 > nY)
                    return DYN;
                else
                    return DYP;
            }
            else if (0.0 > nZ)
                return DZN;
            else
                return DZP;
        }
    }
    /**
     * Magnitude classes with one and two axes.
     */
    public enum Direction2 {

        DX, DY, DZ, DXY, DXZ, DYZ;

        public final static double E = 1e-2;

        public static Direction2 For(double[] n){

            final double nX = n[X];
            final double nY = n[Y];
            final double nZ = n[Z];

            final double anX = Math.abs(nX);
            final double anY = Math.abs(nY);
            final double anZ = Math.abs(nZ);

            if (0.0 == DE((anX - anY),E))

                return DXY;

            else if (0.0 == DE((anX - anZ),E))

                return DXZ;

            else if (0.0 == DE((anY - anZ),E))

                return DYZ;

            else if (anX > anY){

                if (anX > anZ)

                    return DX;
                else
                    return DZ;
            }
            else if (anY > anZ)

                return DY;
            else
                return DZ;
        }
    }

    /**
     * Unique array for buffer changes only under construction and
     * cloning.
     * @see Abstract
     */
    private double[] v;


    public Vector(){
        this(0.0f,0.0f,0.0f);
    }
    public Vector(double x, double y, double z){
        super();
        this.v = (new double[]{x,y,z});
    }
    public Vector(double[] v){
        super();
        if (3 == v.length)
            this.v = v;
        else
            throw new IllegalArgumentException();
    }
    public Vector(Vector v){
        super();
        this.v = v.v.clone();
    }


    public Vector clone(){
        try {
            Vector clone = (Vector)super.clone();
            clone.v = clone.v.clone();
            clone.b = null;
            return clone;
        }
        catch (java.lang.CloneNotSupportedException exc){
            throw new InternalError();
        }
    }
    public final Vector set(double x, double y, double z) {
        double[] v = this.v;
        v[X] = x;
        v[Y] = y;
        v[Z] = z;
        return this;
    }
    public final Vector zero() {
        System.arraycopy(Zero,0,this.v,0,3);
        return this;
    }
    public final Vector copyFrom(Vector v){
        return this.copyFrom(v.v);
    }
    public final Vector copyFrom(double[] v){
        if (3 == v.length){
            System.arraycopy(v,0,this.v,0,3);
            return this;
        }
        else
            throw new IllegalArgumentException();
    }
    public final Vector copyTo(Vector v){
        this.copyTo(v.v);
        return v;
    }
    public final double[] copyTo(double[] v){
        if (3 == v.length){
            System.arraycopy(this.v,0,v,0,3);
            return v;
        }
        else
            throw new IllegalArgumentException();
    }
    public final Vector add(Vector v){
        double[] a = this.v;
        double[] b = v.v;
        a[X] = Z(a[X] + b[X]);
        a[Y] = Z(a[Y] + b[Y]);
        a[Z] = Z(a[Z] + b[Z]);
        return this;
    }
    public final Vector add(double[] b){
        double[] a = this.v;

        a[X] = Z(a[X] + b[X]);
        a[Y] = Z(a[Y] + b[Y]);
        a[Z] = Z(a[Z] + b[Z]);
        return this;
    }
    public final Vector add(double dx, double dy, double dz){
        double[] a = this.v;
        a[X] = Z(a[X] + dx);
        a[Y] = Z(a[Y] + dy);
        a[Z] = Z(a[Z] + dz);
        return this;
    }
    public final Vector sub(Vector v){
        double[] a = this.v;
        double[] b = v.v;
        a[X] = Z(a[X] - b[X]);
        a[Y] = Z(a[Y] - b[Y]);
        a[Z] = Z(a[Z] - b[Z]);
        return this;
    }
    public final Vector mul(double s){
        if (1.0 != s){
            double[] a = this.v;
            a[X] = Z(a[X] * s);
            a[Y] = Z(a[Y] * s);
            a[Z] = Z(a[Z] * s);
        }
        return this;
    }
    public final Vector div(double s){
        if (1.0 != s){
            double[] a = this.v;
            a[X] = Z(a[X] / s);
            a[Y] = Z(a[Y] / s);
            a[Z] = Z(a[Z] / s);
        }
        return this;
    }
    public final double dot(Vector v){
        double[] a = this.v;
        double[] b = v.v;

        return Z(a[X]*b[X] + a[Y]*b[Y] + a[Z]*b[Z]);
    }
    public final double length(){
        return Math.sqrt(this.dot(this));
    }
    public final Vector cross(Vector v){
        double[] c = this.v;
        double[] a = c.clone();
        double[] b = v.v;

        c[X] = Z(a[Y] * b[Z] - a[Z] * b[Y]);
        c[Y] = Z(a[Z] * b[X] - a[X] * b[Z]);
        c[Z] = Z(a[X] * b[Y] - a[Y] * b[X]);
        return this;
    }
    public final Vector normalize(){
        double[] c = this.v;

        double length = this.length();
        if (Math.abs(length) < EPSILON) {
            if ((c[X] >= c[Y]) && (c[X] >= c[Z])) {
                c[X] = 1.0;
                c[Y] = c[Z] = 0.0;
            }
            else {
                if (c[Y] >= c[Z]) {
                    c[Y] = 1.0;
                    c[X] = c[Z] = 0.0;
                }
                else {
                    c[Z] = 1.0;
                    c[X] = c[Y] = 0.0;
                }
            }
        }
        else {
            double m = 1.0 / length;
            c[X] = Z1(c[X] * m);
            c[Y] = Z1(c[Y] * m);
            c[Z] = Z1(c[Z] * m);
        }
        return this;
    }
    /**
     * Compute a vector normal to the line from this to 'a' and the
     * line from this to 'b'.
     */
    public final Vector normal(Vector a, Vector b){

        Vector p = new Vector(b).sub(this);

        Vector q = new Vector(a).sub(this);

        return q.cross(p).normalize();
    }
    /**
     * Compute a point centroid in the triangle described by the
     * points this 'a', and the arguments 'b' and 'c'.
     */
    public final Vector centroid(Vector b, Vector c){

        return new Vector(this).add(b).add(c).div(3);
    }
    public final Vector transform(Matrix m){
        double[] c = this.v;
        double[] mm = m.array();
        double[] a = c.clone();

        c[X] = mm[M00] * a[X] + mm[M01] * a[Y] + mm[M02] * a[Z] + mm[M03];
        c[Y] = mm[M10] * a[X] + mm[M11] * a[Y] + mm[M12] * a[Z] + mm[M13];
        c[Z] = mm[M20] * a[X] + mm[M21] * a[Y] + mm[M22] * a[Z] + mm[M23];

        return this;
    }
    public Vector min(Vector v){
        double[] c = Zero.clone();
        double[] a = this.v;
        double[] b = v.v;

        for (int i = 0; i < 3; ++i) {
            if (a[i] < c[i]) {
                c[i] = a[i];
            }
        }
        return new Vector(c);
    }
    public Vector max(Vector v){
        double[] c = Zero.clone();
        double[] a = this.v;
        double[] b = v.v;

        for (int i = 0; i < 3; ++i) {
            if (a[i] > c[i]) {
                c[i] = a[i];
            }
        }
        return new Vector(c);
    }
    public Vector mid(Vector v){

        double[] a = this.v;
        double[] b = v.v;

        a[X] = Z((a[X] + b[X]) / 2.0);
        a[Y] = Z((a[Y] + b[Y]) / 2.0);
        a[Z] = Z((a[Z] + b[Z]) / 2.0);

        return this;
    }
    public double distance(Vector b){
        final double[] this_v = this.v;
        final double[] that_v = b.v;
        final double dx = (this_v[X]-that_v[X]);
        final double dy = (this_v[Y]-that_v[Y]);
        final double dz = (this_v[Z]-that_v[Z]);

        return Math.sqrt( (dx*dx) + (dy*dy) + (dz*dz));
    }
    public Magnitude1 magnitude1(){
        return Magnitude1.For(this.v);
    }
    public Direction1 direction1(){
        return Direction1.For(this.v);
    }
    public Direction2 direction2(){
        return Direction2.For(this.v);
    }
    /**
     * @return For this and that unit vectors, this and that are in line
     */
    public boolean colinear(Vector that){

        return (EEQ(Math.abs(this.v[X]),Math.abs(that.v[X]))
                && EEQ(Math.abs(this.v[Y]),Math.abs(that.v[Y]))
                && EEQ(Math.abs(this.v[Z]),Math.abs(that.v[Z])));
    }
    public final double[] array(){
        return this.v;
    }

    public final double x(){
        return this.v[X];
    }
    public final double getX(){
        return this.v[X];
    }
    public final Vector x(double x){
        this.v[X] = x;
        return this;
    }
    public final Vector setX(double x){
        this.v[X] = x;
        return this;
    }

    public final double y(){
        return this.v[Y];
    }
    public final double getY(){
        return this.v[Y];
    }
    public final Vector y(double y){
        this.v[Y] = y;
        return this;
    }
    public final Vector setY(double y){
        this.v[Y] = y;
        return this;
    }

    public final double z(){
        return this.v[Z];
    }
    public final double getZ(){
        return this.v[Z];
    }
    public final Vector z(double z){
        this.v[Z] = z;
        return this;
    }
    public final Vector setZ(double z){
        this.v[Z] = z;
        return this;
    }

    public String toString(){
        double[] v = this.v;
        return String.format("%30.26f %30.26f %30.26f", v[X], v[Y], v[Z]);
    }
    public boolean equals(Object that){
        if (this == that)
            return true;

        else if (that instanceof Vector)

            return this.equals( (Vector)that);
        else
            return false;
    }
    public boolean equals(Vector that){
        if (this == that)
            return true;

        else if (null != that){

            return (0 == this.compareTo(that));
        }
        else
            return false;
    }
    public int compareTo(Vector that){
        if (this == that)
            return 0;
        else {
            /*
             * Classify according to the largest difference
             */
            final double[] thisV = this.v;
            final double[] thatV = that.v;

            final double dx = Z(thisV[X]-thatV[X]);
            final double dy = Z(thisV[Y]-thatV[Y]);
            final double dz = Z(thisV[Z]-thatV[Z]);
            final double adx = Math.abs(dx);
            final double ady = Math.abs(dy);
            final double adz = Math.abs(dz);

            if (adx > ady){
                if (adz > adx){

                    if (0.0 < dz)
                        return 1;
                    else
                        return -1;
                }
                else {
                    if (0.0 < dx)
                        return 1;
                    else
                        return -1;
                }
            }
            else if (ady > adz){

                if (0.0 < dy)
                    return 1;
                else
                    return -1;
            }
            else if (adz > adx){

                if (0.0 < dz)
                    return 1;
                else
                    return -1;
            }
            else if (0.0 == dx)
                return 0;
            else {
                if (0.0 < dx)
                    return 1;
                else
                    return -1;
            }
        }
    }


    private final static double[] Zero = {0.0,0.0,0.0};


    public final static double Diameter(fv3.Bounds bounds){
        Vector min = new Vector(bounds.getBoundsMinX(),bounds.getBoundsMinY(),bounds.getBoundsMinZ());
        Vector max = new Vector(bounds.getBoundsMaxX(),bounds.getBoundsMaxY(),bounds.getBoundsMaxZ());
        return max.distance(min);
    }
    public final static double Diameter(double minX, double maxX,
                                        double minY, double maxY,
                                        double minZ, double maxZ)
    {
        Vector min = new Vector(minX,minY,minZ);
        Vector max = new Vector(maxX,maxY,maxZ);
        return max.distance(min);
    }
}
