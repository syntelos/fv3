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

/**
 * Font glyph normal dimensions requested by the caller.  When width
 * or height are zero, no scaling is performed.
 * 
 * @author John Pritchard
 */
public class FontOptions
    extends Object
{

    public final double width, height, depth;


    public FontOptions(double w, double h, double d){
        super();
        if (0.0 <= w && 0.0 <= h && 0.0 <= d){
            this.width = w;
            this.height = h;
            this.depth = d;
        }
        else
            throw new IllegalArgumentException();
    }
    public FontOptions(double w, double h){
        this(w,h,0);
    }
    public FontOptions(){
        this(0,0,0);
    }
}
