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

/**
 * A GL viewport.  The position of the viewport is from the origin in
 * the lower left corner (not top left).  
 * 
 * The default (unset or not programmatically defined) view is one
 * having no effect on visual output (GL).
 * 
 * @see Region
 */
public interface Viewport
    extends Component
{

    public Viewport set(int x, int y, int w, int h);

    public int x();
    public int getX();
    public Viewport x(int x);
    public Viewport setX(int x);
    public int y();
    public int getY();
    public Viewport y(int y);
    public Viewport setY(int y);
    public int w();
    public int getW();
    public Viewport w(int w);
    public Viewport setW(int w);
    public int h();
    public int getH();
    public Viewport h(int h);
    public Viewport setH(int h);

}
