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

import fv3.csg.Solid;

public class SplitFace
    extends java.lang.Object
    implements fv3.csg.u.Notation
{

    public final String kind;

    public final Vertex[][] faces;


    public SplitFace(String kind, Vertex...faces){
        super();
        this.kind = kind;
        if (null != faces && 0 < faces.length){

            int count = (faces.length/3);

            Vertex[][] fv = new Vertex[count][3];

            for (int fc = 0, cc = 0; fc < count; fc++){
                fv[fc][0] = faces[cc++];
                fv[fc][1] = faces[cc++];
                fv[fc][2] = faces[cc++];
            }
            this.faces = fv;
        }
        else
            this.faces = null;
    }


    public boolean hasFaces(){
        return (null != this.faces);
    }
    public int size(){
        Vertex[][] faces = this.faces;
        if (null != faces)
            return faces.length;
        else
            return 0;
    }
    public Vertex[] get(int idx){

        return this.faces[idx];
    }
    public Face create(Solid s, Face.Name n, int idx){

        return new Face(s,n.copy(this.kind),this.faces[idx]);
    }
    public int indexOf(Face face){

        Vertex[][] faces = this.faces;

        for (int cc = 0, count = faces.length; cc < count; cc++){
            if (face.equals(faces[cc]))
                return cc;
        }
        return -1;
    }
}
