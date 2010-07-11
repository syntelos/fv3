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

public class PolygonMode
    extends fv3.model.Object
{
    public final static class Fill
        extends PolygonMode
    {
        public Fill(){
            super(GL2.GL_FRONT,GL2.GL_FILL);
        }
        public Fill(int face){
            super(face,GL2.GL_FILL);
        }
    }
    public final static class Line
        extends PolygonMode
    {
        public Line(){
            super(GL2.GL_FRONT,GL2.GL_LINE);
        }
        public Line(int face){
            super(face,GL2.GL_LINE);
        }
    }
    public final static class Point
        extends PolygonMode
    {
        public Point(){
            super(GL2.GL_FRONT,GL2.GL_POINT);
        }
        public Point(int face){
            super(face,GL2.GL_POINT);
        }
    }


    private final int glFace;
    private final int glType;


    public PolygonMode(int glFace, int glType){
        super();
        this.glFace = glFace;
        this.glType = glType;
    }


    public void define(GL2 gl){
        gl.glPolygonMode(this.glFace,this.glType);
    }
}
