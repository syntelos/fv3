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
import fv3.font.ttf.Cmap;
import fv3.font.ttf.Glyf;
import fv3.font.ttf.Head;
import fv3.font.ttf.Hhea;
import fv3.font.ttf.Hmtx;
import fv3.font.ttf.Loca;
import fv3.font.ttf.Maxp;
import fv3.font.ttf.Name;
import fv3.font.ttf.Os2;
import fv3.font.ttf.Post;
import fv3.font.ttf.Table;
import fv3.font.ttf.TTF;
import fv3.font.ttf.TTCF;
import fv3.font.ttf.TYP1;


/**
 * Open Type / True Type Font (OTF/TTF) reader developed from <a
 * href="http://developer.apple.com/textfonts/TTRefMan/RM01/Chap1.html">Apple's
 * True Type Reference Manual</a>, and the <a
 * href="http://fontforge.sf.net/">FontForge</a> codebase.
 * 
 * This is intended as a data source for compiling font shape data
 * into generated code.  The first goal for {@link TTFFont} is for
 * fixed width fonts without hinting.
 * 
 * As of this writing, this class implements only the most essential
 * OTF/TTF feature set for its intended purpose.  
 * It does not implement TTCF, CID and Type1.
 * 
 * 
 * @author John Pritchard
 */
public class TTFFont
    extends Font<TTFGlyph>
{
    private final static int MAGIC_TTCF = ('t'<<24)|('t'<<16)|('c'<<8)|('f');
    private final static int MAGIC_TYP1 = ('t'<<24)|('y'<<16)|('p'<<8)|('1');
    private final static int MAGIC_CID = ('C'<<24)|('I'<<16)|('D'<<8)|(' ');
    private final static int MAGIC_TTF1 = 0x10000;
    private final static int MAGIC_TTF2 = 0x20000;
    private final static int MAGIC_TTF3 = ('t'<<24)|('r'<<16)|('u'<<8)|('e');
    private final static int MAGIC_OTTO = ('O'<<24)|('T'<<16)|('T'<<8)|('O');


    public final boolean isTTF, isTTCF, isTYP1, isCID;

    private final TTF ttf;
    private final TTCF ttcf;
    private final TYP1 typ1;
    private final CID cid;

    private lxl.Index map;

    private double scale;


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
        switch (magic = reader.readSint32()){

        case MAGIC_TTF1:
        case MAGIC_TTF2:
        case MAGIC_TTF3:
        case MAGIC_OTTO:
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
            throw new IllegalStateException(String.format("Unrecognized file format for '%s' (0x%x)",name,magic));
        }

        this.isTTF = isTTF;
        this.isTTCF = isTTCF;
        this.isTYP1 = isTYP1;
        this.isCID = isCID;
        this.ttf = ttf;
        this.ttcf = ttcf;
        this.typ1 = typ1;
        this.cid = cid;
        /*
         * Init
         */
        if (this.isTTF)
            this.ttf.init(this,reader);
        else if (this.isTTCF)
            this.ttcf.init(this,reader);
        else if (this.isTYP1)
            this.typ1.init(this,reader);
        else 
            this.cid.init(this,reader);
    }


    public TTFGlyph get(char ch){
        lxl.Index map = this.map;
        if (null != map)
            return this.get(map.get(new Character(ch)));
        else
            throw new IllegalStateException("Glyphs not mapped.");
    }
    public final double getScale(){
        double scale = this.scale;
        if (0.0 == scale){
            scale = this.getTableHead().scale(this.options);
            this.scale = scale;
        }
        return scale;
    }
    public final double getEm(){

        Head head = this.getTableHead();
        if (null != head)
            return head.emsize;
        else
            return 0.0;
    }
    public final double getMinX(){

        Head head = this.getTableHead();
        if (null != head)
            return head.minX;
        else
            return 0.0;
    }
    public final double getMinY(){

        Head head = this.getTableHead();
        if (null != head)
            return head.minY;
        else
            return 0.0;
    }
    public final double getMaxX(){

        Head head = this.getTableHead();
        if (null != head)
            return head.maxX;
        else
            return 0.0;
    }
    public final double getMaxY(){

        Head head = this.getTableHead();
        if (null != head)
            return head.maxY;
        else
            return 0.0;
    }
    public final double getAscent(){

        Hhea hhea = this.getTableHhea();
        if (null != hhea)
            return hhea.ascent;
        else
            return 0.0;
    }
    public final double getDescent(){

        Hhea hhea = this.getTableHhea();
        if (null != hhea)
            return hhea.descent;
        else
            return 0.0;
    }
    public final double getLeading(){

        Hhea hhea = this.getTableHhea();
        if (null != hhea)
            return hhea.leading;
        else
            return 0.0;
    }
    public final String getCopyright(){
        Name name = this.getTableName();
        if (null != name)
            return name.copyright;
        else
            return null;
    }
    public final String getFamily(){
        Name name = this.getTableName();
        if (null != name)
            return name.family;
        else
            return null;
    }
    public final String getSubfamily(){
        Name name = this.getTableName();
        if (null != name)
            return name.subfamily;
        else
            return null;
    }
    public final String getUniqueId(){
        Name name = this.getTableName();
        if (null != name)
            return name.uniqueid;
        else
            return null;
    }
    public final String getFullname(){
        Name name = this.getTableName();
        if (null != name)
            return name.fullname;
        else
            return null;
    }
    public final String getVersion(){
        Name name = this.getTableName();
        if (null != name)
            return name.version;
        else
            return null;
    }
    public final String getFontname(){
        Name name = this.getTableName();
        if (null != name)
            return name.fontname;
        else
            return null;
    }
    public final String getTrademark(){
        Name name = this.getTableName();
        if (null != name)
            return name.trademark;
        else
            return null;
    }
    public final String getManufacturer(){
        Name name = this.getTableName();
        if (null != name)
            return name.manufacturer;
        else
            return null;
    }
    public final String getDesigner(){
        Name name = this.getTableName();
        if (null != name)
            return name.designer;
        else
            return null;
    }
    public final String getDescriptor(){
        Name name = this.getTableName();
        if (null != name)
            return name.descriptor;
        else
            return null;
    }
    public final String getVendorUrl(){
        Name name = this.getTableName();
        if (null != name)
            return name.vendorurl;
        else
            return null;
    }
    public final String getDesignerUrl(){
        Name name = this.getTableName();
        if (null != name)
            return name.designerurl;
        else
            return null;
    }
    public final String getLicense(){
        Name name = this.getTableName();
        if (null != name)
            return name.license;
        else
            return null;
    }
    public final String getLicenseUrl(){
        Name name = this.getTableName();
        if (null != name)
            return name.licenseurl;
        else
            return null;
    }
    public final int countFaces(){
        if (this.isTTCF)
            return this.ttcf.getLength();
        else
            throw new IllegalStateException("Not TTCF");
    }
    public final String firstFaceName(){
        if (this.isTTCF)
            return this.ttcf.firstName();
        else
            throw new IllegalStateException("Not TTCF");
    }
    public final String getFaceName(int idx){
        if (this.isTTCF)
            return this.ttcf.getName(idx);
        else
            throw new IllegalStateException("Not TTCF");
    }
    public final int indexOfFaceName(String name){
        if (this.isTTCF)
            return this.ttcf.indexOfName(name);
        else
            throw new IllegalStateException("Not TTCF");
    }
    public final int countTables(){
        if (this.isTTF)
            return this.ttf.countTables();
        else
            throw new IllegalStateException("Not TTF");
    }
    public final Table getTable(int idx){
        if (this.isTTF)
            return this.ttf.getTable(idx);
        else
            throw new IllegalStateException("Not TTF");
    }
    public final String getTableName(int idx){
        if (this.isTTF)
            return this.ttf.getName(idx);
        else
            throw new IllegalStateException("Not TTF");
    }
    public final int getTableType(int idx){
        if (this.isTTF)
            return this.ttf.getType(idx);
        else
            throw new IllegalStateException("Not TTF");
    }
    public final Table getTableByType(int type){
        if (this.isTTF)
            return this.ttf.getTableByType(type);
        else
            throw new IllegalStateException("Not TTF");
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
    public final Hmtx getTableHmtx(){
        return (Hmtx)this.getTableByType(Hmtx.TYPE);
    }
    public final Loca getTableLoca(){
        return (Loca)this.getTableByType(Loca.TYPE);
    }
    public final Maxp getTableMaxp(){
        return (Maxp)this.getTableByType(Maxp.TYPE);
    }
    public final Name getTableName(){
        return (Name)this.getTableByType(Name.TYPE);
    }
    public final Os2 getTableOs2(){
        return (Os2)this.getTableByType(Os2.TYPE);
    }
    public final Post getTablePost(){
        return (Post)this.getTableByType(Post.TYPE);
    }
    public void createGlyph(Glyf glyf, int index, int offset, int next, TTFFontReader reader){

        TTFGlyph glyph = new TTFGlyph(this,glyf,index,offset,next);

        this.add(glyph);
    }
    public void mapGlyphs(){
        this.map = new lxl.Index(this.getLength());
    }
    public void mapGlyph(int index, char ch){
        TTFGlyph glyph = this.get(index);
        if (null != glyph){
            glyph.character = ch;
            this.map.add(new Character(ch),index);
        }
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
                int count = font.countTables();
                System.out.printf("Found TTF font '%s' with %d tables.\n",font.getName(),count);
                for (int cc = 0; cc < count; cc++){
                    System.out.printf("\tTable '%s'.\n",font.getTableName(cc));
                    switch (font.getTableType(cc)){

                    case fv3.font.ttf.Glyf.TYPE:{
                        for (TTFGlyph glyph: font)
                            System.out.printf("\t\t%s\n",glyph.toString());// ",\n\t\t      "

                        break;
                    }
                    case fv3.font.ttf.Head.TYPE:{
                        fv3.font.ttf.Head table = (fv3.font.ttf.Head)font.getTable(cc);
                        System.out.printf("\t\tEm-Size %f\n",table.emsize);
                        System.out.printf("\t\tMin-X %f\n",table.minX);
                        System.out.printf("\t\tMin-Y %f\n",table.minY);
                        System.out.printf("\t\tMax-X %f\n",table.maxX);
                        System.out.printf("\t\tMax-Y %f\n",table.maxY);
                        break;
                    }
                    case fv3.font.ttf.Hhea.TYPE:{
                        fv3.font.ttf.Hhea table = (fv3.font.ttf.Hhea)font.getTable(cc);
                        System.out.printf("\t\tAscent %f\n",table.ascent);
                        System.out.printf("\t\tDescent %f\n",table.descent);
                        System.out.printf("\t\tLeading %f\n",table.leading);
                        System.out.printf("\t\tAdvance-Width-Max %f\n",table.advanceWidthMax);
                        System.out.printf("\t\tMin-Left-Side-Bearing %f\n",table.minLeftSideBearing);
                        System.out.printf("\t\tMin-Right-Side-Bearing %f\n",table.minRightSideBearing);
                        System.out.printf("\t\tX-Max-Extent %f\n",table.xMaxExtent);
                        break;
                    }
                    case fv3.font.ttf.Maxp.TYPE:{
                        fv3.font.ttf.Maxp table = (fv3.font.ttf.Maxp)font.getTable(cc);
                        System.out.printf("\t\tGlyph-Count %d\n",table.glyphCount);
                        break;
                    }
                    case fv3.font.ttf.Name.TYPE:{
                        fv3.font.ttf.Name table = (fv3.font.ttf.Name)font.getTable(cc);
                        if (null != table.copyright)
                            System.out.printf("\t\tCopyright %s\n",table.copyright);
                        if (null != table.family)
                            System.out.printf("\t\tFamily %s\n",table.family);
                        if (null != table.subfamily)
                            System.out.printf("\t\tSub-Family %s\n",table.subfamily);
                        if (null != table.uniqueid)
                            System.out.printf("\t\tFullname %s\n",table.uniqueid);
                        if (null != table.fullname)
                            System.out.printf("\t\tFullname %s\n",table.fullname);
                        if (null != table.version)
                            System.out.printf("\t\tVersion %s\n",table.version);
                        if (null != table.fontname)
                            System.out.printf("\t\tFontname %s\n",table.fontname);
                        if (null != table.trademark)
                            System.out.printf("\t\tTrademark %s\n",table.trademark);
                        if (null != table.manufacturer)
                            System.out.printf("\t\tManufacturer %s\n",table.manufacturer);
                        if (null != table.designer)
                            System.out.printf("\t\tDesigner %s\n",table.designer);
                        if (null != table.descriptor)
                            System.out.printf("\t\tDescriptor %s\n",table.descriptor);
                        if (null != table.vendorurl)
                            System.out.printf("\t\tVendor-Url %s\n",table.vendorurl);
                        if (null != table.designerurl)
                            System.out.printf("\t\tDesigner-Url %s\n",table.designerurl);
                        if (null != table.license)
                            System.out.printf("\t\tLicense %s\n",table.license);
                        if (null != table.licenseurl)
                            System.out.printf("\t\tLicense-Url %s\n",table.licenseurl);
                        break;
                    }
                    case fv3.font.ttf.Os2.TYPE:{
                        fv3.font.ttf.Os2 table = (fv3.font.ttf.Os2)font.getTable(cc);
                        System.out.printf("\t\tAch-Vend-ID %s\n",table.achVendID);
                        System.out.printf("\t\tUs-Weight-Class %d\n",table.usWeightClass);
                        System.out.printf("\t\tUs-Width-Class %d\n",table.usWidthClass);
                        System.out.printf("\t\tFs-Type %d\n",table.fsType);
                        System.out.printf("\t\tFs-Family-Class %d\n",table.fsFamilyClass);
                        System.out.printf("\t\tFs-Selection %d\n",table.fsSelection);
                        System.out.printf("\t\tFs-First-Char-Index %d\n",table.fsFirstCharIndex);
                        System.out.printf("\t\tFs-Last-Char-Index %d\n",table.fsLastCharIndex);
                        System.out.printf("\t\tX-Average-Char-Width %f\n",table.xAverageCharWidth);
                        System.out.printf("\t\tY-Subscript-X-Size %f\n",table.ySubscriptXSize);
                        System.out.printf("\t\tY-Subscript-Y-Size %f\n",table.ySubscriptYSize);
                        System.out.printf("\t\tY-Subscript-X-Offset %f\n",table.ySubscriptXOffset);
                        System.out.printf("\t\tY-Subscript-Y-Offset %f\n",table.ySubscriptYOffset);
                        System.out.printf("\t\tY-Superscript-X-Size %f\n",table.ySuperscriptXSize);
                        System.out.printf("\t\tY-Superscript-Y-Size %f\n",table.ySuperscriptYSize);
                        System.out.printf("\t\tY-Superscript-X-Offset %f\n",table.ySuperscriptXOffset);
                        System.out.printf("\t\tY-Superscript-Y-Offset %f\n",table.ySuperscriptYOffset);
                        System.out.printf("\t\tY-Strikeout-Size %f\n",table.yStrikeoutSize);
                        System.out.printf("\t\tY-Strikeout-Position %f\n",table.yStrikeoutPosition);

                        break;
                    }
                    }
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
