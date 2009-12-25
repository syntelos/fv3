/*
 * fv3.math
 * Copyright (c) 2009 John Pritchard, all rights reserved.
 * Portions Copyright (C) 1996-2008 by Jan Eric Kyprianidis, all rights reserved.
 *     
 * This program is free  software: you can redistribute it and/or modify 
 * it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program  is  distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU Lesser General Public License for more details.
 * 
 * You should  have received a copy of the GNU Lesser General Public License
 * along with  this program; If not, see <http://www.gnu.org/licenses/>. 
 */
package fv3.math;

/**
 * A transformation matrix in geometric three- space.
 * 
 * This class employs the Row Major Notation.  The Row Major Notation
 * expresses the elements of the matrix as illustrated in the
 * following:
 * 
 * <pre>
 *          | 0,0  0,1  0,2  0,3 |
 *          |                    |
 *          | 1,0  1,1  1,2  1,3 |
 *      M = |                    |
 *          | 2,0  2,1  2,2  2,3 |
 *          |                    |
 *          | 3,0  3,1  3,2  3,3 |
 * </pre>
 * 
 * This class has been written with Row Major Notation for its
 * familiarity with C and Java syntax, as in:
 * 
 * <pre>
 * double[][] M = new double[4][4];
 * for (int I = 0, J; I &lt; 4; I++){
 *     for (J = 0; J &lt; 4; J++){
 * 
 *         if ( I == J)
 *             M[I][J] = 1.0;
 *         else
 *             M[I][J] = 0.0;
 *     }
 * }
 * </pre>
 * 
 * Using this notation, this class maintains order of storage for Open
 * GL.
 * 
 * 
 * @see Abstract
 * @author jdp
 */
public class Matrix
    extends Abstract
{
    /*
     * Row Major Array Indeces Notation
     */
    public final static int M00 =  0;
    public final static int M01 =  1;
    public final static int M02 =  2;
    public final static int M03 =  3;
    public final static int M10 =  4;
    public final static int M11 =  5;
    public final static int M12 =  6;
    public final static int M13 =  7;
    public final static int M20 =  8;
    public final static int M21 =  9;
    public final static int M22 = 10;
    public final static int M23 = 11;
    public final static int M30 = 12;
    public final static int M31 = 13;
    public final static int M32 = 14;
    public final static int M33 = 15;


    private final double[] m;


    public Matrix(){
        super();
        this.m = Identity.clone();
    }
    public Matrix(double[] m){
        super();
        if (null != m && 16 == m.length)
            this.m = m;
        else
            throw new IllegalArgumentException();
    }
    public Matrix(Matrix m){
        super();
        this.m = m.array().clone();
    }


    public final Matrix identity(){
        System.arraycopy(Identity,0,this.m,0,16);
        return this;
    }
    public final double[] array(){
        return this.m;
    }
    public final Matrix copyFrom(Matrix m){
        return this.copyFrom(m.m);
    }
    public final Matrix copyFrom(double[] m){
        if (16 == m.length){
            System.arraycopy(m,0,this.m,0,16);
            return this;
        }
        else
            throw new IllegalArgumentException();
    }
    public final Matrix copyTo(Matrix m){
        this.copyTo(m.m);
        return m;
    }
    public final double[] copyTo(double[] m){
        if (16 == m.length){
            System.arraycopy(this.m,0,m,0,16);
            return m;
        }
        else
            throw new IllegalArgumentException();
    }
    public final Matrix mul(Matrix m){
        return this.mul(m.m);
    }
    public final Matrix mul(double[] b){
        double[] m = this.m;
        double[] a = m.clone();
        int i, j, k;
        double ab;
        for (j = 0; j < 4; j++) {
            for (i = 0; i < 4; i++) {
                ab = 0.0;
                for (k = 0; k < 4; k++){
                    ab += (a[I(k,i)] * b[I(j,k)]);
                }
                m[I(j,i)] = ab;
            }
        }
        return this;
    }
    /**
     * <pre>
     *          |  *    *    *    *  |
     *          |                    |
     *          |  *    *    *    *  |
     *      M = |                    |
     *          |  *    *    *    *  |
     *          |                    |
     *          |  A    B    C    *  |
     * 
     * A = (M00 * X) + (M10 * Y) + (M20 * Z)
     * B = (M01 * X) + (M11 * Y) + (M21 * Z)
     * C = (M02 * X) + (M12 * Y) + (M22 * Z)
     * </pre>
     */
    public final Matrix translate(double x, double y, double z){
        double[] m = this.m;

        for (int i = 0; i < 3; i++) {
            m[I(3,i)] += m[I(0,i)] * x + m[I(1,i)] * y + m[I(2,i)] * z;
        }
        return this;
    }
    public final Matrix translate(Vector v){
        double[] vv = v.array();
        return this.translate(vv[Vector.X],vv[Vector.Y],vv[Vector.Z]);
    }
    /**
     * <pre>
     *          |  A    A    A    A  |
     *          |                    |
     *          |  B    B    B    B  |
     *      M = |                    |
     *          |  C    C    C    C  |
     *          |                    |
     *          |  *    *    *    *  |
     * 
     * A = (A * X)
     * B = (B * Y)
     * C = (C * Z)
     * </pre>
     */
    public final Matrix scale(double s){
        if (1.0 != s){
            double[] m = this.m;

            for (int i = 0; i < 4; i++) {
                m[I(0,i)] *= s;
                m[I(1,i)] *= s;
                m[I(2,i)] *= s;
            }
        }
        return this;
    }
    public final Matrix rotate(AxisAngle a){
        double[] axis = a.normalize().array();
        double[] R = Identity.clone();
        
        double x = axis[Vector.X];
        double y = axis[Vector.Y];
        double z = axis[Vector.Z];

        double angle = a.angle();
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double c1 = (1.0 - c);

        double xx = (x * x);
        double yy = (y * y);
        double zz = (z * z);
        double xy = (x * y);
        double xz = (x * z);
        double yz = (y * z);
        double xs = (x * s);
        double ys = (y * s);
        double zs = (z * s);

        R[M00] = ((xx * c1) +  c);
        R[M01] = ((xy * c1) - zs);
        R[M02] = ((xz * c1) - ys);
        R[M10] = ((xy * c1) + zs);
        R[M11] = ((yy * c1) +  c);
        R[M12] = ((yz * c1) - xs);
        R[M20] = ((xz * c1) - ys);
        R[M21] = ((yz * c1) + xs);
        R[M22] = ((zz * c1) -  c);

        return this.mul(R);
    }
    public final Matrix rotateX(double angle){
        return this.rotate(AxisAngle.Axis.X(angle));
    }
    public final Matrix rotateY(double angle){
        return this.rotate(AxisAngle.Axis.Y(angle));
    }
    public final Matrix rotateZ(double angle){
        return this.rotate(AxisAngle.Axis.Z(angle));
    }
    public final Matrix rotateXY(double ax, double ay){
        return this.rotateX(ax).rotateY(ay);
    }
    /**
     * @return The determinant of this matrix.  
     */
    public final double det(){
        double[] m = this.m;

        double a1, a2, a3, a4, b1, b2, b3, b4, c1, c2, c3, c4, d1, d2, d3, d4;

        a1 = m[M00];
        b1 = m[M10];
        c1 = m[M20];
        d1 = m[M30];
        a2 = m[M01];
        b2 = m[M11];
        c2 = m[M21];
        d2 = m[M31];
        a3 = m[M02];
        b3 = m[M12];
        c3 = m[M22];
        d3 = m[M32];
        a4 = m[M03];
        b4 = m[M13];
        c4 = m[M23];
        d4 = m[M33];
        return (a1 * Det3x3(b2, b3, b4, c2, c3, c4, d2, d3, d4) -
                b1 * Det3x3(a2, a3, a4, c2, c3, c4, d2, d3, d4) +
                c1 * Det3x3(a2, a3, a4, b2, b3, b4, d2, d3, d4) -
                d1 * Det3x3(a2, a3, a4, b2, b3, b4, c2, c3, c4));
    }
    public final double m00(){
        return this.m[M00];
    }
    public final double getM00(){
        return this.m[M00];
    }
    public final Matrix m00(double m00){
        this.m[M00] = m00;
        return this;
    }
    public final Matrix setM00(double m00){
        this.m[M00] = m00;
        return this;
    }
    public final double m01(){
        return this.m[M01];
    }
    public final double getM01(){
        return this.m[M01];
    }
    public final Matrix m01(double m01){
        this.m[M01] = m01;
        return this;
    }
    public final Matrix setM01(double m01){
        this.m[M01] = m01;
        return this;
    }
    public final double m02(){
        return this.m[M02];
    }
    public final double getM02(){
        return this.m[M02];
    }
    public final Matrix m02(double m02){
        this.m[M02] = m02;
        return this;
    }
    public final Matrix setM02(double m02){
        this.m[M02] = m02;
        return this;
    }
    public final double m03(){
        return this.m[M03];
    }
    public final double getM03(){
        return this.m[M03];
    }
    public final Matrix m03(double m03){
        this.m[M03] = m03;
        return this;
    }
    public final Matrix setM03(double m03){
        this.m[M03] = m03;
        return this;
    }
    public final double m10(){
        return this.m[M10];
    }
    public final double getM10(){
        return this.m[M10];
    }
    public final Matrix m10(double m10){
        this.m[M10] = m10;
        return this;
    }
    public final Matrix setM10(double m10){
        this.m[M10] = m10;
        return this;
    }
    public final double m11(){
        return this.m[M11];
    }
    public final double getM11(){
        return this.m[M11];
    }
    public final Matrix m11(double m11){
        this.m[M11] = m11;
        return this;
    }
    public final Matrix setM11(double m11){
        this.m[M11] = m11;
        return this;
    }
    public final double m12(){
        return this.m[M12];
    }
    public final double getM12(){
        return this.m[M12];
    }
    public final Matrix m12(double m12){
        this.m[M12] = m12;
        return this;
    }
    public final Matrix setM12(double m12){
        this.m[M12] = m12;
        return this;
    }
    public final double m13(){
        return this.m[M13];
    }
    public final double getM13(){
        return this.m[M13];
    }
    public final Matrix m13(double m13){
        this.m[M13] = m13;
        return this;
    }
    public final Matrix setM13(double m13){
        this.m[M13] = m13;
        return this;
    }
    public final double m20(){
        return this.m[M20];
    }
    public final double getM20(){
        return this.m[M20];
    }
    public final Matrix m20(double m20){
        this.m[M20] = m20;
        return this;
    }
    public final Matrix setM20(double m20){
        this.m[M20] = m20;
        return this;
    }
    public final double m21(){
        return this.m[M21];
    }
    public final double getM21(){
        return this.m[M21];
    }
    public final Matrix m21(double m21){
        this.m[M21] = m21;
        return this;
    }
    public final Matrix setM21(double m21){
        this.m[M21] = m21;
        return this;
    }
    public final double m22(){
        return this.m[M22];
    }
    public final double getM22(){
        return this.m[M22];
    }
    public final Matrix m22(double m22){
        this.m[M22] = m22;
        return this;
    }
    public final Matrix setM22(double m22){
        this.m[M22] = m22;
        return this;
    }
    public final double m23(){
        return this.m[M23];
    }
    public final double getM23(){
        return this.m[M23];
    }
    public final Matrix m23(double m23){
        this.m[M23] = m23;
        return this;
    }
    public final Matrix setM23(double m23){
        this.m[M23] = m23;
        return this;
    }
    public final double m30(){
        return this.m[M30];
    }
    public final double getM30(){
        return this.m[M30];
    }
    public final Matrix m30(double m30){
        this.m[M30] = m30;
        return this;
    }
    public final Matrix setM30(double m30){
        this.m[M30] = m30;
        return this;
    }
    public final double m31(){
        return this.m[M31];
    }
    public final double getM31(){
        return this.m[M31];
    }
    public final Matrix m31(double m31){
        this.m[M31] = m31;
        return this;
    }
    public final Matrix setM31(double m31){
        this.m[M31] = m31;
        return this;
    }
    public final double m32(){
        return this.m[M32];
    }
    public final double getM32(){
        return this.m[M32];
    }
    public final Matrix m32(double m32){
        this.m[M32] = m32;
        return this;
    }
    public final Matrix setM32(double m32){
        this.m[M32] = m32;
        return this;
    }
    public final double m33(){
        return this.m[M33];
    }
    public final double getM33(){
        return this.m[M33];
    }
    public final Matrix m33(double m33){
        this.m[M33] = m33;
        return this;
    }
    public final Matrix setM33(double m33){
        this.m[M33] = m33;
        return this;
    }

    public String toString(){
        double[] m = this.m;
        return String.format("%30.26f %30.26f %30.26f %30.26f\n%30.26f %30.26f %30.26f %30.26f\n%30.26f %30.26f %30.26f %30.26f\n%30.26f %30.26f %30.26f %30.26f", m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8], m[9], m[10], m[11], m[12], m[13], m[14], m[15]);
    }


    /** 
     * Double array (1) index
     * 
     * @param m Row index
     * @param n Column index
     * @return Double array (1) index
     */
    public final static int I(int m, int n){
        switch (m){
        case 0:
            return n;
        case 1:
            return (4+n);
        case 2:
            return (8+n);
        case 3:
            return (12+n);
        default:
            throw new IllegalArgumentException(String.valueOf(n));
        }
    }


    private final static double[] Identity = {1.0, 0.0, 0.0, 0.0, 
                                              0.0, 1.0, 0.0, 0.0, 
                                              0.0, 0.0, 1.0, 0.0, 
                                              0.0, 0.0, 0.0, 1.0};


    private final static double Det3x3(double a1, double a2, double a3,
                                       double b1, double b2, double b3,
                                       double c1, double c2, double c3)
    {
        return (a1*Det2x2(b2, b3, c2, c3) -
                b1*Det2x2(a2, a3, c2, c3) +
                c1*Det2x2(a2, a3, b2, b3));
    }
    private final static double Det2x2( double a, double b, 
                                        double c, double d)
    {
        return ((a)*(d) - (b)*(c));
    }

    /*
     * Matrix Tests
     */
    public static void main(String[] argv){
        /*
         * Placeholder
         */
        Matrix m = new Matrix();
        System.out.println(m.toString());
    }
}
