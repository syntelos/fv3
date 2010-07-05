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
    implements fv3.csg.Notation,
               java.lang.Iterable<Face>,
               fv3.Bounds
{
    public enum Construct {
        Union, Intersection, Difference;
    }

    protected State state;

    protected Construct constructOp;

    protected Solid constructA, constructB;

    /**
     * @param countVertices Estimated or expected number of vertices
     */
    public Solid(int countVertices){
        super(Type.Triangles,countVertices);/* [TODO] (TriangleStrip)
                                             */
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
    public Solid add(Vertex a, Vertex b, Vertex c){
        this.state.add(new Face(this,a,b,c));
        return this;
    }
    /**
     * Add a new triangular face in three (X,Y,Z) vertices
     */
    public Solid add(double[] a, double[] b, double[] c){
        return this.add(new Vertex(a),new Vertex(b),new Vertex(c));
    }
    /**
     * Add a new triangular face from three sets of (X,Y,Z) vertices
     */
    public Solid add(double[] face){
        return this.add(new Vertex(face,0),new Vertex(face,3),new Vertex(face,6));
    }
    /**
     * Add a new triangular face in three (X,Y,Z) vertices
     */
    public Solid add(double ax, double ay, double az, 
                     double bx, double by, double bz, 
                     double cx, double cy, double cz)
    {
        return this.add(new Vertex(ax,ay,az),new Vertex(bx,by,bz),new Vertex(cx,cy,cz));
    }
    public Solid add(VertexArray array){

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
    public Solid union(Solid that){
        this.init(that);
        try {
            Solid re = this.compose(that,State.Face.Outside,State.Face.Same,State.Face.Outside);
            re.constructOp = Solid.Construct.Union;
            re.constructA = this;
            re.constructB = that;
            return re;
        }
        finally {
            this.reinit(that);
        }
    }
    /**
     * Construct a new solid as the intersection of "this" and "that".
     * The intersection is the remainder of this minus that.
     */
    public Solid intersection(Solid that){
        this.init(that);
        try {
            Solid re = this.compose(that,State.Face.Inside,State.Face.Same,State.Face.Inside);
            re.constructOp = Solid.Construct.Intersection;
            re.constructA = this;
            re.constructB = that;
            return re;
        }
        finally {
            this.reinit(that);
        }
    }
    /**
     * Construct a new solid as the difference of "this" and "that".
     * The difference is this minus that.
     */
    public Solid difference(Solid that){
        this.init(that);
        try {
            that.invertInsideFaces();
            Solid re = this.compose(that,State.Face.Outside,State.Face.Opposite,State.Face.Inside);
            re.constructOp = Solid.Construct.Difference;
            re.constructA = this;
            re.constructB = that;
            return re;
        }
        finally {
            this.reinit(that);
        }
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
    public Solid compile(){
        {
            super.countVertices(this.state.countVertices());

            int fc = 0, vc = 0;

            for (Face face: this.state){    /* [TODO] (TriangleStrip)
                                             */
                this.setVertices(vc, face.vertices(), 0, 3).setNormal(fc, face.normal());

                fc += 1;
                vc += 3;
            }
        }
        this.visible = true;

        return this;
    }
    public Bound getBound(){
        return this.state.getBound();
    }
    public double getBoundsMinX(){
        return this.getBound().getBoundsMinX();
    }
    public double getBoundsMidX(){
        return this.getBound().getBoundsMidX();
    }
    public double getBoundsMaxX(){
        return this.getBound().getBoundsMaxX();
    }
    public double getBoundsMinY(){
        return this.getBound().getBoundsMinY();
    }
    public double getBoundsMidY(){
        return this.getBound().getBoundsMidY();
    }
    public double getBoundsMaxY(){
        return this.getBound().getBoundsMaxY();
    }
    public double getBoundsMinZ(){
        return this.getBound().getBoundsMinZ();
    }
    public double getBoundsMidZ(){
        return this.getBound().getBoundsMidZ();
    }
    public double getBoundsMaxZ(){
        return this.getBound().getBoundsMaxZ();
    }
    public int countVertices(){
        return this.state.countVertices();
    }
    public void destroy(){

        this.state.destroy();
    }
    public java.util.Iterator<Face> iterator(){
        return this.state.iterator();
    }
    protected Solid add(Face other){
        this.state.add(other.clone(this));
        return this;
    }
    protected Vertex u(Vertex a){
        fv3.csg.Vertex b = this.state.vertices.get(a);
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
    private Solid compose(Solid that, State.Face a, State.Face b, State.Face c){
        Solid re = new Solid(this.countVertices());
        for (Face face: this.state){
            if (face.is(a) || face.is(b))
                re.add(face);
        }
        for (Face face: that.state){
            if (face.is(c))
                re.add(face);
        }
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

                            double dThisA = thisFace.a.distance(thatFace);
                            double dThisB = thisFace.b.distance(thatFace);
                            double dThisC = thisFace.c.distance(thatFace);

							int sThisA = (dThisA > EPS ? 1 :(dThisA < -EPS ? -1 : 0)); 
							int sThisB = (dThisB > EPS ? 1 :(dThisB < -EPS ? -1 : 0));
							int sThisC = (dThisC > EPS ? 1 :(dThisC < -EPS ? -1 : 0));

                            if (!(sThisA == sThisB && sThisB == sThisC)){

                                double dThatA = thatFace.a.distance(thisFace);
                                double dThatB = thatFace.b.distance(thisFace);
                                double dThatC = thatFace.c.distance(thisFace);

                                int sThatA = (dThatA > EPS ? 1 :(dThatA < -EPS ? -1 : 0)); 
                                int sThatB = (dThatB > EPS ? 1 :(dThatB < -EPS ? -1 : 0));
                                int sThatC = (dThatC > EPS ? 1 :(dThatC < -EPS ? -1 : 0));

                                if (!(sThatA == sThatB && sThatB == sThatC)){

                                    Line line = new Line(thisFace, thatFace);

                                    Segment thisSeg = new Segment(line, thisFace, sThisA, sThisB, sThisC);

                                    Segment thatSeg = new Segment(line, thatFace, sThatA, sThatB, sThatC);

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

			if ((startVertex == thisFace.a && endVertex == thisFace.b) || (startVertex == thisFace.b && endVertex == thisFace.a))

				splitEdge = 1;

			else if ((startVertex == thisFace.b && endVertex == thisFace.c) || (startVertex == thisFace.c && endVertex == thisFace.b))

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
                        double dot1, dot2, dot3;
                        Vector vertexVector;

                        vertexVector = new Vector(endPos.x()-thisFace.a.x, endPos.y()-thisFace.a.y, endPos.z()-thisFace.a.z)
                            .normalize();

                        dot1 = Math.abs(segmentVector.dot(vertexVector));

                        vertexVector = new Vector(endPos.x()-thisFace.b.x, endPos.y()-thisFace.b.y, endPos.z()-thisFace.b.z)
                            .normalize();

                        dot2 = Math.abs(segmentVector.dot(vertexVector));

                        vertexVector = new Vector(endPos.x()-thisFace.c.x, endPos.y()-thisFace.c.y, endPos.z()-thisFace.c.z)
                            .normalize();

                        dot3 = Math.abs(segmentVector.dot(vertexVector));

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
	private void splitFaceThree(Face thisFace, Vector newPos1, Vector newPos2, Vertex startVertex, Vertex endVertex){

        thisFace.dropFrom(this);
		
		Vertex vertex1 = new Vertex(newPos1, State.Vertex.Boundary);
		Vertex vertex2 = new Vertex(newPos2, State.Vertex.Boundary);
						
		if (startVertex.equals(thisFace.a) && endVertex.equals(thisFace.b)){

			this.add(thisFace.a, vertex1, vertex2);
			this.add(thisFace.a, vertex2, thisFace.c);
			this.add(vertex1, thisFace.b, vertex2);
		}
		else if (startVertex.equals(thisFace.b) && endVertex.equals(thisFace.a)){

			this.add(thisFace.a, vertex2, vertex1);
			this.add(thisFace.a, vertex1, thisFace.c);
			this.add(vertex2, thisFace.b, vertex1);
		}
		else if (startVertex.equals(thisFace.b) && endVertex.equals(thisFace.c)){

			this.add(thisFace.b, vertex1, vertex2);
			this.add(thisFace.b, vertex2, thisFace.a);
			this.add(vertex1, thisFace.c, vertex2);
		}
		else if (startVertex.equals(thisFace.c) && endVertex.equals(thisFace.b)){

			this.add(thisFace.b, vertex2, vertex1);
			this.add(thisFace.b, vertex1, thisFace.a);
			this.add(vertex2, thisFace.c, vertex1);
		}
		else if (startVertex.equals(thisFace.c) && endVertex.equals(thisFace.a)){

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
