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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * 
 * @author John Pritchard
 */
public class FontReader
    extends Object
    implements java.io.Closeable
{

    public static ByteBuffer Resource(String name)
        throws IOException
    {
        InputStream in = FontReader.class.getResourceAsStream("/fonts/"+name+".cff");

        if (in == null)
            throw new IOException(String.format("Resource not found '%s'",name));
        else {
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                int array_size = 1024; // choose a size...
                byte[] array = new byte[array_size];
                int rb;

                while ((rb = in.read(array, 0, array_size)) > -1) {
                    bytes.write(array, 0, rb);
                }
                return ByteBuffer.wrap(bytes.toByteArray());
            }
            finally {
                in.close();
            }
        }
    }


    private ByteBuffer buffer;


    public FontReader(String resource)
        throws IOException
    {
        this(Resource(resource));
    }
    public FontReader(ByteBuffer in){
        super();
        if (null != in)
            this.buffer = in;
        else
            throw new IllegalArgumentException();
    }


    public Glyph read(Font font){
        if (null == this.buffer)
            throw new IllegalStateException("Closed");
        else {
            try {
                Glyph glyph = font.createGlyph();
                glyph.read(this);
                return glyph;
            }
            catch (java.nio.BufferUnderflowException end){

                return null;
            }
        }
    }
    public void close() throws IOException {

        this.buffer = null;
    }
    private int read(){

        int v = this.buffer.get() & 0xFF;

        if (v != 0x80) {
            return v << 8;
        }
        else {
            int v2 = this.buffer.get() & 0xFF;

            if (v2 == 0x00) {
                return 0x8000;
            }
            else {
                int v3 = this.buffer.get() & 0xFF;

                return (v3 << 8) | v2;
            }
        }
    }
}
