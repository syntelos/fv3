/*
 * fv3
 * Copyright (C) 2010, John Pritchard, all rights reserved.
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package fv3.model;

import fv3.math.Color;
import fv3.nui.Light;
import fv3.tk.Animator;

import javax.media.opengl.GL2;

/**
 * An exception containing a model can be used for debugging the
 * construction of a larger model by showing a problematic part.
 */
public class Debugger
    extends IllegalStateException
{

    public final static class World
        extends fv3.World
    {

        public World(Debugger deb){
            super();
            this.setBgColor(Color.White);

            this.add(new Light());

            this.add(deb.model);

            this.setFv3Bounds();

            this.defineCamera('A').orthoFront(this);
            this.defineCamera('B').orthoBack(this);
            this.defineCamera('C').orthoTop(this);
            this.defineCamera('D').orthoBottom(this);
            this.defineCamera('E').orthoLeft(this);
            this.defineCamera('F').orthoRight(this);
        }
    }

    public final Debugger.World world;
    public final Model model;


    public Debugger(String m, fv3.Model.Element... model){
        super(m);
        if (null != model && 0 < model.length){

            this.model = new Model();

            this.model.add(new Material(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, Color.Blue));
            this.model.add(new ShadeModel(GL2.GL_FLAT));
            this.model.add(new PolygonMode.Line(GL2.GL_FRONT_AND_BACK));

            for (fv3.Model.Element object: model){

                this.model.add(object);
            }

            this.world = new Debugger.World(this);
        }
        else
            throw new IllegalArgumentException();
    }

    public void show(){

        Animator animator = new Animator(this.world);
        animator.start();
    }
    public fv3.Bounds.CircumSphere getFv3Bounds(){
        return (fv3.Bounds.CircumSphere)this.world.getFv3Bounds();
    }
    public void println(){
        System.out.println("Bounds");
        System.out.println(this.getFv3Bounds().toString("\t"));
        System.out.println("Model");
        System.out.println(this.model.toString("\t"));
        System.out.println("Geom");
        for (fv3.Model.Element el: this.model){

            System.out.println(el);
        }
    }
}
