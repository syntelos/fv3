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

/**
 * CSG Rotation subclasses are nonconvex solids centered at (0,0,0).  
 */
public class Torus
    extends Rotation
{
    public static class XY
        extends Torus
    {
        public XY(double iR, double oR){
            this(iR,oR,Error.Default);
        }
        public XY(double iR, double oR, double e){
            super(iR,oR,e);

            Vertex[][] t = XY.Vertex.List(this);

            for (int ru = 0, rv = 1; ru < this.rn; ru++, rv++){

                if (rv >= this.rn)
                    rv = 0;

                for (int cu = 0, cv = 1; cu < this.cn; cu++, cv++){

                    if (cv >= this.cn)
                        cv = 0;

                    Vertex qa = t[ru][cu];
                    Vertex qb = t[ru][cv];
                    Vertex qc = t[rv][cu];
                    Vertex qd = t[rv][cv];

                    Vector qn = new Vector(qa.n).add(qb.n).add(qc.n).add(qd.n).div(4);

                    this.add(qa.x,qa.y,qa.z,
                             qb.x,qb.y,qb.z,
                             qc.x,qc.y,qc.z,
                             qn);

                    this.add(qc.x,qc.y,qc.z,
                             qd.x,qd.y,qd.z,
                             qa.x,qa.y,qa.z,
                             qn);
                }
            }
        }
        public XY(XY t){
            super(t);
        }


        protected static class Vertex {

            protected static Vertex[][] List(Torus t){

                Vertex[][] re = new Vertex[t.rn][t.cn];

                double ra = 0.0, ca;

                for (int ru = 0; ru < t.rn; ru++){

                    final double cos_ra = Math.cos(ra);
                    final double sin_ra = Math.sin(ra);

                    Vertex[] rel = re[ru];

                    ca = 0.0;
                    for (int cu = 0; cu < t.cn; cu++){

                        rel[cu] = new Vertex(t.innerRadius,t.outerRadius,
                                             cos_ra, sin_ra, ca);

                        ca += t.cs;
                    }
                    ra += t.rs;
                }
                return re;
            }


            protected final double x, y, z;
            protected final Vector n;

            protected Vertex(double iR, double oR, 
                             double cos_ra, double sin_ra, 
                             double ca)
            {
                super();

                final double cos_ca = Math.cos(ca);
                final double sin_ca = Math.sin(ca);

                final double oR_P_cosCa_M_iR = (oR+cos_ca*iR);

                this.x = cos_ra*oR_P_cosCa_M_iR;
                this.y = sin_ra*oR_P_cosCa_M_iR;
                this.z = sin_ca*iR;
                /*
                 * Tangent of arc of revolution
                 */
                final double rx = -sin_ra;
                final double ry = cos_ra;
                final double rz = 0;
                /*
                 * Tangent of circle in revolution
                 */
                final double cx = cos_ra*(-sin_ca);
                final double cy = sin_ra*(-sin_ca);
                final double cz = cos_ca;
                /*
                 * Normal: cross product of tangents
                 */
                final double nx = ry*cz - rz*cy;
                final double ny = rz*cx - rx*cz;
                final double nz = rx*cy - ry*cx;
                final double nlen = Math.sqrt(nx*nx + ny*ny + nz*nz);

                this.n = new Vector( (nx / nlen), (ny / nlen), (nz / nlen));
            }
        }
    }


    public final double innerRadius, outerRadius, error;

    protected final int cn,rn;

    protected final double cs,rs;


    public Torus(double innerR, double outerR, double e){
        super(0);
        if (innerR == innerR && 0.0 < innerR){
            if (outerR == outerR && 0.0 < outerR){
                if (outerR > innerR){
                    if (Error.Circle.V(e)){
                        this.innerRadius = innerR;
                        this.outerRadius = outerR;
                        this.error = e;
                        /*
                         * Shape in rotation
                         */
                        final double cr = (outerR-innerR)/2.0;

                        this.cn = Error.Circle.N(cr,e);

                        this.cs = (PI_M2 / (double)this.cn);
                        /*
                         * Arc of rotation
                         */
                        this.rn = Error.Circle.N(outerR,e);

                        this.rs = (PI_M2 / (double)this.rn);
                    }
                    else
                        throw new IllegalArgumentException(String.format("Invalid error %g",e));
                }
                else
                    throw new IllegalArgumentException(String.format("Invalid radii, outer (%g) > inner (%g)",outerR,innerR));
            }
            else
                throw new IllegalArgumentException(String.format("Invalid radius %g",outerR));
        }
        else
            throw new IllegalArgumentException(String.format("Invalid radius %g",innerR));
    }
    public Torus(Torus t){
        super(t);
        this.innerRadius = t.innerRadius;
        this.outerRadius = t.outerRadius;
        this.error = t.error;
        this.cn = t.cn;
        this.rn = t.rn;
        this.cs = t.cs;
        this.rs = t.rs;
    }


}
