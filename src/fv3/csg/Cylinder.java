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
package fv3.csg;

import fv3.csg.u.Error;
import fv3.csg.u.Face;

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
        public XY(float r, float d){
            this(r,d,Error.Default);
        }
        public XY(float r, float d, float e){
            super(String.format(N,r,d),r,d,e);

            final float[] cv = CV(r,e);

            final float dd2 = (d/2.0f);

            final float z0 = -dd2;
            final float z1 = +dd2;

            final int count = cv.length;

            int fx = 0;

            for (int cc = 0; cc < count; ){

                float x0 = cv[cc++];
                float y0 = cv[cc++];
                float x1, y1;

                if (cc < count){
                    x1 = cv[cc];
                    y1 = cv[cc+1];
                }
                else {
                    x1 = cv[0];
                    y1 = cv[1];
                }

                this.add(new Face(this, new Face.Name(this,(fx++),"Triangle fan disk (Z+)"),
                                  ZERO, ZERO,  z1,
                                  x0,   y0,  z1,
                                  x1,   y1,  z1));

                this.add(new Face(this,new Face.Name(this,(fx++),"Quad triangle A"),
                                  x0, y0, z1,
                                  x0, y0, z0,
                                  x1, y1, z0));
                this.add(new Face(this, new Face.Name(this,(fx++),"Quad triangle B"),
                                  x0, y0, z1,
                                  x1, y1, z0,
                                  x1, y1, z1));

                this.add(new Face(this, new Face.Name(this,(fx++),"Triangle fan disk (Z-)"),
                                  ZERO, ZERO,  z0,
                                  x1,   y1,  z0,
                                  x0,   y0,  z0));
            }
        }
        public XY(XY c){
            super(c);
        }


        protected final static String N = "Cylinder.XY(%3.2g,%3.2g)";
    }
    public static class YZ
        extends Cylinder
    {
        public YZ(float r, float d){
            this(r,d,Error.Default);
        }
        public YZ(float r, float d, float e){
            super(String.format(N,r,d),r,d,e);

            final float[] cv = CV(r,e);

            final float dd2 = (d/2.0f);

            final float x0 = -dd2;
            final float x1 = +dd2;

            final int count = cv.length;

            int fx = 0;

            for (int cc = 0; cc < count; ){

                float y0 = cv[cc++];
                float z0 = cv[cc++];
                float z1, y1;

                if (cc < count){
                    y1 = cv[cc];
                    z1 = cv[cc+1];
                }
                else {
                    y1 = cv[0];
                    z1 = cv[1];
                }

                this.add(new Face(this, new Face.Name(this,(fx++),"Triangle fan disk (X+)"),
                                  x1,  ZERO, ZERO,
                                  x1,   y0,  z0,
                                  x1,   y1,  z1));

                this.add(new Face(this, new Face.Name(this,(fx++),"Quad triangle A"),
                                  x1, y0, z0,
                                  x0, y0, z0,
                                  x0, y1, z1));
                this.add(new Face(this, new Face.Name(this,(fx++),"Quad triangle B"),
                                  x1, y0, z0,
                                  x0, y1, z1,
                                  x1, y1, z1));

                this.add(new Face(this, new Face.Name(this,(fx++),"Triangle fan disk (X-)"),
                                  x0,  ZERO, ZERO,
                                  x0,   y1,  z1,
                                  x0,   y0,  z0));
            }
        }
        public YZ(YZ c){
            super(c);
        }

        protected final static String N = "Cylinder.YZ(%3.2g,%3.2g)";
    }
    public static class ZX
        extends Cylinder
    {
        public ZX(float r, float d){
            this(r,d,Error.Default);
        }
        public ZX(float r, float d, float e){
            super(String.format(N,r,d),r,d,e);

            final float[] cv = CV(r,e);

            final float dd2 = (d/2.0f);

            final float y0 = -dd2;
            final float y1 = +dd2;

            final int count = cv.length;

            int fx = 0;

            for (int cc = 0; cc < count; ){

                float z0 = cv[cc++];
                float x0 = cv[cc++];
                float z1, x1;

                if (cc < count){
                    z1 = cv[cc];
                    x1 = cv[cc+1];
                }
                else {
                    z1 = cv[0];
                    x1 = cv[1];
                }

                this.add(new Face(this, new Face.Name(this,(fx++),"Triangle fan disk (Y+)"),
                                  ZERO,  y1, ZERO,
                                  x0,   y1,  z0,
                                  x1,   y1,  z1));

                this.add(new Face(this, new Face.Name(this,(fx++),"Quad triangle A"),
                                  x0, y1, z0,
                                  x0, y0, z0,
                                  x1, y0, z1));
                this.add(new Face(this, new Face.Name(this,(fx++),"Quad triangle B"),
                                  x0, y1, z0,
                                  x1, y0, z1,
                                  x1, y1, z1));

                this.add(new Face(this, new Face.Name(this,(fx++),"Triangle fan disk (Y-)"),
                                  ZERO,  y0, ZERO,
                                  x1,   y0,  z1,
                                  x0,   y0,  z0));
            }
        }
        public ZX(ZX c){
            super(c);
        }

        protected final static String N = "Cylinder.ZX(%3.2g,%3.2g)";
    }


    public final float radius, depth, error;


    protected Cylinder(String n, float r, float d, float e){
        super(n,0);
        if (r == r && ZERO < r){
            if (d == d && ZERO < d){
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
        super(c.name.desc,c);
        this.radius = c.radius;
        this.depth = c.depth;
        this.error = c.error;
    }




    /**
     * @param r Radius of circle
     * @param e Error between zero and one
     * @return Circle vertices for R in (domain,range) order
     */
    protected final static float[] CV(float r, float e){

        if (ZERO < e){

            final int cn = Error.Circle.N(r,e);

            final float ds = (PI_M2 / (float)cn);

            float[] cv = new float[cn<<1];
            float a = ZERO;

            if (r != 1.0f){

                for (int idx = 0, count = (cn<<1); idx < count; ){

                    cv[idx++] = (r*(float)Math.cos(a));
                    cv[idx++] = (r*(float)Math.sin(a));

                    a += ds;
                }
            }
            else {

                for (int idx = 0, count = (cn<<1); idx < count; ){

                    cv[idx++] = (float)Math.cos(a);
                    cv[idx++] = (float)Math.sin(a);

                    a += ds;
                }
            }
            return cv;
        }
        else
            throw new IllegalArgumentException(String.valueOf(e));
    }
}
