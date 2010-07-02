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
package fv3.model;

import fv3.math.VertexArray;

import javax.media.opengl.GL2;

/**
 * Vertex array builders for subsequent matrix transformations via
 * {@link fv3.math.VertexArray#transform(fv3.math.Matrix)}.
 */
public abstract class Geom
    extends VertexArray
{

    public static class Line
        extends Geom
    {
        /**
         * Line 
         */
        public Line(double x0, double y0, double z0,
                    double x1, double y1, double z1)
        {
            super(Type.Lines,2);

            this.setVertex(0,x0,y0,z0);
            this.setVertex(1,x1,y1,z1);
        }
        public Line(Line ln){
            super(ln);
        }
    }

    public static class Circle
        extends Geom
    {
        /**
         * Construct a circle in the X-Y plane centered at (0,0,0)
         */
        public Circle(double r){
            super(Type.LineLoop,((r*r)/2.0));

            final double ds = (PI_M2 / (double)this.countVertices);

            double a = 0.0;

            if (r != 1.0){

                for (int idx = 0, count = this.countVertices; idx < count; idx++){

                    this.setVertex(idx,(r*Math.cos(a)),(r*Math.sin(a)),0.0);

                    a += ds;
                }
            }
            else {

                for (int idx = 0, count = this.countVertices; idx < count; idx++){

                    this.setVertex(idx,Math.cos(a),Math.sin(a),0.0);

                    a += ds;
                }
            }
        }
        public Circle(Circle c){
            super(c);
        }
    }


    protected Geom(Type t, int c){
        super(t,c);
    }
    protected Geom(Type t, double c){
        super(t, ((int)Math.ceil(c)));
    }
    protected Geom(Geom g){
        super(g);
    }
}
