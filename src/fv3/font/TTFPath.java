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

import fv3.font.ttf.Point;

/**
 * Quadratic
 * 
 * @author John Pritchard
 */
public class TTFPath
    extends Object
    implements Path<TTFFont,TTFGlyph>
{
    public final boolean isStraight, isCubic, isQuadratic, isSynthetic; 

    public final Point point;

    public double startX, startY, controlX, controlY, controlX2, controlY2, endX, endY;

    private double[] points;

    /**
     * Straight line
     */
    public TTFPath(Point point,
                   double startX, double startY,
                   double endX, double endY)
    {
        super();
        this.isStraight = true;
        this.isQuadratic = false;
        this.isCubic = false;
        this.isSynthetic = false;
        this.point = point;
        this.startX = startX;
        this.startY = startY;
        this.controlX = 0.0;
        this.controlY = 0.0;
        this.controlX2 = 0.0;
        this.controlY2 = 0.0;
        this.endX = endX;
        this.endY = endY;
    }

    /**
     * Quadratic
     */
    public TTFPath(Point point, boolean synthetic,
                   double startX, double startY,
                   double controlX, double controlY,
                   double endX, double endY)
    {
        super();
        this.isStraight = false;
        this.isQuadratic = true;
        this.isCubic = false;
        this.isSynthetic = synthetic;
        this.point = point;
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
    public TTFPath(Point point,
                   double startX, double startY,
                   double controlX, double controlY,
                   double controlX2, double controlY2,
                   double endX, double endY)
    {
        super();
        this.isStraight = false;
        this.isQuadratic = false;
        this.isCubic = true;
        this.isSynthetic = false;
        this.point = point;
        this.startX = startX;
        this.startY = startY;
        this.controlX = controlX;
        this.controlY = controlY;
        this.controlX2 = controlX2;
        this.controlY2 = controlY2;
        this.endX = endX;
        this.endY = endY;
    }


    /**
     * Called from {@link TTFGlyph} on contour change.  Reference
     * "this" is the first element of the contour.
     */
    TTFPath close(TTFGlyph glyph, TTFPath last){

        if (this.point.contour == last.point.contour){

            this.startX = last.endX;

            this.startY = last.endY;
        }
        //         else
        //             throw new IllegalStateException();

        return last;
    }
    public void init(TTFFont font, TTFGlyph glyph, FontOptions opts){
        double scale = font.getScale();
        if (this.isStraight){
            double x0 = (this.startX * scale);
            double y0 = (this.startY * scale);
            double x3 = (this.endX * scale);
            double y3 = (this.endY * scale);
            if (x0 != x3 || y0 != y3){
                double[] points = new double[4];
                points[0] = x0;
                points[1] = y0;
                points[2] = x3;
                points[3] = y3;
                this.points = points;
            }
        }
        else if (this.isQuadratic){

            double x0 = (this.startX * scale);
            double y0 = (this.startY * scale);
            double x1 = (this.controlX * scale);
            double y1 = (this.controlY * scale);
            double x3 = (this.endX * scale);
            double y3 = (this.endY * scale);

            double x_b = 1 * x0 - 2 * x1 + 1 * x3;
            double x_c = -2 * x0 + 2 * x1;
            double x_d = x0;
            double y_b = 1 * y0 - 2 * y1 + 1 * y3;
            double y_c = -2 * y0 + 2 * y1;
            double y_d = y0;

            int step = 12; /*(TODO) Range step to scale
                            * (a) project fair value
                            * (b) drop points not significant
                            */
            double em = font.getEm();
            double dt = (em/step);
            double[] points = new double[(step<<1)+2];

            int xp = 0;
            int yp = 1;
            points[xp] = x0;
            points[yp] = y0;
            xp += 2;
            xp += 2;

            for (double t = dt; t < em; t += dt, xp += 2, yp += 2){

                double x = (((((x_b * t) / em) + x_c) * t) / em) + x_d;
                double y = (((((y_b * t) / em) + y_c) * t) / em) + y_d;

                points[xp] = x;
                points[yp] = y;
            }
            this.points = points;
        }
        else {


            double x0 = (this.startX * scale);
            double y0 = (this.startY * scale);
            double x1 = (this.controlX * scale);
            double y1 = (this.controlY * scale);
            double x2 = (this.controlX2 * scale);
            double y2 = (this.controlY2 * scale);
            double x3 = (this.endX * scale);
            double y3 = (this.endY * scale);
            double x_a = -x0 + 3 * x1 - 3 * x2 + x3;
            double x_b = 3 * x0 - 6 * x1 + 3 * x2;
            double x_c = -3 * x0 + 3 * x1;
            double x_d = x0;
            double y_a = -y0 + 3 * y1 - 3 * y2 + y3;
            double y_b = 3 * y0 - 6 * y1 + 3 * y2;
            double y_c = -3 * y0 + 3 * y1;
            double y_d = y0;

            int step = 12; /*(TODO) Range step to scale
                            */
            double em = font.getEm();
            double dt = (em/step);
            double[] points = new double[(step<<1)+2];

            int xp = 0;
            int yp = 1;
            points[xp] = x0;
            points[yp] = y0;
            xp += 2;
            xp += 2;

            for (double t = dt; t < em; t += dt, xp += 2, yp += 2){

                double x = ((((((((x_a * t) / em) + x_b) * t) / em) + x_c) * t) / em) + x_d;
                double y = ((((((((y_a * t) / em) + y_b) * t) / em) + y_c) * t) / em) + y_d;

                points[xp] = x;
                points[yp] = y;
            }
        }
    }
    public void destroy(){
    }
    public double[] points(){
        return this.points;
    }
    public String toString(){
        if (this.isStraight)
            return String.format("TTFPath(%f, %f, %f, %f)", this.startX, this.startY, this.endX, this.endY);
        else if (this.isQuadratic)
            return String.format("TTFPath(%f, %f, %f, %f, %f, %f)", this.startX, this.startY, this.controlX, this.controlY, this.endX, this.endY);
        else
            return String.format("TTFPath(%f, %f, %f, %f, %f, %f, %f, %f)", this.startX, this.startY, this.controlX, this.controlY, this.controlX2, this.controlY2, this.endX, this.endY);
    }
}
