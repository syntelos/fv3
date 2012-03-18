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
/*
 * Copyright (c) James P. Buzbee 1996
 * House Blend Software
 * 
 *  Permission to use, copy, modify, and distribute this software
 *  for any use is hereby granted provided
 *  this notice is kept intact within the source file
 *  This is freeware, use it as desired !
 * 
 *  Very loosly based on code with authors listed as :
 *  Alan Richardson, Pete Holzmann, James Hurt
 */
package fv3.font;

/**
 * 
 * 
 * @author John Pritchard
 */
public final class HersheyPath
    extends Object
    implements Path<HersheyFont,HersheyGlyph>
{

    protected final static int X = 0;
    protected final static int Y = 1;


    protected final int nvertices, nlines;

    private final char vertices[][];

    protected int minX, midX, maxX, minY, midY, maxY, minZ, midZ, maxZ;


    public HersheyPath(HersheyFont font, HersheyFontReader reader, int n){
        super();
        this.nvertices = n;
        this.vertices = new char[2][n];

        int nlines = 0;
        int minX = 0, maxX = 0, minY = 0, maxY = 0;
        boolean moveto = true;
        int x0 = 0, y0 = 0, x1, y1;

        for (int i = 0; i < n; i++){

            switch (i){
            case 32:
            case 68:
            case 104:
            case 140:
                /*
                 * At the end of the line
                 * 
                 * Skip the carriage return
                 */
                reader.read();
                break;
            }

            int c = reader.read();

            if (c == '\n')
                c = reader.read();


            x1 = c;
            y1 = reader.read();

            this.vertices[X][i] = (char)x1;
            this.vertices[Y][i] = (char)y1;

            if (0 < i){

                x1 = (x1 - 'R');
                y1 = ('R' - y1);

                if (' ' == x1)
                    moveto = true;
                else {

                    if (!moveto){

                        if (x0 != x1 || y0 != y1){

                            if (0 == nlines){

                                minX = Math.min(x0,x1);
                                minY = Math.min(y0,y1);
                                maxX = Math.max(x0,x1);
                                maxY = Math.max(y0,y1);
                            }
                            else {

                                minX = Math.min(minX,(Math.min(x0,x1)));
                                minY = Math.min(minY,(Math.min(y0,y1)));
                                maxX = Math.max(maxX,(Math.max(x0,x1)));
                                maxY = Math.max(maxY,(Math.max(y0,y1)));
                            }
                            nlines += 1;
                        }
                    }
                    else
                        moveto = false;

                    x0 = x1;
                    y0 = y1;
                }
            }
        }

        this.nlines = nlines;

        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;

        reader.read();
    }

    public void init(HersheyFont font, HersheyGlyph glyph, FontOptions opts){

        this.midX = (this.minX + this.maxX)>>1;
        this.midY = (this.minY + this.maxY)>>1;
        this.midZ = (this.minZ + this.maxZ)>>1;

        if (0 < this.nlines){

            final double ofsX = -font.pathSetMinX;
            final double ofsY = -font.pathSetMinY;

            final int vn = this.nvertices;

            boolean moveto = true;
            int nc = 0;
            double x0 = 0, y0 = 0, x1, y1;

            for (int vc = 1; vc < vn; vc++){
                char vx = this.vertices[X][vc];
                char vy = this.vertices[Y][vc];
                if (' ' == vx)
                    moveto = true;
                else {

                    x1 = TX(vx,opts.width)+ofsX;

                    y1 = TY(vy,opts.height)+ofsY;

                    if (!moveto){

                        if (x0 != x1 || y0 != y1){

                            glyph.setVertex(nc++,x0,y0,0.0);
                            glyph.setVertex(nc++,x1,y1,0.0);
                        }
                    }
                    else
                        moveto = false;

                    x0 = x1;
                    y0 = y1;
                }
            }
            glyph.countVertices(nc);
        }
    }
    public void destroy(){
    }

    private static double TX (double p, double mag){
        if (0.0 < mag)
            return ((p - 'R') * mag);
        else
            return (p - 'R');
    }
    private static double TY (double p, double mag){
        if (0.0 < mag)
            return (('R' - p) * mag);
        else
            return ('R' - p);
    }
}
