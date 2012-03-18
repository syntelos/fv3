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

import fv3.Bounds;

/**
 * 
 */
public class CircumCube
    extends Model
{

    public CircumCube(fv3.model.Object prefix, Bounds.CircumSphere s){
        super();
        this.add(prefix);
        s.glBoundary(this);
    }
    public CircumCube(fv3.model.Object[] prefix, Bounds.CircumSphere s){
        super();
        this.add(prefix);
        s.glBoundary(this);
    }
    public CircumCube(Bounds.CircumSphere s){
        super();
        s.glBoundary(this);
    }
}
