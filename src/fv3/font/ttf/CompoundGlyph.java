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

    public final int flags, index, matchCompound, matchComponent;

    public final double a, b, c, d, e, f, m, n, am, cm, bn, dn;


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

        if (0 != (this.flags & WE_HAVE_A_SCALE)){
            double s = reader.read214();
            this.a = s;
            this.b = 0.0;
            this.c = 0.0;
            this.d = s;
        }
        else if (0 != (this.flags & WE_HAVE_AN_X_AND_Y_SCALE)){
            this.a = reader.read214();
            this.b = 0.0;
            this.c = 0.0;
            this.d = reader.read214();
        }
        else if (0 != (this.flags & WE_HAVE_A_TWO_BY_TWO)){
            this.a = reader.read214();
            this.b = reader.read214();
            this.c = reader.read214();
            this.d = reader.read214();
        }
        else {
            this.a = 1.0;
            this.b = 0.0;
            this.c = 0.0;
            this.d = 1.0;
        }

        if ( 0 != (this.flags & ARGS_ARE_XY_VALUES)){
            double m = Math.max(Math.abs(a),Math.abs(b));
            double n = Math.max(Math.abs(c),Math.abs(d));
            if (Math.abs(Math.abs(this.a) - Math.abs(this.c)) <= EPSILON)
                m *= 2.0;

            if (Math.abs(Math.abs(c) - Math.abs(d)) <= EPSILON)
                n *= 2.0;

            this.m = m;
            this.n = n;
            this.am = (this.a/this.m);
            this.cm = (this.c/this.m);
            this.bn = (this.b/this.n);
            this.dn = (this.d/this.n);
            this.e = arg1;
            this.f = arg2;
            this.matchCompound = -1;
            this.matchComponent = -1;
        }
        else {
            this.m = 0.0;
            this.n = 0.0;
            this.am = 0.0;
            this.cm = 0.0;
            this.bn = 0.0;
            this.dn = 0.0;
            this.e = 0.0;
            this.f = 0.0;
            this.matchCompound = arg1;
            this.matchComponent = arg2;
        }
    }


    public boolean hasMatchIndeces(){

        return (0 == (this.flags & ARGS_ARE_XY_VALUES));
    }
    public boolean hasTransform(){

        return (0 != (this.flags & ARGS_ARE_XY_VALUES));
    }
    public double[] transform(double[] dst, double[] src){
        /*
         * See freetype 'ttgload.c'.  This is the documented
         * composite glyph transform.  It's known to be broken.
         */
        for (int x = 0, y = 1, z = src.length; y < z; x += 2, y += 2){
            dst[x] = (this.m * ((this.am * src[x]) + (this.cm * src[y]) + this.e));
            dst[y] = (this.n * ((this.bn * src[x]) + (this.dn * src[y]) + this.f));
        }
        return dst;
    }
    public boolean more(){
        return (0 != (this.flags & MORE_COMPONENTS));
    }
    public String toString(){
            
        if (0 != (this.flags & ARGS_ARE_XY_VALUES)){
            return String.format("Compound( 0x%x, %d, %f, %f, %f, %f, %f, %f)",this.flags,this.index,this.a,this.b,this.c,this.d,this.e,this.f);
        }
        else {
            return String.format("Compound( 0x%x, %d, %d, %d)",this.flags,this.index,this.matchCompound,this.matchComponent);
        }
    }
}
