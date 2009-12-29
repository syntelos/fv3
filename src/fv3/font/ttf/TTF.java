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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.UnsupportedCharsetException;

/**
 * 
 * @author John Pritchard
 */
public final class TTF
    extends Object
    implements Cloneable
{


    public final int count, types[];

    public final Table[] tables;


    public TTF(TTFFont font, TTFFontReader reader) {
        super();
        this.count = reader.readUint16();
        this.types = new int[this.count];
        int searchRange = reader.readUint16();
        int entrySelector = reader.readUint16();
        int rangeShift = reader.readUint16();
        Table[] tables = new Table[Table.COUNT];
        for (int idx = 0; idx < this.count; idx++){
            int tag = reader.readUint32();
            int chk = reader.readUint32();
            int ofs = reader.readUint32();
            int len = reader.readUint32();
            Table table = Table.Create(tag,ofs,len);
            if (null != table){
                int type = table.getType();
                this.types[idx] = type;
                if (null == tables[type])
                    tables[type] = table;
                else {
                    throw new IllegalStateException(String.format("Duplicate table '%s'.",table.getName()));
                }
            }
            else
                this.types[idx] = -1;
        }
        this.tables = tables;
        for (int idx = 0; idx < Table.COUNT; idx++){
            Table table = this.tables[idx];
            if (null != table)
                table.init(font,this,reader);
        }
    }


    public int countTables(){
        return this.count;
    }
    /**
     * Lookup by array index
     * @param idx Dense index
     */
    public boolean hasTable(int idx){
        return this.hasTableByType(this.types[idx]);
    }
    /**
     * Lookup by array index
     * @param idx Dense index
     */
    public Table getTable(int idx){
        return this.getTableByType(this.types[idx]);
    }
    /**
     * Lookup by array index
     * @param idx Dense index
     */
    public String getName(int idx){
        return this.getTableByType(this.types[idx]).getName();
    }
    /**
     * Lookup by type value.  Each table class has a TYPE constant
     * defined for use as the argument to this method.
     * @param idx Sparse index
     */
    public boolean hasTableByType(int type){
        if (-1 != type)
            return (null != this.tables[type]);
        else
            return false;
    }
    /**
     * Lookup by type value.  Each table class has a TYPE constant
     * defined for use as the argument to this method.
     * @param idx Sparse index
     */
    public Table getTableByType(int type){
        if (-1 != type)
            return this.tables[type];
        else
            return null;
    }
}
