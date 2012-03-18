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
package fv3.csg.u;

import fv3.csg.Solid;
import fv3.math.Color;
import fv3.math.VertexArray;
import fv3.model.Enable;
import fv3.model.Material;
import fv3.model.Normal;
import fv3.model.PointSize;
import fv3.model.PolygonMode;
import fv3.model.ShadeModel;

/**
 * Visual segment analysis stepper for debugging.
 */
public class Illustrator
    extends fv3.model.Model
{

    public static class Pair
        extends fv3.model.Object
    {
        public final Face faceA, faceB;

        public final int a_A_b, a_B_b, a_C_b, b_A_a, b_B_a, b_C_a;

        public final boolean testA, testB;

        private VertexArray lines, points;

        private Segment segment;


        public Pair(Face a, Face b){
            this(null,a,b);
        }
        public Pair(Pair previous, Face a, Face b){
            super();
            this.lines = new VertexArray(VertexArray.Type.Lines,12);
            if (null != previous)
                this.points = previous.points;
            else
                this.points = new VertexArray(VertexArray.Type.Points,0);
            this.faceA = a;
            this.faceB = b;
            {
                final float a_a_x = (a.a.x+0.01f);
                final float a_a_y = (a.a.y+0.01f);
                final float a_a_z = (a.a.z+0.01f);
                final float a_b_x = (a.b.x+0.01f);
                final float a_b_y = (a.b.y+0.01f);
                final float a_b_z = (a.b.z+0.01f);
                final float a_c_x = (a.c.x+0.01f);
                final float a_c_y = (a.c.y+0.01f);
                final float a_c_z = (a.c.z+0.01f);

                final float b_a_x = (b.a.x+0.01f);
                final float b_a_y = (b.a.y+0.01f);
                final float b_a_z = (b.a.z+0.01f);
                final float b_b_x = (b.b.x+0.01f);
                final float b_b_y = (b.b.y+0.01f);
                final float b_b_z = (b.b.z+0.01f);
                final float b_c_x = (b.c.x+0.01f);
                final float b_c_y = (b.c.y+0.01f);
                final float b_c_z = (b.c.z+0.01f);

                this.lines.setVertex( 0,a_a_x,a_a_y,a_a_z);
                this.lines.setVertex( 1,a_b_x,a_b_y,a_b_z);
                this.lines.setVertex( 2,a_b_x,a_b_y,a_b_z);
                this.lines.setVertex( 3,a_c_x,a_c_y,a_c_z);
                this.lines.setVertex( 4,a_c_x,a_c_y,a_c_z);
                this.lines.setVertex( 5,a_a_x,a_a_y,a_a_z);

                this.lines.setVertex( 6,b_a_x,b_a_y,b_a_z);
                this.lines.setVertex( 7,b_b_x,b_b_y,b_b_z);
                this.lines.setVertex( 8,b_b_x,b_b_y,b_b_z);
                this.lines.setVertex( 9,b_c_x,b_c_y,b_c_z);
                this.lines.setVertex(10,b_c_x,b_c_y,b_c_z);
                this.lines.setVertex(11,b_a_x,b_a_y,b_a_z);
            }

            this.a_A_b = this.faceA.a.sdistance(this.faceB);
            this.a_B_b = this.faceA.b.sdistance(this.faceB);
            this.a_C_b = this.faceA.c.sdistance(this.faceB);

            this.b_A_a = this.faceB.a.sdistance(this.faceA);
            this.b_B_a = this.faceB.b.sdistance(this.faceA);
            this.b_C_a = this.faceB.c.sdistance(this.faceA);

            this.testA = (a_A_b != a_B_b ||
                          a_B_b != a_C_b ||
                          a_A_b != a_C_b);


            this.testB = (b_A_a != b_B_a ||
                          b_B_a != b_C_a ||
                          b_A_a != b_C_a);

            if (this.testA && this.testB){
                try {
                    this.segment = new Segment(this.faceA, this.faceB,
                                               this.a_A_b, this.a_B_b, this.a_C_b,
                                               this.b_A_a, this.b_B_a, this.b_C_a);
                    Segment.Endpoint ep ;
                    ep = this.segment.endpointA1;
                    if (null != ep)
                        this.points.addVertex(ep.vertex.x,ep.vertex.y,ep.vertex.z);
                    ep = this.segment.endpointA2;
                    if (null != ep)
                        this.points.addVertex(ep.vertex.x,ep.vertex.y,ep.vertex.z);
                    ep = this.segment.endpointB1;
                    if (null != ep)
                        this.points.addVertex(ep.vertex.x,ep.vertex.y,ep.vertex.z);
                    ep = this.segment.endpointB2;
                    if (null != ep)
                        this.points.addVertex(ep.vertex.x,ep.vertex.y,ep.vertex.z);

                }
                catch (IllegalArgumentException ignore){
                }
            }
        }
        public float[] normal(){
            return this.faceA.normal();
        }
    } 




    public final Solid a, b;

    public final boolean valid;

    private final Bound bBound;

    private final int faceCountA, faceCountB, normalIndex, pairIndex;

    private int cca, ccb;

    private Face faceA, faceB;

    private Pair previous, current;

    private boolean needsRedefine, setup;


    public Illustrator(Solid a, Solid b){
        super();
        super.add(new PolygonMode(GL_FRONT_AND_BACK));
        super.add(new PointSize(3.0f));
        this.normalIndex = this.size();
        this.pairIndex = this.normalIndex+1;

        this.a = a.push();
        this.b = b.push();
        this.faceCountA = a.countSolidFaces();
        this.faceCountB = b.countSolidFaces();
        this.bBound = b.getBound();
        this.valid = (a.getBound().intersect(this.bBound));
    }


    public Pair next(){
        for (; this.cca < this.faceCountA; this.cca++){
            this.faceA = this.a.getSolidFace(this.cca);
            if (this.faceA.getBound().intersect(this.bBound)){
                for (; this.ccb < this.faceCountB; this.ccb++){
                    this.faceB = this.b.getSolidFace(this.ccb);

                    if (this.faceA.getBound().intersect(this.faceB.getBound())){

                        this.previous = this.current;

                        this.current = new Pair(this.current,this.faceA, this.faceB);

                        this.ccb += 1;

                        return this.setup(this.current);
                    }
                }
                this.ccb = 0;
            }
        }
        return null;
    }
    public boolean needsRedefine(){
        boolean re = this.needsRedefine;
        this.needsRedefine = false;
        return re;
    }
    private Pair setup(Pair pair){
        this.needsRedefine = true;
        if (setup){
            this.model[this.normalIndex] = new Normal(pair.normal());
            this.model[this.pairIndex] = pair;
        }
        else {
            setup = true;
            this.add(new Normal(pair.normal()));
            this.add(pair);
        }
        return pair;
    }
}
