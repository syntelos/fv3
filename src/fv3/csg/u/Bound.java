/*
 * fv3
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
 * A cuboid bounding region 
 * 
 * @author John Pritchard
 */
public final class Bound 
    extends java.lang.Object
    implements fv3.csg.u.Notation,
               fv3.Bounds
{
    public final float maxX, midX, minX;
    public final float maxY, midY, minY;
    public final float maxZ, midZ, minZ;
	

    public Bound(Face face){
        super();
        final Vertex a = face.a;
        final Vertex b = face.b;
        final Vertex c = face.c;
        this.minX = Math.min(Math.min(a.x,b.x),c.x);
        this.maxX = Math.max(Math.max(a.x,b.x),c.x);
        this.minY = Math.min(Math.min(a.y,b.y),c.y);
        this.maxY = Math.max(Math.max(a.y,b.y),c.y);
        this.minZ = Math.min(Math.min(a.z,b.z),c.z);
        this.maxZ = Math.max(Math.max(a.z,b.z),c.z);

        this.midX = (minX + maxX)/2.0f;
        this.midY = (minY + maxY)/2.0f;
        this.midZ = (minZ + maxZ)/2.0f;
    }
    public Bound(Mesh solid){
        super();
        float maxX = 0, minX = 0, maxY = 0, minY = 0, maxZ = 0, minZ = 0;
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

        this.midX = (minX + maxX)/2.0f;
        this.midY = (minY + maxY)/2.0f;
        this.midZ = (minZ + maxZ)/2.0f;
    }


    /**
     * @see AH
     */
    public boolean intersect(Bound b){
        /*
         * Faster to disprove the intersection than to prove it.
         */
        if (( this.minX > b.maxX )||
            ( this.maxX < b.minX )||
            ( this.minY > b.maxY )||
            ( this.maxY < b.minY )||
            ( this.minZ > b.maxZ )||
            ( this.maxZ < b.minZ ))

            return false;
        else
            return true;
    }
    public float getBoundsMinX(){
        return this.minX;
    }
    public float getBoundsMidX(){
        return this.midX;
    }
    public float getBoundsMaxX(){
        return this.maxX;
    }
    public float getBoundsMinY(){
        return this.minY;
    }
    public float getBoundsMidY(){
        return this.midY;
    }
    public float getBoundsMaxY(){
        return this.maxY;
    }
    public float getBoundsMinZ(){
        return this.minZ;
    }
    public float getBoundsMidZ(){
        return this.midZ;
    }
    public float getBoundsMaxZ(){
        return this.maxZ;
    }
}
