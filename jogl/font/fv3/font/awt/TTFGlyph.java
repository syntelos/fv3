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

import fv3.font.TTFPath;
import fv3.font.FontOptions;
import fv3.font.ttf.Glyf;
import fv3.font.ttf.Head;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

/**
 * 
 * @author jdp
 */
public class TTFGlyph
    extends fv3.font.TTFGlyph
{
    private final static double ControlR = 3.0;
    private final static double ControlD = (2.0 * ControlR);
    private final static float PathPointDX = 14f;
    private final static float PathPointDY = 8f;


    protected Path2D.Double path2d, grid2d;

    private int path2count;

    private String[] path2desc;


    protected TTFGlyph(TTFFont font, Glyf table, int index, int offset, int next){
        super(font,table,index,offset,next);
    }


    public void drawGrid(Graphics2D g, Font small, Font micro, Color bg, Color fg){

        double scale = this.font.getScale();

        g.setFont(micro);
        g.setColor(fg);

        Line2D line;
        Ellipse2D on, off;
        double x0, y0, x1, y1, x2, y2;
        float x0p, y0p, x1p, y1p, x2p, y2p;

        for (int idx = 0, count = this.getLength(); idx < count; idx++){

            TTFPath path = this.get(idx);

            if (path.isStraight){

                x0 = (path.startX * scale)-ControlR;
                y0 = (path.startY * scale)-ControlR;

                on = new Ellipse2D.Double(x0, y0, ControlD, ControlD);
                g.fill(on);
                if (-1 != path.point.start){
                    x0p = (float)(x0-PathPointDX);
                    y0p = (float)(y0-PathPointDY);

                    line = new Line2D.Double(x0,y0,x0p,y0p);
                    g.draw(line);
                    if (11.0 > path.point.start)
                        x0p -= PathPointDY;
                    else
                        x0p -= PathPointDX;
                    g.drawString(String.valueOf(path.point.start),x0p,y0p);
                }

                x2 = (path.endX * scale)-ControlR;
                y2 = (path.endY * scale)-ControlR;

                on = new Ellipse2D.Double(x2, y2, ControlD, ControlD);
                g.fill(on);
                if (-1 != path.point.end){
                    x2p = (float)(x2-PathPointDX);
                    y2p = (float)(y2-PathPointDY);

                    line = new Line2D.Double(x2,y2,x2p,y2p);
                    g.draw(line);
                    if (11.0 > path.point.end)
                        x2p -= PathPointDY;
                    else
                        x2p -= PathPointDX;
                    g.drawString(String.valueOf(path.point.end),x2p,y2p);
                }

            }
            else {

                x0 = (path.startX * scale)-ControlR;
                y0 = (path.startY * scale)-ControlR;

                on = new Ellipse2D.Double(x0, y0, ControlD, ControlD);
                g.fill(on);
                if (-1 != path.point.start){
                    x0p = (float)(x0-PathPointDX);
                    y0p = (float)(y0-PathPointDY);

                    line = new Line2D.Double(x0,y0,x0p,y0p);
                    g.draw(line);
                    if (11.0 > path.point.start)
                        x0p -= PathPointDY;
                    else
                        x0p -= PathPointDX;
                    g.drawString(String.valueOf(path.point.start),x0p,y0p);
                }

                x1 = (path.controlX * scale)-ControlR;
                y1 = (path.controlY * scale)-ControlR;

                off = new Ellipse2D.Double(x1, y1, ControlD, ControlD);
                g.draw(off);
                if (-1 != path.point.control){
                    x1p = (float)(x1-PathPointDX);
                    y1p = (float)(y1-PathPointDY);

                    line = new Line2D.Double(x1,y1,x1p,y1p);
                    g.draw(line);
                    if (11.0 > path.point.control)
                        x1p -= PathPointDY;
                    else
                        x1p -= PathPointDX;
                    g.drawString(String.valueOf(path.point.control),x1p,y1p);
                }

                x2 = (path.endX * scale)-ControlR;
                y2 = (path.endY * scale)-ControlR;

                on = new Ellipse2D.Double(x2, y2, ControlD, ControlD);
                g.fill(on);
                if (-1 != path.point.end){
                    x2p = (float)(x2-PathPointDX);
                    y2p = (float)(y2-PathPointDY);

                    line = new Line2D.Double(x2,y2,x2p,y2p);
                    g.draw(line);
                    if (11.0 > path.point.end)
                        x2p -= PathPointDY;
                    else
                        x2p -= PathPointDX;
                    g.drawString(String.valueOf(path.point.end),x2p,y2p);
                }
            }
        }
    }
    public void drawOutline(Graphics2D g){
        Path2D.Double path2d = this.path2d;
        if (null != path2d)
            g.draw(path2d);
    }
    public final boolean hasPath2d(){
        return (null != this.path2d);
    }
    public final Path2D.Double getPath2d(){
        return this.path2d;
    }
    public String[] getPath2dDescription(){
        String[] path2desc = this.path2desc;
        if (null == path2desc){
            int pc = 0;
            path2desc = new String[this.path2count];

            double scale = this.font.getScale();

            TTFPath last = null;
            double x0, y0, x1, y1, x2, y2;
            for (TTFPath path: this){
                x0 = path.startX;
                y0 = path.startY;
                x1 = path.controlX;
                y1 = path.controlY;
                x2 = path.endX;
                y2 = path.endY;

                if (null != last){
                    if (last.endX != x0 || last.endY != y0){
                        x0 *= scale;
                        y0 *= scale;
                        path2desc[pc++] = path.point.stringMoveTo(x0,y0);
                    }
                }
                else {
                    x0 *= scale;
                    y0 *= scale;
                    path2desc[pc++] = path.point.stringMoveTo(x0,y0);
                }

                if (path.isStraight){
                    x2 *= scale;
                    y2 *= scale;
                    path2desc[pc++] = path.point.stringLineTo(x2,y2);
                }
                else {
                    x1 *= scale;
                    y1 *= scale;
                    x2 *= scale;
                    y2 *= scale;
                    path2desc[pc++] = path.point.stringQuadTo(x1,y1,x2,y2);
                }
                last = path;
            }
            this.path2desc = path2desc;
        }
        return path2desc;
    }
    public void init(FontOptions options) {
        super.init(options);
        double scale = this.font.getScale();
        this.path2count = 0;
        Path2D.Double path2d = new Path2D.Double();
        TTFPath last = null;
        double x0, y0, x1, y1, x2, y2;
        for (TTFPath path: this)
        {
            x0 = path.startX;
            y0 = path.startY;
            x1 = path.controlX;
            y1 = path.controlY;
            x2 = path.endX;
            y2 = path.endY;

            if (null != last){
                if (last.endX != x0 || last.endY != y0){
                    x0 *= scale;
                    y0 *= scale;
                    path2d.moveTo(x0,y0);
                    this.path2count++;
                }
            }
            else {
                x0 *= scale;
                y0 *= scale;
                path2d.moveTo(x0,y0);
                this.path2count++;
            }

            if (path.isStraight){
                x2 *= scale;
                y2 *= scale;
                path2d.lineTo(x2,y2);
                this.path2count++;
            }
            else {
                x1 *= scale;
                y1 *= scale;
                x2 *= scale;
                y2 *= scale;
                path2d.quadTo(x1,y1,x2,y2);
                this.path2count++;
            }
            last = path;
        }
        this.path2d = path2d;
    }
}
