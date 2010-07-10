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
import lxl.Map;

/**
 * 
 */
public final class Mesh
    extends lxl.ArrayList<Face>
    implements java.util.Comparator<Face>
{


    public Map<Vertex,Vertex> vertices;

    private Bound bound;

    private Mesh prev;


    public Mesh(int v){
        super();
        super.setComparator(this);
        this.vertices = new Map<Vertex,Vertex>(v);
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
