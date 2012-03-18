/*
 * fv3
 * Copyright (C) 2010, John Pritchard, all rights reserved.
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
package fv3.csg.u;

import lxl.List;
import lxl.Set;

/**
 * Mesh tools
 */
public final class Mesh
    extends lxl.Set<Face>
    implements java.util.Comparator<Face>
{


    private Set<Vertex> vertices;

    private Bound bound;

    private Mesh prev;


    public Mesh(int v){
        super();
        super.setComparator(this);
        this.vertices = new Set<Vertex>(v>>1);
    }


    public Mesh replace(Face old, Face[] list){
        if (null != old && null != list){
            int idx = this.indexOf(old);
            if (-1 != idx){

                this.set(idx++,list[0]);

                for (int cc = 1, count = list.length; cc < count; cc++){

                    this.insert(list[cc],idx++);
                }
                return this;
            }
            else
                throw new IllegalStateException();
        }
        else
            throw new IllegalArgumentException();
    }
    public final Vertex u(Vertex a){
        int idx = this.vertices.indexOf(a);
        if (-1 == idx){
            this.vertices.add(a);
            return a;
        }
        else {
            Vertex b = this.vertices.get(idx);
            if (a.isUnknown())
                return b;
            else
                return b.with(a);
        }
    }
    public final Mesh remove(Vertex a){
        this.vertices.remove(a);
        return this;
    }
    public int countVertices(){
        return (3*this.size());
    }
    public Bound getBound(){
        Bound bound = this.bound;
        if (null == bound){
            bound = new Bound(this);
            this.bound = bound;
        }
        return bound;
    }
    public Mesh push(){
        if (null == this.prev){
            Mesh clone = (Mesh)super.clone();
            clone.vertices = this.vertices.clone();
            clone.prev = this;
            return clone;
        }
        else
            throw new IllegalStateException();
    }
    public Mesh pop(){
        Mesh prev = this.prev;
        if (null != prev){
            super.clear();
            this.vertices.clear();
            return prev;
        }
        else
            throw new IllegalStateException();
    }
    public void destroy(){
        super.clear();
        this.vertices.clear();
    }
    public boolean equals(Object that){
        return (this == that);
    }
    public int compare(Face a, Face b){
        return a.compareTo(b);
    }
}
