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
/*
 * Copyright (c) James P. Buzbee 1996
 * House Blend Software
 * 
 *  Permission to use, copy, modify, and distribute this software
 *  for any use is hereby granted provided
 *  this notice is kept intact within the source file
 *  This is freeware, use it as desired !
 * 
 *  Very loosly based on code with authors listed as :
 *  Alan Richardson, Pete Holzmann, James Hurt
 */
package fv3.font;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.BufferUnderflowException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.UnsupportedCharsetException;

/**
 * 
 * 
 * @author John Pritchard
 */
public class HersheyFontReader
    extends FontReader
{
    public static InputStream ResourceAsStream(String name)
        throws IOException
    {
        return HersheyFontReader.class.getResourceAsStream("/fonts/"+name+".jhf");
    }


    public HersheyFontReader(String name)
        throws IOException
    {
        super("/fonts/"+name+".jhf");
    }
    public HersheyFontReader(String name, ByteBuffer in){
        super(name, in);
    }
    public HersheyFontReader(File source)
        throws IOException
    {
        super(source);
    }


    protected int asciiInt (int n){
        char[] buf = new char[n];
        int c;
        int j = 0;
        try {
            for (int i = 0; i < n; i++){
                c = this.read();

                while ((c == '\n') || (c == '\r')){
                    c = this.read();
                }

                if ((char) c != ' '){

                    buf[j++] = (char) c;
                }
            }
            return (Integer.parseInt (String.copyValueOf (buf, 0, j)));
        }
        catch (java.nio.BufferUnderflowException eof){

            return -1;
        }
    }

}
