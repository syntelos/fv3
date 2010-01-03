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

import java.awt.geom.Path2D;

/**
 * 
 * @author jdp
 */
public class TTFFont
    extends fv3.font.TTFFont
{

    public TTFFont(String name, TTFFontReader reader){
        super(name,reader);
    }
    public TTFFont(String name, TTFFontReader reader, FontOptions opts){
        super(name,reader,opts);
    }


    public Path2D.Double getPath(int idx){
        TTFGlyph glyph = (TTFGlyph)this.get(idx);
        if (null != glyph)
            return glyph.getPath();
        else
            return null;
    }
    public void createGlyph(Glyf glyf, int index, int offset, int next, TTFFontReader reader){

        TTFGlyph glyph = new TTFGlyph(this,glyf,index,offset,next);

        this.add(glyph);
    }
}
