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

import java.awt.geom.Path2D;

/**
 * The objective here is to replicate the GL lineset, not exploiting
 * the Java2D QUADTO.
 * 
 * @author jdp
 */
public class TTFGlyph
    extends fv3.font.TTFGlyph
{

    protected Path2D.Double path;


    protected TTFGlyph(TTFFont font, Glyf table, int index, int offset, int next){
        super(font,table,index,offset,next);
    }


    public final boolean hasPath(){
        return (null != this.path);
    }
    public final Path2D.Double getPath(){
        return this.path;
    }
    public void init(FontOptions options) {
        super.init(options);
        Path2D.Double path = new Path2D.Double();
        {
            /*
             * Placeholder only compiles (i.e. this is not correct)
             */
            double[] points = this.points();
            if (null != points){
                for (int pc = 0, pz = (points.length>>1); pc < pz; pc++){
                    int x = (pc<<1);
                    int y = (x+1);
                    if (0 == pc)
                        path.moveTo(points[y],points[y]);
                    else
                        path.lineTo(points[x],points[y]);
                }
            }
        }
        this.path = path;
    }
}
