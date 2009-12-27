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


public final class Track
    extends Object
{
    public static enum Flags {

        REPEAT(0x0001),
        SMOOTH(0x0002),
        LOCK_X(0x0008),
        LOCK_Y(0x0010),
        LOCK_Z(0x0020),
        UNLINK_X(0x0100),
        UNLINK_Y(0x0200),
        UNLINK_Z(0x0400);

        public final int flag;

        private Flags(int flag){
            this.flag = flag;
        }
    }
    public static enum Type {

        BOOL(0),
        FLOAT(1),
        VECTOR(3),
        QUAT(4);

        public final int type;

        Type(int type){
            this.type = type;
        }
    }

    public final Type type; 
    public int        flags;
    public Key[]      keys;   


    public Track(Type type){
        super();
        if (null != type)
            this.type = type;
        else
            throw new Fv3Exception();
    }

    public void read(Model model, Reader r, Chunk cp)
        throws Fv3Exception
    {
        this.flags = r.readU16(cp);
        cp.skip(8);
        int nkeys = r.readS32(cp);
        this.keys = new Key[nkeys];
        Key[] keys = this.keys;
        switch (this.type){
        case BOOL:
            for (int i = 0; i < nkeys; ++i) {
                keys[i] = new Key();
                keys[i].read(model,r,cp);
                keys[i].frame = r.readS32(cp);
            }
            break;
        case FLOAT:
            for (int i = 0; i < nkeys; ++i) {
                keys[i] = new Key();
                keys[i].read(model,r,cp);
                keys[i].value[0] = r.readFloat(cp);
            }
            break;
        case VECTOR:
            for (int i = 0; i < nkeys; ++i) {
                keys[i] = new Key();
                keys[i].read(model,r,cp);
                r.readVector(cp, keys[i].value);
            }
            break;
        case QUAT:
            for (int i = 0; i < nkeys; ++i) {
                keys[i] = new Key();
                keys[i].read(model,r,cp);
                keys[i].value[3] = r.readFloat(cp);
                r.readVector(cp, keys[i].value);
            }
            break;
        }
    }
}
