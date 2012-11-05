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

import fv3.font.ttf.CompoundGlyph;
import fv3.font.ttf.Point;

/**
 * Quadratic
 * 
 * @author John Pritchard
 */
public final class TTFPath
    extends Object
    implements Path<TTFFont,TTFGlyph>
{
    public final boolean isStraight, isCubic, isQuadratic, isSynthetic; 

    public final Point point;

    public float startX, startY, controlX, controlY, controlX2, controlY2, endX, endY;


    /**
     * Straight line
     */
    public TTFPath(Point point,
                   float startX, float startY,
                   float endX, float endY)
    {
        super();
        this.isStraight = true;
        this.isQuadratic = false;
        this.isCubic = false;
        this.isSynthetic = false;
        this.point = point;
        this.startX = startX;
        this.startY = startY;
        this.controlX = 0.0f;
        this.controlY = 0.0f;
        this.controlX2 = 0.0f;
        this.controlY2 = 0.0f;
        this.endX = endX;
        this.endY = endY;
    }
    /**
     * Synthetic straight line
     */
    public TTFPath(Point point, TTFPath first, TTFPath last)
    {
        super();
        this.isStraight = true;
        this.isQuadratic = false;
        this.isCubic = false;
        this.isSynthetic = true;
        this.point = point;
        this.startX = last.endX;
        this.startY = last.endY;
        this.controlX = 0.0f;
        this.controlY = 0.0f;
        this.controlX2 = 0.0f;
        this.controlY2 = 0.0f;
        this.endX = first.startX;
        this.endY = first.startY;
    }
    /**
     * Compound copy
     */
    public TTFPath(CompoundGlyph cg, Point point, TTFPath copy)
    {
        super();
        this.isStraight = copy.isStraight;
        this.isQuadratic = copy.isQuadratic;
        this.isCubic = copy.isCubic;
        this.isSynthetic = copy.isSynthetic;
        this.point = point;
        float[] dst = cg.transform(this.source(),copy.source());
        if (this.isStraight){
            this.startX    = dst[0];
            this.startY    = dst[1];
            this.controlX  = 0.0f;
            this.controlY  = 0.0f;
            this.controlX2 = 0.0f;
            this.controlY2 = 0.0f;
            this.endX      = dst[2];
            this.endY      = dst[3];
        }
        else if (this.isQuadratic){
            this.startX    = dst[0];
            this.startY    = dst[1];
            this.controlX  = dst[2];
            this.controlY  = dst[3];
            this.controlX2 = 0.0f;
            this.controlY2 = 0.0f;
            this.endX      = dst[4];
            this.endY      = dst[5];
        }
        else if (this.isCubic){
            this.startX    = dst[0];
            this.startY    = dst[1];
            this.controlX  = dst[2];
            this.controlY  = dst[3];
            this.controlX2 = dst[4];
            this.controlY2 = dst[5];
            this.endX      = dst[6];
            this.endY      = dst[7];
        }
    }
    /**
     * Quadratic
     */
    public TTFPath(Point point, boolean synthetic,
                   float startX, float startY,
                   float controlX, float controlY,
                   float endX, float endY)
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
        this.controlX2 = 0.0f;
        this.controlY2 = 0.0f;
        this.endX = endX;
        this.endY = endY;
    }
    /**
     * Cubic
     */
    public TTFPath(Point point,
                   float startX, float startY,
                   float controlX, float controlY,
                   float controlX2, float controlY2,
                   float endX, float endY)
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

            if (0.0f == this.startX && 0.0f == this.startY){

                this.startX = last.endX;

                this.startY = last.endY;

                return last;
            }
            else if (0.0f == last.endX && 0.0f == last.endY){

                last.endX = this.startX;

                last.endY = this.startY;

                return last;
            }
            else {
                TTFPath synthetic = new TTFPath((new Point(this,last)),this,last);
                glyph.add(synthetic);
                return synthetic;
            }
        }
        else
            throw new IllegalStateException();
    }
    public void init(TTFFont font, TTFGlyph glyph, FontOptions opts){

        final float scale = font.getScale();

        if (this.isStraight){

            final float x0 = (this.startX * scale);
            final float y0 = (this.startY * scale);
            final float x3 = (this.endX * scale);
            final float y3 = (this.endY * scale);

            if (x0 != x3 || y0 != y3){

                glyph.moveTo(x0,y0);
                glyph.lineTo(x3,y3);
            }

        }
        else if (this.isQuadratic){

            final float x0 = (this.startX * scale);
            final float y0 = (this.startY * scale);
            final float x1 = (this.controlX * scale);
            final float y1 = (this.controlY * scale);
            final float x3 = (this.endX * scale);
            final float y3 = (this.endY * scale);

            glyph.moveTo(x0,y0);
            glyph.quadTo(x1,y1,x3,y3);
        }
        else {

            final float x0 = (this.startX * scale);
            final float y0 = (this.startY * scale);
            final float x1 = (this.controlX * scale);
            final float y1 = (this.controlY * scale);
            final float x2 = (this.controlX2 * scale);
            final float y2 = (this.controlY2 * scale);
            final float x3 = (this.endX * scale);
            final float y3 = (this.endY * scale);

            glyph.moveTo(x0,y0);
            glyph.cubicTo(x1,y1,x2,y2,x3,y3);
        }
    }
    public void destroy(){
    }

    public final float[] source(){
        if (this.isStraight){
            return new float[]{this.startX,this.startY,this.endX,this.endY};
        }
        else if (this.isQuadratic){
            return new float[]{this.startX,this.startY,this.controlX,this.controlY,this.endX,this.endY};
        }
        else {
            return new float[]{this.startX,this.startY,this.controlX,this.controlY,this.controlX2,this.controlY2,this.endX,this.endY};
        }
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
