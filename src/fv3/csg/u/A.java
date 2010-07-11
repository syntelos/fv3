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
package fv3.csg.u;

import fv3.csg.Solid;

/**
 * CSG algorithm 
 */
public abstract class A 
    extends java.lang.Object
    implements Notation
{

    public final Solid.Construct op;

    public final Solid a, b, r;

    /**
     * Subclass performs CSG operation.  Subsequently requires call to
     * method "destroy".
     */
    public A(Solid.Construct op, Solid a, Solid b){
        super();
        this.op = op;
        this.a = a.push();
        this.b = b.push();
        this.r = new Solid(op,a,b);
    }

    public void destroy(){
        this.a.pop();
        this.b.pop();
    }


    protected static void InitFaces(Solid a, Solid b){

        for (Face af: a){

            af.init();
        }
        for (Face bf: b){

            bf.init();
        }
    }
}
