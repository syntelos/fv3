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
 * name table
 * 
 * @author John Pritchard
 */
public final class Name
    extends Table
    implements Cloneable
{
    public final static int ID = ('n'<<24)|('a'<<16)|('m'<<8)|('e');
    public final static int TYPE = 49;
    public final static String NAME = "name";
    public final static String DESC = "name table";


    public String copyright, family, subfamily, uniqueid, fullname, version, 
        fontname, trademark, manufacturer, designer, descriptor, vendorurl, 
        designerurl, license, licenseurl;


    protected Name(int ofs, int len) {
        super(ofs,len);
    }


    public void init(TTFFont font, TTF tables, TTFFontReader reader){
        this.seekto(reader);
        reader.readUint16();
        int count = reader.readUint16();
        int tabof = this.offset + reader.readUint16();
        for (int cc = 0; cc < count; cc++){
            int plat = reader.readUint16();
            int spec = reader.readUint16();
            int lang = reader.readUint16();
            int name = reader.readUint16();
            int strl = reader.readUint16();
            int stro = tabof + reader.readUint16();
            String string = reader.readString(plat,spec,stro,strl);
            if (null != string){
                switch (name){
                case TTF_COPYRIGHT:
                    this.copyright = string;
                    break;
                case TTF_FAMILY:
                    this.family = string;
                    break;
                case TTF_SUBFAMILY:
                    this.subfamily = string;
                    break;
                case TTF_UNIQUEID:
                    this.uniqueid = string;
                    break;
                case TTF_FULLNAME:
                    this.fullname = string;
                    break;
                case TTF_VERSION:
                    this.version = string;
                    break;
                case TTF_POSTSCRIPTNAME:
                    this.fontname = string;
                    break;
                case TTF_TRADEMARK:
                    this.trademark = string;
                    break;
                case TTF_MANUFACTURER:
                    this.manufacturer = string;
                    break;
                case TTF_DESIGNER:
                    this.designer = string;
                    break;
                case TTF_DESCRIPTOR:
                    this.descriptor = string;
                    break;
                case TTF_VENDORURL:
                    this.vendorurl = string;
                    break;
                case TTF_DESIGNERURL:
                    this.designerurl = string;
                    break;
                case TTF_LICENSE:
                    this.license = string;
                    break;
                case TTF_LICENSEURL:
                    this.licenseurl = string;
                    break;
                }
            }
        }
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
