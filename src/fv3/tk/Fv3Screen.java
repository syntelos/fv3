/*
 * fv3tk
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
package fv3.tk;

import fv3.Fv3Exception;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import com.sun.javafx.newt.Display;
import com.sun.javafx.newt.NewtFactory;
import com.sun.javafx.newt.Screen;

/**
 * This physical screen descriptor is created by the Animator} thread.
 * As a result, it is not available from component constructors.  This
 * is necessary because the screen descriptor accesses native
 * resources that may only be accessed by one thread, the Animator
 * thread.  
 * 
 * The init and display methods in the {@link Fv3Component} ({@link
 * fv3.Component}) interface both occur from the Animator thread and
 * have access to the screen descriptor.
 * 
 * @author jdp
 */
public final class Fv3Screen
    extends Object
{
    volatile static Fv3Screen Current;

    public final static Fv3Screen Current(){

        return Current;
    }



    public final GLProfile glProfile;
    public final GLCapabilities glCapabilities;
    public final Display display;
    public final Screen screen;
    public final double x, y, width, w2, height, h2;
    private volatile boolean alive;


    /**
     * Created in animator thread 
     */
    Fv3Screen(){
        this(0);
    }
    Fv3Screen(int screen){
        super();
        Current = this;
        this.glProfile = GLProfile.getDefault();
        this.glCapabilities = new GLCapabilities(glProfile);
        NewtFactory.setUseEDT(false);
        this.display = NewtFactory.createDisplay(null);
        this.screen = NewtFactory.createScreen(this.display, screen);
        int x = Animator.X;
        int y = Animator.Y;
        int w = Animator.W;
        int h = Animator.H;
        this.x = (-1 == x)?(0):(x);
        this.y = (-1 == y)?(0):(y);
        if (-1 == w){
            if (0 == x)
                this.width = this.screen.getWidth();
            else
                this.width = (this.screen.getWidth() - x);
        }
        else
            this.width = Math.min(w,(this.screen.getWidth()-x));

        this.w2 = (this.width / 2.0);

        if (-1 == h){
            if (0 == y)
                this.height = this.screen.getHeight();
            else
                this.height = (this.screen.getHeight() - y);
        }
        else
            this.height = Math.min(h,(this.screen.getHeight() - y));

        this.h2 = (this.height / 2.0);

        this.alive = true;
    }


    public boolean contains(double x, double y, double w, double h){
        if (this.x <= x && this.y <= y){
            w += x;
            h += y;
            return (this.width >= w && this.height >= h);
        }
        else
            return false;
    }
    public boolean isAlive(){
        return this.alive;
    }
    boolean screenDestroy(){
        if (this.alive){
            this.alive = false;
            try {
                this.screen.destroy();
            }
            catch (Throwable t){
            }
            try {
                this.display.destroy();
            }
            catch (Throwable t){
            }
            return true;
        }
        else
            return false;
    }
    void input(){
        if (this.alive)
            this.display.pumpMessages();
    }

}
