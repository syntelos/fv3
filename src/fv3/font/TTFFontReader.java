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

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.BufferUnderflowException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.UnsupportedCharsetException;

/**
 * <h3>TTF I/O Note</h3>
 * 
 * This package is using Sint32 where Uint32 is correct in terms of
 * the specification of the file format.  As the I/O data interface is
 * based on Sint32 addressing, the Sint32 is correct in this
 * (operating) context.  The full width of the Sint32 is not employed,
 * only zero- positive values are employed.
 * 
 * @author John Pritchard
 */
public class TTFFontReader
    extends FontReader
{
    private final static long SfntDate1970 = 0x7c25b080;



    public TTFFontReader(String resource)
        throws IOException
    {
        super(resource);
    }
    public TTFFontReader(ByteBuffer in){
        super(in);
    }
    public TTFFontReader(File source)
        throws IOException
    {
        super(source);
    }



    public int readOffset(int size) {
        switch (size){
        case 1:
            return this.readUint8();
        case 2:
            return this.readUint16();
        case 3:
            return this.readUint24();
        default:
            return this.readSint32();
        }
    }
    public double read1616() {
        int val = this.readSint32();

        double integer = (val>>16);
        double mantissa = ((val & 0xffff) / 65536.0);

        return (integer + mantissa);
    }
    public double read214() {
        int val = this.readUint16();

        double integer = ((val<<16)>>30);
        double mantissa = ((val & 0x3fff) / 16384.0);

        return (integer + mantissa);
    }
    public long readDate(){
        return (this.readSint64() - SfntDate1970);
    }
    public String readString(int p, int s, int ofs, int len){
        try {
            Charset enc = Encoding(p,s);
            this.seek(ofs);
            ByteBuffer bary = ByteBuffer.allocate(len);
            this.read(bary);
            CharBuffer cary = enc.decode(bary);
            return cary.toString();
        }
        catch (UnsupportedCharsetException unknown){

            return null;
        }
    }

    public final static Charset Encoding(int platform, int specific){
        switch (platform){
        case 0:
            switch (specific){
            case 4:
                return Charset.forName("UTF-16");
            default:
                return Charset.forName("UTF-8");
            }
        case 1:
            switch (specific){
            case 0:
                return Charset.forName("Mac");
            case 1:
                return Charset.forName("Sjis");
            case 2:
                return Charset.forName("Big5hkscs");
            case 3:
                return Charset.forName("EUC-KR");
            case 25:
                return Charset.forName("EUC-CN");
            default:
                return Charset.forName("Unknown");
            }
        case 2:
            switch (specific){
            case 0:
                return Charset.forName("ASCII");
            case 1:
                return Charset.forName("UTF-8");
            case 2:
                return Charset.forName("ISO8859-1");
            default:
                return Charset.forName("Unknown");
            }
        case 3:
            switch (specific){
            case 0:
            case 1:
                return Charset.forName("UTF-8");
            case 2:
                return Charset.forName("EUC-CN");
            case 3:
                return Charset.forName("Sjis");
            case 4:
                return Charset.forName("Big5hkscs");
            case 5:
                return Charset.forName("EUC-KR");
            case 6:
                return Charset.forName("Johab");
            case 10:
                return Charset.forName("UTF-16");
            default:
                return Charset.forName("Unknown");
            }
        default:
            return Charset.forName("Unknown");
        }
    }
}
