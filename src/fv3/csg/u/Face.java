/*
 * Fv3 CSG
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

/**
 * Triangular face used by {@link fv3.csg.Solid}.
 */
public final class Face
    extends java.lang.Object
    implements fv3.csg.u.Notation,
               java.lang.Comparable<Face>,
               java.lang.Cloneable
{
    public enum LP {
        Up, Down, On, None;

        public final static boolean Up(LP... args){
            for (LP lp: args){
                if (LP.Up == lp)
                    return true;
            }
            return false;
        }
        public final static boolean Down(LP... args){
            for (LP lp: args){
                if (LP.Down == lp)
                    return true;
            }
            return false;
        }
        public final static boolean On(LP... args){
            for (LP lp: args){
                if (LP.On == lp)
                    return true;
            }
            return false;
        }
        public final static LP X(Vector p, Vector pla, Vector plb){

            final double p_y = p.y();
            final double p_z = p.z();
            final double pla_y = pla.y();
            final double pla_z = pla.z();
            final double plb_y = plb.y();
            final double plb_z = plb.z();

            if ((EPS < Math.abs(pla_y - plb_y)) &&
                (((p_y >= pla_y) && (p_y <= plb_y)) ||
                 ((p_y <= pla_y) && (p_y >= plb_y))))
                {
                    double a = (plb_z - pla_z) / (plb_y - pla_y);
                    double b = pla_z - a*pla_y;
                    double z = a*p_y + b;

                    if (z > p_z+EPS)

                        return LP.Up;

                    else if (z < p_z-EPS)

                        return LP.Down;
                    else
                        return LP.On;
                }
            else
                return LP.None;

        }
        public final static LP Y(Vector p, Vector pla, Vector plb){

            final double p_x = p.x();
            final double p_z = p.z();
            final double pla_x = pla.x();
            final double pla_z = pla.z();
            final double plb_x = plb.x();
            final double plb_z = plb.z();

            if ((EPS < Math.abs(pla_x - plb_x)) &&
                (((p_x >= pla_x) && (p_x <= plb_x)) ||
                 ((p_x <= pla_x) && (p_x >= plb_x))))
                {
                    double a = (plb_z - pla_z) / (plb_x - pla_x);
                    double b = pla_z - a*pla_x;
                    double z = a*p_x + b;

                    if (z > p_z+EPS)

                        return LP.Up;			

                    else if (z < p_z-EPS)

                        return LP.Down;
                    else
                        return LP.On;
                }
            else
                return LP.None;
        }
        public final static LP Z(Vector p, Vector pla, Vector plb){

            final double p_x = p.x();
            final double p_y = p.y();
            final double pla_x = pla.x();
            final double pla_y = pla.y();
            final double plb_x = plb.x();
            final double plb_y = plb.y();

            if ((EPS < Math.abs(pla_x - plb_x)) &&
                (((p_x >= pla_x) && (p_x <= plb_x)) ||
                 ((p_x <= pla_x) && (p_x >= plb_x))))
                {
                    double a = (plb_y - pla_y) / (plb_x - pla_x);
                    double b = pla_y - a*pla_x;
                    double y = a*p_x + b;

                    if (y > p_y+EPS)

                        return LP.Up;			

                    else if (y < p_y-EPS)

                        return LP.Down;
                    else
                        return LP.On;
                }
            else
                return LP.None;

        }
    }

    /**
     * The only valid change to a face is one that is subsequently
     * reversed (i.e. invert normal).  All other changes must replace
     * the face in the state of the solid.
     */
    public Vertex a, b, c;

    public State.Face status = State.Face.Unknown;

    private Vector normal, centroid;

    private Bound bound;

    private boolean inverted;


    public Face(Solid s, Vertex a, Vertex b, Vertex c){
        super();
        if (null != s && null != a && null != b && null != c){
            this.a = s.u(a).memberOf(this);
            this.b = s.u(b).memberOf(this);
            this.c = s.u(c).memberOf(this);
        }
        else
            throw new IllegalArgumentException();
    }
    public Face(Solid s, Vertex a, Vertex b, Vertex c, Vector n){
        super();
        if (null != s && null != a && null != b && null != c && null != n){

            Vector check = a.getVector().normal(b.getVector(),c.getVector());
            Vector.Direction1 checkD = check.direction1();
            Vector.Direction1 nD = n.direction1();
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
        }
        else
            throw new IllegalArgumentException();
    }


    public void init(){
        this.status = State.Face.Unknown;
        if (this.inverted){
            this.inverted = false;
            this.invertNormal();
            this.inverted = false;
        }
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
    public void destroy(){
        this.a.destroy();
        this.b.destroy();
        this.c.destroy();
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
            clone.status = State.Face.Unknown;
            clone.a = s.u(clone.a).memberOf(clone);
            clone.b = s.u(clone.b).memberOf(clone);
            clone.c = s.u(clone.c).memberOf(clone);
            return clone;
        }
        catch (CloneNotSupportedException exc){
            throw new InternalError();
        }
    }
    public Face dropFrom(Solid s){
        try {
            s.state.remove(this);
        }
        catch (java.util.NoSuchElementException ignore){
        }
        if (this.a.dropMember(this))
            s.state.vertices.remove(this.a);

        if (this.b.dropMember(this))
            s.state.vertices.remove(this.b);

        if (this.c.dropMember(this))
            s.state.vertices.remove(this.c);

        return this;
    }
    public Face deconstruct(Solid s){

        if (this.a.dropMember(this))
            s.state.vertices.remove(this.a);

        if (this.b.dropMember(this))
            s.state.vertices.remove(this.b);

        if (this.c.dropMember(this))
            s.state.vertices.remove(this.c);

        return this;
    }
	public Vector getNormal(){

        Vector n = this.normal;

        if (null == n){

            n = this.a.getVector().normal(this.b.getVector(),this.c.getVector());
		
            this.normal = n;
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
    public String toString(){
        return this.toString("","\n");
    }
    public String toString(String pr){
        return this.toString(pr,"\n");
    }
    public String toString(String pr, String in){
        if (null == pr)
            pr = "";
        if (null == in)
            in = " ";

        StringBuilder string = new StringBuilder();

        string.append(pr);
        string.append(this.status);
        string.append(in);
        string.append(pr);
        string.append(this.a);
        string.append(in);
        string.append(pr);
        string.append(this.b);
        string.append(in);
        string.append(pr);
        string.append(this.c);
        string.append(in);
        string.append(pr);
        string.append(this.getNormal());

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
            this.normal = null;
        }
    }
    public double[] getNxyzd(){

        Vertex a = this.a;

        Vector n = this.getNormal();

		double x = n.getX();
		double y = n.getY();
		double z = n.getZ();

		double d = -(x*a.x + x*a.y + z*a.z);

        return new double[]{x,y,z,d};
    }
    public boolean simpleClassify(){
        if (this.a.isUnknown() && this.b.isUnknown() && this.c.isUnknown())
            return false;
        else {
            State.Vertex t = this.a.con(State.Vertex.Inside,State.Vertex.Outside);
            if (null != t){
                if (State.Vertex.Inside == t)
                    this.status = State.Face.Inside;
                else
                    this.status = State.Face.Outside;
                return true;
            }
            else {
                t = this.b.con(State.Vertex.Inside,State.Vertex.Outside);
                if (null != t){
                    if (State.Vertex.Inside == t)
                        this.status = State.Face.Inside;
                    else
                        this.status = State.Face.Outside;
                    return true;
                }
                else {
                    t = this.c.con(State.Vertex.Inside,State.Vertex.Outside);
                    if (null != t){
                        if (State.Vertex.Inside == t)
                            this.status = State.Face.Inside;
                        else
                            this.status = State.Face.Outside;
                        return true;
                    }
                    else
                        return false;
                }
            }
        }
    }
    public void rayTraceClassify(Solid object){

		Vector p0 = new Vector();
		p0.x((this.a.x + this.b.x + this.c.x)/3.0);
		p0.y((this.a.y + this.b.y + this.c.y)/3.0);
		p0.z((this.a.z + this.b.z + this.c.z)/3.0);
		Segment.Line ray = new Segment.Line(getNormal(),p0);

		Face closestFace = null;
        double closestDistance;
        {
            double dotProduct, distance; 
            Vector intersectionPoint;

            scan:do {

                closestDistance = Double.MAX_VALUE;

                for (Face face: object){

                    dotProduct = face.getNormal().dot(ray.getDirection());

                    intersectionPoint = ray.computePlaneIntersection(face.getNormal(), face.a.getPosition());
								
                    if (null != intersectionPoint){

                        distance = ray.computePointToPointDistance(intersectionPoint);
					
                        if (EPS > Math.abs(distance)){

                            if (EPS > Math.abs(dotProduct)){

                                ray.perturbDirection();

                                continue scan;
                            }
                            else {

                                if (face.hasPoint(intersectionPoint)){

                                    closestFace = face;
                                    closestDistance = 0;

                                    break scan;
                                }
                            }
                        }
                        else if (EPS < Math.abs(dotProduct) && EPS < distance){

                            if (distance < closestDistance){

                                if (face.hasPoint(intersectionPoint)){

                                    closestDistance = distance;
                                    closestFace = face;
                                }
                            }
                        }
                    }
                }
                break scan;
            }
            while (true);
		}

		if (null == closestFace)

			this.status = State.Face.Outside;

		else {

			double dotProduct = closestFace.getNormal().dot(ray.getDirection());
			
			if (EPS > Math.abs(closestDistance)){

				if (EPS < dotProduct)

					this.status = State.Face.Same;

				else if (-EPS > dotProduct)

					this.status = State.Face.Opposite;
			}
			else if (EPS < dotProduct)

				this.status = State.Face.Inside;

			else if (-EPS > dotProduct)

				this.status = State.Face.Outside;
		}
	}
    private boolean hasPoint( Vector p){
        Vector n = this.getNormal();

        Vector a = this.a.getPosition();
        Vector b = this.b.getPosition();
        Vector c = this.c.getPosition();

        LP lpab, lpbc, lpca;

        if (EPS < Math.abs(n.x())){

			lpab = LP.X(p, a, b);
			lpbc = LP.X(p, b, c);
			lpca = LP.X(p, c, a);
        }
        else if (EPS < Math.abs(n.y())){

			lpab = LP.Y(p, a, b);
			lpbc = LP.Y(p, b, c);
			lpca = LP.Y(p, c, a);
        }
        else {

			lpab = LP.Z(p, a, b);
			lpbc = LP.Z(p, b, c);
			lpca = LP.Z(p, c, a);
        }
	
		return ((LP.Up(lpab,lpbc,lpca) && LP.Down(lpab,lpbc,lpca)) ||
                (LP.On(lpab,lpbc,lpca)));
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
}
