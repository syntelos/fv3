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


public final class Background
    extends Object
    implements Cloneable
{

    public boolean     useBitmap;
    public String      bitmapName;
    public boolean     useSolid;
    public float[]     solidColor = new float[3];
    public boolean     useGradient;
    public float       gradientPercent;
    public float[]     gradientTop = new float[3];
    public float[]     gradientMiddle = new float[3];
    public float[]     gradientBottom = new float[3];


    public Background(Model model, Reader r, Chunk cp)
        throws Fv3Exception
    {
        super();
        this.read(model,r,cp);
    }


    public void read(Model model, Reader r, Chunk cp)
        throws Fv3Exception
    {
        Chunk cp1 = r.next(cp);
        switch (cp1.id){
        case Chunk.BIT_MAP:
            this.bitmapName = r.readString(cp1);
            break;
        case Chunk.SOLID_BGND: {
            boolean lin = false;
            while (cp1.in()) {
                Chunk cp2 = r.next(cp1);
                switch (cp2.id) {
                case Chunk.LIN_COLOR_F:
                    r.readColor(cp2,this.solidColor);
                    lin = true;
                    break;
                case Chunk.COLOR_F:
                    //if(!lin)..
                    r.readColor(cp2,this.solidColor);
                    break;
                }
            }
            break;
        }
        case Chunk.V_GRADIENT: {
            int[] index = new int[2];
            float[][][] col = new float[2][3][3];
            int lin = 0;
            this.gradientPercent = r.readFloat(cp1);
            while (cp1.in()){
                Chunk cp2 = r.next(cp1);
                switch (cp2.id){
                case Chunk.COLOR_F:
                    r.readColor(cp2, col[0][index[0]]);
                    index[0]++;
                    break;
                case Chunk.LIN_COLOR_F:
                    r.readColor(cp2, col[1][index[1]]);
                    index[1]++;
                    lin = 1;
                    break;
                }
            }
            for (int i = 0; i < 3; ++i) {
                this.gradientTop[i] = col[lin][0][i];
                this.gradientMiddle[i] = col[lin][1][i];
                this.gradientBottom[i] = col[lin][2][i];
            }
            break;
        }
        case Chunk.USE_BIT_MAP:
            this.useBitmap = true;
            break;
        case Chunk.USE_SOLID_BGND:
            this.useSolid = true;
            break;
        case Chunk.USE_V_GRADIENT:
            this.useGradient = true;
            break;
        }
    }
    public Background clone(){
        try {
            Background background = (Background)super.clone();

            return background;
        }
        catch (CloneNotSupportedException exc){
            throw new InternalError();
        }
    }
}
