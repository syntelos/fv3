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

import fv3.font.CFFPath;
import fv3.font.CFFPoint;

/**
 * 
 * @author Tim Tyler
 * @author John Pritchard
 */
public final class InstructionClosePath
    extends Instruction
{


    InstructionClosePath() {
        super(CLOSE_PATH, "ClosePath");
    }

    public void execute(InstructionStream is) {

        // santiy check... 

        CFFPath fep = is.getFep();
        int number_of_curves = fep.getNumberOfCurves();
        if (number_of_curves > 0) {

            // fix the first point to be equal to the last point...
            CFFPoint end_point = fep.getCurve(number_of_curves - 1).getP4();

            is.getFep().getCurve(0).setP1(end_point);
            is.getCFFPathList().add(is.getFep());
        }
    }

    public void copy(InstructionStream is_in, InstructionStream is_out) {
        is_out.add(CLOSE_PATH);
        is_in.setQuit(true);
    }
}
