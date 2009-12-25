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
import fv3.font.CFFPathList;
import fv3.font.CFFPoint;
import fv3.font.CFFPointList;

/**
 * @author Tim Tyler
 * @author John Pritchard
 */
public final class InstructionStream
    implements InstructionConstants
{
    private static final int INCREMENT = 16;

    private int[] stream;
    private int number = 0;

    private CFFPointList fepointlist;
    private CFFPathList fepathlist;
    private CFFPath fep; //  try to get rid of this...

    private int instruction_pointer;

    private boolean quit = false;
    private boolean needs_remaking = true;



    public InstructionStream() {
        super();
        this.instruction_pointer = 0;
        this.number = 10;
        this.stream = new int[number];
        this.fepointlist = new CFFPointList();
        this.fepathlist = new CFFPathList();
    }


    int getNextInstruction() {
        return this.stream[instruction_pointer++];
    }
    public boolean alive(){
        return (null != this.stream);
    }
    public void destroy(){
        this.stream = null;
        CFFPointList fepointlist = this.fepointlist;
        if (null != fepointlist){
            this.fepointlist = null;
            fepointlist.destroy();
        }
        CFFPathList fepathlist = this.fepathlist;
        if (null != fepathlist){
            this.fepathlist = null;
            fepathlist.destroy();
        }
        this.fep = null;
    }
    public void add(int ins) {
        if (this.instruction_pointer >= this.number) {
            int[] new_array = new int[this.number + INCREMENT];
            System.arraycopy(this.stream, 0, new_array, 0, this.number);
            this.stream = new_array;
            this.number += INCREMENT;
        }

        this.stream[this.instruction_pointer++] = ins;
    }
    public void translatePoint(int ip, int dx, int dy) {
        this.stream[ip++] += dx;
        this.stream[ip] += dy;
    }
    public void scalePoint(int ip, float fx, float fy) {
        this.stream[ip++] *= fx;
        this.stream[ip] *= fy;
    }
    private void setPoint(int ip, int x, int y) {
        this.stream[ip++] = x;
        this.stream[ip] = y;
    }
    public void setPoint(CFFPoint p, int x, int y) {
        this.setPoint(p.getInstructionPointer(), x, y);
    }
    public void translate(int dx, int dy) {
        this.fepointlist.translate(this,dx,dy);
    }
    public void scale(float dx, float dy) {
        this.fepointlist.scale(this,dx,dy);
    }
    public void execute() {

        this.quit = false;
        this.instruction_pointer = 0;

        do {
            int instruction_number = this.stream[this.instruction_pointer++];

            InstructionSet.Get(instruction_number).execute(this);

        } while (!this.quit);
    }
    private void copy(InstructionStream is_out, int ip) {

        this.quit = false;
        this.instruction_pointer = ip;
        do {
            int type = this.stream[this.instruction_pointer++];

            InstructionSet.Get(type).copy(this, is_out);

        } while (!this.quit);
    }
    private void translateStroke(int ip, int dx, int dy) {
        this.quit = false;
        this.instruction_pointer = ip;
        do {
            // ***very*** crude...
            int type = this.stream[this.instruction_pointer++];

            if (type == CLOSE_PATH) {
                this.quit = true;
            } else {
                InstructionSet.Get(type).translate(this, dx, dy);
            }
        } while (!this.quit);
    }
    public boolean isInNeedOfRemaking() {
        return this.needs_remaking;
    }
    public void setRemakeFlag(boolean needs_remaking) {
        this.needs_remaking = needs_remaking;
    }
    public void add(CFFPath p, InstructionStream is_out) {
        this.copy(is_out, p.getInstructionPointer());
    }
    /**
     * **** Don't call this directly ****
     */
    public CFFPathList getCFFPathList() {
        return this.fepathlist;
    }
    /**
     * **** Don't call this directly ****
     */
    public CFFPointList getCFFPointList() {
        return this.fepointlist;
    }
    void translateOnePoint(int dx, int dy) {
        this.stream[this.instruction_pointer++] += dx;
        this.stream[this.instruction_pointer++] += dy;
    }
    public int getInstructionAt(int offset) {
        return this.stream[offset];
    }
    public void setInstructionAt(int offset, int value) {
        this.stream[offset] = value;
    }
    public void deleteInstructionsAt(int offset, int length) {
        System.arraycopy(this.stream, offset + length, this.stream, offset, number - offset - length);
        this.number = number - length;
    }
    public void setInstructionPointer(int instruction_pointer) {
        this.instruction_pointer = instruction_pointer;
    }
    public int getInstructionPointer() {
        return this.instruction_pointer;
    }
    void setFep(CFFPath fep) {
        this.fep = fep;
    }
    CFFPath getFep() {
        return fep;
    }
    void setQuit(boolean quit) {
        this.quit = quit;
    }
    boolean isQuitting() {
        return quit;
    }
    public int getIndexOfCurveContainingPoint(CFFPoint point) {
        int index_of_point = point.getInstructionPointer();
        this.instruction_pointer = 0;
        int type;

        do {
            int return_value = this.instruction_pointer;
            type = this.stream[this.instruction_pointer++];

            this.instruction_pointer += InstructionSet.Get(type).numberOfCoordinates();
            if (this.instruction_pointer > index_of_point) {
                return return_value;
            }
        }
        while (type != END_GLYPH);

        throw new RuntimeException("Point not found");
    }
    public void dump() {
        this.instruction_pointer = 0;
        int type;

        do {
            type = this.stream[this.instruction_pointer++];
            System.err.println("INSTR: " + InstructionSet.Get(type));
            this.instruction_pointer += InstructionSet.Get(type).numberOfCoordinates();
        }
        while (type != END_GLYPH);
    }
}
