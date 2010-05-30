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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * The <code>"/fonts/fonts.dir"</code> resource is a plain text file
 * listing font file names without filename extensions -- one per
 * line.
 */
public class FontsDir
    extends Object
{
    public final static Charset UTF8 = Charset.forName("UTF-8");


    public final int size;

    public final String[] names;

    public final lxl.Index map;


    public FontsDir(){
        super();
        String names[] = null;
        try {
            InputStream in = this.getClass().getResourceAsStream("/fonts/fonts.dir");
            if (null != in){
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in,UTF8));
                    String line;
                    while (null != (line = reader.readLine())){
                        if (0 != line.length() && '#' != line.charAt(0)){
                            if (null == names)
                                names = new String[]{line};
                            else {
                                int len = names.length;
                                String[] copier = new String[len+1];
                                System.arraycopy(names,0,copier,0,len);
                                copier[len] = line;
                                names = copier;
                            }
                        }
                    }
                }
                finally {
                    in.close();
                }
            }
        }
        catch (java.io.IOException exc){
            exc.printStackTrace();
        }
        this.names = names;
        if (null != names){
            int count = names.length;
            if (0 < count){
                this.size = count;
                this.map = new lxl.Index(count);
                for (int cc = 0; cc < count; cc++){
                    this.map.put(names[cc],cc);
                }
            }
            else {
                this.size = 0;
                this.map = new lxl.Index(10);
            }
        }
        else {
            this.size = 0;
            this.map = new lxl.Index(10);
        }
    }


    public int indexOf(String name){
        return this.map.get(name);
    }
}
