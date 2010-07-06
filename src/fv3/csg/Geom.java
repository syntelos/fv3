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
package fv3.csg;

import fv3.math.Vector;
import static fv3.math.Vector.Direction1.*;
import fv3.math.VertexArray;

/**
 * CSG Geom subclasses are convex solids centered at (0,0,0).  This
 * class provides tools for adding outward facing vertices based on
 * these constraint.  Using this class, the order of vertices within a
 * face
 */
public class Geom
    extends Solid
{
    public enum Norm {
        NX, NY, NZ, NXY, NXZ, NZY;
    }

    public Geom(int c){
        super(c);
    }
    public Geom(VertexArray v){
        super(v);
    }


    /**
     * If face is improperly ordered, reorder it (by replacement)
     * before adding it internally.
     */
    protected Solid addN(Face f){

        final Vector normal = f.getNormal();
        final double[] n = normal.array();
        final double nX = n[X];
        final double nY = n[Y];
        final double nZ = n[Z];

        final double[] c = f.centroid();
        final double cX = c[X];
        final double cY = c[Y];
        final double cZ = c[Z];

        switch (normal.direction1()){
        case DX:

            if (Sign(nX) == Sign(cX))
                super.addN(f);
            else {
                f.deconstruct();
                super.addN(new Face(this,f.a,f.c,f.b));
            }
            return this;

        case DY:

            if (Sign(nY) == Sign(cY))
                super.addN(f);
            else {
                f.deconstruct();
                super.addN(new Face(this,f.a,f.c,f.b));
            }
            return this;

        case DZ:

            if (Sign(nZ) == Sign(cZ))
                super.addN(f);
            else {
                f.deconstruct();
                super.addN(new Face(this,f.a,f.c,f.b));
            }
            return this;

        default:
            throw new IllegalStateException();
        }
    }
    /**
     * Convert geometric parameters with maximum error into a
     * resolution parameter.
     */
    public static class Error
        extends java.lang.Object
        implements fv3.csg.Notation
    {
        public final static class Circle
            extends Error
        {
            public final static double Min = 1e-6;
            public final static double Max = 1e1;

            /**
             * @param e Maximum sagitta
             * @return Generally acceptable value for 'e'
             */
            public final static boolean V(double e){
                return (e == e && Error.Circle.Min <= e && Error.Circle.Max >= e);
            }
            /**
             * @param r Radius of circle
             * @param n Number of arc sectors in circle
             * @return Sagitta for 'r' and 'n'
             */
            public final static double E(double r, int n){

                final double s = (r*(PI_M2 / (double)n));

                final double l = (s / 2.0);

                return (r-Math.sqrt((r*r)-(l*l)));
            }
            /**
             * Scanner for largest value of N satisfying requirement
             * E.
             * 
             * @param r Radius
             * @param e Maximum sagitta
             * @return An even number of circular arc-parts greater
             * than or equal to four that satisfies requirement E
             */
            public final static int N(double r, double e){

                if (0.0 < r && 0.0 < e){

                    double qr0 = 4;

                    while (true){

                        int n0 = (int)(r * qr0);

                        if (E(r,n0) <= e){

                            double qr1 = (qr0 - 2.0);

                            while (true){

                                int n1 = (int)(r * qr1);

                                if (E(r,n1) > e)

                                    return (int)(r * (qr1 + 2.0));

                                else {
                                    qr1 -= 2.0;
                                }
                            }
                        }
                        else {
                            qr0 *= 2;
                        }
                    }
                }
                else
                    throw new IllegalArgumentException();
            }
        }
    }
}
