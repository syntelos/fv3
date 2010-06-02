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
package fv3;

import fv3.math.Vector;
import fv3.model.Begin;
import fv3.model.End;
import fv3.model.Model;
import fv3.model.Vertex;

import javax.media.opengl.GL2;

/**
 * Interface implemented by components having bounds.  All methods
 * must return correct information. 
 * 
 * Bounds change when components have matrices.  The returned values
 * are "external" dimensions, including matrix transformations.
 * 
 * @see Component
 */
public interface Bounds
{
    public double getBoundsMinX();
    public double getBoundsMidX();
    public double getBoundsMaxX();

    public double getBoundsMinY();
    public double getBoundsMidY();
    public double getBoundsMaxY();

    public double getBoundsMinZ();
    public double getBoundsMidZ();
    public double getBoundsMaxZ();


    /**
     * Common bounds adaptor used by {@link fv3.Camera}.
     */
    public static class CircumSphere
        extends java.lang.Object
        implements Bounds
    {
        public final static CircumSphere For(Component c){
            if (c.hasFv3Bounds()){
                Bounds b = c.getFv3Bounds();
                if (b instanceof CircumSphere)
                    return (CircumSphere)b;
            }
            return new CircumSphere(c);
        }


        public final double minX, minY, minZ;
        public final double midX, midY, midZ;
        public final double maxX, maxY, maxZ;
        public final double diameter, radius;


        public CircumSphere(Bounds bounds){
            super();

            this.minX = bounds.getBoundsMinX();
            this.minY = bounds.getBoundsMinY();
            this.minZ = bounds.getBoundsMinZ();

            this.maxX = bounds.getBoundsMaxX();
            this.maxY = bounds.getBoundsMaxY();
            this.maxZ = bounds.getBoundsMaxZ();

            this.midX = bounds.getBoundsMidX();
            this.midY = bounds.getBoundsMidY();
            this.midZ = bounds.getBoundsMidZ();

            this.diameter = Vector.Diameter(this.minX, this.maxX,
                                            this.minY, this.maxY,
                                            this.minZ, this.maxZ);
            this.radius = (this.diameter / 2.0);
        }
        /**
         * Fetch bounds for a component or region or throw an
         * exception.
         * @exception java.lang.IllegalArgumentException Argument neither bounded nor type region
         * @exception java.lang.IllegalStateException Region has no bounded child
         */
        public CircumSphere(Component c)
            throws java.lang.IllegalArgumentException, 
                   java.lang.IllegalStateException
        {
            super();
            if (c.hasFv3Bounds()){

                Bounds bounds = c.getFv3Bounds();

                this.minX = bounds.getBoundsMinX();
                this.minY = bounds.getBoundsMinY();
                this.minZ = bounds.getBoundsMinZ();

                this.maxX = bounds.getBoundsMaxX();
                this.maxY = bounds.getBoundsMaxY();
                this.maxZ = bounds.getBoundsMaxZ();

                this.midX = bounds.getBoundsMidX();
                this.midY = bounds.getBoundsMidY();
                this.midZ = bounds.getBoundsMidZ();

                this.diameter = Vector.Diameter(this.minX, this.maxX,
                                                this.minY, this.maxY,
                                                this.minZ, this.maxZ);
                this.radius = (this.diameter / 2.0);
            }
            else if (c instanceof Region){

                boolean once = true;

                double minX = 0, minY = 0, minZ = 0;
                double maxX = 0, maxY = 0, maxZ = 0;

                Region region = (Region)c;
                for (Component child : region.getChildren()){
                    if (child.hasFv3Bounds()){
                        Bounds bounds = child.getFv3Bounds();
                        if (once){
                            once = false;
                            minX = bounds.getBoundsMinX();
                            maxX = bounds.getBoundsMaxX();
                            minY = bounds.getBoundsMinY();
                            maxY = bounds.getBoundsMaxY();
                            minZ = bounds.getBoundsMinZ();
                            maxZ = bounds.getBoundsMaxZ();
                        }
                        else {
                            minX = Math.min(minX,bounds.getBoundsMinX());
                            maxX = Math.max(maxX,bounds.getBoundsMaxX());
                            minY = Math.min(minY,bounds.getBoundsMinY());
                            maxY = Math.max(maxY,bounds.getBoundsMaxY());
                            minZ = Math.min(minZ,bounds.getBoundsMinZ());
                            maxZ = Math.max(maxZ,bounds.getBoundsMaxZ());
                        }
                    }
                }
                if (!once){
                    double midX = ((maxX - minX)/2)+minX;
                    double midY = ((maxY - minY)/2)+minY;
                    double midZ = ((maxZ - minZ)/2)+minZ;

                    double d = Vector.Diameter(minX, maxX,
                                               minY, maxY,
                                               minZ, maxZ);

                    this.minX = minX;
                    this.minY = minY;
                    this.minZ = minZ;

                    this.midX = midX;
                    this.midY = midY;
                    this.midZ = midZ;

                    this.maxX = maxX;
                    this.maxY = maxY;
                    this.maxZ = maxZ;

                    this.diameter = d;
                    this.radius = (d / 2.0);
                }
                else
                    throw new IllegalStateException("No bounds found in region");
            }
            else
                throw new IllegalArgumentException("Component has no bounds and is not region");
        }


        public double getBoundsMinX(){
            return this.minX;
        }
        public double getBoundsMidX(){
            return this.midX;
        }
        public double getBoundsMaxX(){
            return this.maxX;
        }
        public double getBoundsMinY(){
            return this.minY;
        }
        public double getBoundsMidY(){
            return this.midY;
        }
        public double getBoundsMaxY(){
            return this.maxY;
        }
        public double getBoundsMinZ(){
            return this.minZ;
        }
        public double getBoundsMidZ(){
            return this.midZ;
        }
        public double getBoundsMaxZ(){
            return this.maxZ;
        }
        /**
         * Boundary cube 
         */
        public void glBoundary(Model model){

            model.add(new Begin(GL2.GL_LINES));
            /*
             * Boundary X
             */
            model.add(new Vertex( minX, maxY, minZ)); //(Xa)
            model.add(new Vertex( maxX, maxY, minZ));

            model.add(new Vertex( minX, maxY, maxZ)); //(Xb)
            model.add(new Vertex( maxX, maxY, maxZ));

            model.add(new Vertex( minX, minY, maxZ)); //(Xc)
            model.add(new Vertex( maxX, minY, maxZ));

            model.add(new Vertex( minX, minY, minZ)); //(Xd)
            model.add(new Vertex( maxX, minY, minZ));
            /*
             * Boundary Y
             */
            model.add(new Vertex( minX, minY, minZ)); //(Ya)
            model.add(new Vertex( minX, maxY, minZ));

            model.add(new Vertex( minX, minY, maxZ)); //(Yb)
            model.add(new Vertex( minX, maxY, maxZ));

            model.add(new Vertex( maxX, minY, maxZ)); //(Yc)
            model.add(new Vertex( maxX, maxY, maxZ));

            model.add(new Vertex( maxX, minY, maxZ)); //(Yd)
            model.add(new Vertex( maxX, maxY, minZ));
            /*
             * Boundary Z
             */
            model.add(new Vertex( minX, maxY, minZ)); //(Za)
            model.add(new Vertex( minX, maxY, maxZ));

            model.add(new Vertex( maxX, maxY, minZ)); //(Zb)
            model.add(new Vertex( maxX, maxY, maxZ));

            model.add(new Vertex( maxX, minY, minZ)); //(Zc)
            model.add(new Vertex( maxX, minY, maxZ));

            model.add(new Vertex( minX, minY, minZ)); //(Zd)
            model.add(new Vertex( minX, minY, maxZ));

            model.add(new End());
        }
        /**
         * Axes through cube center.
         */
        public void glCenter(Model model){

            model.add(new Begin(GL2.GL_LINES));

            model.add(new Vertex( minX, midY, midZ)); //(X)
            model.add(new Vertex( maxX, midY, midZ));

            model.add(new Vertex( midX, minY, midZ)); //(Y)
            model.add(new Vertex( midX, maxY, midZ));

            model.add(new Vertex( midX, midY, minZ)); //(Z)
            model.add(new Vertex( midX, midY, maxZ));

            model.add(new End());
        }
    }

}
