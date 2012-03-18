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

import fv3.math.Color;


public final class Material
    extends fv3.model.Object
{
    public final int face, name;
    public final float[] params;


    public Material(int face, int name, Color color){
        this(face,name,color.array());
    }
    public Material(int face, int name, float[] params){
        super();
        if (0 < face && 0 < name && null != params){
            this.face = face;
            this.name = name;
            this.params = params;
        }
        else
            throw new IllegalArgumentException();
    }
}
