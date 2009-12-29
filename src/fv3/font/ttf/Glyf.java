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


    public int count, offsets[];


    protected Glyf(int ofs, int len) {
        super(ofs,len);
    }


    public void init(TTFFont font, TTF tables, TTFFontReader reader){
        Head head = (Head)tables.getTableByType(Head.TYPE);
        if (null != head){
            this.count = 0;
            {
                Loca loca = (Loca)tables.getTableByType(Loca.TYPE);
                Maxp maxp = (Maxp)tables.getTableByType(Maxp.TYPE);
                if (null != loca){
                    if (null != maxp){
                        int mC = maxp.glyphCount;
                        int lC = loca.glyphCount;
                        if (mC == lC)
                            this.count = lC;
                        else if (0 == mC)
                            this.count = lC;
                        else if (0 == lC)
                            this.count = mC;
                        else
                            throw new IllegalStateException(String.format("TFF missing glyph count from  '%s' and '%s'.",Loca.NAME,Maxp.NAME));
                    }
                    else {
                        this.count = loca.glyphCount;
                    }
                }
                else if (null != maxp)
                    this.count = maxp.glyphCount;
                else
                    throw new IllegalStateException(String.format("TFF missing one of '%s' or '%s'.",Loca.NAME,Maxp.NAME));
            }
            /*
             * Do read 'glyf', and then read each glyph...
             */
            if (0 != this.count){

                this.seekto(reader);

                this.offsets = new int[this.count+1];

                if (head.index_to_loc_is_long){

                    for (int cc = 0, cz = (this.count+1); cc < cz; cc++){

                        this.offsets[cc] = reader.readUint32();
                    }
                }
                else {
                    for (int cc = 0, cz = (this.count+1); cc < cz; cc++){

                        this.offsets[cc] = (2* reader.readUint16());
                    }
                }
                /*
                 * Read each glyph.
                 */
                for (int cc = 0; cc < this.count; cc++){
                    int start = this.offsets[cc];
                    int end = this.offsets[cc+1];
                    if (end > start && end < this.length)
                        font.readGlyph(this,cc,start,end,reader);
                }
            }
            else
                throw new IllegalStateException(String.format("TFF missing positive glyph count from one of '%s' or '%s'.",Loca.NAME,Maxp.NAME));
        }
        else
            throw new IllegalStateException(String.format("TFF missing '%s'.",Head.NAME));
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
