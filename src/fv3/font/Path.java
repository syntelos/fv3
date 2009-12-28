/*
 * fv3
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
package fv3.font;

/**
 * Part of a {@link Glyph}.
 * 
 * @author John Pritchard
 */
public interface Path {

    /**
     * The first point coordinate in a path may be identical to the
     * last point coordinate in the immediately previous path in a
     * glyph.
     * 
     * @return A series of (X,Y) coordinates describing a set of
     * lines.  May be null.  Otherwise a minimum of one (X,Y)
     * coordinate pair.
     */
    public double[] points();

    public void destroy();

}
