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
 * embedded bitmap scaling control table
 * 
 * @author John Pritchard
 */
public final class Ebsc
    extends Table
    implements Cloneable
{
    public final static int ID = ('E'<<24)|('B'<<16)|('S'<<8)|('C');
    public final static int TYPE = 16;
    public final static String NAME = "EBSC";
    public final static String DESC = "embedded bitmap scaling control table";


    protected Ebsc(int ofs, int len) {
        super(ofs,len);
    }


    public void init(TTFFont font, TTF tables, TTFFontReader reader){

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
