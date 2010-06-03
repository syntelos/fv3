/*
 * fv3.math
 * Copyright (c) 2009 John Pritchard, all rights reserved.
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
package fv3.math;

import java.nio.FloatBuffer;

/**
 * An RGBA color value.
 * 
 * @see Abstract
 * @author jdp
 */
public final class Color
    extends java.lang.Object
    implements fv3.math.Notation
{

    public final static Color White     = new Color( 1.0f, 1.0f, 1.0f, 1.0f);
    public final static Color LightGray = new Color( .75294117647058823529f, .75294117647058823529f, .75294117647058823529f, 1.0f);
    public final static Color Gray      = new Color( .50196078431372549019f, .50196078431372549019f, .50196078431372549019f, 1.0f);
    public final static Color DarkGray  = new Color( .25098039215686274509f, .25098039215686274509f, .25098039215686274509f, 1.0f);
    public final static Color Black     = new Color( 0.0f, 0.0f, 0.0f, 1.0f);
    public final static Color Red       = new Color( 1.0f, 0.0f, 0.0f, 1.0f);
    public final static Color Pink      = new Color( 1.0f, .68627450980392156862f, .68627450980392156862f, 1.0f);
    public final static Color Orange    = new Color( 1.0f, .78431372549019607843f, 0.0f, 1.0f);
    public final static Color Yellow    = new Color( 1.0f, 1.0f, 0.0f, 1.0f);
    public final static Color Green     = new Color( 0.0f, 1.0f, 0.0f, 1.0f);
    public final static Color Magenta   = new Color( 1.0f, 0.0f, 1.0f, 1.0f);
    public final static Color Cyan      = new Color( 0.0f, 1.0f, 1.0f, 1.0f);
    public final static Color Blue      = new Color( 0.0f, 0.0f, 1.0f, 1.0f);

    public final static float Zf(float vf){

        if (vf == vf){
            if (EPSILON > Math.abs(vf))
                return 0.0f;
            else
                return vf;
        }
        else
            throw new IllegalArgumentException(String.valueOf(vf));
    }


    private final static float[] Init = {0f,0f,0f,1f};

    public final static float[] New(){
        return Init.clone();
    }
    public final static float[] New(float v){
        return new float[]{v,v,v};
    }


    private final float[] c = Color.New();

    private volatile FloatBuffer b;


    public Color(){
        super();
    }
    public Color(float r, float g, float b, float a){
        super();
        this.c[R] = r;
        this.c[G] = g;
        this.c[B] = b;
        this.c[A] = a;
    }


    public final float r(){
        return this.c[R];
    }
    public final float rf(){
        return Zf(this.c[R]);
    }
    public final float getR(){
        return this.c[R];
    }
    public final Color r(float r){
        this.c[R] = r;
        return this;
    }
    public final Color setR(float r){
        this.c[R] = r;
        return this;
    }

    public final float g(){
        return this.c[G];
    }
    public final float gf(){
        return Zf(this.c[G]);
    }
    public final float getG(){
        return this.c[G];
    }
    public final Color g(float g){
        this.c[G] = g;
        return this;
    }
    public final Color setG(float g){
        this.c[G] = g;
        return this;
    }

    public final float b(){
        return this.c[B];
    }
    public final float bf(){
        return Zf(this.c[B]);
    }
    public final float getB(){
        return this.c[B];
    }
    public final Color b(float b){
        this.c[B] = b;
        return this;
    }
    public final Color setB(float b){
        this.c[B] = b;
        return this;
    }

    public final float a(){
        return this.c[A];
    }
    public final float af(){
        return Zf(this.c[A]);
    }
    public final float getA(){
        return this.c[A];
    }
    public final Color a(float a){
        this.c[A] = a;
        return this;
    }
    public final Color setA(float a){
        this.c[A] = a;
        return this;
    }
    public float[] array(){
        return this.c;
    }
    public FloatBuffer buffer(){
        FloatBuffer b = this.b;
        if (null == b){
            b = FloatBuffer.wrap(this.array());
            this.b = b;
        }
        return b;
    }
    public String toString(){
        float[] c = this.c;
        return String.format("%30.26f %30.26f %30.26f %30.26f", c[0], c[1], c[2], c[3]);
    }

}
