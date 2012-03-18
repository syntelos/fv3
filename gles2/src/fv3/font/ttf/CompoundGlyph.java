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
package fv3.font.ttf;

import fv3.font.TTFFontReader;

import java.lang.Math;

/**
 * Compound glyph specification.
 * 
 * @author John Pritchard
 */
public final class CompoundGlyph {
    private final static int ARG_1_AND_2_ARE_WORDS    = (1);
    private final static int ARGS_ARE_XY_VALUES       = (1<<1);
    private final static int ROUND_XY_TO_GRID         = (1<<2);
    private final static int WE_HAVE_A_SCALE          = (1<<3);
    private final static int MORE_COMPONENTS          = (1<<5);
    private final static int WE_HAVE_AN_X_AND_Y_SCALE = (1<<6);
    private final static int WE_HAVE_A_TWO_BY_TWO     = (1<<7);
    private final static int WE_HAVE_INSTRUCTIONS     = (1<<8);
    private final static int USE_MY_METRICS           = (1<<9);
    private final static int OVERLAP_COMPOUND         = (1<<10);

    private final static double EPSILON = 33.0/65536.0;

    public final boolean scale, translation, match;

    public final int flags, index, matchCompound, matchComponent;

    public final double xx, xy, yy, yx, tx, ty;


    public CompoundGlyph(TTFFontReader reader){
        super();

        this.flags = reader.readUint16();
        this.index = reader.readUint16();

        int arg1, arg2;

        if (0 != (this.flags & ARG_1_AND_2_ARE_WORDS)){
            arg1 = reader.readSint16();
            arg2 = reader.readSint16();
        }
        else {
            arg1 = reader.readSint8();
            arg2 = reader.readSint8();
        }

        double tx, ty;

        if (0 != (this.flags & WE_HAVE_A_SCALE)){
            this.scale = true;
            double s = reader.read214();
            this.xx = s;
            this.xy = s;
            this.yy = s;
            this.yx = s;
            tx = s;
            ty = s;
        }
        else if (0 != (this.flags & WE_HAVE_AN_X_AND_Y_SCALE)){
            this.scale = true;
            this.xx = reader.read214();
            this.xy = 0.0;
            this.yy = reader.read214();
            this.yx = 0.0;
            tx = this.xx;
            ty = this.yy;
        }
        else if (0 != (this.flags & WE_HAVE_A_TWO_BY_TWO)){
            this.scale = true;
            this.xx = reader.read214();
            this.yx = reader.read214();
            this.xy = reader.read214();
            this.yy = reader.read214();
            tx = Math.sqrt((this.xx*this.xx)+(this.xy+this.xy));
            ty = Math.sqrt((this.yy*this.yy)+(this.yx+this.yx));
        }
        else {
            this.scale = false;
            this.xx = 0.0;
            this.xy = 0.0;
            this.yy = 0.0;
            this.yx = 0.0;
            tx = 1.0;
            ty = 1.0;
        }

        if ( 0 != (this.flags & ARGS_ARE_XY_VALUES)){
            this.match = false;
            this.translation = true;
            if (this.scale){
                this.tx = (tx * arg1);
                this.ty = (ty * arg2);
            }
            else {
                this.tx = arg1;
                this.ty = arg2;
            }
            this.matchCompound = -1;
            this.matchComponent = -1;
        }
        else {
            this.match = true;
            this.translation = false;
            this.tx = 0.0;
            this.ty = 0.0;
            this.matchCompound = arg1;
            this.matchComponent = arg2;
        }
    }


    public double[] transform(double[] dst, double[] src){
        /*
         * From freetype 'ttgload.c'.  The documented composite glyph
         * transform is known to be broken, and fontforge is hard to
         * read in this neighborhood.  Freetype is relatively clear on
         * the subject, with something known to work.
         */
        if (this.scale){
            if (this.translation){
                for (int x,y,cc = 0, count = (src.length>>1); cc < count; cc++){
                    x = (cc<<1);
                    y = (x+1);

                    dst[x] = ((this.xx * src[x]) + (this.xy * src[y]) + this.tx);
                    dst[y] = ((this.yx * src[x]) + (this.yy * src[y]) + this.ty);
                }
            }
            else {
                for (int x,y,cc = 0, count = (src.length>>1); cc < count; cc++){
                    x = (cc<<1);
                    y = (x+1);

                    dst[x] = ((this.xx * src[x]) + (this.xy * src[y]));
                    dst[y] = ((this.yx * src[x]) + (this.yy * src[y]));
                }
            }
        }
        else if (this.translation){
            for (int x,y,cc = 0, count = (src.length>>1); cc < count; cc++){
                x = (cc<<1);
                y = (x+1);

                dst[x] = (src[x] + this.tx);
                dst[y] = (src[y] + this.ty);
            }
        }
        else {
            System.arraycopy(src,0,dst,0,src.length);
        }
        return dst;
    }
    public boolean more(){
        return (0 != (this.flags & MORE_COMPONENTS));
    }
    public String toString(){
        if (this.match)
            return String.format("Compound( 0x%x, %d, %d, %d)",this.flags,this.index,this.matchCompound,this.matchComponent);
        else 
            return String.format("Compound( 0x%x, %d, %f, %f, %f, %f, %f, %f)",this.flags,this.index,this.xx,this.yx,this.xy,this.yy,this.tx,this.ty);
    }
}
