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
package fv3.font;

import fv3.font.ttf.CID;
import fv3.font.ttf.Table;
import fv3.font.ttf.TTF;
import fv3.font.ttf.TTCF;
import fv3.font.ttf.TYP1;


/**
 * Developed from reading Apple "TTRefMan", FreeType, FontForge and
 * SIL "Intro to".  Of these, the most directly useful was the
 * FontForge codebase, downstream of the FreeType codebase.
 * 
 * @author John Pritchard
 */
public class TTFFont
    extends Font
{
    private final static int MAGIC_TTCF = ('t'<<24)|('t'<<16)|('c'<<8)|('f');
    private final static int MAGIC_TYP1 = ('t'<<24)|('y'<<16)|('p'<<8)|('1');
    private final static int MAGIC_CID = ('C'<<24)|('I'<<16)|('D'<<8)|(' ');
    private final static int MAGIC_OTF1 = 0x10000;
    private final static int MAGIC_OTF2 = 0x20000;
    private final static int MAGIC_OTF3 = ('t'<<24)|('r'<<16)|('u'<<8)|('e');
    private final static int MAGIC_OTF4 = ('O'<<24)|('T'<<16)|('T'<<8)|('O');


    public final boolean isTTF, isTTCF, isTYP1, isCID;

    private final TTF ttf;
    private final TTCF ttcf;
    private final TYP1 typ1;
    private final CID cid;


    public TTFFont(String name, TTFFontReader reader) {
        this(name,reader,(new FontOptions()));
    }
    public TTFFont(String name, TTFFontReader reader, FontOptions opts) {
        super(name,reader,opts);
        boolean isTTF = false, isTTCF = false, isTYP1 = false, isCID = false;
        TTF ttf = null;
        TTCF ttcf = null;
        TYP1 typ1 = null;
        CID cid = null;

        int magic;
        switch (magic = reader.readUint32()){

        case MAGIC_OTF1:
        case MAGIC_OTF2:
        case MAGIC_OTF3:
        case MAGIC_OTF4:
            isTTF = true;
            ttf = new TTF(this,reader);
            break;
        case MAGIC_TTCF:
            isTTCF = true;
            ttcf = new TTCF(this,reader);
            break;
        case MAGIC_TYP1:
            isTYP1 = true;
            typ1 = new TYP1(this,reader);
            break;
        case MAGIC_CID:
            isCID = true;
            cid = new CID(this,reader);
            break;
        default:
            throw new IllegalStateException(String.format("Bad file magic for '%s': %x.",name,magic));
        }

        this.isTTF = isTTF;
        this.isTTCF = isTTCF;
        this.isTYP1 = isTYP1;
        this.isCID = isCID;
        this.ttf = ttf;
        this.ttcf = ttcf;
        this.typ1 = typ1;
        this.cid = cid;
    }


    public int countFaces(){
        if (this.isTTCF)
            return this.ttcf.getLength();
        else
            return 0;
    }
    public String firstFaceName(){
        if (this.isTTCF)
            return this.ttcf.firstName();
        else
            return null;
    }
    public String getFaceName(int idx){
        if (this.isTTCF)
            return this.ttcf.getName(idx);
        else
            return null;
    }
    public int indexOfFaceName(String name){
        if (this.isTTCF)
            return this.ttcf.indexOfName(name);
        else
            return -1;
    }
    public int countTables(){
        if (this.isTTF)
            return this.ttcf.getLength();
        else
            return 0;
    }
    public Table getTable(int idx){
        if (this.isTTF)
            return this.ttf.getTable(idx);
        else
            return null;
    }
    public String getTableName(int idx){
        if (this.isTTF)
            return this.ttf.getName(idx);
        else
            return null;
    }
    public Table getTableByType(int type){
        if (this.isTTF)
            return this.ttf.getTableByType(type);
        else
            return null;
    }
    protected Glyph createGlyph(){

        return new TTFGlyph(this);
    }


    public static void main(String[] argv){
        java.io.File file = new java.io.File(argv[0]);
        try {
            TTFFontReader reader = new TTFFontReader(file);
            TTFFont font = new TTFFont(file.getName(),reader);
            if (font.isTTCF){
                int count = font.countFaces();
                System.out.printf("Found TTCF font '%s' with %d faces.\n",count,font.getName());
                for (int cc = 0; cc < count; cc++){
                    System.out.printf("\tFace '%s'.\n",font.getFaceName(cc));
                }
            }
            else if (font.isTTF){
                int count = font.countFaces();
                System.out.printf("Found TTF font '%s' with %d tables.\n",font.getName(),count);
                for (int cc = 0; cc < count; cc++){
                    System.out.printf("\tTable '%s'.\n",font.getTableName(cc));
                }
            }
            else {
                System.out.printf("Unrecognized font '%s'.\n",font.getName());
            }

            System.exit(0);
        }
        catch (java.io.IOException exc){
            exc.printStackTrace();
            System.exit(1);
        }
    }
}
