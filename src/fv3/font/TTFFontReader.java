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
import java.io.InputStream;
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
public final class TTFFontReader
    extends FontReader
{
    private final static long SfntDate1970 = 0x7c25b080;

    public static InputStream ResourceAsStream(String name)
        throws IOException
    {
        return TTFFontReader.class.getResourceAsStream("/fonts/"+name+".ttf");
    }


    public TTFFontReader(String name)
        throws IOException
    {
        super("/fonts/"+name+".ttf");
    }
    public TTFFontReader(String name, ByteBuffer in){
        super(name, in);
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
    public float read1616() {
        int val = this.readSint32();

        float integer = (val>>16);
        float fraction = ((val & 0xffff) / 65536.0f);

        return (integer + fraction);
    }
    public float read214() {
        int val = this.readUint16();

        float integer = ((val<<16)>>30);
        float fraction = ((val & 0x3fff) / 16384.0f);

        return (integer + fraction);
    }
    public long readDate(){
        return (this.readSint64() - SfntDate1970);
    }
    public String readString(int p, int s, int ofs, int len){
        try {
            int position = this.buffer.position();
            Charset enc = Encoding(p,s);
            this.buffer.position(ofs);
            ByteBuffer bary = ByteBuffer.allocate(len);
            this.read(bary);
            CharBuffer cary = enc.decode(bary);
            this.buffer.position(position);
            return cary.toString();
        }
        catch (UnsupportedCharsetException unknown){

            return null;
        }
    }
    private final static Charset CharsetUnknown = Charset.forName("UTF-16");

    public final static Charset Encoding(int platform, int specific){

        switch (platform){
        case 0:
            switch (specific){
            case 4:
                return Charset.forName("UTF-32");
            default:
                return Charset.forName("UTF-16");
            }
        case 1:
            switch (specific){
            case 0:
                return Charset.forName("UTF-16");
            case 1:
                return Charset.forName("Sjis");
            case 2:
                return Charset.forName("Big5");
            case 3:
                return Charset.forName("EUC-KR");
            case 25:
                return Charset.forName("EUC-CN");
            default:
                return CharsetUnknown;
            }
        case 2:
            switch (specific){
            case 0:
                return Charset.forName("US-ASCII");
            case 1:
                return Charset.forName("UTF-16");
            case 2:
                return Charset.forName("ISO8859-1");
            default:
                return CharsetUnknown;
            }
        case 3:
            switch (specific){
            case 0:
            case 1:
                return Charset.forName("UTF-16");
            case 2:
                return Charset.forName("EUC-CN");
            case 3:
                return Charset.forName("Sjis");
            case 4:
                return Charset.forName("Big5");
            case 5:
                return Charset.forName("EUC-KR");
            case 6:
                return Charset.forName("Johab");
            case 10:
                return Charset.forName("UTF-32");
            default:
                return CharsetUnknown;
            }
        default:
            return CharsetUnknown;
        }
    }


    public static class TestF2DOT14 {

        public final static float F214(int bits){
            float integer = ((bits<<16)>>30);
            float fraction = ((bits & 0x3fff) / 16384.0f);
            return (integer+fraction);
        }

        public static TestF2DOT14[] List = {
            new TestF2DOT14(1.99993896484375f,  0x7fff),
            new TestF2DOT14(1.75f,      0x7000),
            new TestF2DOT14(6.103515625E-5f,  0x0001),
            new TestF2DOT14(0.0f,       0x0000),
            new TestF2DOT14(-6.103515625E-5f, 0xffff),
            new TestF2DOT14(-2.0f,      0x8000)
        };


        public final float R;

        public final int B;


        TestF2DOT14(float R, int B){
            super();
            this.R = R;
            this.B = B;
        }

        public boolean evaluate(){

            return (this.R == F214(this.B));
        }
        public float value(){

            return F214(this.B);
        }
        public String toString(){
            return String.format("R:%f, B:%x",this.R, this.B);
        }
    }

    public final static void main(String[] argv){
        int errors = 0;
        for (TestF2DOT14 test: TestF2DOT14.List){
            if (test.evaluate())
                System.out.println("OK "+test);
            else {
                System.out.println("ER "+test.value()+" != "+test);
                errors += 1;
            }
        }
        System.exit(errors);
    }
}
