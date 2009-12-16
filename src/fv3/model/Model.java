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

public final class Model
    extends fv3.nui.Model
{

    private final Object[] model;

    private volatile boolean inited;

    private volatile int lid;


    public Model(Object[] model){
        super();
        if (null != model)
            this.model = model;
        else
            throw new IllegalArgumentException();
    }


    public int getGlListCount(){
        if (this.inited)
            return 1;
        else
            return 0;
    }
    public int getGlListId(int idx)
        throws java.lang.ArrayIndexOutOfBoundsException
    {
        if (this.inited && 0 == idx)
            return this.lid;
        else
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    public void init(GL2 gl){

        if (!this.inited){
            this.lid = gl.glGenLists(1);
            this.inited = true;
        }
        else {
            gl.glDeleteLists(this.lid,1);
            this.lid = gl.glGenLists(1);
            this.inited = true;
        }

        gl.glNewList(this.lid, GL2.GL_COMPILE);
        Object[] model = this.model;
        for (int cc = 0, count = model.length; cc < count; cc++){
            model[cc].apply(gl);
        }
        gl.glEndList();
    }
    public void display(GL2 gl){

        gl.glCallList(this.lid);
    }
    public void destroy(){
        this.inited = false;
        super.destroy();
    }
}
