/*
 * fv3
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
 * Sort an (X,Y,Z)+ list of vertices on any two dimensions with
 * rotational order.
 * 
 * Rotational order in two dimensions sorts coordinates in quadrants
 * as Q1 &lt; Q2 &lt; Q3 &lt; Q4.
 * 
 * The independent axis is one of X, Y or Z not in the two dimensional
 * sort.  It is the excluded dimension.

 * <table border="0">
 * <thead>
 * <tr>
 * <th>Independent Axis</th><th>Domain</th><th>Range</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>Z</td><td>X</td><td>Y</td>
 * </tr>
 * <tr>
 * <td>X</td><td>Z</td><td>Y</td>
 * </tr>
 * <tr>
 * <td>Y</td><td>X</td><td>Z</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @author John Pritchard
 */
public final class QuickSort
    extends java.lang.Object
    implements fv3.math.Notation
{
    /**
     * @param m Domain coordinate
     * @param n Range coordinate
     */
    private final static double M(double m, double n){
        return Math.atan(n/m);
    }
    private final static double MX = Short.MAX_VALUE;
    private final static int Index(double[] vertices, int ofs, int fix){

        double x = vertices[ofs+X];
        double y = vertices[ofs+Y];
        double z = vertices[ofs+Z];

        switch (fix){
        case X:
            return (int)(MX*M(z,y));
        case Y:
            return (int)(MX*M(x,z));
        case Z:
            return (int)(MX*M(x,y));

        default:
            throw new IllegalArgumentException(String.valueOf(fix));
        }
    }
    private final static int Mod3 = (Integer.MAX_VALUE-3);


    public final double[] vertices;

    public final int fix;

    private final int[] index;


    /**
     * Sort vertices on any two dimensions for rotational order.
     * 
     * @param vertices Vertex list in (X,Y,Z)+
     * @param fix Fixed or independent dimension from {@link fv3.math.Notation} 
     */
    public QuickSort(double[] vertices, int fix){
        super();
        this.vertices = vertices;
        this.fix = fix;

        final int len = vertices.length;
        final int vc = (len/3);
        {
            int[] index = new int[vc];
            for (int cc = 0; cc < vc; cc++){

                index[cc] = Index(vertices,(cc*3),fix);
            }
            this.index = index;
        }

        this.sort(0,(len-3));
    }


    private int select(int left, int right){
        final int xl = (left/3);
        final int xr = (right/3);
        final int xd = (xr-xl);
        if (2 > xd)
            return this.index[xl];
        else {
            int[] select = new int[xd];
            System.arraycopy(this.index,left,select,0,xd);
            java.util.Arrays.sort(select);
            return select[xd>>1];
        }
    }
    private int index(int index){
        return this.index[ index / 3 ];
    }
    private void sort(int left, int right){

        int i = left, j = right;

        double[] tmp = new double[3];

        double pivot = this.select(left,right);

        while (i <= j) {

            while (this.index(i) < pivot)

                i += 3;

            while (this.index(j) > pivot)

                j -= 3;

            if (i <= j) {

                System.arraycopy(vertices,i,tmp,0,3);      // tmp = i;
                System.arraycopy(vertices,j,vertices,i,3); // i = j;
                System.arraycopy(tmp,0,vertices,j,3);      // j = tmp;

                i += 3;
                j -= 3;
            }
        }

        if (left < j)

            this.sort(left, j);

        if (i < right)

            this.sort(i, right);
    }
}
