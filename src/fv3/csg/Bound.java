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

/**
 * Based on the work of Danilo Balby Silva Castanheira in <a
 * href="http://unbboolean.sf.net/">J3DBool</a> following <a
 * href="http://www.cs.brown.edu/~jfh/papers/Laidlaw-CSG-1986/main.htm">"Constructive
 * Solid Geometry for Polyhedral Objects"</a>.
 */
public final class Bound 
    extends java.lang.Object
    implements fv3.csg.Notation
{


	public final double xMax, xMin, yMax, yMin, zMax, zMin;
	

    public Bound(Face face){
        super();
        this.xMin = Math.min(Math.min(face.a.x,face.b.x),face.c.x);
        this.xMax = Math.max(Math.max(face.a.x,face.b.x),face.c.x);
        this.yMin = Math.min(Math.min(face.a.y,face.b.y),face.c.y);
        this.yMax = Math.max(Math.max(face.a.y,face.b.y),face.c.y);
        this.zMin = Math.min(Math.min(face.a.z,face.b.z),face.c.z);
        this.zMax = Math.max(Math.max(face.a.z,face.b.z),face.c.z);
    }
    public Bound(State solid){
        super();
        double xMax = 0, xMin = 0, yMax = 0, yMin = 0, zMax = 0, zMin = 0;
        boolean once = true;

        for (Face face: solid){
            if (once){
                once = false;
                Bound b = face.getBound();
                xMin = b.xMin;
                xMax = b.xMax;
                yMin = b.yMin;
                yMax = b.yMax;
                zMin = b.zMin;
                zMax = b.zMax;
            }
            else {
                Bound b = face.getBound();
                xMin = Math.min(xMin,b.xMin);
                xMax = Math.max(xMax,b.xMax);
                yMin = Math.min(yMin,b.yMin);
                yMax = Math.max(yMax,b.yMax);
                zMin = Math.min(zMin,b.zMin);
                zMax = Math.max(zMax,b.zMax);
            }
        }
        this.xMax = xMax;
        this.xMin = xMin;
        this.yMax = yMax;
        this.yMin = yMin;
        this.zMax = zMax;
        this.zMin = zMin;
    }


	public boolean overlap(Bound bound){

		return (! (( this.xMin > bound.xMax + EPS )||
                   ( this.xMax < bound.xMin - EPS )||
                   ( this.yMin > bound.yMax + EPS )||
                   ( this.yMax < bound.yMin - EPS )||
                   ( this.zMin > bound.zMax + EPS )||
                   ( this.zMax < bound.zMin - EPS )));
	}
}
