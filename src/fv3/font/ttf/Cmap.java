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

import java.nio.charset.UnsupportedCharsetException;

/**
 * character code mapping table
 * 
 * @author John Pritchard
 */
public final class Cmap
    extends Table
    implements Cloneable
{
    public final static int ID = ('c'<<24)|('m'<<16)|('a'<<8)|('p');
    public final static int TYPE = 10;
    public final static String NAME = "cmap";
    public final static String DESC = "character code mapping table";



    public CmapTable unicode;


    protected Cmap(int ofs, int len) {
        super(ofs,len);
    }


    public void init(TTFFont font, TTF tables, TTFFontReader reader){
        this.seekto(reader);
        int version = reader.readUint16();
        int nTables = reader.readUint16();
        CmapTable map;
        for (int cc = 0; cc < nTables; cc++){
            try {
                map = new CmapTable(this,reader);
                if (map.isUnicode){
                    if (map.isUnicodeGeneral){
                        this.unicode = map;
                        break;
                    }
                    else if (null == this.unicode)
                        this.unicode = map;
                }
            }
            catch (UnsupportedCharsetException exc){
            }
        }

        if (null == this.unicode)
            throw new IllegalStateException("Unicode font map not found.");
        else {
            this.unicode.read(this,reader);
        }
    }
    public TTFGlyph init2(TTFGlyph glyph){
        this.unicode.init2(glyph);
        return glyph;
    }
    protected void init2(TTFFont font){
        this.unicode.init2(font);
    }
    public TTFGlyph lookup(TTFFont font, char ch){
        return this.unicode.lookup(font,ch);
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
