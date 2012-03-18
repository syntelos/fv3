/*
 * Fv3
 * Copyright (C) 2009  John Pritchard, jdp@syntelos.org
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fv3.model;

/**
 * Members of this class do not define a complete display list, but
 * contribute elements to a display list.
 */
public abstract class Object
    extends java.lang.Object
    implements fv3.Model.Element
{
    public final static fv3.Model.Element[] Add(fv3.Model.Element[] list, fv3.Model.Element object){
        if (null == object)
            return list;
        else if (null == list)
            return new fv3.Model.Element[]{object};
        else {
            int len = list.length;
            fv3.Model.Element[] copier = new fv3.Model.Element[len+1];
            System.arraycopy(list,0,copier,0,len);
            copier[len] = object;
            return copier;
        }
    }
    public final static fv3.Model.Element[] Add(fv3.Model.Element[] list, fv3.Model.Element[] object){
        if (null == object)
            return list;
        else if (null == list)
            return object;
        else {
            int len1 = list.length;
            int len2 = object.length;
            fv3.Model.Element[] copier = new fv3.Model.Element[len1+len2];
            System.arraycopy(list,0,copier,0,len1);
            System.arraycopy(object,0,copier,len1,len2);
            return copier;
        }
    }
    /**
     * @param list List of values
     * @param sublist List of values
     * @return Sorted unique list of values from list and sublist.  If
     * one of list or sublist is null, the other is returned as the
     * sorted unique list.
     */
    public final static int[] Add(int[] list, int[] sublist){
        if (null == sublist)
            return list;
        else if (null == list)
            return sublist;
        else {
            int len = list.length;
            int slen = sublist.length;
            int[] copier = new int[len+slen];
            System.arraycopy(list,0,copier,0,len);
            System.arraycopy(sublist,0,copier,len,slen);

            java.util.Arrays.sort(copier);

            for (int a = 0, b = 1; b < copier.length; a++,b++){

                if (copier[a] == copier[b]){

                    copier = Remove(copier,a);
                }
            }

            return copier;
        }
    }
    public final static int[] Remove(int[] list, int index){
        if (0 > index || null == list)
            return list;
        else {
            int len = list.length;
            int term = (len-1);
            if (0 == term)
                return null;
            else {
                int[] copy = new int[term];

                if (0 == index){
                    System.arraycopy(list,1,copy,0,term);
                }
                else if (term == index){
                    System.arraycopy(list,0,copy,0,term);
                }
                else {
                    System.arraycopy(list,0,copy,0,index);
                    System.arraycopy(list,(index+1),copy,index,(term-index));
                }
                return copy;
            }
        }
    }



    public boolean needsRedefine(){
        return false;
    }
}
