/*
 * fv3
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
 * Fit a cubic spline to a set of points and plot to an independent
 * resolution (set).  
 * 
 * Largely based on code from Kevin's Online Panel Code and Numerical
 * Recipes' Cubic Spline Interpolation.
 * 
 * @author John Pritchard
 */
public class Spline
    extends Object
    implements Notation
{

    private final int s, n, np;

    private float ds, inX[], inY[], outX[], outY[], s1[], s2[], xp[], yp[];


    /**
     * @param n Plot output dimension in (X(n), Y(n))
     * @param x Input data points (copied, read only)
     * @param y Input data points (copied, read only)
     */
    public Spline(int n, float[] x, float[] y){
        this(n,x,y,false);
    }
    /**
     * @param n Plot output dimension in (X(n), Y(n))
     * @param x Input data points (copied, read only)
     * @param y Input data points (copied, read only)
     * @param smooth Optionally smooth the input data
     */
    public Spline(int n, float[] x, float[] y, boolean smooth){
        super();
        if (0 < n && null != x && null != y && x.length == y.length){
            /*
             * Configure
             */
            this.s = (n+1);
            this.n = n;
            this.np = x.length;
            this.inX = x.clone();
            this.inY = y.clone();
            this.ds = (float)Math.pow((1.0 / this.n),2);
            /*
             * Initialize
             */
            this.s1 = new float[this.s];
            this.s2 = new float[this.s];
            this.xp = new float[this.s];
            this.yp = new float[this.s];
            /*
             * Smooth Data
             */
            if (smooth)
                Smooth(this.inX,this.inY);
            /*
             * Compute Arc lengths
             */
            {
                float dx1, dy1, ds;
                for (int cc = 1; cc < this.np; cc++){
                    dx1 = (this.inX[cc] - this.inX[cc-1]);
                    dy1 = (this.inY[cc] - this.inY[cc-1]);
                    ds = (float)Math.sqrt( dx1*dx1 + dy1*dy1);
                    this.s1[cc] = (this.s1[cc-1] + ds);
                }
            }
            /*
             */
            Generate(this.s1,this.inX,this.np,this.xp);
            Generate(this.s1,this.inY,this.np,this.yp);
            /*
             */
            Distribute(this.s1,0,(this.np-1),this.s2,0,this.np,this.ds);
            /*
             * Plot
             */
            this.outX = new float[this.n];
            this.outY = new float[this.n];

            for (int cc = 0; cc < this.n; cc++){

                this.outX[cc] = Splint(this.s1,this.inX,this.xp,this.np,this.s2[cc]);
                this.outY[cc] = Splint(this.s1,this.inY,this.yp,this.np,this.s2[cc]);
            }
            this.outX[0] = this.inX[0];
            this.outY[0] = this.inY[0];
            this.outX[this.n-1] = this.inX[this.np-1];
            this.outY[this.n-1] = this.inY[this.np-1];
        }
        else
            throw new IllegalArgumentException();
    }


    /**
     * @return Array in ((X)+,(Y)+) order of size (X(n), Y(n))
     */
    public float[][] copy(){

        return new float[][]{
            this.outX.clone(),
            this.outY.clone()
        };
    }


    public final static void Smooth(float[] x, float[] y){
        final int n = x.length;
        float[] xx = new float[n], yy = new float[n];
        {
            System.arraycopy(x,0,xx,0,n);
            System.arraycopy(y,0,yy,0,n);
        }
        {
            final int trm = (n-1);
            for (int lc = 0, idx; lc < 2; lc++){
                for (idx = 1; idx < trm; idx++){
                    x[idx] = ((xx[idx-1] + xx[idx] + xx[idx+1]) / 3.0f);
                    y[idx] = ((yy[idx-1] + yy[idx] + yy[idx+1]) / 3.0f);
                }
                System.arraycopy(x,1,xx,1,trm);
                System.arraycopy(y,1,yy,1,trm);
            }
        }
    }
    /**
     * Numerical Recipes cubic spline routine computes the second
     * derivatives at each node for the data points x and y, real
     * vectors of length n.  This version ignores the endpoint slopes
     * of the spline for a natural spline.
     */
    public final static void Generate(float[] x, float[] y, int n, float[] y2){

        y2[0] = ZERO;

        float u[] = new float[n];
        u[0] = ZERO;

        {
            float sig, p;

            for (int i = 1, trm = (n-1); i < trm; i++){

                sig = (x[i]-x[i-1])/(x[i+1]-x[i-1]);

                p = sig*y2[i-1]+2.0f;

                y2[i] = (sig-1.0f)/p;

                u[i] = (6.0f*((y[i+1]-y[i])/(x[i+1]-x[i])-(y[i]-y[i-1])
                             /(x[i]-x[i-1]))/(x[i+1]-x[i-1])-sig*u[i-1])/p;
            }
        }

        y2[n-1] = ZERO;

        for (int k = (n-2); -1 < k; k--){

            y2[k] = y2[k]*y2[k+1]+u[k];
        }
    }
    /**
     * Given the arrays xa[1..n] and ya[1..n], which tabulate a
     * function (with the xai's in order), and given the array
     * y2a[1..n], which is the output from spline above, and given a
     * value of x, this routine returns a cubic-spline interpolated
     * value y.
     */
    public final static float Splint(float xa[], float ya[], float y2a[], int n, float x)
    {
        int klo = 1, khi = n, k;
        float h, b, a;

        while (khi-klo > 1) {
            k=(khi+klo) >> 1;
            if (xa[k] > x)
                khi=k;
            else
                klo=k;
        }
        h = xa[khi]-xa[klo];
        if (h == ZERO) 
            throw new IllegalStateException("Bad xa input to routine splint"); 
        else {
            a = (xa[khi]-x)/h;
            b = (x-xa[klo])/h; 
            return (a*ya[klo]+b*ya[khi]+((a*a*a-a)*y2a[klo]+(b*b*b-b)*y2a[khi])*(h*h)/6.0f);
        }
    }
    public final static void Distribute(float[] s1, int nb1, int ne1, 
                                        float[] s2, int nb2, int ne2, float ds)
    {
        float ct[] = new float[10], c[] = new float[10];
        int ict[] = new int[10], ipt[] = new int[10];

        /*
         *  load polynomial vars.
         */
        ipt[0] = (nb2+1);
        ict[0] = 0;
        ct[0] = s1[nb1];

        ipt[1] = (nb2+1);
        ict[1] = 1;
        ct[1] = ds;

        ipt[2] = (ne2+1);
        ict[2] = 0;
        ct[2] = s1[ne1];

        ipt[3] = (ne2+1);
        ict[3] = 1;
        ct[3] = ds;

        if (Polynomial(ct,ict,ipt,4,c))
            throw new IllegalStateException("Singular s-redistribution");
        else {
            s2[nb2] = ZERO;
            for (int j = (nb2+1); j < ne2; j++){

                s2[j] = (c[0] +
                         c[1] * (j+1) +
                         c[2] * (float)Math.pow((j+1),2) +
                         c[3] * (float)Math.pow((j+1),3));
            }

            s2[ne2] = s1[ne1];
        }
    }
    /**
     * Subroutine polynm is a library routine used to compute the
     * coefficients of an nth degree polynomial for a curve fit.
     * 
     * @param ct is the location or slope of the curve.
     * @param ict is an array with switching values. ict = 0 for a
     * location control.  ict = 1 for a derivative (slope) control.
     * @param ipt is an array of the indicies that are used for control.
     * @param n is the number of control points.
     * @param c is the array of coefficients found.
     */
    public final static boolean Polynomial(float[] ct, int[] ict, int[] ipt, 
                                            final int n, float[] c)
    {
        float a[][] = new float[10][11];
        
        for (int irow = 0; irow < n; irow++){
            for (int icol = 0; icol < n; icol++){
              if ( 0 == ict[irow])
                  /*
                   * Position control
                   */
                  a[irow][icol] = (float)Math.pow( ipt[irow], icol);
              else
                  /*
                   * Slope control
                   */
                  a[irow][icol] = (icol)*(float)Math.pow(ipt[irow],(icol-1));
            }
            /*
             * RHS loaded
             */
            a[irow][n] = ct[irow];
        }
        return Inverse(n,a,c);
    }
    /**
     * Solve a system of linearly independent equations.
     * 
     * @param n is the number of equations.
     * @param a is the n*n matrix of coefficients with the rhs values
     *     appended to the right side of the matrix.
     * @param f is the solution set for the system.
     * 
     * The matrix a is over written and no attempt is made at this
     * time to handle singular matrices.
     */
    public final static boolean Inverse(final int n, float[][] a, float[] c){

        final int trm = (n-1);
        /*
         * Forward sweep
         */
        outer:
        for (int j = 0; j < trm; j++){
            /*
             * Row normalization and stack shifting
             */
            inner:
            for (int i = j; i < n; i++){

                if ( ZERO != a[i][j]){

                    for (int k = n; k >= j; k--){

                        a[i][k] = a[i][k] / a[i][j];
                    }
                }
                else if ( trm != i){

                    for (int l = (i+1); l < n; l++){

                        if ( ZERO != a[l][j]){

                            for (int k = j; k <= n; k++){

                                float atemp = a[i][k];
                                a[i][k] = a[l][k];
                                a[l][k] = atemp;
                            }
                        }
                        else if ( l == trm ){
                            break inner;
                        }
                    }
                }
            }
            /*
             * Upper triangularization
             */
            for (int i = (j+1); i < n; i++){

                if ( ZERO != a[i][j]){

                    for (int k = j; k <= n; k++){

                        a[i][k] = a[i][k] - a[j][k];
                    }
                }
            }
        }
        /*
         * Singularity check
         */
        if ( ZERO == a[trm][trm]){

           return true;
        }
        /*
         * Back substitution
         */
        else {
            for (int j = trm; -1 < j; j--){

                c[j] = a[j][n] / a[j][j];

                for (int i = (j+1); i < n; i++){

                    c[j] = c[j] - c[i] * a[j][i] / a[j][j];
                }
            }
            return false;
        }
    }
}
