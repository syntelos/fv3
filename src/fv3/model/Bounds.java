/*
 * Fv3
 * Copyright (C) 2009  John Pritchard, jdp@syntelos.org
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
package fv3.model;

import fv3.math.Matrix;

/**
 * Model bounds.
 */
public class Bounds
    extends java.lang.Object
    implements fv3.Bounds,
               fv3.math.Notation
{
    public final float minX, maxX, minY, maxY, minZ, maxZ, midX, midY, midZ;

    public Bounds(Model m){
        super();
        float minX = 0, maxX = 0;
        float minY = 0, maxY = 0;
        float minZ = 0, maxZ = 0;

        Matrix mm = m.composeFv3Matrix();
        if (null != mm){

            for (int idx = 0, cnt = m.size(); idx < cnt; idx++){

                fv3.Model.Element object = m.get(idx);

                if (object instanceof Vertex){

                    float[] v = mm.transform(((Vertex)object).copy());

                    float x = v[X];
                    float y = v[Y];
                    float z = v[Z];

                    if (x != x)
                        throw new IllegalStateException();
                    else {
                        minX = Math.min(minX, x);
                        maxX = Math.max(maxX, x);
                    }
                    if (y != y)
                        throw new IllegalStateException();
                    else {
                        minY = Math.min(minY, y);
                        maxY = Math.max(maxY, y);
                    }
                    if (z != z)
                        throw new IllegalStateException();
                    else {
                        minZ = Math.min(minZ, z);
                        maxZ = Math.max(maxZ, z);
                    }
                }
                else if (object instanceof fv3.Bounds){

                    fv3.Bounds bounds = (fv3.Bounds)object;

                    minX = Math.min(minX,bounds.getBoundsMinX());
                    maxX = Math.max(maxX,bounds.getBoundsMaxX());
                    minY = Math.min(minY,bounds.getBoundsMinY());
                    maxY = Math.max(maxY,bounds.getBoundsMaxY());
                    minZ = Math.min(minZ,bounds.getBoundsMinZ());
                    maxZ = Math.max(maxZ,bounds.getBoundsMaxZ());
                }
            }
        }
        else {

            for (int idx = 0, cnt = m.size(); idx < cnt; idx++){

                fv3.Model.Element object = m.get(idx);

                if (object instanceof Vertex){

                    Vertex v = (Vertex)object;

                    float x = v.x;
                    float y = v.y;
                    float z = v.z;

                    if (x != x)
                        throw new IllegalStateException();
                    else {
                        minX = Math.min(minX, x);
                        maxX = Math.max(maxX, x);
                    }
                    if (y != y)
                        throw new IllegalStateException();
                    else {
                        minY = Math.min(minY, y);
                        maxY = Math.max(maxY, y);
                    }
                    if (z != z)
                        throw new IllegalStateException();
                    else {
                        minZ = Math.min(minZ, z);
                        maxZ = Math.max(maxZ, z);
                    }
                }
                else if (object instanceof fv3.Bounds){

                    fv3.Bounds bounds = (fv3.Bounds)object;

                    minX = Math.min(minX,bounds.getBoundsMinX());
                    maxX = Math.max(maxX,bounds.getBoundsMaxX());
                    minY = Math.min(minY,bounds.getBoundsMinY());
                    maxY = Math.max(maxY,bounds.getBoundsMaxY());
                    minZ = Math.min(minZ,bounds.getBoundsMinZ());
                    maxZ = Math.max(maxZ,bounds.getBoundsMaxZ());
                }
            }
        }

        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;

        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;

        this.midX = (minX + maxX)/2.0f;
        this.midY = (minY + maxY)/2.0f;
        this.midZ = (minZ + maxZ)/2.0f;
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
    public String toString(){
        return this.toString("","\n");
    }
    public String toString(String pr){
        return this.toString(pr,"\n");
    }
    public String toString(String pr, String in){
        if (null == pr)
            pr = "";
        if (null == in)
            in = "";

        return String.format("%s%30.26f %30.26f %30.26f%s%s%30.26f %30.26f %30.26f%s%s%30.26f %30.26f %30.26f", 
                             pr, minX, minY, minZ, 
                             in, pr, midX, midY, midZ, 
                             in, pr, maxX, maxY, maxZ);
    }
}
