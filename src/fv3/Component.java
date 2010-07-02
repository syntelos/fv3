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

import java.nio.DoubleBuffer;

import fv3.math.AxisAngle;
import fv3.math.Matrix;
import fv3.math.Vector;

/**
 * A component that is not a {@link Region} is a leaf in a component
 * graph.  At least for the benefit of discussion, a component is a GL
 * vertex list.
 * 
 * <h3>Operation</h3>
 * 
 * The component graph is populated in the constructors of
 * implementors, before the Fv3 Component "init" event occurs, before
 * the Fv3 tk Animator thread has started.
 * 
 * <h3>Matrices</h3>
 * 
 * The {@link World} handles perspective and view matrices via {@link
 * Camera}, while components optionally define model matrices.
 * 
 * Implementors of {@link Region} push and multiply the matrices of
 * all components according to their value returned from {@link
 * #pushFv3Matrix}.
 * 
 * @see Region
 * @see fv3.nui.Component
 * @see fv3.nui.Region
 */
public interface Component 
    extends fv3.math.Notation,
            fv3.tk.Fv3Component,
            lxl.Component
{
    /**
     * @return Parent
     */
    public Component getParent();
    /**
     * @param p Parent
     * @return This (not the argument, parent)
     */
    public Component setParent(Component p);

    public boolean hasFv3Matrix();
    public boolean hasNotFv3Matrix();
    /**
     * @return If not a region, return "has matrix" for the containing
     * region to push and multiply this matrix into the modelview.
     * Should be true when the component has a matrix and the
     * component will not push the matrix itself.  Should be false for
     * implementors of Region.
     */
    public boolean pushFv3Matrix();
    /**
     * Defines the coordinate space within this component.  Because
     * Fv3 employs the model view matrix stack in the definition of
     * component coordinate spaces, GL's model view stack depth limit
     * of 32 is imposed.  At most thirty two coordinate spaces may be
     * defined in a single component tree branch from root to leaf.
     * @return Null to inherit the coordinate space.
     */
    public Matrix getFv3Matrix();
    /**
     * @return The matrix composed by the ordered list of matrices in
     * this instance and its ancestors.  Null when this component and
     * its ancestors have no matrices.
     */
    public Matrix composeFv3Matrix();

    public DoubleBuffer getFv3MatrixBuffer();

    public boolean hasFv3Bounds();
    public boolean hasNotFv3Bounds();
    /**
     * Bounds in the containing coordinate space defined by the
     * composed matrix.
     */
    public Bounds getFv3Bounds();
    /**
     * Throws a runtime exception, argument or state, on error
     * conditions.  
     * @return This instance object
     */
    public Component setFv3Bounds();
    /**
     * The current state of component visibility.  A component is
     * visible by default (typically).
     */
    public boolean isVisible();
    /**
     * A component is responsible for unmapping its vertex list should
     * it become invisible.
     */
    public Component setVisible(boolean b);

    public Component translate(double x, double y, double z);

    public Component translate(Vector v);

    public Component scale(double s);

    public Component scale(double x, double y, double z);

    public Component rotate(AxisAngle a);
    /**
     * @param a Angle in radians
     */
    public Component rotateX(double a);
    /**
     * @param a Angle in radians
     */
    public Component rotateY(double a);
    /**
     * @param a Angle in radians
     */
    public Component rotateZ(double a);
    /**
     * Angles in radians
     */
    public Component rotate(double ax, double ay, double az);
}
