/*
 * fv3.ds
 * Copyright (c) 2009 John Pritchard, all rights reserved.
 *     
 * This program is free  software: you can redistribute it and/or modify 
 * it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program  is  distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU Lesser General Public License for more details.
 * 
 * You should  have received a copy of the GNU Lesser General Public License
 * along with  this program; If not, see <http://www.gnu.org/licenses/>. 
 */
package fv3.ds;

import fv3.Fv3Exception;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * Memory mapped file handler -- closed via garbage collection.
 * 
 * @see Chunk
 * @author jdp
 */
public final class Reader
    extends Object
    implements java.io.Closeable
{
    public final static long MAX_FILESIZE = Integer.MAX_VALUE;

    public final String name;
    public final int length;

    private ByteBuffer buffer;

    private int pos;


    /**
     * Map the file into memory.
     */
    public Reader(File source)
        throws IOException, Fv3Exception
    {
        super();
        if (null != source && source.isFile()){
            this.name = source.getPath();
            long length = source.length();
            if (MAX_FILESIZE < length)
                throw new Fv3Exception("Size of file '"+this.name+"' exceeds practical limits.");
            else {
                this.length = (int)length;
                FileChannel fileChannel = (new FileInputStream(source)).getChannel();
                try {
                    this.buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY,0L,this.length);
                }
                finally {
                    fileChannel.close();
                }
            }
        }
        else if (null != source)
            throw new Fv3Exception("File not found, '"+source.getPath()+"'.");
        else
            throw new Fv3Exception("Null file argument.");
    }
    public Reader(String name, ByteBuffer source)
        throws Fv3Exception
    {
        super();
        if (null != name && null != source){
            this.name = name;
            this.buffer = source;
            this.length = (source.limit() - source.position());
        }
        else
            throw new Fv3Exception("Null name or buffer argument.");
    }


    public byte readS8(Chunk cp) throws Fv3Exception {

        return this.buffer.get(cp.pos++);
    }
    public int readU8(Chunk cp) throws Fv3Exception {

        return (this.buffer.get(cp.pos++) & 0xff);
    }
    public int readU16(Chunk cp) throws Fv3Exception {

        return ((this.buffer.get(cp.pos++) & 0xff)
                | ((this.buffer.get(cp.pos++) & 0xff) << 8));
    }
    public short readS16(Chunk cp) throws Fv3Exception {

        return (short)((this.buffer.get(cp.pos++) & 0xff)
                       | ((this.buffer.get(cp.pos++) & 0xff) << 8));
    }
    public int readS32(Chunk cp) throws Fv3Exception {

        return ((this.buffer.get(cp.pos++) & 0xff)
                | ((this.buffer.get(cp.pos++) & 0xff) << 8)
                | ((this.buffer.get(cp.pos++) & 0xff) << 16)
                | (this.buffer.get(cp.pos++) << 24));
    }
    public int readU32(Chunk cp) throws Fv3Exception {

        return (((this.buffer.get(cp.pos++) & 0xff)
                 | ((this.buffer.get(cp.pos++) & 0xff) << 8)
                 | ((this.buffer.get(cp.pos++) & 0xff) << 16)
                 | (this.buffer.get(cp.pos++) << 24))
                & Integer.MAX_VALUE);
    }
    public float readFloat(Chunk cp) throws Fv3Exception {
        return Float.intBitsToFloat(this.readS32(cp));
    }
    public String readString(Chunk cp){
        StringBuilder strbuf = new StringBuilder();
        while (cp.in()){
            byte ch = this.buffer.get(cp.pos++);
            if (0 == ch)
                return strbuf.toString();
            else if (0 > ch)
                throw new Fv3Exception("Illegal value in string 0x"+Integer.toHexString(ch));
            else
                strbuf.append( (char)ch);
        }
        throw new Fv3Exception("Unterminated string.");
    }
    public void readVector(Chunk cp, float[] v){
        v[0] = this.readFloat(cp);
        v[1] = this.readFloat(cp);
        v[2] = this.readFloat(cp);
    }
    public void readColor(Chunk cp, float[] c){
        c[0] = this.readFloat(cp);
        c[1] = this.readFloat(cp);
        c[2] = this.readFloat(cp);
    }
    public float readPercentageS16(Chunk cp, float defaultValue){
        while (cp.in()){
            Chunk cp1 = this.next(cp);
            switch (cp1.id){
            case Chunk.INT_PERCENTAGE: {
                int i = this.readS16(cp1);
                return (float)(1.0 * i / 100.0);
            }
            }
        }
        return defaultValue;
    }
    public void readMaterialColor(Chunk cp, float[] rgb){
        boolean lin = false;
        while (cp.in()){
            Chunk cp1 = this.next(cp);
            switch (cp1.id){
            case Chunk.LIN_COLOR_24: {
                for (int i = 0; i < 3; ++i) {
                    rgb[i] = 1.0f * this.readU8(cp1) / 255.0f;
                }
                lin = true;
                break;
            }
            case Chunk.COLOR_24: {
                /* gamma corrected color chunk
                   replaced in 3ds R3 by LIN_COLOR_24
                 */
                if (!lin) {
                    for (int i = 0; i < 3; ++i) {
                        rgb[i] = 1.0f * this.readU8(cp1) / 255.0f;
                    }
                }
                break;
            }
            case Chunk.LIN_COLOR_F: {
                for (int i = 0; i < 3; ++i) {
                    rgb[i] = this.readFloat(cp1);
                }
                lin = true;
                break;
            }
            case Chunk.COLOR_F: {
                if (!lin) {
                    for (int i = 0; i < 3; ++i) {
                        rgb[i] = this.readFloat(cp1);
                    }
                }
                break;
            }
            }
        }
    }
    /**
     * Get a chunk for the head of the file.  User must validate that
     * the Chunk ID is 0x4D4D for a 3DS file.
     */
    public Chunk start() throws Fv3Exception {
        Chunk boot = new Chunk(this.length);
        int start = 0;
        int id = this.readU16(boot);
        int len = this.readS32(boot);
        return new Chunk(start,id,len);
    }
    /**
     * Read a chunk at the position of the argument.  The input chunk
     * pointer is forwarded to the next byte following the read and
     * returned chunk pointer -- skipping the read chunk.
     */
    public Chunk next(Chunk cp) throws Fv3Exception {
        int start = cp.pos;
        int id = this.readU16(cp);
        int len = this.readS32(cp);
        Chunk next = new Chunk(start,id,len);
        cp.pos = next.next;
        return next;
    }
    /**
     * Release the internal reference to the memory mapped file for
     * garbage collection.
     */
    public void close(){
        this.buffer = null;
    }
}
