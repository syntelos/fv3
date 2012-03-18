/*
 * fv3 CSG
 * Copyright (C) 2012  John Pritchard, all rights reserved.
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
import static fv3.csg.u.Triangulate.Kind.*;
import fv3.math.Matrix;
import fv3.math.Vector;

/**
 * Triangular face used by {@link fv3.csg.Solid}.
 */
public final class Face
    extends fv3.math.Abstract
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
     * Barycentric method 
     * 
     * An intersection is "on" the face when the intersection vertex
     * lies on an edge of the face.  An intersection is "in" the face
     * with the intersection vertex lies within the area of the face
     * plane bounded by the edges.  The "In" state is exclusive of the
     * "On" state.
     * 
     * @see http://www.blackpawn.com/texts/pointinpoly/default.html
     */
    public static class Intersection {
        /**
         * Intersection face
         */
        public final Face face;
        /**
         * Intersection vertex
         */
        public final Vertex vertex;

        public final float ca, ba;

        public final boolean in, on, onAB, onBC, onCA;

        public final boolean isA, isB, isC;


        /**
         * The intersection a face and a point.
         * 
         * @exception java.lang.IllegalArgumentException Intersection not found.
         */
        public Intersection(Face f, Vertex p)
            throws java.lang.IllegalArgumentException
        {
            super();
            if (null != f && null != p){
                this.face = f;
                this.vertex = p;
                {
                    final Vector ca = f.c.getVector().sub(f.a.getVector());
                    final Vector ba = f.b.getVector().sub(f.a.getVector());
                    final Vector pa = p.getVector().sub(f.a.getVector());
                    final float dotCACA = ca.dot(ca);
                    final float dotCABA = ca.dot(ba);
                    final float dotCAPA = ca.dot(pa);
                    final float dotBABA = ba.dot(ba);
                    final float dotBAPA = ba.dot(pa);
                    final float _den = (dotCACA * dotBABA - dotCABA * dotCABA);

                    this.ca = Z1((dotBABA * dotCAPA - dotCABA * dotBAPA) / _den);
                    this.ba = Z1((dotCACA * dotBAPA - dotCABA * dotCAPA) / _den);
                }
                /*
                 */
                if (0.0 > this.ca || 0.0 > this.ba)

                    throw new IllegalArgumentException();

                else if (0.0 == this.ca){

                    if (this.ba <= 1.0){
                        this.in = false;
                        this.on = true;

                        this.onAB = true;
                        this.onBC = false;
                        this.onCA = false;

                        this.isB = (1.0 == this.ba);
                        if (this.isB)
                            this.isA = false;
                        else
                            this.isA = (0.0 == this.ba);

                        this.isC = false;
                    }
                    else
                        throw new IllegalArgumentException();
                }
                else if (0.0 == this.ba){

                    if (this.ca <= 1.0){
                        this.in = false;
                        this.on = true;
                        this.onAB = false;
                        this.onBC = false;
                        this.onCA = true;

                        this.isA = false;
                        this.isB = false;
                        this.isC = (1.0 == this.ca);
                    }
                    else
                        throw new IllegalArgumentException();
                }
                else {
                    final float sum = this.ca + this.ba;

                    this.isA = false;
                    this.isB = false;
                    this.isC = false;

                    if (EEQ(1.0f, sum)){
                        this.in = false;
                        this.on = true;
                        this.onAB = false;
                        this.onBC = true;
                        this.onCA = false;
                    }
                    else if (sum < 1.0f){
                        this.in = true;
                        this.on = false;
                        this.onAB = false;
                        this.onBC = false;
                        this.onCA = false;
                    }
                    else
                        throw new IllegalArgumentException();
                }
            }
            else
                throw new IllegalArgumentException();
        }



        public int hashCode(){
            return this.vertex.hashCode();
        }
        public boolean equals(Object that){
            if (that instanceof Intersection)
                return this.equals((Intersection)that);
            else
                return false;
        }
        public boolean equals(Intersection that){
            if (null == that)
                return false;
            else
                return this.vertex.equals(that.vertex);
        }
        /**
         * Path order comparison
         */
        public int compareTo(Intersection that){
            if (null == that)
                return 1;
            else
                return this.vertex.compareTo(that.vertex);
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

    private Vector normal, centroid;

    private Bound bound;

    private boolean inverted;

    private Segment[] membership;

    private float d;

    private boolean alive = true;


    public Face(Solid s, Name n, float[] a, float[] b, float[] c){
        this(s,n,new Vertex(a),new Vertex(b),new Vertex(c));
    }
    public Face(Solid s, Name n, float[] a, float[] b, float[] c, float[] nv){
        this(s,n,new Vertex(a),new Vertex(b),new Vertex(c),new Vector(nv));
    }
    public Face(Solid s, Name n, float[] face){
        this(s,n,new Vertex(face,0),new Vertex(face,3),new Vertex(face,6));
    }
    public Face(Solid s, Name n, float[] face, float[] normal){
        this(s,n,new Vertex(face,0),new Vertex(face,3),new Vertex(face,6),new Vector(normal));
    }
    public Face(Solid s, Name n,
                float ax, float ay, float az, 
                float bx, float by, float bz, 
                float cx, float cy, float cz)
    {
        this(s,n,new Vertex(ax,ay,az),new Vertex(bx,by,bz),new Vertex(cx,cy,cz));
    }
    public Face(Solid s, Name n,
                float ax, float ay, float az, 
                float bx, float by, float bz, 
                float cx, float cy, float cz,
                float nx, float ny, float nz)
    {
        this(s,n,new Vertex(ax,ay,az),new Vertex(bx,by,bz),new Vertex(cx,cy,cz),new Vector(nx,ny,nz));
    }
    public Face(Solid s, Name n,
                float ax, float ay, float az, 
                float bx, float by, float bz, 
                float cx, float cy, float cz,
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

            assert (!a.equals(b));
            assert (!b.equals(c));

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

            assert (!a.equals(b));
            assert (!b.equals(c));

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
    public float getBoundsMinX(){
        return this.getBound().getBoundsMinX();
    }
    public float getBoundsMidX(){
        return this.getBound().getBoundsMidX();
    }
    public float getBoundsMaxX(){
        return this.getBound().getBoundsMaxX();
    }
    public float getBoundsMinY(){
        return this.getBound().getBoundsMinY();
    }
    public float getBoundsMidY(){
        return this.getBound().getBoundsMidY();
    }
    public float getBoundsMaxY(){
        return this.getBound().getBoundsMaxY();
    }
    public float getBoundsMinZ(){
        return this.getBound().getBoundsMinZ();
    }
    public float getBoundsMidZ(){
        return this.getBound().getBoundsMidZ();
    }
    public float getBoundsMaxZ(){
        return this.getBound().getBoundsMaxZ();
    }
    public float[] vertices(){

        float[] re = new float[9];
        this.a.copy(re,0);
        this.b.copy(re,3);
        this.c.copy(re,6);
        return re;
    }
    public float[] normal(){
        return this.getNormal().array();
    }
    public float[] centroid(){
        return this.getCentroid().array();
    }
    /**
     * @param from Edge vertex 
     * @param to Edge vertex 
     * @return Direction from vertex 'from' to vertex 'to' is in winding order
     */
    public boolean isEdgeOrder(Vertex from, Vertex to){

        if (from == this.a){
            if (to == this.b)
                return true;
            else if (to == this.c)
                return false;
            else if (to == this.a)
                throw new IllegalArgumentException("Arguments identical");
            else
                throw new IllegalArgumentException("Argument not face vertex");
        }
        else if (from == this.b){
            if (to == this.a)
                return false;
            else if (to == this.c)
                return true;
            else if (to == this.b)
                throw new IllegalArgumentException("Arguments identical");
            else
                throw new IllegalArgumentException("Argument not face vertex");
        }
        else if (from == this.c){
            if (to == this.a)
                return true;
            else if (to == this.b)
                return false;
            else if (to == this.c)
                throw new IllegalArgumentException("Arguments identical");
            else
                throw new IllegalArgumentException("Argument not face vertex");
        }
        else
            throw new IllegalArgumentException("Argument not face vertex");
    }
    public boolean is(State s){
        return (s == this.a.status || s == this.b.status || s == this.c.status);
    }
    public boolean isnot(State s){
        return (s != this.a.status && s != this.b.status && s != this.c.status);
    }
    public boolean isUnknown(){
        return this.is(State.Unknown);
    }
    public boolean isNotUnknown(){
        return this.isnot(State.Unknown);
    }
    public boolean isInside(){
        return (State.Inside == this.a.status);
    }
    public boolean isOutside(){
        return (State.Outside == this.a.status);
    }
    public boolean isBoundary(){
        if (null == this.membership)
            return this.is(State.Boundary);
        else
            return true;
    }
    public boolean isNotBoundary(){
        if (null != this.membership)
            return false;
        else
            return this.isnot(State.Boundary);
    }
    public boolean alive(){
        return this.alive;
    }
    public void destroy(Solid s){
        this.alive = false;
        this.membership = null;

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
                final float[] na = n.array();

                final float x = na[X];
                final float y = na[Y];
                final float z = na[Z];

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
    public Face.Intersection intersect(Vertex p){

        return new Face.Intersection(this,p);
    }
    public float distance(Vertex that){

        final float[] n = this.getNxyzd();

        final float nx = n[0];
        final float ny = n[1];
        final float nz = n[2];
        final float fd = n[3];

        return (nx*that.x + ny*that.y + nz*that.z + fd);
    }
    public int sdistance(Vertex that){

        final float d;
        {
            final float[] n = this.getNxyzd();

            final float nx = n[0];
            final float ny = n[1];
            final float nz = n[2];
            final float fd = n[3];

            d = (nx*that.x + ny*that.y + nz*that.z + fd);
        }

        if (d > EPS)
            return 1;
        else if (d < -EPS)
            return -1;
        else
            return 0;
    }
    public State sclass(Vertex that){
        return State.Classify(this.sdistance(that));
    }
    public boolean isMemberOf(Segment segment){
        return (-1 != Segment.IndexOf(this.membership,segment));
    }
    public boolean isNotMemberOf(Segment segment){
        return (-1 == Segment.IndexOf(this.membership,segment));
    }
    public Face memberOf(Segment segment){

        if (null != segment && this.isNotMemberOf(segment)){

            this.membership = Segment.Add(this.membership,segment);
            /*
             * Maintain path order at the segment path source.
             * See Segment.Path
             */
            if (1 < this.membership.length)
                java.util.Arrays.sort(this.membership);
        }
        return this;
    }
    public int countMembership(){
        final Segment[] m = this.membership;
        if (null == m)
            return 0;
        else 
            return m.length;
    }
    public int indexOfMember(Segment item){
        return Segment.IndexOf(this.membership,item);
    }
    public Segment getMember(int idx){

        return this.membership[idx];
    }
    public boolean hasMembership(){
        return (null != this.membership);
    }
    public boolean hasNotMembership(){
        return (null == this.membership);
    }
    public Segment[] segments(){
        return this.membership;
    }
    public fv3.Model.Element[] debugger(){
        Vector n = this.getNormal();

        fv3.font.Font font = fv3.font.Font.For("futural");

        float rX = (this.getBoundsMaxX()-this.getBoundsMinX())/3.0f;
        float rY = (this.getBoundsMaxY()-this.getBoundsMinY())/3.0f;
        float rZ = (this.getBoundsMaxZ()-this.getBoundsMinZ())/3.0f;


        fv3.Model.Element[] model = new fv3.Model.Element[]{

            new fv3.model.Normal(n.x(),n.y(),n.z()),
            new fv3.model.Vertex(this.a.x,this.a.y,this.a.z),
            new fv3.model.Vertex(this.b.x,this.b.y,this.b.z),
            new fv3.model.Vertex(this.c.x,this.c.y,this.c.z),

            font.clone('A').fit2(this.a.x,this.a.y,this.a.z,rX,rY,rZ),
            font.clone('B').fit2(this.b.x,this.b.y,this.b.z,rX,rY,rZ),
            font.clone('C').fit2(this.c.x,this.c.y,this.c.z,rX,rY,rZ)
        };
        rX /= 2.0f;
        rY /= 2.0f;
        rZ /= 2.0f;
        for (Segment s: this.membership){
            model = fv3.model.Object.Add(model,s.debugger(rX,rY,rZ));
        }
        return model;
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
        }
    }
    /**
     */
    public float[] getNxyzd(){

        float[] n = this.normal();

        return new float[]{n[X],n[Y],n[Z],this.d};
    }
    /**
     * Vertex classification propagation called from triangulation
     */
    public Face classify(Vertex m){

        return this.classify(m,m.status,true);
    }
    /**
     * Vertex classification propagation
     */
    public Face classify(Vertex from, State vs, boolean fwd){

        if (this.isNotBoundary()){

            if (from != this.a)
                this.a.classify(vs,false);

            if (from != this.b)
                this.b.classify(vs,false);

            if (from != this.c)
                this.c.classify(vs,false);
        }
        else if (fwd){

            if (State.Boundary != this.a.status){

                if (State.Boundary != this.b.status){

                    if (from != this.a)
                        this.a.classify(vs,false);

                    if (from != this.b)
                        this.b.classify(vs,false);
                }
                else
                    this.a.status = vs;
            }
            else if (State.Boundary != this.b.status){

                if (State.Boundary != this.c.status){

                    if (from != this.b)
                        this.b.classify(vs,false);

                    if (from != this.c)
                        this.c.classify(vs,false);
                }
                else
                    this.b.status = vs;
            }
            else if (State.Boundary != this.c.status)

                this.c.status = vs;
        }
        return this;
    }
    /**
     * @see AH
     */
    public void triangulate(Solid s){

        Segment sg = this.membership[0];

        sg.classify(this);

        Face[] replacements = null;

        switch (sg.triangulateKind(this)){
        case M:
            replacements = Triangulate.M(this,s);
            break;
        case VE:
            replacements = Triangulate.V(this,s,sg.endpoint1(this),sg.endpoint2(this));
            break;
        case EV:
            replacements = Triangulate.V(this,s,sg.endpoint2(this),sg.endpoint1(this));
            break;
        case EE:
            replacements = Triangulate.E(this,s,sg.endpoint1(this),sg.endpoint2(this));
            break;
        default:
            throw new IllegalStateException();
        }

        s.replace(this,replacements);
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
        string.append(this.a);
        string.append(' ');
        string.append(this.b);
        string.append(' ');
        string.append(this.c);

        return string.toString();
    }
    public Vertex.Iterator iterator(){

        return new Vertex.Iterator(this.a,this.b,this.c);
    }

    /*
     */

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
