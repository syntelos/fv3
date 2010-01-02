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

    public int format, length, language;

    public int[] glyphIndexArray;

    public int[] endChars, startChars, glyphIndexDelta, glyphIndexDeltaRangeOffset;



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


    public TTFGlyph lookup(TTFFont font, char ch){
        switch(this.format){
        case 0:{
            int[] glyphIndexArray = this.glyphIndexArray;
            if (null != glyphIndexArray){
                int index = ch;
                int glyfx = glyphIndexArray[index];
                return font.get(glyfx);
            }
            return null;
        }
        case 4:{
            int search = ch, start, offset, delta, glyfx;
            int index = 0;
            int[] endChars = this.endChars;
            for (int segCount = endChars.length; index < segCount; index++){
                if (endChars[index] >= search){
                    start = this.startChars[index];
                    if (start <= search){

                        offset = this.glyphIndexDeltaRangeOffset[index];

                        if (Integer.MIN_VALUE == offset && 0xffff == start)

                            return font.get(0);

                        else if (Integer.MIN_VALUE == offset){

                            delta = this.glyphIndexDelta[index];
                            glyfx = (delta + search);

                            return font.get(glyfx);
                        }
                        else if (Integer.MAX_VALUE != offset){

                            glyfx = (search + offset - start);
                            if (-1 < glyfx){
                                glyfx = this.glyphIndexArray[glyfx];
                                return font.get(glyfx);
                            }
                            else 
                                return font.get(0);
                        }
                        else
                            return font.get(0);
                    }
                }
            } 
            return null;
        }
        }
        return null;
    }
    protected void init2(TTFFont font){
        switch(this.format){
        case 0:{
            int[] glyphIndexArray = this.glyphIndexArray;
            if (null != glyphIndexArray){
                for (int cc = 0, count = glyphIndexArray.length; cc < count; cc++){
                    int glyfx = glyphIndexArray[cc];
                    if (-1 < glyfx){
                        TTFGlyph glyph = font.get(glyfx);
                        if (null != glyph)
                            glyph.character = (char)cc;
                    }
                }
            }
            return;
        }
        case 4:{
            int[] startChars = this.startChars;
            int[] endChars = this.endChars;
            int[] glyphIndexDelta = this.glyphIndexDelta;
            int[] glyphIndexDeltaRangeOffset = this.glyphIndexDeltaRangeOffset;
            int index = 0, segCount = startChars.length, start, end, offset, delta, glyfx;

            for (; index < segCount; index++){

                start = startChars[index];
                end = endChars[index];
                offset = this.glyphIndexDeltaRangeOffset[index];

                if (Integer.MIN_VALUE == offset && 0xffff == start)
                    continue;

                else if (Integer.MIN_VALUE == offset){
                    delta = this.glyphIndexDelta[index];

                    for (int search = start; search <= end; search++){
                        glyfx = (search + delta);
                        TTFGlyph glyph = font.get(glyfx);
                        if (null != glyph)
                            glyph.character = (char)search;
                    }
                }
                else if (Integer.MAX_VALUE != offset){

                    for (int search = start; search <= end; search++){
                        glyfx = (search + offset - start);
                        if (-1 < glyfx){
                            glyfx = this.glyphIndexArray[glyfx];
                            TTFGlyph glyph = font.get(glyfx);
                            if (null != glyph)
                                glyph.character = (char)search;
                        }
                    }
                }
            } 
            return;
        }
        }
    }
    protected void read(Cmap table, TTFFontReader reader) {
        reader.seek(this.offset);
        this.format = reader.readUint16();
        this.length = reader.readUint16();
        this.language = reader.readUint16();
        switch(this.format){
        case 0:{
            this.glyphIndexArray = new int[256];
            for (int cc = 0; cc < 256; cc++){
                this.glyphIndexArray[cc] = reader.readUint8();
            }
            break;
        }
        case 2:
            throw new IllegalStateException("Cmap table format "+this.format);
        case 4:{

            int segCount = reader.readUint16()>>1;
            reader.skip(6);

            this.endChars = new int[segCount];
            {
                for (int cc = 0; cc < segCount; cc++){
                    this.endChars[cc] = reader.readUint16();
                }
            }
            if (0 != reader.readUint16()){
                throw new IllegalStateException("Cmap table format "+this.format+" error.");
            }
            this.startChars = new int[segCount];
            {
                for (int cc = 0; cc < segCount; cc++){
                    this.startChars[cc] = reader.readUint16();
                }
            }
            this.glyphIndexDelta = new int[segCount];
            {
                for (int cc = 0; cc < segCount; cc++){
                    this.glyphIndexDelta[cc] = reader.readSint16();
                }
            }
            this.glyphIndexDeltaRangeOffset = new int[segCount];

            if (1 == (reader.tell() & 1))
                throw new IllegalStateException("Unexpected glyph table offset (pos) found in TTF file.");
            else {
                for (int cc = 0, top, bottom; cc < segCount; cc++){
                    top = reader.readUint16();
                    if (1 == (top & 1))
                        throw new IllegalStateException("Unexpected glyph index delta range offset (top) found in TTF file.");
                    else if (0 == top)
                        this.glyphIndexDeltaRangeOffset[cc] = Integer.MIN_VALUE;
                    else if (0xffff == top)
                        this.glyphIndexDeltaRangeOffset[cc] = Integer.MAX_VALUE;
                    else {
                        top = ((top>>1)+cc);
                        bottom = (segCount-cc);

                        this.glyphIndexDeltaRangeOffset[cc] = (bottom - top);
                    }
                }
            }

            int glyphIndexArrayLen = ((this.length - (16 + (8*segCount))) >>1);

            this.glyphIndexArray = new int[glyphIndexArrayLen];
            {
                for (int cc = 0; cc < glyphIndexArrayLen; cc++){
                    this.glyphIndexArray[cc] = reader.readUint16();
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
            throw new IllegalStateException("Cmap table format "+this.format);
        }
    }
    public int hashCode(){
        return this.hashcode;
    }
    public boolean equals(Object that){
        return (that == this);
    }
}
