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


public final class Material
    extends Object
{
    private final static double[] DefaultAmbient  = new double[]{0.588235f,0.588235f,0.588235f};
    private final static double[] DefaultDiffuse  = new double[]{0.588235f,0.588235f,0.588235f};
    private final static double[] DefaultSpecular = new double[]{0.898039f,0.898039f,0.898039f};
    public final static double DefaultShininess = 0.1f;
    public final static double DefaultWireSize = 1.0f;
    public final static int DefaultShading = 3;

    public int           user_id;
    public Object        user_ptr;
    public String        name;                                  /* Material name */
    public double[]       ambient = DefaultAmbient.clone();      /* Material ambient reflectivity */
    public double[]       diffuse = DefaultDiffuse.clone();      /* Material diffuse reflectivity */
    public double[]       specular = DefaultSpecular.clone();    /* Material specular reflectivity */
    public double         shininess = DefaultShininess;          /* Material specular exponent */
    public double         shinStrength;
    public boolean       useBlur;
    public double         blur;
    public double         transparency;
    public double         falloff;
    public boolean       isAdditive;
    public boolean       selfIllumFlag; /* bool */
    public double         selfIllum;
    public boolean       useFalloff;
    public int           shading = DefaultShading;
    public boolean       soften;        /* bool */
    public boolean       faceMap;       /* bool */
    public boolean       twoSided;      /* Material visible from back */
    public boolean       mapDecal;      /* bool */
    public boolean       useWire;
    public boolean       useWireAbs;
    public double         wireSize = DefaultWireSize;
    public TextureMap    texture1Map = new TextureMap();
    public TextureMap    texture1Mask = new TextureMap();
    public TextureMap    texture2Map = new TextureMap();
    public TextureMap    texture2Mask = new TextureMap();
    public TextureMap    opacityMap = new TextureMap();
    public TextureMap    opacityMask = new TextureMap();
    public TextureMap    bumpMap = new TextureMap();
    public TextureMap    bumpMask = new TextureMap();
    public TextureMap    specularMap = new TextureMap();
    public TextureMap    specularMask = new TextureMap();
    public TextureMap    shininessMap = new TextureMap();
    public TextureMap    shininessMask = new TextureMap();
    public TextureMap    selfIllumMap = new TextureMap();
    public TextureMap    selfIllumMask = new TextureMap();
    public TextureMap    reflectionMap = new TextureMap();
    public TextureMap    reflectionMask = new TextureMap();
    public int           autoreflMapFlags;
    public int           autoreflMapAntiAlias;  /* 0=None, 1=Low, 2=Medium, 3=High */
    public int           autoreflMapSize;
    public int           autoreflMapFrameStep;


    public Material(Model model, Reader r, Chunk cp)
        throws Fv3Exception
    {
        super();
        this.read(model,r,cp);
    }

    public void read(Model model, Reader r, Chunk cp)
        throws Fv3Exception
    {
        while (cp.in()){
            Chunk cp1 = r.next(cp);
            switch (cp1.id){
            case Chunk.MAT_NAME:
                this.name = r.readString(cp1);
                break;
            case Chunk.MAT_AMBIENT:
                r.readMaterialColor(cp1,this.ambient);
                break;
            case Chunk.MAT_DIFFUSE:
                r.readMaterialColor(cp1,this.diffuse);
                break;
            case Chunk.MAT_SPECULAR:
                r.readMaterialColor(cp1,this.specular);
                break;
            case Chunk.MAT_SHININESS:
                this.shininess = r.readPercentageS16(cp1,this.shininess);
                break;
            case Chunk.MAT_SHIN2PCT:
                this.shinStrength = r.readPercentageS16(cp1,this.shinStrength);
                break;
            case Chunk.MAT_TRANSPARENCY:
                this.transparency = r.readPercentageS16(cp1,this.transparency);
                break;
            case Chunk.MAT_XPFALL:
                this.falloff = r.readPercentageS16(cp1,this.falloff);
                break;
            case Chunk.MAT_SELF_ILPCT:
                this.selfIllum = r.readPercentageS16(cp1,this.selfIllum);
                break;
            case Chunk.MAT_USE_XPFALL:
                this.useFalloff = true;
                break;
            case Chunk.MAT_REFBLUR:
                this.blur = r.readPercentageS16(cp1,this.blur);
                break;
            case Chunk.MAT_USE_REFBLUR:
                this.useBlur = true;
                break;
            case Chunk.MAT_SHADING:
                this.shading = r.readS16(cp1);
                break;
            case Chunk.MAT_SELF_ILLUM:
                this.selfIllumFlag = true;
                break;
            case Chunk.MAT_TWO_SIDE:
                this.twoSided = true;
                break;
            case Chunk.MAT_DECAL:
                this.mapDecal = true;
                break;
            case Chunk.MAT_ADDITIVE:
                this.isAdditive = true;
                break;
            case Chunk.MAT_FACEMAP:
                this.faceMap = true;
                break;
            case Chunk.MAT_PHONGSOFT:
                this.soften = true;
                break;
            case Chunk.MAT_WIRE:
                this.useWire = true;
                break;
            case Chunk.MAT_WIREABS:
                this.useWireAbs = true;
                break;
            case Chunk.MAT_WIRE_SIZE:
                this.wireSize = r.readFloat(cp1);
                break;
            case Chunk.MAT_TEXMAP:
                this.texture1Map.read(model,r,cp1);
                break;
            case Chunk.MAT_TEXMASK:
                this.texture1Mask.read(model,r,cp1);
                break;
            case Chunk.MAT_TEX2MAP:
                this.texture2Map.read(model,r,cp1);
                break;
            case Chunk.MAT_TEX2MASK:
                this.texture2Mask.read(model,r,cp1);
                break;
            case Chunk.MAT_OPACMAP:
                this.opacityMap.read(model,r,cp1);
                break;
            case Chunk.MAT_OPACMASK:
                this.opacityMask.read(model,r,cp1);
                break;
            case Chunk.MAT_BUMPMAP:
                this.bumpMap.read(model,r,cp1);
                break;
            case Chunk.MAT_BUMPMASK:
                this.bumpMask.read(model,r,cp1);
                break;
            case Chunk.MAT_SPECMAP:
                this.specularMap.read(model,r,cp1);
                break;
            case Chunk.MAT_SPECMASK:
                this.specularMask.read(model,r,cp1);
                break;
            case Chunk.MAT_SHINMAP:
                this.shininessMap.read(model,r,cp1);
                break;
            case Chunk.MAT_SHINMASK:
                this.shininessMask.read(model,r,cp1);
                break;
            case Chunk.MAT_SELFIMAP:
                this.selfIllumMap.read(model,r,cp1);
                break;
            case Chunk.MAT_SELFIMASK:
                this.selfIllumMask.read(model,r,cp1);
                break;
            case Chunk.MAT_REFLMAP:
                this.reflectionMap.read(model,r,cp1);
                break;
            case Chunk.MAT_REFLMASK:
                this.reflectionMask.read(model,r,cp1);
                break;
            case Chunk.MAT_ACUBIC:
                cp1.skip(1);
                this.autoreflMapAntiAlias = r.readS8(cp1);
                this.autoreflMapFlags = r.readS16(cp1);
                this.autoreflMapSize = r.readS32(cp1);
                this.autoreflMapFrameStep = r.readS32(cp1);
                break;
            }
        }
    }
}
