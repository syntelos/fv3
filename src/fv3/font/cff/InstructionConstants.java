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
 * @author Tim Tyler
 * @author John Pritchard
 */
public interface InstructionConstants {

    int ERROR            =  0;
    int GLYPH_NUMBER     =  1;
    int GLYPH_NEXT       =  2;
    int STRAIGHT_LINE    =  3;
    int QUADRATIC_BEZIER =  4;
    int CUBIC_BEZIER     =  5;
    int END_GLYPH        =  6;
    int END_FONT         =  7;
    int CLOSE_PATH       =  8;
    int OPEN_PATH        =  9;
}
