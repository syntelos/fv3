/*
 * fv3
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
package fv3.csg.u;

/**
 * Convert geometric parameters with maximum error into a
 * resolution parameter.
 */
public class Error
    extends java.lang.Object
    implements fv3.csg.u.Notation
{

    public final static float Default = 1e-2f;


    public final static class Circle
        extends Error
    {
        public final static float Min = 1e-6f;
        public final static float Max = 1e1f;

        /**
         * @param e Maximum sagitta
         * @return Sane value for 'e'
         */
        public final static boolean V(float e){
            return (e == e && Error.Circle.Min <= e && Error.Circle.Max >= e);
        }
        /**
         * @param r Radius of circle
         * @param n Number of arc sectors in circle
         * @return Sagitta for 'r' and 'n'
         */
        public final static float E(float r, int n){

            final float s = (r*(PI_M2 / (float)n));

            final float l = (s / 2.0f);

            return (r-(float)Math.sqrt((r*r)-(l*l)));
        }
        /**
         * Scanner for largest value of N satisfying requirement
         * E.
         * 
         * @param r Radius
         * @param e Maximum sagitta
         * @return A number of circular arc-parts best satisfying the
         * requirements
         */
        public final static int N(float r, float e){

            if (r == r && ZERO < r && V(e)){

                float qr0 = 4.0f;

                while (true){

                    int n0 = (int)(r * qr0);

                    if (E(r,n0) <= e){

                        float qr1 = (qr0 - 2.0f);

                        while (ZERO < qr1){

                            int n1 = (int)(r * qr1);

                            if (E(r,n1) > e)

                                return (int)(r * (qr1 + 2.0f));

                            else {
                                qr1 -= 1.0f;
                            }
                        }
                        return (int)r;
                    }
                    else {
                        qr0 *= 2.0f;
                    }
                }
            }
            else
                throw new IllegalArgumentException();
        }
    }
}
