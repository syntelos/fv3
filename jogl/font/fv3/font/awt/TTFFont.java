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
package fv3.font.awt;

import fv3.font.TTFFontReader;
import fv3.font.FontOptions;
import fv3.font.ttf.Glyf;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

/**
 * 
 * @author jdp
 */
public class TTFFont
    extends fv3.font.TTFFont
{
    private final static float Pad = 3.0f;
    private final static float PadV = 14f;

    private double scaleDt, scaleMinX, scaleMinY, scaleMaxX, scaleMaxY, scaleLeft, scaleTop;


    public TTFFont(String name, TTFFontReader reader){
        super(name,reader);
    }
    public TTFFont(String name, TTFFontReader reader, FontOptions opts){
        super(name,reader,opts);
    }


    public Path2D.Double getPath2d(char ch){
        TTFGlyph glyph = (TTFGlyph)this.get(ch);
        if (null != glyph)
            return glyph.getPath2d();
        else
            return null;
    }
    public void createGlyph(Glyf glyf, int index, int offset, int next, TTFFontReader reader){

        TTFGlyph glyph = new TTFGlyph(this,glyf,index,offset,next);

        this.add(glyph);
    }
    public void drawGrid(Graphics2D g, Font small, Font micro, Color bg, Color fg, double left, double top){

        double dt = this.scaleDt;
        double minX = this.scaleMinX;
        double minY = this.scaleMinY;
        double maxX = this.scaleMaxX;
        double maxY = this.scaleMaxY;

        if (left != this.scaleLeft || top != this.scaleTop){
            this.scaleLeft = left;
            this.scaleTop = top;

            double scale = this.getScale();
            double em = (this.getEm() * scale);
            dt = (em/10.0);


            minX = (this.getMinX() * scale);
            minY = (this.getMinY() * scale)+100;
            maxX = Math.min(left,((this.getMaxX() * scale)-100));
            maxY = Math.min(top,((this.getMaxY() * scale)-100));

            for (double t = 0.0, z = (minX - dt); t >= z; t -= dt)
                minX = t;
            for (double t = 0.0, z = (maxX + dt); t <= z; t += dt)
                maxX = t;
            for (double t = 0.0, z = (minY - dt); t >= z; t -= dt)
                minY = t;
            for (double t = 0.0, z = (maxY + dt); t <= z; t += dt)
                maxY = t;

            this.scaleDt = dt;
            this.scaleMinX = minX;
            this.scaleMinY = minY;
            this.scaleMaxX = maxX;
            this.scaleMaxY = maxY;
        }

        g.setFont(small);
        g.setColor(bg);

        Line2D line;

        for (double x = minX; x <= maxX; x += dt){

            line = new Line2D.Double(x,minY,x,maxY);

            g.draw(line);
            if (0.0 == x)
                g.draw(line);

            if (x < maxX)
                g.drawString(String.format("%4.1f",x),(float)(x),(float)(maxY+PadV));
        }
        for (double y = minY; y <= maxY; y += dt){

            line = new Line2D.Double(minX,y,maxX,y);

            g.draw(line);
            if (0.0 == y)
                g.draw(line);

            g.drawString(String.format("%4.1f",y),(float)(minX-48),(float)(y+Pad));

        }
    }
}
