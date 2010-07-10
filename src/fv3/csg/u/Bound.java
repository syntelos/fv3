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

/**
 * Based on the work of Danilo Balby Silva Castanheira in <a
 * href="http://unbboolean.sf.net/">J3DBool</a> following <a
 * href="http://www.cs.brown.edu/~jfh/papers/Laidlaw-CSG-1986/main.htm">"Constructive
 * Solid Geometry for Polyhedral Objects"</a>.
 */
public final class Bound 
    extends java.lang.Object
    implements fv3.csg.u.Notation,
               fv3.Bounds
{
	public final double maxX, midX, minX;
	public final double maxY, midY, minY;
	public final double maxZ, midZ, minZ;
	

    public Bound(Face face){
        super();
        this.minX = Math.min(Math.min(face.a.x,face.b.x),face.c.x);
        this.maxX = Math.max(Math.max(face.a.x,face.b.x),face.c.x);
        this.minY = Math.min(Math.min(face.a.y,face.b.y),face.c.y);
        this.maxY = Math.max(Math.max(face.a.y,face.b.y),face.c.y);
        this.minZ = Math.min(Math.min(face.a.z,face.b.z),face.c.z);
        this.maxZ = Math.max(Math.max(face.a.z,face.b.z),face.c.z);

        this.midX = (minX + maxX)/2.0;
        this.midY = (minY + maxY)/2.0;
        this.midZ = (minZ + maxZ)/2.0;
    }
    public Bound(Mesh solid){
        super();
        double maxX = 0, minX = 0, maxY = 0, minY = 0, maxZ = 0, minZ = 0;
        boolean once = true;

        for (Face face: solid){
            if (once){
                once = false;
                Bound b = face.getBound();
                minX = b.minX;
                maxX = b.maxX;
                minY = b.minY;
                maxY = b.maxY;
                minZ = b.minZ;
                maxZ = b.maxZ;
            }
            else {
                Bound b = face.getBound();
                minX = Math.min(minX,b.minX);
                maxX = Math.max(maxX,b.maxX);
                minY = Math.min(minY,b.minY);
                maxY = Math.max(maxY,b.maxY);
                minZ = Math.min(minZ,b.minZ);
                maxZ = Math.max(maxZ,b.maxZ);
            }
        }
        this.maxX = maxX;
        this.minX = minX;
        this.maxY = maxY;
        this.minY = minY;
        this.maxZ = maxZ;
        this.minZ = minZ;

        this.midX = (minX + maxX)/2.0;
        this.midY = (minY + maxY)/2.0;
        this.midZ = (minZ + maxZ)/2.0;
    }


	public boolean overlap(Bound bound){

		return (! (( this.minX > bound.maxX + EPS )||
                   ( this.maxX < bound.minX - EPS )||
                   ( this.minY > bound.maxY + EPS )||
                   ( this.maxY < bound.minY - EPS )||
                   ( this.minZ > bound.maxZ + EPS )||
                   ( this.maxZ < bound.minZ - EPS )));
	}
    public double getBoundsMinX(){
        return this.minX;
    }
    public double getBoundsMidX(){
        return this.midX;
    }
    public double getBoundsMaxX(){
        return this.maxX;
    }
    public double getBoundsMinY(){
        return this.minY;
    }
    public double getBoundsMidY(){
        return this.midY;
    }
    public double getBoundsMaxY(){
        return this.maxY;
    }
    public double getBoundsMinZ(){
        return this.minZ;
    }
    public double getBoundsMidZ(){
        return this.midZ;
    }
    public double getBoundsMaxZ(){
        return this.maxZ;
    }
}
