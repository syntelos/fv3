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
import fv3.font.ttf.CompoundGlyph;
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

    private final static int Start = 0, Control = 1, End = 2;

    /**
     * TTF internal coordinates and dimensions
     */
    public final int index, offset, length, exclusive;

    private int nContours, nPoints;

    /**
     * Glyph bounding box
     */
    public double minX, maxX, minY, maxY;

    public char character;

    public CompoundGlyph[] compound;


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
            {
                this.nContours = nContours;

                this.minX = reader.readSint16();
                this.minY = reader.readSint16();
                this.maxX = reader.readSint16();
                this.maxY = reader.readSint16();
            }
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
                {
                    this.nPoints = nPoints;
                }

                ///
                reader.skip(reader.readUint16());
                //             int hintCount = reader.readUint16();
                //             int[] hints = new int[hintCount];
                //             {
                //                 for (int cc = 0; cc < hintCount; cc++)
                //                     hints[cc] = reader.readUint8();
                //             }
                ///

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
                                         * The approach taken is from fontforge.  It is
                                         * incorrect, according to the spec.
                                         * 
                                         * The solution here needs to work for any series of Off
                                         * Curve (Control) points.
                                         * 
                                         * I've marked these paths "synthetic".  There's lots
                                         * of them.
                                         * 
                                         * There's two possibilities for this strategy.  One is
                                         * that after reviewing a large number of fonts in
                                         * the wild, George found only shallow paths at these
                                         * places.  (FontForge incorporates a substantial
                                         * number of case studies). Another possibility might
                                         * be that this strategy minimizes it's own impact
                                         * on the path from the creation of On Curve
                                         * points.  And then another combines both.
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
                this.compound = new CompoundGlyph[]{new CompoundGlyph(reader)};
                while (this.compound[index].more()){
                    CompoundGlyph add = new CompoundGlyph(reader);
                    index += 1;
                    CompoundGlyph[] copier = new CompoundGlyph[index+1];
                    System.arraycopy(this.compound,0,copier,0,index);
                    copier[index] = add;
                    this.compound = copier;
                }
            }
            else
                throw new UnsupportedOperationException(String.format("Unrecognized contour indicator (%d).",nContours));
        }
    }

    public String toString(){

        return String.format("TTFGlyph( %4d, %6s, %3d, %3d)",this.index,CharacterToString(this.character,'\''),this.nPoints,this.nContours);
    }

    public String toString(String infix){
        infix += "            ";
        String prefix = ("TTFGlyph( "+this.index+", '"+CharacterToString(this.character,'\'')+"', ");

        if (null != this.compound){
            StringBuilder string = new StringBuilder();
            string.append(prefix);
            CompoundGlyph[] compound = this.compound;
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

    public final static String CharacterToString(char ch, char quote){
        if (' ' <= ch && '~' >= ch){
            StringBuilder string = new StringBuilder();
            string.append(quote);
            if (quote == ch)
                string.append('\\');

            string.append(ch);
            string.append(quote);
            return string.toString();
        }
        else
            return "0x"+Integer.toHexString(ch).toUpperCase();
    }
}
