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

/**
 * glyph outline table
 * 
 * @author John Pritchard
 */
public final class Glyf
    extends Table
    implements Cloneable
{
    public final static int ID = ('g'<<24)|('l'<<16)|('y'<<8)|('f');
    public final static int TYPE = 28;
    public final static String NAME = "glyf";
    public final static String DESC = "glyph outline table";


    protected Glyf(int ofs, int len) {
        super(ofs,len);
    }


    public void init(TTFFont font, TTF tables, TTFFontReader reader){
        FontOptions options = font.options;
        for (TTFGlyph glyph: font){
            glyph.read(reader);
            glyph.init(options);
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
