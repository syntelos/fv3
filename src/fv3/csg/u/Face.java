/*
 * fv3 CSG
 * Copyright (C) 2010  John Pritchard, jdp@syntelos.org
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fv3.csg.u;

import fv3.csg.Solid;

import fv3.math.Matrix;
import fv3.math.Vector;
import static fv3.math.Vector.Magnitude1.*;

import lxl.Set;

/**
 * Triangular face used by {@link fv3.csg.Solid}.
 */
public final class Face
    extends java.lang.Object
    implements fv3.csg.u.Notation,
               fv3.csg.u.Name.Named,
               java.lang.Comparable<Face>,
               java.lang.Iterable<Vertex>,
               java.lang.Cloneable
{

    public final static class Name 
        extends fv3.csg.u.Name
    {
        public Name(Solid s, int index, String desc){
            super(Kind.Face,s,index,desc);
        }
        protected Name(Name n, String desc2){
            super(n,desc2);
        }

        public Name copy(String desc2){
            return new Name(this,desc2);
        }
    }
    /**
     * An interior edge joins faces with equivalent normals -- as in
     * the case of two triangles forming a rectangle, or the elements
     * of a triangle fan forming a disc.
     */
    public enum Edge {
        Interior, Exterior;


        public final static void Classify(Solid solid){

            for (Face face: solid){

                for (Vertex v: face){

                    for (Face join: v){
                        try {
                            Face.Shared shared = face.shared(join);

                            Vector faceN = face.getNormal();
                            Vector joinN = join.getNormal();

                            shared.edge(faceN.equals(joinN));
                        }
                        catch (IllegalArgumentException noedge){
                        }
                    }
                }
            }
        }
    }
    public final static void Classify(Solid solid){

        for (Face face: solid){

            if (!face.classify()){
                System.err.println("Failed to classify: "+face);
            }
        }
    }
    /**
     * Edge shared in faces A and B.  Sharing is symmetric:
     * Shared(a,b) == (Shared(b,a).  Sharing is assymmetric in
     * notation only.
     */
    public final static class Shared 
        extends java.lang.Object
        implements java.lang.Comparable<Shared>
    {
        /**
         * Shared edge of two vertices (A,B,C) in terms of the first
         * and second faces to the constructor:
         * <pre>
         * FROM  
         * VERTEX { (Face1) EQ (Face2) }
         * TO
         * VERTEX { (Face1) EQ (Face2) }.
         * </pre>
         * 
         * For example, "AA" means vertex "A" in the first face is
         * identical to vertex "A" in the second face.
         * 
         * For example, "AABB" identifies the edge known as "A.B."
         * (Edge AB) to the first face and ".A.B" (Edge AB) to the
         * second face.
         * 
         * For example, "ABBA" identifies the edge known as "A.B."
         * (AB) to the first face and ".B.A" (BA) to the second face.
         */
        public enum E {
            AABB, AABC, AACB, AACC, ABBA, ABBC, ABCA, ABCC, ACBA, ACBB, ACCB, ACCA, 
                BACB, BACC, BBCA, BBCC, BCCA, BCCB;

            public boolean isAB(){
                switch(this){
                case AABB:
                case AABC:
                    return true;
                case AACB:
                case AACC:
                    return false;
                case ABBA:
                case ABBC:
                    return true;
                case ABCA:
                case ABCC:
                    return false;
                case ACBA:
                case ACBB:
                    return true;
                case ACCB:
                case ACCA:
                case BACB:
                case BACC:
                case BBCA:
                case BBCC:
                case BCCA:
                case BCCB:
                    return false;
                default:
                    throw new IllegalStateException();
                }
            }
            public boolean isBC(){
                switch(this){
                case AABB:
                case AABC:
                case AACB:
                case AACC:
                case ABBA:
                case ABBC:
                case ABCA:
                case ABCC:
                case ACBA:
                case ACBB:
                case ACCB:
                case ACCA:
                    return false;
                case BACB:
                case BACC:
                case BBCA:
                case BBCC:
                case BCCA:
                case BCCB:
                    return true;
                default:
                    throw new IllegalStateException();
                }
            }
            public boolean isCA(){
                switch(this){
                case AABB:
                case AABC:
                    return false;
                case AACB:
                case AACC:
                    return true;
                case ABBA:
                case ABBC:
                    return false;
                case ABCA:
                case ABCC:
                    return true;
                case ACBA:
                case ACBB:
                    return false;
                case ACCB:
                case ACCA:
                    return true;
                case BACB:
                case BACC:
                case BBCA:
                case BBCC:
                case BCCA:
                case BCCB:
                    return false;
                default:
                    throw new IllegalStateException();
                }
            }
        }


        public final Face a, b;

        public final E edge;

        public final int hashCode;

        /**
         * Construct vertices and edge in A shared with B.  (Principal
         * A, Argument B).
         * 
         * @exception java.lang.IllegalArgumentException Shared edge not found.
         */
        public Shared(Face a, Face b)
            throws java.lang.IllegalArgumentException
        {
            super();
            this.a = a;
            this.b = b;
            this.hashCode = (a.hashCode()^b.hashCode());
            /*
             * This approach will run faster than testing vertex
             * membership
             */
            if (a.a == b.a){

                if (a.b == b.b)
                    this.edge = E.AABB;

                else if (a.b == b.c)
                    this.edge = E.AABC;

                else if (a.c == b.b)
                    this.edge = E.AACB;

                else if (a.c == b.c)
                    this.edge = E.AACC;

                else 
                    throw new IllegalArgumentException();
            }
            else if (a.a == b.b){

                if (a.b == b.a)
                    this.edge = E.ABBA;

                else if (a.b == b.c)
                    this.edge = E.ABBC;

                else if (a.c == b.a)
                    this.edge = E.ABCA;

                else if (a.c == b.c)
                    this.edge = E.ABCC;
                else 
                    throw new IllegalArgumentException();
            }
            else if (a.a == b.c){

                if (a.b == b.a)
                    this.edge = E.ACBA;

                else if (a.b == b.b)
                    this.edge = E.ACBB;

                else if (a.c == b.b)
                    this.edge = E.ACCB;

                else if (a.c == b.a)
                    this.edge = E.ACCA;
                else 
                    throw new IllegalArgumentException();
            }
            else if (a.b == b.a){

                if (a.c == b.b)
                    this.edge = E.BACB;

                else if (a.c == b.c)
                    this.edge = E.BACC;
                else 
                    throw new IllegalArgumentException();
            }
            else if (a.b == b.b){

                if (a.c == b.a)
                    this.edge = E.BBCA;

                else if (a.c == b.c)
                    this.edge = E.BBCC;
                else 
                    throw new IllegalArgumentException();
            }
            else if (a.b == b.c){

                if (a.c == b.a)
                    this.edge = E.BCCA;

                else if (a.c == b.b)
                    this.edge = E.BCCB;
                else 
                    throw new IllegalArgumentException();
            }
            else 
                throw new IllegalArgumentException();
        }


        public boolean isAB(){
            return this.edge.isAB();
        }
        public boolean isBC(){
            return this.edge.isBC();
        }
        public boolean isCA(){
            return this.edge.isCA();
        }
        public Shared edge(boolean interior){
            switch (this.edge){
            case AABB:
                if (interior){
                    this.a.ab = Face.Edge.Interior;
                    this.b.ab = Face.Edge.Interior;
                }
                else {
                    this.a.ab = Face.Edge.Exterior;
                    this.b.ab = Face.Edge.Exterior;
                }
                return this;
            case AABC:
                if (interior){
                    this.a.ab = Face.Edge.Interior;
                    this.b.ca = Face.Edge.Interior;
                }
                else {
                    this.a.ab = Face.Edge.Exterior;
                    this.b.ca = Face.Edge.Exterior;
                }
                return this;
            case AACB:
                if (interior){
                    this.a.ca = Face.Edge.Interior;
                    this.b.ab = Face.Edge.Interior;
                }
                else {
                    this.a.ca = Face.Edge.Exterior;
                    this.b.ab = Face.Edge.Exterior;
                }
                return this;
            case AACC:
                if (interior){
                    this.a.ca = Face.Edge.Interior;
                    this.b.ca = Face.Edge.Interior;
                }
                else {
                    this.a.ca = Face.Edge.Exterior;
                    this.b.ca = Face.Edge.Exterior;
                }
                return this;
            case ABBA:
                if (interior){
                    this.a.ab = Face.Edge.Interior;
                    this.b.ab = Face.Edge.Interior;
                }
                else {
                    this.a.ab = Face.Edge.Exterior;
                    this.b.ab = Face.Edge.Exterior;
                }
                return this;
            case ABBC:
                if (interior){
                    this.a.ab = Face.Edge.Interior;
                    this.b.bc = Face.Edge.Interior;
                }
                else {
                    this.a.ab = Face.Edge.Exterior;
                    this.b.bc = Face.Edge.Exterior;
                }
                return this;
            case ABCA:
                if (interior){
                    this.a.ca = Face.Edge.Interior;
                    this.b.ab = Face.Edge.Interior;
                }
                else {
                    this.a.ca = Face.Edge.Exterior;
                    this.b.ab = Face.Edge.Exterior;
                }
                return this;
            case ABCC:
                if (interior){
                    this.a.ca = Face.Edge.Interior;
                    this.b.bc = Face.Edge.Interior;
                }
                else {
                    this.a.ca = Face.Edge.Exterior;
                    this.b.bc = Face.Edge.Exterior;
                }
                return this;
            case ACBA:
                if (interior){
                    this.a.ab = Face.Edge.Interior;
                    this.b.ca = Face.Edge.Interior;
                }
                else {
                    this.a.ab = Face.Edge.Exterior;
                    this.b.ca = Face.Edge.Exterior;
                }
                return this;
            case ACBB:
                if (interior){
                    this.a.ab = Face.Edge.Interior;
                    this.b.bc = Face.Edge.Interior;
                }
                else {
                    this.a.ab = Face.Edge.Exterior;
                    this.b.bc = Face.Edge.Exterior;
                }
                return this;
            case ACCB:
                if (interior){
                    this.a.ca = Face.Edge.Interior;
                    this.b.bc = Face.Edge.Interior;
                }
                else {
                    this.a.ca = Face.Edge.Exterior;
                    this.b.bc = Face.Edge.Exterior;
                }
                return this;
            case ACCA:
                if (interior){
                    this.a.ca = Face.Edge.Interior;
                    this.b.ca = Face.Edge.Interior;
                }
                else {
                    this.a.ca = Face.Edge.Exterior;
                    this.b.ca = Face.Edge.Exterior;
                }
                return this; 
            case BACB:
                if (interior){
                    this.a.bc = Face.Edge.Interior;
                    this.b.ab = Face.Edge.Interior;
                }
                else {
                    this.a.bc = Face.Edge.Exterior;
                    this.b.ab = Face.Edge.Exterior;
                }
                return this;
            case BACC:
                if (interior){
                    this.a.bc = Face.Edge.Interior;
                    this.b.ca = Face.Edge.Interior;
                }
                else {
                    this.a.bc = Face.Edge.Exterior;
                    this.b.ca = Face.Edge.Exterior;
                }
                return this;
            case BBCA:
                if (interior){
                    this.a.bc = Face.Edge.Interior;
                    this.b.ab = Face.Edge.Interior;
                }
                else {
                    this.a.bc = Face.Edge.Exterior;
                    this.b.ab = Face.Edge.Exterior;
                }
                return this;
            case BBCC:
                if (interior){
                    this.a.bc = Face.Edge.Interior;
                    this.b.bc = Face.Edge.Interior;
                }
                else {
                    this.a.bc = Face.Edge.Exterior;
                    this.b.bc = Face.Edge.Exterior;
                }
                return this;
            case BCCA:
                if (interior){
                    this.a.bc = Face.Edge.Interior;
                    this.b.ca = Face.Edge.Interior;
                }
                else {
                    this.a.bc = Face.Edge.Exterior;
                    this.b.ca = Face.Edge.Exterior;
                }
                return this;
            case BCCB:
                if (interior){
                    this.a.bc = Face.Edge.Interior;
                    this.b.bc = Face.Edge.Interior;
                }
                else {
                    this.a.bc = Face.Edge.Exterior;
                    this.b.bc = Face.Edge.Exterior;
                }
                return this;
            default:
                throw new IllegalStateException();
            }
        }
        public int hashCode(){
            return this.hashCode;
        }
        public boolean equals(Object that){
            if (this == that)
                return true;
            else if (that instanceof Shared)
                return this.equals((Shared)that);
            else
                return false;
        }
        public boolean equals(Shared that){
            if (this == that)
                return true;
            else {
                return ((this.a.equals(that.a)
                         && this.b.equals(that.b))
                        ||
                        (this.a.equals(that.b)
                         && this.b.equals(that.a)));
            }
        }
        public int compareTo(Shared that){

            if (this.a.equals(that.a)){

                if (this.b.equals(that.b))

                    return 0;
                else
                    return this.b.compareTo(that.b);
            }
            else if (this.a.equals(that.b)){

                if (this.b.equals(that.a))

                    return 0;
                else
                    return this.b.compareTo(that.a);
            }
            else if (this.b.equals(that.b))

                return this.a.compareTo(that.a);

            else if (this.b.equals(that.a))

                return this.a.compareTo(that.b);
            else
                return this.a.compareTo(that.a);
        }
    }
    /**
     * Face replacement operator 
     * @see @AH
     */
    public final static class Replacement
        extends java.lang.Object
    {

        public final Face old;
        public final Face[] with;


        public Replacement(Face old, Face[] with){
            super();
            if (null != old){
                this.old = old;
                this.with = with;
            }
            else
                throw new IllegalArgumentException();
        }


        public void apply(Solid s){

            final Face[] with = this.with;
            if (null != with)
                s.replace(this.old,with);
        }


        public final static Replacement[] Add(Replacement[] list, Replacement item){
            if (null == item)
                return list;
            else if (null == list)
                return new Replacement[]{item};
            else {
                int len = list.length;
                Replacement[] copier = new Replacement[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = item;
                return copier;
            }
        }

    }


    public final Name name;

    public final int id, hashCode;
    /**
     * The only valid change to a face is one that is subsequently
     * reversed (i.e. invert normal).  All other changes must replace
     * the face in the state of the solid.
     */
    public Vertex a, b, c;

    public Edge ab, bc, ca;

    public State.Face status = State.Face.Unknown;

    private Vector normal, centroid;

    private Bound bound;

    private boolean inverted;

    private AH.Segment[] membership;

    private double d;

    private Face.Shared[] shared;

    private boolean alive = true;


    public Face(Solid s, Name n, double[] a, double[] b, double[] c){
        this(s,n,new Vertex(a),new Vertex(b),new Vertex(c));
    }
    public Face(Solid s, Name n, double[] a, double[] b, double[] c, double[] nv){
        this(s,n,new Vertex(a),new Vertex(b),new Vertex(c),new Vector(nv));
    }
    public Face(Solid s, Name n, double[] face){
        this(s,n,new Vertex(face,0),new Vertex(face,3),new Vertex(face,6));
    }
    public Face(Solid s, Name n, double[] face, double[] normal){
        this(s,n,new Vertex(face,0),new Vertex(face,3),new Vertex(face,6),new Vector(normal));
    }
    public Face(Solid s, Name n,
                double ax, double ay, double az, 
                double bx, double by, double bz, 
                double cx, double cy, double cz)
    {
        this(s,n,new Vertex(ax,ay,az),new Vertex(bx,by,bz),new Vertex(cx,cy,cz));
    }
    public Face(Solid s, Name n,
                double ax, double ay, double az, 
                double bx, double by, double bz, 
                double cx, double cy, double cz,
                double nx, double ny, double nz)
    {
        this(s,n,new Vertex(ax,ay,az),new Vertex(bx,by,bz),new Vertex(cx,cy,cz),new Vector(nx,ny,nz));
    }
    public Face(Solid s, Name n,
                double ax, double ay, double az, 
                double bx, double by, double bz, 
                double cx, double cy, double cz,
                Vector nv)
    {
        this(s,n,new Vertex(ax,ay,az),new Vertex(bx,by,bz),new Vertex(cx,cy,cz),nv);
    }
    public Face(Solid s, Name n, Vertex[] face){
        this(s,n,face[0],face[1],face[2]);
    }
    public Face(Solid s, Name n, Vertex a, Vertex b, Vertex c){
        super();
        if (null != s && null != n && null != a && null != b && null != c){
            this.name = n;
            this.id = n.id;

            this.a = s.u(a).memberOf(this);
            this.b = s.u(b).memberOf(this);
            this.c = s.u(c).memberOf(this);

            this.hashCode = this.a.hashCode()^this.b.hashCode()^this.c.hashCode();
        }
        else
            throw new IllegalArgumentException();
    }
    public Face(Solid s, Name n, Vertex a, Vertex b, Vertex c, Vector nv){
        super();
        if (null != s && null != n && null != a && null != b && null != c && null != nv){
            this.name = n;
            this.id = n.id;

            Vector check = a.getVector().normal(b.getVector(),c.getVector());
            Vector.Direction1 checkD = check.direction1();
            Vector.Direction1 nD = nv.direction1();
            switch (checkD.colinear(nD)){

            case 0:
                this.a = s.u(a).memberOf(this);
                this.b = s.u(b).memberOf(this);
                this.c = s.u(c).memberOf(this);
                break;
            case 1:
                this.a = s.u(a).memberOf(this);
                this.b = s.u(c).memberOf(this);
                this.c = s.u(b).memberOf(this);
                break;

            default:
                throw new IllegalStateException("Direction of argument normal ("+nD+") is incongruous with direction of face normal ("+checkD+")");
            }
            this.hashCode = this.a.hashCode()^this.b.hashCode()^this.c.hashCode();
        }
        else
            throw new IllegalArgumentException();
    }


    public void init(){

        this.status = State.Face.Unknown;

        if (this.inverted)
            this.uninvertNormal();

        this.a.init();
        this.b.init();
        this.c.init();

        this.membership = null;
    }
    public Name getName(){
        return this.name;
    }
    public Bound getBound(){
        Bound bound = this.bound;
        if (null == bound){
            bound = new Bound(this);
            this.bound = bound;
        }
        return bound;
    }
    public double[] vertices(){

        double[] re = new double[9];
        this.a.copy(re,0);
        this.b.copy(re,3);
        this.c.copy(re,6);
        return re;
    }
    public double[] normal(){
        return this.getNormal().array();
    }
    public double[] centroid(){
        return this.getCentroid().array();
    }
    public boolean is(State.Face s){
        return (s == this.status);
    }
    public boolean isUnknown(){
        return (State.Face.Unknown == this.status);
    }
    public boolean isNotUnknown(){
        return (State.Face.Unknown != this.status);
    }
    public boolean isInside(){
        return (State.Face.Inside == this.status);
    }
    public boolean isOutside(){
        return (State.Face.Outside == this.status);
    }
    public boolean isSame(){
        return (State.Face.Same == this.status);
    }
    public boolean isOpposite(){
        return (State.Face.Opposite == this.status);
    }
    public Face setUnknown(){
        this.status = State.Face.Unknown;
        return this;
    }
    public Face setInside(){
        this.status = State.Face.Inside;
        return this;
    }
    public Face setOutside(){
        this.status = State.Face.Outside;
        return this;
    }
    public Face setSame(){
        this.status = State.Face.Same;
        return this;
    }
    public Face setOpposite(){
        this.status = State.Face.Opposite;
        return this;
    }
    public boolean alive(){
        return this.alive;
    }
    public void destroy(Solid s){
        this.alive = false;
        this.membership = null;
        this.shared = null;

        if (this.a.dropMember(this))
            s.remove(this.a);

        if (this.b.dropMember(this))
            s.remove(this.b);

        if (this.c.dropMember(this))
            s.remove(this.c);
    }
    public Face transform(Solid s, Matrix m){

        this.deconstruct(s);

        Vertex a = new Vertex(m.transform(this.a.copy()));
        Vertex b = new Vertex(m.transform(this.b.copy()));
        Vertex c = new Vertex(m.transform(this.c.copy()));

        this.a = s.u(a).memberOf(this);
        this.b = s.u(b).memberOf(this);
        this.c = s.u(c).memberOf(this);

        return this;
    }
    public Face clone(Solid s){
        try {
            Face clone = (Face)super.clone();
            /*
             * Consistent with vertex cloning case
             */
            clone.status = State.Face.Unknown;

            clone.a = s.u(clone.a.clone()).memberOf(clone);
            clone.b = s.u(clone.b.clone()).memberOf(clone);
            clone.c = s.u(clone.c.clone()).memberOf(clone);

            if (null != this.membership)
                clone.membership = this.membership.clone();

            return clone;
        }
        catch (CloneNotSupportedException exc){
            throw new InternalError();
        }
    }
    public Face dropFrom(Solid s){

        this.alive = false;
        try {
            s.remove(this);
        }
        catch (java.util.NoSuchElementException ignore){
        }
        return this;
    }
    public Face deconstruct(Solid s){

        if (this.a.dropMember(this))
            s.remove(this.a);

        if (this.b.dropMember(this))
            s.remove(this.b);

        if (this.c.dropMember(this))
            s.remove(this.c);

        return this;
    }
	public Vector getNormal(){

        Vector n = this.normal;

        if (null == n){
            final Vertex a = this.a;
            final Vertex b = this.b;
            final Vertex c = this.c;

            n = a.getVector().normal(b.getVector(),c.getVector());
		
            this.normal = n;
            {
                final double[] na = n.array();

                final double x = na[X];
                final double y = na[Y];
                final double z = na[Z];

                this.d = -(x*a.x + x*a.y + z*a.z);
            }
        }
        return n;
	}
	public Vector getCentroid(){

        Vector c = this.centroid;

        if (null == c){

            c = this.a.getVector().centroid(this.b.getVector(),this.c.getVector());
		
            this.centroid = c;
        }
        return c;
	}
    public Face.Shared shared(Face join){

        Face.Shared[] shared = this.shared;
        if (null == shared){

            Face.Shared fs = new Face.Shared(this,join);
            this.shared = new Face.Shared[]{fs};
            return fs;
        }
        else {
            final int count = shared.length;

            for (int cc = 0; cc < count; cc++){
                Face.Shared s = shared[cc];
                if (s.b.equals(join))
                    return s;
            }

            Face.Shared fs = new Face.Shared(this,join);
            {
                Face.Shared[] copier = new Face.Shared[count+1];
                System.arraycopy(shared,0,copier,0,count);
                copier[count] = fs;
                this.shared = copier;
            }
            return fs;
        }
    }
    public Face shareAB(){
        Face.Shared[] shared = this.shared;
        for (int cc = 0; cc < 3; cc++){
            Face.Shared s = shared[cc];
            if (s.isAB())
                return s.b;
        }
        throw new IllegalStateException();
    }
    public Face shareBC(){
        Face.Shared[] shared = this.shared;
        for (int cc = 0; cc < 3; cc++){
            Face.Shared s = shared[cc];
            if (s.isBC())
                return s.b;
        }
        throw new IllegalStateException();
    }
    public Face shareCA(){
        Face.Shared[] shared = this.shared;
        for (int cc = 0; cc < 3; cc++){
            Face.Shared s = shared[cc];
            if (s.isCA())
                return s.b;
        }
        throw new IllegalStateException();
    }
    public Face share(Vertex u, Vertex v){
        if (u == this.a){
            if (v == this.b)
                return this.shareAB();

            else if (v == this.c)
                return this.shareCA();
            else
                throw new IllegalStateException();
        }
        else if (u == this.b){

            if (v == this.a)
                return this.shareAB();

            else if (v == this.c)
                return this.shareBC();
            else
                throw new IllegalStateException();
        }
        else if (u == this.c){

            if (v == this.a)
                return this.shareCA();

            else if (v == this.b)
                return this.shareBC();
            else
                throw new IllegalStateException();
        }
        else
            throw new IllegalStateException();
    }
    public Vertex opposite(Vertex u, Vertex v){
        if (u == this.a){
            if (v == this.b)
                return this.c;

            else if (v == this.c)
                return this.b;
            else
                throw new IllegalStateException();
        }
        else if (u == this.b){

            if (v == this.a)
                return this.c;

            else if (v == this.c)
                return this.a;
            else
                throw new IllegalStateException();
        }
        else if (u == this.c){

            if (v == this.a)
                return this.b;

            else if (v == this.b)
                return this.a;
            else
                throw new IllegalStateException();
        }
        else
            throw new IllegalStateException();
    }
    public boolean isMemberOf(AH.Segment segment){
        return (-1 != AH.Segment.IndexOf(this.membership,segment));
    }
    public boolean isNotMemberOf(AH.Segment segment){
        return (-1 == AH.Segment.IndexOf(this.membership,segment));
    }
    public Face memberOf(AH.Segment segment){

        if (null != segment && this.isNotMemberOf(segment)){

            this.membership = AH.Segment.Add(this.membership,segment);
            /*
             * Maintain path order at the segment path source.
             * See AH.Segment.Path
             */
            if (1 < this.membership.length)
                java.util.Arrays.sort(this.membership);
        }
        return this;
    }
    public int countMembership(){
        final AH.Segment[] m = this.membership;
        if (null == m)
            return 0;
        else 
            return m.length;
    }
    public int indexOfMember(AH.Segment item){
        return AH.Segment.IndexOf(this.membership,item);
    }
    public AH.Segment getMember(int idx){

        return this.membership[idx];
    }
    public boolean hasMembership(){
        return (null != this.membership);
    }
    public boolean hasNotMembership(){
        return (null == this.membership);
    }
    public AH.Segment[] segments(){
        return this.membership;
    }
    /**
     * Path order comparison
     */
    public int compareTo(Face that){
        if (this == that)
            return 0;
        else {
            int t = this.a.compareTo(that.a);
            if (0 == t){
                t = this.b.compareTo(that.b);
                if (0 == t)
                    return this.c.compareTo(that.c);
                else
                    return t;
            }
            else
                return t;
        }
    }
    public int compareTo(Vertex[] face){
        if (null == face || 3 != face.length)
            return 0;
        else {
            int t = this.a.compareTo(face[0]);
            if (0 == t){
                t = this.b.compareTo(face[1]);
                if (0 == t)
                    return this.c.compareTo(face[2]);
                else
                    return t;
            }
            else
                return t;
        }
    }
    public int hashCode(){
        return this.hashCode;
    }
    public boolean equals(Object that){
        if (this == that)
            return true;
        else if (that instanceof Face)
            return (0 == this.compareTo( (Face)that));
        else
            return false;
    }
    public boolean equals(Vertex[] face){

        return (0 == this.compareTo(face));
    }
    public String toString(){

        StringBuilder string = new StringBuilder();

        string.append(this.name);
        string.append(' ');
        string.append(this.status);
        string.append(' ');
        string.append(this.a);
        string.append(' ');
        string.append(this.b);
        string.append(' ');
        string.append(this.c);

        return string.toString();
    }
    /**
     * Invert normal direction
     */
    public void invertNormal(){
        if (this.inverted)
            throw new IllegalStateException();
        else {
            this.inverted = true;
            Vertex tmp = this.b;
            this.b = this.a;
            this.a = tmp;
            this.bound = null;
            this.normal = null;
            this.centroid = null;
            this.ab = null;
            this.bc = null;
            this.ca = null;
        }
    }
    public void uninvertNormal(){
        if (!this.inverted)
            throw new IllegalStateException();
        else {
            this.inverted = false;
            Vertex tmp = this.b;
            this.b = this.a;
            this.a = tmp;
            this.bound = null;
            this.normal = null;
            this.centroid = null;
            this.ab = null;
            this.bc = null;
            this.ca = null;
        }
    }
    /**
     */
    public double[] getNxyzd(){

        double[] n = this.normal();

        return new double[]{n[X],n[Y],n[Z],this.d};
    }
    /**
     * Called in triangulation on new faces.
     * @see AH
     */
    public Face classify(Vertex m){

        this.status = State.Vertex.ToFace(m.status);

        if (this.a.isUnknown())
            this.a.status = State.Vertex.Boundary;
        if (this.b.isUnknown())
            this.b.status = State.Vertex.Boundary;
        if (this.c.isUnknown())
            this.c.status = State.Vertex.Boundary;

        return this;
    }
    /**
     * Called in triangulation on new faces.
     * @see AH
     */
    public Face classify(Vertex u, Vertex v){

        return this.classify(this.share(u,v).opposite(u,v));
    }
    /**
     * Called after intersection, before triangulation.
     * @see AH
     */
    public Face classify(State.Vertex vs){

        if (this.hasNotMembership()){

            this.status = State.Vertex.ToFace(vs);

            this.a.classify(vs);
            this.b.classify(vs);
            this.c.classify(vs);
        }
        return this;
    }
    /**
     * Called after triangulation.
     * @see AH
     */
    public boolean classify(){
        if (this.isNotUnknown())
            return true;
        else if (this.a.isUnknown() && this.b.isUnknown() && this.c.isUnknown())
            return false;
        else {
            State.Vertex t;

            t = this.a.con(State.Vertex.Inside,State.Vertex.Outside);

            if (null != t){

                this.classify(t);

                return true;
            }
            else {
                t = this.b.con(State.Vertex.Inside,State.Vertex.Outside);

                if (null != t){

                    this.classify(t);

                    return true;
                }
                else {
                    t = this.c.con(State.Vertex.Inside,State.Vertex.Outside);

                    if (null != t){

                        this.classify(t);

                        return true;
                    }
                    else
                        return false;
                }
            }
        }
    }
    public Vertex.Iterator iterator(){

        return new Vertex.Iterator(this.a,this.b,this.c);
    }
    public boolean coplanar(Vertex v){
        return this.coplanar(v.getVector());
    }
    /**
     * @param v Point may be in plane of face.  (Pass a vector clone
     * for free use)
     */
    public boolean coplanar(Vector v){
        double d = (this.getNormal().dot(v.sub(this.a.getVector())));
        return (EPS > Math.abs(d));
    }
    public Vertex next(Vertex v){
        if (v == this.a)
            return this.b;
        else if (v == this.b)
            return this.c;
        else if (v == this.c)
            return this.a;
        else
            throw new IllegalStateException();
    }
    /**
     * @return A 2D approximation to the area for comparison as a
     * triangle quality metric
     */
    public double qArea(){

        switch(this.getNormal().magnitude1()){
        case MX:
            return (((a.y*b.z)-(b.y*a.z))+((b.y*c.z)-(c.y*b.z)))/2.0;

        case MY:
            return (((a.z*b.x)-(b.z*a.x))+((b.z*c.x)-(c.z*b.x)))/2.0;

        case MZ:
            return (((a.x*b.y)-(b.x*a.y))+((b.x*c.y)-(c.x*b.y)))/2.0;

        default:
            throw new IllegalStateException();
        }
    }
    public double qAngleMin(){

        Vector a = this.a.getVector().normalize();
        Vector b = this.b.getVector().normalize();
        Vector c = this.c.getVector().normalize();

        double ab = Math.abs(a.angle(b));
        double bc = Math.abs(b.angle(c));
        double ca = Math.abs(c.angle(a));

        return Math.min(Math.min(ab,bc),ca);
    }
    public double qAngleMax(){

        Vector a = this.a.getVector().normalize();
        Vector b = this.b.getVector().normalize();
        Vector c = this.c.getVector().normalize();

        double ab = Math.abs(a.angle(b));
        double bc = Math.abs(b.angle(c));
        double ca = Math.abs(c.angle(a));

        return Math.max(Math.max(ab,bc),ca);
    }

    public final static Face[] Add(Face[] list, Face item){
        if (null == item)
            return list;
        else if (null == list)
            return new Face[]{item};
        else {
            int len = list.length;
            Face[] copier = new Face[len+1];
            System.arraycopy(list,0,copier,0,len);
            copier[len] = item;
            return copier;
        }
    }
    public final static Face[] Cat(Face[] list1, Face[] list2){
        if (null == list2)
            return list1;
        else if (null == list1)
            return list2;
        else {
            int len1 = list1.length;
            int len2 = list2.length;
            Face[] copier = new Face[len1+len2];
            System.arraycopy(list1,0,copier,0,len1);
            System.arraycopy(list2,0,copier,len1,len2);
            return copier;
        }
    }
    public final static Face[][] Add(Face[][] list, Face[] item){
        if (null == item)
            return list;
        else if (null == list)
            return new Face[][]{item};
        else {
            int len = list.length;
            Face[][] copier = new Face[len+1][];
            System.arraycopy(list,0,copier,0,len);
            copier[len] = item;
            return copier;
        }
    }
    public static class Iterator
        extends java.lang.Object
        implements java.util.Iterator<Face>
    {

        public final int length;

        private final Face[] list;

        private int index;

        public Iterator(Face[] list){
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

        public boolean hasNext(){
            return (this.index < this.length);
        }
        public Face next(){
            return this.list[this.index++];
        }
        public void remove(){
            throw new UnsupportedOperationException();
        }
    }
}
