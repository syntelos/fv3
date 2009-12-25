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
import fv3.font.CFFPointList;

/**
 * 
 * @author Tim Tyler
 * @author John Pritchard
 */
public final class InstructionQuadraticBezier
    extends Instruction
{


    InstructionQuadraticBezier() {
        super(QUADRATIC_BEZIER, "QuadraticBezier");
    }


    public void execute(InstructionStream is) {

        CFFPointList point_list = is.getCFFPointList();

        CFFPoint fept1 = point_list.add(is);
        int in1x = is.getNextInstruction();

        fept1.setX(in1x);
        int in1y = is.getNextInstruction();

        fept1.setY(in1y);
        CFFPoint fept2 = point_list.add(is);
        int in2x = is.getNextInstruction();

        fept2.setX(in2x);
        int in2y = is.getNextInstruction();

        fept2.setY(in2y);
        is.getFep().add(new CurveBezierQuadratic(GetCurrentPoint(), fept1, fept2));
        SetCurrentPoint(fept2);
    }

    public void copy(InstructionStream is_in, InstructionStream is_out) {
        is_out.add(QUADRATIC_BEZIER);
        is_out.add(is_in.getNextInstruction());
        is_out.add(is_in.getNextInstruction());
        is_out.add(is_in.getNextInstruction());
        is_out.add(is_in.getNextInstruction());
    }

    public void translate(InstructionStream is, int dx, int dy) {
        is.translateOnePoint(dx, dy);
        is.translateOnePoint(dx, dy);
    }

    public int numberOfCoordinates() {
        return 4;
    }

}
