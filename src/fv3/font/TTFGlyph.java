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

import fv3.font.ttf.Cmap;
import fv3.font.ttf.Glyf;
import fv3.font.ttf.Head;
import fv3.font.ttf.TTF;

/**
 * This class may be subclassed with a {@link Font} subclass as for
 * implementing a text editor.
 * 
 * @author John Pritchard
 */
public class TTFGlyph
    extends Glyph<TTFFont,TTFPath>
{
    private final static int ON_CURVE = 0x01;
    private final static int X_SHORT  = 0x02;
    private final static int Y_SHORT  = 0x04;
    private final static int REPEAT   = 0x08;
    private final static int X_SAME   = 0x10;
    private final static int Y_SAME   = 0x20;

    /**
     * Compound glyph specification.
     */
    public final static class Compound {
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


        Compound(TTFFontReader reader){
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
            for (int x = 0, y = 1, z = src.length; y < z; x += 2, y += 2){
                dst[x] = (this.m * ((this.am * src[x]) + (this.cm * src[y]) + this.e));
                dst[y] = (this.n * ((this.bn * src[x]) + (this.dn * src[y]) + this.f));
            }
            return dst;
        }
        boolean more(){
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

    private final static int Start = 0, Control = 1, End = 2;

    /**
     * TTF internal coordinates and dimensions
     */
    public final int index, offset, length, exclusive;

    /**
     * Glyph bounding box
     */
    public double minX, maxX, minY, maxY;

    public char character;

    public Compound[] compound;


    protected TTFGlyph(TTFFont font, Glyf glyf, int index, int offset, int next){
        super(font);
        this.index = index;
        this.offset = (glyf.offset + offset);
        this.length = (next-offset);
        this.exclusive = (this.offset + this.length);
    }


    public final boolean isSimple(){
        return (null == this.compound);
    }
    public final boolean isCompound(){
        return (null != this.compound);
    }
    public void read(TTFFontReader reader){
        if (0 == this.length)
            return;
        else {
            reader.seek(this.offset);

            int nContours = reader.readSint16();
            this.minX = reader.readSint16();
            this.minY = reader.readSint16();
            this.maxX = reader.readSint16();
            this.maxY = reader.readSint16();

            if (0 < nContours){


                int[] contourIndex = new int[nContours];
                {
                    for (int cc = 0; cc < nContours; cc++){
                        contourIndex[cc] = reader.readUint16();
                        if (0 != cc && contourIndex[cc] < contourIndex[cc-1] ){
                            throw new IllegalStateException(String.format("In Glyph %d(%c): Path indeces error (%d < %d)", this.index, contourIndex[cc-1], contourIndex[cc]));
                        }
                    }
                }
                int nPoints = (contourIndex[nContours-1])+1;


                reader.skip(reader.readUint16());
                //             int hintCount = reader.readUint16();
                //             int[] hints = new int[hintCount];
                //             {
                //                 for (int cc = 0; cc < hintCount; cc++)
                //                     hints[cc] = reader.readUint8();
                //             }

                int[] flags = new int[nPoints];
                {
                    for (int cc = 0; cc < nPoints; cc++){
                        flags[cc] = reader.readUint8();
                        if (0 != (flags[cc] & REPEAT)){
                            int dz = reader.readUint8();
                            if ( (cc + dz) > nPoints){
                                String erm = String.format("In Glyph %d(%c): Flag count is wrong (or total is): %d %d\n", this.index, (cc+dz), nPoints );
                                throw new IllegalStateException(erm);
                            }
                            else {
                                for (int dd = 0; dd < dz; dd++){
                                    flags[cc+dd+1] = flags[cc];
                                }
                                cc += dz;
                            }
                        }
                    }
                }
                double points[] = new double[(nPoints<<1)];
                {
                    double last = 0.0;
                    int x, y;
                    for (int cc = 0; cc < nPoints; cc++){
                        x = (cc<<1);
                        if (0 != (flags[cc] & X_SHORT)){
                            int off = reader.readUint8();
                            if (0 == (flags[cc] & X_SAME))
                                off = -off;

                            points[x] = (last + off);
                        }
                        else if (0 != (flags[cc] & X_SAME)){
                            points[x] = last;
                        }
                        else
                            points[x] = (last + reader.readSint16());

                        last = points[x];
                    }
                    last = 0.0;

                    for (int cc = 0; cc < nPoints; cc++){
                        y = (cc<<1)+1;
                        if (0 != (flags[cc] & Y_SHORT)){
                            int off = reader.readUint8();
                            if (0 == (flags[cc] & Y_SAME))
                                off = -off;

                            points[y] = (last + off);
                        }
                        else if (0 != (flags[cc] & Y_SAME)){
                            points[y] = last;
                        }
                        else
                            points[y] = (last + reader.readSint16());

                        last = points[y];
                    }
                }
                {
                    int Path = Start;
                    int cc = 0;
                    if (cc < nPoints){
                        int contour = 0;
                        int contourEnd = contourIndex[contour];

                        if (0 != (flags[cc] & ON_CURVE)){
                            int x = (cc<<1);
                            int y = (x+1);
                            double startX = points[x];
                            double startY = points[y];
                            double controlX = 0.0, controlY = 0.0;
                            double controlX2 = 0.0, controlY2 = 0.0;
                            double endX = 0.0, endY = 0.0;

                            for (cc++; cc < nPoints; cc++){

                                if (cc > contourEnd){
                                    contour += 1;
                                    contourEnd = contourIndex[contour];
                                }
                                x = (cc<<1);
                                y = (x+1);
                                if (0 != (flags[cc] & ON_CURVE)){
                                    switch (Path){
                                    case Start:
                                        endX = points[x];
                                        endY = points[y];

                                        this.add(new TTFPath(contour, this.getLength(),
                                                             startX, startY, endX, endY));

                                        Path = Start;

                                        startX = endX;
                                        startY = endY;
                                        controlX = 0.0;
                                        controlY = 0.0;
                                        controlX2 = 0.0;
                                        controlY2 = 0.0;
                                        endX = 0.0;
                                        endY = 0.0;
                                        break;
                                    case Control:
                                        endX = points[x];
                                        endY = points[y];

                                        this.add(new TTFPath(contour, this.getLength(), true,
                                                             startX, startY, controlX, controlY, endX, endY));

                                        Path = Start;

                                        startX = endX;
                                        startY = endY;
                                        controlX = 0.0;
                                        controlY = 0.0;
                                        controlX2 = 0.0;
                                        controlY2 = 0.0;
                                        endX = 0.0;
                                        endY = 0.0;
                                        break;

                                    default:
                                        throw new IllegalStateException(String.format("Glyph: %d(%c); Path: 0x%x; Point: (%d < %d); Flags: '%s'; X: %f, Y: %f.",this.index,this.character,Path,cc,nPoints,FontReader.Bitstring(flags[cc],8),points[x],points[y]));
                                    }
                                }
                                else {
                                    switch (Path){
                                    case Start:
                                        controlX = points[x];
                                        controlY = points[y];

                                        Path = Control;

                                        break;
                                    case Control:
                                        /*
                                         * The famous TTF point injection
                                         */
                                        controlX2 = points[x];
                                        controlY2 = points[y];
                                        /*
                                         * The approach taken is from
                                         * fontforge.  It seems an
                                         * effective punt as it needs
                                         * to work for a series of
                                         * control points -- and where
                                         * I've seen this
                                         * (e.g. NEUROPOL '?') I've
                                         * seen shallow curves.
                                         * 
                                         * Therefore I've marked them
                                         * "synthetic", and the next
                                         * pass (path init2) will be a
                                         * better place to sort this
                                         * out.
                                         */
                                        endX = (controlX2 - controlX)/2.0;
                                        endY = (controlY2 - controlY)/2.0; 
                                        //System.err.printf("Synthesizing point on path in Glyph: %d(%c); Path: 0x%x; Point: (%d < %d); StartX: %f, StartY: %f; ControlX: %f, ControlY: %f; EndX: %f, EndY: %f.\n",this.index,this.character,Path,cc,nPoints,startX,startY,controlX,controlY,endX,endY);
                                        /*
                                         */
                                        this.add(new TTFPath(contour, this.getLength(), true,
                                                             startX, startY, controlX, controlY, endX, endY));

                                        Path = Control;

                                        startX = endX;
                                        startY = endY;
                                        controlX = controlX2;
                                        controlY = controlY2;
                                        controlX2 = 0.0;
                                        controlY2 = 0.0;
                                        endX = 0.0;
                                        endY = 0.0;
                                        break;

                                    default:
                                        throw new IllegalStateException(String.format("Glyph: %d(%c); Path: 0x%x; Point: (%d < %d); Flags: '%s'; X: %f, Y: %f.",this.index,this.character,Path,cc,nPoints,FontReader.Bitstring(flags[cc],8),points[x],points[y]));
                                    }
                                }
                            }
                        }
                        else
                            throw new IllegalStateException(String.format("Glyph %d(%c) Starts Off Path?",this.index, this.character));
                    }
                }
            }
            else if (0 == nContours){
                throw new UnsupportedOperationException("[TODO] Control point.");
            }
            else if (-1 == nContours){
                int index = 0;
                this.compound = new Compound[]{new Compound(reader)};
                while (this.compound[index].more()){
                    Compound add = new Compound(reader);
                    index += 1;
                    Compound[] copier = new Compound[index+1];
                    System.arraycopy(this.compound,0,copier,0,index);
                    copier[index] = add;
                    this.compound = copier;
                }
            }
            else
                throw new UnsupportedOperationException(String.format("Unrecognized contour indicator (%d).",nContours));
        }
    }

    public String toString(String infix){
        infix += "                     ";
        String prefix = ("TTFGlyph( "+this.index+", '"+CharacterToString(this.character)+"', ");

        if (null != this.compound){
            StringBuilder string = new StringBuilder();
            string.append(prefix);
            Compound[] compound = this.compound;
            for (int cc = 0, count = compound.length; cc < count; cc++){
                if (0 != cc)
                    string.append(infix);
                string.append(compound[cc].toString());
            }
            string.append(")");
            return string.toString();
        }
        else
            return super.toString(prefix,infix,")");
    }

    public final static String CharacterToString(char ch){
        if (' ' <= ch && '~' >= ch)
            return String.valueOf(ch);
        else
            return "0x"+Integer.toHexString(ch);
    }
}
