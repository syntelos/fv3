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
 * 
 * @author John Pritchard
 */
public final class TTCF
    extends Header
{
    private final static int NAME = (('n'<<24)|('a'<<16)|('m'<<8)|('e'));


    public final int version, count;

    private final int[] offsets;

    private final String[] names;


    public TTCF(TTFFont font, TTFFontReader reader) {
        super();
        this.version = reader.readSint32();
        this.count = reader.readSint32();
        this.offsets = new int[this.count];
        for (int cc = 0; cc < this.count; cc++){
            this.offsets[cc] = reader.readSint32();
        }
        this.names = new String[this.count];
        for (int cc = 0; cc < this.count; cc++){
            reader.seek(this.offsets[cc]);
            reader.readSint32();  // version
            int num = reader.readUint16();
            reader.readUint16();  // srange
            reader.readUint16();  // esel
            reader.readUint16();  // rshift
            int chofs = 0, chlen = 0;
            for (int nn = 0; nn < num; nn++){
                int tag = reader.readSint32();
                reader.readSint32();  // checksum
                chofs = reader.readSint32();
                chlen = reader.readSint32();
                if (NAME == tag)
                    break;
            }
            if (0 != chofs && 0 != chlen){
                reader.seek(chofs);
                reader.readUint16(); //format
                num = reader.readUint16();
                chofs += reader.readUint16();

                for (int nn = 0; nn < num; nn++){
                    int plat = reader.readUint16();
                    int spec = reader.readUint16();
                    int lang = reader.readUint16();
                    int name = reader.readUint16();
                    int len = reader.readUint16();
                    int off = chofs + reader.readUint16();
                    String s = reader.readString(plat,spec,off,len);
                    if (null != s){
                        this.names[cc] = s;
                        break;
                    }
                }
            }
        }
    }


    public int getLength(){
        return this.count;
    }
    public int firstOffset(){
        if (0 < this.count)
            return this.offsets[0];
        else
            return -1;
    }
    public int getOffset(int idx){
        if (-1 < idx && idx < this.count)
            return this.offsets[idx];
        else
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    public String firstName(){
        if (0 < this.count)
            return this.names[0];
        else
            return null;
    }
    public String getName(int idx){
        if (-1 < idx && idx < this.count)
            return this.names[idx];
        else
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    public int indexOfName(String name){
        String[] names = this.names;
        if (null == names)
            return -1;
        else {
            for (int cc = 0, count = names.length; cc < count; cc++){
                if (name.equals(names[cc]))
                    return cc;
            }
            return -1;
        }
    }
    public int offsetForName(String name){
        int idx = this.indexOfName(name);
        if (-1 != idx)
            return this.offsets[idx];
        else
            return -1;
    }

}
