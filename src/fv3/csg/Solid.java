/*
 * Fv3 CSG
 * Copyright (C) 2010  Danilo Balby Silva Castanheira (danbalby@yahoo.com)
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
package fv3.csg;

import fv3.csg.u.Bound;
import fv3.csg.u.Face;
import fv3.csg.u.Segment;
import fv3.csg.u.Vertex;
import fv3.math.Matrix;
import fv3.math.Vector;
import fv3.math.VertexArray;

import lxl.List;
import lxl.Map;

import javax.media.opengl.GL2;

/**
 * A solid encloses a finite volume.  No two faces may intersect
 * (overlap).
 * 
 * Based on the work of Danilo Balby Silva Castanheira in <a
 * href="http://unbboolean.sf.net/">J3DBool</a> following <a
 * href="http://www.cs.brown.edu/~jfh/papers/Laidlaw-CSG-1986/main.htm">"Constructive
 * Solid Geometry for Polyhedral Objects"</a>.
 * 
 * This is a mesh data structure: vertices are each unique.  The mesh
 * sub class data structure is "compiled" into the super class vertex
 * array for rendering.
 * 
 * Vertices are loaded, construction operations may be performed, and
 * then the vertex array is compiled for rendering.
 */
public class Solid
    extends fv3.math.VertexArray
    implements fv3.csg.u.Notation,
               java.lang.Iterable<Face>,
               fv3.Bounds
{
    public enum Construct {
        Union, Intersection, Difference;
    }

    public State state;

    public Construct constructOp;

    public Solid constructA, constructB;

    /**
     * @param countVertices Estimated or expected number of vertices
     */
    public Solid(int countVertices){
        super(Type.Triangles,countVertices);

        this.state = new State(countVertices);

        this.visible = false;
    }
    public Solid(VertexArray array){
        super(Type.Triangles,array);

        this.state = new State(this.countVertices);

        for (int face = 0, count = this.countFaces; face < count; face++){

            this.add(this.getFace(face));
        }
    }


    /**
     * Add a new triangular face in three vertices.  Face vertices are
     * ordered clock wise looking at the "back" of the face, and
     * counter clock wise looking at the "front" of the face.
     */
    public final Solid add(Vertex a, Vertex b, Vertex c){
        return this.addN(new Face(this,a,b,c));
    }
    /**
     * Add a new triangular face in three (X,Y,Z) vertices and an
     * approximate normal.
     */
    public final Solid add(Vertex a, Vertex b, Vertex c, Vector n){
        return this.addN(new Face(this,a,b,c,n));
    }
    /**
     * Add a new triangular face in three (X,Y,Z) vertices
     */
    public final Solid add(double[] a, double[] b, double[] c){
        return this.addN(new Face(this,new Vertex(a),new Vertex(b),new Vertex(c)));
    }
    /**
     * Add a new triangular face in three (X,Y,Z) vertices and an
     * approximate normal.
     */
    public final Solid add(double[] a, double[] b, double[] c, double[] n){
        return this.addN(new Face(this,new Vertex(a),new Vertex(b),new Vertex(c),new Vector(n)));
    }
    /**
     * Add a new triangular face from three sets of (X,Y,Z) vertices
     */
    public final Solid add(double[] face){
        return this.addN(new Face(this,new Vertex(face,0),new Vertex(face,3),new Vertex(face,6)));
    }
    /**
     * Add a new triangular face from three sets of (X,Y,Z) vertices
     * and an approximate normal.
     */
    public final Solid add(double[] face, double[] normal){
        return this.addN(new Face(this,new Vertex(face,0),new Vertex(face,3),new Vertex(face,6),new Vector(normal)));
    }
    /**
     * Add a new triangular face in three (X,Y,Z) vertices
     */
    public final Solid add(double ax, double ay, double az, 
                           double bx, double by, double bz, 
                           double cx, double cy, double cz)
    {
        return this.addN(new Face(this,new Vertex(ax,ay,az),new Vertex(bx,by,bz),new Vertex(cx,cy,cz)));
    }
    /**
     * Add a new triangular face in three (X,Y,Z) vertices and an
     * approximate normal.
     */
    public final Solid add(double ax, double ay, double az, 
                           double bx, double by, double bz, 
                           double cx, double cy, double cz,
                           double nx, double ny, double nz)
    {
        return this.addN(new Face(this,new Vertex(ax,ay,az),new Vertex(bx,by,bz),new Vertex(cx,cy,cz),new Vector(nx,ny,nz)));
    }
    /**
     * Add a new triangular face in three (X,Y,Z) vertices and an
     * approximate normal.
     */
    public final Solid add(double ax, double ay, double az, 
                           double bx, double by, double bz, 
                           double cx, double cy, double cz,
                           Vector n)
    {
        return this.addN(new Face(this,new Vertex(ax,ay,az),new Vertex(bx,by,bz),new Vertex(cx,cy,cz),n));
    }
    public final Solid add(VertexArray array){

        double[] triangles = array.vertices(VertexArray.Type.Triangles);

        for (int index = 0, count = triangles.length; index < count; ){

            double x0 = triangles[index++];
            double y0 = triangles[index++];
            double z0 = triangles[index++];

            double x1 = triangles[index++];
            double y1 = triangles[index++];
            double z1 = triangles[index++];

            double x2 = triangles[index++];
            double y2 = triangles[index++];
            double z2 = triangles[index++];

            this.add(new Vertex(x0,y0,z0),new Vertex(x1,y1,z1),new Vertex(x2,y2,z2));
        }
        return this;
    }
    /**
     * Construct a new solid as the union of "this" and "that".  This
     * union is the (minimal) sum of this and that.
     */
    public final Solid union(Solid that){
        this.init(that);
        try {
            return this.compose(Construct.Union,that);
        }
        finally {
            this.reinit(that);
        }
    }
    /**
     * Construct a new solid as the intersection of "this" and "that".
     * The intersection is the remainder of this minus that.
     */
    public final Solid intersection(Solid that){
        this.init(that);
        try {
            return this.compose(Construct.Intersection,that);
        }
        finally {
            this.reinit(that);
        }
    }
    /**
     * Construct a new solid as the difference of "this" and "that".
     * The difference is this minus that.
     */
    public final Solid difference(Solid that){
        this.init(that);
        try {
            that.invertInsideFaces();
            return this.compose(Construct.Difference,that);
        }
        finally {
            this.reinit(that);
        }
    }
    public final Solid transform(Matrix m){

        if (this.visible)
            super.transform(m);
        else {

            for (Face face: this.state){

                face.transform(this,m);
            }
        }
        return this;
    }
    /**
     * Update the superclass vertex array with the state of the faces
     * in this instance, as for rendering.  
     * 
     * This is not necessary when the solid was constructed from a
     * vertex array and has not changed since construction.
     * 
     * Otherwise this step is necessary to rendering this shape.
     */
    public final Solid compile(){

        super.countVertices(this.state.countVertices());

        int nc = 0, vc = 0;

        for (Face face: this.state){

            this.setVertices(vc, face.vertices(), 0, 3);
            vc += 3;

            double[] n = face.normal();

            this.setNormal(nc++, n);
            this.setNormal(nc++, n);
            this.setNormal(nc++, n);
        }
        this.visible = true;

        return this;
    }
    public final Solid compile(Matrix m){
        if (null != m){
            super.countVertices(this.state.countVertices());

            int nc = 0, vc = 0;

            for (Face face: this.state){

                Vector a = m.transform(face.a.getVector());
                Vector b = m.transform(face.b.getVector());
                Vector c = m.transform(face.c.getVector());
                Vector n = a.normal(b,c);

                this.setVertex(vc++, a);
                this.setVertex(vc++, b);
                this.setVertex(vc++, c);

                this.setNormal(nc++, n);
                this.setNormal(nc++, n);
                this.setNormal(nc++, n);
            }

            this.visible = true;

            return this;
        }
        else
            return this.compile();
    }
    public final Bound getBound(){
        return this.state.getBound();
    }
    public final double getBoundsMinX(){
        return this.getBound().getBoundsMinX();
    }
    public final double getBoundsMidX(){
        return this.getBound().getBoundsMidX();
    }
    public final double getBoundsMaxX(){
        return this.getBound().getBoundsMaxX();
    }
    public final double getBoundsMinY(){
        return this.getBound().getBoundsMinY();
    }
    public final double getBoundsMidY(){
        return this.getBound().getBoundsMidY();
    }
    public final double getBoundsMaxY(){
        return this.getBound().getBoundsMaxY();
    }
    public final double getBoundsMinZ(){
        return this.getBound().getBoundsMinZ();
    }
    public final double getBoundsMidZ(){
        return this.getBound().getBoundsMidZ();
    }
    public final double getBoundsMaxZ(){
        return this.getBound().getBoundsMaxZ();
    }
    public final int countVertices(){
        return this.state.countVertices();
    }
    public void destroy(){

        this.state.destroy();
    }
    public final java.util.Iterator<Face> iterator(){
        return this.state.iterator();
    }
    /**
     * Initial build "add" is overridden by {@link Geom} for sorting
     * face vertex order in three space.
     */
    protected Solid addN(Face face){

        this.state.add(face);
        return this;
    }
    /**
     * Construction "add" is literal, unlike "builder add" which may
     * have builder convenience semantics.
     */
    protected Solid addC0(Construct op, Face face){

        this.state.add(face.clone(this));
        return this;
    }
    protected Solid addC1(Construct op, Face face){

        this.state.add(face.clone(this));
        return this;
    }
    public final Vertex u(Vertex a){
        Vertex b = this.state.vertices.get(a);
        if (null == b){
            this.state.vertices.put(a,a);
            return a;
        }
        else
            return b;
    }
    private void init(Solid that){
        this.push();
        that.push();
		this.splitFaces(that);
		that.splitFaces(this);
		this.classifyFaces(that);
		that.classifyFaces(this);
    }
    private void reinit(Solid that){
        this.pop();
        that.pop();
        for (Face thisFace: this){
            thisFace.init();
        }
        for (Face thatFace: that){
            thatFace.init();
        }
    }
    private void push(){
        this.state = this.state.push();
    }
    private void pop(){
        this.state = this.state.pop();
    }
    private Solid compose(Construct op, Solid that){
        Solid re = new Solid(this.countVertices());
        State.Face a, b, c;
        switch (op){
        case Union:
            a = State.Face.Outside;
            b = State.Face.Same;
            c = State.Face.Outside;
            break;
        case Intersection:
            a = State.Face.Inside;
            b = State.Face.Same;
            c = State.Face.Inside;
            break;
        case Difference:
            a = State.Face.Outside;
            b = State.Face.Opposite;
            c = State.Face.Inside;
            break;
        default:
            throw new IllegalStateException();
        }

        for (Face thisFace: this){

            if (thisFace.is(a)
                || thisFace.is(b))
            {
                re.addC0(op,thisFace);
            }
        }

        for (Face thatFace: that){

            if (thatFace.is(c)){

                re.addC1(op,thatFace);
            }
        }

        re.constructOp = op;
        re.constructA = this;
        re.constructB = that;
        return re;
    }
    private void classifyFaces(Solid that){

        for (Face face: this.state){

            if (!face.simpleClassify()){

                face.rayTraceClassify(that);

				if (State.Vertex.Unknown == face.a.status) 

					face.a.status = State.Face.ToVertex(face.status);

				if (State.Vertex.Unknown== face.b.status) 

					face.b.status = State.Face.ToVertex(face.status);

				if (State.Vertex.Unknown == face.c.status) 

					face.c.status = State.Face.ToVertex(face.status);
            }
        }
    }
    private void invertInsideFaces(){
        for (Face face: this.state){
            if (face.isInside())
                face.invertNormal();
        }
    }
    private void splitFaces(Solid that){

        Bound thatBound = that.getBound();

        if (this.getBound().overlap(thatBound)){

            for (Face thisFace: this.state){

                if (thisFace.getBound().overlap(thatBound)){

                    for (Face thatFace: that.state){

                        if (thisFace.getBound().overlap(thatFace.getBound())){

                            double DthisA = thisFace.a.distance(thatFace);
                            double DthisB = thisFace.b.distance(thatFace);
                            double DthisC = thisFace.c.distance(thatFace);

							int SthisA = (DthisA > EPS ? 1 :(DthisA < -EPS ? -1 : 0)); 
							int SthisB = (DthisB > EPS ? 1 :(DthisB < -EPS ? -1 : 0));
							int SthisC = (DthisC > EPS ? 1 :(DthisC < -EPS ? -1 : 0));

                            if (!(SthisA == SthisB && SthisB == SthisC)){

                                double DthatA = thatFace.a.distance(thisFace);
                                double DthatB = thatFace.b.distance(thisFace);
                                double DthatC = thatFace.c.distance(thisFace);

                                int SthatA = (DthatA > EPS ? 1 :(DthatA < -EPS ? -1 : 0)); 
                                int SthatB = (DthatB > EPS ? 1 :(DthatB < -EPS ? -1 : 0));
                                int SthatC = (DthatC > EPS ? 1 :(DthatC < -EPS ? -1 : 0));

                                if (!(SthatA == SthatB && SthatB == SthatC)){

                                    Segment.Line line = new Segment.Line(thisFace, thatFace);

                                    Segment thisSeg = new Segment(line, thisFace, SthisA, SthisB, SthisC);

                                    Segment thatSeg = new Segment(line, thatFace, SthatA, SthatB, SthatC);

									if (thisSeg.intersect(thatSeg)){

										this.splitFace(thisFace,thisSeg,thatSeg);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private void splitFace(Face thisFace, Segment thisSeg, Segment thatSeg){

		Vertex startVertex = thisSeg.getStartVertex();
		Vertex endVertex = thisSeg.getEndVertex();
        /*
         */
		Vector startPos;
		Segment.Type startType;
		double startDist;
		if (thatSeg.getStartDistance() > thisSeg.getStartDistance()+EPS){
			startDist = thatSeg.getStartDistance();
			startType = thisSeg.getIntermediateType();
			startPos = thatSeg.getStartPosition();
		}
		else {
			startDist = thisSeg.getStartDistance();
			startType = thisSeg.getStartType();
			startPos = thisSeg.getStartPosition();
		}
        /*
         */
		Vector endPos;
		Segment.Type endType;
		double endDist;
		if (thatSeg.getEndDistance() < thisSeg.getEndDistance()-EPS){
			endDist = thatSeg.getEndDistance();
			endType = thisSeg.getIntermediateType();
			endPos = thatSeg.getEndPosition();
		}
		else {
			endDist = thisSeg.getEndDistance();
			endType = thisSeg.getEndType();
			endPos = thisSeg.getEndPosition();
		}
        /*
         */
		Segment.Type middleType = thisSeg.getIntermediateType();
        /*
         */
		if (startType == Segment.Type.Vertex)
			startVertex.status = State.Vertex.Boundary;

		if (endType == Segment.Type.Vertex)
			endVertex.status = State.Vertex.Boundary;
        /*
         */
		if (startType == Segment.Type.Vertex && endType == Segment.Type.Vertex)

			return;

		else if (middleType == Segment.Type.Edge){

			int splitEdge;

			if ((startVertex == thisFace.a && endVertex == thisFace.b)
                || (startVertex == thisFace.b && endVertex == thisFace.a))

				splitEdge = 1;

			else if ((startVertex == thisFace.b && endVertex == thisFace.c)
                     || (startVertex == thisFace.c && endVertex == thisFace.b))

				splitEdge = 2; 
			else
				splitEdge = 3;

			
			if (Segment.Type.Vertex == startType){

				this.splitFaceTwo(thisFace, endPos, splitEdge);
			}
			else if (Segment.Type.Vertex == endType){

				this.splitFaceTwo(thisFace, startPos, splitEdge);
			}
			else if (startDist == endDist){

				this.splitFaceTwo(thisFace, endPos, splitEdge);
            }
			else {

				if ((startVertex == thisFace.a && endVertex == thisFace.b) ||
                    (startVertex == thisFace.b && endVertex == thisFace.c) ||
                    (startVertex == thisFace.c && endVertex == thisFace.a))
				{
					this.splitFaceThree(thisFace, startPos, endPos, splitEdge);
				}
				else
					this.splitFaceThree(thisFace, endPos, startPos, splitEdge);
			}
		}
		else if (Segment.Type.Vertex == startType){

            if (Segment.Type.Edge == endType)

                this.splitFaceTwo(thisFace, endPos, endVertex);

            else if (Segment.Type.Face == endType)

                this.splitFaceThree(thisFace, endPos, startVertex);
        }
		else if (Segment.Type.Edge == startType){

            if ( Segment.Type.Vertex == endType)

                this.splitFaceTwo(thisFace, startPos, startVertex);

            else if (Segment.Type.Edge == endType)

                this.splitFaceThree(thisFace, startPos, endPos, startVertex, endVertex);

            else if (Segment.Type.Face == endType)

                this.splitFaceFour(thisFace, startPos, endPos, startVertex);
        }
		else if (Segment.Type.Face == startType){

            if (Segment.Type.Vertex == endType)

                this.splitFaceThree(thisFace, startPos, endVertex);

            else if (Segment.Type.Edge == endType)

                this.splitFaceFour(thisFace, endPos, startPos, endVertex);

            else if (Segment.Type.Face == endType){

                Vector segmentVector = new Vector(startPos.x()-endPos.x(), 
                                                  startPos.y()-endPos.y(), 
                                                  startPos.z()-endPos.z());

                if (EPS > Math.abs(segmentVector.x()) &&
                    EPS > Math.abs(segmentVector.y()) &&
                    EPS > Math.abs(segmentVector.z()))
                {
                    this.splitFaceThree(thisFace, startPos);
                }
                else {
                    int linedVertex;
                    Vector linedVertexPos;
                    {
                        Vector vertexVector;

                        vertexVector = new Vector(endPos.x()-thisFace.a.x,
                                                  endPos.y()-thisFace.a.y,
                                                  endPos.z()-thisFace.a.z)
                            .normalize();

                        double dot1 = Math.abs(segmentVector.dot(vertexVector));

                        vertexVector = new Vector(endPos.x()-thisFace.b.x, 
                                                  endPos.y()-thisFace.b.y,
                                                  endPos.z()-thisFace.b.z)
                            .normalize();

                        double dot2 = Math.abs(segmentVector.dot(vertexVector));

                        vertexVector = new Vector(endPos.x()-thisFace.c.x,
                                                  endPos.y()-thisFace.c.y,
                                                  endPos.z()-thisFace.c.z)
                            .normalize();

                        double dot3 = Math.abs(segmentVector.dot(vertexVector));

                        if (dot1 > dot2 && dot1 > dot3){
                            linedVertex = 1;
                            linedVertexPos = thisFace.a.getPosition();
                        }
                        else if (dot2 > dot3 && dot2 > dot1){
                            linedVertex = 2;
                            linedVertexPos = thisFace.b.getPosition();
                        }
                        else {
                            linedVertex = 3;
                            linedVertexPos = thisFace.c.getPosition();
                        }
                    }

                    if (linedVertexPos.distance(startPos) > linedVertexPos.distance(endPos))

                        this.splitFaceFive(thisFace, startPos, endPos, linedVertex);
                    else
                        this.splitFaceFive(thisFace, endPos, startPos, linedVertex);
                }
            }
		}
    }
	private void splitFaceTwo(Face thisFace, Vector newPos, int splitEdge){

        thisFace.dropFrom(this);
		
		Vertex vertex = new Vertex(newPos, State.Vertex.Boundary); 

        switch (splitEdge){
        case 1:
			this.add(thisFace.a, vertex, thisFace.c);
			this.add(vertex, thisFace.b, thisFace.c);
            break;
        case 2:
			this.add(thisFace.b, vertex, thisFace.a);
			this.add(vertex, thisFace.c, thisFace.a);
            break;
        case 3:
			this.add(thisFace.c, vertex, thisFace.b);
			this.add(vertex, thisFace.a, thisFace.b);
            break;
        default:
            throw new IllegalArgumentException(String.valueOf(splitEdge));
		}
	}	
	private void splitFaceTwo(Face thisFace, Vector newPos, Vertex endVertex){

        thisFace.dropFrom(this);
		
		Vertex vertex = new Vertex(newPos, State.Vertex.Boundary);
					
		if (endVertex.equals(thisFace.a)){

			this.add(thisFace.a, vertex, thisFace.c);
			this.add(vertex, thisFace.b, thisFace.c);
		}
		else if (endVertex.equals(thisFace.b)){

			this.add(thisFace.b, vertex, thisFace.a);
            this.add(vertex, thisFace.c, thisFace.a);
		}
		else {
			this.add(thisFace.c, vertex, thisFace.b);
			this.add(vertex, thisFace.a, thisFace.b);
		}
	}
	private void splitFaceThree(Face thisFace, Vector newPos1, Vector newPos2, int splitEdge){

        thisFace.dropFrom(this);
		
		Vertex vertex1 = new Vertex(newPos1, State.Vertex.Boundary);
		Vertex vertex2 = new Vertex(newPos2, State.Vertex.Boundary);
				
        switch (splitEdge){
        case 1:
			this.add(thisFace.a, vertex1, thisFace.c);
			this.add(vertex1, vertex2, thisFace.c);
			this.add(vertex2, thisFace.b, thisFace.c);
            break;
        case 2:
			this.add(thisFace.b, vertex1, thisFace.a);
			this.add(vertex1, vertex2, thisFace.a);
			this.add(vertex2, thisFace.c, thisFace.a);
            break;
        case 3:
			this.add(thisFace.c, vertex1, thisFace.b);
			this.add(vertex1, vertex2, thisFace.b);
			this.add(vertex2, thisFace.a, thisFace.b);
            break;
        default:
            throw new IllegalArgumentException(String.valueOf(splitEdge));
		}
	}
	private void splitFaceThree(Face thisFace, Vector newPos, Vertex endVertex){

        thisFace.dropFrom(this);
		
		Vertex vertex = new Vertex(newPos, State.Vertex.Boundary);
						
		if (endVertex.equals(thisFace.a)){

			this.add(thisFace.a, thisFace.b, vertex);
			this.add(thisFace.b, thisFace.c, vertex);
			this.add(thisFace.c, thisFace.a, vertex);
		}
		else if (endVertex.equals(thisFace.b)){

			this.add(thisFace.b, thisFace.c, vertex);
			this.add(thisFace.c, thisFace.a, vertex);
			this.add(thisFace.a, thisFace.b, vertex);
		}
		else {
			this.add(thisFace.c, thisFace.a, vertex);
			this.add(thisFace.a, thisFace.b, vertex);
			this.add(thisFace.b, thisFace.c, vertex);
		}
	}
	private void splitFaceThree(Face thisFace, Vector newPos1, Vector newPos2, 
                                Vertex startVertex, Vertex endVertex)
    {
        thisFace.dropFrom(this);
		
		Vertex vertex1 = new Vertex(newPos1, State.Vertex.Boundary);
		Vertex vertex2 = new Vertex(newPos2, State.Vertex.Boundary);

        final boolean StartA = startVertex.equals(thisFace.a);
        final boolean StartB = startVertex.equals(thisFace.b);
        final boolean StartC = startVertex.equals(thisFace.c);
        final boolean EndA = endVertex.equals(thisFace.a);
        final boolean EndB = endVertex.equals(thisFace.b);
        final boolean EndC = endVertex.equals(thisFace.c);
						
		if (StartA && EndB){

			this.add(thisFace.a, vertex1, vertex2);
			this.add(thisFace.a, vertex2, thisFace.c);
			this.add(vertex1, thisFace.b, vertex2);
		}
		else if (StartB && EndA){

			this.add(thisFace.a, vertex2, vertex1);
			this.add(thisFace.a, vertex1, thisFace.c);
			this.add(vertex2, thisFace.b, vertex1);
		}
		else if (StartB && EndC){

			this.add(thisFace.b, vertex1, vertex2);
			this.add(thisFace.b, vertex2, thisFace.a);
			this.add(vertex1, thisFace.c, vertex2);
		}
		else if (StartC && EndB){

			this.add(thisFace.b, vertex2, vertex1);
			this.add(thisFace.b, vertex1, thisFace.a);
			this.add(vertex2, thisFace.c, vertex1);
		}
		else if (StartC && EndA){

			this.add(thisFace.c, vertex1, vertex2);
			this.add(thisFace.c, vertex2, thisFace.b);
			this.add(vertex1, thisFace.a, vertex2);
		}
		else {
			this.add(thisFace.c, vertex2, vertex1);
			this.add(thisFace.c, vertex1, thisFace.b);
			this.add(vertex2, thisFace.a, vertex1);
		}
	}
	private void splitFaceThree(Face thisFace, Vector newPos){

        thisFace.dropFrom(this);
		
		Vertex vertex = new Vertex(newPos, State.Vertex.Boundary);
				
		this.add(thisFace.a, thisFace.b, vertex);
		this.add(thisFace.b, thisFace.c, vertex);
		this.add(thisFace.c, thisFace.a, vertex);
	}
	private void splitFaceFour(Face thisFace, Vector newPos1, Vector newPos2, Vertex endVertex){

        thisFace.dropFrom(this);
		
		Vertex vertex1 = new Vertex(newPos1, State.Vertex.Boundary);
		Vertex vertex2 = new Vertex(newPos2, State.Vertex.Boundary);
		
		if (endVertex.equals(thisFace.a)){

			this.add(thisFace.a, vertex1, vertex2);
			this.add(vertex1, thisFace.b, vertex2);
			this.add(thisFace.b, thisFace.c, vertex2);
			this.add(thisFace.c, thisFace.a, vertex2);
		}
		else if (endVertex.equals(thisFace.b)){

			this.add(thisFace.b, vertex1, vertex2);
			this.add(vertex1, thisFace.c, vertex2);
			this.add(thisFace.c, thisFace.a, vertex2);
			this.add(thisFace.a, thisFace.b, vertex2);
		}
		else {
			this.add(thisFace.c, vertex1, vertex2);
			this.add(vertex1, thisFace.a, vertex2);
			this.add(thisFace.a, thisFace.b, vertex2);
			this.add(thisFace.b, thisFace.c, vertex2);
		}
	}	
	private void splitFaceFive(Face thisFace, Vector newPos1, Vector newPos2, int linedVertex){

        thisFace.dropFrom(this);
		
		Vertex vertex1 = new Vertex(newPos1, State.Vertex.Boundary);
		Vertex vertex2 = new Vertex(newPos2, State.Vertex.Boundary);

		switch (linedVertex){

        case 1:
			this.add(thisFace.b, thisFace.c, vertex1);
			this.add(thisFace.b, vertex1, vertex2);
			this.add(thisFace.c, vertex2, vertex1);
			this.add(thisFace.b, vertex2, thisFace.a);
			this.add(thisFace.c, thisFace.a, vertex2);
            break;
        case 2:
			this.add(thisFace.c, thisFace.a, vertex1);
			this.add(thisFace.c, vertex1, vertex2);
			this.add(thisFace.a, vertex2, vertex1);
			this.add(thisFace.c, vertex2, thisFace.b);
			this.add(thisFace.a, thisFace.b, vertex2);
            break;
        case 3:
			this.add(thisFace.a, thisFace.b, vertex1);
			this.add(thisFace.a, vertex1, vertex2);
			this.add(thisFace.b, vertex2, vertex1);
			this.add(thisFace.a, vertex2, thisFace.c);
			this.add(thisFace.b, thisFace.c, vertex2);
            break;
        default:
            throw new IllegalStateException(String.valueOf(linedVertex));
		}
	}
}
