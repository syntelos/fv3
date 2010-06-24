/*
 * fv3.math
 * Copyright (c) 2009 John Pritchard, all rights reserved.
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

import java.nio.DoubleBuffer;

/**
 * A list of (X,Y,Z) verteces with geometric tools.
 * 
 * @see Abstract
 * @author jdp
 */
public class VertexArray
    extends Abstract
{
    public enum Type {
        Points, Lines, LineStrip, LineLoop, Triangles, TriangleStrip, TriangleFan, Quads, QuadStrip, Polygon;
    }

    public final Type type;

    public final int countVertices;

    private final double[] list;

    public final int countFaces;

    private final double[] normals;

    private volatile DoubleBuffer n;


    /**
     * @param count Number of verteces in this triangle strip
     */
    public VertexArray(int count){
        this(null,count);
    }
    /**
     * @param type Use of verteces as for calculating normals
     * @param count Number of verteces in this list.
     */
    public VertexArray(Type type, int count){
        super();
        if (null == type)
            this.type = Type.TriangleStrip;
        else
            this.type = type;

        this.countVertices = count;
        this.list = new double[3 * count];
        switch(this.type){
        case Points:
        case Lines:
        case LineStrip:
        case LineLoop:
            this.countFaces = 0;
            this.normals = null;
            break;
        case Triangles:
            this.countFaces = (count/3);
            this.normals = new double[this.countFaces*3];
            break;
        case TriangleStrip:
            this.countFaces = (count-2);
            this.normals = new double[this.countFaces*3];
            break;
        case TriangleFan:
            this.countFaces = (count-2);
            this.normals = new double[this.countFaces*3];
            break;
        case Quads:
            this.countFaces = (count/4);
            this.normals = new double[this.countFaces*3];
            break;
        case QuadStrip:
            this.countFaces = (count/2)-1;
            this.normals = new double[this.countFaces*3];
            break;
        case Polygon:
            this.countFaces = 1;
            this.normals = new double[this.countFaces*3];
            break;
        default:
            throw new IllegalStateException();
        }
    }


    public final int countVertex(){
        return this.countVertices;
    }
    /**
     * @param index Vertex index
     * @return Copy of the three values in the referenced vertex.
     */
    public final double[] getVertex(int index){

        return this.copyVertex(index,(new double[3]),0);
    }
    public final double[] copyVertex(int index, double[] re, int ofs){

        int start = (3 * index);
        System.arraycopy(this.list,start,re,ofs,3);
        return re;
    }
    /**
     * @param index Vertex index
     * @param vertex Array of three values to copy into the referenced vertex.
     * @return This
     */
    public final VertexArray setVertex(int index, double[] vertex){

        int start = (3 * index);
        System.arraycopy(vertex,0,this.list,start,3);
        return this;
    }
    public final double[] array(){
        return this.list;
    }
    public final int countFaces(){
        return this.countFaces;
    }
    /**
     * Array
     */
    public final double[] getFace(int face){
        int[] vertices = this.faceIndeces(face);

        int n = (3 * vertices.length);

        double[] faceV = new double[n];

        for (int x = 0, z = vertices.length; x < z; x++){

            this.copyVertex(vertices[x],faceV,(3*x));
        }
        return faceV;
    }
    public final int countNormal(){
        return this.countFaces;
    }
    /**
     * @param index Normal index
     * @return Copy of the three values in the referenced normal.
     */
    public final double[] getNormal(int index){

        return this.copyNormal(index,(new double[3]),0);
    }
    public final double[] copyNormal(int index, double[] re, int ofs){

        int start = (3 * index);
        System.arraycopy(this.normals,start,re,ofs,3);
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
    /**
     * Compute a normal for a face.
     * @param face Index from zero into faces.
     */
    public final VertexArray computeNormal(int face){

        int[] vertices = this.faceIndeces(face);
        if (3 > vertices.length)
            throw new IllegalStateException(this.type.toString());
        else {
            Vector va = new Vector(this.getVertex(vertices[0]));
            Vector vb = new Vector(this.getVertex(vertices[1]));
            Vector vc = new Vector(this.getVertex(vertices[2]));
            Vector normal = va.normal(vb,vc);
            return this.setNormal(face,normal.array());
        }
    }
    public final double[] normals(){
        return this.normals;
    }
    public DoubleBuffer normalsBuffer(){
        DoubleBuffer n = this.n;
        if (null == n){
            n = DoubleBuffer.wrap(this.normals());
            this.n = n;
        }
        return n;
    }
    /**
     * Indeces for features composed of vertices, i.e. "face" extended
     * to all types including points and lines.
     * @param face Index counting from zero
     * @return Indeces for verteces in this face
     */
    public final int[] faceIndeces(int face){
        switch(this.type){
        case Points:
            return new int[]{face};
        case Lines:{
            int a = (2 * face);
            int b = (a + 1);

            return new int[]{a,b};
        }
        case LineStrip:{
            int a = (face);
            int b = (a + 1);

            return new int[]{a,b};
        }
        case LineLoop:{
            int a = (face);
            int b = (a + 1);

            return new int[]{a,b};
        }
        case Triangles:{

            int n = (3 * face);
            int a = (n);
            int b = (n + 1);
            int c = (n + 2);

            return new int[]{a,b,c};
        }
        case TriangleStrip:{
            int a, b, c;
            int n = face;
            if (1 == (n&1)){
                a = (n);
                b = (n + 1);
                c = (n + 2);
            }
            else {
                a = (n + 1);
                b = (n);
                c = (n + 2);
            }
            return new int[]{a,b,c};
        }
        case TriangleFan:{

            int n = face;
            int a = (0);
            int b = (n + 1);
            int c = (n + 2);

            return new int[]{a,b,c};
        }
        case Quads:{

            int n = (4 * face);
            int a = (n);
            int b = (n + 1);
            int c = (n + 2);
            int d = (n + 3);

            return new int[]{a,b,c,d};
        }
        case QuadStrip:{

            int n = (2 * face);
            int a = (n);
            int b = (n + 1);
            int c = (n + 3);
            int d = (n + 2);

            return new int[]{a,b,c,d};
        }
        case Polygon:{
            int n = this.countVertices;
            int[] v = new int[n];
            for (int x = 0; x < n; x++)
                v[x] = x;
            return v;
        }
        default:
            throw new IllegalStateException();
        }
    }
}
