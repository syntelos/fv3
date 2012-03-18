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

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * character code mapping table
 * 
 * @author John Pritchard
 */
public final class CmapTable
    extends Object
    implements Cloneable
{

    public final int platform, specific, offset, hashcode;

    public final Charset charset;

    public final boolean isUnicode, isUnicodeMac, isUnicodeGeneral;



    protected CmapTable(Cmap table, TTFFontReader reader)
        throws UnsupportedCharsetException
    {
        super();
        this.platform = reader.readUint16();
        this.specific = reader.readUint16();
        this.charset = TTFFontReader.Encoding(this.platform,this.specific);
        this.offset = (table.offset+reader.readSint32());
        this.hashcode = (this.platform<<16)|(this.specific);

        switch (this.platform){
        case 0:
            this.isUnicode = (3 == this.specific);
            this.isUnicodeMac = this.isUnicode;
            this.isUnicodeGeneral = false;
            break;
        case 3:
            this.isUnicode = (1 == this.specific);
            this.isUnicodeGeneral = this.isUnicode;
            this.isUnicodeMac = false;
            break;
        default:
            this.isUnicode = false;
            this.isUnicodeMac = false;
            this.isUnicodeGeneral = false;
            break;
        }
    }


    protected void init(TTFFont font, TTFFontReader reader) {

        font.mapGlyphs();

        reader.seek(this.offset);

        int format = reader.readUint16();
        int length = reader.readUint16();
        int language = reader.readUint16();

        switch(format){
        case 0:{

            for (char ch = 0; ch < 256; ch++){
                int index = reader.readUint8();
                font.mapGlyph(index,ch);
            }
            break;
        }
        case 2:
            throw new IllegalStateException("Cmap table format "+format);
        case 4:{

            int segCount = reader.readUint16()>>1;
            reader.skip(6);

            int[] endChars = new int[segCount];
            {
                for (int cc = 0; cc < segCount; cc++){
                    endChars[cc] = reader.readUint16();
                }
            }
            if (0 != reader.readUint16()){
                throw new IllegalStateException("Cmap table format "+format+" error.");
            }
            int[] startChars = new int[segCount];
            {
                for (int cc = 0; cc < segCount; cc++){
                    startChars[cc] = reader.readUint16();
                }
            }
            int[] glyphIndexDelta = new int[segCount];
            {
                for (int cc = 0; cc < segCount; cc++){
                    glyphIndexDelta[cc] = reader.readSint16();
                }
            }
            int[] glyphIndexDeltaRangeOffset = new int[segCount];

            int glyphIndexDeltaRangeOffsetBase = reader.tell();
            {
                for (int cc = 0, ofs; cc < segCount; cc++){

                    glyphIndexDeltaRangeOffset[cc] = reader.readUint16();
                }
            }
            /*
             * Use the glyph index array directly from the reader for
             * native TTF range offset arithmetic.  
             */
            {
                int index, start, end, search, offset, delta, addr, pos, glyfx;

                for (index = 0; index < segCount; index++){
                    start = startChars[index];
                    if (0xffff != start){
                        end = endChars[index];
                        offset = glyphIndexDeltaRangeOffset[index];
                        addr = (glyphIndexDeltaRangeOffsetBase + (index<<1));
                        delta = glyphIndexDelta[index];
                        for (search = start; search <= end; search++){
                            if (0 == offset){
                                glyfx = (search + delta);
                                font.mapGlyph(glyfx, (char)search);
                            }
                            else if (0xffff != offset){
                                pos = offset + ((search - start)<<1) + addr;
                                reader.seek(pos);
                                glyfx = reader.readUint16();
                                font.mapGlyph(glyfx, (char)search);
                            }
                        }
                    }
                }
            }
            break;
        }
        case 6:{
            //[TODO]
        }
        case 8:
        case 10:
        case 12:
        default:
            throw new IllegalStateException("Cmap table format "+format);
        }
    }
    public int hashCode(){
        return this.hashcode;
    }
    public boolean equals(Object that){
        return (that == this);
    }
}
