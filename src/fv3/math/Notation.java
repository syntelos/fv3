/*
 * fv3.math
 * Copyright (C) 2010, John Pritchard, all rights reserved.
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package fv3.math;

/**
 * Array notation for {@link Vector} and {@link Matrix}
 * 
 */
public interface Notation
{
    /**
     * Common Math constants
     */
    public final static double EPSILON = (1e-8);
    public final static double EPS = EPSILON;
    public final static double EPS2 = (EPSILON*2.0);

    public final static double PI = Math.PI;
    public final static double PI2 = (Math.PI/2.0);

    /**
     * Vector array notation
     */
    public final static int X = 0;
    public final static int Y = 1;
    public final static int Z = 2;

    /**
     * Matrix array notation
     */
    public final static int M00 =  0;
    public final static int M01 =  4;
    public final static int M02 =  8;
    public final static int M03 =  12;
    public final static int M10 =  1;
    public final static int M11 =  5;
    public final static int M12 =  9;
    public final static int M13 =  13;
    public final static int M20 =  2;
    public final static int M21 =  6;
    public final static int M22 =  10;
    public final static int M23 =  14;
    public final static int M30 =  3;
    public final static int M31 =  7;
    public final static int M32 =  11;
    public final static int M33 =  15;

    /**
     * Color array notation
     */

    public final static int R = 0;
    public final static int G = 1;
    public final static int B = 2;
    public final static int A = 3;


}
