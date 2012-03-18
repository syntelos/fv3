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
    extends lxl.Set<String>
{
    public final static Charset UTF8 = Charset.forName("UTF-8");



    public FontsDir(){
        super();
        try {
            InputStream in = this.getClass().getResourceAsStream("/fonts/fonts.dir");
            if (null != in){
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in,UTF8));
                    String line;
                    while (null != (line = reader.readLine())){
                        line = line.trim();
                        if (0 != line.length() && '#' != line.charAt(0)){
                            this.add(line);
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
    }

}
