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

import fv3.tk.Animator;

import java.io.IOException;

import java.net.URL;

import javax.media.opengl.GL2;

/**
 * The world of the screen may contain many regions and components
 * having many coordinate spaces -- all visible simultaneously without
 * pages or tabs -- as a single application, or a practical workspace.
 * 
 * A component is added to the world of the screen with the "load"
 * function.  Or, a new view is created from a component with the
 * "clear and load" function.
 * 
 * The methods defined here will not interfere with the normal
 * operation of an instance of this class as a normal {@link Region}.
 * 
 * @author jdp
 */
public class World
    extends fv3.nui.Region
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


    private volatile static World Window;
    /*
     * Main subclasses do the same..
     */
    public static void main(String[] argv){
        try {
            World window = new World();
            Animator animator = new Animator(window);
            animator.start();
        }
        catch (Exception exc){
            exc.printStackTrace();
            System.exit(1);
        }
    }


    private volatile Camera[] cameras = new Camera[26];

    private volatile int cameraCurrent;
    private volatile boolean cameraChange;


    protected World(){
        super();
        if (null == Window)
            Window = this;

        this.useCamera('A');
    }


    /**
     * @return Current camera by name
     */
    public char currentCamera(){
        char name = (char)(this.cameraCurrent+'A');
        return name;
    }
    /**
     * @return The current camera.  The default camera is "A".
     */
    public Camera getCamera(){
        return this.cameras[this.cameraCurrent];
    }
    /**
     * @param name Camera name, an ASCII alpha letter (A-Z == a-z).
     * @return The new current camera.  If the named camera didn't
     * exist before this method call, one is created as a clone of the
     * camera current before this method call.
     */
    public Camera useCamera(char name){
        if ('A' <= name && 'Z' >= name){

            int idx = name-'A';

            if (null == this.cameras[idx])
                this.cameras[idx] = new Camera(name,this.cameras[this.cameraCurrent]);

            this.cameraChange = (idx != this.cameraCurrent);
            this.cameraCurrent = idx;
            return this.cameras[idx];
        }
        else if ('a' <= name && 'z' >= name){
            
            int idx = name-'a';

            if (null == this.cameras[idx])
                this.cameras[idx] = new Camera(name,this.cameras[this.cameraCurrent]);

            this.cameraChange = (idx != this.cameraCurrent);
            this.cameraCurrent = idx;
            return this.cameras[idx];
        }
        else
            throw new IllegalArgumentException(String.format("0x%x",(int)name));
    }
    /**
     * Include a camera in the set of cameras without making it
     * current.
     */
    public Camera defineCamera(Camera camera){

        this.cameras[camera.index] = camera;
        return camera;
    }
    /**
     * Include a camera in the set of cameras, making it current.
     */
    public Camera useCamera(Camera camera){

        this.cameras[camera.index] = camera;
        this.cameraChange = (camera.index != this.cameraCurrent);
        this.cameraCurrent = camera.index;
        return camera;
    }

    public void init(GL2 gl) {

        this.cameras[this.cameraCurrent].init(gl,this.glu);

        super.init(gl);
    }
    public void display(GL2 gl){

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        if (this.cameraChange){
            this.cameraChange = false;

            Camera camera = this.cameras[this.cameraCurrent];
            camera.init(gl,this.glu);
            camera.display(gl,this.glu);
        }
        else
            this.cameras[this.cameraCurrent].display(gl,this.glu);

        super.display(gl);
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
