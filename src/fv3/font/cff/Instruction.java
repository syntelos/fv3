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
 * Represents a single instruction
 * @author Tim Tyler
 * @author John Pritchard
 */
public class Instruction
    extends Object
    implements InstructionConstants
{
    private volatile static CFFPoint CurrentPoint;

    protected static void SetCurrentPoint(CFFPoint p) {
        CurrentPoint = p;
    }
    protected static CFFPoint GetCurrentPoint() {
        return CurrentPoint;
    }


    public final int number;
    public final String name;


    Instruction(int n, String d) {
        number = n;
        name = d;
    }


    public void copy(InstructionStream is_in, InstructionStream is_out) {
    }
    public void execute(InstructionStream is) {
    }
    public void translate(InstructionStream is_in, int dx, int dy) {
    }
    public int numberOfCoordinates() {
        return 0;
    }
    public String toString() {
        return name;
    }
}
