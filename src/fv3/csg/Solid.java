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
import fv3.csg.u.Mesh;
import fv3.csg.u.Segment;
import fv3.csg.u.SplitFace;
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
               fv3.csg.u.Name.Named,
               java.lang.Iterable<Face>,
               fv3.Bounds
{
    public enum Construct {
        Union, Intersection, Difference;
    }

    public final static class Name 
        extends fv3.csg.u.Name
    {
        public Name(String desc){
            super(Kind.Solid,desc);
        }
        protected Name(Name n, String desc2){
            super(n,desc2);
        }

        public Name copy(String desc2){
            return new Name(this,desc2);
        }
    }


    public Mesh state;

    public final Name name;

    public Construct constructOp;

    public Solid constructA, constructB;

    /**
     * @param countVertices Estimated or expected number of vertices
     */
    public Solid(String name, int cv){
        super(Type.Triangles,cv);

        this.state = new Mesh(cv);

        this.name = new Name(name);

        this.visible = false;
    }
    public Solid(String name, VertexArray array){
        super(Type.Triangles,array);

        this.state = new Mesh(this.countVertices);

        this.name = new Name(name);

        for (int face = 0, count = this.countFaces; face < count; face++){

            this.add(new Face(this, new Face.Name(this,face,"VA"),
                              this.getFace(face)));
        }
    }
    private Solid(Construct op, Solid a, Solid b){
        this(String.format("%s of (%s) and (%s))",op,a.getName(),b.getName()),a.countVertices());

        this.constructOp = op;
        this.constructA = a;
        this.constructB = b;
    }


    /**
     * Initial build "add" is overridden by {@link Geom} for sorting
     * face vertex order in three space.
     */
    public Solid add(Face face){

        this.state.add(face);
        return this;
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

            this.add(new Face(this, new Face.Name(this,((index/3)-1),"VA"),
                              x0,y0,z0,x1,y1,z1,x2,y2,z2));
        }
        return this;
    }
    public Name getName(){
        return this.name;
    }
    /**
     * Construct a new solid as the union of "this" and "that".  This
     * union is the (minimal) sum of this and that.
     */
    public final Solid union(Solid that){

        return this.compose(Construct.Union,that);
    }
    /**
     * Construct a new solid as the intersection of "this" and "that".
     * The intersection is the remainder of this minus that.
     */
    public final Solid intersection(Solid that){

        return this.compose(Construct.Intersection,that);
    }
    /**
     * Construct a new solid as the difference of "this" and "that".
     * The difference is this minus that.
     */
    public final Solid difference(Solid that){

        return this.compose(Construct.Difference,that);
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
        for (Face thisFace: this){
            thisFace.init();
        }
        for (Face thatFace: that){
            thatFace.init();
        }
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
    }
    private void push(){
        this.state = this.state.push();
    }
    private void pop(){
        this.state = this.state.pop();
    }
    private Solid compose(Construct op, Solid that){
        try {
            this.init(that);

            Solid re = new Solid(op,this,that);

            switch (op){

            case Union:

                for (Face thisFace: this){

                    if (thisFace.is(State.Face.Outside)
                        || thisFace.is(State.Face.Same))
                        {
                            re.addC0(op,thisFace);
                        }
                }

                for (Face thatFace: that){

                    if (thatFace.is(State.Face.Outside)){

                        re.addC1(op,thatFace);
                    }
                }
                return re;

            case Intersection:

                for (Face thisFace: this){

                    if (thisFace.is(State.Face.Inside)
                        || thisFace.is(State.Face.Same))
                        {
                            re.addC0(op,thisFace);
                        }
                }

                for (Face thatFace: that){

                    if (thatFace.is(State.Face.Inside)){

                        re.addC1(op,thatFace);
                    }
                }
                return re;

            case Difference:

                that.invertInsideFaces();

                for (Face thisFace: this){

                    if (thisFace.is(State.Face.Outside)
                        || thisFace.is(State.Face.Opposite))
                        {
                            re.addC0(op,thisFace);
                        }
                }

                for (Face thatFace: that){

                    if (thatFace.is(State.Face.Inside)){

                        re.addC1(op,thatFace);
                    }
                }
                return re;
            default:
                throw new IllegalStateException();
            }
        }
        finally {
            this.reinit(that);
        }
    }
    private void classifyFaces(Solid that){

        for (Face face: this.state){

            if (!face.simpleClassify()){

                face.rayTraceClassify(that);

                face.mark();
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

            scan:
            for (Face thisFace: this){

                if (thisFace.getBound().overlap(thatBound)){

                    for (Face thatFace: that){

                        if (thisFace.getBound().overlap(thatFace.getBound())){

                            final double DthisA = thisFace.a.distance(thatFace);
                            final double DthisB = thisFace.b.distance(thatFace);
                            final double DthisC = thisFace.c.distance(thatFace);

							final int SthisA = (DthisA > EPS ? 1 :(DthisA < -EPS ? -1 : 0)); 
							final int SthisB = (DthisB > EPS ? 1 :(DthisB < -EPS ? -1 : 0));
							final int SthisC = (DthisC > EPS ? 1 :(DthisC < -EPS ? -1 : 0));

                            if (!(SthisA == SthisB && SthisB == SthisC)){

                                final double DthatA = thatFace.a.distance(thisFace);
                                final double DthatB = thatFace.b.distance(thisFace);
                                final double DthatC = thatFace.c.distance(thisFace);

                                final int SthatA = (DthatA > EPS ? 1 :(DthatA < -EPS ? -1 : 0)); 
                                final int SthatB = (DthatB > EPS ? 1 :(DthatB < -EPS ? -1 : 0));
                                final int SthatC = (DthatC > EPS ? 1 :(DthatC < -EPS ? -1 : 0));

                                if (!(SthatA == SthatB && SthatB == SthatC)){

                                    final Segment.Line line = new Segment.Line(thisFace, thatFace);

                                    final Segment thisSeg = new Segment(line, thisFace, 
                                                                        SthisA, SthisB, SthisC);

                                    final Segment thatSeg = new Segment(line, thatFace, 
                                                                        SthatA, SthatB, SthatC);

									if (thisSeg.intersect(thatSeg)){

										SplitFace s = Solid.SplitFace(thisFace,thisSeg,thatSeg);
                                        if (null != s){

                                            int eqv = s.indexOf(thisFace);

                                            if (-1 != eqv){

                                                for (int fc = 0, fz = s.size(); fc < fz; fc++){
                                                    if (eqv != fc)
                                                        this.add(s.create(this,thisFace.name,fc));
                                                }
                                            }
                                            else {
                                                thisFace.dropFrom(this);

                                                for (int fc = 0, fz = s.size(); fc < fz; fc++){

                                                    this.add(s.create(this,thisFace.name,fc));
                                                }
                                            }
                                            continue scan;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private final static SplitFace SplitFace(Face thisFace, Segment thisSeg, Segment thatSeg){

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

			return null;

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

				return SplitFace2I(thisFace, endPos, splitEdge);
			}
			else if (Segment.Type.Vertex == endType){

				return SplitFace2I(thisFace, startPos, splitEdge);
			}
			else if (startDist == endDist){

				return SplitFace2I(thisFace, endPos, splitEdge);
            }
			else {

				if ((startVertex == thisFace.a && endVertex == thisFace.b) ||
                    (startVertex == thisFace.b && endVertex == thisFace.c) ||
                    (startVertex == thisFace.c && endVertex == thisFace.a))
				{
					return SplitFace3I(thisFace, startPos, endPos, splitEdge);
				}
				else
					return SplitFace3I(thisFace, endPos, startPos, splitEdge);
			}
		}
		else if (Segment.Type.Vertex == startType){

            if (Segment.Type.Edge == endType)

                return SplitFace2V(thisFace, endPos, endVertex);

            else if (Segment.Type.Face == endType)

                return SplitFace3V(thisFace, endPos, startVertex);
        }
		else if (Segment.Type.Edge == startType){

            if ( Segment.Type.Vertex == endType)

                return SplitFace2V(thisFace, startPos, startVertex);

            else if (Segment.Type.Edge == endType)

                return SplitFace3L(thisFace, startPos, endPos, startVertex, endVertex);

            else if (Segment.Type.Face == endType)

                return SplitFace4V(thisFace, startPos, endPos, startVertex);
        }
		else if (Segment.Type.Face == startType){

            if (Segment.Type.Vertex == endType)

                return SplitFace3V(thisFace, startPos, endVertex);

            else if (Segment.Type.Edge == endType)

                return SplitFace4V(thisFace, endPos, startPos, endVertex);

            else if (Segment.Type.Face == endType){

                Vector segmentVector = new Vector(startPos.x()-endPos.x(), 
                                                  startPos.y()-endPos.y(), 
                                                  startPos.z()-endPos.z());

                if (EPS > Math.abs(segmentVector.x()) &&
                    EPS > Math.abs(segmentVector.y()) &&
                    EPS > Math.abs(segmentVector.z()))
                {
                    return SplitFace3(thisFace, startPos);
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

                        return SplitFace5I(thisFace, startPos, endPos, linedVertex);
                    else
                        return SplitFace5I(thisFace, endPos, startPos, linedVertex);
                }
            }
		}
        return null;
    }
	private final static SplitFace SplitFace2I(Face thisFace, Vector newPos, int splitEdge){

		Vertex vertex = new Vertex(newPos, State.Vertex.Boundary); 

        switch (splitEdge){
        case 1:
			return new SplitFace("SplitFace2I", thisFace.a, vertex, thisFace.c, vertex, thisFace.b, thisFace.c);
        case 2:
			return new SplitFace("SplitFace2I", thisFace.b, vertex, thisFace.a, vertex, thisFace.c, thisFace.a);
        case 3:
			return new SplitFace("SplitFace2I", thisFace.c, vertex, thisFace.b, vertex, thisFace.a, thisFace.b);
        default:
            throw new IllegalArgumentException(String.valueOf(splitEdge));
		}
	}	
	private final static SplitFace SplitFace2V(Face thisFace, Vector newPos, Vertex endVertex){
		
		Vertex vertex = new Vertex(newPos, State.Vertex.Boundary);
					
		if (endVertex.equals(thisFace.a))

			return new SplitFace("SplitFace2V", thisFace.a, vertex, thisFace.c, vertex, thisFace.b, thisFace.c);

		else if (endVertex.equals(thisFace.b))

			return new SplitFace("SplitFace2V", thisFace.b, vertex, thisFace.a, vertex, thisFace.c, thisFace.a);
		else 
			return new SplitFace("SplitFace2V", thisFace.c, vertex, thisFace.b, vertex, thisFace.a, thisFace.b);
	}
	private final static SplitFace SplitFace3I(Face thisFace, Vector newPos1, Vector newPos2, int splitEdge){

		Vertex vertex1 = new Vertex(newPos1, State.Vertex.Boundary);
		Vertex vertex2 = new Vertex(newPos2, State.Vertex.Boundary);
				
        switch (splitEdge){
        case 1:
			return new SplitFace("SplitFace3I", thisFace.a, vertex1, thisFace.c, vertex1, vertex2, thisFace.c, vertex2, thisFace.b, thisFace.c);
        case 2:
            return new SplitFace("SplitFace3I", thisFace.b, vertex1, thisFace.a, vertex1, vertex2, thisFace.a, vertex2, thisFace.c, thisFace.a);
        case 3:
            return new SplitFace("SplitFace3I", thisFace.c, vertex1, thisFace.b, vertex1, vertex2, thisFace.b, vertex2, thisFace.a, thisFace.b);
        default:
            throw new IllegalArgumentException(String.valueOf(splitEdge));
		}
	}
	private final static SplitFace SplitFace3V(Face thisFace, Vector newPos, Vertex endVertex){

		Vertex vertex = new Vertex(newPos, State.Vertex.Boundary);
						
		if (endVertex.equals(thisFace.a))

			return new SplitFace("SplitFace3V", thisFace.a, thisFace.b, vertex, thisFace.b, thisFace.c, vertex, thisFace.c, thisFace.a, vertex);

		else if (endVertex.equals(thisFace.b))

			return new SplitFace("SplitFace3V", thisFace.b, thisFace.c, vertex, thisFace.c, thisFace.a, vertex, thisFace.a, thisFace.b, vertex);
		else 
			return new SplitFace("SplitFace3V", thisFace.c, thisFace.a, vertex, thisFace.a, thisFace.b, vertex, thisFace.b, thisFace.c, vertex);
	}
	private final static SplitFace SplitFace3L(Face thisFace, Vector newPos1, Vector newPos2, 
                             Vertex startVertex, Vertex endVertex)
    {
		
		Vertex vertex1 = new Vertex(newPos1, State.Vertex.Boundary);
		Vertex vertex2 = new Vertex(newPos2, State.Vertex.Boundary);

        final boolean StartA = startVertex.equals(thisFace.a);
        final boolean StartB = startVertex.equals(thisFace.b);
        final boolean StartC = startVertex.equals(thisFace.c);
        final boolean EndA = endVertex.equals(thisFace.a);
        final boolean EndB = endVertex.equals(thisFace.b);
        final boolean EndC = endVertex.equals(thisFace.c);
						
		if (StartA && EndB)

			return new SplitFace("SplitFace3L", thisFace.a, vertex1, vertex2, thisFace.a, vertex2, thisFace.c, vertex1, thisFace.b, vertex2);

		else if (StartB && EndA)

			return new SplitFace("SplitFace3L", thisFace.a, vertex2, vertex1, thisFace.a, vertex1, thisFace.c, vertex2, thisFace.b, vertex1);

		else if (StartB && EndC)

			return new SplitFace("SplitFace3L", thisFace.b, vertex1, vertex2, thisFace.b, vertex2, thisFace.a, vertex1, thisFace.c, vertex2);

		else if (StartC && EndB)

			return new SplitFace("SplitFace3L", thisFace.b, vertex2, vertex1, thisFace.b, vertex1, thisFace.a, vertex2, thisFace.c, vertex1);

		else if (StartC && EndA)

			return new SplitFace("SplitFace3L", thisFace.c, vertex1, vertex2, thisFace.c, vertex2, thisFace.b, vertex1, thisFace.a, vertex2);
		else 
			return new SplitFace("SplitFace3L", thisFace.c, vertex2, vertex1, thisFace.c, vertex1, thisFace.b, vertex2, thisFace.a, vertex1);
	}
	private final static SplitFace SplitFace3(Face thisFace, Vector newPos){

		Vertex vertex = new Vertex(newPos, State.Vertex.Boundary);

        return new SplitFace("SplitFace3", thisFace.a, thisFace.b, vertex, thisFace.b, thisFace.c, vertex, thisFace.c, thisFace.a, vertex);
	}
	private final static SplitFace SplitFace4V(Face thisFace, Vector newPos1, Vector newPos2, Vertex endVertex){
		
		Vertex vertex1 = new Vertex(newPos1, State.Vertex.Boundary);
		Vertex vertex2 = new Vertex(newPos2, State.Vertex.Boundary);
		
		if (endVertex.equals(thisFace.a))

			return new SplitFace("SplitFace4V", thisFace.a, vertex1, vertex2, vertex1, thisFace.b, vertex2, thisFace.b, thisFace.c, vertex2, thisFace.c, thisFace.a, vertex2);

		else if (endVertex.equals(thisFace.b))

			return new SplitFace("SplitFace4V", thisFace.b, vertex1, vertex2, vertex1, thisFace.c, vertex2, thisFace.c, thisFace.a, vertex2, thisFace.a, thisFace.b, vertex2);
		else 
			return new SplitFace("SplitFace4V", thisFace.c, vertex1, vertex2, vertex1, thisFace.a, vertex2, thisFace.a, thisFace.b, vertex2, thisFace.b, thisFace.c, vertex2);
	}	
	private final static SplitFace SplitFace5I(Face thisFace, Vector newPos1, Vector newPos2, int linedVertex){
		
		Vertex vertex1 = new Vertex(newPos1, State.Vertex.Boundary);
		Vertex vertex2 = new Vertex(newPos2, State.Vertex.Boundary);

		switch (linedVertex){

        case 1:
			return new SplitFace("SplitFace5I", thisFace.b, thisFace.c, vertex1, thisFace.b, vertex1, vertex2, thisFace.c, vertex2, vertex1, thisFace.b, vertex2, thisFace.a, thisFace.c, thisFace.a, vertex2);

        case 2:
			return new SplitFace("SplitFace5I", thisFace.c, thisFace.a, vertex1, thisFace.c, vertex1, vertex2, thisFace.a, vertex2, vertex1, thisFace.c, vertex2, thisFace.b, thisFace.a, thisFace.b, vertex2);

        case 3:
			return new SplitFace("SplitFace5I", thisFace.a, thisFace.b, vertex1, thisFace.a, vertex1, vertex2, thisFace.b, vertex2, vertex1, thisFace.a, vertex2, thisFace.c, thisFace.b, thisFace.c, vertex2);

        default:
            throw new IllegalStateException(String.valueOf(linedVertex));
		}
	}
    public String toString(){
        return this.toString(" ","\n");
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
        string.append(this.name);

        for (Face face : this){

            string.append(in);
            string.append(pr);
            string.append(face);
        }
        return string.toString();
    }
}
