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
 * Algorithm developed following
 * 
 * Laidlaw, Trumbore and Hughes, 1986, "Constructive Solid Geometry
 * for Polyhedral Objects"</a>, and
 * 
 * Philip M. Hubbard, 1990, "Constructive Solid Geometry for
 * Triangulated Polyhedra" (an improvement on Laidlaw, Trumbore and
 * Hughes for triangles), and
 * 
 * with adaptations suggested by that paper and the work of Danilo
 * Balby Silva Castanheira in UnBBoolean/J3DBool.
 *
 *
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

        InitFaces(a,b);

        Face.Edge.Classify(a);
        Face.Edge.Classify(b);

        this.intersections = new AH.Intersections(a,b);

        this.intersections.classify();

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
     * Set of segments
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
     * Intersection segment
     */
    public final static class Segment
        extends java.lang.Object
        implements java.lang.Comparable<Segment>
    {
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

                public enum Vertex
                    implements Endpoint.Kind
                {
                    AA, AB, AC, BA, BB, BC;

                    public final static Vertex ChangeFace(Vertex v){
                        switch(v){
                        case AA:
                            return BA;
                        case AB:
                            return BB;
                        case AC:
                            return BC;
                        case BA:
                            return AA;
                        case BB:
                            return AB;
                        case BC:
                            return AC;
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
                public enum Edge
                    implements Endpoint.Kind
                {
                    AAB, ABC, ACA, BAB, BBC, BCA;


                    public final static Edge ChangeFace(Edge e){
                        switch(e){
                        case AAB:
                            return BAB;
                        case ABC:
                            return BBC;
                        case ACA:
                            return BCA;
                        case BAB:
                            return AAB;
                        case BBC:
                            return ABC;
                        case BCA:
                            return ACA;
                        default:
                            throw new IllegalStateException();
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
            }

            public final Kind kind;
            public final Vertex vertex;


            public Endpoint(Kind k, Vertex v){
                super();
                if (null != k && null != v){
                    this.kind = k;
                    this.vertex = v;
                }
                else
                    throw new IllegalStateException();//(error in Segment ctor)
            }
            /**
             * Change face for context change, when Face A is known as
             * Face B.
             */
            public Endpoint(Endpoint e){
                super();
                if (null != e){
                    if (e.isEdge())
                        this.kind = Endpoint.Kind.Edge.ChangeFace((Endpoint.Kind.Edge)e.kind);
                    else
                        this.kind = Endpoint.Kind.Vertex.ChangeFace((Endpoint.Kind.Vertex)e.kind);
                    this.vertex = e.vertex;
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
        public enum Kind {
            VE, EV, EE;

            public final static Kind For(Segment.Endpoint a, Segment.Endpoint b){

                if (null == a){

                    if (b.isEdge())
                        return Kind.EE;
                    else
                        throw new IllegalArgumentException();
                }
                else if (null == b){

                    if (a.isEdge())
                        return Kind.EE;
                    else
                        throw new IllegalArgumentException();
                }
                else if (a.isEdge()){

                    if (b.isEdge())
                        return Kind.EE;
                    else
                        return Kind.EV;
                }
                else 
                    return Kind.VE;
            }
        }

        public final Face a, b;

        public final Vector direction;
        /**
         * An endpoint may be null in the case of multiple
         * intersection segments in a face.
         */
        public final Endpoint endpointA1, endpointA2, endpointB1, endpointB2;

        public final int a_A_b, a_B_b, a_C_b;
        public final int b_A_a, b_B_a, b_C_a;

        public final Kind kindA, kindB;

        public final int hashCode;


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
            if ((null != endpointA1 || null != endpointA2)
                && (null != endpointB1 || null != endpointB2)){

                this.a = a;
                this.b = b;
                this.a_A_b = a_A_b;
                this.a_B_b = a_B_b;
                this.a_C_b = a_C_b;
                this.b_A_a = b_A_a;
                this.b_B_a = b_B_a; 
                this.b_C_a = b_C_a;
                this.kindA = Segment.Kind.For(endpointA1,endpointA2);
                this.kindB = Segment.Kind.For(endpointB1,endpointB2);
                this.endpointA1 = endpointA1;
                this.endpointA2 = endpointA2;
                this.endpointB1 = endpointB1;
                this.endpointB2 = endpointB2;
                /*
                 * Normalize for equivalence testing
                 */
                this.direction = a.getNormal().clone().cross(b.getNormal()).normalize();

                int hashCode = (this.a.hashCode()^this.b.hashCode());
                if (null != endpointA1)
                    hashCode ^= endpointA1.hashCode();
                if (null != endpointA2)
                    hashCode ^= endpointA2.hashCode();
                if (null != endpointB1)
                    hashCode ^= endpointB1.hashCode();
                if (null != endpointB2)
                    hashCode ^= endpointB2.hashCode();

                this.hashCode = hashCode;
                /*
                 */
                a.memberOf(this);
                b.memberOf(this);
            }
            else
                throw new IllegalArgumentException();
        }


        public boolean isA(Face face){
            return (face == this.a);
        }
        public boolean isB(Face face){
            return (face == this.b);
        }
        public boolean hasEndpoint(Endpoint e){
            return (null != e
                    && (e == this.endpointA1 || e == this.endpointA2 
                        || e == this.endpointB1 || e == this.endpointB2));
        }
        public char face(Face face){
            if (face == this.a)
                return 'a';
            else if (face == this.b)
                return 'b';
            else
                return 0;
        }
        public boolean hasVertexIn(Face face){

            if (face == this.a){
                if (null != this.endpointA1 && this.endpointA1.isVertex())
                    return true;
                else if (null != this.endpointA2 && this.endpointA2.isVertex())
                    return true;
                else
                    return false;
            }
            else if (face == this.b){
                if (null != this.endpointB1 && this.endpointB1.isVertex())
                    return true;
                else if (null != this.endpointB2 && this.endpointB2.isVertex())
                    return true;
                else
                    return false;
            }
            else
                throw new IllegalStateException();
        }
        public void triangulate(Solid sa, Solid sb){
            /*
             * Triangulation strategy
             */
            Face.Replacement[] replacements = null;
            /*
             * Face A
             */
            Face fa = this.a;
            if (1 < fa.countMembership()){

                replacements = Segment.TriangulateM(fa,sa);
            }
            else {
                switch (this.kindA){
                case VE:
                    replacements = Segment.TriangulateV(fa,sa,this.endpointA1,this.endpointA2);
                    break;
                case EV:
                    replacements = Segment.TriangulateV(fa,sa,this.endpointA2,this.endpointA1);
                    break;
                case EE:
                    replacements = Segment.TriangulateE(fa,sa,this.endpointA1,this.endpointA2);
                    break;
                default:
                    throw new IllegalStateException();
                }
            }
            if (null != replacements){
                for (Face.Replacement frpl: replacements){
                    frpl.apply(sa);
                }
            }
            /*
             * Face B
             */
            Face fb = this.b;
            if (1 < fb.countMembership()){

                replacements = Segment.TriangulateM(fb,sb);
            }
            else {
                switch (this.kindB){
                case VE:
                    replacements = Segment.TriangulateV(fb,sb,this.endpointB1,this.endpointB2);
                    break;
                case EV:
                    replacements = Segment.TriangulateV(fb,sb,this.endpointB2,this.endpointB1);
                    break;
                case EE:
                    replacements = Segment.TriangulateE(fb,sb,this.endpointB1,this.endpointB2);
                    break;
                default:
                    throw new IllegalStateException();
                }
            }
            if (null != replacements){
                for (Face.Replacement frpl: replacements){
                    frpl.apply(sb);
                }
            }
        }
        public void classify(){

            this.a.a.classify(State.Vertex.Classify(this.a_A_b));
            this.a.b.classify(State.Vertex.Classify(this.a_B_b));
            this.a.c.classify(State.Vertex.Classify(this.a_C_b));

            this.b.a.classify(State.Vertex.Classify(this.b_A_a));
            this.b.b.classify(State.Vertex.Classify(this.b_B_a));
            this.b.c.classify(State.Vertex.Classify(this.b_C_a));
        }
        public boolean colinear(Segment that){

            return this.direction.colinear(that.direction);
        }
        public Endpoint[] endpointsFor(Face f){
            if (f == this.a){
                return new Endpoint[]{
                    this.endpointA1,
                    this.endpointA2
                };
            }
            else if (f == this.b){
                return new Endpoint[]{
                    this.endpointB1,
                    this.endpointB2
                };
            }
            else
                return null;
        }
        public int hashCode(){
            return this.hashCode;
        }
        public boolean equals(Object that){
            if (this == that)
                return true;
            else if (that instanceof Segment)
                return this.equals( (Segment)that);
            else
                return false;
        }
        public boolean equals(Segment that){
            if (this == that)
                return true;
            else if (null == that)
                return false;
            else if (this.a.equals(that.a) && 
                     this.b.equals(that.b)){

                if (null != this.endpointA1){
                    if (null != that.endpointA1){
                        if (!this.endpointA1.equals(that.endpointA1))
                            return false;
                    }
                    else
                        return false;
                }
                else if (null != that.endpointA1)
                    return false;

                if (null != this.endpointA2){
                    if (null != that.endpointA2){
                        if (!this.endpointA2.equals(that.endpointA2))
                            return false;
                    }
                    else
                        return false;
                }
                else if (null != that.endpointA2)
                    return false;

                if (null != this.endpointB1){
                    if (null != that.endpointB1){
                        if (!this.endpointB1.equals(that.endpointB1))
                            return false;
                    }
                    else
                        return false;
                }
                else if (null != that.endpointB1)
                    return false;

                if (null != this.endpointB2){
                    if (null != that.endpointB2){
                        if (!this.endpointB2.equals(that.endpointB2))
                            return false;
                    }
                    else
                        return false;
                }
                else if (null != that.endpointB2)
                    return false;


                return true;
            }
            else
                return false;
        }
        public int compareTo(Segment that){
            if (this == that)
                return 0;
            else {
                int t;
                t = this.a.compareTo(that.a);
                if (0 == t){
                    t = this.b.compareTo(that.b);
                    if (0 == t){

                        if (null != this.endpointA1){
                            if (null != that.endpointA1){

                                t = this.endpointA1.compareTo(that.endpointA1);
                                if (0 != t)
                                    return t;
                            }
                            else
                                return 1;
                        }
                        else if (null != that.endpointA1)
                            return -1;

                        if (null != this.endpointA2){
                            if (null != that.endpointA2){

                                t = this.endpointA2.compareTo(that.endpointA2);
                                if (0 != t)
                                    return t;
                            }
                            else
                                return 1;
                        }
                        else if (null != that.endpointA2)
                            return -1;

                        if (null != this.endpointB1){
                            if (null != that.endpointB1){

                                t = this.endpointB1.compareTo(that.endpointB1);
                                if (0 != t)
                                    return t;
                            }
                            else
                                return 1;
                        }
                        else if (null != that.endpointB1)
                            return -1;

                        if (null != this.endpointB2){
                            if (null != that.endpointB2){

                                t = this.endpointB2.compareTo(that.endpointB2);
                                if (0 != t)
                                    return t;
                            }
                            else
                                return 1;
                        }
                        else if (null != that.endpointB2)
                            return -1;

                        return 0;
                    }
                    else
                        return t;
                }
                else
                    return t;
            }
        }

        /**
         * 
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
        /**
         * 
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
        /**
         * This depends on the member segments in {@link Face} being
         * in their sort order.
         */
        private final static Face.Replacement[] TriangulateM(Face f, Solid s){
            if (f.alive()){
                /*
                 * Starting with one approach...  This will not
                 * exploit internal features (not employing edge
                 * classes).
                 */
                Face[] replacements = null;

                final int count = f.countMembership();
                final int term = (count-1);
                final int mid = (count>>1);

            }
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
