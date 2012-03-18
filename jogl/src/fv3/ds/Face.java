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

public final class Face
    extends Object
{
    public final static int vis_ac = 0x01;
    public final static int vis_bc = 0x02;
    public final static int vis_ab = 0x04;
    public final static int wrap_u = 0x08;
    public final static int wrap_v = 0x10;
    public final static int select_3 = (1<<13);
    public final static int select_2 = (1<<14);
    public final static int select_1 = (1<<15);

    public static enum Flags {

        VIS_AC(vis_ac),       /**< Bit 0: Edge visibility AC */
        VIS_BC(vis_bc),       /**< Bit 1: Edge visibility BC */
        VIS_AB(vis_ab),       /**< Bit 2: Edge visibility AB */
        WRAP_U(wrap_u),       /**< Bit 3: Face is at tex U wrap seam */
        WRAP_V(wrap_v),       /**< Bit 4: Face is at tex V wrap seam */
        SELECT_3(select_3),    /**< Bit 13: Selection of the face in selection 3*/
        SELECT_2(select_2),    /**< Bit 14: Selection of the face in selection 2*/
        SELECT_1(select_1);    /**< Bit 15: Selection of the face in selection 1*/


        public final int flag;

        private Flags(int flag){
            this.flag = flag;
        }

        public final static Flags[] In(int flags){
            Flags[] re = null;
            for (int sh = 0; sh < 16; sh++){
                Flags ef = For( (flags & (1<<sh)));
                if (null != ef){
                    if (null == re)
                        re = new Flags[]{ef};
                    else {
                        int len = re.length;
                        Flags[] copier = new Flags[len+1];
                        System.arraycopy(re,0,copier,0,len);
                        copier[len] = ef;
                        re = copier;
                    }
                }
                if (4 == sh)
                    sh = 12;
            }
            return re;
        }
        public final static Flags For(int flags){
            switch(flags){
            case vis_ac:
                return Flags.VIS_AC;
            case vis_bc:
                return Flags.VIS_BC;
            case vis_ab:
                return Flags.VIS_AB;
            case wrap_u:
                return Flags.WRAP_U;
            case wrap_v:
                return Flags.WRAP_V;
            case select_3:
                return Flags.SELECT_3;
            case select_2:
                return Flags.SELECT_2;
            case select_1:
                return Flags.SELECT_1;
            default:
                return null;
            }
        }
    }
    public final static Face[] New(int z){
        Face[] c = new Face[z];
        for (int cc = 0; cc < z; cc++){
            c[cc] = new Face();
        }
        return c;
    }


    public int[] index = {0,0,0};
    public int   flags;
    public int   material = -1;
    public int   smoothing_group;


    public Face(){
        super();
    }


}
