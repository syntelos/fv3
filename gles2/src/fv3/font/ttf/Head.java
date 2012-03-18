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

import fv3.font.FontOptions;
import fv3.font.TTFFont;
import fv3.font.TTFFontReader;
import fv3.font.TTFGlyph;
import fv3.font.TTFPath;

import java.lang.Math;

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


    public int flags, style, lowestRecPPEM, fontDirectionHint;

    public long created, modified;

    /**
     * Font bounding box
     */
    public float minX, maxX, minY, maxY;

    public boolean optimized_for_cleartype, apply_lsb, indexToLocIsLong;

    public float emsize;


    protected Head(int ofs, int len) {
        super(ofs,len);
    }


    public void init(TTFFont font, TTF tables, TTFFontReader reader){
        reader.seek(this.offset+16);
        this.flags = reader.readUint16();
        this.optimized_for_cleartype = (0 != (this.flags & (1<<13)));
        this.apply_lsb = (0 == (this.flags & 2));
        this.emsize = reader.readUint16();

        this.created = reader.readDate();
        this.modified = reader.readDate();

        this.minX = reader.readSint16();
        this.minY = reader.readSint16();
        this.maxX = reader.readSint16();
        this.maxY = reader.readSint16();

        this.style = reader.readUint16();
        this.lowestRecPPEM = reader.readUint16();
        this.fontDirectionHint = reader.readSint16();
        switch (reader.readUint16()){
        case 0:
            this.indexToLocIsLong = false;
            break;
        case 1:
            this.indexToLocIsLong = true;
            break;
        default:
            throw new IllegalStateException();
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
    public float scale(FontOptions options){

        if (0 == options.width || 0 == options.height)
            return 1.0f;
        else {
            float x = (this.maxX - this.minX);
            float y = (this.maxY - this.minY);
            float sx = (options.width / x);
            float sy = (options.height / y);
            return Math.max(sx,sy);
        }
    }
}
