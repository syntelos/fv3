/*
 * fv3.math
 * Copyright (c) 2009 John Pritchard, all rights reserved.
 * Portions Copyright (C) 1996-2008 by Jan Eric Kyprianidis, all rights reserved.
 *     
 * This program is free  software: you can redistribute it and/or modify 
 * it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program  is  distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU Lesser General Public License for more details.
 * 
 * You should  have received a copy of the GNU Lesser General Public License
 * along with  this program; If not, see <http://www.gnu.org/licenses/>. 
 */
package fv3.math;

/**
 * A list of (X,Y,Z) verteces in geometric three- space.
 * 
 * @see Abstract
 * @author jdp
 */
public class VertexArray
    extends Abstract
{

    private final int count;
    private final double[] list;

    private volatile int faces;
    private volatile double[] normals;


    /**
     * @param count Number of verteces in this list.
     */
    public VertexArray(int count){
        super();
        this.count = count;
        this.list = new double[3 * count];
    }


    /**
     * @param index Vertex index
     * @return Copy of the three values in the referenced vertex.
     */
    public final double[] get(int index){

        return this.get(index,(new double[3]));
    }
    public final double[] get(int index, double[] re){

        int start = (3 * index);
        System.arraycopy(this.list,start,re,0,3);
        return re;
    }
    /**
     * @param index Vertex index
     * @param vertex Array of three values to copy into the referenced vertex.
     * @return This
     */
    public final VertexArray set(int index, double[] vertex){

        int start = (3 * index);
        System.arraycopy(vertex,0,this.list,start,3);
        return this;
    }
    public final double[] array(){
        return this.list;
    }
    /**
     * @param index Normal index
     * @return Copy of the three values in the referenced normal.
     */
    public final double[] getNormal(int index){

        return this.get(index,(new double[3]));
    }
    public final double[] getNormal(int index, double[] re){

        int start = (3 * index);
        System.arraycopy(this.normals,start,re,0,3);
        return re;
    }
    /**
     * @param index Normal index
     * @param vertex Array of three values to copy into the referenced vertex.
     * @return This
     */
    public final VertexArray setNormal(int index, double[] vertex){

        int start = (3 * index);
        System.arraycopy(vertex,0,this.normals,start,3);
        return this;
    }
    public final double[] normals(){
        return this.normals;
    }
    public final VertexArray faces(int count){
        this.faces = count;
        this.normals = new double[3 * count];

        return this;
    }
    public final VertexArray face(int index, int a, int b, int c){
        Vector va = new Vector(this.get(a));
        Vector vb = new Vector(this.get(b));
        Vector vc = new Vector(this.get(c));
        Vector normal = va.normal(vb,vc);
        return this.setNormal(index,normal.array());
    }
}
