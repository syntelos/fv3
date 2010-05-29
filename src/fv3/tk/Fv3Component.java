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

import com.sun.javafx.newt.* ;

import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;
import javax.media.opengl.glu.GLU;

/**
 * Interface from {@link Animator} to {@link Fv3Canvas}.
 * 
 * The init method performs projection operations, and the display
 * method performs modelview operations.
 * 
 * @see Fv3Canvas
 * @author jdp
 */
public interface Fv3Component
    extends KeyListener,
            MouseListener
{
    /**
     * Called once from the {@link Animator} after the {@link
     * Fv3Screen} and GL version ({@link Fv3glv}) have been defined.
     */
    public void init(GL2 gl);
    /**
     * Called once from the {@link Animator} before "init" and after
     * the {@link Fv3Screen} and GL version ({@link Fv3glv}) have been
     * defined.  The {@link Animator} thread creates one GLU instance
     * which is shared.
     */
    public void setGLU(GLU glu);
    /**
     * Called by the {@link Animator} for many updates per second.
     * @param gl GL context
     */
    public void display(GL2 gl);
}
