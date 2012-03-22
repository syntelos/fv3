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
 * From the paper and source code "Fast Polygon Area and Newell Normal
 * Computation", Daniel Sunday, Journal of Graphics Tools, 7(2):9-13,
 * 2002
 * 
 * <h3>Signed Area</h3>
 * 
 * The Signed area for a polygon is positive when the vertices are
 * oriented counterclockwise around the polygon, and negative when
 * oriented clockwise.
 * 
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
    public final static float Area(float[] x, float[] y){

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
                    float[] copier;
                    {
                        copier = new float[n+1];
                        System.arraycopy(x,0,copier,0,n);
                        x = copier;
                    }
                    {
                        copier = new float[n+1];
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
                    float[] copier;
                    {
                        copier = new float[n+2];
                        System.arraycopy(x,0,copier,0,n);
                        x = copier;
                    }
                    {
                        copier = new float[n+2];
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

            float sum = ZERO;
            float xm, ylow, yhigh;

            for (int i = 0; i < n; i++){

                ylow = y[i];

                xm = x[i+1];

                yhigh = y[i+2];

                sum += (xm * (yhigh - ylow));
            }
            return (sum / 2.0f);
        }
        else
            throw new IllegalArgumentException();
    }
    /**
     * @param face 2D polygon coordinates list in (X,Y)+ order
     * @return The signed area of a 2D polygon
     */
    public final static float Area2(float[] vertices){

        final int count = vertices.length/2;

        float[] x = new float[count], y = new float[count];

        for (int v = 0, p = 0; p < count; p++){

            x[p] = vertices[v++];
            y[p] = vertices[v++];
        }
        return FastArea.Area(x,y);
    }
    /**
     * @param face Face coordinates list in (X,Y,Z)+ order
     * @return The approximate unit normal of a 3D nearly planar polygon
     */
    public final static float Area(float[] face){
        float[][] c = Face(face);
        float[] x = c[0], y = c[1], z = c[2];

        float[] n = Normal(x,y,z);

        return Area(x,y,z,n[0],n[1],n[2]);
    }
    /**
     * @param face Face coordinates list in ((X,Y,Z))+ order
     * @param n Normal in (X,Y,Z)
     * @return The approximate unit normal of a 3D nearly planar polygon
     */
    public final static float Area(float[][] face, float[] n){
        float[][] c = Face(face);
        float[] x = c[0], y = c[1], z = c[2];

        return Area(x,y,z,n[0],n[1],n[2]);
    }
    /**
     * @param x Planar polygon
     * @param y Planar polygon
     * @param z Planar polygon
     * @param n Plane normal
     * @return the signed area of a 3D planar polygon (given normal vector)
     */
    public final static float Area(float[] x, float[] y, float[] z,
                                    float nx, float ny, float nz)
    {
        /*
         * Length of normal
         */
        float len = (float)Math.sqrt(nx*nx + ny*ny + nz*nz);

        /* 
         * Select largest normal coordinate to ignore for projection
         */
        float ax = Math.abs(nx);
        float ay = Math.abs(ny);
        float az = Math.abs(nz);
    
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
    public final static float[] Normal(float[] face){
        float[][] c = Face(face);
        return Normal(c[0],c[1],c[2]);
    }
    /**
     * @param face Face coordinates list in ((X,Y,Z))+ order
     * @return The approximate unit normal of a 3D nearly planar polygon
     */
    public final static float[] Normal(float[][] face){
        float[][] c = Face(face);
        return Normal(c[0],c[1],c[2]);
    }
    /**
     * @return The approximate unit normal of a 3D nearly planar polygon
     */
    public final static float[] Normal(float[] x, float[] y, float[] z){

        /* Newell normal
         */
        float nwx = Area(y, z);
        float nwy = Area(z, x);
        float nwz = Area(x, y);

        /* Length of the Newell normal
         * 
         * (Area of polygon = length of Newell normal)
         */
        float nlen = (float)Math.sqrt( nwx*nwx + nwy*nwy + nwz*nwz );
        /* 
         * Unit normal
         */
        float nx = nwx / nlen;
        float ny = nwy / nlen;
        float nz = nwz / nlen;

        return new float[]{nx,ny,nz};
    }
    /**
     * @param face Face coordinates list in (X,Y,Z)+ order
     * @return Face coordinates list in ((X)+,(Y)+,(Z)+) order
     */
    public final static float[][] Face(float[] face){
        final int v = face.length;
        final int n = (v/3);
        float[][] re = new float[3][n];

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
    public final static float[][] Face(float[][] face){
        int n = face.length;
        float[][] re = new float[3][n];

        for (int cc = 0; cc < n; cc++){
            re[0][cc] = face[cc][0];
            re[1][cc] = face[cc][1];
            re[2][cc] = face[cc][2];
        }
        return re;
    }
}
