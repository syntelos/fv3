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
 * 
 * @author Tim Tyler
 * @author John Pritchard
 */
public abstract class InstructionSet
    extends Object
    implements InstructionConstants
{

    public static Instruction Get(int instruction_number) {

        return SET[instruction_number];
    }



    private final static Instruction[] SET;
    static {
        SET = new Instruction[OPEN_PATH+1];
        Instruction Error = new InstructionError();
        SET[ERROR]            = Error;
        SET[GLYPH_NEXT]       = Error;
        SET[GLYPH_NUMBER]     = Error;
        SET[STRAIGHT_LINE]    = new InstructionStraightLine();
        SET[QUADRATIC_BEZIER] = new InstructionQuadraticBezier();
        SET[CUBIC_BEZIER]     = new InstructionCubicBezier();
        SET[OPEN_PATH]        = new InstructionOpenPath();
        SET[CLOSE_PATH]       = new InstructionClosePath();
        SET[END_GLYPH]        = new InstructionEndGlyph();
        SET[END_FONT]         = new InstructionEndFont();
    }

}
