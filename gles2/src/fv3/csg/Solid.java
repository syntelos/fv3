/*
 * Fv3 
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
package fv3.csg;

import fv3.csg.u.A;
import fv3.csg.u.Bound;
import fv3.csg.u.Face;
import fv3.csg.u.Mesh;
import fv3.csg.u.Vertex;
import fv3.math.Matrix;
import fv3.math.Vector;
import fv3.math.VertexArray;

import lxl.List;
import lxl.Map;

/**
 * A solid encloses a finite volume.  No two faces may intersect
 * (overlap).
 * 
 * This is a mesh data structure: vertices are each unique.  The mesh
 * sub class data structure is "compiled" into the super class vertex
 * array for rendering.
 * 
 * Vertices are loaded, construction operations may be performed, and
 * then the vertex array is compiled for rendering.
 */
public class Solid
    extends fv3.math.VertexArray
    implements fv3.csg.u.Notation,
               fv3.csg.u.Name.Named,
               java.lang.Iterable<Face>,
               fv3.Bounds
{
    public enum Construct {
        Union, Intersection, Difference;
    }

    public final static class Name 
        extends fv3.csg.u.Name
    {
        public Name(String desc){
            super(Kind.Solid,desc);
        }
        protected Name(Name n, String desc2){
            super(n,desc2);
        }

        public Name copy(String desc2){
            return new Name(this,desc2);
        }
    }


    private Mesh mesh;

    public final Name name;

    public final Construct constructOp;

    public final Solid constructA, constructB;

    /**
     * @param countVertices Estimated or expected number of vertices
     */
    public Solid(String name, int cv){
        super(Type.Triangles,cv);

        this.mesh = new Mesh(cv);

        this.name = new Name(name);

        this.constructOp = null;
        this.constructA = null;
        this.constructB = null;
    }
    public Solid(String name, VertexArray array){
        super(Type.Triangles,array);

        this.mesh = new Mesh(this.countVertices);

        this.name = new Name(name);

        for (int face = 0, count = this.countFaces; face < count; face++){

            this.add(new Face(this, new Face.Name(this,face,"VA"),
                              this.getFace(face)));
        }
        this.constructOp = null;
        this.constructA = null;
        this.constructB = null;
    }
    /**
     * Used by CSG algorithms.
     * @see fv3.csg.u.A
     */
    public Solid(Construct op, Solid a, Solid b){
        super(Type.Triangles,a.countVertices());

        this.mesh = new Mesh(a.countVertices);

        this.name = new Name(String.format("%s of (%s) and (%s))",op,a.getName(),b.getName()));

        this.constructOp = op;
        this.constructA = a;
        this.constructB = b;
    }


    public int countSolidFaces(){
        return this.mesh.size();
    }
    public Face getSolidFace(int idx){
        return this.mesh.get(idx);
    }
    /**
     * Initial build "add" is overridden by {@link Geom} for sorting
     * face vertex order in three space.
     */
    public Solid add(Face face){

        this.mesh.add(face);
        return this;
    }
    public final Solid add(VertexArray array){

        float[] triangles = array.vertices(VertexArray.Type.Triangles);

        for (int index = 0, count = triangles.length; index < count; ){

            float x0 = triangles[index++];
            float y0 = triangles[index++];
            float z0 = triangles[index++];

            float x1 = triangles[index++];
            float y1 = triangles[index++];
            float z1 = triangles[index++];

            float x2 = triangles[index++];
            float y2 = triangles[index++];
            float z2 = triangles[index++];

            this.add(new Face(this, new Face.Name(this,((index/3)-1),"VA"),
                              x0,y0,z0,x1,y1,z1,x2,y2,z2));
        }
        return this;
    }
    public Name getName(){
        return this.name;
    }
    /**
     * Construct a new solid as the union of "this" and "that".  This
     * union is the (minimal) sum of this and that.
     */
    public final Solid union(Solid that){

        A a = new fv3.csg.u.AH(Construct.Union,this,that);
        try {
            return a.r;
        }
        finally {
            a.destroy();
        }
    }
    /**
     * Construct a new solid as the intersection of "this" and "that".
     * The intersection is the remainder of this minus that.
     */
    public final Solid intersection(Solid that){

        A a = new fv3.csg.u.AH(Construct.Intersection,this,that);
        try {
            return a.r;
        }
        finally {
            a.destroy();
        }
    }
    /**
     * Construct a new solid as the difference of "this" and "that".
     * The difference is this minus that.
     */
    public final Solid difference(Solid that){

        A a = new fv3.csg.u.AH(Construct.Difference,this,that);
        try {
            return a.r;
        }
        finally {
            a.destroy();
        }
    }
    public final Solid transform(Matrix m){

        for (Face face: this){

            face.transform(this,m);
        }
        return this;
    }
    /**
     * Update the superclass vertex array with the state of the faces
     * in this instance, as for rendering.  
     * 
     * This is not necessary when the solid was constructed from a
     * vertex array and has not changed since construction.
     * 
     * Otherwise this step is necessary to rendering this shape.
     */
    public final Solid compile(){

        super.countVertices(this.mesh.countVertices());

        int nc = 0, vc = 0;

        for (Face face: this.mesh){

            this.setVertices(vc, face.vertices(), 0, 3);
            vc += 3;

            float[] n = face.normal();

            this.setNormal(nc++, n);
            this.setNormal(nc++, n);
            this.setNormal(nc++, n);
        }
        return this;
    }
    public final Solid compile(Matrix m){
        if (null != m){
            super.countVertices(this.mesh.countVertices());

            int nc = 0, vc = 0;

            for (Face face: this.mesh){

                Vector a = m.transform(face.a.getVector());
                Vector b = m.transform(face.b.getVector());
                Vector c = m.transform(face.c.getVector());
                Vector n = a.normal(b,c);

                this.setVertex(vc++, a);
                this.setVertex(vc++, b);
                this.setVertex(vc++, c);

                this.setNormal(nc++, n);
                this.setNormal(nc++, n);
                this.setNormal(nc++, n);
            }
            return this;
        }
        else
            return this.compile();
    }
    public final Bound getBound(){
        return this.mesh.getBound();
    }
    public final float getBoundsMinX(){
        return this.getBound().getBoundsMinX();
    }
    public final float getBoundsMidX(){
        return this.getBound().getBoundsMidX();
    }
    public final float getBoundsMaxX(){
        return this.getBound().getBoundsMaxX();
    }
    public final float getBoundsMinY(){
        return this.getBound().getBoundsMinY();
    }
    public final float getBoundsMidY(){
        return this.getBound().getBoundsMidY();
    }
    public final float getBoundsMaxY(){
        return this.getBound().getBoundsMaxY();
    }
    public final float getBoundsMinZ(){
        return this.getBound().getBoundsMinZ();
    }
    public final float getBoundsMidZ(){
        return this.getBound().getBoundsMidZ();
    }
    public final float getBoundsMaxZ(){
        return this.getBound().getBoundsMaxZ();
    }
    public final int countVertices(){
        return this.mesh.countVertices();
    }
    public void destroy(){
        try {
            for (Face face: this)
                face.destroy(this);
        }
        finally {
            this.mesh.destroy();
        }
    }
    public final java.util.Iterator<Face> iterator(){
        return this.mesh.iterator();
    }
    /**
     * Construction "add" performs clone
     */
    public Solid addC(Face face){

        this.mesh.add(face.clone(this));
        return this;
    }
    /**
     * Face unique vertex
     */
    public final Vertex u(Vertex a){

        return this.mesh.u(a);
    }
    public final Solid remove(Vertex a){

        this.mesh.remove(a);

        a.destroy();

        return this;
    }
    public final Solid remove(Face f){

        this.mesh.remove(f);

        f.destroy(this);

        return this;
    }
    public final Solid replace(Face old, Face[] with){

        if (null != with){

            this.mesh.replace(old, with);

            old.destroy(this);
        }
        return this;
    }
    public Solid push(){
        this.mesh = this.mesh.push();
        return this;
    }
    public Solid pop(){
        this.mesh = this.mesh.pop();
        return this;
    }
    public String toString(){
        return this.toString(" ","\n");
    }
    public String toString(String pr){
        return this.toString(pr,"\n");
    }
    public String toString(String pr, String in){
        if (null == pr)
            pr = "";
        if (null == in)
            in = " ";

        StringBuilder string = new StringBuilder();

        string.append(pr);
        string.append(this.name);

        for (Face face : this){

            string.append(in);
            string.append(pr);
            string.append(face);
        }
        return string.toString();
    }
}
