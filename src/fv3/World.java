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

import fv3tk.Animator;

import java.io.IOException;

import java.net.URL;

/**
 * The world of the screen may contain many regions and components
 * having many coordinate spaces -- all visible simultaneously without
 * pages or tabs -- as a single application or practical workspace.
 * 
 * A component is added to the world of the screen with the "load"
 * function.  Or, a new view is created from a component with the
 * "clear and load" function.
 * 
 * @author jdp
 */
public class World
    extends fv3.nui.Region
{
    public static Component ClearAndLoad(URL jnlp) throws IOException {
        Instance.clear();
        return Instance.load(jnlp);
    }
    public static Component Load(URL jnlp) throws IOException {
        return Instance.load(jnlp);
    }
    public static Component Unload(URL jnlp) throws IOException {
        return Instance.unload(jnlp);
    }
    public static Component Current(){
        return Instance.getCurrent();
    }
    public static void Current(Component c){
        Instance.setCurrent(c);
    }


    private volatile static World Instance;

    public static void main(String[] argv){
        try {
            Animator animator = new Animator(World.Instance);
            animator.start();
        }
        catch (Exception exc){
            exc.printStackTrace();
            System.exit(1);
        }
    }


    protected World(){
        super();
        if (null == Instance)
            Instance = this;
        else
            throw new IllegalStateException();
    }


    /**
     * Unload current components.
     */
    public void clear(){
        throw new UnsupportedOperationException("To be done");
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
}
