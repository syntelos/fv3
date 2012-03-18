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
package fv3.model;

import fv3.math.VertexArray;

/**
 * Vertex array builders for subsequent matrix transformations via
 * {@link fv3.math.VertexArray#transform(fv3.math.Matrix)}.
 */
public abstract class Geom
    extends VertexArray
{

    /**
     * Line 
     */
    public static class Line
        extends Geom
    {
        public Line(float x0, float y0, float z0,
                    float x1, float y1, float z1)
        {
            super(Type.Lines,2);

            this.setVertex(0,x0,y0,z0);
            this.setVertex(1,x1,y1,z1);
        }
        public Line(Line ln){
            super(ln);
        }
    }

    /**
     * Circle
     */
    public static abstract class Circle
        extends Geom
    {
        /**
         * Construct a circle in the X-Y plane centered at (0,0,0)
         */
        public static class XY
            extends Circle
        {
            public XY(float r){
                super(r);

                final float ds = (PI_M2 / (float)this.countVertices);

                float a = ZERO;

                if (r != 1.0f){

                    for (int idx = 0, count = this.countVertices; idx < count; idx++){

                        this.setVertex(idx,(r*(float)Math.cos(a)),(r*(float)Math.sin(a)),ZERO);

                        a += ds;
                    }
                }
                else {

                    for (int idx = 0, count = this.countVertices; idx < count; idx++){

                        this.setVertex(idx,(float)Math.cos(a),(float)Math.sin(a),ZERO);

                        a += ds;
                    }
                }
            }
            public XY(XY c){
                super(c);
            }
        }
        /**
         * Construct a circle in the Y-Z plane centered at (0,0,0)
         */
        public static class YZ
            extends Circle
        {
            public YZ(float r){
                super(r);

                final float ds = (PI_M2 / (float)this.countVertices);

                float a = ZERO;

                if (r != 1.0f){

                    for (int idx = 0, count = this.countVertices; idx < count; idx++){

                        this.setVertex(idx,ZERO,(r*(float)Math.cos(a)),(r*(float)Math.sin(a)));

                        a += ds;
                    }
                }
                else {

                    for (int idx = 0, count = this.countVertices; idx < count; idx++){

                        this.setVertex(idx,ZERO,((float)Math.cos(a)),((float)Math.sin(a)));

                        a += ds;
                    }
                }
            }
            public YZ(YZ c){
                super(c);
            }
        }
        /**
         * Construct a circle in the Z-X plane centered at (0,0,0)
         */
        public static class ZX
            extends Circle
        {
            public ZX(float r){
                super(r);

                final float ds = (PI_M2 / (float)this.countVertices);

                float a = ZERO;

                if (r != 1.0f){

                    for (int idx = 0, count = this.countVertices; idx < count; idx++){

                        this.setVertex(idx,(r*(float)Math.sin(a)),ZERO,(r*(float)Math.cos(a)));

                        a += ds;
                    }
                }
                else {

                    for (int idx = 0, count = this.countVertices; idx < count; idx++){

                        this.setVertex(idx,((float)Math.sin(a)),ZERO,((float)Math.cos(a)));

                        a += ds;
                    }
                }
            }
            public ZX(ZX c){
                super(c);
            }
        }


        protected Circle(float r){
            super(Type.LineLoop,(r*2.0f));
        }
        protected Circle(Circle c){
            super(c);
        }
    }


    protected Geom(Type t, int c){
        super(t,c);
    }
    protected Geom(Type t, float c){
        super(t, ((int)Math.ceil(c)));
    }
    protected Geom(Geom g){
        super(g);
    }
}
