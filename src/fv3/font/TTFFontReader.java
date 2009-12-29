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

/**
 * 
 * @author John Pritchard
 */
public class TTFFontReader
    extends FontReader
{


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
            return this.readUint32();
        }
    }
    public double read1616() {
        int val = this.readUint32();

        double integer = (val>>16);
        double mantissa = ((val & 0xffff) / 65536.0);

        return (integer + mantissa );
    }
    public double read214() {
        int val = this.readUint16();

        double integer = ((val<<16)>>30);
        double mantissa = ((val & 0x3fff) / 16384.0);

        return (integer + mantissa );
    }

}
