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
 * <h3>Hierarchical Object Model</h3>
 * 
 * Fv3 employs the {@link lxl.Hier lxl component hierarchy} for an
 * object model as a dynamic work space.
 * 
 * Like a Document Object Model, the Fv3 component graph permits an
 * application or tool to spider the Fv3 World -- possibly to
 * elaborate on the lxl graph with new classes of branches.
 * 
 * The Fv3 Component graph exists in the lxl graph at the identifying
 * class {@link fv3.Component}.
 * 
 * 
 * @see Component
 * @see fv3.nui.Region
 */
public interface Region
    extends Component,
            lxl.Hier
{
    /**
     * The position of the view in the list of children should be zero.
     * @return Negative one for not found, otherwise a zero- positive
     * index into the list of children.
     */
    public int indexOfView();
    /**
     * @return Index of view is not negative one.
     */
    public boolean hasView();
    /**
     * @return Null for not found.
     */
    public Viewport getView();
    /**
     * Replace or insert a non- null view in the list of component children.
     * @param view If null, no effect.
     */
    public Region setView(Viewport view);

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

    public Component getParent();

    public Region setParent(Component p);

    public List<Component> getChildren();

    public Region setChildren(List<Component> c);

    public Region dropChildren();

}
