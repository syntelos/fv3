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
package fv3.nui;

import fv3.math.Matrix;

import javax.media.opengl.GL2;

/**
 * List model.
 * 
 * A GL display list defines a glBegin, glEnd sequence once at
 * initialization time.
 * 
 * This class complements the display list procedure with display list
 * element objects for integrating a variety of application
 * programming techniques.
 * 
 */
public abstract class List
    extends Component
    implements fv3.Model
{

    protected final int count;

    protected volatile int[] lid;


    /**
     * @param count Number of display lists for model.
     */
    public List(int count){
        super();
        if (0 < count){
            this.count = count;
            this.lid = new int[count];
            for (int cc = 0; cc < count; cc++)
                this.lid[cc] = -1;
        }
        else
            throw new IllegalArgumentException(String.valueOf(count));
    }


    /**
     * @param idx Display list index
     * @return Display list procedure 
     */
    public abstract Element list(int idx);


    public void init(GL2 gl){

        super.init(gl);

        for (int cc = 0; cc < this.count; cc++){

            int lid = this.lid[cc];

            if (-1 == lid){

                lid = gl.glGenLists(1);

                this.lid[cc] = lid;

                gl.glNewList(lid, GL2.GL_COMPILE);

                this.list(cc).define(gl);

                gl.glEndList();
            }
        }
    }
    public void display(GL2 gl){

        super.display(gl);

        for (int cc = 0; cc < this.count; cc++){

            int lid = this.lid[cc];

            if (-1 != lid)
                gl.glCallList(lid);
        }
    }
    public void destroy(){
        GL2 gl = GL();

        for (int cc = 0; cc < this.count; cc++){

            int lid = this.lid[cc];

            if (-1 != lid){

                this.lid[cc] = -1;

                gl.glDeleteLists(lid,1);
            }
        }
        super.destroy();
    }
    public final int getGlListCount(){

        return this.count;
    }
    public final int getGlListId(int idx)
        throws java.lang.ArrayIndexOutOfBoundsException
    {
        if (-1 < idx && idx < this.count)

            return this.lid[idx];
        else
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }

}
