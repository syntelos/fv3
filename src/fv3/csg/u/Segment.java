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

public final class Segment
    extends Object
    implements Notation
{
    public enum Type {
        Vertex, Face, Edge;
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
	public boolean intersect(Segment segment){
		return (!(this.endDist < segment.startDist+EPS || segment.endDist < this.startDist+EPS));
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
