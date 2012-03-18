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
 * This class implements one display list, exclusively.
 * 
 * @see fv3.model.Object
 */
public class Model
    extends fv3.nui.List
    implements fv3.Model.Element,
               java.lang.Iterable<fv3.Model.Element>
{


    protected volatile Element[] model;

    protected volatile int[] ables;


    public Model(){
        super(1);
    }
    public Model(Element[] model){
        super(1);
        this.add(model);
    }


    public boolean needsRedefine(){
        return false;
    }
    public int[] ables(){

        return this.ables;
    }
    public void define(GL2 gl){

        this.ables = null;

        Element[] model = this.model;

        if (null != model){

            for (int cc = 0, count = model.length; cc < count; cc++){

                Element el = model[cc];

                int[] ables = el.ables();
                {
                    if (null != ables){
                        for (int ac = 0, az = ables.length; ac < az; ac++)
                            gl.glEnableClientState(ables[ac]);

                        this.ables = fv3.model.Object.Add(this.ables,ables);
                    }
                }

                el.define(gl);

                {
                    if (null != ables){
                        for (int ac = 0, az = ables.length; ac < az; ac++)
                            gl.glDisableClientState(ables[ac]);
                    }
                }
            }
        }
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
        Element[] model = this.model;
        if (null == model)
            return 0;
        else
            return model.length;
    }
    public final fv3.Model.Element get(int idx){
        return this.model[idx];
    }
    public final Model add(fv3.Model.Element object){
        if (null != object)
            this.model = fv3.model.Object.Add(this.model,object);

        return this;
    }
    public final Model add(fv3.Model.Element[] list){
        if (null != list)
            this.model = fv3.model.Object.Add(this.model,list);

        return this;
    }
    public Element list(int idx){
        return this;
    }
    public final java.util.Iterator<fv3.Model.Element> iterator(){

        return new fv3.Model.Element.Iterator(this.model);
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
    public Model glBoundary(fv3.Bounds bounds){
        final double minX = bounds.getBoundsMinX();
        final double maxX = bounds.getBoundsMaxX();
        final double minY = bounds.getBoundsMinY();
        final double maxY = bounds.getBoundsMaxY();
        final double minZ = bounds.getBoundsMinZ();
        final double maxZ = bounds.getBoundsMaxZ();

        this.add(new Begin(GL2.GL_LINES));
        /*
         * Boundary X
         */
        this.add(new Vertex( minX, maxY, minZ)); //(Xa)
        this.add(new Vertex( maxX, maxY, minZ));

        this.add(new Vertex( minX, maxY, maxZ)); //(Xb)
        this.add(new Vertex( maxX, maxY, maxZ));

        this.add(new Vertex( minX, minY, maxZ)); //(Xc)
        this.add(new Vertex( maxX, minY, maxZ));

        this.add(new Vertex( minX, minY, minZ)); //(Xd)
        this.add(new Vertex( maxX, minY, minZ));
        /*
         * Boundary Y
         */
        this.add(new Vertex( minX, minY, minZ)); //(Ya)
        this.add(new Vertex( minX, maxY, minZ));

        this.add(new Vertex( minX, minY, maxZ)); //(Yb)
        this.add(new Vertex( minX, maxY, maxZ));

        this.add(new Vertex( maxX, minY, maxZ)); //(Yc)
        this.add(new Vertex( maxX, maxY, maxZ));

        this.add(new Vertex( maxX, minY, maxZ)); //(Yd)
        this.add(new Vertex( maxX, maxY, minZ));
        /*
         * Boundary Z
         */
        this.add(new Vertex( minX, maxY, minZ)); //(Za)
        this.add(new Vertex( minX, maxY, maxZ));

        this.add(new Vertex( maxX, maxY, minZ)); //(Zb)
        this.add(new Vertex( maxX, maxY, maxZ));

        this.add(new Vertex( maxX, minY, minZ)); //(Zc)
        this.add(new Vertex( maxX, minY, maxZ));

        this.add(new Vertex( minX, minY, minZ)); //(Zd)
        this.add(new Vertex( minX, minY, maxZ));

        this.add(new End());

        return this;
    }
}
