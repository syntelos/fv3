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
 * @see AH
 */
public abstract class A 
    extends java.lang.Object
    implements Notation
{

    public final Solid.Construct op;

    public final Solid a, b, r;
    /**
     * Intersection sets
     */
    protected final lxl.Set<Face> inA = new lxl.Set<Face>();
    protected final lxl.Set<Face> inB = new lxl.Set<Face>();

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
        {
            Bound bBound = b.getBound();

            if (a.getBound().intersect(bBound)){

                scan:
                for (Face aFace: a){

                    if (aFace.getBound().intersect(bBound)){

                        for (Face bFace: b){

                            if (aFace.getBound().intersect(bFace.getBound())){

                                final int a_A_b = aFace.a.sdistance(bFace);
                                final int a_B_b = aFace.b.sdistance(bFace);
                                final int a_C_b = aFace.c.sdistance(bFace);

                                if (a_A_b != a_B_b ||
                                    a_B_b != a_C_b ||
                                    a_A_b != a_C_b){

                                    final int b_A_a = bFace.a.sdistance(aFace);
                                    final int b_B_a = bFace.b.sdistance(aFace);
                                    final int b_C_a = bFace.c.sdistance(aFace);

                                    if (b_A_a != b_B_a ||
                                        b_B_a != b_C_a ||
                                        b_A_a != b_C_a){
                                        try {
                                            Segment s = new Segment(aFace, bFace,
                                                                    a_A_b, a_B_b, a_C_b,
                                                                    b_A_a, b_B_a, b_C_a);

                                            this.inA.add(aFace);
                                            this.inB.add(bFace);
                                        }
                                        catch (IllegalArgumentException ignore){
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Restore operands to original states
     */
    public void destroy(){
        this.inA.clear();
        this.inB.clear();

        this.a.pop();

        this.b.pop();

        for (Face af: this.a){

            af.init();
        }
        for (Face bf: this.b){

            bf.init();
        }
    }


    protected static void InvertInsideFaces(Solid a){

        for (Face face: a){

            if (face.isInside())

                face.invertNormal();
        }
    }
}
