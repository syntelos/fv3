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
        Path2D.Double path2d = new Path2D.Double();
        for (TTFPath path: this)
        {
            if (path.isStraight){
                path2d.moveTo(path.startX,path.startY);
                path2d.lineTo(path.endX,path.endY);
            }
            else {
                path2d.moveTo(path.startX,path.startY);
                path2d.quadTo(path.controlX,path.controlY,path.endX,path.endY);
            }
        }
        this.path = path;
    }
}
