/*
 * FastArea.c++
 * Copyright 2002, Daniel Sunday, all rights reserved.
 * 
 * This code may be freely used and modified for any purpose providing
 * that this copyright notice is included with it.
 * 
 * SoftSurfer makes no warranty for this code, and cannot be held
 * liable for any real or imagined damage resulting from its use.
 * 
 * Users of this code must verify correctness for their application.
 */
/*
 * FastArea.java
 * Copyright (c) 2010 John Pritchard, all rights reserved.
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
 * From the paper and source code "Fast Polygon Area and Newell Normal
 * Computation", Daniel Sunday, Journal of Graphics Tools, 7(2):9-13,
 * 2002
 * 
 * @see http://jgt.akpeters.com/papers/Sunday02/
 * @see http://softsurfer.com/Archive/algorithm_0101/algorithm_0101.htm
 * @author Daniel Sunday
 * @author John Pritchard
 */
public abstract class FastArea
    extends Abstract
{
    /**
     * @param x List of X coordinates in polygon
     * @param y List of Y coordinates in polygon
     * @return the signed area of a 2D polygon
     */
    public final static double Area(double[] x, double[] y){

        if (x.length == y.length){
            int n = x.length;
            {
                /*
                 * Guarantee the first two vertices are also at array end
                 * 
                 *   x[n]   = x[0];    y[n] = y[0];
                 * 
                 *   x[n+1] = x[1];  y[n+1] = y[1];
                 */
                int t = (n-1);
                if (EEQ(x[t],x[0]) && EEQ(y[t],y[0])){
                    double[] copier;
                    {
                        copier = new double[n+1];
                        System.arraycopy(x,0,copier,0,n);
                        x = copier;
                    }
                    {
                        copier = new double[n+1];
                        System.arraycopy(y,0,copier,0,n);
                        y = copier;
                    }
                    n -= 1;

                    x[t+1] = x[1];
                    y[t+1] = y[1];
                }
                else if (EEQ(x[t-1],x[0]) && EEQ(y[t-1],y[0]) &&
                         EEQ(x[t],x[1]) && EEQ(y[t],y[1]))
                {
                    n -= 2;
                }
                else {
                    double[] copier;
                    {
                        copier = new double[n+2];
                        System.arraycopy(x,0,copier,0,n);
                        x = copier;
                    }
                    {
                        copier = new double[n+2];
                        System.arraycopy(y,0,copier,0,n);
                        y = copier;
                    }
                    t = n;

                    x[t] = x[0];
                    y[t] = y[0];
                    x[t+1] = x[1];
                    y[t+1] = y[1];
                }
            }

            double sum = 0.0;
            double xm, ylow, yhigh;

            for (int i = 0; i < n; i++){

                ylow = y[i];

                xm = x[i+1];

                yhigh = y[i+2];

                sum += (xm * (yhigh - ylow));
            }
            return (sum / 2.0);
        }
        else
            throw new IllegalArgumentException();
    }
    /**
     * @param face Face coordinates list in (X,Y,Z)+ order
     * @return The approximate unit normal of a 3D nearly planar polygon
     */
    public final static double Area(double[] face){
        double[][] c = Face(face);
        double[] x = c[0], y = c[1], z = c[2];

        double[] n = Normal(x,y,z);

        return Area(x,y,z,n[0],n[1],n[2]);
    }
    /**
     * @param face Face coordinates list in ((X,Y,Z))+ order
     * @param n Normal in (X,Y,Z)
     * @return The approximate unit normal of a 3D nearly planar polygon
     */
    public final static double Area(double[][] face, double[] n){
        double[][] c = Face(face);
        double[] x = c[0], y = c[1], z = c[2];

        return Area(x,y,z,n[0],n[1],n[2]);
    }
    /**
     * @param x Planar polygon
     * @param y Planar polygon
     * @param z Planar polygon
     * @param n Plane normal
     * @return the signed area of a 3D planar polygon (given normal vector)
     */
    public final static double Area(double[] x, double[] y, double[] z,
                                    double nx, double ny, double nz)
    {
        /*
         * Length of normal
         */
        double len = Math.sqrt(nx*nx + ny*ny + nz*nz);

        /* 
         * Select largest normal coordinate to ignore for projection
         */
        double ax = Math.abs(nx);
        double ay = Math.abs(ny);
        double az = Math.abs(nz);
    
        if (ax > ay) {

            if (ax > az)			           /// ignore x-coord
                return Area(y, z) * (len / nx);
        }
        else if (ay > az)			           /// ignore y-coord

            return Area(z, x) * (len / ny);
        /*
         */
        return Area(x, y) * (len / nz);        /// ignore z-coord
    }
    /**
     * @param face Face coordinates list in (X,Y,Z)+ order
     * @return The approximate unit normal of a 3D nearly planar polygon
     */
    public final static double[] Normal(double[] face){
        double[][] c = Face(face);
        return Normal(c[0],c[1],c[2]);
    }
    /**
     * @param face Face coordinates list in ((X,Y,Z))+ order
     * @return The approximate unit normal of a 3D nearly planar polygon
     */
    public final static double[] Normal(double[][] face){
        double[][] c = Face(face);
        return Normal(c[0],c[1],c[2]);
    }
    /**
     * @return The approximate unit normal of a 3D nearly planar polygon
     */
    public final static double[] Normal(double[] x, double[] y, double[] z){

        /* Newell normal
         */
        double nwx = Area(y, z);
        double nwy = Area(z, x);
        double nwz = Area(x, y);

        /* Length of the Newell normal
         * 
         * (Area of polygon = length of Newell normal)
         */
        double nlen = Math.sqrt( nwx*nwx + nwy*nwy + nwz*nwz );
        /* 
         * Unit normal
         */
        double nx = nwx / nlen;
        double ny = nwy / nlen;
        double nz = nwz / nlen;

        return new double[]{nx,ny,nz};
    }
    /**
     * @param face Face coordinates list in (X,Y,Z)+ order
     * @return Face coordinates list in ((X)+,(Y)+,(Z)+) order
     */
    public final static double[][] Face(double[] face){
        final int v = face.length;
        final int n = (v/3);
        double[][] re = new double[3][n];

        for (int fc = 0, rc = 0; fc < v; rc++){
            re[0][rc] = face[fc++];
            re[1][rc] = face[fc++];
            re[2][rc] = face[fc++];
        }
        return re;
    }
    /**
     * @param face Face coordinates list in ((X,Y,Z))+ order
     * @return Face coordinates list in ((X)+,(Y)+,(Z)+) order
     */
    public final static double[][] Face(double[][] face){
        int n = face.length;
        double[][] re = new double[3][n];

        for (int cc = 0; cc < n; cc++){
            re[0][cc] = face[cc][0];
            re[1][cc] = face[cc][1];
            re[2][cc] = face[cc][2];
        }
        return re;
    }
}
