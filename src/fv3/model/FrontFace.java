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

public class FrontFace
    extends fv3.model.Object
{
    public final static class CW
        extends FrontFace
    {
        public CW(){
            super(GL2.GL_CW);
        }
    }
    public final static class CCW
        extends FrontFace
    {
        public CCW(){
            super(GL2.GL_CCW);
        }
    }


    private final int glType;


    public FrontFace(int glType){
        super();
        this.glType = glType;
    }


    public void apply(GL2 gl){
        gl.glFrontFace(this.glType);
    }
    public Object.Type getObjectType(){
        return Object.Type.FrontFace;
    }
}
