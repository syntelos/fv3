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

public enum ObjectFlags {

    OBJECT_HIDDEN(0x01),
    OBJECT_VIS_LOFTER(0x02), 
    OBJECT_DOESNT_CAST(0x04), 
    OBJECT_MATTE(0x08), 
    OBJECT_DONT_RCVSHADOW(0x10), 
    OBJECT_FAST(0x20), 
    OBJECT_FROZEN(0x40);


    public final int flag;

    ObjectFlags(int flag){
        this.flag = flag;
    }


}
