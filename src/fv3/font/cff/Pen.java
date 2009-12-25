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
public abstract class Pen {

    private int width;



    public int getTop() {
        return width;
    }

    public int getBottom() {
        return width;
    }

    public int getLeft() {
        return width;
    }

    public int getRight() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    boolean isZeroWidth() {
        if (getTop() != 0) {
            return false;
        }
    
        if (getBottom() != 0) {
            return false;
        }
    
        if (getLeft() != 0) {
            return false;
        }
    
        if (getRight() != 0) {
            return false;
        }

        return true;
    }
  
    public int hashCode() {
        int hash_code = (width << 3) - (width << 9) ^ 0x19975468;

        return hash_code;
    }
}
