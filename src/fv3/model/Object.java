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

import javax.media.opengl.GL2;

/**
 * 
 */
public abstract class Object
    extends java.lang.Object
{
    public enum Type {
        Begin, ShadeModel, Normal3f, Vertex3f, Materialfv, End;
    }
    public final static Object[] Add(Object[] list, Object object){
        if (null == object)
            return list;
        else if (null == list)
            return new Object[]{object};
        else {
            int len = list.length;
            Object[] copier = new Object[len+1];
            System.arraycopy(list,0,copier,0,len);
            copier[len] = object;
            return copier;
        }
    }
    public final static Object[] Add(Object[] list, Object[] object){
        if (null == object)
            return list;
        else if (null == list)
            return object;
        else {
            int len1 = list.length;
            int len2 = object.length;
            Object[] copier = new Object[len1+len2];
            System.arraycopy(list,0,copier,0,len1);
            System.arraycopy(object,0,copier,len1,len2);
            return copier;
        }
    }


    public abstract Object.Type getObjectType();

    /**
     * Apply to vertex list definition
     */
    public abstract void apply(GL2 gl);

}
