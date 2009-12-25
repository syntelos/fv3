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

import fv3.font.cff.Curve;
import fv3.font.cff.InstructionStream;

/**
 * A single closed loop.
 * Part of a glyph.
 * @author Tim Tyler
 * @author John Pritchard
 */
public final class CFFPath
    implements fv3.font.cff.PathConstants
{
    private static final int INCREMENT = 16;
    private static final int LINE_WIDTH = 0x100;
    private static final int LINE_WIDTH_THIN = 0xC0;

    private static final int CIRCULAR = 0x1234;
    private static final int SQUARE = 0x5678;

    private static int cfg_shape = CIRCULAR;


    private int number_of_curves;
    private Curve[] curve;
    private boolean direction;
    private boolean got_direction = false;
    private int instruction_pointer;
    private InstructionStream instruction_stream;
    private CFFPointList fepl;


    public CFFPath(InstructionStream is) {
        super();
        number_of_curves = 0;
        curve = new Curve[number_of_curves];
        instruction_stream = is;
        instruction_pointer = is.getInstructionPointer() - 1;
    }


    public void add(Curve e) {
        if (number_of_curves >= curve.length) {
            Curve[] new_array = new Curve[number_of_curves + INCREMENT];
            System.arraycopy(curve, 0, new_array, 0, curve.length);
            curve = new_array;
        }
        curve[number_of_curves++] = e;
    }
    public boolean contains(CFFPoint p) {
        return indexOf(p) >= 0;
    }
    public int indexOf(CFFPoint p) {
        CFFPointList fepl = getCFFPointList();

        return fepl.indexOf(p);
    }
    public CFFPoint safelyGetPoint(int i) {
        CFFPointList fepl = getCFFPointList();

        return fepl.safelyGetPoint(i);
    }
    public void setNumberOfElements(int number_of_elements) {
        this.number_of_curves = number_of_elements;
    }
    public int getNumberOfCurves() {
        return number_of_curves;
    }
    public void setPathElement(Curve[] path_element) {
        this.curve = path_element;
    }
    public Curve getCurve(int i) {
        return curve[i];
    }
    public void setInstructionPointer(int instruction_pointer) {
        this.instruction_pointer = instruction_pointer;
    }
    public int getInstructionPointer() {
        return instruction_pointer;
    }
    public CFFPointList getCFFPointList() {
        if (fepl == null) {
            fepl = new CFFPointList();
            for (int i = 0; i < number_of_curves; i++) {
                curve[i].simplyAddPoints(fepl);
            }
        }
        return fepl;
    }
    public void addPointsToCFFPointList(CFFPointList fepl) {
        for (int i = 0; i < number_of_curves; i++) {
            curve[i].simplyAddPoints(fepl);
        }
    }
    public void dump() {

        for (int i = number_of_curves; --i >= 0;) {

            Curve cv = curve[i];

            cv.dump();
        }
    }
}
