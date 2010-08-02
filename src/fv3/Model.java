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
package fv3;

import javax.media.opengl.GL2;

/**
 * This interface exposes a GL display list as a foundation for
 * animating classes of geometric objects.
 * 
 * @see fv3.nui.List
 * @see fv3.model.Model
 */
public interface Model 
    extends Component
{
    /**
     * A model list element procedure defines a display list.
     * 
     * The define method is called immediately following glNewList
     * and before glEndList to define a display list.
     * 
     * For example, the define method often calls glBegin and glEnd.
     * 
     * Each of the values returned by the ables method are passed into
     * calls to glEnableClientState before glCallList, and into
     * glDisableClientState after glCallList.
     */
    public interface Element
        extends fv3.math.Notation
    {
        /**
         * @return List of client state enables before call list,
         * disables after call list.  Null for none.
         */
        public int[] ables();
        /**
         * Define the display list
         */
        public void define(GL2 gl);


        public final static class Iterator
            extends java.lang.Object
            implements java.util.Iterator<Element>
        {

            public final int length;

            private final Element[] list;

            private int index;

            public Iterator(Element[] list){
                super();
                if (null == list){
                    this.list = null;
                    this.length = 0;
                }
                else {
                    this.list = list.clone();
                    this.length = this.list.length;
                }
            }

            public boolean hasNext(){
                return (this.index < this.length);
            }
            public Element next(){
                return this.list[this.index++];
            }
            public void remove(){
                throw new UnsupportedOperationException();
            }
        }
    }

    /**
     * @return Zero- positive number of GL display lists employed by
     * this model.
     */
    public int getGlListCount();
    /**
     * @param idx A Zero- positive index into the GL display lists
     * defined by this model.
     * @return GL list identifier
     * @exception java.lang.ArrayIndexOutOfBoundsException For
     * argument 'idx' not greater than negative one and less than list
     * count.
     */
    public int getGlListId(int idx)
        throws java.lang.ArrayIndexOutOfBoundsException;
}
