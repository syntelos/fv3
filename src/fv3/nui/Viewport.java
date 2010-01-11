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

import fv3.tk.Fv3Screen;

import javax.media.opengl.GL2;

/**
 * @see fv3.View
 */
public class Viewport
    extends Component
    implements fv3.Viewport
{
    protected volatile boolean defined;

    protected volatile int x, y, w, h;


    public Viewport(){
        super();
    }
    public Viewport(int x, int y, int w, int h){
        super();

        this.set(x,y,w,h);
    }


    public final Viewport set(int x, int y, int w, int h){
        this.defined = true;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        return this;
    }
    public void init(GL2 gl){
        if (this.defined){
            gl.glViewport(this.x,this.y,this.w,this.h);
        }
    }

    public final int x(){
        return this.x;
    }
    public final int getX(){
        return this.x;
    }
    public final Viewport x(int x){
        this.x = x;
        return this;
    }
    public final Viewport setX(int x){
        this.x = x;
        return this;
    }

    public final int y(){
        return this.y;
    }
    public final int getY(){
        return this.y;
    }
    public final Viewport y(int y){
        this.y = y;
        return this;
    }
    public final Viewport setY(int y){
        this.y = y;
        return this;
    }

    public final int w(){
        return this.w;
    }
    public final int getW(){
        return this.w;
    }
    public final Viewport w(int w){
        this.w = w;
        return this;
    }
    public final Viewport setW(int w){
        this.w = w;
        return this;
    }

    public final int h(){
        return this.h;
    }
    public final int getH(){
        return this.h;
    }
    public final Viewport h(int h){
        this.h = h;
        return this;
    }
    public final Viewport setH(int h){
        this.h = h;
        return this;
    }
}
