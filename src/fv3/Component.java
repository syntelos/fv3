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

import java.nio.FloatBuffer;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * A component that is not a {@link Region} is a leaf in a component
 * graph.  At least for the benefit of discussion, a component is a GL
 * vertex list.
 * @see Region
 * @see fv3.nui.Component
 */
public interface Component 
    extends fv3tk.Fv3Component,
            lxl.Component
{
    /**
     * Called after construction and after being added to the
     * component graph, and before the Fv3Tk init.  The implementor
     * may find this step useful, or may ignore it completely.
     */
    public void init(Region parent);

    public boolean hasFv3Matrix();
    public boolean hasNotFv3Matrix();
    /**
     * Defines the coordinate space within this component.  Because
     * Fv3 employs the model view matrix stack in the definition of
     * component coordinate spaces, GL's model view stack depth limit
     * of 32 is imposed.  At most thirty two coordinate spaces may be
     * defined in a single component tree branch from root to leaf.
     * @return Null to inherit the coordinate space.
     */
    public Matrix4f getFv3Matrix();

    public FloatBuffer getFv3MatrixBuffer();

    public boolean hasFv3Bounds();
    public boolean hasNotFv3Bounds();
    /**
     * Bounds in the coordinate space within this component. 
     */
    public Bounds3f getFv3Bounds();

    public FloatBuffer getFv3BoundsBuffer();
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

    public Component translate(float x, float y, float z);

    public Component translate(Vector3f v);

    public Component scale(float s);

    public Component rotate(Quat4f q);

    public Component rotate(AxisAngle4f a);

}
