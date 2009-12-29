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

import fv3.font.ttf.Glyf;
import fv3.font.ttf.Head;
import fv3.font.ttf.TTF;

/**
 * This class may be subclassed with a {@link Font} subclass as for
 * implementing a text editor.
 * 
 * @author John Pritchard
 */
public class TTFGlyph
    extends Glyph
{
    /**
     * TTF file internal coordinate and dimension
     */
    public final int index, offset, length;

    public double vwidth;


    protected TTFGlyph(TTFFont font, Glyf glyf, int index, int offset, int next){
        super(font);
        this.index = index;
        this.offset = (glyf.offset + offset);
        this.length = (next-offset);
    }



    public void init(FontOptions opts) {

    }
    protected void read(TTFFontReader reader, Head head){
        reader.seek(this.offset);
        this.vwidth = head.emsize;

    }

}
