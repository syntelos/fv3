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
import fv3.csg.u.Vertex;
import fv3.math.Matrix;
import fv3.math.VertexArrayProfile;

/**
 * Rotate a convex solid centered on a known coordinate in the axis of
 * rotation.
 * 
 * The solid must be compiled into its vertex array (super class)
 * before rotation.
 * 
 * This works by selecting all points in the plane normal to the axis
 * of rotation at the plane coordinate within a plane epsilon.
 * 
 * Correctness depends on the desired rotation profile (exclusively)
 * lying in the plane coordinate within the plane epsilon.
 * 
 * @see fv3.math.VertexArrayProfile
 * @author John Pritchard
 */
public abstract class Rotation
    extends NonConvex
{
    public static class Z
        extends Rotation
    {
        public Z(float r, float e, Convex s){
            this(r,e,s,ZERO);
        }
        public Z(float r, float e, Convex s, float pz){
            this(r,e,s,pz,EPSILON);
        }
        public Z(float r, float e, Convex s, float pz, float pe){
            super(N,r,e);
            if (null != s){
                VertexArrayProfile sp = s.profileXY(pz,pe);

                Vertex[][] t = this.list(sp);

                final int cn = sp.countVertices();

                for (int ru = 0, rv = 1; ru < this.rn; ru++, rv++){

                    if (rv == this.rn)
                        rv = 0;

                    for (int cu = 0, cv = 1; cu < cn; cu++, cv++){

                        if (cv == cn)
                            cv = 0;

                        Vertex qa = t[ru][cu];
                        Vertex qb = t[rv][cu];
                        Vertex qc = t[rv][cv];
                        Vertex qd = t[ru][cv];

                        this.add(new Face(this, new Face.Name(this,(ru*cu),String.format("(%d,%d),(%d,%d),(%d,%d)",ru,cu,rv,cu,ru,cv)),
                                          qa, qb, qd));
                        this.add(new Face(this, new Face.Name(this,(ru*cu),String.format("(%d,%d),(%d,%d),(%d,%d)",rv,cu,rv,cv,ru,cv)),
                                          qb, qc, qd));
                    }
                }
            }
            else
                throw new IllegalArgumentException();
        }

        protected Vertex[][] list(VertexArrayProfile sp){

            final int cn = sp.countVertices();

            Vertex[][] re = new Vertex[this.rn][cn];

            float ra = ZERO;

            for (int ru = 0; ru < this.rn; ru++){

                Matrix m = new Matrix().rotateZ(ra);

                Vertex[] rel = re[ru];

                for (int cu = 0; cu < cn; cu++){

                    rel[cu] = new Vertex(m.transform(sp.getVertex(cu)));
                }
                ra += this.rs;
            }
            return re;
        }
        protected final static String N = "Rotation.Z";
    }


    public final float radius;
    public final float error;
    protected final int rn;
    protected final float rs;

    protected Rotation(String n, float r, float e){
        super(n,0);
        if (r == r && ZERO < r){
            this.radius = r;
            if (e == e && ZERO < e){
                this.error = e;
                /*
                 * Arc of rotation
                 */
                this.rn = Error.Circle.N(r,e);

                this.rs = (PI_M2 / (float)this.rn);
            }
            else
                throw new IllegalArgumentException();
        }
        else
            throw new IllegalArgumentException();
    }

}
