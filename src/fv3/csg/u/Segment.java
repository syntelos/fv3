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

/**
 * An intersection segment has endpoints in Face A (from Solid A) and
 * Face B (from Solid B), and is shared by both.  However, the
 * intersection segment can be empty in one face (but not the other),
 * or can lie partially or entirely within the edge boundary of one or
 * both faces.
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
         * Intersection path classifications for "Vertex In Edge",
         * "Vertex To Edge", "Vertex To Vertex", "Edge In Edge", "Edge
         * To Edge" and their directional (face orientation) inverses.
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

            Segment.Endpoint start = list[0].endpointOn1(f), end = list[last].endpointOn1(f);

            this.start = start;
            this.end = end;

            if (start.isVertex()){
                if (end.isVertex())
                    this.kind = Kind.VTV;
                else {
                    if (start.isInEdge(end))
                        this.kind = Kind.VIE;
                    else
                        this.kind = Kind.VTE;
                }
            }
            else if (end.isVertex()){
                if (end.isInEdge(start))
                    this.kind = Kind.EIV;
                else
                    this.kind = Kind.ETV;
            }
            else {
                if (start.isInEdge(end))
                    this.kind = Kind.EIE;
                else
                    this.kind = Kind.ETE;
            }
        }


        public State sclass(Face f, Vertex v){

            return this.list[0].sclass(f,v);
        }
        public Vertex startEndpointIn1(Face f){

            return this.list[0].endpointIn1(f).vertex;
        }
        public Vertex startEndpointOn1(Face f){

            return this.list[0].endpointOn1(f).vertex;
        }
        public Vertex startEndpointIn2(Face f){

            return this.list[0].endpointIn2(f).vertex;
        }
        public Vertex startEndpointOn2(Face f){

            return this.list[0].endpointOn2(f).vertex;
        }
        public Vertex termEndpointIn2(Face f){

            return this.list[this.last-1].endpointIn2(f).vertex;
        }
        public Vertex termEndpointOn2(Face f){

            return this.list[this.last-1].endpointOn2(f).vertex;
        }
        public Vertex endEndpointIn1(Face f){

            return this.list[this.last].endpointIn1(f).vertex;
        }
        public Vertex endEndpointOn1(Face f){

            return this.list[this.last].endpointOn1(f).vertex;
        }
        public Vertex endEndpointIn2(Face f){

            return this.list[this.last].endpointIn2(f).vertex;
        }
        public Vertex endEndpointOn2(Face f){

            return this.list[this.last].endpointOn2(f).vertex;
        }
        public Vertex startFaceVertexForEdgeIn1(Face f){

            return this.list[0].faceVertexForEdgeIn1(f);
        }
        public boolean startIsOutboundFromFaceVertexForEdgeIn1(Face f, Vertex v){

            return this.list[0].isOutboundFromFaceVertexForEdgeIn1(f,v);
        }
    }
    /**
     * 
     */
    public final static class Endpoint
        extends java.lang.Object
        implements java.lang.Comparable<Endpoint>
    {
        /**
         * Segment endpoint source description: Face and Vertex.
         */
        public interface Kind {
            /**
             * Identify Face and Vertex in CSG Operand order
             */
            public enum Vertex
                implements Endpoint.Kind
            {
                AA, AB, AC, BA, BB, BC;

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
             * Identify Face and Edge in CSG Operand order
             */
            public enum Edge
                implements Endpoint.Kind
            {
                AAB, ABC, ACA, BAB, BBC, BCA;


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
        }

        public final Kind kind;
        public final Vertex vertex;
        private final Vector normal;


        public Endpoint(Kind k, Vertex v){
            super();
            if (null != k && null != v){
                this.kind = k;
                this.vertex = v;
                this.normal = v.getVector().normalize();
            }
            else
                throw new IllegalStateException();//(error in Segment ctor)
        }


        public boolean isEdge(){

            return (this.kind instanceof Endpoint.Kind.Edge);
        }
        public boolean isVertex(){

            return (this.kind instanceof Endpoint.Kind.Vertex);
        }
        public boolean isInEdge(Endpoint that){

            if (this.kind instanceof Endpoint.Kind.Vertex)

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
        public int hashCode(){
            return this.vertex.hashCode();
        }
        public boolean equals(Object that){
            if (that instanceof Endpoint)
                return this.equals((Endpoint)that);
            else
                return false;
        }
        public boolean equals(Endpoint that){
            if (null == that)
                return false;
            else
                return this.vertex.equals(that.vertex);
        }
        /**
         * Path order comparison
         */
        public int compareTo(Endpoint that){
            if (null == that)
                return 1;
            else
                return this.vertex.compareTo(that.vertex);
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
     * Segment kind: member of a multiple segment path,
     * vertex-edge, edge-vertex, or edge-edge.  These are
     * semantically equivalent to "vertex to edge" or "edge to
     * edge" (as exist for a non empty, one segment triangle
     * bisection), but symbolically differentiated for the
     * compiler.
     */
    public enum Kind {
        M, VE, EV, EE;

        public final static Kind For(Segment.Endpoint e1, Segment.Endpoint e2){

            if (null == e2)

                return Kind.M;

            else if (e1.isEdge()){

                if (e2.isEdge())
                    return Kind.EE;
                else
                    return Kind.EV;
            }
            else if (e2.isEdge())
                return Kind.VE;
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

    public final Kind kindA, kindB;
    /**
     * Path sort vector
     */
    private final Vector vector;


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
                        if (null == endpointA1)
                            endpointA1 = new Endpoint(endpoint,a.a);
                        else 
                            endpointA2 = new Endpoint(endpoint,a.a);
                        break;
                    case AB:
                        if (null == endpointA1)
                            endpointA1 = new Endpoint(endpoint,a.b);
                        else 
                            endpointA2 = new Endpoint(endpoint,a.b);
                        break;
                    case AC:
                        if (null == endpointA1)
                            endpointA1 = new Endpoint(endpoint,a.c);
                        else 
                            endpointA2 = new Endpoint(endpoint,a.c);
                        break;
                    case BA:
                        if (null == endpointB1)
                            endpointB1 = new Endpoint(endpoint,b.a);
                        else 
                            endpointB2 = new Endpoint(endpoint,b.a);
                        break;
                    case BB:
                        if (null == endpointB1)
                            endpointB1 = new Endpoint(endpoint,b.b);
                        else 
                            endpointB2 = new Endpoint(endpoint,b.b);
                        break;
                    case BC:
                        if (null == endpointB1)
                            endpointB1 = new Endpoint(endpoint,b.c);
                        else 
                            endpointB2 = new Endpoint(endpoint,b.c);
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
                            Vertex p = Intersect(a.a,a.b,b);
                            if (null != p){

                                if (null == endpointA1)
                                    endpointA1 = new Endpoint(endpoint,p);
                                else 
                                    endpointA2 = new Endpoint(endpoint,p);
                            }
                        }
                        break;
                    case ABC:
                        if (null == endpointA1 || null == endpointA2){
                            Vertex p = Intersect(a.b,a.c,b);
                            if (null != p){

                                if (null == endpointA1)
                                    endpointA1 = new Endpoint(endpoint,p);
                                else
                                    endpointA2 = new Endpoint(endpoint,p);
                            }
                        }
                        break;
                    case ACA:
                        if (null == endpointA1 || null == endpointA2){
                            Vertex p = Intersect(a.a,a.c,b);
                            if (null != p){

                                if (null == endpointA1)
                                    endpointA1 = new Endpoint(endpoint,p);
                                else
                                    endpointA2 = new Endpoint(endpoint,p);
                            }
                        }
                        break;
                    case BAB:
                        if (null == endpointB1 || null == endpointB2){
                            Vertex p = Intersect(b.a,b.b,a);
                            if (null != p){

                                if (null == endpointB1)
                                    endpointB1 = new Endpoint(endpoint,p);
                                else 
                                    endpointB2 = new Endpoint(endpoint,p);
                            }
                        }
                        break;
                    case BBC:
                        if (null == endpointB1 || null == endpointB2){
                            Vertex p = Intersect(b.b,b.c,a);
                            if (null != p){

                                if (null == endpointB1)
                                    endpointB1 = new Endpoint(endpoint,p);
                                else 
                                    endpointB2 = new Endpoint(endpoint,p);
                            }
                        }
                        break;
                    case BCA:
                        if (null == endpointB1 || null == endpointB2){
                            Vertex p = Intersect(b.a,b.c,a);
                            if (null != p){

                                if (null == endpointB1)
                                    endpointB1 = new Endpoint(endpoint,p);
                                else 
                                    endpointB2 = new Endpoint(endpoint,p);
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
                    case 1:
                        Endpoint tmp = endpointB1;
                        endpointB1 = endpointB2;
                        endpointB2 = tmp;
                        break;
                    default:
                        throw new IllegalStateException();
                    }
                }


                this.kindA = Segment.Kind.For(endpointA1,endpointA2);
                this.kindB = Segment.Kind.For(endpointB1,endpointB2);
                this.endpointA1 = endpointA1;
                this.endpointA2 = endpointA2;
                this.endpointB1 = endpointB1;
                this.endpointB2 = endpointB2;

                /*
                 * A-B symmetric path order vector for
                 * Segment.compareTo
                 */
                if (null != endpointA1){

                    if (null != endpointA2){

                        this.vector = endpointA1.getNormal().mid(endpointA2.getNormal());
                    }
                    else if (null != endpointB1){

                        this.vector = endpointA1.getNormal().mid(endpointB1.getNormal());
                    }
                    else
                        throw new IllegalArgumentException();
                }
                else if (null != endpointB2){

                    this.vector = endpointB1.getNormal().mid(endpointB2.getNormal());
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
    public Segment.Kind kind(Face f){
        if (f == this.a)
            return this.kindA;
        else if (f == this.b)
            return this.kindB;
        else
            throw new IllegalArgumentException();
    }
    /**
     * On the boundary defined by the vertices and edges of the
     * argument face.
     * @see Segment$Kind#M
     * @see Segment$Path
     */
    public Endpoint endpointOn1(Face f){

        if (f == this.a)

            return this.endpointA1;
        else 
            return this.endpointB1;
    }
    public Endpoint endpointOn2(Face f){

        if (f == this.a)

            return this.endpointA2;
        else 
            return this.endpointB2;
    }
    /**
     * In the surface defined by the vertices and edges of the
     * argument face.
     * @see Segment$Kind#M
     * @see Segment$Path
     */
    public Endpoint endpointIn1(Face f){

        if (f == this.a)

            return this.endpointB1;
        else 
            return this.endpointA1;
    }
    public Endpoint endpointIn2(Face f){

        if (f == this.a)

            return this.endpointB2;
        else 
            return this.endpointA2;
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

        return this.vector.compareTo(that.vector);
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

    public Vertex Intersect(Vertex lineA, Vertex lineB, Face fp){

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
            else {
                Vertex p = new Vertex(lineA.getVector().add(u.mul(s)));

                if (fp.contains(p))
                    /*
                     * Initial vertex classification
                     */
                    return p.boundary();
                else
                    return null;
            }
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
