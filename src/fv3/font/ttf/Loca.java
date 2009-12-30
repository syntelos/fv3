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
 * glyph location table
 * 
 * @author John Pritchard
 */
public final class Loca
    extends Table
    implements Cloneable
{
    public final static int ID = ('l'<<24)|('o'<<16)|('c'<<8)|('a');
    public final static int TYPE = 41;
    public final static String NAME = "loca";
    public final static String DESC = "glyph location table";



    protected Loca(int ofs, int len) {
        super(ofs,len);
    }


    public void init(TTFFont font, TTF tables, TTFFontReader reader){
        Head head = tables.getTableHead();
        if (null != head){
            Maxp maxp = tables.getTableMaxp();
            if (null != maxp){
                int count = maxp.glyphCount;
                if (0 < count){
                    boolean longOffsets = head.indexToLocIsLong;

                    int[] offsets = new int[count+1];

                    this.seekto(reader);

                    if (longOffsets){

                        for (int cc = 0, cz = (count+1); cc < cz; cc++){

                            offsets[cc] = reader.readSint32();
                        }
                    }
                    else {
                        for (int cc = 0, cz = (count+1); cc < cz; cc++){

                            offsets[cc] = (reader.readUint16()<<1);
                        }
                    }
                    /*
                     * Read each glyph.
                     */
                    Glyf glyf = tables.getTableGlyf();
                    if (null != glyf){
                        int glyfBound = glyf.length;
                        for (int cc = 0; cc < count; cc++){
                            int start = offsets[cc];
                            int end = offsets[cc+1];
                            if (end > start && end < glyfBound)
                                font.readGlyph(glyf,cc,start,end,reader);
                        }
                    }
                    else
                        throw new IllegalStateException(String.format("TFF missing table '%s'.",Glyf.NAME));
                }
                else
                    throw new IllegalStateException(String.format("TFF missing positive glyph count in '%s'.",Maxp.NAME));
            }
            else
                throw new IllegalStateException(String.format("TFF missing table '%s'.",Maxp.NAME));
        }
        else
            throw new IllegalStateException(String.format("TFF missing table '%s'.",Head.NAME));
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
