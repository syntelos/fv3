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
     * This init method call occurs immediately following glNewList
     * and before glEndList.  

     * This method calls glBegin and glEnd.
     */
    public interface Element {

        public void define(GL2 gl);
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
