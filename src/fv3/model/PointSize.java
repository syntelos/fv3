/*
 * fv3
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
package fv3.model;

import javax.media.opengl.GL2;

public class PointSize
    extends fv3.model.Object
{

    private final float size;


    public PointSize(double size){
        this( (float)size);
    }    
    public PointSize(float size){
        super();
        if (0.0 < size)
            this.size = size;
        else
            throw new IllegalArgumentException();
    }


    public void define(GL2 gl){
        gl.glPointSize(this.size);
    }
}
