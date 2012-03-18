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
package fv3.font.ttf;

import fv3.font.TTFGlyph;
import fv3.font.TTFPath;

import java.lang.Math;

/**
 * Point identification in Path.
 * 
 * @author John Pritchard
 */
public final class Point {

    public final int contour;

    public int start = -1, control = -1, end = -1, index = -1;


    public Point(int contour){
        super();
        this.contour = contour;
    }
    public Point(TTFPath first, TTFPath last){
        super();
        this.contour = last.point.contour;
        this.index = (last.point.index + 1);
        this.start = last.point.end;
        this.end = first.point.start;
    }
    /**
     * Compound copy
     */
    public Point(int contour, int base, Point copy){
        super();
        this.contour = contour;
        if (-1 != copy.start)
            this.start = base+copy.start;
        if (-1 != copy.end)
            this.end = base+copy.end;
    }


    public int high(){

        return Math.max(Math.max(this.start,this.control),this.end);
    }
    public Point close(TTFGlyph glyph){
        this.index = glyph.getLength();
        return this;
    }
    public String stringMoveTo(double startX, double startY){

        if (-1 != this.start)
            return String.format("Path: %3d; Contour: %2d; MoveTo(%d:{%f,%f});",this.index,this.contour,this.start,startX,startY);
        else
            return String.format("Path: %3d; Contour: %2d; MoveTo({%f,%f});",this.index,this.contour,startX,startY);
    }
    public String stringLineTo(double endX, double endY){

        if (-1 != this.end)
            return String.format("Path: %3d; Contour: %2d; LineTo(%d:{%f,%f});",this.index,this.contour,this.end,endX,endY);
        else
            return String.format("Path: %3d; Contour: %2d; LineTo({%f,%f});",this.index,this.contour,endX,endY);
    }
    public String stringQuadTo(double controlX, double controlY, double endX, double endY){

        if (-1 != this.control){
            if (-1 != this.end)
                return String.format("Path: %3d; Contour: %2d; QuadTo(%d:{%f,%f},%d:{%f,%f});",this.index,this.contour,this.control,controlX,controlY,this.end,endX,endY);
            else
                return String.format("Path: %3d; Contour: %2d; QuadTo(%d:{%f,%f},{%f,%f});",this.index,this.contour,this.control,controlX,controlY,endX,endY);
        }
        else if (-1 != this.end)
            return String.format("Path: %3d; Contour: %2d; QuadTo({%f,%f},%d:{%f,%f});",this.index,this.contour,controlX,controlY,this.end,endX,endY);
        else
            return String.format("Path: %3d; Contour: %2d; QuadTo({%f,%f},{%f,%f});",this.index,this.contour,controlX,controlY,endX,endY);
    }
}
