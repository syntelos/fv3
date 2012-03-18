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
	public final double maxX, midX, minX;
	public final double maxY, midY, minY;
	public final double maxZ, midZ, minZ;
	

    public VertexArrayBounds(VertexArray array){
        super();
        double maxX = 0, minX = 0, maxY = 0, minY = 0, maxZ = 0, minZ = 0;
        boolean once = true;

        for (int cc = 0, count = array.countVertices(); cc < count; cc++){
            double[] vertex = array.getVertex(cc);
            double x = vertex[X];
            double y = vertex[Y];
            double z = vertex[Z];
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
