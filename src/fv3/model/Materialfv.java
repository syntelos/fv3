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

public final class Materialfv
    extends fv3.model.Object
{
    private final int face, name;
    private final float[] params;


    public Materialfv(int face, int name, float[] params){
        super();
        if (0 < face && 0 < name && null != params){
            this.face = face;
            this.name = name;
            this.params = params;
        }
        else
            throw new IllegalArgumentException();
    }


    public void apply(GL2 gl){
        gl.glMaterialfv(this.face,this.name,this.params,0);
    }
    public Object.Type getObjectType(){
        return Object.Type.Materialfv;
    }
}
