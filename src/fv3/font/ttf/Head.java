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
package fv3.font.ttf;

import fv3.font.TTFFont;
import fv3.font.TTFFontReader;
import fv3.font.TTFGlyph;
import fv3.font.TTFPath;

/**
 * font header table
 * 
 * @author John Pritchard
 */
public final class Head
    extends Table
    implements Cloneable
{
    public final static int ID = ('h'<<24)|('e'<<16)|('a'<<8)|('d');
    public final static int TYPE = 33;
    public final static String NAME = "head";
    public final static String DESC = "font header table";


    public int flags, ffb[] = new int[4], macstyle;

    public boolean optimized_for_cleartype, apply_lsb, index_to_loc_is_long;

    public double emsize, ascent, descent;


    protected Head(int ofs, int len) {
        super(ofs,len);
    }


    public void init(TTFFont font, TTF tables, TTFFontReader reader){
        reader.seek(this.offset+16);
        this.flags = reader.readUint16();
        this.optimized_for_cleartype = (0 != (this.flags & (1<<13)));
        this.apply_lsb = (0 == (this.flags & 2));
        this.emsize = reader.readUint16();
        this.ascent = (0.8*this.emsize);
        this.descent = (this.emsize-this.ascent);

        if (tables.hasTableByType(Fftm.TYPE)){
            reader.readDate();
            reader.readDate();
        }
        else {
            tables.modification = reader.readDate();
            tables.creation = reader.readDate();
        }
        for (int cc = 0; cc < 4; cc++)
            this.ffb[cc] = reader.readUint16();
        this.macstyle = reader.readUint16();
        for (int cc = 0; cc < 2; cc++)
            reader.readUint16();
        this.index_to_loc_is_long = (0 != reader.readUint16());
        if (this.index_to_loc_is_long){
            Loca loca = (Loca)tables.getTableByType(Loca.TYPE);
            loca.glyphCount  = loca.length/4-1;
            if (loca.glyphCount < 0)
                loca.glyphCount = 0;
        }
    }
    public String getName(){
        return NAME;
    }
    public int getTag(){
        return ID;
    }
    public int getType(){
        return TYPE;
    }
}
