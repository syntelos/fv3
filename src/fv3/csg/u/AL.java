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

/**
 * Laidlaw, Trumbore and Hughes <a
 * href="http://www.cs.brown.edu/~jfh/papers/Laidlaw-CSG-1986/main.htm">"Constructive
 * Solid Geometry for Polyhedral Objects"</a>.
 * 
 * Based on the work of Danilo Balby Silva Castanheira in <a
 * href="http://unbboolean.sf.net/">J3DBool</a>.
 *
 * @author Danilo Balby Silva Castanheira
 * @author John Pritchard
 */
public class AL
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

                face.mark();
            }
        }
    }
    private static void InvertInsideFaces(Solid a){

        for (Face face: a){

            if (face.isInside())

                face.invertNormal();
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

                            final double DaA = aFace.a.distance(bFace);
                            final double DaB = aFace.b.distance(bFace);
                            final double DaC = aFace.c.distance(bFace);

							final int SaA = (DaA > EPS ? 1 :(DaA < -EPS ? -1 : 0)); 
							final int SaB = (DaB > EPS ? 1 :(DaB < -EPS ? -1 : 0));
							final int SaC = (DaC > EPS ? 1 :(DaC < -EPS ? -1 : 0));

                            if (!(SaA == SaB && SaB == SaC)){

                                final double DbA = bFace.a.distance(aFace);
                                final double DbB = bFace.b.distance(aFace);
                                final double DbC = bFace.c.distance(aFace);

                                final int SbA = (DbA > EPS ? 1 :(DbA < -EPS ? -1 : 0)); 
                                final int SbB = (DbB > EPS ? 1 :(DbB < -EPS ? -1 : 0));
                                final int SbC = (DbC > EPS ? 1 :(DbC < -EPS ? -1 : 0));

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

}
