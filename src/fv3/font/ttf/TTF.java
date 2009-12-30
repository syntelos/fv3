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
    extends Header
{

    /**
     * Number of tables present
     */
    public final int count;
    /**
     * Count- many ordered index of TYPE's available indeces for
     * 'tables'.
     */
    public final int types[];
    /**
     * {@link Table#COUNT} - many slots indexed by the TYPE constants
     * defined in table classes, eg, {@link Name} or {@link Head}.  
     * 
     * Note that there's two extra slots on the tail for "unknown"
     * types, assigned dynamically to instances of {@link
     * Table$Unknown}.
     */
    public final Table[] tables;


    /**
     * Called from {@link fv3.font.TTFFont}
     */
    public TTF(TTFFont font, TTFFontReader reader) {
        super();
        /*
         * Read head
         */
        this.count = reader.readUint16();
        this.types = new int[this.count];
        int searchRange = reader.readUint16();
        int entrySelector = reader.readUint16();
        int rangeShift = reader.readUint16();
        int unknown = Table.COUNT;
        Table[] tables = new Table[unknown+2];
        for (int idx = 0; idx < this.count; idx++){
            int tag = reader.readSint32();
            int chk = reader.readSint32();
            int ofs = reader.readSint32();
            int len = reader.readSint32();

            Table table = Table.Create(tag,ofs,len);

            if (null == table){
                table = new Table.Unknown(tag,ofs,len,unknown++);
            }

            int type = table.getType();
            this.types[idx] = type;
            if (null == tables[type])
                tables[type] = table;
            else {
                throw new IllegalStateException(String.format("Duplicate table '%s'.",table.getName()));
            }
        }
        this.tables = tables;
    }


    /**
     * Called from {@link fv3.font.TTFFont}
     */
    public void init(TTFFont font, TTFFontReader reader) {

        Head head = this.getTableHead();
        head.init(font,this,reader);

        Hhea hhea = this.getTableHhea();
        hhea.init(font,this,reader);

        Maxp maxp = this.getTableMaxp();
        maxp.init(font,this,reader);

        Name name = this.getTableName();
        name.init(font,this,reader);

        Loca loca = this.getTableLoca();
        loca.init(font,this,reader);

        Cmap cmap = this.getTableCmap();
        cmap.init(font,this,reader);

    }
    public int countTables(){
        return this.count;
    }
    /**
     * Lookup by array index
     * @param idx Dense index
     */
    public boolean hasTable(int idx){
        int type = this.types[idx];
        if (-1 != type)
            return this.hasTableByType(type);
        else
            return false;
    }
    /**
     * Lookup by array index
     * @param idx Dense index
     */
    public Table getTable(int idx){
        int type = this.types[idx];
        if (-1 != type)
            return this.getTableByType(type);
        else
            return null;
    }
    /**
     * Lookup by array index
     * @param idx Dense index
     */
    public String getName(int idx){
        int type = this.types[idx];
        if (-1 != type)
            return this.getTableByType(type).getName();
        else
            return null;
    }
    /**
     * Lookup by array index
     * @param idx Dense index
     * @return Sparse index, or zero for not found.
     */
    public int getType(int idx){
        int type = this.types[idx];
        if (-1 != type)
            return this.getTableByType(type).getType();
        else
            return 0;
    }
    public final Cmap getTableCmap(){
        return (Cmap)this.getTableByType(Cmap.TYPE);
    }
    public final Glyf getTableGlyf(){
        return (Glyf)this.getTableByType(Glyf.TYPE);
    }
    public final Head getTableHead(){
        return (Head)this.getTableByType(Head.TYPE);
    }
    public final Hhea getTableHhea(){
        return (Hhea)this.getTableByType(Hhea.TYPE);
    }
    public final Name getTableName(){
        return (Name)this.getTableByType(Name.TYPE);
    }
    public final Loca getTableLoca(){
        return (Loca)this.getTableByType(Loca.TYPE);
    }
    public final Maxp getTableMaxp(){
        return (Maxp)this.getTableByType(Maxp.TYPE);
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
    public boolean hasNotTableByType(int type){
        if (-1 != type)
            return (null == this.tables[type]);
        else
            return true;
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
            throw new IllegalArgumentException(String.valueOf(type));
    }
}
