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
package fv3tk;

import javax.media.opengl.DebugGL2;
import javax.media.opengl.GLContext;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.TraceGL2;
import javax.media.opengl.glu.GLU;

import com.sun.javafx.newt.PaintEvent;
import com.sun.javafx.newt.PaintListener;
import com.sun.javafx.newt.Window;
import com.sun.javafx.newt.WindowEvent;
import com.sun.javafx.newt.WindowListener;
import com.sun.javafx.newt.opengl.GLWindow;

/**
 * Animator is instantiated by the application main in a subclass of
 * {@link Fv3Canvas}, and started.  Fv3tk is designed for one animator
 * on one (full screen) window (per jvm process).
 * 
 * <h3>Elapse of Time</h3>
 * 
 * The {@link Animator$Sync} provides a speed limit algorithm for
 * animations.  It is configured by a single system property for the
 * least number of milliseconds per animation input and rendering
 * cycle.
 * 
 * <h3>Application of Time</h3>
 * 
 * The methods {@link #currentTime()}, {@link deltaTimeMillis()} and
 * {@link #deltaTimeNanos()} provide a stable representation of time
 * for the animation.
 * 
 * 
 * @see Fv3Canvas
 * @author jdp
 */
public final class Animator
    extends java.lang.Thread
    implements PaintListener,
               WindowListener
{
    /**
     * This can be called from any of the {@link Fv3Component}
     * methods, including the input event listeners.  
     */
    public static GL2 GL(){
        return (GL2)GLContext.getCurrentGL();
    }
    public static Animator currentAnimator(){
        try {
            return (Animator)Thread.currentThread();
        }
        catch (ClassCastException cast){
            return Instance;
        }
    }
    public static GLWindow currentWindow(){
        return Animator.currentAnimator().glWindow;
    }

    private volatile static Animator Instance;

    static {
        System.setProperty("java.awt.headless","true");
    }
    /**
     * GLWindow position X optionally defined by system property
     * <code>"fv3tk.Animator.X"</code>.
     * 
     * Default value: Zero
     */
    public final static int X;
    static {
        int x = -1;
        String config = System.getProperty("fv3tk.Animator.X");
        if (null != config){
            try {
                int tt = Integer.parseInt(config);
                x = tt;
                System.err.println(String.format("%s: Set fv3tk.Animator.X(%d)",Thread.currentThread().getName(),x));
            }
            catch (NumberFormatException exc){
            }
        }
        X = x;
    }
    /**
     * GLWindow position Y optionally defined by system property
     * <code>"fv3tk.Animator.Y"</code>.
     * 
     * Default value: Zero
     */
    public final static int Y;
    static {
        int y = -1;
        String config = System.getProperty("fv3tk.Animator.Y");
        if (null != config){
            try {
                int tt = Integer.parseInt(config);
                y = tt;
                System.err.println(String.format("%s: Set fv3tk.Animator.Y(%d)",Thread.currentThread().getName(),y));
            }
            catch (NumberFormatException exc){
            }
        }
        Y = y;
    }
    /**
     * GLWindow position W optionally defined by system property
     * <code>"fv3tk.Animator.W"</code>.
     * 
     * Default value: Zero
     */
    public final static int W;
    static {
        int w = -1;
        String config = System.getProperty("fv3tk.Animator.W");
        if (null != config){
            try {
                int tt = Integer.parseInt(config);
                w = tt;
                System.err.println(String.format("%s: Set fv3tk.Animator.W(%d)",Thread.currentThread().getName(),w));
            }
            catch (NumberFormatException exc){
            }
        }
        W = w;
    }
    /**
     * GLWindow position H optionally defined by system property
     * <code>"fv3tk.Animator.H"</code>.
     * 
     * Default value: Zero
     */
    public final static int H;
    static {
        int h = -1;
        String config = System.getProperty("fv3tk.Animator.H");
        if (null != config){
            try {
                int tt = Integer.parseInt(config);
                h = tt;
                System.err.println(String.format("%s: Set fv3tk.Animator.H(%d)",Thread.currentThread().getName(),h));
            }
            catch (NumberFormatException exc){
            }
        }
        H = h;
    }

    /**
     * GLWindow swap interval optionally defined by system property
     * <code>"fv3tk.Animator.SwapInterval"</code>.
     * 
     * Default value: accept underlying JOGL/NEWT default value.
     */
    public final static int SwapInterval;
    static {
        int swapi = -1;
        String config = System.getProperty("fv3tk.Animator.SwapInterval");
        if (null != config){
            try {
                int tt = Integer.parseInt(config);
                swapi = tt;
                System.err.println(String.format("%s: Set fv3tk.Animator.SwapInterval(%d)",Thread.currentThread().getName(),swapi));
            }
            catch (NumberFormatException exc){
            }
        }
        SwapInterval = swapi;
    }
    /**
     * GL tracing optionally defined by system property
     * <code>"fv3tk.Animator.GLTrace"</code>.
     * 
     * Default value: accept underlying JOGL/NEWT default value.
     */
    public final static boolean GLTrace;
    static {
        boolean traceb = false;
        String config = System.getProperty("fv3tk.Animator.GLTrace");
        if (null != config){
            traceb = ("true".equals(config));
            System.err.println(String.format("%s: Set fv3tk.Animator.GLTrace(%b)",Thread.currentThread().getName(),traceb));
        }
        GLTrace = traceb;
    }
    /**
     * GL debugging optionally defined by system property
     * <code>"fv3tk.Animator.GLDebug"</code>.
     * 
     * Default value: accept underlying JOGL/NEWT default value.
     */
    public final static boolean GLDebug;
    static {
        boolean traceb = false;
        String config = System.getProperty("fv3tk.Animator.GLDebug");
        if (null != config){
            traceb = ("true".equals(config));
            System.err.println(String.format("%s: Set fv3tk.Animator.GLDebug(%b)",Thread.currentThread().getName(),traceb));
        }
        GLDebug = traceb;
    }

    /**
     * Sync implements a speed limit in service of either or both two
     * goals: uniformity of the experience of time, and economy of CPU
     * consumption.
     * 
     * The event and rendering animation cycle may take no fewer than
     * "DT" milliseconds.
     * 
     * The animation cycle may be slower on slow or busy computers.
     * 
     * A (non zero) positive DT value is optionally defined in the
     * system property named <code>"fv3tk.Animator.Sync.DT"</code>.
     * 
     * A DT value between 20 and 40 is 50 to 25 frames per second,
     * while a DT value between 50 and 100 is for conventional
     * computer interaction in 20 to 10 frames per second.
     */
    public final static class Sync {
        /**
         * Animator cycle time optionally defined by system property
         * <code>"fv3tk.Animator.Sync.DT"</code>.
         * 
         * Ideal elapsed time per animation cycle.
         */
        public final static long DT;
        static {
            long dt = 40L;

            String config = System.getProperty("fv3tk.Animator.Sync.DT");
            if (null != config){
                try {
                    long tt = Long.parseLong(config);
                    if (0 < tt){
                        dt = tt;
                        System.err.println(String.format("%s: Set fv3tk.Animator.Sync.DT(%d)",Thread.currentThread().getName(),dt));
                    }
                }
                catch (NumberFormatException exc){
                    System.err.println(String.format("Error parsing value of 'fv3tk.Animator.Sync.DT' from '%s'",config));
                }
            }
            DT = dt;
        }

        private volatile long last;

        public Sync(){
            super();
        }

        public synchronized void wake(){
            this.notify();
        }
        public synchronized void waitfor()
            throws InterruptedException
        {
            long current = System.currentTimeMillis();
            /*
             * (actual elapsed time) := (current time) - (previous time)
             * 
             * (delta from uniform time) := (ideal elapsed time) - (actual elapsed time)
             * 
             * if (0 < (delta from uniform time))
             *   =>(ideal > actual)=>(actual too fast)=>(do wait for difference)
             * else
             *   =>(actual > ideal)=>(actual too slow)=>(do thread yield)
             * 
             */
            long du = (DT - (current - this.last));
            this.last = current;
            if (0 < du)
                this.wait(du);
            else
                Thread.yield();
        }
    }


    private final Sync sync = new Sync();

    private final Fv3Component fv3c;

    private Fv3Screen fv3s;

    private final boolean undecorated = true;

    private volatile boolean halting, running = true;

    private volatile GLWindow glWindow;

    private volatile long currentTime, deltaTimeMillis, deltaTimeNanos;

    /**
     * Constructed and started from "main" in canvas.
     */
    public Animator(Fv3Component fv3c){
        super("FV3 Animator");
        if (null != Instance){
            if (Instance.isAlive())
                throw new IllegalStateException("Live instance found.");
        }
        Instance = this;

        this.setDaemon(false);
        this.setPriority(MIN_PRIORITY);
        if (null != fv3c)
            this.fv3c = fv3c;
        else
            throw new IllegalArgumentException("Missing class of component.");
    }


    /**
     * Uniform display time.  This time is set to the system clock
     * before each entry into the animation cycle (input and
     * rendering).  It may be employed for a uniform simulation clock
     * throughout the display execution path.
     * @see #deltaTimeMillis()
     * @see #deltaTimeNanos()
     */
    public long currentTime(){
        return this.currentTime;
    }
    /**
     * @return The time since the previous animation cycle, in
     * milliseconds.  Calculated as (current - last).
     */
    public long deltaTimeMillis(){
        return this.deltaTimeMillis;
    }
    /**
     * @return The time since the previous animation cycle, in
     * nanoseconds.  Calculated as (current - last).
     */
    public long deltaTimeNanos(){
        return this.deltaTimeNanos;
    }
    public GLWindow getWindow(){
        return this.glWindow;
    }
    public boolean isRunning(){
        return this.running;
    }
    public void halt(){
        this.halting = true;
        try {
            this.interrupt();

            Thread.sleep(Sync.DT+10L);

            this.stop();
        }
        catch (Exception exc){
            return;
        }
    }
    public void run(){
        this.animatorInit();
        try {
            long lastM = System.currentTimeMillis(), deltaM, currentM;
            long lastN = System.nanoTime(), deltaN, currentN;
            while (this.animatorWaitfor()){
                try {
                    currentM = System.currentTimeMillis();
                    currentN = System.nanoTime();
                    deltaM = (currentM - lastM);
                    deltaN = (currentN - lastN);
                    lastM = currentM;
                    lastN = currentN;

                    this.currentTime = currentM;
                    this.deltaTimeMillis = deltaM;
                    this.deltaTimeNanos = deltaN;
                    
                    this.animatorDisplay();
                }
                catch (Exception exc){
                    exc.printStackTrace();
                }
            }
        }
        catch (InterruptedException aux){

            System.err.println(String.format("%s: interrupted",Thread.currentThread().getName()));
                    
            return;
        }
        finally {
            this.running = false;

            System.err.println(String.format("%s: halting",Thread.currentThread().getName()));

            this.glWindow.setVisible(false);
            this.glWindow.destroy();

            this.fv3s.screenDestroy();

            this.fv3h.hidDestroy();
        }
    }

    private boolean animatorWaitfor() throws InterruptedException {
        this.sync.waitfor();
        return (!this.halting);
    }
    private void animatorInit(){

        this.fv3s = new Fv3Screen();

        Fv3Component fv3c = this.fv3c;
        Fv3Screen fv3s = this.fv3s;
        int x = (int)fv3s.x;
        int y = (int)fv3s.y;
        int width = (int)fv3s.width;
        int height = (int)fv3s.height;

        this.glWindow = GLWindow.create(fv3s.glCapabilities,this.undecorated);
        GLWindow glWindow = this.glWindow;

        /*[TODO Fullscreen disabled for review]
         * 
         * modal := is this modal on some platforms?
         * 
         * if (modal)
         * then
         *  (dont use fullscreen);
         * else
         *   (use fullscreen);
         *
        glWindow.setFullscreen(true);
         */
        glWindow.setPosition(x,y);
        glWindow.setSize(width,height);
        glWindow.addKeyListener(fv3c);
        glWindow.addMouseListener(fv3c);
        glWindow.addPaintListener(this);
        glWindow.addWindowListener(this);

        glWindow.setVisible(true);

        GLContext glx = glWindow.getContext();

        switch (glx.makeCurrent()){
        case GLContext.CONTEXT_NOT_CURRENT:
            throw new IllegalStateException("Context error.");
        case GLContext.CONTEXT_CURRENT:
        case GLContext.CONTEXT_CURRENT_NEW:
            try {
                GL2 gl = glx.getGL().getGL2();

                Fv3glv.Instance(gl);

                gl.glViewport(x,y,width,height);

                fv3c.setGLU(GLU.createGLU(gl));

                if (-1 < Animator.SwapInterval){
                    System.err.println(String.format("%s: Set SwapInterval(%d)",this.getName(),Animator.SwapInterval));
                    gl.setSwapInterval(Animator.SwapInterval);
                }
                if (Animator.GLTrace){
                    System.err.println(String.format("%s: Using GL Tracing",this.getName()));
                    gl = new TraceGL2(gl,System.out);
                }
                if (Animator.GLDebug){
                    System.err.println(String.format("%s: Using GL Tracing",this.getName()));
                    gl = new DebugGL2(gl);
                }

                fv3c.init(gl);

                glWindow.requestFocus();

                return;
            }
            finally {
                glx.release();
            }
        default:
            throw new IllegalStateException("Unreachable.");
        }
    }
    private void animatorDisplay(){
        GLWindow glWindow = this.glWindow;
        GLContext glx = glWindow.getContext();
        if (GLContext.CONTEXT_NOT_CURRENT == glx.makeCurrent())
            throw new IllegalStateException("Context error.");
        else {

            Fv3Component fv3c = this.fv3c;
            try {
                GL2 gl = glx.getGL().getGL2();
                if (Animator.GLTrace){
                    gl = new TraceGL2(gl,System.out);
                }
                if (Animator.GLDebug){
                    gl = new DebugGL2(gl);
                }

                Fv3Screen fv3s = this.fv3s;

                glWindow.lockSurface();
                try {
                    fv3s.input();

                    fv3c.display(gl);
                }
                finally {
                    glWindow.unlockSurface();
                }
            }
            finally {
                glWindow.swapBuffers();
                glx.release();
            }
        }
    }
    public void requestFocus(){
        GLWindow glWindow = this.glWindow;
        if (null != glWindow)
            glWindow.requestFocus();
    }
    public void exposed(PaintEvent e){
    }
    public void windowResized(WindowEvent e){
    }
    public void windowMoved(WindowEvent e){
    }
    public void windowDestroyNotify(WindowEvent e){
    }
    public void windowGainedFocus(WindowEvent e){
        this.requestFocus();
    }
    public void windowLostFocus(WindowEvent e){
    }
}
