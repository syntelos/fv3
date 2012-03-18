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
 * Portable metrics table
 * 
 * @author John Pritchard
 */
public final class Os2
    extends Table
    implements Cloneable
{
    public final static int ID = ('O'<<24)|('S'<<16)|('/'<<8)|('2')|(' ');
    public final static int TYPE = 51;
    public final static String NAME = "OS/2";
    public final static String DESC = "OS/2 and Windows specific metrics table";

    public float xAverageCharWidth, ySubscriptXSize, ySubscriptYSize, 
        ySubscriptXOffset, ySubscriptYOffset, ySuperscriptXSize, ySuperscriptYSize,
        ySuperscriptXOffset, ySuperscriptYOffset, yStrikeoutSize, yStrikeoutPosition;

    public int usWeightClass, usWidthClass, fsType, fsFamilyClass, fsSelection,
        fsFirstCharIndex, fsLastCharIndex;

    public String achVendID;


    protected Os2(int ofs, int len) {
        super(ofs,len);
    }


    public void init(TTFFont font, TTF tables, TTFFontReader reader){
        reader.seek(this.offset+2);
        this.xAverageCharWidth = reader.readSint16();
        this.usWeightClass = reader.readUint16();
        this.usWidthClass = reader.readUint16();
        this.fsType = reader.readSint16();
        this.ySubscriptXSize = reader.readSint16();
        this.ySubscriptYSize = reader.readSint16();
        this.ySubscriptXOffset = reader.readSint16();
        this.ySubscriptYOffset = reader.readSint16();
        this.ySuperscriptXSize = reader.readSint16();
        this.ySuperscriptYSize = reader.readSint16();
        this.ySuperscriptXOffset = reader.readSint16();
        this.ySuperscriptYOffset = reader.readSint16();
        this.yStrikeoutSize = reader.readSint16();
        this.yStrikeoutPosition = reader.readSint16();
        this.fsFamilyClass = reader.readSint16();
        reader.skip(26);//PANOSE+ulcharRange
        {
            byte[] achVendID = new byte[4];
            reader.read(achVendID);
            this.achVendID = new String(achVendID,0,0,4).trim();
        }
        this.fsSelection = reader.readUint16();
        this.fsFirstCharIndex = reader.readUint16();
        this.fsLastCharIndex = reader.readUint16();
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
