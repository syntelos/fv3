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

import javax.media.opengl.GL2;

public final class Vertex
    extends fv3.model.Object
{

    public final double x, y, z;


    public Vertex(double x, double y, double z){
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public double[] copy(){
        return new double[]{x,y,z};
    }
    public void apply(GL2 gl){
        gl.glVertex3d(this.x,this.y,this.z);
    }
    public Object.Type getObjectType(){
        return Object.Type.Vertex;
    }

    public final static Vertex[] Add(Vertex[] list, Vertex item){
        if (null == item)
            return list;
        else if (null == list)
            return new Vertex[]{item};
        else {
            int len = list.length;
            Vertex[] copier = new Vertex[len+1];
            System.arraycopy(list,0,copier,0,len);
            copier[len] = item;
            return copier;
        }
    }
}
