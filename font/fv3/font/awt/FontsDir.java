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
package fv3.font.awt;

import fv3.font.FontOptions;
import fv3.font.TTFFontReader;

import java.awt.Font;
import java.io.InputStream;

/**
 * 
 * @author jdp
 */
public class FontsDir
    extends fv3.font.FontsDir
{

    public final Font[] awt;

    public final TTFFont[] cache;


    public FontsDir(){
        super();
        int count = this.size();
        if (0 < count){
            this.awt = new Font[count];
            for (int cc = 0; cc < count; cc++){
                String name = this.get(cc);
                try {
                    InputStream in = TTFFontReader.ResourceAsStream(name);
                    if (null != in){
                        this.awt[cc] = Font.createFont(Font.TRUETYPE_FONT,in);
                        in.close();
                    }
                }
                catch (Exception exc){
                    synchronized(System.err){
                        System.err.printf("Error reading font '%s':\n\t",name);
                        exc.printStackTrace(System.err);
                    }
                }
            }
            this.cache = new TTFFont[count];
        }
        else {
            this.awt = new Font[0];
            this.cache = new TTFFont[0];
        }
    }


    public TTFFont cache(String name, double fow, double foh){
        return this.cache(this.indexOf(name),name,fow,foh);
    }
    public TTFFont cache(int index, double fow, double foh){
        return this.cache(index,this.get(index),fow,foh);
    }
    private TTFFont cache(int index, String name, double fow, double foh){
        if (-1 != index && null != name){
            TTFFont font = this.cache[index];
            if (null != font){
                if (fow == font.options.width && foh == font.options.height)
                    return font;
            }

            FontOptions options = new FontOptions(fow,foh);
            try {
                TTFFontReader reader = new TTFFontReader(name);

                font = new TTFFont(name,reader,options);

                this.cache[index] = font;

                return font;
            }
            catch (java.io.IOException exc){
                exc.printStackTrace();
            }
        }
        return null;
    }
    public Font get(String name){
        int index = this.indexOf(name);
        if (-1 != index)
            return this.awt[index];
        else
            return null;
    }
    public Font derive(String name, int style, int size){
        int index = this.indexOf(name);
        if (-1 != index){
            Font font = this.awt[index];
            if (null != font)
                return font.deriveFont(style,size);
        }
        return null;
    }
    public Font derive(int index, int style, int size){
        if (-1 != index){
            Font font = this.awt[index];
            if (null != font)
                return font.deriveFont(style,size);
        }
        return null;
    }
    public Font as(String name, int style, int size){
        int index = this.indexOf(name);
        if (-1 != index){
            Font font = this.awt[index];
            if (null != font){
                if (size == font.getSize() && style == font.getStyle())
                    return font;
                else {
                    font = font.deriveFont(style,size);
                    this.awt[index] = font;
                    return font;
                }
            }
        }
        return null;
    }
    public Font as(int index, int style, int size){
        if (-1 != index){
            Font font = this.awt[index];
            if (null != font){
                if (size == font.getSize() && style == font.getStyle())
                    return font;
                else {
                    font = font.deriveFont(style,size);
                    this.awt[index] = font;
                    return font;
                }
            }
        }
        return null;
    }
}
