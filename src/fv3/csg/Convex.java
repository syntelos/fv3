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

import fv3.math.Vector;
import static fv3.math.Vector.Magnitude1.*;
import fv3.math.VertexArray;

/**
 * CSG Convex subclasses are convex solids centered at (0,0,0).  This
 * class provides tools for adding outward facing vertices based on
 * these constraints.
 */
public class Convex
    extends Solid
{

    public Convex(String n, int c){
        super(n,c);
    }
    public Convex(String n, Convex v){
        super(n,v);
    }

}
