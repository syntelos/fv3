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
 * parallel to XY, ZY or ZX and the solid centered at (0,0,0).
 */
public abstract class Cylinder
    extends Convex
{
    public static class XY
        extends Cylinder
    {
        public XY(double r, double d){
            this(r,d,ERROR);
        }
        public XY(double r, double d, double e){
            super(r,d,e);

            double[] cv = CV(r,e);

            double dd2 = (d/2.0);

            double z0 = -dd2;
            double z1 = +dd2;

            for (int cc = 0, count = cv.length; ; ){

                double x0 = cv[cc++];
                double y0 = cv[cc++];

                if (cc < count){
                    double x1 = cv[cc];
                    double y1 = cv[cc+1];

                    DCXY(this,
                         0.0, 0.0,  z0,
                         0.0, 0.0,  z1,
                         x0,   y0,  z0,
                         x1,   y1,  z1);
                }
                else {
                    double x1 = cv[0];
                    double y1 = cv[1];

                    DCXY(this,
                         0.0, 0.0,  z0,
                         0.0, 0.0,  z1,
                         x0,   y0,  z0,
                         x1,   y1,  z1);

                    break;
                }
            }
        }
        public XY(XY c){
            super(c);
        }
    }
    public static class ZY
        extends Cylinder
    {
        public ZY(double r, double d){
            this(r,d,ERROR);
        }
        public ZY(double r, double d, double e){
            super(r,d,e);

            double[] cv = CV(r,e);

            double dd2 = (d/2.0);

            double x0 = -dd2;
            double x1 = +dd2;

            for (int cc = 0, count = cv.length; ; ){

                double z0 = cv[cc++];
                double y0 = cv[cc++];

                if (cc < count){
                    double z1 = cv[cc];
                    double y1 = cv[cc+1];

                    DCZY(this,
                         x0,  0.0, 0.0,
                         x1,  0.0, 0.0,
                         x0,   y0,  z0,
                         x1,   y1,  z1);
                }
                else {

                    double z1 = cv[0];
                    double y1 = cv[1];

                    DCZY(this,
                         x0,  0.0, 0.0,
                         x1,  0.0, 0.0,
                         x0,   y0,  z0,
                         x1,   y1,  z1);

                    break;
                }
            }
        }
        public ZY(ZY c){
            super(c);
        }
    }
    public static class ZX
        extends Cylinder
    {
        public ZX(double r, double d){
            this(r,d,ERROR);
        }
        public ZX(double r, double d, double e){
            super(r,d,e);

            double[] cv = CV(r,e);

            double dd2 = (d/2.0);

            double y0 = -dd2;
            double y1 = +dd2;

            for (int cc = 0, count = cv.length; ; ){

                double z0 = cv[cc++];
                double x0 = cv[cc++];

                if (cc < count){
                    double z1 = cv[cc];
                    double x1 = cv[cc+1];

                    DCZX(this,
                         0.0,  y0, 0.0,
                         0.0,  y1, 0.0,
                         x0,   y0,  z0,
                         x1,   y1,  z1);
                }
                else {
                    double z1 = cv[0];
                    double x1 = cv[1];

                    DCZX(this,
                         0.0,  y0, 0.0,
                         0.0,  y1, 0.0,
                         x0,   y0,  z0,
                         x1,   y1,  z1);

                    break;
                }
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


    protected final static double ERROR = 0.0001;

    /**
     * @param r Radius of circle
     * @param e Error between zero and one (default 0.0001)
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
    protected final static void DCXY(Cylinder geom, 
                                     double cx0, double cy0, double cz0,
                                     double cx1, double cy1, double cz1,
                                     double  x0, double  y0, double  z0,
                                     double  x1, double  y1, double  z1)
    {
        /*
         * Triangle fan disk (0)
         */
        geom.add(cx0, cy0, cz0,
                 x0,   y0,  z0,
                 x1,   y1,  z0);
        /*
         * Quad triangle pair in depth
         */
        geom.add(x0, y0, z0,
                 x0, y0, z1,
                 x1, y1, z1);
        geom.add(x0, y0, z0,
                 x1, y1, z1,
                 x1, y1, z0);
        /*
         * Triangle fan disk (1)
         */
        geom.add(cx1, cy1, cz1,
                 x0,   y0,  z1,
                 x1,   y1,  z1);
    }
    protected final static void DCZY(Cylinder geom, 
                                     double cx0, double cy0, double cz0,
                                     double cx1, double cy1, double cz1,
                                     double  x0, double  y0, double  z0,
                                     double  x1, double  y1, double  z1)
    {
        /*
         * Triangle fan disk (0)
         */
        geom.add(cx0, cy0, cz0,
                 x0,   y0,  z0,
                 x0,   y1,  z1);
        /*
         * Quad triangle pair in depth
         */
        geom.add(x0, y0, z0,
                 x1, y0, z0,
                 x1, y1, z1);
        geom.add(x0, y0, z0,
                 x1, y1, z1,
                 x0, y1, z1);
        /*
         * Triangle fan disk (1)
         */
        geom.add(cx1, cy1, cz1,
                 x1,   y0,  z0,
                 x1,   y1,  z1);
    }
    protected final static void DCZX(Cylinder geom, 
                                     double cx0, double cy0, double cz0,
                                     double cx1, double cy1, double cz1,
                                     double  x0, double  y0, double  z0,
                                     double  x1, double  y1, double  z1)
    {
        /*
         * Triangle fan disk (0)
         */
        geom.add(cx0, cy0, cz0,
                 x0,   y0,  z0,
                 x1,   y0,  z1);
        /*
         * Quad triangle pair in depth
         */
        geom.add(x0, y0, z0,
                 x0, y1, z0,
                 x1, y1, z1);
        geom.add(x0, y0, z0,
                 x1, y1, z1,
                 x1, y0, z1);
        /*
         * Triangle fan disk (1)
         */
        geom.add(cx1, cy1, cz1,
                 x0,   y1,  z0,
                 x1,   y1,  z1);
    }
}
