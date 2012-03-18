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


public final class TextureMap
    extends Object
{
    public enum Flags {

        DECALE(0x0001),
        MIRROR(0x0002),
        NEGATE(0x0008),
        NO_TILE(0x0010),
        SUMMED_AREA(0x0020),
        ALPHA_SOURCE(0x0040),
        TINT(0x0080),
        IGNORE_ALPHA(0x0100),
        RGB_TINT(0x0200);

        public final int flag;

        private Flags(int flag){
            this.flag = flag;
        }
    }


    public int         user_id;
    public Object      user_ptr;
    public String      name;
    public int         flags = 0x10;
    public double       percent = 1.0f;
    public double       blur;
    public double[]     scale = {1.0f,1.0f};
    public double[]     offset = {0f,0f};
    public double       rotation;
    public double[]     tint_1 = {0f,0f,0f};
    public double[]     tint_2 = {0f,0f,0f};
    public double[]     tint_r = {0f,0f,0f};
    public double[]     tint_g = {0f,0f,0f};
    public double[]     tint_b = {0f,0f,0f};


    public TextureMap(){
        super();
    }


    public void read(Model model, Reader r, Chunk cp)
        throws Fv3Exception
    {
        while (cp.in()){
            Chunk cp1 = r.next(cp);
            switch(cp1.id){
            case Chunk.INT_PERCENTAGE:
                this.percent = 1.0f * r.readS16(cp1) / 100.0f;
                break;
            case Chunk.MAT_MAPNAME:
                this.name = r.readString(cp1);
                break;
            case Chunk.MAT_MAP_TILING:
                this.flags = r.readU16(cp1);
                break;
            case Chunk.MAT_MAP_TEXBLUR: 
                this.blur = r.readFloat(cp1);
                break;
            case Chunk.MAT_MAP_USCALE:
                this.scale[0] = r.readFloat(cp1);
                break;
            case Chunk.MAT_MAP_VSCALE:
                this.scale[1] = r.readFloat(cp1);
                break;
            case Chunk.MAT_MAP_UOFFSET:
                this.offset[0] = r.readFloat(cp1);
                break;
            case Chunk.MAT_MAP_VOFFSET:
                this.offset[1] = r.readFloat(cp1);
                break;
            case Chunk.MAT_MAP_ANG:
                this.rotation = r.readFloat(cp1);
                break;
            case Chunk.MAT_MAP_COL1:
                this.tint_1[0] = 1.0f * r.readU8(cp1) / 255.0f;
                this.tint_1[1] = 1.0f * r.readU8(cp1) / 255.0f;
                this.tint_1[2] = 1.0f * r.readU8(cp1) / 255.0f;
                break;
            case Chunk.MAT_MAP_COL2:
                this.tint_2[0] = 1.0f * r.readU8(cp1) / 255.0f;
                this.tint_2[1] = 1.0f * r.readU8(cp1) / 255.0f;
                this.tint_2[2] = 1.0f * r.readU8(cp1) / 255.0f;
                break;
            case Chunk.MAT_MAP_RCOL:
                this.tint_r[0] = 1.0f * r.readU8(cp1) / 255.0f;
                this.tint_r[1] = 1.0f * r.readU8(cp1) / 255.0f;
                this.tint_r[2] = 1.0f * r.readU8(cp1) / 255.0f;
                break;
            case Chunk.MAT_MAP_GCOL:
                this.tint_g[0] = 1.0f * r.readU8(cp1) / 255.0f;
                this.tint_g[1] = 1.0f * r.readU8(cp1) / 255.0f;
                this.tint_g[2] = 1.0f * r.readU8(cp1) / 255.0f;
                break;
            case Chunk.MAT_MAP_BCOL:
                this.tint_b[0] = 1.0f * r.readU8(cp1) / 255.0f;
                this.tint_b[1] = 1.0f * r.readU8(cp1) / 255.0f;
                this.tint_b[2] = 1.0f * r.readU8(cp1) / 255.0f;
                break;
            }
        }
    }
}
