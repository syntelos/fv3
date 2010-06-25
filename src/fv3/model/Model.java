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

import fv3.math.Matrix;

import javax.media.opengl.GL2;

/**
 * A single display list (see {@link fv3.nui.List}) described by a
 * list of individual GL procedure calls from this package.
 */
public class Model
    extends fv3.nui.List
    implements fv3.Model.Element
{


    protected volatile Object[] model;


    public Model(){
        super(1);
    }
    public Model(Object[] model){
        super(1);
        this.add(model);
    }


    public final boolean hasFv3Bounds(){
        return (0 != this.size());
    }
    public final boolean hasNotFv3Bounds(){
        return (0 == this.size());
    }
    public final fv3.Bounds getFv3Bounds(){
        fv3.Bounds bounds = this.bounds;
        if (null == bounds){
            bounds = new Bounds(this);
            this.bounds = bounds;
        }
        return bounds;
    }
    public final fv3.Component setFv3Bounds(){
        this.bounds = new Bounds(this);
        return this;
    }
    public final int size(){
        Object[] model = this.model;
        if (null == model)
            return 0;
        else
            return model.length;
    }
    public final fv3.model.Object get(int idx){
        return this.model[idx];
    }
    public final Model add(fv3.model.Object object){
        if (null != object)
            this.model = Object.Add(this.model,object);

        return this;
    }
    public final Model add(fv3.model.Object[] list){
        if (null != list)
            this.model = Object.Add(this.model,list);

        return this;
    }
    public Element list(int idx){
        return this;
    }
    public void define(GL2 gl){

        Object[] model = this.model;

        for (int cc = 0, count = model.length; cc < count; cc++){

            model[cc].apply(gl);
        }
    }

    public String toString(String pr, String in){

        fv3.Bounds bounds = this.bounds;
        if (bounds instanceof Bounds)

            return ((Bounds)bounds).toString(pr,in);

        else if (bounds instanceof fv3.Bounds.CircumSphere)

            return ((fv3.Bounds.CircumSphere)bounds).toString(pr,in);
        else
            return this.getClass().getName();
    }
}
