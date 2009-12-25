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

import fv3.font.cff.InstructionStream;

/**
 * @author Tim Tyler
 * @author John Pritchard
 */
public class CFFPoint {

    public volatile int x;
    public volatile int y;

    private int instruction_pointer; // for use keeping track of things in the editor...


    public CFFPoint(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }

    public CFFPoint(InstructionStream is) {
        super();
        instruction_pointer = is.getInstructionPointer();
        this.x = -1;
        this.y = -1;
    }


    public void setX(int x) {
        this.x = x;
    }
    public int getX() {
        return x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getY() {
        return y;
    }
    public void setInstructionPointer(int instruction_pointer) {
        this.instruction_pointer = instruction_pointer;
    }
    public int getInstructionPointer() {
        return instruction_pointer;
    }
    public int squaredDistanceFrom(CFFPoint p) {
        int dx = (p.getX() - x) >> 1;
        int dy = (p.getY() - y) >> 1;

        return dx * dx + dy * dy;
    }
    public int quickDistanceFrom(CFFPoint p) {
        return (Math.abs(p.getX() - x) + Math.abs(p.getY() - y)) >> 1;
    }
    public boolean equals(Object o) {
        if (this == o)
            return true;
        else if (o instanceof CFFPoint){
            CFFPoint fep = (CFFPoint) o;
            return ((fep.x == x) && (fep.y == y));
        }
        else
            return false;
    }
    public int hashCode() {

        return ((this.x * 31) ^ (this.y * 31));
    }
}
