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

import fv3tk.Fv3Screen;

import javax.media.opengl.GL2;

/**
 * @see fv3.View
 */
public class View
    extends Component
    implements fv3.View
{
    protected volatile boolean defined;

    protected volatile int x, y, w, h;


    public View(){
        super();
    }
    public View(int x, int y, int w, int h){
        super();

        this.set(x,y,w,h);
    }


    public final View set(int x, int y, int w, int h){
        this.defined = true;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        return this;
    }
    public void init(GL2 gl){
        if (this.defined)
            gl.glViewport(this.x,this.y,this.w,this.h);
    }

    public final int x(){
        return this.x;
    }
    public final int getX(){
        return this.x;
    }
    public final View x(int x){
        this.x = x;
        return this;
    }
    public final View setX(int x){
        this.x = x;
        return this;
    }

    public final int y(){
        return this.y;
    }
    public final int getY(){
        return this.y;
    }
    public final View y(int y){
        this.y = y;
        return this;
    }
    public final View setY(int y){
        this.y = y;
        return this;
    }

    public final int w(){
        return this.w;
    }
    public final int getW(){
        return this.w;
    }
    public final View w(int w){
        this.w = w;
        return this;
    }
    public final View setW(int w){
        this.w = w;
        return this;
    }

    public final int h(){
        return this.h;
    }
    public final int getH(){
        return this.h;
    }
    public final View h(int h){
        this.h = h;
        return this;
    }
    public final View setH(int h){
        this.h = h;
        return this;
    }
    public final View center(){
        return this.center(Fv3Screen.Current());
    }
    public final View centerHor(){
        return this.centerHor(Fv3Screen.Current());
    }
    public final View centerVer(){
        return this.centerVer(Fv3Screen.Current());
    }
    public final View center(fv3tk.Fv3Screen fv3s){

        this.x = (int)(fv3s.w2 - (this.w / 2.0));
        this.y = (int)(fv3s.h2 - (this.h / 2.0));
        return this;
    }
    public final View centerHor(fv3tk.Fv3Screen fv3s){

        this.x = (int)(fv3s.w2 - (this.w / 2.0));
        return this;
    }
    public final View centerVer(fv3tk.Fv3Screen fv3s){

        this.y = (int)(fv3s.h2 - (this.h / 2.0));
        return this;
    }
}
