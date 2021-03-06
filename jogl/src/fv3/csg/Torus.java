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

import fv3.csg.u.Error;
import fv3.csg.u.Face;
import fv3.math.Vector;

/**
 * 
 */
public abstract class Torus
    extends NonConvex
{
    /**
     * 
     */
    public static class XY
        extends Torus
    {
        public XY(double iR, double oR){
            this(iR,oR,Error.Default);
        }
        public XY(double iR, double oR, double e){
            super(N,iR,oR,e);

            Vertex[][] t = XY.Vertex.List(this);

            for (int ru = 0, rv = 1; ru < this.rn; ru++, rv++){

                if (rv == this.rn)
                    rv = 0;

                for (int cu = 0, cv = 1; cu < this.cn; cu++, cv++){

                    if (cv == this.cn)
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
        public XY(XY t){
            super(N,t);
        }


        public static class Vertex
            extends fv3.csg.u.Vertex
        {

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

            protected Vertex(double iR, double oR, 
                             double cos_ra, double sin_ra, 
                             double ca)
            {
                this(iR,oR,cos_ra,sin_ra,ca,Math.cos(ca),Math.sin(ca));
            }
            private Vertex(double iR, double oR, 
                           double cos_ra, double sin_ra, 
                           double ca, 
                           double cos_ca, double sin_ca)
            {
                this(iR,oR,cos_ra,sin_ra,ca,cos_ca,sin_ca,(oR+cos_ca*iR));
            }

            private Vertex(double iR, double oR, 
                           double cos_ra, double sin_ra, 
                           double ca, 
                           double cos_ca, double sin_ca,
                           double oR_P_cosCa_M_iR)
            {
                super((cos_ra*oR_P_cosCa_M_iR),(sin_ra*oR_P_cosCa_M_iR),(sin_ca*iR));
            }
        }
        protected final static String N = "Torus.XY";
    }


    public final double innerRadius, outerRadius, error;

    protected final int cn,rn;

    protected final double cs,rs;


    protected Torus(String n, double innerR, double outerR, double e){
        super(n,0);
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
    protected Torus(String n, Torus t){
        super(n,t);
        this.innerRadius = t.innerRadius;
        this.outerRadius = t.outerRadius;
        this.error = t.error;
        this.cn = t.cn;
        this.rn = t.rn;
        this.cs = t.cs;
        this.rs = t.rs;
    }

}
