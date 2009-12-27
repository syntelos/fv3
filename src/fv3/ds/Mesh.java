/*
 * fv3.ds
 * Copyright (c) 2009 John Pritchard, all rights reserved.
 * Copyright (C) 1996-2008 by Jan Eric Kyprianidis, all rights reserved.
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
/*
 * Based on the work of Jan Eric Kyprianidis,  Martin van Velsen, Robin
 * Feroq, Jimm Pitts, Mats Byggmästar, and Josh DeFord.
 */
package fv3.ds;

import fv3.Fv3Exception;

/**
 *
 */
public final class Mesh
    extends Object
{
    public final String name;
    public int       user_id;
    public Object    user_ptr;
    public int       object_flags;                 /** @see ObjectFlags */ 
    public int       color;                        /** Index to editor palette [0..255] */
    public float[][] matrix = new float[4][4];     /** Transformation matrix for mesh data */
    public float[][] vertices = {};
    public float[][] texcos = {};
    public int[]     vflags = {};
    public Face[]    faces = {};
    public String    box_front;
    public String    box_back;
    public String    box_left;
    public String    box_right;
    public String    box_top;
    public String    box_bottom;
    public MapType   map_type = MapType.NONE;
    public float[]   map_pos = new float[3];
    public float[][] map_matrix = new float[4][4];
    public float     map_scale;
    public float[]   map_tile = {0f,0f};
    public float[]   map_planar_size = {0f,0f};
    public float     map_cylinder_height;

    public double minX, maxX, minY, maxY, minZ, maxZ;

    private volatile float[][] normals;




    public Mesh(Model model, Reader r, Chunk cp, String name)
        throws Fv3Exception
    {
        super();
        this.name = name;
        this.read(model,r,cp);

        double minX = 0.0, minY = 0.0, minZ = 0.0, maxX = 0.0, maxY = 0.0, maxZ = 0.0;

        for (int cc = 0, count = this.vertices.length; cc < count; ++cc) {
            float[] vertex = this.vertices[cc];
            double x = vertex[0];
            double y = vertex[1];
            double z = vertex[2];
            if (0 == cc){
                minX = x;
                maxX = x;
                minY = y;
                maxY = y;
                minZ = z;
                maxZ = z;
            }
            else {
                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
                minZ = Math.min(minZ, z);
                maxZ = Math.max(maxZ, z);
            }
        }
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }


    public float[][] normals(){
        float[][] normals = this.normals;
        if (null == normals){
            int count = this.faces.length;
            normals = new float[count][3] ;
            for (int cc = 0; cc < count; ++cc){
                Normal(normals[cc],
                       this.vertices[this.faces[cc].index[0]],
                       this.vertices[this.faces[cc].index[1]],
                       this.vertices[this.faces[cc].index[2]]);
            }
            this.normals = normals;
        }
        return normals;
    }
    public void read(Model model, Reader r, Chunk cp)
        throws Fv3Exception
    {
        while (cp.in()){
            Chunk cp2 = r.next(cp);
            switch(cp2.id){
            case Chunk.MESH_MATRIX: {
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 3; j++) {
                        this.matrix[i][j] = r.readFloat(cp2);
                    }
                }
                break;
            }
            case Chunk.MESH_COLOR: {
                this.color = r.readU8(cp2);
                break;
            }
            case Chunk.POINT_ARRAY: {
                int count = r.readU16(cp2);
                this.vertices = Resize(this.vertices,count);
                if (0 != this.texcos.length)
                    this.texcos = Resize(this.texcos,count);
                if (0 != this.vflags.length)
                    this.vflags = Resize(this.vflags,count);
                for (int i = 0; i < count; ++i) {
                    r.readVector(cp2, this.vertices[i]);
                }
                break;
            }

            case Chunk.POINT_FLAG_ARRAY: {
                int nflags = r.readU16(cp2);
                int count = ((this.vertices.length >= nflags)?(this.vertices.length):(nflags));
                this.vertices = Resize(this.vertices,count);
                if (0 != this.texcos.length)
                    this.texcos = Resize(this.texcos,count);
                this.vflags = Resize(this.vflags,count);
                for (int i = 0; i < nflags; ++i) {
                    this.vflags[i] = r.readU16(cp2);
                }
                break;
            }

            case Chunk.FACE_ARRAY: {
                int nfaces = r.readU16(cp2);
                this.faces = Face.New(nfaces);
                for (int cc = 0; cc < nfaces; cc++){
                    Face face = this.faces[cc];
                    face.index[0] = r.readU16(cp2);
                    face.index[1] = r.readU16(cp2);
                    face.index[2] = r.readU16(cp2);
                    face.flags = r.readU16(cp2);
                }
                while (cp2.in()){
                    Chunk cp3 = r.next(cp2);
                    switch (cp3.id){
                    case Chunk.MSH_MAT_GROUP: {
                        String name = r.readString(cp3);
                        int material = model.indexOfMaterialForName(name);
                        int n = r.readU16(cp3);
                        for (int cc = 0; cc < n; ++cc) {
                            int index = r.readU16(cp3);
                            if (index < nfaces) {
                                this.faces[index].material = material;
                            } 
                        }
                        break;
                    }
                    case Chunk.SMOOTH_GROUP: {
                        for (int i = 0; i < nfaces; ++i) {
                            this.faces[i].smoothing_group = r.readS32(cp3);
                        }
                        break;
                    }
                    case Chunk.MSH_BOXMAP: {
                        this.box_front = r.readString(cp3);
                        this.box_back = r.readString(cp3);
                        this.box_left = r.readString(cp3);
                        this.box_right = r.readString(cp3);
                        this.box_top = r.readString(cp3);
                        this.box_bottom = r.readString(cp3);
                        break;
                    }
                    }
                }
                break;
            }
            case Chunk.MESH_TEXTURE_INFO: {

                //FIXME: this.map_type = r.readU16(cp2);

                for (int i = 0; i < 2; ++i) {
                    this.map_tile[i] = r.readFloat(cp2);
                }
                for (int i = 0; i < 3; ++i) {
                    this.map_pos[i] = r.readFloat(cp2);
                }
                this.map_scale = r.readFloat(cp2);

                Identity(this.map_matrix);
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 3; j++) {
                        this.map_matrix[i][j] = r.readFloat(cp2);
                    }
                }
                for (int i = 0; i < 2; ++i) {
                    this.map_planar_size[i] = r.readFloat(cp2);
                }
                this.map_cylinder_height = r.readFloat(cp2);
                break;
            }

            case Chunk.TEX_VERTS: {
                int ntexcos = r.readU16(cp2);
                int count = ((this.vertices.length >= ntexcos)?(this.vertices.length):(ntexcos));
                this.vertices = Resize(this.vertices,count);
                this.texcos = Resize(this.texcos,count);
                this.vflags = Resize(this.vflags,count);
                for (int cc = 0; cc < ntexcos; ++cc) {
                    float[] texcos = this.texcos[cc];
                    texcos[0] = r.readFloat(cp2);
                    texcos[1] = r.readFloat(cp2);
                }
                break;
            }

            }
        }

        if (Det(this.matrix) < 0.0) {
            /* Flip X coordinate of vertices if mesh matrix
             * has negative determinant
             */
            float[][] inv_matrix = new float[4][4], M = new float[4][4];
            float[] tmp = new float[3];

            Copy(inv_matrix, this.matrix);
            Inv(inv_matrix);
            Copy(M, this.matrix);
            Scale(M, -1.0f, 1.0f, 1.0f);
            Mult(M, M, inv_matrix);

            for (int i = 0, z = this.vertices.length; i < z; ++i) {
                Transform(tmp, M, this.vertices[i]);
                Copy(this.vertices[i], tmp);
            }
        }

    }

    /*
     */
    private final static float EPSILON = (float)1e-5;

    private final static float[][] Identity = {
        {1,0,0,0},
        {0,1,0,0},
        {0,0,1,0},
        {0,0,0,1}
    };

    private final static int[] Resize(int[] list, int size){
        if (null == list)
            return new int[size];
        else {
            int length = list.length;
            if (size == length)
                return list;
            else {
                int[] copier = new int[size];
                System.arraycopy(list,0,copier,0,length);
                return copier;
            }
        }
    }
    private final static float[][] Resize(float[][] list, int size){
        if (null == list)
            return new float[size][3];
        else {
            int length = list.length;
            if (size == length)
                return list;
            else {
                float[][] copier = new float[size][3];
                System.arraycopy(list,0,copier,0,length);
                return copier;
            }
        }
    }
    private final static void Identity(float[][] m){
        System.arraycopy(Identity[0],0,m[0],0,4);
        System.arraycopy(Identity[1],0,m[1],0,4);
        System.arraycopy(Identity[2],0,m[2],0,4);
        System.arraycopy(Identity[3],0,m[3],0,4);
    }
    private final static float[][] Copy(float[][] dst, float[][] src){
        System.arraycopy(src[0],0,dst[0],0,4);
        System.arraycopy(src[1],0,dst[1],0,4);
        System.arraycopy(src[2],0,dst[2],0,4);
        System.arraycopy(src[3],0,dst[3],0,4);
        return dst;
    }
    private final static float Det2x2( float a, float b, 
                                       float c, float d)
    {
        return ((a)*(d) - (b)*(c));
    }
    private final static float Det3x3(float a1, float a2, float a3,
                                float b1, float b2, float b3,
                                float c1, float c2, float c3)
    {
        return (a1*Det2x2(b2, b3, c2, c3) -
               b1*Det2x2(a2, a3, c2, c3) +
               c1*Det2x2(a2, a3, b2, b3));
    }
    /**
     * Find determinant of a matrix.
     */
    private final static float Det(float m[][]) {
        float a1, a2, a3, a4, b1, b2, b3, b4, c1, c2, c3, c4, d1, d2, d3, d4;

        a1 = m[0][0];
        b1 = m[1][0];
        c1 = m[2][0];
        d1 = m[3][0];
        a2 = m[0][1];
        b2 = m[1][1];
        c2 = m[2][1];
        d2 = m[3][1];
        a3 = m[0][2];
        b3 = m[1][2];
        c3 = m[2][2];
        d3 = m[3][2];
        a4 = m[0][3];
        b4 = m[1][3];
        c4 = m[2][3];
        d4 = m[3][3];
        return (a1 * Det3x3(b2, b3, b4, c2, c3, c4, d2, d3, d4) -
                b1 * Det3x3(a2, a3, a4, c2, c3, c4, d2, d3, d4) +
                c1 * Det3x3(a2, a3, a4, b2, b3, b4, d2, d3, d4) -
                d1 * Det3x3(a2, a3, a4, b2, b3, b4, c2, c3, c4));
    }
    private final static boolean Inv(float m[][]) {
        int i, j, k;
        int[] pvt_i = new int[4], pvt_j = new int[4];            /* Locations of pivot elements */
        float pvt_val;               /* Value of current pivot element */
        float hold;                  /* Temporary storage */
        float determinat;

        determinat = 1.0f;
        for (k = 0; k < 4; k++)  {
            /* Locate k'th pivot element */
            pvt_val = m[k][k];          /* Initialize for search */
            pvt_i[k] = k;
            pvt_j[k] = k;
            for (i = k; i < 4; i++) {
                for (j = k; j < 4; j++) {
                    if (Math.abs(m[i][j]) > Math.abs(pvt_val)) {
                        pvt_i[k] = i;
                        pvt_j[k] = j;
                        pvt_val = m[i][j];
                    }
                }
            }

            /* Product of pivots, gives determinant when finished */
            determinat *= pvt_val;
            if (Math.abs(determinat) < EPSILON) {
                return false;  /* Matrix is singular (zero determinant) */
            }

            /* "Interchange" rows (with sign change stuff) */
            i = pvt_i[k];
            if (i != k) {             /* If rows are different */
                for (j = 0; j < 4; j++) {
                    hold = -m[k][j];
                    m[k][j] = m[i][j];
                    m[i][j] = hold;
                }
            }

            /* "Interchange" columns */
            j = pvt_j[k];
            if (j != k) {            /* If columns are different */
                for (i = 0; i < 4; i++) {
                    hold = -m[i][k];
                    m[i][k] = m[i][j];
                    m[i][j] = hold;
                }
            }

            /* Divide column by minus pivot value */
            for (i = 0; i < 4; i++) {
                if (i != k) m[i][k] /= (-pvt_val) ;
            }

            /* Reduce the matrix */
            for (i = 0; i < 4; i++) {
                hold = m[i][k];
                for (j = 0; j < 4; j++) {
                    if (i != k && j != k) m[i][j] += hold * m[k][j];
                }
            }

            /* Divide row by pivot */
            for (j = 0; j < 4; j++) {
                if (j != k) m[k][j] /= pvt_val;
            }

            /* Replace pivot by reciprocal (at last we can touch it). */
            m[k][k] = 1.0f / pvt_val;
        }

        /* That was most of the work, one final pass of row/column interchange */
        /* to finish */
        for (k = 4 - 2; k >= 0; k--) { /* Don't need to work with 1 by 1 corner*/
            i = pvt_j[k];          /* Rows to swap correspond to pivot COLUMN */
            if (i != k) {          /* If rows are different */
                for (j = 0; j < 4; j++) {
                    hold = m[k][j];
                    m[k][j] = -m[i][j];
                    m[i][j] = hold;
                }
            }

            j = pvt_i[k];         /* Columns to swap correspond to pivot ROW */
            if (j != k)           /* If columns are different */
                for (i = 0; i < 4; i++) {
                    hold = m[i][k];
                    m[i][k] = -m[i][j];
                    m[i][j] = hold;
                }
        }
        return true;
    }
    private final static float[][] Scale(float m[][], float x, float y, float z) {
        for (int i = 0; i < 4; i++) {
            m[0][i] *= x;
            m[1][i] *= y;
            m[2][i] *= z;
        }
        return m;
    }
    private final static float[][] Mult(float[][] m, float[][] a, float[][] b){
        int i, j, k;
        float ab;
        for (j = 0; j < 4; j++) {
            for (i = 0; i < 4; i++) {
                ab = 0.0f;
                for (k = 0; k < 4; k++){
                    ab += (a[k][i] * b[j][k]);
                }
                m[j][i] = ab;
            }
        }
        return m;
    }
    private final static float[] Copy(float dst[], float src[]) {
        System.arraycopy(src,0,dst,0,3);
        return dst;
    }
    private final static float[] Transform(float c[], float m[][], float a[]) {
        c[0] = m[0][0] * a[0] + m[1][0] * a[1] + m[2][0] * a[2] + m[3][0];
        c[1] = m[0][1] * a[0] + m[1][1] * a[1] + m[2][1] * a[2] + m[3][1];
        c[2] = m[0][2] * a[0] + m[1][2] * a[1] + m[2][2] * a[2] + m[3][2];
        return c;
    }
    private final static float[] Sub(float c[], float a[], float b[]) {
        for (int i = 0; i < 3; ++i) {
            c[i] = a[i] - b[i];
        }
        return c;
    }
    private final static float[] Cross(float c[], float a[], float b[]) {
        c[0] = a[1] * b[2] - a[2] * b[1];
        c[1] = a[2] * b[0] - a[0] * b[2];
        c[2] = a[0] * b[1] - a[1] * b[0];
        return c;
    }
    private final static float[] Normalize(float c[]) {
        float l, m;
        l = (float)Math.sqrt(c[0] * c[0] + c[1] * c[1] + c[2] * c[2]);
        if (Math.abs(l) < EPSILON) {
            if ((c[0] >= c[1]) && (c[0] >= c[2])) {
                c[0] = 1.0f;
                c[1] = c[2] = 0.0f;
            }
            else {
                if (c[1] >= c[2]) {
                    c[1] = 1.0f;
                    c[0] = c[2] = 0.0f;
                }
                else {
                    c[2] = 1.0f;
                    c[0] = c[1] = 0.0f;
                }
            }
        }
        else {
            m = 1.0f / l;
            c[0] *= m;
            c[1] *= m;
            c[2] *= m;
        }
        return c;
    }
    private final static float[] Normal(float n[], float a[], float b[], float c[]) {
        float p[] = new float[3], q[] = new float[3];
        Sub(p, c, b);
        Sub(q, a, b);
        Cross(n, p, q);
        return Normalize(n);
    }
}
