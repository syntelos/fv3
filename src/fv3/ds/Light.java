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

public final class Light
    extends Object
{

    public int         user_id;
    public Object      user_ptr;
    public String      name;
    public int         object_flags; 
    public boolean     spot_light;     /* bool */
    public boolean     see_cone;
    public double[]     color = new double[3];
    public double[]     position = new double[3];
    public double[]     target = new double[3];
    public double       roll;
    public boolean     off;              /* bool */
    public double       outer_range;
    public double       inner_range;
    public double       multiplier;
    /*const char**  excludes;*/
    public double       attenuation;
    public boolean     rectangular_spot;   /* bool */
    public boolean     shadowed;           /* bool */
    public double       shadow_bias;
    public double       shadow_filter;
    public int         shadow_size;
    public double       spot_aspect;
    public boolean     use_projector;
    public String      projector;
    public boolean     spot_overshoot;      /* bool */
    public boolean     ray_shadows;         /* bool */
    public double       ray_bias;
    public double       hotspot;
    public double       falloff;


    public Light(){
        super();
    }


}
