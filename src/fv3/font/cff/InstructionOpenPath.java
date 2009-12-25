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
public final class InstructionOpenPath
    extends Instruction
{


    InstructionOpenPath() {
        super(OPEN_PATH, "OpenPath");
    }


    public void execute(InstructionStream is) {

        is.setFep(new CFFPath(is));

        SetCurrentPoint(new CFFPoint(0, 0));
    }

    public void copy(InstructionStream is_in, InstructionStream is_out) {

        is_out.add(OPEN_PATH);
    }
}
