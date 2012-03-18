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
import fv3.math.Vector;

import javax.media.opengl.GL2;

/**
 * An intersection segment has endpoints in Face A (from CSG Operand
 * A) and Face B (from CSG Operand B).  As the intersection of two
 * faces, the points of intersection are in both faces.  However, the
 * intersection segment can be empty in one face (but not the other),
 * or can lie partially or entirely within the boundary of one or both
 * faces.
 *
 * @see AH
 * @author John Pritchard
 */
public final class Segment
    extends fv3.math.Abstract
    implements Notation,
               java.lang.Comparable<Segment>
{
    /**
     * Multiple intersection path segments at a Face.
     */
    public final static class Path
        extends java.lang.Object
    {
        /**
         * Intersection / Triangulation Path classes for the start and
         * end path endpoints as: "Vertex In Edge", "Vertex To Edge",
         * "Vertex To Vertex", "Edge In Vertex", "Edge To Vertex",
         * "Edge In Edge", and "Edge To Edge".
         */
        public enum Kind {

            VIE, VTE, VTV, EIV, ETV, EIE, ETE;
        }


        public final Segment[] list;

        public final int last;
        /**
         * Consumers depend on start and end being points being in
         * Solid A (or new or cloned).
         */
        public final Segment.Endpoint start, end;

        public final Segment.Path.Kind kind;

        /**
         * This doesn't do all of the "bad case" checking that's
         * done by the intersection phase.
         * 
         * @param f Face from Solid "A" or "B"
         */
        public Path(Face f){
            super();
            /*
             * This depends on the member segments in {@link Face} being
             * in path order.  See Face.memberOf and Segment.compareTo.
             */
            this.list = f.segments();

            this.last = list.length-1;

            Segment.Endpoint start = list[0].endpoint1(f), end = list[last].endpoint2(f);

            this.start = start;
            this.end = end;

            if (start.isVertexOn(f)){
                if (end.isVertexOn(f))
                    this.kind = Kind.VTV;
                else {
                    if (start.isInEdgeOn(f,end))
                        this.kind = Kind.VIE;
                    else
                        this.kind = Kind.VTE;
                }
            }
            else if (end.isVertexOn(f)){
                if (end.isInEdgeOn(f,start))
                    this.kind = Kind.EIV;
                else
                    this.kind = Kind.ETV;
            }
            else {
                if (start.isInEdgeOn(f,end))
                    this.kind = Kind.EIE;
                else
                    this.kind = Kind.ETE;
            }
        }


        public State sclass(Face f, Vertex v){

            return this.list[0].sclass(f,v);
        }
        public Vertex startEndpoint1(Face f){

            return this.list[0].endpoint1(f).vertex;
        }
        public Vertex startEndpoint2(Face f){

            return this.list[0].endpoint2(f).vertex;
        }
        public Vertex termEndpoint1(Face f){

            return this.list[this.last-1].endpoint1(f).vertex;
        }
        public Vertex termEndpoint2(Face f){

            return this.list[this.last-1].endpoint2(f).vertex;
        }
        public Vertex endEndpoint1(Face f){

            return this.list[this.last].endpoint1(f).vertex;
        }
        public Vertex endEndpoint2(Face f){

            return this.list[this.last].endpoint2(f).vertex;
        }
        public Vertex startFaceVertexForEdgeIn1(Face f){

            return this.list[0].faceVertexForEdgeIn1(f);
        }
        public boolean startIsOutboundFromFaceVertexForEdgeIn1(Face f, Vertex v){

            return this.list[0].isOutboundFromFaceVertexForEdgeIn1(f,v);
        }
    }
    /**
     * A segment endpoint in or on a Face from the other CSG Operand.
     * "In" meaning contained by the Face but not on an edge, and "on"
     * meaning on an edge of the Face.
     */
    public final static class Endpoint
        extends Face.Intersection
    {
        /**
         * Segment endpoint description
         */
        public interface Kind {
            /**
             * Identify a Face Vertex from a CSG Operand. A Face from
             * operand A or B, Vertex A or B or C (is on a Face from
             * the other operand).
             */
            public enum Vertex
                implements Endpoint.Kind
            {
                AA, AB, AC, BA, BB, BC;

                public boolean isA(){
                    switch(this){
                    case AA:
                    case AB:
                    case AC:
                        return true;
                    default:
                        return false;
                    }
                }
                public boolean isB(){
                    switch(this){
                    case BA:
                    case BB:
                    case BC:
                        return true;
                    default:
                        return false;
                    }
                }
                public Endpoint.Kind.Edge edgeFor(){
                    switch(this){
                    case AA:
                        return Endpoint.Kind.Edge.AAB;
                    case AB:
                        return Endpoint.Kind.Edge.ABC;
                    case AC:
                        return Endpoint.Kind.Edge.ACA;
                    case BA:
                        return Endpoint.Kind.Edge.BAB;
                    case BB:
                        return Endpoint.Kind.Edge.BBC;
                    case BC:
                        return Endpoint.Kind.Edge.BCA;
                    default:
                        throw new IllegalStateException();
                    }
                }
                public boolean isInEdge(Kind.Edge e){
                    switch (e){
                    case AAB:
                        return (AA == this || AB == this);
                    case ABC:
                        return (AB == this || AC == this);
                    case ACA:
                        return (AA == this || AC == this);
                    case BAB:
                        return (BA == this || BB == this);
                    case BBC:
                        return (BB == this || BC == this);
                    case BCA:
                        return (BA == this || BC == this);
                    default:
                        throw new IllegalStateException();
                    }
                }
                /**
                 * The argument edge is in normal "winding" order from
                 * this vertex.
                 */
                public boolean isOutbound(Kind.Edge e){
                    switch (e){
                    case AAB:
                        return (AA == this);
                    case ABC:
                        return (AB == this);
                    case ACA:
                        return (AC == this);
                    case BAB:
                        return (BA == this);
                    case BBC:
                        return (BB == this);
                    case BCA:
                        return (BC == this);
                    default:
                        throw new IllegalStateException();
                    }
                }
                public final static int IndexOf(Endpoint.Kind.Vertex[] list, Endpoint.Kind.Vertex item){
                    if (null == item || null == list)
                        return -1;
                    else {
                        final int count = list.length;
                        for (int cc = 0; cc < count; cc++){
                            if (item == list[cc])
                                return cc;
                        }
                        return -1;
                    }
                }
                public final static Endpoint.Kind.Vertex[] Add(Endpoint.Kind.Vertex[] list, Endpoint.Kind.Vertex item){
                    if (null == item)
                        return list;
                    else if (null == list)
                        return new Endpoint.Kind.Vertex[]{item};
                    else if (-1 == IndexOf(list,item)){
                        int len = list.length;
                        Endpoint.Kind.Vertex[] copier = new Endpoint.Kind.Vertex[len+1];
                        System.arraycopy(list,0,copier,0,len);
                        copier[len] = item;
                        return copier;
                    }
                    else
                        return list;
                }
            }
            /**
             * Identify Face Edge in CSG Operands.  Identify an edge
             * from a Face in CSG Operand A or B (as intersecting the
             * Face from the other CSG Operand).
             */
            public enum Edge
                implements Endpoint.Kind
            {
                AAB, ABC, ACA, BAB, BBC, BCA;

                public boolean isA(){
                    switch(this){
                    case AAB:
                    case ABC:
                    case ACA:
                        return true;
                    default:
                        return false;
                    }
                }
                public boolean isB(){
                    switch(this){
                    case BAB:
                    case BBC:
                    case BCA:
                        return true;
                    default:
                        return false;
                    }
                }

                public final static int IndexOf(Endpoint.Kind.Edge[] list, Endpoint.Kind.Edge item){
                    if (null == item || null == list)
                        return -1;
                    else {
                        final int count = list.length;
                        for (int cc = 0; cc < count; cc++){
                            if (item == list[cc])
                                return cc;
                        }
                        return -1;
                    }
                }
                public final static Endpoint.Kind.Edge[] Add(Endpoint.Kind.Edge[] list, Endpoint.Kind.Edge item){
                    if (null == item)
                        return list;
                    else if (null == list)
                        return new Endpoint.Kind.Edge[]{item};
                    else if (-1 == IndexOf(list,item)){
                        int len = list.length;
                        Endpoint.Kind.Edge[] copier = new Endpoint.Kind.Edge[len+1];
                        System.arraycopy(list,0,copier,0,len);
                        copier[len] = item;
                        return copier;
                    }
                    else
                        return list;
                }
            }


            public boolean isA();
            public boolean isB();
        }

        /**
         * Kind in terms of CSG Operand order notation.
         */
        public final Kind kind;

        public final Face face2;

        private final Vector normal;

        /**
         * Segment construction vertex case
         * 
         * @exception java.lang.IllegalArgumentException Intersection not found.
         */
        public Endpoint(Kind.Vertex k, Vertex v0, Face f1, Face f2)
            throws java.lang.IllegalArgumentException
        {
            super(f1,v0);

            this.kind = k;
            this.face2 = f2;
            this.normal = this.vertex.getVector().normalize();
        }
        /**
         * Segment construction edge case
         * 
         * @exception java.lang.IllegalArgumentException Intersection not found.
         */
        public Endpoint(Kind.Edge k, Vertex v0, Vertex v1, Face f1, Face f2)
            throws java.lang.IllegalArgumentException
        {
            super(f1,Segment.Intersect(v0,v1,f1));

            this.kind = k;
            this.face2 = f2;
            this.normal = this.vertex.getVector().normalize();
        }


        public boolean isA(){
            return this.kind.isA();
        }
        public boolean isB(){
            return this.kind.isB();
        }
        public boolean sameKind(Endpoint that){
            if (this.kind.isA())
                return that.kind.isA();
            else
                return that.kind.isB();
        }
        public boolean isIn(Face f){
            if (f == this.face)
                return this.in;
            else
                return false;
        }
        public boolean isOn(Face f){
            if (f == this.face)
                return this.on;
            else
                return true;
        }
        public boolean isVertexOn(Face f){
            if (f == this.face){
                if (this.on)
                    return (this.isA || this.isB || this.isC);
                else
                    return false;
            }
            else
                return (this.kind instanceof Endpoint.Kind.Vertex);
        }
        public boolean isEdgeOn(Face f){
            if (f == this.face){
                if (this.on)
                    return (!(this.isA || this.isB || this.isC));
                else
                    return false;
            }
            else
                return (this.kind instanceof Endpoint.Kind.Edge);
        }
        public Endpoint.Kind.Edge edgeFor(Face f){
            if (f == this.face){
                if (this.on){
                    if (this.onAB){
                        if (this.kind.isA())
                            return Endpoint.Kind.Edge.BAB;
                        else
                            return Endpoint.Kind.Edge.AAB;
                    }
                    else if (this.onBC){
                        if (this.kind.isA())
                            return Endpoint.Kind.Edge.BBC;
                        else
                            return Endpoint.Kind.Edge.ABC;
                    }
                    else {
                        if (this.kind.isA())
                            return Endpoint.Kind.Edge.BCA;
                        else
                            return Endpoint.Kind.Edge.ACA;
                    }
                }
                else
                    throw new IllegalStateException();
            }
            else if (this.kind instanceof Endpoint.Kind.Edge)
                return (Endpoint.Kind.Edge)this.kind;
            else
                return ((Endpoint.Kind.Vertex)this.kind).edgeFor();
        }
        /**
         * @return This endpoint shares an edge in 'f' with 'that'
         * endpoint.
         */
        public boolean isInEdgeOn(Face f, Endpoint that){

            if (f == this.face)
                return (this.edgeFor(f) == that.edgeFor(f));

            else if (this.kind instanceof Endpoint.Kind.Vertex)

                return ((Endpoint.Kind.Vertex)this.kind).isInEdge((Endpoint.Kind.Edge)that.kind);
            else 
                return (this.kind == that.kind);
        }
        /**
         * @return Vertex to edge is in winding order
         */
        public boolean isOutbound(Endpoint that){

            return ((Endpoint.Kind.Vertex)this.kind).isOutbound((Endpoint.Kind.Edge)that.kind);
        }
        public Vector getNormal(){
            return this.normal.clone();
        }


        public final static int IndexOf(Endpoint[] list, Endpoint item){
            if (null == item || null == list)
                return -1;
            else {
                final int count = list.length;
                for (int cc = 0; cc < count; cc++){
                    if (list[cc].equals(item))
                        return cc;
                }
                return -1;
            }
        }
        public final static Endpoint[] Add(Endpoint[] list, Endpoint item){
            if (null == item)
                return list;
            else if (null == list)
                return new Endpoint[]{item};
            else {
                int len = list.length;
                Endpoint[] copier = new Endpoint[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = item;
                return copier;
            }
        }
    }
    /**
     * Segment classification by available endpoints from CSG
     * Operands.
     */
    public enum Kind {

        AA,AAB,AB,AABB,ABB,BB;

        /**
         * @return Triangulation segment case for 'thisF'.
         */
        public final static Kind For(Segment.Endpoint a1, Segment.Endpoint a2,
                                     Segment.Endpoint b1, Segment.Endpoint b2)
        {
            if (null != a1){
                if (null != a2){
                    if (null != b1){
                        if (null != b2)
                            return AABB;
                        else
                            return AAB;
                    }
                    else
                        return AA;
                }
                else if (null != b1){

                    if (null != b2)
                        return ABB;
                    else
                        return AB;
                }
                else
                    throw new IllegalArgumentException();
            }
            else if (null != b1){

                if (null != b2)
                    return BB;
                else
                    throw new IllegalArgumentException();
            }
            else
                throw new IllegalArgumentException();
        }
    }

    public final Face a, b;

    /**
     * An endpoint may be null in the case of multiple
     * intersection segments in a face.
     */
    public final Endpoint endpointA1, endpointA2, endpointB1, endpointB2;
    /**
     * Signed distances from face and vertex to face.  For
     * example, "Face A Vertex A to FaceB" is "a_A_b".  Signed distance of +1 is "Outside" 
     */
    public final int a_A_b, a_B_b, a_C_b;
    public final int b_A_a, b_B_a, b_C_a;

    public final Triangulate.Kind triKindA, triKindB;

    public final Segment.Kind kind;
    /**
     * Path sort vector
     */
    private final Vector normal;


    Segment(Face a, Face b, 
            int a_A_b, int a_B_b, int a_C_b,
            int b_A_a, int b_B_a, int b_C_a)
        {
            super();

            Endpoint.Kind.Vertex[] endpointsV = null;
            Endpoint.Kind.Edge[] endpointsE = null;
            /*
             */
            if (0 == a_A_b)
                endpointsV = Endpoint.Kind.Vertex.Add(endpointsV,Endpoint.Kind.Vertex.AA);
            else {

                if (0 != a_B_b && a_A_b != a_B_b){

                    endpointsE = Endpoint.Kind.Edge.Add(endpointsE,Endpoint.Kind.Edge.AAB);
                }

                if (0 != a_C_b && a_A_b != a_C_b){

                    endpointsE = Endpoint.Kind.Edge.Add(endpointsE,Endpoint.Kind.Edge.ACA);
                }
            }

            if (0 == a_B_b){

                endpointsV = Endpoint.Kind.Vertex.Add(endpointsV,Endpoint.Kind.Vertex.AB);
            }
            else {

                if (0 != a_C_b && a_B_b != a_C_b){

                    endpointsE = Endpoint.Kind.Edge.Add(endpointsE,Endpoint.Kind.Edge.ABC);
                }
            }

            if (0 == a_C_b){

                endpointsV = Endpoint.Kind.Vertex.Add(endpointsV,Endpoint.Kind.Vertex.AC);
            }
            /*
             */
            if (0 == b_A_a){

                endpointsV = Endpoint.Kind.Vertex.Add(endpointsV,Endpoint.Kind.Vertex.BA);
            }
            else {

                if (0 != b_B_a && b_A_a != b_B_a){

                    endpointsE = Endpoint.Kind.Edge.Add(endpointsE,Endpoint.Kind.Edge.BAB);
                }

                if (0 != b_C_a && b_A_a != b_C_a){

                    endpointsE = Endpoint.Kind.Edge.Add(endpointsE,Endpoint.Kind.Edge.BCA);
                }
            }

            if (0 == b_B_a){

                endpointsV = Endpoint.Kind.Vertex.Add(endpointsV,Endpoint.Kind.Vertex.BB);
            }
            else {

                if (0 != b_C_a && b_B_a != b_C_a){

                    endpointsE = Endpoint.Kind.Edge.Add(endpointsE,Endpoint.Kind.Edge.BBC);
                }
            }

            if (0 == b_C_a){

                endpointsV = Endpoint.Kind.Vertex.Add(endpointsV,Endpoint.Kind.Vertex.BC);
            }


            /*
             * Determine endpoints "A{1,2}" and "B{1,2}"
             */

            Endpoint endpointA1 = null, endpointA2 = null,
                endpointB1 = null, endpointB2 = null;

            if (null != endpointsV){
                /*
                 * Scan vertex cases
                 */
                for (Endpoint.Kind.Vertex endpoint : endpointsV){

                    switch (endpoint){
                    case AA:
                        try {
                            if (null == endpointA1)
                                endpointA1 = new Endpoint(endpoint,a.a,b,a);
                            else 
                                endpointA2 = new Endpoint(endpoint,a.a,b,a);

                            //System.err.println("+ "+endpoint+" "+b.getName());
                        }
                        catch (IllegalArgumentException drop){
                            //System.err.println("! "+endpoint+" "+b.getName());
                        }
                        break;
                    case AB:
                        try {
                            if (null == endpointA1)
                                endpointA1 = new Endpoint(endpoint,a.b,b,a);
                            else 
                                endpointA2 = new Endpoint(endpoint,a.b,b,a);

                            //System.err.println("+ "+endpoint+" "+b.getName());
                        }
                        catch (IllegalArgumentException drop){
                            //System.err.println("! "+endpoint+" "+b.getName());
                        }
                        break;
                    case AC:
                        try {
                            if (null == endpointA1)
                                endpointA1 = new Endpoint(endpoint,a.c,b,a);
                            else 
                                endpointA2 = new Endpoint(endpoint,a.c,b,a);

                            //System.err.println("+ "+endpoint+" "+b.getName());
                        }
                        catch (IllegalArgumentException drop){
                            //System.err.println("! "+endpoint+" "+b.getName());
                        }
                        break;
                    case BA:
                        try {
                            if (null == endpointB1)
                                endpointB1 = new Endpoint(endpoint,b.a,a,b);
                            else 
                                endpointB2 = new Endpoint(endpoint,b.a,a,b);

                            //System.err.println("+ "+endpoint+" "+a.getName());
                        }
                        catch (IllegalArgumentException drop){
                            //System.err.println("! "+endpoint+" "+a.getName());
                        }
                        break;
                    case BB:
                        try {
                            if (null == endpointB1)
                                endpointB1 = new Endpoint(endpoint,b.b,a,b);
                            else 
                                endpointB2 = new Endpoint(endpoint,b.b,a,b);

                            //System.err.println("+ "+endpoint+" "+a.getName());
                        }
                        catch (IllegalArgumentException drop){
                            //System.err.println("! "+endpoint+" "+a.getName());
                        }
                        break;
                    case BC:
                        try {
                            if (null == endpointB1)
                                endpointB1 = new Endpoint(endpoint,b.c,a,b);
                            else 
                                endpointB2 = new Endpoint(endpoint,b.c,a,b);

                            //System.err.println("+ "+endpoint+" "+a.getName());
                        }
                        catch (IllegalArgumentException drop){
                            //System.err.println("! "+endpoint+" "+a.getName());
                        }
                        break;
                    default:
                        throw new IllegalStateException();
                    }
                }
            }
            /*
             * Scan for edges at the exhaustion vertex cases.
             * 
             * Case: avoid creating a floating vertex that
             * is identical to a face vertex.
             */
            if (null != endpointsE){
                /*
                 */
                for (Endpoint.Kind.Edge endpoint : endpointsE){

                    switch (endpoint){
                    case AAB:
                        if (null == endpointA1 || null == endpointA2){
                            try {
                                if (null == endpointA1)
                                    endpointA1 = new Endpoint(endpoint,a.a,a.b,b,a);
                                else 
                                    endpointA2 = new Endpoint(endpoint,a.a,a.b,b,a);

                                //System.err.println("+ "+endpoint+" "+b.getName());
                            }
                            catch (IllegalArgumentException drop){
                                //System.err.println("! "+endpoint+" "+b.getName());
                            }
                        }
                        break;
                    case ABC:
                        if (null == endpointA1 || null == endpointA2){
                            try {
                                if (null == endpointA1)
                                    endpointA1 = new Endpoint(endpoint,a.b,a.c,b,a);
                                else
                                    endpointA2 = new Endpoint(endpoint,a.b,a.c,b,a);

                                //System.err.println("+ "+endpoint+" "+b.getName());
                            }
                            catch (IllegalArgumentException drop){
                                //System.err.println("! "+endpoint+" "+b.getName());
                            }
                        }
                        break;
                    case ACA:
                        if (null == endpointA1 || null == endpointA2){
                            try {
                                if (null == endpointA1)
                                    endpointA1 = new Endpoint(endpoint,a.a,a.c,b,a);
                                else
                                    endpointA2 = new Endpoint(endpoint,a.a,a.c,b,a);

                                //System.err.println("+ "+endpoint+" "+b.getName());
                            }
                            catch (IllegalArgumentException drop){
                                //System.err.println("! "+endpoint+" "+b.getName());
                            }
                        }
                        break;
                    case BAB:
                        if (null == endpointB1 || null == endpointB2){
                            try {
                                if (null == endpointB1)
                                    endpointB1 = new Endpoint(endpoint,b.a,b.b,a,b);
                                else 
                                    endpointB2 = new Endpoint(endpoint,b.a,b.b,a,b);

                                //System.err.println("+ "+endpoint+" "+a.getName());
                            }
                            catch (IllegalArgumentException drop){
                                //System.err.println("! "+endpoint+" "+a.getName());
                            }
                        }
                        break;
                    case BBC:
                        if (null == endpointB1 || null == endpointB2){
                            try {
                                if (null == endpointB1)
                                    endpointB1 = new Endpoint(endpoint,b.b,b.c,a,b);
                                else 
                                    endpointB2 = new Endpoint(endpoint,b.b,b.c,a,b);

                                //System.err.println("+ "+endpoint+" "+a.getName());
                            }
                            catch (IllegalArgumentException drop){
                                //System.err.println("! "+endpoint+" "+a.getName());
                            }
                        }
                        break;
                    case BCA:
                        if (null == endpointB1 || null == endpointB2){
                            try {
                                if (null == endpointB1)
                                    endpointB1 = new Endpoint(endpoint,b.a,b.c,a,b);
                                else 
                                    endpointB2 = new Endpoint(endpoint,b.a,b.c,a,b);

                                //System.err.println("+ "+endpoint+" "+a.getName());
                            }
                            catch (IllegalArgumentException drop){
                                //System.err.println("! "+endpoint+" "+a.getName());
                            }
                        }
                        break;
                    default:
                        throw new IllegalStateException();
                    }
                }
            }

            /*
             * Complete with endpoints in A and B
             */
            if (null != endpointA1 || null != endpointB1){

                this.a = a;
                this.b = b;
                this.a_A_b = a_A_b;
                this.a_B_b = a_B_b;
                this.a_C_b = a_C_b;
                this.b_A_a = b_A_a;
                this.b_B_a = b_B_a; 
                this.b_C_a = b_C_a;

                /*
                 * Path ordering of endpoints for path ordering of segments
                 */
                if (null != endpointA1 && null != endpointA2){
                    switch (endpointA1.compareTo(endpointA2)){
                    case -1:
                        break;
                    case 0:
                        endpointA2 = null;
                        break;
                    case 1:
                        Endpoint tmp = endpointA1;
                        endpointA1 = endpointA2;
                        endpointA2 = tmp;
                        break;
                    default:
                        throw new IllegalStateException();
                    }
                }
                /*
                 */
                if (null != endpointB1 && null != endpointB2){
                    switch (endpointB1.compareTo(endpointB2)){
                    case -1:
                        break;
                    case 0:
                        endpointB2 = null;
                        break;
                    case 1:
                        Endpoint tmp = endpointB1;
                        endpointB1 = endpointB2;
                        endpointB2 = tmp;
                        break;
                    default:
                        throw new IllegalStateException();
                    }
                }

                this.endpointA1 = endpointA1;
                this.endpointA2 = endpointA2;
                this.endpointB1 = endpointB1;
                this.endpointB2 = endpointB2;

                this.kind = Segment.Kind.For(endpointA1,endpointA2,endpointB1,endpointB2);

                this.triKindA = Triangulate.Kind.For(a,this.endpoint1(a),this.endpoint2(a));
                this.triKindB = Triangulate.Kind.For(a,this.endpoint1(b),this.endpoint2(b));

                /*
                 * A-B symmetric path order vector for
                 * Segment.compareTo
                 */
                if (null != endpointA1){

                    if (null != endpointA2){

                        this.normal = endpointA1.getNormal().mid(endpointA2.getNormal());
                    }
                    else if (null != endpointB1){

                        this.normal = endpointA1.getNormal().mid(endpointB1.getNormal());
                    }
                    else
                        throw new IllegalArgumentException();
                }
                else if (null != endpointB2){

                    this.normal = endpointB1.getNormal().mid(endpointB2.getNormal());
                }
                else
                    throw new IllegalArgumentException();
                /*
                 */
                a.memberOf(this);
                b.memberOf(this);
            }
            else
                throw new IllegalArgumentException();
        }


    /**
     * Classification must be performed after intersection so that the
     * {@link Face} based propagation limits are effective.
     */
    public void classify(Face f){
        /*
         * Initial vertex classification
         */
        if (f == this.a){

            this.a.a.status = State.Classify(this.a_A_b);
            this.a.b.status = State.Classify(this.a_B_b);
            this.a.c.status = State.Classify(this.a_C_b);
        }
        else if (f == this.b){

            this.b.a.status = State.Classify(this.b_A_a);
            this.b.b.status = State.Classify(this.b_B_a);
            this.b.c.status = State.Classify(this.b_C_a);
        }
        else
            throw new IllegalArgumentException();
    }
    public State sclass(Face f, Vertex v){

        if (f == this.a){

            return this.b.sclass(v);
        }
        else if (f == this.b){

            return this.a.sclass(v);
        }
        else
            throw new IllegalArgumentException();
    }
    public Triangulate.Kind triangulateKind(Face f){
        if (f == this.a)
            return this.triKindA;
        else if (f == this.b)
            return this.triKindB;
        else
            throw new IllegalArgumentException();
    }
    public Endpoint endpoint1(Face f){

        if (f == this.a){
            switch (this.kind){
            case AA:
            case AAB:
            case AB:
            case AABB:
            case ABB:
                return this.endpointA1;
            case BB:
                return this.endpointB1;
            default:
                throw new IllegalStateException();
            }
        }
        else {
            switch (this.kind){
            case AA:
                return this.endpointA1;
            case AAB:
            case AB:
            case AABB:
            case ABB:
            case BB:
                return this.endpointB1;
            default:
                throw new IllegalStateException();
            }
        }
    }
    public Endpoint endpoint2(Face f){

        if (f == this.a){
            switch (this.kind){
            case AA:
            case AAB:
                return this.endpointA2;
            case AB:
                return this.endpointB1;
            case AABB:
                return this.endpointA2;
            case ABB:
                return this.endpointB1;
            case BB:
                return this.endpointB2;
            default:
                throw new IllegalStateException();
            }
        }
        else {
            switch (this.kind){
            case AA:
            case AAB:
                return this.endpointA2;
            case AB:
            case AABB:
            case ABB:
            case BB:
                return this.endpointB2;
            default:
                throw new IllegalStateException();
            }
        }
    }
    /**
     * @param f Face in this segment having first endpoint (1) in
     * edge
     * 
     * @return First path order face vertex in edge endpoint for
     * the argument face
     */
    public Vertex faceVertexForEdgeIn1(Face f){
        if (f == this.a){

            Endpoint edgeIn = this.endpointA1;
            switch ((Endpoint.Kind.Edge)edgeIn.kind){
            case AAB:
                if (-1 == f.a.compareTo(f.b))
                    return f.a;
                else
                    return f.b;
            case ABC:
                if (-1 == f.b.compareTo(f.c))
                    return f.b;
                else
                    return f.c;
            case ACA:
                if (-1 == f.c.compareTo(f.a))
                    return f.c;
                else
                    return f.a;
            default:
                throw new IllegalStateException();
            }
        }
        else {

            Endpoint edgeIn = this.endpointB1;
            switch ((Endpoint.Kind.Edge)edgeIn.kind){
            case BAB:
                if (-1 == f.a.compareTo(f.b))
                    return f.a;
                else
                    return f.b;
            case BBC:
                if (-1 == f.b.compareTo(f.c))
                    return f.b;
                else
                    return f.c;
            case BCA:
                if (-1 == f.c.compareTo(f.a))
                    return f.c;
                else
                    return f.a;
            default:
                throw new IllegalStateException();
            }
        }
    }
    /**
     * @param f Face in this segment having second endpoint (2) in
     * edge
     * 
     * @return Second path order face vertex in edge endpoint for
     * the argument face
     */
    public Vertex faceVertexForEdgeIn2(Face f){
        if (f == this.a){

            Endpoint edgeIn = this.endpointA2;
            switch ((Endpoint.Kind.Edge)edgeIn.kind){
            case AAB:
                if (1 == f.a.compareTo(f.b))
                    return f.a;
                else
                    return f.b;
            case ABC:
                if (1 == f.b.compareTo(f.c))
                    return f.b;
                else
                    return f.c;
            case ACA:
                if (1 == f.c.compareTo(f.a))
                    return f.c;
                else
                    return f.a;
            default:
                throw new IllegalStateException();
            }
        }
        else {

            Endpoint edgeIn = this.endpointB2;
            switch ((Endpoint.Kind.Edge)edgeIn.kind){
            case BAB:
                if (1 == f.a.compareTo(f.b))
                    return f.a;
                else
                    return f.b;
            case BBC:
                if (1 == f.b.compareTo(f.c))
                    return f.b;
                else
                    return f.c;
            case BCA:
                if (1 == f.c.compareTo(f.a))
                    return f.c;
                else
                    return f.a;
            default:
                throw new IllegalStateException();
            }
        }
    }
    public boolean isOutboundFromFaceVertexForEdgeIn1(Face f, Vertex v){

        if (f == this.a){

            Endpoint edgeIn = this.endpointA1;
            switch ((Endpoint.Kind.Edge)edgeIn.kind){
            case AAB:
                return (v == f.a);

            case ABC:
                return (v == f.b);

            case ACA:
                return (v == f.c);

            default:
                throw new IllegalStateException();
            }
        }
        else {

            Endpoint edgeIn = this.endpointB1;
            switch ((Endpoint.Kind.Edge)edgeIn.kind){
            case BAB:
                return (v == f.a);

            case BBC:
                return (v == f.b);

            case BCA:
                return (v == f.c);

            default:
                throw new IllegalStateException();
            }
        }
    }
    /**
     * Sort to path order
     */
    public int compareTo(Segment that){

        Vector a = this.normal;
        Vector b = that.normal;
        if (a.equals(b))
            return 0;
        else {
            double angle = a.angle(b);
            if (0.0 > angle)
                return -1;
            else
                return 1;
        }
    }
    public fv3.Model.Element[] debugger(double rX, double rY, double rZ){


        fv3.Model.Element[] model = new fv3.Model.Element[]{
            new fv3.model.PointSize(3.0),
            new fv3.model.Begin(GL2.GL_POINTS)
        };

        Vertex v;

        if (null != this.endpointA1){
                
            v = this.endpointA1.vertex;

            model = fv3.model.Object.Add(model,new fv3.model.Vertex(v.x,v.y,v.z));

            if (null != this.endpointA2){

                v = this.endpointA2.vertex;

                model = fv3.model.Object.Add(model,new fv3.model.Vertex(v.x,v.y,v.z));
            }
        }

        if (null != this.endpointB1){

            v = this.endpointB1.vertex;

            model = fv3.model.Object.Add(model,new fv3.model.Vertex(v.x,v.y,v.z));

            if (null != this.endpointB2){

                v = this.endpointB2.vertex;

                model = fv3.model.Object.Add(model,new fv3.model.Vertex(v.x,v.y,v.z));
            }
        }
        model = fv3.model.Object.Add(model,new fv3.model.End());

        return model;
    }

    /*
     */

    public final static int IndexOf(Segment[] list, Segment item){
        if (null == item || null == list)
            return -1;
        else {
            final int count = list.length;
            for (int cc = 0; cc < count; cc++){
                if (list[cc].equals(item))
                    return cc;
            }
            return -1;
        }
    }
    public final static Segment[] Add(Segment[] list, Segment item){
        if (null == item)
            return list;
        else if (null == list)
            return new Segment[]{item};
        else if (-1 == IndexOf(list,item)){
            int len = list.length;
            Segment[] copier = new Segment[len+1];
            System.arraycopy(list,0,copier,0,len);
            copier[len] = item;
            return copier;
        }
        else
            return list;
    }
    public final static Segment[] Remove(Segment[] list, int index){
        if (0 > index)
            throw new java.util.NoSuchElementException();
        else {
            int len = list.length;
            int term = (len-1);
            Segment[] copy = new Segment[term];

            if (0 == index){
                System.arraycopy(list,1,copy,0,term);
            }
            else if (term == index){
                System.arraycopy(list,0,copy,0,term);
            }
            else {
                System.arraycopy(list,0,copy,0,index);
                System.arraycopy(list,(index+1),copy,index,(term-index));
            }
            return copy;
        }
    }
    /**
     * @param lineA Point A in line of intersection
     * @param lineB Point B in line of intersection
     * @param fp Face plane: a face describing the plane of intersection
     * 
     * @return Point of intersection between a line and a plane.
     * Return null for no point of intersection, including the case of
     * the line on the plane.
     */
    public final static Vertex Intersect(Vertex lineA, Vertex lineB, Face fp){

        final Vector u = lineB.getVector().sub(lineA.getVector());
        final Vector w = lineA.getVector().sub(fp.a.getVector());

        final Vector fpn = fp.getNormal();

        double d = fpn.dot(u);
        double n = -(fpn.dot(w));

        if (EPS > Math.abs(d))
            return null;
        else {
            double s = (n/d);
            if (0.0 > s || 1.0 < s)
                return null;
            else 
                return new Vertex(lineA.getVector().add(u.mul(s))).boundary();
        }
    }

    public static class Iterator
        extends java.lang.Object
        implements java.util.Iterator<Segment>,
                   java.lang.Iterable<Segment>
    {

        public final int length;

        private final Segment[] list;

        private int index;

        public Iterator(Segment[] list){
            super();
            if (null == list){
                this.list = null;
                this.length = 0;
            }
            else {
                this.list = list.clone();
                this.length = this.list.length;
            }
        }
        public Iterator(Segment[] list, Segment exclude){
            super();
            if (null == list){
                this.list = null;
                this.length = 0;
            }
            else {
                int index = Segment.IndexOf(list,exclude);
                if (-1 == index){
                    this.list = list.clone();
                    this.length = this.list.length;
                }
                else {
                    this.list = Remove(list,index);
                    this.length = this.list.length;
                }
            }
        }
        public Iterator(Segment[] list, Segment[] exclude){
            super();
            if (null == list){
                this.list = null;
                this.length = 0;
            }
            else {
                for (Segment ex: exclude){
                    int index = Segment.IndexOf(list,ex);
                    if (-1 != index)
                        list = Remove(list,index);
                }
                this.list = list;
                this.length = (null == list)?(0):(list.length);
            }
        }

        public boolean hasNext(){
            return (this.index < this.length);
        }
        public Segment next(){
            return this.list[this.index++];
        }
        public void remove(){
            throw new UnsupportedOperationException();
        }
        public java.util.Iterator<Segment> iterator(){
            return this;
        }
    }
}
