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
 * CSG algorithm developed following
 * 
 * Laidlaw, Trumbore and Hughes, 1986, "Constructive Solid Geometry
 * for Polyhedral Objects"</a>, and
 * 
 * Philip M. Hubbard, 1990, "Constructive Solid Geometry for
 * Triangulated Polyhedra" (an improvement on Laidlaw, Trumbore and
 * Hughes for triangles), and
 * 
 * with adaptations suggested by that paper, and with the study and
 * application of the work of Danilo Balby Silva Castanheira in
 * UnBBoolean/J3DBool.
 *
 * <h3>Introduction</h3>
 * 
 * Certainly the most complex aspect of the code in these classes is
 * the symmetry of an intersection segment between two faces from each
 * of two solids.  
 * 
 * The intersection of two faces has two points, with many cases for
 * their representation.  Combined with symmetry, the reasonable
 * performance programming model is fairly intensive.
 * 
 * Each intersection segment is a member of two faces from each of the
 * two solids under operation.  The programming notation within the
 * Segment class is defined in terms of the CSG operand solids ("A"
 * and "B"), but this convenience is misleading.  The segment is
 * symmetric, and employed in the triangulation of each face ("A" and
 * "B") independently throughout the triangulation code.
 * 
 * In simple triangulation, TriangulateV or TriangulateE, this
 * symmetry is described by the grouping of equivalent "A" and "B"
 * cases.  For example, endpoint vertex kind "AA" and "BA" are grouped
 * into one case, and endpoint edge kind "AAB" and "BAB" are grouped
 * into one case.
 * 
 * For multi-segment triangulation in the TriangulateM family, the
 * Segment and Endpoint classes define methods that require a face
 * argument to relate perspective.
 * 
 * <h3>Design</h3>
 * 
 * This class is intended to develop one optimal solution set for
 * intersection and triangulation.  This is reasonable because
 * (generally) maximizing triangle quality has one solution set, and
 * maximizing triangle quality serves all purposes when not more
 * expensive than not maximizing triangle quality.
 * 
 * The implementation of triangulation by cases serves triangle
 * quality and performance.  Triangle quality is a design time subject
 * evident in the products of the code, not otherwise seen in the code
 * itself.
 * 
 * <h3>Implementation</h3>
 * 
 * The code in these classes is intended to perform error checking and
 * to throw runtime exceptions from the outer layers, and to throw
 * cast or array or pointer exceptions from the inner (self
 * referential) layers.  This policy is good for performance and
 * produces a good indication of severity.
 * 
 * The Intersection class constructor catches Illegal Argument
 * exceptions from the Segment class constructor in order to ignore an
 * empty intersection segment.  Therefore exceptions created to
 * indicate error conditions -- in code called from the Segment
 * constructor -- will throw Illegal State exceptions.  
 *
 * @see http://docs.google.com/document/pub?id=1uIZzKy_P6XTZMJ0-ciZeToiMkqUNzK5WejArjX0-BgI
 * @see http://unbboolean.sf.net/
 * @see http://www.cs.brown.edu/~jfh/papers/Laidlaw-CSG-1986/main.htm
 * @see doi:10.1.1.34.9374
 * @see ftp://ftp.cs.brown.edu/pub/techreports/90/cs90-07.ps.Z
 * 
 * @author John Pritchard
 */
public final class AH
    extends A
{

    public final AH.Intersections intersections;


    /**
     * Perform operation.  Subsequently requires call to method
     * "destroy".
     */
    public AH(Solid.Construct op, Solid a, Solid b){
        super(op,a,b);
        /*
         * Mark the internal and external features of A and B.
         */
        Face.Edge.Classify(a);
        Face.Edge.Classify(b);
        /*
         * Determine intersection segments throughout A and B.
         */
        this.intersections = new AH.Intersections(a,b);
        /*
         * Classify inside and outside verteces around the boundaries
         * in A and B.
         */
        this.intersections.classify();
        /*
         * Split faces in A and B, replacing old faces with new faces,
         * and classifying all vertices as inside, outside or boundary.
         */
        this.intersections.triangulate(a,b);

        Face.Classify(a);
        Face.Classify(b);


        switch (op){

        case Union:

            for (Face aFace: a){

                if (aFace.is(State.Face.Outside)
                    || aFace.is(State.Face.Same))
                    {
                        r.addC(op,aFace);
                    }
            }

            for (Face bFace: b){

                if (bFace.is(State.Face.Outside)){

                    r.addC(op,bFace);
                }
            }
            return ;

        case Intersection:

            for (Face aFace: a){

                if (aFace.is(State.Face.Inside)
                    || aFace.is(State.Face.Same))
                    {
                        r.addC(op,aFace);
                    }
            }

            for (Face bFace: b){

                if (bFace.is(State.Face.Inside)){

                    r.addC(op,bFace);
                }
            }
            return ;

        case Difference:

            InvertInsideFaces(b);

            for (Face aFace: a){

                if (aFace.is(State.Face.Outside)
                    || aFace.is(State.Face.Opposite))
                    {
                        r.addC(op,aFace);
                    }
            }

            for (Face bFace: b){

                if (bFace.is(State.Face.Inside)){

                    r.addC(op,bFace);
                }
            }
            return ;
        default:
            throw new IllegalStateException();
        }
    }


    public void destroy(){
        this.intersections.clear();
        super.destroy();
    }


    /**
     * Set of intersection segments in solids A and B.
     */
    public final static class Intersections
        extends lxl.Set<Segment>
    {

        private Solid a, b;

        Intersections(Solid a, Solid b){
            super();
            this.a = a;
            this.b = b;

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

                                            this.add(s);
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


        public void classify(){

            for (Segment s : this){

                s.classify();
            }
        }
        public void triangulate(Solid a, Solid b){

            for (Segment s : this){

                s.triangulate(a,b);
            }
        }
    }
    /**
     * An intersection segment has endpoints in Face A (from Solid A)
     * and Face B (from Solid B), and is shared by both.
     */
    public final static class Segment
        extends java.lang.Object
        implements java.lang.Comparable<Segment>
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
             * 
             * @see http://docs.google.com/document/pub?id=1uIZzKy_P6XTZMJ0-ciZeToiMkqUNzK5WejArjX0-BgI
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


            public Vertex startEndpointIn1(Face f){

                return this.list[0].endpointIn1(f).vertex;
            }
            public Vertex termEndpointIn2(Face f){

                return this.list[this.last-1].endpointIn2(f).vertex;
            }
            public Vertex startEndpointOn1(Face f){

                return this.list[0].endpointOn1(f).vertex;
            }
            public Vertex termEndpointOn2(Face f){

                return this.list[this.last-1].endpointOn2(f).vertex;
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
        private final Endpoint endpointA1, endpointA2, endpointB1, endpointB2;

        private final int a_A_b, a_B_b, a_C_b;
        private final int b_A_a, b_B_a, b_C_a;

        private final Kind kindA, kindB;
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
         * Split faces in solids A and B to accomodate their
         * intersection.
         */
        public void triangulate(Solid sa, Solid sb){
            /*
             * Triangulation strategy open to multiple solution over
             * internal features
             */
            Face.Replacement[] replacements = null;
            /*
             * Face A
             */
            switch (this.kindA){
            case M:
                replacements = Segment.TriangulateM(this.a,sa);
                break;
            case VE:
                replacements = Segment.TriangulateV(this.a,sa,this.endpointA1,this.endpointA2);
                break;
            case EV:
                replacements = Segment.TriangulateV(this.a,sa,this.endpointA2,this.endpointA1);
                break;
            case EE:
                replacements = Segment.TriangulateE(this.a,sa,this.endpointA1,this.endpointA2);
                break;
            default:
                throw new IllegalStateException();
            }
            if (null != replacements){
                for (Face.Replacement frpl: replacements){
                    frpl.apply(sa);
                }
            }
            /*
             * Face B
             */
            switch (this.kindB){
            case M:
                replacements = Segment.TriangulateM(this.b,sb);
                break;
            case VE:
                replacements = Segment.TriangulateV(this.b,sb,this.endpointB1,this.endpointB2);
                break;
            case EV:
                replacements = Segment.TriangulateV(this.b,sb,this.endpointB2,this.endpointB1);
                break;
            case EE:
                replacements = Segment.TriangulateE(this.b,sb,this.endpointB1,this.endpointB2);
                break;
            default:
                throw new IllegalStateException();
            }
            if (null != replacements){
                for (Face.Replacement frpl: replacements){
                    frpl.apply(sb);
                }
            }
        }
        /**
         * Classify verteces in solids A and B.
         */
        public void classify(){

            this.a.a.classify(State.Vertex.Classify(this.a_A_b));
            this.a.b.classify(State.Vertex.Classify(this.a_B_b));
            this.a.c.classify(State.Vertex.Classify(this.a_C_b));

            this.b.a.classify(State.Vertex.Classify(this.b_A_a));
            this.b.b.classify(State.Vertex.Classify(this.b_B_a));
            this.b.c.classify(State.Vertex.Classify(this.b_C_a));
        }
        /**
         * On the boundary defined by the vertices and edges of the
         * argument face.
         * @see AH$Segment$Kind#M
         * @see AH$Segment$Path
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
         * @see AH$Segment$Kind#M
         * @see AH$Segment$Path
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
         * New & cloned points have status unknown, then boundary.
         */
        private final static Face.Replacement[] TriangulateV(Face f, Solid s, Endpoint e1, Endpoint e2)
        {
            if (f.alive()){

                final Vertex a = f.a;
                final Vertex b = f.b;
                final Vertex c = f.c;

                switch ((Endpoint.Kind.Vertex)e1.kind){
                case AA:
                case BA:
                    switch((Endpoint.Kind.Edge)e2.kind){
                    case AAB:
                    case BAB:
                        throw new IllegalStateException("AH.TriangulateV(A,AB)");

                    case ABC:
                    case BBC:{

                        Vertex bc = e2.vertex;

                        return new Face.Replacement[]{
                            new Face.Replacement(f, new Face[]{
                                    new Face(s,f.name.copy("AH.TriangulateV(A,BC)"), a, b,bc).classify(b),
                                    new Face(s,f.name.copy("AH.TriangulateV(A,BC)"), a,bc, c).classify(c)
                                })
                        };
                    }
                    case ACA:
                    case BCA:
                        throw new IllegalStateException("AH.TriangulateV(A,CA)");

                    default:
                        throw new IllegalStateException();
                    }

                case AB:
                case BB:
                    switch((Endpoint.Kind.Edge)e2.kind){
                    case AAB:
                    case BAB:
                        throw new IllegalStateException("AH.TriangulateV(A,AB)");

                    case ABC:
                    case BBC:
                        throw new IllegalStateException("AH.TriangulateV(A,CA)");

                    case ACA:
                    case BCA:{

                        Vertex ca = e2.vertex;

                        return new Face.Replacement[]{
                            new Face.Replacement(f, new Face[]{
                                    new Face(s,f.name.copy("AH.TriangulateV(B,CA)"), b, c,ca).classify(c),
                                    new Face(s,f.name.copy("AH.TriangulateV(B,CA)"), b,ca, a).classify(a)
                                })
                        };
                    }
                    default:
                        throw new IllegalStateException();
                    }

                case AC:
                case BC:
                    switch((Endpoint.Kind.Edge)e2.kind){
                    case AAB:
                    case BAB:{

                        Vertex ab = e2.vertex;

                        return new Face.Replacement[]{
                            new Face.Replacement(f, new Face[]{
                                    new Face(s,f.name.copy("AH.TriangulateV(C,AB)"), c, a,ab).classify(a),
                                    new Face(s,f.name.copy("AH.TriangulateV(C,AB)"), c,ab, b).classify(b)
                                })
                        };
                    }
                    case ABC:
                    case BBC:
                        throw new IllegalStateException("AH.TriangulateV(C,BC)");

                    case ACA:
                    case BCA:
                        throw new IllegalStateException("AH.TriangulateV(C,CA)");

                    default:
                        throw new IllegalStateException();
                    }

                default:
                    throw new IllegalStateException();
                }
            }
            return null;
        }
        /*
         * New & cloned points have status unknown, then boundary.
         */
        private final static Face.Replacement[] TriangulateE(Face f, Solid s, Endpoint e1, Endpoint e2)
        {
            if (f.alive()){

                final Vertex a = f.a;
                final Vertex b = f.b;
                final Vertex c = f.c;
                /*
                 * Midpoint verteces
                 * 
                 * The approach taken here creates an additional
                 * midpoint to promote mesh quality (min angle,
                 * max angle, min area).
                 */
                Vertex ab, bc, ca;

                String nn;

                switch((Endpoint.Kind.Edge)e1.kind){
                case AAB:
                case BAB:
                    switch((Endpoint.Kind.Edge)e2.kind){
                    case AAB:
                    case BAB:
                        throw new IllegalStateException("AH.TriangulateE(AB,AB)");

                    case ABC:
                    case BBC:
                        /*
                         * Case "B"
                         */
                        nn = "AH.TriangulateE(AB,BC)";
                        ab = e1.vertex;
                        bc = e2.vertex;
                        ca = a.midpoint(c);

                        return new Face.Replacement[]{
                            new Face.Replacement(f, new Face[]{
                                    new Face(s,f.name.copy(nn), a,ab,ca).classify(a),
                                    new Face(s,f.name.copy(nn),ab, b,bc).classify(b),
                                    new Face(s,f.name.copy(nn),ca,ab,bc).classify(a),
                                    new Face(s,f.name.copy(nn), c,ca,bc).classify(a)
                                })
                        };
                    case ACA:
                    case BCA:
                        /*
                         * Case "A"
                         */
                        nn = "AH.TriangulateE(AB,CA)";
                        ab = e1.vertex;
                        bc = b.midpoint(c);
                        ca = e2.vertex;

                        return new Face.Replacement[]{
                            new Face.Replacement(f, new Face[]{
                                    new Face(s,f.name.copy(nn), a,ab,ca).classify(a),
                                    new Face(s,f.name.copy(nn),ab, b,bc).classify(b),
                                    new Face(s,f.name.copy(nn),ca,ab,bc).classify(b),
                                    new Face(s,f.name.copy(nn), c,ca,bc).classify(b)
                                })
                        };
                    default:
                        throw new IllegalStateException();
                    }

                case ABC:
                case BBC:
                    switch((Endpoint.Kind.Edge)e2.kind){
                    case AAB:
                    case BAB:
                        /*
                         * Case "B"
                         */
                        nn = "AH.TriangulateE(BC,AB)";
                        ab = e2.vertex;
                        ca = a.midpoint(c);
                        bc = e1.vertex;

                        return new Face.Replacement[]{
                            new Face.Replacement(f, new Face[]{
                                    new Face(s,f.name.copy(nn), a,ab,ca).classify(a),
                                    new Face(s,f.name.copy(nn),ab, b,bc).classify(b),
                                    new Face(s,f.name.copy(nn),ca,ab,bc).classify(a),
                                    new Face(s,f.name.copy(nn), c,ca,bc).classify(a)
                                })
                        };
                    case ABC:
                    case BBC:
                        throw new IllegalStateException("AH.TriangulateE(BC,BC)");

                    case ACA:
                    case BCA:
                        /*
                         * Case "C"
                         */
                        nn = "AH.TriangulateE(BC,CA)";
                        ab = a.midpoint(b);
                        bc = e1.vertex;
                        ca = e2.vertex;

                        return new Face.Replacement[]{
                            new Face.Replacement(f, new Face[]{
                                    new Face(s,f.name.copy(nn), a,ab,ca).classify(b),
                                    new Face(s,f.name.copy(nn),ab, b,bc).classify(b),
                                    new Face(s,f.name.copy(nn),ca,ab,bc).classify(b),
                                    new Face(s,f.name.copy(nn), c,ca,bc).classify(c)
                                })
                        };
                    default:
                        throw new IllegalStateException();
                    }

                case ACA:
                case BCA:
                    switch((Endpoint.Kind.Edge)e2.kind){
                    case AAB:
                    case BAB:
                        /*
                         * Case "A"
                         */
                        nn = "AH.TriangulateE(CA,AB)";
                        ab = e2.vertex;
                        bc = b.midpoint(c);
                        ca = e1.vertex;

                        return new Face.Replacement[]{
                            new Face.Replacement(f, new Face[]{
                                    new Face(s,f.name.copy(nn), a,ab,ca).classify(a),
                                    new Face(s,f.name.copy(nn),ab, b,bc).classify(b),
                                    new Face(s,f.name.copy(nn),ca,ab,bc).classify(b),
                                    new Face(s,f.name.copy(nn), c,ca,bc).classify(b)
                                })
                        };
                    case ABC:
                    case BBC:
                        /*
                         * Case "C"
                         */
                        nn = "AH.TriangulateE(CA,BC)";
                        ab = a.midpoint(b);
                        bc = e2.vertex;
                        ca = e1.vertex;

                        return new Face.Replacement[]{
                            new Face.Replacement(f, new Face[]{
                                    new Face(s,f.name.copy(nn), a,ab,ca).classify(b),
                                    new Face(s,f.name.copy(nn),ab, b,bc).classify(b),
                                    new Face(s,f.name.copy(nn),ca,ab,bc).classify(b),
                                    new Face(s,f.name.copy(nn), c,ca,bc).classify(c)
                                })
                        };
                    case ACA:
                    case BCA:
                        throw new IllegalStateException("AH.TriangulateE(CA,CA)");

                    default:
                        throw new IllegalStateException();
                    }

                default:
                    throw new IllegalStateException();
                }
            }
            return null;
        }
        /*
         * New & cloned points have status unknown, then boundary.
         */
        private final static Face.Replacement[] TriangulateM(Face f, Solid s){
            if (f.alive()){

                Segment.Path p = new Segment.Path(f);

                switch (p.kind){
                case VIE:
                    return TriangulateM_VIE(f, s, p);
                case VTE:
                    return TriangulateM_VTE(f, s, p);
                case VTV:
                    return TriangulateM_VTV(f, s, p);
                case EIV:
                    return TriangulateM_EIV(f, s, p);
                case ETV:
                    return TriangulateM_ETV(f, s, p);
                case EIE:
                    return TriangulateM_EIE(f, s, p);
                case ETE:
                    return TriangulateM_ETE(f, s, p);

                default:
                    throw new IllegalStateException();
                }
            }
            return null;
        }
        /*
         * New & cloned points have status unknown, then boundary.
         */
        private final static Face.Replacement[] TriangulateM_VIE(Face f, Solid s, Segment.Path p){
            Face[] replacements = null;

            final int count = f.countMembership();

            final boolean outbound = p.start.isOutbound(p.end);

            final Vertex a = p.start.vertex;
            final Vertex b = f.next(a);
            final Vertex c = f.next(b);
            final Vertex e = p.end.vertex;

            if (2 == count){

                final Vertex o = p.startEndpointIn1(f);
                /*
                 * Not boundary point: needs classification
                 */
                final Vertex m = b.midpoint(c).classify(c);

                if (outbound){
                    replacements = new Face[]{
                        new Face(s,f.name.copy("TriangulateM_VIE(2/O)"),a,e,o).classify(a,b),
                        new Face(s,f.name.copy("TriangulateM_VIE(2/O)"),a,o,c).classify(c),
                        new Face(s,f.name.copy("TriangulateM_VIE(2/O)"),c,o,m).classify(c),
                        new Face(s,f.name.copy("TriangulateM_VIE(2/O)"),e,m,o).classify(c),
                        new Face(s,f.name.copy("TriangulateM_VIE(2/O)"),e,b,m).classify(b)
                    };
                }
                else {
                    replacements = new Face[]{
                        new Face(s,f.name.copy("TriangulateM_VIE(2/I)"),a,o,e).classify(a,b),
                        new Face(s,f.name.copy("TriangulateM_VIE(2/I)"),a,c,o).classify(c),
                        new Face(s,f.name.copy("TriangulateM_VIE(2/I)"),c,m,o).classify(c),
                        new Face(s,f.name.copy("TriangulateM_VIE(2/I)"),e,o,m).classify(c),
                        new Face(s,f.name.copy("TriangulateM_VIE(2/I)"),e,m,b).classify(b)
                    };
                }
            }
            else {
                final int term = (count-1);
                /*
                 * Not boundary points: need classification
                 */
                final Vertex m1 = a.midpoint(e).classify(f.share(a,b).opposite(a,b));
                final Vertex m2 = b.midpoint(c).classify(c);

                Vertex o;

                o = p.startEndpointIn1(f);

                if (outbound){
                    replacements = new Face[]{
                        new Face(s,f.name.copy("TriangulateM_VIE(N/O)"), a, o, c).classify(c),
                        new Face(s,f.name.copy("TriangulateM_VIE(N/O)"), a,m1, o).classify(c),
                    };
                }
                else {
                    replacements = new Face[]{
                        new Face(s,f.name.copy("TriangulateM_VIE(N/I)"), a, c, o).classify(c),
                        new Face(s,f.name.copy("TriangulateM_VIE(N/I)"), a, o,m1).classify(c),
                    };
                }

                for (int sc = 1; sc < term; sc++){
                    Segment se = p.list[sc];
                    Vertex o1 = se.endpointIn1(f).vertex;
                    Vertex o2 = se.endpointIn2(f).vertex;

                    if (outbound){
                        replacements = Face.Cat(replacements, new Face[]{
                                new Face(s,f.name.copy("TriangulateM_VIE(N/O)"),o1,m1,o2).classify(a,b),
                                new Face(s,f.name.copy("TriangulateM_VIE(N/O)"),o1,o2, c).classify(c),
                            });
                    }
                    else {
                        replacements = Face.Cat(replacements, new Face[]{
                                new Face(s,f.name.copy("TriangulateM_VIE(N/I)"),o1,o2,m1).classify(a,b),
                                new Face(s,f.name.copy("TriangulateM_VIE(N/I)"),o1, c,o2).classify(c),
                            });
                    }
                }

                o = p.termEndpointIn2(f);

                if (outbound){
                    replacements = Face.Cat(replacements, new Face[]{
                            new Face(s,f.name.copy("TriangulateM_VIE(N/O)"),m1, e, o).classify(a,b),
                            new Face(s,f.name.copy("TriangulateM_VIE(N/O)"), o, e,m2).classify(c),
                            new Face(s,f.name.copy("TriangulateM_VIE(N/O)"),m2, e, b).classify(b)
                        });
                }
                else {
                    replacements = Face.Cat(replacements, new Face[]{
                            new Face(s,f.name.copy("TriangulateM_VIE(N/I)"),m1, o, e).classify(a,b),
                            new Face(s,f.name.copy("TriangulateM_VIE(N/I)"), o,m2, e).classify(c),
                            new Face(s,f.name.copy("TriangulateM_VIE(N/I)"),m2, b, e).classify(b)
                        });
                }
            }
            return new Face.Replacement[]{
                new Face.Replacement(f,replacements)
            };
        }
        private final static Face.Replacement[] TriangulateM_EIV(Face f, Solid s, Segment.Path p){
            ////////////////////////////////
            ////////////////////////////////
            ////////////////////////////////
            return null;
        }
        /*
         * New & cloned points have status unknown, then boundary.
         */
        private final static Face.Replacement[] TriangulateM_VTE(Face f, Solid s, Segment.Path p){
            Face[] replacements = null;

            final int count = f.countMembership();

            final boolean outbound = p.start.isOutbound(p.end);

            final Vertex a = p.start.vertex;
            final Vertex b = f.next(a);
            final Vertex c = f.next(b);
            final Vertex e = p.end.vertex;

            /*
             * Not boundary points: need classification
             */
            final Vertex m1 = a.midpoint(b).classify(b);
            final Vertex m2 = c.midpoint(a).classify(c);

            if (2 == count){

                final Vertex o = p.startEndpointIn1(f);

                if (outbound){
                    replacements = new Face[]{
                        new Face(s,f.name.copy("TriangulateM_VTE(2/O)"), a, o,m2).classify(c),
                        new Face(s,f.name.copy("TriangulateM_VTE(2/O)"), a,m1, o).classify(b),
                        new Face(s,f.name.copy("TriangulateM_VTE(2/O)"), o, e,m2).classify(c),
                        new Face(s,f.name.copy("TriangulateM_VTE(2/O)"), o,m1, e).classify(b),
                        new Face(s,f.name.copy("TriangulateM_VTE(2/O)"),m2, e, c).classify(c),
                        new Face(s,f.name.copy("TriangulateM_VTE(2/O)"),m1, e, b).classify(b)
                    };
                }
                else {
                    replacements = new Face[]{
                        new Face(s,f.name.copy("TriangulateM_VTE(2/I)"), a,m2, o).classify(c),
                        new Face(s,f.name.copy("TriangulateM_VTE(2/I)"), a, o,m1).classify(b),
                        new Face(s,f.name.copy("TriangulateM_VTE(2/I)"), o,m2, e).classify(c),
                        new Face(s,f.name.copy("TriangulateM_VTE(2/I)"), o, e, m1).classify(b),
                        new Face(s,f.name.copy("TriangulateM_VTE(2/I)"),m2, c, e).classify(c),
                        new Face(s,f.name.copy("TriangulateM_VTE(2/I)"),m1, b, e).classify(b)
                    };
                }
            }
            else {
                final int term = (count-1);

                Vertex o;

                o = p.startEndpointIn1(f);

                if (outbound){
                    replacements = new Face[]{
                        new Face(s,f.name.copy("TriangulateM_VTE(2/O)"), a, o,m2).classify(c),
                        new Face(s,f.name.copy("TriangulateM_VTE(2/O)"), a,m1, o).classify(b)
                    };
                }
                else {
                    replacements = new Face[]{
                        new Face(s,f.name.copy("TriangulateM_VTE(2/I)"), a,m2, o).classify(c),
                        new Face(s,f.name.copy("TriangulateM_VTE(2/I)"), a, o,m1).classify(b)
                    };
                }

                for (int sc = 1; sc < term; sc++){
                    Segment se = p.list[sc];
                    Vertex o1 = se.endpointIn1(f).vertex;
                    Vertex o2 = se.endpointIn2(f).vertex;

                    if (outbound){
                        replacements = Face.Cat(replacements, new Face[]{
                                new Face(s,f.name.copy("TriangulateM_VTE(N/O)"),o1,m1,o2).classify(b),
                                new Face(s,f.name.copy("TriangulateM_VTE(N/O)"),o1,o2,m2).classify(c),
                            });
                    }
                    else {
                        replacements = Face.Cat(replacements, new Face[]{
                                new Face(s,f.name.copy("TriangulateM_VTE(N/I)"),o1,o2,m1).classify(b),
                                new Face(s,f.name.copy("TriangulateM_VTE(N/I)"),o1,m2,o2).classify(c),
                            });
                    }
                }

                o = p.termEndpointIn2(f);

                if (outbound){
                    replacements = Face.Cat(replacements, new Face[]{
                            new Face(s,f.name.copy("TriangulateM_VTE(N/O)"),m1, o, b).classify(b),
                            new Face(s,f.name.copy("TriangulateM_VTE(N/O)"), o, b, e).classify(b),
                            new Face(s,f.name.copy("TriangulateM_VTE(N/O)"),m2, o, e).classify(c),
                            new Face(s,f.name.copy("TriangulateM_VTE(N/O)"),m2, e, c).classify(c)
                        });
                }
                else {
                    replacements = Face.Cat(replacements, new Face[]{
                            new Face(s,f.name.copy("TriangulateM_VTE(N/I)"),m1, b, o).classify(b),
                            new Face(s,f.name.copy("TriangulateM_VTE(N/I)"), o, e, b).classify(b),
                            new Face(s,f.name.copy("TriangulateM_VTE(N/I)"),m2, e, o).classify(c),
                            new Face(s,f.name.copy("TriangulateM_VTE(N/I)"),m2, c, e).classify(c)
                        });
                }
            }
            return new Face.Replacement[]{
                new Face.Replacement(f,replacements)
            };
        }
        private final static Face.Replacement[] TriangulateM_ETV(Face f, Solid s, Segment.Path p){
            ////////////////////////////////
            ////////////////////////////////
            ////////////////////////////////
            return null;
        }
        private final static Face.Replacement[] TriangulateM_EIE(Face f, Solid s, Segment.Path p){
            Face[] replacements = null;

            final int count = f.countMembership();

            final Vertex e1 = p.start.vertex;
            final Vertex e2 = p.end.vertex;
            final Vertex a = p.startFaceVertexForEdgeIn1(f);
            final Vertex b = f.next(a);
            final Vertex c = f.next(b);

            /*
             * Not boundary point: needs classification
             */
            final Vertex m1 = c.midpoint(a).classify(c);
            final Vertex m2 = b.midpoint(c).classify(c);

            final boolean outbound = p.startIsOutboundFromFaceVertexForEdgeIn1(f,a);

            if (2 == count){

                final Vertex o = p.startEndpointIn1(f);

                if (outbound){
                    replacements = new Face[]{
                        new Face(s,f.name.copy("TriangulateM_EIE(2/O)"), a,e1,m1).classify(a),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/O)"),e1, o,m1).classify(c),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/O)"),m1, o, c).classify(c),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/O)"), c, o,m2).classify(c),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/O)"), o,e2,m2).classify(c),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/O)"),e2, b,m2).classify(b),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/O)"),e1,e2, o).classify(a,b)
                    };
                }
                else {
                    replacements = new Face[]{
                        new Face(s,f.name.copy("TriangulateM_EIE(2/I)"), a,m1,e1).classify(a),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/I)"),e1,m1, o).classify(c),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/I)"),m1, c, o).classify(c),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/I)"), c,m2, o).classify(c),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/I)"), o,m2,e2).classify(c),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/I)"),e2,m2, b).classify(b),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/I)"),e1, o,e2).classify(a,b)
                    };
                }
            }
            else {
                final int term = (count-1);
                /*
                 * Not boundary points: need classification
                 */
                final Vertex m3 = e1.midpoint(e2).classify(f.share(a,b).opposite(a,b));

                Vertex o;

                o = p.startEndpointIn1(f);

                if (outbound){
                    replacements = new Face[]{
                        new Face(s,f.name.copy("TriangulateM_EIE(2/O)"), a,e1,m1).classify(a),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/O)"),e1, o,m1).classify(c),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/O)"),m1, o, c).classify(c),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/O)"),e1, o,m3).classify(c),
                    };
                }
                else {
                    replacements = new Face[]{
                        new Face(s,f.name.copy("TriangulateM_EIE(2/I)"), a,m1,e1).classify(a),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/I)"),e1,m1, o).classify(c),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/I)"),m1, c, o).classify(c),
                        new Face(s,f.name.copy("TriangulateM_EIE(2/O)"),e1,m3, o).classify(c),
                    };
                }

                for (int sc = 1; sc < term; sc++){
                    Segment se = p.list[sc];
                    Vertex o1 = se.endpointIn1(f).vertex;
                    Vertex o2 = se.endpointIn2(f).vertex;

                    if (outbound){
                        replacements = Face.Cat(replacements, new Face[]{
                                new Face(s,f.name.copy("TriangulateM_EIE(N/O)"),o1,m3,o2).classify(a,b),
                                new Face(s,f.name.copy("TriangulateM_EIE(N/O)"),o1,o2, c).classify(c)
                            });
                    }
                    else {
                        replacements = Face.Cat(replacements, new Face[]{
                                new Face(s,f.name.copy("TriangulateM_EIE(N/I)"),o1,o2,m3).classify(a,b),
                                new Face(s,f.name.copy("TriangulateM_EIE(N/I)"),o1, c,o2).classify(c)
                            });
                    }
                }

                o = p.endEndpointOn2(f);

                if (outbound){
                    replacements = Face.Cat(replacements, new Face[]{
                            new Face(s,f.name.copy("TriangulateM_EIE(N/O)"),m3,e2, o).classify(a,b),
                            new Face(s,f.name.copy("TriangulateM_EIE(N/O)"), o,e2,m2).classify(c),
                            new Face(s,f.name.copy("TriangulateM_EIE(N/O)"), o,m2, c).classify(c),
                            new Face(s,f.name.copy("TriangulateM_EIE(N/O)"),e2,m2, b).classify(b)
                        });
                }
                else {
                    replacements = Face.Cat(replacements, new Face[]{
                            new Face(s,f.name.copy("TriangulateM_EIE(N/I)"),m3, o,e2).classify(a,b),
                            new Face(s,f.name.copy("TriangulateM_EIE(N/I)"), o,m2,e2).classify(c),
                            new Face(s,f.name.copy("TriangulateM_EIE(N/I)"), o, c,m2).classify(c),
                            new Face(s,f.name.copy("TriangulateM_EIE(N/I)"),e2, b,m2).classify(b)
                        });
                }
            }
            return new Face.Replacement[]{
                new Face.Replacement(f,replacements)
            };
        }
        private final static Face.Replacement[] TriangulateM_ETE(Face f, Solid s, Segment.Path p){
            ////////////////////////////////
            ////////////////////////////////
            ////////////////////////////////
            return null;
        }
        private final static Face.Replacement[] TriangulateM_VTV(Face f, Solid s, Segment.Path p){
            ////////////////////////////////
            ////////////////////////////////
            ////////////////////////////////
            return null;
        }
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
                else
                    return new Vertex(lineA.getVector().add(u.mul(s)));
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
}
