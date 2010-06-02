/*
 * fv3.math
 * Copyright (c) 2009 John Pritchard, all rights reserved.
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
 * This class employs the Row Major Notation.  The matrix notation is
 * an addressing scheme.  In using Row Major Notation, this class
 * adopts an address mapping to Open GL's Row Minor storage format.
 * 
 * The Row Major Notation used exposed by this class expresses the
 * elements of the matrix as illustrated in the following:
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
 * From Row Major notation, this class maps storage location for Open
 * GL.  GL's matrix notation and storage, as in the red book, is Row
 * Minor (Column Major).  
 * 
 * Row Minor Notation can be confusing from the perspective of the
 * programming language, while it's natural from the relative
 * perspective of hardware internals.  The Row Minor storage order is
 * defined as follows.
 * 
 * <pre>
 *          |  0    4    8   12  |
 *          |                    |
 *          |  1    5    9   13  |
 *      N = |                    |
 *          |  2    6   10   14  |
 *          |                    |
 *          |  3    7   11   15  |
 * </pre>
 * 
 * 
 * @see Abstract
 * @author jdp
 */
public class Matrix
    extends Abstract
{


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
                m[I(j,i)] = Z(ab);
            }
        }
        return this;
    }
    public final Matrix translate(double x, double y, double z){
        double[] m = this.m;

        for (int i = 0; i < 3; i++) {
            m[I(i,3)] += m[I(i,0)] * x + m[I(i,1)] * y + m[I(i,2)] * z;
        }
        return this;
    }
    public final Matrix translate(Vector v){
        double[] vv = v.array();
        return this.translate(vv[X],vv[Y],vv[Z]);
    }
    public final Vector getTranslation(){
        return new Vector(this.m[M03],this.m[M13],this.m[M23]);
    }
    public final Matrix scale(double s){
        if (1.0 != s){
            double[] m = this.m;

            for (int i = 0; i < 4; i++) {
                m[I(i,0)] *= s;
                m[I(i,1)] *= s;
                m[I(i,2)] *= s;
            }
        }
        return this;
    }
    public final Matrix scale(double x, double y, double z){

        double[] m = this.m;

        for (int i = 0; i < 4; i++) {
            m[I(i,0)] *= x;
            m[I(i,1)] *= y;
            m[I(i,2)] *= z;
        }
        return this;
    }
    public final Vector getScale(){
        return new Vector(this.m[M00],this.m[M11],this.m[M22]);
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
        R[M02] = ((xz * c1) + ys);
        R[M10] = ((xy * c1) + zs);
        R[M11] = ((yy * c1) +  c);
        R[M12] = ((yz * c1) - xs);
        R[M20] = ((xz * c1) - ys);
        R[M21] = ((yz * c1) + xs);
        R[M22] = ((zz * c1) +  c);

        return this.mul(R);
    }
    public final Matrix rotateX(double rax){
        double[] R = Identity.clone();

        double c = Math.cos(rax);
        double s = Math.sin(rax);

        R[M11] = c;
        R[M21] = -s;

        R[M12] = s;
        R[M22] = c;

        return this.mul(R);
    }
    public final Matrix rotateY(double ray){
        double[] R = Identity.clone();

        double c = Math.cos(ray);
        double s = Math.sin(ray);

        R[M00] = c;
        R[M20] = -s;

        R[M02] = s;
        R[M22] = c;

        return this.mul(R);
    }
    public final Matrix rotateZ(double raz){
        double[] R = Identity.clone();

        double c = Math.cos(raz);
        double s = Math.sin(raz);

        R[M00] = c;
        R[M10] = -s;

        R[M01] = s;
        R[M11] = c;

        return this.mul(R);
    }
    public final Matrix rotate(double rax, double ray, double raz){
        double[] R = Identity.clone();

        double cx = Math.cos(rax);
        double sx = Math.sin(rax);

        double cy = Math.cos(ray);
        double sy = Math.sin(ray);

        double cz = Math.cos(raz);
        double sz = Math.sin(raz);

        R[M00] =  cy * cz;
        R[M01] = -cy * sz;
        R[M02] =  sy;

        R[M10] =  (sx * sy * cz)+(cx * sz);
        R[M11] = -(sx * sy * sz) + (cx * cz);
        R[M12] = -sx * cy;

        R[M20] = -(cx * sy * cz) + (sx * sz);
        R[M21] =  (cx * sy * sz) + (sx * cz);
        R[M22] =  cx * cy;

        return this.mul(R);
    }
    public final Vector transform(Vector v){
        double[] c = v.array();
        double[] mm = this.m;
        double[] a = c.clone();

        c[X] = mm[M00] * a[X] + mm[M01] * a[Y] + mm[M02] * a[Z] + mm[M03];
        c[Y] = mm[M10] * a[X] + mm[M11] * a[Y] + mm[M12] * a[Z] + mm[M13];
        c[Z] = mm[M20] * a[X] + mm[M21] * a[Y] + mm[M22] * a[Z] + mm[M23];

        return v;
    }
    public final double[] transform(double[] v){

        double[] mm = this.m;
        double[] a = v.clone();

        v[X] = mm[M00] * a[X] + mm[M01] * a[Y] + mm[M02] * a[Z] + mm[M03];
        v[Y] = mm[M10] * a[X] + mm[M11] * a[Y] + mm[M12] * a[Z] + mm[M13];
        v[Z] = mm[M20] * a[X] + mm[M21] * a[Y] + mm[M22] * a[Z] + mm[M23];

        return v;
    }
    public final double m00(){
        return this.m[M00];
    }
    public final double getM00(){
        return this.m[M00];
    }
    public final Matrix m00(double m00){
        this.m[M00] = Z(m00);
        return this;
    }
    public final Matrix setM00(double m00){
        this.m[M00] = Z(m00);
        return this;
    }
    public final double m01(){
        return this.m[M01];
    }
    public final double getM01(){
        return this.m[M01];
    }
    public final Matrix m01(double m01){
        this.m[M01] = Z(m01);
        return this;
    }
    public final Matrix setM01(double m01){
        this.m[M01] = Z(m01);
        return this;
    }
    public final double m02(){
        return this.m[M02];
    }
    public final double getM02(){
        return this.m[M02];
    }
    public final Matrix m02(double m02){
        this.m[M02] = Z(m02);
        return this;
    }
    public final Matrix setM02(double m02){
        this.m[M02] = Z(m02);
        return this;
    }
    public final double m03(){
        return this.m[M03];
    }
    public final double getM03(){
        return this.m[M03];
    }
    public final Matrix m03(double m03){
        this.m[M03] = Z(m03);
        return this;
    }
    public final Matrix setM03(double m03){
        this.m[M03] = Z(m03);
        return this;
    }
    public final double m10(){
        return this.m[M10];
    }
    public final double getM10(){
        return this.m[M10];
    }
    public final Matrix m10(double m10){
        this.m[M10] = Z(m10);
        return this;
    }
    public final Matrix setM10(double m10){
        this.m[M10] = Z(m10);
        return this;
    }
    public final double m11(){
        return this.m[M11];
    }
    public final double getM11(){
        return this.m[M11];
    }
    public final Matrix m11(double m11){
        this.m[M11] = Z(m11);
        return this;
    }
    public final Matrix setM11(double m11){
        this.m[M11] = Z(m11);
        return this;
    }
    public final double m12(){
        return this.m[M12];
    }
    public final double getM12(){
        return this.m[M12];
    }
    public final Matrix m12(double m12){
        this.m[M12] = Z(m12);
        return this;
    }
    public final Matrix setM12(double m12){
        this.m[M12] = Z(m12);
        return this;
    }
    public final double m13(){
        return this.m[M13];
    }
    public final double getM13(){
        return this.m[M13];
    }
    public final Matrix m13(double m13){
        this.m[M13] = Z(m13);
        return this;
    }
    public final Matrix setM13(double m13){
        this.m[M13] = Z(m13);
        return this;
    }
    public final double m20(){
        return this.m[M20];
    }
    public final double getM20(){
        return this.m[M20];
    }
    public final Matrix m20(double m20){
        this.m[M20] = Z(m20);
        return this;
    }
    public final Matrix setM20(double m20){
        this.m[M20] = Z(m20);
        return this;
    }
    public final double m21(){
        return this.m[M21];
    }
    public final double getM21(){
        return this.m[M21];
    }
    public final Matrix m21(double m21){
        this.m[M21] = Z(m21);
        return this;
    }
    public final Matrix setM21(double m21){
        this.m[M21] = Z(m21);
        return this;
    }
    public final double m22(){
        return this.m[M22];
    }
    public final double getM22(){
        return this.m[M22];
    }
    public final Matrix m22(double m22){
        this.m[M22] = Z(m22);
        return this;
    }
    public final Matrix setM22(double m22){
        this.m[M22] = Z(m22);
        return this;
    }
    public final double m23(){
        return this.m[M23];
    }
    public final double getM23(){
        return this.m[M23];
    }
    public final Matrix m23(double m23){
        this.m[M23] = Z(m23);
        return this;
    }
    public final Matrix setM23(double m23){
        this.m[M23] = Z(m23);
        return this;
    }
    public final double m30(){
        return this.m[M30];
    }
    public final double getM30(){
        return this.m[M30];
    }
    public final Matrix m30(double m30){
        this.m[M30] = Z(m30);
        return this;
    }
    public final Matrix setM30(double m30){
        this.m[M30] = Z(m30);
        return this;
    }
    public final double m31(){
        return this.m[M31];
    }
    public final double getM31(){
        return this.m[M31];
    }
    public final Matrix m31(double m31){
        this.m[M31] = Z(m31);
        return this;
    }
    public final Matrix setM31(double m31){
        this.m[M31] = Z(m31);
        return this;
    }
    public final double m32(){
        return this.m[M32];
    }
    public final double getM32(){
        return this.m[M32];
    }
    public final Matrix m32(double m32){
        this.m[M32] = Z(m32);
        return this;
    }
    public final Matrix setM32(double m32){
        this.m[M32] = Z(m32);
        return this;
    }
    public final double m33(){
        return this.m[M33];
    }
    public final double getM33(){
        return this.m[M33];
    }
    public final Matrix m33(double m33){
        this.m[M33] = Z(m33);
        return this;
    }
    public final Matrix setM33(double m33){
        this.m[M33] = Z(m33);
        return this;
    }

    public String toString(){
        return this.toString("","\n");
    }
    public String toString(String pr){
        return this.toString(pr,"\n");
    }
    public String toString(String pr, String in){
        if (null == pr)
            pr = "";
        if (null == in)
            in = "";
        double[] m = this.m;
        return String.format("%s%30.26f %30.26f %30.26f %30.26f%s%s%30.26f %30.26f %30.26f %30.26f%s%s%30.26f %30.26f %30.26f %30.26f%s%s%30.26f %30.26f %30.26f %30.26f", pr, m[M00], m[M01], m[M02], m[M03], in, pr, m[M10], m[M11], m[M12], m[M13], in, pr, m[M20], m[M21], m[M22], m[M23], in, pr, m[M30], m[M31], m[M32], m[M33]);
    }


    /** 
     * @param m Row Major Row Index
     * @param n Row Major Column Index
     * @return GL Array Index
     */
    public final static int I(int m, int n){
        switch (m){
        case 0:
            return (4*n);
        case 1:
            return (1+(4*n));
        case 2:
            return (2+(4*n));
        case 3:
            return (3+(4*n));
        default:
            throw new IllegalArgumentException(String.valueOf(n));
        }
    }


    private final static double[] Identity = {1.0, 0.0, 0.0, 0.0, 
                                              0.0, 1.0, 0.0, 0.0, 
                                              0.0, 0.0, 1.0, 0.0, 
                                              0.0, 0.0, 0.0, 1.0};


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
