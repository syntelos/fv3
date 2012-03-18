/*
 * fv3.math
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
package fv3.math;

/**
 * 
 */
public class VertexArrayBounds
    extends java.lang.Object
    implements fv3.math.Notation,
               fv3.Bounds
{
    public final float maxX, midX, minX;
    public final float maxY, midY, minY;
    public final float maxZ, midZ, minZ;
	

    public VertexArrayBounds(VertexArray array){
        super();
        float maxX = 0, minX = 0, maxY = 0, minY = 0, maxZ = 0, minZ = 0;
        boolean once = true;

        for (int cc = 0, count = array.countVertices(); cc < count; cc++){
            float[] vertex = array.getVertex(cc);
            float x = vertex[X];
            float y = vertex[Y];
            float z = vertex[Z];
            if (once){
                once = false;
                minX = x;
                maxX = x;
                minY = y;
                maxY = y;
                minZ = z;
                maxZ = z;
            }
            else {
                minX = Math.min(minX,x);
                maxX = Math.max(maxX,x);
                minY = Math.min(minY,y);
                maxY = Math.max(maxY,y);
                minZ = Math.min(minZ,z);
                maxZ = Math.max(maxZ,z);
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
