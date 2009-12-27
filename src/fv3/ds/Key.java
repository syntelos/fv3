/*
 * fv3.ds
 * Copyright (c) 2009 John Pritchard, all rights reserved.
 * Copyright (C) 1996-2008 by Jan Eric Kyprianidis, all rights reserved.
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
/*
 * Based on the work of Jan Eric Kyprianidis,  Martin van Velsen, Robin
 * Feroq, Jimm Pitts, Mats Byggmästar, and Josh DeFord.
 */
package fv3.ds;

import fv3.Fv3Exception;


public final class Key
    extends Object
{
    public static enum Flags {

        USE_TENS(0x01),
        USE_CONT(0x02),
        USE_BIAS(0x04),
        USE_EASE_TO(0x08),
        USE_EASE_FROM(0x10);

        public final int flag;

        private Flags(int flag){
            this.flag = flag;
        }
    }


    public int     frame;
    public int     flags;
    public float   tens;
    public float   cont;
    public float   bias;
    public float   easeTo;
    public float   easeFrom;
    public float[] value = {0f,0f,0f,0f};


    public Key(){
        super();
    }


    public void read(Model model, Reader r, Chunk cp)
        throws Fv3Exception
    {
        this.frame = r.readS32(cp);
        this.flags = r.readU16(cp);
        if (0 != (this.flags & Key.Flags.USE_TENS.flag)){
            this.tens = r.readFloat(cp);
        }
        if (0 != (this.flags & Key.Flags.USE_CONT.flag)){
            this.cont = r.readFloat(cp);
        }
        if (0 != (this.flags & Key.Flags.USE_BIAS.flag)){
            this.bias = r.readFloat(cp);
        }
        if (0 != (this.flags & Key.Flags.USE_EASE_TO.flag)){
            this.easeTo = r.readFloat(cp);
        }
        if (0 != (this.flags & Key.Flags.USE_EASE_FROM.flag)){
            this.easeFrom = r.readFloat(cp);
        }
    }
}
