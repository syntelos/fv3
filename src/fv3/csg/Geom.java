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
}
