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

import java.util.Random;


public final class Line
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
        final double px = EPS2*R.nextDouble();
        final double py = EPS2*R.nextDouble();
        final double pz = EPS2*R.nextDouble();

		this.direction.add( px, py, pz);
	}
}
