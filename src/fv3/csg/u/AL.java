/*
 * fv3 "AL"
 * Copyright (C) 2010  Danilo Balby Silva Castanheira (danbalby@yahoo.com)
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

import java.util.Random;

/**
 * Laidlaw, Trumbore and Hughes, 1986, <a
 * href="http://www.cs.brown.edu/~jfh/papers/Laidlaw-CSG-1986/main.htm">"Constructive
 * Solid Geometry for Polyhedral Objects"</a>.
 * 
 * Based on the work of Danilo Balby Silva Castanheira in <a
 * href="http://unbboolean.sf.net/">J3DBool</a>.
 *
 * @see AH
 * @author Danilo Balby Silva Castanheira
 * @author John Pritchard
 */
public final class AL
    extends A
{

    /**
     * Perform operation.  Subsequently requires call to method
     * "destroy".
     */
    public AL(Solid.Construct op, Solid a, Solid b){
        super(op,a,b);

        InitFaces(a,b);

        SplitFaces(a,b);

        SplitFaces(b,a);

        ClassifyFaces(a,b);

        ClassifyFaces(b,a);

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



    private static void ClassifyFaces(Solid a, Solid b){

        for (Face face: a){

            if (!face.simpleClassify()){

                face.rayTraceClassify(b);

                face.markAL();
            }
        }
    }
    private final static void SplitFaces(Solid a, Solid b){

        Bound bBound = b.getBound();

        if (a.getBound().overlap(bBound)){

            scan:
            for (Face aFace: a){

                if (aFace.getBound().overlap(bBound)){

                    for (Face bFace: b){

                        if (aFace.getBound().overlap(bFace.getBound())){

                            final int SaA = aFace.a.sdistance(bFace);
                            final int SaB = aFace.b.sdistance(bFace);
                            final int SaC = aFace.c.sdistance(bFace);

                            if (!(SaA == SaB && SaB == SaC)){

                                final int SbA = bFace.a.sdistance(aFace);
                                final int SbB = bFace.b.sdistance(aFace);
                                final int SbC = bFace.c.sdistance(aFace);

                                if (!(SbA == SbB && SbB == SbC)){

                                    final Segment.Line line = new Segment.Line(aFace, bFace);

                                    final Segment aSeg = new Segment(line, aFace, 
                                                                        SaA, SaB, SaC);

                                    final Segment bSeg = new Segment(line, bFace, 
                                                                        SbA, SbB, SbC);

									if (aSeg.intersect(bSeg)){

										SplitFace s = AL.SplitFace(aFace,aSeg,bSeg);
                                        if (null != s){

                                            int eqv = s.indexOf(aFace);

                                            if (-1 != eqv){

                                                for (int fc = 0, fz = s.size(); fc < fz; fc++){
                                                    if (eqv != fc)
                                                        a.add(s.create(a,aFace.name,fc));
                                                }
                                            }
                                            else {
                                                aFace.dropFrom(a);

                                                for (int fc = 0, fz = s.size(); fc < fz; fc++){

                                                    a.add(s.create(a,aFace.name,fc));
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

    private final static SplitFace SplitFace(Face aFace, Segment aSeg, Segment bSeg){

		Vertex startVertex = aSeg.getStartVertex();
		Vertex endVertex = aSeg.getEndVertex();
        /*
         */
		Vector startPos;
		Segment.Type startType;
		double startDist;
		if (bSeg.getStartDistance() > aSeg.getStartDistance()+EPS){
			startDist = bSeg.getStartDistance();
			startType = aSeg.getIntermediateType();
			startPos = bSeg.getStartPosition();
		}
		else {
			startDist = aSeg.getStartDistance();
			startType = aSeg.getStartType();
			startPos = aSeg.getStartPosition();
		}
        /*
         */
		Vector endPos;
		Segment.Type endType;
		double endDist;
		if (bSeg.getEndDistance() < aSeg.getEndDistance()-EPS){
			endDist = bSeg.getEndDistance();
			endType = aSeg.getIntermediateType();
			endPos = bSeg.getEndPosition();
		}
		else {
			endDist = aSeg.getEndDistance();
			endType = aSeg.getEndType();
			endPos = aSeg.getEndPosition();
		}
        /*
         */
		Segment.Type middleType = aSeg.getIntermediateType();
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

			if ((startVertex == aFace.a && endVertex == aFace.b)
                || (startVertex == aFace.b && endVertex == aFace.a))

				splitEdge = 1;

			else if ((startVertex == aFace.b && endVertex == aFace.c)
                     || (startVertex == aFace.c && endVertex == aFace.b))

				splitEdge = 2; 
			else
				splitEdge = 3;

			
			if (Segment.Type.Vertex == startType){

				return SplitFace2I(aFace, endPos, splitEdge);
			}
			else if (Segment.Type.Vertex == endType){

				return SplitFace2I(aFace, startPos, splitEdge);
			}
			else if (startDist == endDist){

				return SplitFace2I(aFace, endPos, splitEdge);
            }
			else {

				if ((startVertex == aFace.a && endVertex == aFace.b) ||
                    (startVertex == aFace.b && endVertex == aFace.c) ||
                    (startVertex == aFace.c && endVertex == aFace.a))
				{
					return SplitFace3I(aFace, startPos, endPos, splitEdge);
				}
				else
					return SplitFace3I(aFace, endPos, startPos, splitEdge);
			}
		}
		else if (Segment.Type.Vertex == startType){

            if (Segment.Type.Edge == endType)

                return SplitFace2V(aFace, endPos, endVertex);

            else if (Segment.Type.Face == endType)

                return SplitFace3V(aFace, endPos, startVertex);
        }
		else if (Segment.Type.Edge == startType){

            if ( Segment.Type.Vertex == endType)

                return SplitFace2V(aFace, startPos, startVertex);

            else if (Segment.Type.Edge == endType)

                return SplitFace3L(aFace, startPos, endPos, startVertex, endVertex);

            else if (Segment.Type.Face == endType)

                return SplitFace4V(aFace, startPos, endPos, startVertex);
        }
		else if (Segment.Type.Face == startType){

            if (Segment.Type.Vertex == endType)

                return SplitFace3V(aFace, startPos, endVertex);

            else if (Segment.Type.Edge == endType)

                return SplitFace4V(aFace, endPos, startPos, endVertex);

            else if (Segment.Type.Face == endType){

                Vector segmentVector = new Vector(startPos.x()-endPos.x(), 
                                                  startPos.y()-endPos.y(), 
                                                  startPos.z()-endPos.z());

                if (EPS > Math.abs(segmentVector.x()) &&
                    EPS > Math.abs(segmentVector.y()) &&
                    EPS > Math.abs(segmentVector.z()))
                {
                    return SplitFace3(aFace, startPos);
                }
                else {
                    int linedVertex;
                    Vector linedVertexPos;
                    {
                        Vector vertexVector;

                        vertexVector = new Vector(endPos.x()-aFace.a.x,
                                                  endPos.y()-aFace.a.y,
                                                  endPos.z()-aFace.a.z)
                            .normalize();

                        double dot1 = Math.abs(segmentVector.dot(vertexVector));

                        vertexVector = new Vector(endPos.x()-aFace.b.x, 
                                                  endPos.y()-aFace.b.y,
                                                  endPos.z()-aFace.b.z)
                            .normalize();

                        double dot2 = Math.abs(segmentVector.dot(vertexVector));

                        vertexVector = new Vector(endPos.x()-aFace.c.x,
                                                  endPos.y()-aFace.c.y,
                                                  endPos.z()-aFace.c.z)
                            .normalize();

                        double dot3 = Math.abs(segmentVector.dot(vertexVector));

                        if (dot1 > dot2 && dot1 > dot3){
                            linedVertex = 1;
                            linedVertexPos = aFace.a.getPosition();
                        }
                        else if (dot2 > dot3 && dot2 > dot1){
                            linedVertex = 2;
                            linedVertexPos = aFace.b.getPosition();
                        }
                        else {
                            linedVertex = 3;
                            linedVertexPos = aFace.c.getPosition();
                        }
                    }

                    if (linedVertexPos.distance(startPos) > linedVertexPos.distance(endPos))

                        return SplitFace5I(aFace, startPos, endPos, linedVertex);
                    else
                        return SplitFace5I(aFace, endPos, startPos, linedVertex);
                }
            }
		}
        return null;
    }
	private final static SplitFace SplitFace2I(Face aFace, Vector newPos, int splitEdge){

		Vertex vertex = new Vertex(newPos, State.Vertex.Boundary); 

        switch (splitEdge){
        case 1:
			return new SplitFace("SplitFace2I", aFace.a, vertex, aFace.c, vertex, aFace.b, aFace.c);
        case 2:
			return new SplitFace("SplitFace2I", aFace.b, vertex, aFace.a, vertex, aFace.c, aFace.a);
        case 3:
			return new SplitFace("SplitFace2I", aFace.c, vertex, aFace.b, vertex, aFace.a, aFace.b);
        default:
            throw new IllegalArgumentException(String.valueOf(splitEdge));
		}
	}	
	private final static SplitFace SplitFace2V(Face aFace, Vector newPos, Vertex endVertex){
		
		Vertex vertex = new Vertex(newPos, State.Vertex.Boundary);
					
		if (endVertex.equals(aFace.a))

			return new SplitFace("SplitFace2V", aFace.a, vertex, aFace.c, vertex, aFace.b, aFace.c);

		else if (endVertex.equals(aFace.b))

			return new SplitFace("SplitFace2V", aFace.b, vertex, aFace.a, vertex, aFace.c, aFace.a);
		else 
			return new SplitFace("SplitFace2V", aFace.c, vertex, aFace.b, vertex, aFace.a, aFace.b);
	}
	private final static SplitFace SplitFace3I(Face aFace, Vector newPos1, Vector newPos2, int splitEdge){

		Vertex vertex1 = new Vertex(newPos1, State.Vertex.Boundary);
		Vertex vertex2 = new Vertex(newPos2, State.Vertex.Boundary);
				
        switch (splitEdge){
        case 1:
			return new SplitFace("SplitFace3I", aFace.a, vertex1, aFace.c, vertex1, vertex2, aFace.c, vertex2, aFace.b, aFace.c);
        case 2:
            return new SplitFace("SplitFace3I", aFace.b, vertex1, aFace.a, vertex1, vertex2, aFace.a, vertex2, aFace.c, aFace.a);
        case 3:
            return new SplitFace("SplitFace3I", aFace.c, vertex1, aFace.b, vertex1, vertex2, aFace.b, vertex2, aFace.a, aFace.b);
        default:
            throw new IllegalArgumentException(String.valueOf(splitEdge));
		}
	}
	private final static SplitFace SplitFace3V(Face aFace, Vector newPos, Vertex endVertex){

		Vertex vertex = new Vertex(newPos, State.Vertex.Boundary);
						
		if (endVertex.equals(aFace.a))

			return new SplitFace("SplitFace3V", aFace.a, aFace.b, vertex, aFace.b, aFace.c, vertex, aFace.c, aFace.a, vertex);

		else if (endVertex.equals(aFace.b))

			return new SplitFace("SplitFace3V", aFace.b, aFace.c, vertex, aFace.c, aFace.a, vertex, aFace.a, aFace.b, vertex);
		else 
			return new SplitFace("SplitFace3V", aFace.c, aFace.a, vertex, aFace.a, aFace.b, vertex, aFace.b, aFace.c, vertex);
	}
	private final static SplitFace SplitFace3L(Face aFace, Vector newPos1, Vector newPos2, 
                             Vertex startVertex, Vertex endVertex)
    {
		
		Vertex vertex1 = new Vertex(newPos1, State.Vertex.Boundary);
		Vertex vertex2 = new Vertex(newPos2, State.Vertex.Boundary);

        final boolean StartA = startVertex.equals(aFace.a);
        final boolean StartB = startVertex.equals(aFace.b);
        final boolean StartC = startVertex.equals(aFace.c);
        final boolean EndA = endVertex.equals(aFace.a);
        final boolean EndB = endVertex.equals(aFace.b);
        final boolean EndC = endVertex.equals(aFace.c);
						
		if (StartA && EndB)

			return new SplitFace("SplitFace3L", aFace.a, vertex1, vertex2, aFace.a, vertex2, aFace.c, vertex1, aFace.b, vertex2);

		else if (StartB && EndA)

			return new SplitFace("SplitFace3L", aFace.a, vertex2, vertex1, aFace.a, vertex1, aFace.c, vertex2, aFace.b, vertex1);

		else if (StartB && EndC)

			return new SplitFace("SplitFace3L", aFace.b, vertex1, vertex2, aFace.b, vertex2, aFace.a, vertex1, aFace.c, vertex2);

		else if (StartC && EndB)

			return new SplitFace("SplitFace3L", aFace.b, vertex2, vertex1, aFace.b, vertex1, aFace.a, vertex2, aFace.c, vertex1);

		else if (StartC && EndA)

			return new SplitFace("SplitFace3L", aFace.c, vertex1, vertex2, aFace.c, vertex2, aFace.b, vertex1, aFace.a, vertex2);
		else 
			return new SplitFace("SplitFace3L", aFace.c, vertex2, vertex1, aFace.c, vertex1, aFace.b, vertex2, aFace.a, vertex1);
	}
	private final static SplitFace SplitFace3(Face aFace, Vector newPos){

		Vertex vertex = new Vertex(newPos, State.Vertex.Boundary);

        return new SplitFace("SplitFace3", aFace.a, aFace.b, vertex, aFace.b, aFace.c, vertex, aFace.c, aFace.a, vertex);
	}
	private final static SplitFace SplitFace4V(Face aFace, Vector newPos1, Vector newPos2, Vertex endVertex){
		
		Vertex vertex1 = new Vertex(newPos1, State.Vertex.Boundary);
		Vertex vertex2 = new Vertex(newPos2, State.Vertex.Boundary);
		
		if (endVertex.equals(aFace.a))

			return new SplitFace("SplitFace4V", aFace.a, vertex1, vertex2, vertex1, aFace.b, vertex2, aFace.b, aFace.c, vertex2, aFace.c, aFace.a, vertex2);

		else if (endVertex.equals(aFace.b))

			return new SplitFace("SplitFace4V", aFace.b, vertex1, vertex2, vertex1, aFace.c, vertex2, aFace.c, aFace.a, vertex2, aFace.a, aFace.b, vertex2);
		else 
			return new SplitFace("SplitFace4V", aFace.c, vertex1, vertex2, vertex1, aFace.a, vertex2, aFace.a, aFace.b, vertex2, aFace.b, aFace.c, vertex2);
	}	
	private final static SplitFace SplitFace5I(Face aFace, Vector newPos1, Vector newPos2, int linedVertex){
		
		Vertex vertex1 = new Vertex(newPos1, State.Vertex.Boundary);
		Vertex vertex2 = new Vertex(newPos2, State.Vertex.Boundary);

		switch (linedVertex){

        case 1:
			return new SplitFace("SplitFace5I", aFace.b, aFace.c, vertex1, aFace.b, vertex1, vertex2, aFace.c, vertex2, vertex1, aFace.b, vertex2, aFace.a, aFace.c, aFace.a, vertex2);

        case 2:
			return new SplitFace("SplitFace5I", aFace.c, aFace.a, vertex1, aFace.c, vertex1, vertex2, aFace.a, vertex2, vertex1, aFace.c, vertex2, aFace.b, aFace.a, aFace.b, vertex2);

        case 3:
			return new SplitFace("SplitFace5I", aFace.a, aFace.b, vertex1, aFace.a, vertex1, vertex2, aFace.b, vertex2, vertex1, aFace.a, vertex2, aFace.c, aFace.b, aFace.c, vertex2);

        default:
            throw new IllegalStateException(String.valueOf(linedVertex));
		}
	}

    /**
     * {@link AL} intersection segment
     * 
     * Based on the work of Danilo Balby Silva Castanheira in <a
     * href="http://unbboolean.sf.net/">J3DBool</a>.
     *
     * @author Danilo Balby Silva Castanheira
     * @author John Pritchard
     */
    public final static class Segment
        extends Object
        implements Notation
    {
        public enum Type {
            Vertex, Face, Edge;
        }

        public final static class Line
            extends Object
            implements Notation
        {

            private Vector point;
            private Vector direction;


            public Line(Face thisFace, Face thatFace){
                super();
                Vector normalThisFace = thisFace.getNormal();
                Vector normalThatFace = thatFace.getNormal();

                Vector direction = normalThisFace.clone().cross(normalThatFace);

                if (!(EPS > direction.length())){
                    final double[] directionA = direction.array();
                    final double[] normalThisFaceA = normalThisFace.array();
                    final double[] normalThatFaceA = normalThatFace.array();

                    double[] point = new double[3];

                    double d1 = -(normalThisFaceA[X]*thisFace.a.x + normalThisFaceA[Y]*thisFace.a.y + normalThisFaceA[Z]*thisFace.a.z);

                    double d2 = -(normalThatFaceA[X]*thatFace.a.x + normalThatFaceA[Y]*thatFace.a.y + normalThatFaceA[Z]*thatFace.a.z);

                    if (EPS < Math.abs(directionA[X])){

                        point[X] = 0;
                        point[Y] = ( (d2 * normalThisFaceA[Z] - d1 * normalThatFaceA[Z]) / directionA[X]);
                        point[Z] = ( (d1 * normalThatFaceA[Y] - d2 * normalThisFaceA[Y]) / directionA[X]);
                    }
                    else if (EPS < Math.abs(direction.y())){

                        point[X] = ( (d1 * normalThatFaceA[Z] - d2 * normalThisFaceA[Z]) / directionA[Y]);
                        point[Y] = 0;
                        point[Z] = ( (d2 * normalThisFaceA[X] - d1 * normalThatFaceA[X]) / directionA[Y]);
                    }
                    else {
                        point[X] = ( (d2 * normalThisFaceA[Y] - d1 * normalThatFaceA[Y]) / directionA[Z]);
                        point[Y] = ( (d1 * normalThatFaceA[X] - d2 * normalThisFaceA[X]) / directionA[Z]);
                        point[Z] = 0;
                    }
                    this.point = new Vector(point);
                }

                this.direction = direction.normalize();
            }
            public Line(Vector direction, Vector point){
                super();
                this.direction = direction.clone().normalize();
                this.point = point.clone();
            }


            public Vector getPoint(){
                return point.clone();
            }
            public Vector getDirection(){
                return direction.clone();
            }
            public double computePointToPointDistance(Vector that){

                double distance = that.distance(point);

                Vector vec = that.clone().sub(point).normalize();

                if (0 > vec.dot(direction))

                    return -distance;			
                else
                    return distance;
            }
            public Vector computeLineIntersection(Line that){

                double[] thisPoint = this.point.array();
                double[] thisDirection = this.direction.array();

                double[] thatPoint = that.getPoint().array(); 
                double[] thatDirection = that.getDirection().array();

                double t;
                if (EPS < Math.abs(thisDirection[Y]*thatDirection[X] - thisDirection[X]*thatDirection[Y]))

                    t = (-thisPoint[Y]*thatDirection[X] + thatPoint[Y]*thatDirection[X] + thatDirection[Y]*thisPoint[X] - thatDirection[Y]*thatPoint[X] )

                        / (thisDirection[Y]*thatDirection[X] - thisDirection[X]*thatDirection[Y]);

                else if (EPS < Math.abs(-thisDirection[X]*thatDirection[Z] + thisDirection[Z]*thatDirection[X]))

                    t = -(-thatDirection[Z]*thisPoint[X] + thatDirection[Z]*thatPoint[X] + thatDirection[X]*thisPoint[Z] - thatDirection[X]*thatPoint[Z])

                        / (-thisDirection[X]*thatDirection[Z] + thisDirection[Z]*thatDirection[X]);

                else if (EPS < Math.abs(-thisDirection[Z]*thatDirection[Y] + thisDirection[Y]*thatDirection[Z]))

                    t = (thisPoint[Z]*thatDirection[Y] - thatPoint[Z]*thatDirection[Y] - thatDirection[Z]*thisPoint[Y] + thatDirection[Z]*thatPoint[Y])

                        / (-thisDirection[Z]*thatDirection[Y] + thisDirection[Y]*thatDirection[Z]);

                else 
                    return null;


                return this.point.clone().add(this.direction.clone().mul(t));
            }
            public Vector computePlaneIntersection(Vector normal, Vector planePoint){


                final double numerator = normal.dot(this.point) - normal.dot(planePoint);

                final double denominator = normal.dot(this.direction);


                if (Math.abs(denominator) < EPS){

                    if (Math.abs(numerator) < EPS)

                        return this.point.clone();
                    else
                        return null;
                }
                else {

                    final double t = -numerator/denominator;

                    return this.point.clone().add(this.direction.clone().mul(t));
                }
            }
            public void perturbDirection(){
                final Random R = new Random();
                final double px = EPS_M2*R.nextDouble();
                final double py = EPS_M2*R.nextDouble();
                final double pz = EPS_M2*R.nextDouble();

                this.direction.add( px, py, pz);
            }
        }


        private Line line;
        private int index;
        private double startDist;
        private double endDist;
	
        private Segment.Type startType;
        private Segment.Type middleType;
        private Segment.Type endType;
	
        private Vertex startVertex;
        private Vertex endVertex; 

        private Vector startPos;
        private Vector endPos;


        public Segment(Line line, Face face, int sA, int sB, int sC){
            super();
            this.line = line;
            this.index = 0;

            if (0 == sA){
                if (this.vertex(face.a)){

                    if (sB == sC)
                        this.vertex(face.a);
                }
            }

            if (2 > this.index){

                if (0 == sB){
                    if (this.vertex(face.b)){

                        if (sA == sC)
                            this.vertex(face.b);
                    }
                }

                if (2 > this.index){

                    if (0 == sC){
                        if (this.vertex(face.c)){

                            if (sA == sB)
                                this.vertex(face.c);
                        }
                    }

                    if (2 > this.index){

                        if ( (1 == sA && -1 == sB) || (-1 == sA && 1 == sB))

                            this.edge(face.a,face.b);

                        if (2 > this.index){

                            if ( (1 == sB && -1 == sC) || (-1 == sB && 1 == sC))

                                this.edge(face.b,face.c);

                            if (2 > this.index){

                                if ( (1 == sC && -1 == sA) || (-1 == sC && 1 == sA))

                                    this.edge(face.c,face.a);
                            }
                        }
                    }
                }
            }
        }


        public Vertex getStartVertex(){
            return this.startVertex;
        }
        public Vertex getEndVertex(){
            return this.endVertex;
        }
        public double getStartDistance(){
            return this.startDist;
        }
        public double getEndDistance(){
            return this.endDist;
        }
        public Segment.Type getStartType(){
            return this.startType;
        }
        public Segment.Type getIntermediateType(){
            return this.middleType;
        }
        public Segment.Type getEndType(){
            return this.endType;
        }
        public int getNumEndsSet(){
            return this.index;
        }
        public Vector getStartPosition(){
            return this.startPos;
        }
        public Vector getEndPosition(){
            return this.endPos;
        }
        public boolean intersect(Segment that){

            final double thisStartDist = (this.startDist+EPS);
            final double thatStartDist = (that.startDist+EPS);

            final double thisEndDist = this.endDist;
            final double thatEndDist = that.endDist;

            if (thisEndDist < thatStartDist || thatEndDist < thisStartDist)

                return false;
            else
                return true;
        }
        private boolean vertex(Vertex vertex){

            switch (this.index){
            case 0:
                this.startVertex = vertex;
                this.startType = Segment.Type.Vertex;
                this.startDist = line.computePointToPointDistance(vertex.getPosition());
                this.startPos = startVertex.getPosition();
                this.index++;
                return true;
            case 1:
                this.endVertex = vertex;
                this.endType = Segment.Type.Vertex;
                this.endDist = line.computePointToPointDistance(vertex.getPosition());
                this.endPos = endVertex.getPosition();
                this.index++;

                if (this.startVertex.equals(this.endVertex))

                    this.middleType = Segment.Type.Vertex;

                else if (Segment.Type.Vertex == this.startType)

                    this.middleType = Segment.Type.Edge;

                //			

                if (this.startDist > this.endDist)
                    this.swapEnds();

                return true;
            default:
                return false;
            }
        }
        private boolean edge(Vertex a, Vertex b){

            Vector edgeDirection = new Vector( (b.x - a.x), (b.y - a.y), (b.z - a.z));

            Line edgeLine = new Line(edgeDirection, a.getPosition());
		
            switch (this.index){

            case 0:

                this.startVertex = a;
                this.startType = Segment.Type.Edge;
                this.startPos = line.computeLineIntersection(edgeLine);
                this.startDist = line.computePointToPointDistance(startPos);
                this.middleType = Segment.Type.Face;
                this.index++;
                return true;

            case 1:
                this.endVertex = a;
                this.endType = Segment.Type.Edge;
                this.endPos = line.computeLineIntersection(edgeLine);
                this.endDist = line.computePointToPointDistance(endPos);
                this.middleType = Segment.Type.Face;
                this.index++;
			

                if (this.startDist > this.endDist)

                    this.swapEnds();
			
                return true;

            default:
                return false;
            }
        }
        private void swapEnds(){

            double distTemp = this.startDist;
            this.startDist = this.endDist;
            this.endDist = distTemp;
		
            Segment.Type typeTemp = this.startType;
            this.startType = this.endType;
            this.endType = typeTemp;
		
            Vertex vertexTemp = this.startVertex;
            this.startVertex = this.endVertex;
            this.endVertex = vertexTemp;
		
            Vector posTemp = this.startPos;
            this.startPos = this.endPos;
            this.endPos = posTemp;		
        }
    }

}
