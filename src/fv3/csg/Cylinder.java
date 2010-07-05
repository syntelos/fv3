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
    extends Solid
{
    public static class XY
        extends Cylinder
    {
        public XY(double r, double d){
            super(r,d);

            double[] cv = CV(r);

            double dd2 = (d/2.0);

            double z0 = -dd2;
            double z1 = +dd2;

            for (int cc = 0, count = cv.length; ; ){

                double x0 = cv[cc++];
                double y0 = cv[cc++];

                if (cc < count){
                    double x1 = cv[cc];
                    double y1 = cv[cc+1];

                    /*
                     * Triangle fan disk @ z0
                     */
                    this.add(0.0,0.0,z0,
                             x0,y0,z0,
                             x1,y1,z0);
                    /*
                     * Quad triangle pair in depth
                     */
                    this.add(x0,y0,z0,
                             x0,y0,z1,
                             x1,y1,z1);
                    this.add(x0,y0,z0,
                             x1,y1,z1,
                             x1,y1,z0);
                    /*
                     * Triangle fan disk @ z1
                     */
                    this.add(0.0,0.0,z1,
                             x1,y1,z1,
                             x0,y0,z1);
                }
                else
                    break;
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
            super(r,d);

            double[] cv = CV(r);

            double dd2 = (d/2.0);

            double x0 = -dd2;
            double x1 = +dd2;

            for (int cc = 0, count = cv.length; ; ){

                double z0 = cv[cc++];
                double y0 = cv[cc++];

                if (cc < count){
                    double z1 = cv[cc];
                    double y1 = cv[cc+1];

                    /*
                     * Triangle fan disk @ x0
                     */
                    this.add(x0,0.0,0.0,
                             x0,y0,z0,
                             x1,y1,z0);
                    /*
                     * Quad triangle pair in depth
                     */
                    this.add(x0,y0,z0,
                             x0,y0,z1,
                             x1,y1,z1);
                    this.add(x0,y0,z0,
                             x1,y1,z1,
                             x1,y1,z0);
                    /*
                     * Triangle fan disk @ x1
                     */
                    this.add(x1,0.0,0.0,
                             x1,y1,z1,
                             x0,y0,z1);
                }
                else
                    break;
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
            super(r,d);

            double[] cv = CV(r);

            double dd2 = (d/2.0);

            double y0 = -dd2;
            double y1 = +dd2;

            for (int cc = 0, count = cv.length; ; ){

                double z0 = cv[cc++];
                double x0 = cv[cc++];

                if (cc < count){
                    double z1 = cv[cc];
                    double x1 = cv[cc+1];

                    /*
                     * Triangle fan disk @ x0
                     */
                    this.add(0.0,y0,0.0,
                             x0,y0,z0,
                             x1,y1,z0);
                    /*
                     * Quad triangle pair in depth
                     */
                    this.add(x0,y0,z0,
                             x0,y0,z1,
                             x1,y1,z1);
                    this.add(x0,y0,z0,
                             x1,y1,z1,
                             x1,y1,z0);
                    /*
                     * Triangle fan disk @ x1
                     */
                    this.add(0.0,y0,0.0,
                             x1,y1,z1,
                             x0,y0,z1);
                }
                else
                    break;
            }
        }
        public ZX(ZX c){
            super(c);
        }
    }


    public final double radius, depth;


    protected Cylinder(double r, double d){
        super((int)Math.ceil(r*4.0));
        if (r == r && 0.0 < r){
            if (d == d && 0.0 < d){
                this.radius = r;
                this.depth = d;
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
    }


    /**
     * @param r Radius of circle
     * @return Circle vertices for R in (domain,range) order
     */
    protected final static double[] CV(double r){

        final int cn = (int)Math.ceil(r*2.0);
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
}
