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


public final class Shadow
    extends Object
{

    public short   map_size;           /* Global shadow map size that ranges from 10 to 4096 */
    public double   low_bias;           /* Global shadow low bias */
    public double   hi_bias;            /* Global shadow hi bias */
    public double   filter;             /* Global shadow filter that ranges from 1 (lowest) to 10 (highest) */
    public double   ray_bias;           /* Global raytraced shadow bias */


    public Shadow(Model model, Reader r, Chunk cp)
        throws Fv3Exception
    {
        super();
    }


}
