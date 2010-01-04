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
    private final static float Pad = 3.0f;
    private final static float PadV = 14f;

    private final static double ControlR = 3.0;
    private final static double ControlD = (2.0 * ControlR);


    protected Path2D.Double path2d, grid2d;


    protected TTFGlyph(TTFFont font, Glyf table, int index, int offset, int next){
        super(font,table,index,offset,next);
    }


    public void drawGrid(Graphics2D g){

        double scale = this.font.getScale();

        double minX = (this.minX * scale);
        double minY = (this.minY * scale);
        double maxX = (this.maxX * scale);
        double maxY = (this.maxY * scale);

        double em = (this.font.getEm() * scale);
        double dt = (em/10.0);

        for (double t = 0.0, z = (minX - dt); t >= z; t -= dt)
            minX = t;
        for (double t = 0.0, z = (maxX + dt); t <= z; t += dt)
            maxX = t;
        for (double t = 0.0, z = (minY - dt); t >= z; t -= dt)
            minY = t;
        for (double t = 0.0, z = (maxY + dt); t <= z; t += dt)
            maxY = t;


        Line2D line;
        for (double x = minX; x <= maxX; x += dt){

            line = new Line2D.Double(x,minY,x,maxY);

            g.draw(line);
            g.drawString(String.format("%4.1f",x),(float)(x),(float)(maxY+PadV));
        }
        for (double y = minY; y <= maxY; y += dt){

            line = new Line2D.Double(minX,y,maxX,y);

            g.draw(line);
            g.drawString(String.format("%4.1f",y),(float)(maxX+Pad),(float)(y+Pad));
        }


        Ellipse2D on, off;
        double x0, y0, x1, y1, x2, y2;
        for (int idx = 0, count = this.getLength(); idx < count; idx++){

            TTFPath path = this.get(idx);

            if (path.isStraight){

                x0 = (path.startX * scale);
                y0 = (path.startY * scale);

                on = new Ellipse2D.Double(x0-ControlR, y0-ControlR, ControlD, ControlD);
                g.fill(on);

                x2 = (path.endX * scale);
                y2 = (path.endY * scale);

                on = new Ellipse2D.Double(x2-ControlR, y2-ControlR, ControlD, ControlD);
                g.fill(on);
            }
            else {

                x0 = (path.startX * scale);
                y0 = (path.startY * scale);

                on = new Ellipse2D.Double(x0-ControlR, y0-ControlR, ControlD, ControlD);
                g.fill(on);

                x1 = (path.controlX * scale);
                y1 = (path.controlY * scale);

                off = new Ellipse2D.Double(x1-ControlR, y1-ControlR, ControlD, ControlD);
                g.draw(off);

                x2 = (path.endX * scale);
                y2 = (path.endY * scale);

                on = new Ellipse2D.Double(x2-ControlR, y2-ControlR, ControlD, ControlD);
                g.fill(on);
            }
        }
    }
    public void drawOutline(Graphics2D g){

        g.draw(this.path2d);
    }
    public final boolean hasPath2d(){
        return (null != this.path2d);
    }
    public final Path2D.Double getPath2d(){
        return this.path2d;
    }
    public final Path2D.Double getGrid2d(){
        Path2D.Double grid2d = this.grid2d;
        if (null == grid2d){
            double scale = this.font.getScale();

            double minX = (this.minX * scale);
            double minY = (this.minY * scale);
            double maxX = (this.maxX * scale);
            double maxY = (this.maxY * scale);

            double em = (this.font.getEm() * scale);
            double dt = (em/10.0);

            for (double t = 0.0, z = (minX - dt); t >= z; t -= dt)
                minX = t;
            for (double t = 0.0, z = (maxX + dt); t <= z; t += dt)
                maxX = t;
            for (double t = 0.0, z = (minY - dt); t >= z; t -= dt)
                minY = t;
            for (double t = 0.0, z = (maxY + dt); t <= z; t += dt)
                maxY = t;

            grid2d = new Path2D.Double();
            for (double x = minX; x <= maxX; x += dt){

                grid2d.moveTo(x,minY);
                grid2d.lineTo(x,maxY);
            }
            for (double y = minY; y <= maxY; y += dt){

                grid2d.moveTo(minX,y);
                grid2d.lineTo(maxX,y);
            }
            this.grid2d = grid2d;
        }
        return grid2d;
    }
    public void init(FontOptions options) {
        super.init(options);
        double scale = this.font.getScale();
        boolean debug = ('A' == this.character);

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
                    if (debug)
                        System.err.printf("Contour %d: MoveTo(%f,%f);\n",path.contour,x0,y0);
                }
            }
            else {
                x0 *= scale;
                y0 *= scale;
                path2d.moveTo(x0,y0);
                if (debug)
                    System.err.printf("Contour %d: MoveTo(%f,%f);\n",path.contour,x0,y0);
            }

            if (path.isStraight){
                x2 *= scale;
                y2 *= scale;
                path2d.lineTo(x2,y2);
                if (debug)
                    System.err.printf("Contour %d: LineTo(%f,%f);\n",path.contour,x2,y2);
            }
            else {
                x1 *= scale;
                y1 *= scale;
                x2 *= scale;
                y2 *= scale;
                path2d.quadTo(x1,y1,x2,y2);
                if (debug)
                    System.err.printf("Contour %d: QuadTo(%f,%f,%f,%f);\n",path.contour,x1,y1,x2,y2);
            }
            last = path;
        }
        this.path2d = path2d;
    }
}
