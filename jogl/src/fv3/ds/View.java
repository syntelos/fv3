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

public final class View
    extends Object
{

    public static enum Type {

        NOT_USED(0),
        TOP(1),
        BOTTOM(2),
        LEFT(3),
        RIGHT(4),
        FRONT(5),
        BACK(6),
        USER(7),
        SPOTLIGHT(18),
        CAMERA(65535);

        public final int type;

        private Type(int type){
            this.type = type;
        }
    }

    public final static View[] Add(View[] list, View item){
        if (null != item){
            if (null == list)
                return new View[]{item};
            else {
                int len = list.length;
                View[] copier = new View[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = item;
                return copier;
            }
        }
        else
            return list;
    }

    public int         type;
    public int         axisLock;
    public short[]     position = {0,0};
    public short[]     size = {0,0};
    public double       zoom;
    public double[]     center = new double[3];
    public double       horizAngle;
    public double       vertAngle;
    public String      camera;


    public View(){
        super();
    }


}
