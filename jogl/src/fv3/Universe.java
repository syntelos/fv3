/*
 * fv3
 * Copyright (C) 2010, John Pritchard, all rights reserved.
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package fv3;

import fv3.tk.Animator;

import java.io.IOException;
import java.net.URL;

/**
 * Universe is the root of a multi-application runtime.  It is a
 * container for an application {@link World} to provide services.
 *
 * @author jdp
 */
public class Universe
    extends World
{
    /**
     * @see #load(java.net.URL)
     * @see #clear()
     */
    public static Component ClearAndLoad(URL jnlp) throws IOException {
        Window.clear();
        return Window.load(jnlp);
    }
    /**
     * @see #load(java.net.URL)
     */
    public static Component Load(URL jnlp) throws IOException {
        return Window.load(jnlp);
    }
    /**
     * @see #unload(java.net.URL)
     */
    public static Component Unload(URL jnlp) throws IOException {
        return Window.unload(jnlp);
    }
    /**
     * Current focus of input events.
     * @see Region
     */
    public static Component Current(){
        return Window.getCurrent();
    }
    /**
     * @see Region
     */
    public static void Current(Component c){
        Window.setCurrent(c);
    }


    private volatile static Universe Window;

    /*
     * Main subclasses do the same..
     */
    public static void main(String[] argv){
        try {
            Universe window = new Universe();
            Animator animator = new Animator(window);
            animator.start();
        }
        catch (Exception exc){
            exc.printStackTrace();
            System.exit(1);
        }
    }


    protected Universe(){
        super();
        if (null == Window)
            Window = this;
    }


    /**
     * This function loads a JNLP descriptor using a {@link
     * loader.sandbox.WebLoader}.  
     * 
     * The JNLP descriptor has an application main class with a main
     * method that should do nothing.  
     * 
     * The JNLP application main class is an instance of {@link
     * Component}.
     * 
     * The returned component becomes a child of the world region
     * (this class).
     */
    public Component load(URL jnlp) throws IOException {
        throw new UnsupportedOperationException("To be done");
    }
    public Component unload(URL jnlp) throws IOException {
        throw new UnsupportedOperationException("To be done");
    }
    public Universe show(){
        new fv3.tk.Animator(this).start();
        return this;
    }
}
