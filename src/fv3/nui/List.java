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
 * Basic implementation of display list from {@link fv3.Model}.
 * 
 * A GL display list is defined once at initialization time.
 * 
 * This class complements the display list procedure with display list
 * element objects for integrating a variety of application
 * programming techniques.
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
     * This abstraction permits the subclass to implement one or more
     * display lists itself or via any other instance object.  
     * 
     * This degree of freedom unites subclasses and list elements
     * under this implementation of the model interface.  Considering
     * users as languages (not excluding interpreters and compilers),
     * subclasses and list elements are first class display list
     * components.
     * 
     * @param idx Display list index
     * @return Display list procedure 
     */
    public abstract Element list(int idx);


    public void init(GL2 gl){

        super.init(gl);

        for (int cc = 0; cc < this.count; cc++){

            int lid = this.lid[cc];

            Element el = this.list(cc);
            /*
             * Init(cc)
             */
            if (-1 == lid && null != el){

                lid = gl.glGenLists(1);

                this.lid[cc] = lid;

                gl.glNewList(lid, GL2.GL_COMPILE);

                el.define(gl);

                gl.glEndList();
            }
        }
    }
    public void display(GL2 gl){

        super.display(gl);

        boolean once = false;

        for (int cc = 0; cc < this.count; cc++){

            int lid = this.lid[cc];

            Element el = this.list(cc);

            if (null != el){

                if (-1 != lid){
                    once = false;

                    if (el.needsRedefine()){

                        this.lid[cc] = -1;

                        gl.glDeleteLists(lid,1);
                        /*
                         * Init(cc)
                         */
                        {
                            lid = gl.glGenLists(1);

                            this.lid[cc] = lid;

                            gl.glNewList(lid, GL2.GL_COMPILE);

                            el.define(gl);

                            gl.glEndList();
                        }
                    }

                    /*
                     * Display(cc)
                     */
                    final int[] ables = el.ables();
                    {
                        if (null != ables){
                            for (int ac = 0, az = ables.length; ac < az; ac++)
                                gl.glEnableClientState(ables[ac]);
                        }
                    }

                    gl.glCallList(lid);

                    {
                        if (null != ables){
                            for (int ac = 0, az = ables.length; ac < az; ac++)
                                gl.glDisableClientState(ables[ac]);
                        }
                    }
                }
                else if (once)
                    continue;
                else {
                    once = true;
                    /*
                     * Init(cc)
                     */
                    {
                        lid = gl.glGenLists(1);

                        this.lid[cc] = lid;

                        gl.glNewList(lid, GL2.GL_COMPILE);

                        el.define(gl);

                        gl.glEndList();

                        cc -= 1;
                    }
                }
            }
        }
    }
    public void destroy(){
        try {
            GL2 gl = GL();
            if (null != gl){

                for (int cc = 0; cc < this.count; cc++){

                    int lid = this.lid[cc];

                    if (-1 != lid){

                        this.lid[cc] = -1;

                        gl.glDeleteLists(lid,1);
                    }
                }
            }
        }
        catch (Throwable any){
        }
        finally {
            super.destroy();
        }
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
