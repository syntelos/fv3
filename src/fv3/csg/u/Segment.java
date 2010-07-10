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
package fv3.csg.u;

import fv3.math.Vector;

import java.util.Random;

public final class Segment
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
