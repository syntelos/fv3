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
 * Quadratic
 * 
 * @author John Pritchard
 */
public class TTFPath
    extends Object
    implements Path
{
    public final boolean isCubic, isQuadratic; 

    public final double startX, startY, controlX, controlY, controlX2, controlY2, endX, endY;

    /**
     * Quadratic
     */
    public TTFPath(double startX, double startY,
                   double controlX, double controlY,
                   double endX, double endY)
    {
        super();
        this.isQuadratic = true;
        this.isCubic = false;
        this.startX = startX;
        this.startY = startY;
        this.controlX = controlX;
        this.controlY = controlY;
        this.controlX2 = 0.0;
        this.controlY2 = 0.0;
        this.endX = endX;
        this.endY = endY;
    }

    /**
     * Cubic
     */
    public TTFPath(double startX, double startY,
                   double controlX, double controlY,
                   double controlX2, double controlY2,
                   double endX, double endY)
    {
        super();
        this.isQuadratic = false;
        this.isCubic = true;
        this.startX = startX;
        this.startY = startY;
        this.controlX = controlX;
        this.controlY = controlY;
        this.controlX2 = controlX2;
        this.controlY2 = controlY2;
        this.endX = endX;
        this.endY = endY;
    }

    public void destroy(){
    }
    public double[] points(){
        return null;
    }
    public String toString(){
        if (this.isQuadratic)
            return String.format("TTFPath(%f, %f, %f, %f, %f, %f)", this.startX, this.startY, this.controlX, this.controlY, this.endX, this.endY);
        else
            return String.format("TTFPath(%f, %f, %f, %f, %f, %f, %f, %f)", this.startX, this.startY, this.controlX, this.controlY, this.controlX2, this.controlY2, this.endX, this.endY);
    }
}
