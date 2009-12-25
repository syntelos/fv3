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
package fv3.font.cff;

/**
 * DisplayOptions -default values...
 * @author Tim Tyler
 * @author John Pritchard
 */
public interface DisplayOptionsConstants {
    int DEFAULT_SLANT = 0x00;
    int DEFAULT_EXPAND = 0x400;
    boolean DEFAULT_HINT = true;
    boolean DEFAULT_FILL = true;
    Pen DEFAULT_PEN = new PenRound(0x300);
    Coords  DEFAULT_COORDS = new Coords(40, 40, 12, 20);
}
