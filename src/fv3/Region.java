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

import lxl.List;

/**
 * A region is a component with children.
 * 
 * <h3>Operation</h3>
 * 
 * The component graph is populated in the constructors of
 * implementors, before the Fv3 Component "init" event occurs, before
 * the Fv3 tk Animator thread has started.
 * 
 * 
 * @see Component
 * @see fv3.nui.Region
 */
public interface Region
    extends Component
{

    /**
     * @return A descendant of this that is current for input events,
     * or null for none.
     */
    public Component getCurrent();
    /**
     * @param c A descendant of this to be made current for input
     * events, or null for none.
     */
    public Region setCurrent(Component c);

    public List<Component> getFv3Children();

    public Region setFv3Children(List<Component> c);

    public Region dropFv3Children();

}
