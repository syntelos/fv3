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
 * A region is a branch node member of a component graph.  
 * 
 * The lxl component hierarchy is an experimental feature for dynamic
 * work space graphing.  With this feature set embedded in the Fv3
 * Component graph, an application or tool can spider the Fv3 World
 * and then elaborate on that graph visually or in data I/O.  The Fv3
 * Component graph exists in the lxl graph in the class {@link
 * fv3.Component}.
 * 
 * @see Component
 * @see fv3.nui.Region
 */
public interface Region
    extends Component,
            lxl.Hier
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
    public void setCurrent(Component c);

    public Component getParent();

    public void setParent(Component p);

    public List<Component> getChildren();

    public void setChildren(List<Component> c);

    public void dropChildren();

}
