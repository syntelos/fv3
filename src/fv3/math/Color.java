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

/**
 * An RGBA color value.
 * 
 * @see Abstract
 * @author jdp
 */
public final class Color
    extends Abstract
{

    public final static int R = 0;
    public final static int G = 1;
    public final static int B = 2;
    public final static int A = 3;

    private final static double[] New = {0f,0f,0f,1f};

    public final static double[] New(){
        return New.clone();
    }
    public final static double[] New(double v){
        return new double[]{v,v,v};
    }


    private final double[] c = New.clone();


    public Color(){
        super();
    }
    public Color(double r, double g, double b, double a){
        super();
        this.c[R] = r;
        this.c[G] = g;
        this.c[B] = b;
        this.c[A] = a;
    }


    public final double r(){
        return this.c[R];
    }
    public final double getR(){
        return this.c[R];
    }
    public final Color r(double r){
        this.c[R] = r;
        return this;
    }
    public final Color setR(double r){
        this.c[R] = r;
        return this;
    }

    public final double g(){
        return this.c[G];
    }
    public final double getG(){
        return this.c[G];
    }
    public final Color g(double g){
        this.c[G] = g;
        return this;
    }
    public final Color setG(double g){
        this.c[G] = g;
        return this;
    }

    public final double b(){
        return this.c[B];
    }
    public final double getB(){
        return this.c[B];
    }
    public final Color b(double b){
        this.c[B] = b;
        return this;
    }
    public final Color setB(double b){
        this.c[B] = b;
        return this;
    }

    public final double a(){
        return this.c[A];
    }
    public final double getA(){
        return this.c[A];
    }
    public final Color a(double a){
        this.c[A] = a;
        return this;
    }
    public final Color setA(double a){
        this.c[A] = a;
        return this;
    }


    public double[] array(){
        return this.c;
    }

    public String toString(){
        double[] c = this.c;
        return String.format("%30.26f %30.26f %30.26f %30.26f", c[0], c[1], c[2], c[3]);
    }

}
