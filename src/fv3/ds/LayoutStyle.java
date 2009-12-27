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

public enum LayoutStyle {

    SINGLE(0),
    TWO_PANE_VERT_SPLIT(1),
    TWO_PANE_HORIZ_SPLIT(2),
    FOUR_PANE(3),
    THREE_PANE_LEFT_SPLIT(4),
    THREE_PANE_BOTTOM_SPLIT(5),
    THREE_PANE_RIGHT_SPLIT(6),
    THREE_PANE_TOP_SPLIT(7),
    THREE_PANE_VERT_SPLIT(8),
    THREE_PANE_HORIZ_SPLIT(9),
    FOUR_PANE_LEFT_SPLIT(10),
    FOUR_PANE_RIGHT_SPLIT(11);


    public final int flag;

    LayoutStyle(int flag){
        this.flag = flag;
    }


}
