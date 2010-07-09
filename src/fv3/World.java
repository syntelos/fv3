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

import fv3.math.Color;
import fv3.tk.Animator;

import java.io.IOException;

import java.net.URL;

import javax.media.opengl.GL2;
import com.sun.javafx.newt.KeyEvent;

/**
 * World is the root of Fv3 applications.  It handles the OGL clear
 * color and Fv3 cameras.  A {@link Camera} defines the projection and
 * view matrices.
 *
 * @see fv3.nui.Region
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


    private volatile Camera[] cameras = new Camera[20];

    private volatile int cameraCurrent;
    private volatile boolean cameraChange;

    private volatile Color bg;


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
    public Camera getCamera(char name){
        if ('A' <= name && 'T' >= name){

            int idx = name-'A';

            return this.cameras[idx];
        }
        else if ('a' <= name && 't' >= name){
            
            int idx = name-'a';

            return this.cameras[idx];
        }
        else
            throw new IllegalArgumentException(String.format("0x%x",(int)name));
    }
    /**
     * @param name Camera name, an ASCII alpha letter (A-Z == a-z).
     * @return The new current camera when available, otherwise the
     * pre-existing current camera. 
     */
    public Camera useCamera(char name){
        if ('A' <= name && 'T' >= name){

            int idx = name-'A';

            if (null == this.cameras[idx])
                return this.cameras[this.cameraCurrent];
            else {
                this.cameraChange = (idx != this.cameraCurrent);
                this.cameraCurrent = idx;
                return this.cameras[idx];
            }
        }
        else if ('a' <= name && 't' >= name){

            int idx = name-'a';

            if (null == this.cameras[idx])
                return this.cameras[this.cameraCurrent];
            else {
                this.cameraChange = (idx != this.cameraCurrent);
                this.cameraCurrent = idx;
                return this.cameras[idx];
            }
        }
        else
            throw new IllegalArgumentException(String.format("0x%x",(int)name));
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
    /**
     * Include a camera in the set of cameras without making it
     * current.  If the named camera is not found, create a copy of
     * the current camera.
     */
    public Camera defineCamera(char name){

        if ('A' <= name && 't' >= name){

            int idx = name-'A';

            if (null == this.cameras[idx])
                this.cameras[idx] = new Camera(name,this.cameras[this.cameraCurrent]);

            return this.cameras[idx];
        }
        else if ('a' <= name && 't' >= name){

            int idx = name-'a';

            if (null == this.cameras[idx])
                this.cameras[idx] = new Camera(Character.toUpperCase(name),this.cameras[this.cameraCurrent]);

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
     * @param ch Key character
     * @see com.sun.javafx.newt.KeyEvent
     */
    public void keyNav(char ch){

        this.getCamera().keyNav(ch);
    }
    public boolean hasBgColor(){
        return (null != this.bg);
    }
    public boolean hasNotBgColor(){
        return (null == this.bg);
    }
    public Color getBgColor(){
        return this.bg;
    }
    public World setBgColor(Color c){
        this.bg = c;
        return this;
    }

    public void init(GL2 gl) {
        /*
         * Define projection matrix
         */
        this.cameras[this.cameraCurrent].init(gl,this.glu);

        /*
         * Propagate event to children via 'fv3.nui.Region'
         */
        super.init(gl);

        this.checkErrors(gl);
    }
    public void display(GL2 gl){

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        Color bg = this.bg;
        if (null != bg)
            gl.glClearColor(bg.rf(),bg.gf(),bg.bf(),bg.af());

        /*
         * Define view matrix
         */
        if (this.cameraChange){
            this.cameraChange = false;

            Camera camera = this.cameras[this.cameraCurrent];
            camera.init(gl,this.glu);
            camera.display(gl,this.glu);
        }
        else
            this.cameras[this.cameraCurrent].display(gl,this.glu);

        /*
         * Propagate event to children via 'fv3.nui.Region'
         */
        super.display(gl);
    }
    /**
     * Unload current components.
     */
    public void clear(){


        super.clear();
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
    public void keyTyped(KeyEvent e) {

        int cc = e.getKeyCode();
        switch (cc){
        case KeyEvent.VK_ESCAPE:
        case KeyEvent.VK_F4:
            super.keyTyped(e);
            return;
        default:
            char ch = e.getKeyChar();

            if ('a' <= ch && ch <= 't')
                this.useCamera(ch);
            else if ('A' <= ch && ch <= 'T')
                this.useCamera(ch);
            else 
                this.keyNav(ch);

            return;
        }
    }
}
