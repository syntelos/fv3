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

/**
 * Create a cylinder from radius and depth with the circular faces
 * parallel to XY, YZ or ZX and the solid centered at (0,0,0).
 */
public abstract class Cylinder
    extends Convex
{
    public static class XY
        extends Cylinder
    {
        public XY(double r, double d){
            this(r,d,Error.Default);
        }
        public XY(double r, double d, double e){
            super(r,d,e);

            final double[] cv = CV(r,e);

            final double dd2 = (d/2.0);

            final double z0 = -dd2;
            final double z1 = +dd2;

            final int count = cv.length;

            for (int cc = 0; cc < count; ){

                double x0 = cv[cc++];
                double y0 = cv[cc++];
                double x1, y1;

                if (cc < count){
                    x1 = cv[cc];
                    y1 = cv[cc+1];
                }
                else {
                    x1 = cv[0];
                    y1 = cv[1];
                }

                /*
                 * Triangle fan disk (Z+)
                 */
                this.add(0.0, 0.0,  z1,
                         x0,   y0,  z1,
                         x1,   y1,  z1);
                /*
                 * Quad triangle pair
                 */
                this.add(x0, y0, z1,
                         x0, y0, z0,
                         x1, y1, z0);
                this.add(x0, y0, z1,
                         x1, y1, z0,
                         x1, y1, z1);
                /*
                 * Triangle fan disk (Z-)
                 */
                this.add(0.0, 0.0,  z0,
                         x1,   y1,  z0,
                         x0,   y0,  z0);
            }
        }
        public XY(XY c){
            super(c);
        }
    }
    public static class YZ
        extends Cylinder
    {
        public YZ(double r, double d){
            this(r,d,Error.Default);
        }
        public YZ(double r, double d, double e){
            super(r,d,e);

            final double[] cv = CV(r,e);

            final double dd2 = (d/2.0);

            final double x0 = -dd2;
            final double x1 = +dd2;

            final int count = cv.length;

            for (int cc = 0; cc < count; ){

                double y0 = cv[cc++];
                double z0 = cv[cc++];
                double z1, y1;

                if (cc < count){
                    y1 = cv[cc];
                    z1 = cv[cc+1];
                }
                else {
                    y1 = cv[0];
                    z1 = cv[1];
                }

                /*
                 * Triangle fan disk (X+)
                 */
                this.add(x1,  0.0, 0.0,
                         x1,   y0,  z0,
                         x1,   y1,  z1);
                /*
                 * Quad triangle pair
                 */
                this.add(x1, y0, z0,
                         x0, y0, z0,
                         x0, y1, z1);
                this.add(x1, y0, z0,
                         x0, y1, z1,
                         x1, y1, z1);
                /*
                 * Triangle fan disk (X-)
                 */
                this.add(x0,  0.0, 0.0,
                         x0,   y1,  z1,
                         x0,   y0,  z0);
            }
        }
        public YZ(YZ c){
            super(c);
        }
    }
    public static class ZX
        extends Cylinder
    {
        public ZX(double r, double d){
            this(r,d,Error.Default);
        }
        public ZX(double r, double d, double e){
            super(r,d,e);

            final double[] cv = CV(r,e);

            final double dd2 = (d/2.0);

            final double y0 = -dd2;
            final double y1 = +dd2;

            final int count = cv.length;

            for (int cc = 0; cc < count; ){

                double z0 = cv[cc++];
                double x0 = cv[cc++];
                double z1, x1;

                if (cc < count){
                    z1 = cv[cc];
                    x1 = cv[cc+1];
                }
                else {
                    z1 = cv[0];
                    x1 = cv[1];
                }

                /*
                 * Triangle fan disk (Y+)
                 */
                this.add(0.0,  y1, 0.0,
                         x0,   y1,  z0,
                         x1,   y1,  z1);
                /*
                 * Quad triangle pair
                 */
                this.add(x0, y1, z0,
                         x0, y0, z0,
                         x1, y0, z1);
                this.add(x0, y1, z0,
                         x1, y0, z1,
                         x1, y1, z1);
                /*
                 * Triangle fan disk (Y-)
                 */
                this.add(0.0,  y0, 0.0,
                         x1,   y0,  z1,
                         x0,   y0,  z0);
            }
        }
        public ZX(ZX c){
            super(c);
        }
    }


    public final double radius, depth, error;


    protected Cylinder(double r, double d, double e){
        super(0);
        if (r == r && 0.0 < r){
            if (d == d && 0.0 < d){
                if (Error.Circle.V(e)){
                    this.radius = r;
                    this.depth = d;
                    this.error = e;
                }
                else
                    throw new IllegalArgumentException(String.format("Invalid error %g",e));
            }
            else
                throw new IllegalArgumentException(String.format("Invalid depth %g",d));
        }
        else
            throw new IllegalArgumentException(String.format("Invalid radius %g",r));
    }
    protected Cylinder(Cylinder c){
        super(c);
        this.radius = c.radius;
        this.depth = c.depth;
        this.error = c.error;
    }




    /**
     * @param r Radius of circle
     * @param e Error between zero and one
     * @return Circle vertices for R in (domain,range) order
     */
    protected final static double[] CV(double r, double e){

        if (0.0 < e){

            final int cn = Error.Circle.N(r,e);

            final double ds = (PI_M2 / (double)cn);

            double[] cv = new double[cn<<1];
            double a = 0.0;

            if (r != 1.0){

                for (int idx = 0, count = (cn<<1); idx < count; ){

                    cv[idx++] = (r*Math.cos(a));
                    cv[idx++] = (r*Math.sin(a));

                    a += ds;
                }
            }
            else {

                for (int idx = 0, count = (cn<<1); idx < count; ){

                    cv[idx++] = Math.cos(a);
                    cv[idx++] = Math.sin(a);

                    a += ds;
                }
            }
            return cv;
        }
        else
            throw new IllegalArgumentException(String.valueOf(e));
    }
}
