/*
 * fv3.math
 * Copyright (C) 2012, John Pritchard, all rights reserved.
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package fv3.math;

/**
 * An (X,Y,Z) vertices buffer with tools.  
 * 
 * The vertices and normals buffers may be resized using the
 * "countVertices" method.  This operation expands or contracts the
 * vertics and normals buffers, preserving information.
 * 
 * @see Abstract
 * @author jdp
 */
public class VertexArray
    extends Abstract
    implements fv3.math.Fv3,
               fv3.Model.Element,
               fv3.Bounds,
               java.lang.Cloneable
{
    public enum Type {
        Points, Lines, LineStrip, LineLoop, Triangles, TriangleStrip, TriangleFan, Quads, QuadStrip, Polygon;


        public final static Type For(int index){
            switch (index){
            case 0:
                return Points;
            case 1:
                return Lines;
            case 2:
                return LineStrip;
            case 3:
                return LineLoop;
            case 4:
                return Triangles;
            case 5:
                return TriangleStrip;
            case 6:
                return TriangleFan;
            case 7:
                return Quads;
            case 8:
                return QuadStrip;
            case 9:
                return Polygon;
            default:
                throw new IllegalArgumentException(String.valueOf(index));
            }
        }

        public boolean hasFaces(){
            switch(this){
            case Points:
            case Lines:
            case LineStrip:
            case LineLoop:
                return false;
            case Triangles:
            case TriangleStrip:
            case TriangleFan:
            case Quads:
            case QuadStrip:
                return true;
            case Polygon:
                return true;
            default:
                throw new IllegalStateException();
            }
        }
    }

    protected volatile Type type;

    protected volatile int countVertices;

    protected volatile float[] vertices;

    protected volatile int countFaces;

    protected volatile float[] normals;

    protected volatile boolean useNormals, redefine;

    protected volatile fv3.Bounds bounds;


    /**
     * @param count Number of verteces in this triangle strip
     */
    public VertexArray(int count){
        this(null,count);
    }
    /**
     * @param type Use of verteces as for calculating normals
     */
    public VertexArray(Type type){
        this(type,0);
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
        this.vertices = new float[3 * count];

        this.countFaces = CountFaces(this.type,count);
        if (0 < this.countFaces)
            this.normals = new float[this.vertices.length];
    }
    public VertexArray(VertexArray src){
        this(src.type,src);
    }
    public VertexArray(Type type, VertexArray src){
        super();

        if (null == type)
            this.type = Type.TriangleStrip;
        else
            this.type = type;

        this.vertices = src.vertices(this.type);
        this.countVertices = (this.vertices.length / 3);

        this.countFaces = CountFaces(this.type,this.countVertices);
        if (0 < this.countFaces){
            this.normals = new float[this.vertices.length];
            this.computeNormals();
        }
    }
    public VertexArray(Type type, float[] vertices){
        super();

        if (null == type)
            this.type = Type.TriangleStrip;
        else
            this.type = type;

        this.vertices = vertices;
        this.countVertices = (this.vertices.length / 3);

        this.countFaces = CountFaces(this.type,this.countVertices);
        if (0 < this.countFaces){
            this.normals = new float[this.vertices.length];
            this.computeNormals();
        }
    }


    public VertexArray clone(){
        try {
            VertexArray clone = (VertexArray)super.clone();
            clone.vertices = clone.vertices.clone();
            if (null != this.normals)
                clone.normals = clone.normals.clone();

            return clone;
        }
        catch (java.lang.CloneNotSupportedException exc){
            throw new InternalError();
        }
    }
    public boolean needsRedefine(){
        boolean re = this.redefine;
        if (re)
            this.redefine = false;
        return re;
    }
    /**
     * Concatencate vertex lists over points, lines, triangles and
     * quads.
     */
    public VertexArray add(VertexArray that){

        this.bounds = null;

        switch(this.type){
        case Points:
        case Lines:
        case Triangles:
        case Quads:

            float[] thatV = that.vertices(this.type);
            {
                final int thisL = (this.vertices.length);
                final int thisC = (thisL/3);
                final int thatL = (thatV.length);
                final int thatC = (thatL/3);

                this.countVertices(thisC+thatC);

                System.arraycopy(thatV,0,this.vertices,thisL,thatL);
            }
            return this;

        default:
            throw new IllegalStateException(this.type.toString());
        }
    }
    public VertexArray transform(Matrix m){

        this.bounds = null;

        float[] vertices = this.vertices;

        for (int index = 0, count = vertices.length; index < count; index += 3){

            m.transform(vertices,index);
        }

        return this;
    }
    public VertexArray translate(float dx, float dy, float dz){

        if (ZERO != dx || ZERO != dy || ZERO != dz){

            this.bounds = null;

            float[] vertices = this.vertices;

            for (int index = 0, count = vertices.length; index < count; ){

                vertices[index++] += dx;
                vertices[index++] += dy;
                vertices[index++] += dz;
            }
        }
        return this;
    }
    public VertexArray translateX(float dx){

        if (ZERO != dx){

            this.bounds = null;

            float[] vertices = this.vertices;

            for (int index = X, count = vertices.length; index < count; index += 3){

                vertices[index] += dx;
            }
        }
        return this;
    }
    public VertexArray translateY(float dy){

        if (ZERO != dy){

            this.bounds = null;

            float[] vertices = this.vertices;

            for (int index = Y, count = vertices.length; index < count; index += 3){

                vertices[index] += dy;
            }
        }
        return this;
    }
    public VertexArray translateZ(float dz){

        if (ZERO != dz){

            this.bounds = null;

            float[] vertices = this.vertices;

            for (int index = Z, count = vertices.length; index < count; index += 3){

                vertices[index] += dz;
            }
        }
        return this;
    }
    public VertexArray scale(float x, float y, float z){

        return this.transform(new Matrix().scale(x,y,z));
    }
    public VertexArray rotate(float x, float y, float z){

        return this.transform(new Matrix().rotate(x,y,z));
    }
    public VertexArray rotateX(float x){

        return this.transform(new Matrix().rotateX(x));
    }
    public VertexArray rotateY(float y){

        return this.transform(new Matrix().rotateY(y));
    }
    public VertexArray rotateZ(float z){

        return this.transform(new Matrix().rotateZ(z));
    }
    public int countVertices(){
        return this.countVertices;
    }
    /**
     * Redefine the buffer size, preserving data.
     * @param count Number of vertices
     */
    public final VertexArray countVertices(int count){
        if (1 > count){
            this.bounds = null;
            this.countVertices = 0;
            this.vertices = new float[0];
            this.countFaces = CountFaces(this.type,0);
            this.normals = null;
        }
        else {
            if (count != this.countVertices){
                this.bounds = null;
                {
                    int many = Math.min(count,this.countVertices);
                    float[] vertices = new float[3 * count];
                    if (0 < many){
                        many *= 3;
                        System.arraycopy(this.vertices,0,vertices,0,many);
                    }
                    this.vertices = vertices;
                }
                this.countVertices = count;

                this.countFaces = CountFaces(this.type,count);
                if (0 < this.countFaces){
                    int many = (null != this.normals)?(this.normals.length):(0);
                    float[] normals = new float[this.vertices.length];
                    if (0 < many){
                        System.arraycopy(this.normals,0,normals,0,many);
                    }
                    this.normals = normals;
                }
                else
                    this.normals = null;
            }
        }
        return this;
    }
    /**
     * @param index Vertex index
     * @return Copy of the three values in the referenced vertex.
     */
    public final float[] getVertex(int index){

        return this.copyVertex(index,(new float[3]),0);
    }
    public final float[] copyVertex(int index, float[] re, int ofs){

        final int start = (3 * index);

        System.arraycopy(this.vertices,start,re,ofs,3);
        return re;
    }
    public final VertexArray setVertex(int index, Vector vertex){

        return this.setVertex(index,vertex.array());
    }
    /**
     * @param index Vertex index
     * @param vertex Array of three values to copy into the referenced vertex.
     * @return This
     */
    public final VertexArray setVertex(int index, float[] vertex){

        final int start = (3 * index);

        System.arraycopy(vertex,0,this.vertices,start,3);
        return this;
    }
    public final VertexArray setVertex(int index, float x, float y, float z){

        final int X = (3 * index);
        final int Y = (X + 1);
        final int Z = (Y + 1);

        this.vertices[X] = x;
        this.vertices[Y] = y;
        this.vertices[Z] = z;

        return this;
    }
    /**
     * @param index Vertex index
     * @param vertex Array of three values to copy into the referenced vertex.
     * @param ofs Offset into vertex array 
     * @return This
     */
    public final VertexArray setVertex(int index, float[] vertex, int ofs){

        final int start = (3 * index);

        System.arraycopy(vertex,ofs,this.vertices,start,3);
        return this;
    }
    /**
     * @param index Vertex index
     * @param vertex Array of three values to copy into the referenced vertex.
     * @param ofs Offset into vertex array 
     * @param count Number of vertices from vertex array to copy into
     * <code>(index, index+1, ..., index+count-1)</code>
     * @return This
     */
    public final VertexArray setVertices(int index, float[] vertex, int ofs, int count){

        final int start = (3 * index);
        final int many = (3 * count);

        System.arraycopy(vertex,ofs,this.vertices,start,many);
        return this;
    }
    public final VertexArray addVertex(float x, float y, float z){
        return this.addVertex(new float[]{x,y,z});
    }
    public final VertexArray addVertex(float[] vertex){

        final int thisL = (this.vertices.length);
        final int thisC = (thisL/3);

        this.countVertices(thisC+1);

        System.arraycopy(vertex,0,this.vertices,thisL,3);

        return this;
    }
    public final float[] array(){
        return this.vertices;
    }
    public final boolean hasFaces(){
        return (0 < this.countFaces);
    }
    public final boolean hasNotFaces(){
        return (0 == this.countFaces);
    }
    public final int countFaces(){
        return this.countFaces;
    }
    /**
     * Array
     */
    public final float[] getFace(int face){
        int[] vertices = this.faceIndeces(face);

        int n = (3 * vertices.length);

        float[] faceV = new float[n];

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
    public final float[] getNormal(int index){

        return this.copyNormal(index,(new float[3]),0);
    }
    public final float[] copyNormal(int index, float[] re, int ofs){
        if (this.useNormals){
            int start = (3 * index);
            System.arraycopy(this.normals,start,re,ofs,3);
            return re;
        }
        else
            return null;
    }
    public final VertexArray setNormal(int index, Vector vector){

        return this.setNormal(index,vector.array());
    }
    /**
     * @param index Normal index
     * @param vector Array of three values to copy into the referenced vector.
     * @return This
     */
    public final VertexArray setNormal(int index, float[] vector){
        this.useNormals = true;

        int start = (3 * index);
        System.arraycopy(vector,0,this.normals,start,3);
        return this;
    }
    /**
     * Compute a normal for a face.
     * @param face Index from zero into faces.
     */
    public final VertexArray computeNormal(int face){
        this.useNormals = true;

        int[] vertices = this.faceIndeces(face);
        if (3 > vertices.length)
            throw new IllegalStateException(this.type.toString());
        else {
            Vector va = new Vector(this.getVertex(vertices[0]));
            Vector vb = new Vector(this.getVertex(vertices[1]));
            Vector vc = new Vector(this.getVertex(vertices[2]));
            float[] normal = va.normal(vb,vc).array();
            this.setNormal(vertices[0],normal);
            this.setNormal(vertices[1],normal);
            this.setNormal(vertices[2],normal);
            return this;
        }
    }
    public final VertexArray computeNormals(){

        switch(this.type){
        case Points:
        case Lines:
        case LineStrip:
        case LineLoop:

            this.useNormals = false;
            return this;

        case Triangles:
        case TriangleStrip:
        case TriangleFan:
        case Quads:
        case QuadStrip:
        case Polygon:

            this.useNormals = true;

            for (int face = 0, count = this.countFaces; face < count; face++){

                int[] vertices = this.faceIndeces(face);

                Vector va = new Vector(this.getVertex(vertices[0]));
                Vector vb = new Vector(this.getVertex(vertices[1]));
                Vector vc = new Vector(this.getVertex(vertices[2]));

                float[] normal = va.normal(vb,vc).array();

                this.setNormal(vertices[0],normal);
                this.setNormal(vertices[1],normal);
                this.setNormal(vertices[2],normal);
            }
            return this;

        default:
            throw new IllegalStateException();
        }
    }
    public final float[] normals(){
        return this.normals;
    }
    /**
     * Indeces for features composed of vertices, i.e. "face" extended
     * to all types including points and lines.
     * @param face Index counting from zero
     * @return Indeces for verteces in this face
     */
    public final int[] faceIndeces(int face){
        return FaceIndeces(this.type,face);
    }
    public final float[] vertices(Type thatType){
        if (null == thatType)
            throw new IllegalArgumentException();

        else if (thatType == this.type)

            return this.vertices.clone();

        else if (this.type.hasFaces() && thatType.hasFaces()){

            final int thatCountVertices = CountVertices(thatType,this.type,this.countVertices);

            float[] thatVertices = new float[3 * thatCountVertices];
            
            for (int face = 0, count = this.countFaces; face < count; face++){

                float[] thisFace = this.getFace(face);

                switch(this.type){

                case Triangles:

                    switch(thatType){

                    case TriangleFan:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));

                    case TriangleStrip:{
                        int[] next = FaceIndeces(thatType,face);
                        if (0 == face){
                            
                            SetVertex(next[0], thisFace, 0, thatVertices);
                            SetVertex(next[1], thisFace, 3, thatVertices);
                            SetVertex(next[2], thisFace, 6, thatVertices);
                        }
                        else {
                            int[] prev = FaceIndeces(thatType,(face-1));

                            if (StripMatch(next,prev)){

                                SetVertex(next[0], thisFace, 0, thatVertices);
                                SetVertex(next[1], thisFace, 3, thatVertices);
                                SetVertex(next[2], thisFace, 6, thatVertices);
                            }
                            else {
                                /*
                                 * Maybe this is a perspective to
                                 * start from for looking into
                                 * http://steve.hollasch.net/cgindex/geometry/gridmesh.c
                                 */
                                throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                            }
                        }
                        break;
                    }
                    case Quads:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    case QuadStrip:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    case Polygon:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    default:
                        throw new IllegalStateException();
                    }
                    break;

                case TriangleStrip:

                    switch(thatType){

                    case Triangles:{

                        int[] next = FaceIndeces(thatType,face);
                        SetVertex(next[0], thisFace, 0, thatVertices);
                        SetVertex(next[1], thisFace, 3, thatVertices);
                        SetVertex(next[2], thisFace, 6, thatVertices);
                        break;
                    }
                    case TriangleFan:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));

                    case Quads:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    case QuadStrip:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    case Polygon:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    default:
                        throw new IllegalStateException();
                    }
                    break;


                case TriangleFan:

                    switch(thatType){

                    case Triangles:{

                        int[] indeces = FaceIndeces(thatType,face);
                        SetVertex(indeces[0], thisFace, 0, thatVertices);
                        SetVertex(indeces[1], thisFace, 3, thatVertices);
                        SetVertex(indeces[2], thisFace, 6, thatVertices);
                        break;
                    }
                    case TriangleStrip:{

                        int[] indeces = FaceIndeces(thatType,face);
                        SetVertex(indeces[0], thisFace, 0, thatVertices);
                        SetVertex(indeces[1], thisFace, 3, thatVertices);
                        SetVertex(indeces[2], thisFace, 6, thatVertices);
                        break;
                    }
                    case Quads:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    case QuadStrip:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    case Polygon:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    default:
                        throw new IllegalStateException();
                    }
                    break;


                case Quads:

                    switch(thatType){

                    case Triangles:{

                        final int thatFaceA = (2 * face);
                        final int thatFaceB = (thatFaceA + 1);

                        int[] indeces;

                        indeces = FaceIndeces(thatType,thatFaceA);
                        SetVertex(indeces[0], thisFace, 0, thatVertices);
                        SetVertex(indeces[1], thisFace, 3, thatVertices);
                        SetVertex(indeces[2], thisFace, 9, thatVertices);

                        indeces = FaceIndeces(thatType,thatFaceB);
                        SetVertex(indeces[0], thisFace, 3, thatVertices);
                        SetVertex(indeces[1], thisFace, 6, thatVertices);
                        SetVertex(indeces[2], thisFace, 9, thatVertices);
                        break;
                    }
                    case TriangleStrip:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    case TriangleFan:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    case QuadStrip:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    case Polygon:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    default:
                        throw new IllegalStateException();
                    }
                    break;


                case QuadStrip:

                    switch(thatType){

                    case Triangles:{

                        final int thatFaceA = (2 * face);
                        final int thatFaceB = (thatFaceA + 1);

                        int[] indeces;

                        indeces = FaceIndeces(thatType,thatFaceA);
                        SetVertex(indeces[0], thisFace, 0, thatVertices);
                        SetVertex(indeces[1], thisFace, 3, thatVertices);
                        SetVertex(indeces[2], thisFace, 9, thatVertices);

                        indeces = FaceIndeces(thatType,thatFaceB);
                        SetVertex(indeces[0], thisFace, 3, thatVertices);
                        SetVertex(indeces[1], thisFace, 6, thatVertices);
                        SetVertex(indeces[2], thisFace, 9, thatVertices);
                        break;
                    }
                    case TriangleStrip:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    case TriangleFan:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));

                    case Quads:{

                        int[] indeces = FaceIndeces(thatType,face);
                        SetVertex(indeces[0], thisFace, 0, thatVertices);
                        SetVertex(indeces[1], thisFace, 3, thatVertices);
                        SetVertex(indeces[2], thisFace, 6, thatVertices);
                        SetVertex(indeces[3], thisFace, 9, thatVertices);
                        break;
                    }
                    case Polygon:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    default:
                        throw new IllegalStateException();
                    }
                    break;


                case Polygon:

                    switch(thatType){

                    case Triangles:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    case TriangleStrip:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    case TriangleFan:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    case Quads:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    case QuadStrip:
                        throw new UnsupportedOperationException(String.format("From %s To %s",this.type,thatType));
                    default:
                        throw new IllegalStateException();
                    }
                    //break;

                default:
                    throw new IllegalStateException();
                }
            }
            return thatVertices;
        }
        else
            throw new IllegalStateException("[TODO] additional conversions");
    }
    public fv3.Bounds getBounds(){
        fv3.Bounds bounds = this.bounds;
        if (null == bounds){
            bounds = new VertexArrayBounds(this);
            this.bounds = bounds;
        }
        return bounds;
    }
    public float getBoundsMinX(){
        return this.getBounds().getBoundsMinX();
    }
    public float getBoundsMidX(){
        return this.getBounds().getBoundsMidX();
    }
    public float getBoundsMaxX(){
        return this.getBounds().getBoundsMaxX();
    }
    public float getBoundsMinY(){
        return this.getBounds().getBoundsMinY();
    }
    public float getBoundsMidY(){
        return this.getBounds().getBoundsMidY();
    }
    public float getBoundsMaxY(){
        return this.getBounds().getBoundsMaxY();
    }
    public float getBoundsMinZ(){
        return this.getBounds().getBoundsMinZ();
    }
    public float getBoundsMidZ(){
        return this.getBounds().getBoundsMidZ();
    }
    public float getBoundsMaxZ(){
        return this.getBounds().getBoundsMaxZ();
    }
    /**
     * Translate this filled vertex array to the upper left coordinate
     * (minX,maxY)
     */
    public final VertexArray fit(float minX, float maxY){
        float tx = (minX - this.getBoundsMinX());
        float ty = (maxY - this.getBoundsMaxY());
        this.translate(tx,ty,ZERO);
        return this;
    }
    /**
     * Fit to box with aspect ratio lock
     */
    public final VertexArray fit(float minX, float maxX, float minY, float maxY){
        return this.fit(minX,maxX,minY,maxY,true);
    }
    /**
     * Fit to box
     */
    public final VertexArray fit(float minX, float maxX, float minY, float maxY, 
                                 boolean aspect)
    {
        final float bMinX = this.getBoundsMinX();
        final float bMaxX = this.getBoundsMaxX();
        final float bMinY = this.getBoundsMinY();
        final float bMaxY = this.getBoundsMaxY();
        final float bMinZ = this.getBoundsMinZ();
        final float bMaxZ = this.getBoundsMaxZ();

        final float tx = (minX - bMinX);
        final float ty = (maxY - bMaxY);

        final float dbX = (bMaxX - bMinX);
        final float dbY = (bMaxY - bMinY);
        float sx, sy;
        if (ZERO != dbX)
            sx = (maxX - minX) / dbX;
        else
            sx = 1.0f;

        if (ZERO != dbY)
            sy = (maxY - minY) / dbY;
        else
            sy = 1.0f;

        if (aspect){
            final float s = Math.min(sx,sy);
            sx = s;
            sy = s;
        }
        Matrix m = new Matrix();
        m.translate(tx,ty,ZERO);
        m.scale(sx,sy,1.0f);
        this.transform(m);
        return this;
    }
    /**
     * Fit to box with aspect ratio lock
     */
    public final VertexArray fit(float minX, float maxX, 
                                 float minY, float maxY, 
                                 float minZ, float maxZ)
    {
        return this.fit(minX,maxX,minY,maxY,minZ,maxZ,true);
    }
    /**
     * Fit to box
     */
    public final VertexArray fit(float minX, float maxX, 
                                 float minY, float maxY, 
                                 float minZ, float maxZ, 
                                 boolean aspect)
    {
        final float bMinX = this.getBoundsMinX();
        final float bMaxX = this.getBoundsMaxX();
        final float bMinY = this.getBoundsMinY();
        final float bMaxY = this.getBoundsMaxY();
        final float bMinZ = this.getBoundsMinZ();
        final float bMaxZ = this.getBoundsMaxZ();

        final float tx = (minX - bMinX);
        final float ty = (maxY - bMaxY);
        final float tz = (maxZ - bMinZ);

        final float dbX = (bMaxX - bMinX);
        final float dbY = (bMaxY - bMinY);
        final float dbZ = (bMaxZ - bMinZ);

        float sx, sy, sz;
        if (ZERO != dbX)
            sx = (maxX - minX) / dbX;
        else
            sx = 1.0f;

        if (ZERO != dbY)
            sy = (maxY - minY) / dbY;
        else
            sy = 1.0f;

        if (ZERO != dbZ)
            sz = (maxZ - minZ) / dbZ;
        else
            sz = 1.0f;

        if (aspect){
            final float s = Math.min(sx,Math.min(sy,sz));
            sx = s;
            sy = s;
            sz = s;
        }
        Matrix m = new Matrix();
        m.translate(tx,ty,tz);
        m.scale(sx,sy,sz);
        this.transform(m);
        return this;
    }
    /**
     * Fit to target point and "scale range".
     */
    public final VertexArray fit2(float pX, float pY, float pZ,
                                  float rX, float rY, float rZ)
    {
        return this.fit2(pX,pY,pZ,rX,rY,rZ,true);
    }
    /**
     * Fit to target point and "scale range".
     */
    public final VertexArray fit2(float pX, float pY, float pZ,
                                  float rX, float rY, float rZ,
                                  boolean aspect)
    {
        final float bMinX = this.getBoundsMinX();
        final float bMaxX = this.getBoundsMaxX();
        final float bMinY = this.getBoundsMinY();
        final float bMaxY = this.getBoundsMaxY();
        final float bMinZ = this.getBoundsMinZ();
        final float bMaxZ = this.getBoundsMaxZ();

        final float tx = (pX - bMinX);
        final float ty = (pY - bMaxY);
        final float tz = (pZ - bMinZ);

        final float dbX = (bMaxX - bMinX);
        final float dbY = (bMaxY - bMinY);
        final float dbZ = (bMaxZ - bMinZ);

        float sx, sy, sz;
        if (ZERO != dbX)
            sx = rX / dbX;
        else
            sx = 1.0f;

        if (ZERO != dbY)
            sy = rY / dbY;
        else
            sy = 1.0f;

        if (ZERO != dbZ)
            sz = rZ / dbZ;
        else
            sz = 1.0f;

        if (aspect){
            final float s = Math.min(sx,Math.min(sy,sz));
            sx = s;
            sy = s;
            sz = s;
        }
        Matrix m = new Matrix();
        m.translate(tx,ty,tz);
        m.scale(sx,sy,sz);
        this.transform(m);
        return this;
    }
    public VertexArrayProfile profileXY(){
        return new VertexArrayProfile.XY(this,ZERO);
    }
    public VertexArrayProfile profileXY(float z){
        return new VertexArrayProfile.XY(this,z);
    }
    public VertexArrayProfile profileXY(float z, float e){
        return new VertexArrayProfile.XY(this,z,e);
    }
    public VertexArrayProfile profileYZ(){
        return new VertexArrayProfile.YZ(this,ZERO);
    }
    public VertexArrayProfile profileYZ(float x){
        return new VertexArrayProfile.YZ(this,x);
    }
    public VertexArrayProfile profileYZ(float x, float e){
        return new VertexArrayProfile.YZ(this,x,e);
    }
    public VertexArrayProfile profileZX(){
        return new VertexArrayProfile.ZX(this,ZERO);
    }
    public VertexArrayProfile profileZX(float y){
        return new VertexArrayProfile.ZX(this,y);
    }
    public VertexArrayProfile profileZX(float y, float e){
        return new VertexArrayProfile.ZX(this,y,e);
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

        StringBuilder string = new StringBuilder();

        string.append(pr);
        string.append(this.type);

        if (0 == this.countFaces){

            final float[] vertices = this.vertices;

            for (int vertex = 0, count = vertices.length; vertex < count; ){

                string.append(in);
                string.append(pr);

                string.append(String.format("%5d",(vertex/3)));
                string.append(' ');
                string.append(String.format("%30.26f",vertices[vertex++]));
                string.append(' ');
                string.append(String.format("%30.26f",vertices[vertex++]));
                string.append(' ');
                string.append(String.format("%30.26f",vertices[vertex++]));
            }
        }
        else {

            for (int face = 0, count = this.countFaces, c, z; face < count; face++){

                string.append(in);
                string.append(pr);

                float[] fary = this.getFace(face);

                for (c = 0, z = fary.length; c < z; c++){
                    if (0 != c){
                        string.append(in);
                        string.append(pr);
                    }
                    string.append(String.format("%5d",face));
                    string.append(' ');
                    string.append(String.format("%30.26f",fary[c++]));
                    string.append(' ');
                    string.append(String.format("%30.26f",fary[c++]));
                    string.append(' ');
                    string.append(String.format("%30.26f",fary[c]));
                }
            }
        }
        return string.toString();
    }

    public final static int CountVertices(Type to, Type from, int fromCountVertices){
        if (to == from)
            return fromCountVertices;
        else {
            int fromCountFaces = CountFaces(from,fromCountVertices);

            switch(from){

            case Triangles:
            case TriangleStrip:
            case TriangleFan:

                switch(to){

                case Triangles:
                case TriangleStrip:
                case TriangleFan:
                    return CountVertices(to,fromCountFaces);

                case Quads:
                case QuadStrip:
                    return CountVertices(to,(fromCountFaces>>1));

                case Polygon:
                default:
                    throw new IllegalStateException();
                }
                //break;

            case Quads:
            case QuadStrip:

                switch(to){

                case Triangles:
                case TriangleStrip:
                case TriangleFan:
                    return CountVertices(to,(fromCountFaces<<1));

                case Quads:
                case QuadStrip:
                    return CountVertices(to,fromCountFaces);

                case Polygon:
                default:
                    throw new IllegalStateException();
                }
                //break;

            case Polygon:
            default:
                throw new IllegalStateException();
            }
        }
    }
    public final static int CountNormals(Type to, Type from, int fromCountFaces){
        if (to == from)
            return fromCountFaces;
        else {
            int fromCountVertices = CountVertices(from,fromCountFaces);
            return CountFaces(to,fromCountVertices);
        }
    }
    public final static int CountVertices(Type to, int countFaces){
        switch(to){

        case Triangles:
            return (3 * countFaces);

        case TriangleStrip:
            return (countFaces+2);

        case TriangleFan:
            return (countFaces+2);

        case Quads:
            return (4 * countFaces);

        case QuadStrip:
            return (2 * (countFaces+1));

        case Polygon:
            return 1;

        default:
            throw new IllegalStateException();
        }
    }
    public final static int CountFaces(Type to, int countVertices){
        switch(to){
        case Points:
        case Lines:
        case LineStrip:
        case LineLoop:
            return 0;

        case Triangles:
            return (countVertices/3);

        case TriangleStrip:
            return (countVertices-2);

        case TriangleFan:
            return (countVertices-2);

        case Quads:
            return (countVertices/4);

        case QuadStrip:
            return (countVertices/2)-1;

        case Polygon:
            return 1;

        default:
            throw new IllegalStateException();
        }
    }
    public final static int[] FaceIndeces(Type type, int face){
        switch(type){
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
            int n = (face);
            int[] v = new int[n];
            for (int x = 0; x < n; x++)
                v[x] = x;
            return v;
        }
        default:
            throw new IllegalStateException();
        }
    }
    public final static void SetVertex(int index, float[] vertex, int ofs, float[] vertices){

        final int start = (3 * index);

        System.arraycopy(vertex,ofs,vertices,start,3);
    }
    public final static boolean StripMatch(int[] thisIndeces, int[] thatIndeces)
    {
        if (thisIndeces.length == thatIndeces.length){
            for (int i = 0, c = -1, j, z = thisIndeces.length; i < z; i++){

                int ii = thisIndeces[i];

                for (j = 0; j < z; j++){

                    int jj = thatIndeces[j];

                    if (ii == jj){

                        if (-1 != c)

                            return (i == (c+1));
                        else
                            c = i;
                    }
                }
            }
            return false;
        }
        else
            throw new IllegalArgumentException(String.format("%d != %d",thisIndeces.length,thatIndeces.length));
    }
}
