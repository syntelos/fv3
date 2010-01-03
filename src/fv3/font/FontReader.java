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
import java.nio.BufferUnderflowException;
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
    public final static long MAX_FILESIZE = Integer.MAX_VALUE;

    public static ByteBuffer Resource(String name)
        throws IOException
    {
        InputStream in = FontReader.class.getResourceAsStream(name);

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


    protected ByteBuffer buffer;


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
    /**
     * Map the file into memory.
     */
    public FontReader(File source)
        throws IOException
    {
        super();
        if (null != source && source.isFile()){
            long length = source.length();
            if (MAX_FILESIZE < length)
                throw new IllegalArgumentException("Size of file '"+source.getPath()+"' exceeds practical limits.");
            else {
                FileChannel fileChannel = (new FileInputStream(source)).getChannel();
                try {
                    this.buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY,0L,length);
                }
                finally {
                    fileChannel.close();
                }
            }
        }
        else if (null != source)
            throw new IllegalArgumentException("File not found, '"+source.getPath()+"'.");
        else
            throw new IllegalArgumentException("Null file argument.");
    }


    public int tell(){
        return this.buffer.position();
    }
    public boolean eof(){
        return (0 >= this.buffer.remaining());
    }
    public FontReader seek(int offset){
        this.buffer.position(offset);
        return this;
    }
    public FontReader skip(int many){
        if (0 < many)
            try {
                return this.seek(this.buffer.position()+many);
            }
            catch (IllegalArgumentException exc){
                throw new IllegalArgumentException(String.valueOf(many),exc);
            }
        else
            return this;
    }
    public FontReader read(byte[] bary, int ofs, int len){
        this.buffer.get(bary,ofs,len);
        return this;
    }
    public FontReader read(byte[] bary){
        this.buffer.get(bary);
        return this;
    }
    public FontReader read(ByteBuffer buf){
        this.buffer.get(buf.array());
        return this;
    }
    public int readUint8()
        throws BufferUnderflowException
    {
        return (this.buffer.get() & 0xff);
    }
    public byte readSint8()
        throws BufferUnderflowException
    {
        return this.buffer.get();
    }
    public int readUint16()
        throws BufferUnderflowException
    {
        int a = (this.buffer.get() & 0xff);
        int b = (this.buffer.get() & 0xff);

        return ((a<<8)|b);
    }
    public short readSint16()
        throws BufferUnderflowException
    {
        int a = (this.buffer.get() & 0xff);
        int b = (this.buffer.get() & 0xff);

        return (short)((a<<8)|b);
    }
    public int readUint24()
        throws BufferUnderflowException
    {
        int a = (this.buffer.get() & 0xff);
        int b = (this.buffer.get() & 0xff);
        int c = (this.buffer.get() & 0xff);

        return ((a<<16)|(b<<8)|c);
    }
    public long readUint32()
        throws BufferUnderflowException
    {
        long a = (this.buffer.get() & 0xff);
        long b = (this.buffer.get() & 0xff);
        long c = (this.buffer.get() & 0xff);
        long d = (this.buffer.get() & 0xff);

        return ((a<<24)|(b<<16)|(c<<8)|d);
    }
    public int readSint32()
        throws BufferUnderflowException
    {
        int a = (this.buffer.get() & 0xff);
        int b = (this.buffer.get() & 0xff);
        int c = (this.buffer.get() & 0xff);
        int d = (this.buffer.get() & 0xff);

        return ((a<<24)|(b<<16)|(c<<8)|d);
    }
    public long readSint64()
        throws BufferUnderflowException
    {
        long a = this.readUint32();
        long b = this.readUint32();

        return ((a<<32)|(b));
    }
    public void close() throws IOException {

        this.buffer = null;
    }
    public final static String Bitstring(int bits, int size){
        StringBuilder string = new StringBuilder();
        for (int sh = (size-1); -1 < sh; sh--){
            boolean bit = (1 == ((bits>>sh)&1));
            if (bit)
                string.append('1');
            else
                string.append('0');
        }
        return string.toString();
    }
}
