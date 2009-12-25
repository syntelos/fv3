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

import fv3.font.CFFPoint;

/**
 * 
 * @author Tim Tyler
 * @author John Pritchard
 */
public final class InstructionStraightLine
    extends Instruction
{


    InstructionStraightLine() {
        super(STRAIGHT_LINE, "StraightLine");
    }


    public void execute(InstructionStream is) {

        CFFPoint fept1 = is.getCFFPointList().add(is);
        int in1x = is.getNextInstruction();

        fept1.setX(in1x);
        int in1y = is.getNextInstruction();

        fept1.setY(in1y);
        is.getFep().add(new CurveStraightLine(GetCurrentPoint(), fept1));
        SetCurrentPoint(fept1);
    }
    public void copy(InstructionStream is_in, InstructionStream is_out) {
        is_out.add(STRAIGHT_LINE);
        is_out.add(is_in.getNextInstruction());
        is_out.add(is_in.getNextInstruction());
    }
    public void translate(InstructionStream is, int dx, int dy) {
        is.translateOnePoint(dx, dy);
    }
    public int numberOfCoordinates() {
        return 2;
    }
}
