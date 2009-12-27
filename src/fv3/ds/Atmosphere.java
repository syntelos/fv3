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


public final class Atmosphere
    extends Object
{

    public boolean     useFog;
    public double[]     fogColor = new double[3];
    public boolean     fogBackground;
    public double       fogNearPlane;
    public double       fogNearDensity;
    public double       fogFarPlane;
    public double       fogFarDensity;
    public boolean     useLayerFog;
    public int         layerFogFlags;
    public double[]     layerFogColor = new double[3];
    public double       layerFogNearY;
    public double       layerFogFarY;
    public double       layerFogDensity;
    public boolean     useDistCue;
    public boolean     distCueBackground;
    public double       distCueNearPlane;
    public double       distCueNearDimming;
    public double       distCueFarPlane;
    public double       distCueFarDimming;


    public Atmosphere(Model model, Reader r, Chunk cp)
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
        case Chunk.FOG: {
            this.fogNearPlane = r.readFloat(cp1);
            this.fogNearDensity = r.readFloat(cp1);
            this.fogFarPlane = r.readFloat(cp1);
            this.fogFarDensity = r.readFloat(cp1);
            while (cp1.in()){
                Chunk cp2 = r.next(cp1);
                switch (cp2.id) {
                case Chunk.LIN_COLOR_F:
                    r.readColor(cp2,this.fogColor);
                    break;
                case Chunk.COLOR_F:
                    break;
                case Chunk.FOG_BGND:
                    this.fogBackground = true;
                    break;
                }
            }
            break;
        }
        case Chunk.LAYER_FOG: {
            boolean lin = false;
            this.layerFogNearY = r.readFloat(cp1);
            this.layerFogFarY = r.readFloat(cp1);
            this.layerFogDensity = r.readFloat(cp1);
            this.layerFogFlags = r.readS32(cp1);
            while (cp1.in()){
                Chunk cp2 = r.next(cp1);
                switch (cp2.id) {
                case Chunk.LIN_COLOR_F:
                    r.readColor(cp2, this.layerFogColor);
                    lin = true;
                    break;
                case Chunk.COLOR_F:
                    //if (!lin)..
                    r.readColor(cp2, this.layerFogColor);
                    break;
                }
            }
            break;
        }
        case Chunk.DISTANCE_CUE: {

            this.distCueNearPlane = r.readFloat(cp1);
            this.distCueNearDimming = r.readFloat(cp1);
            this.distCueFarPlane = r.readFloat(cp1);
            this.distCueFarDimming = r.readFloat(cp1);

            while (cp1.in()) {
                Chunk cp2 = r.next(cp1);
                switch (cp2.id) {
                case Chunk.DCUE_BGND:
                    this.distCueBackground = true;
                    break;
                }
            }
            break;
        }
        case Chunk.USE_FOG:
            this.useFog = true;
            break;
        case Chunk.USE_LAYER_FOG:
            this.useLayerFog = true;
            break;
        case Chunk.USE_DISTANCE_CUE:
            this.useDistCue = true;
            break;
        }
    }
}
