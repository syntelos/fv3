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
import fv3.model.Vertex;

/**
 * Interface implemented by components having bounds.  All methods
 * must return correct information. 
 * 
 * Bounds change when components have matrices.  The returned values
 * are "global" dimensions, including the composition of all matrix
 * transformations in the hierarchy of the component.
 * 
 * @see Component
 * @see Component#composeFv3Matrix
 */
public interface Bounds
    extends fv3.math.Notation
{
    public float getBoundsMinX();
    public float getBoundsMidX();
    public float getBoundsMaxX();

    public float getBoundsMinY();
    public float getBoundsMidY();
    public float getBoundsMaxY();

    public float getBoundsMinZ();
    public float getBoundsMidZ();
    public float getBoundsMaxZ();


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


        public final float minX, minY, minZ;
        public final float midX, midY, midZ;
        public final float maxX, maxY, maxZ;
        public final float diameter, radius;


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
            this.radius = (this.diameter / 2.0f);
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
                this.radius = (this.diameter / 2.0f);
            }
            else if (c instanceof Region){

                boolean once = true;

                float minX = 0, minY = 0, minZ = 0;
                float maxX = 0, maxY = 0, maxZ = 0;

                Region region = (Region)c;
                for (Component child : region.getFv3Children()){
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

                    this.minX = minX;
                    this.minY = minY;
                    this.minZ = minZ;

                    this.maxX = maxX;
                    this.maxY = maxY;
                    this.maxZ = maxZ;

                    this.midX = (minX + maxX)/2.0f;
                    this.midY = (minY + maxY)/2.0f;
                    this.midZ = (minZ + maxZ)/2.0f;

                    float d = Vector.Diameter(minX, maxX,
                                               minY, maxY,
                                               minZ, maxZ);

                    this.diameter = d;
                    this.radius = (d / 2.0f);
                }
                else
                    throw new IllegalStateException("No bounds found in region");
            }
            else
                throw new IllegalArgumentException("Component has no bounds and is not region");
        }


        public float tX(){
            float tx = ( (maxX + minX) / (maxX - minX));
            if (tx != tx)
                return ZERO; //(minX=0; maxX = 0)

            else if (ZERO == tx)
                return ZERO;
            else
                return -(tx);
        }
        public float tY(){
            float ty = ( (maxY + minY) / (maxY - minY));
            if (ty != ty)
                return ZERO;

            else if (ZERO == ty)
                return ZERO;
            else
                return -(ty);
        }
        public float tZ(){
            float tz = ( (maxZ + minZ) / (maxZ - minZ));
            if (tz != tz)
                return ZERO;

            else if (ZERO == tz)
                return ZERO;
            else
                return -(tz);
        }
        public float dX(){
            return Math.abs(maxX - minX);
        }
        public float dY(){
            return Math.abs(maxY - minY);
        }
        public float dZ(){
            return Math.abs(maxZ - minZ);
        }
        public boolean hasDx(){
            return (ZERO < this.dX());
        }
        public boolean hasDy(){
            return (ZERO < this.dY());
        }
        public boolean hasDz(){
            return (ZERO < this.dZ());
        }
        public float getBoundsMinX(){
            return this.minX;
        }
        public float getBoundsMidX(){
            return this.midX;
        }
        public float getBoundsMaxX(){
            return this.maxX;
        }
        public float getBoundsMinY(){
            return this.minY;
        }
        public float getBoundsMidY(){
            return this.midY;
        }
        public float getBoundsMaxY(){
            return this.maxY;
        }
        public float getBoundsMinZ(){
            return this.minZ;
        }
        public float getBoundsMidZ(){
            return this.midZ;
        }
        public float getBoundsMaxZ(){
            return this.maxZ;
        }
        /**
         * Boundary cube 
         */
        public void glBoundary(Model model){
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
        }
        /**
         * Axes through cube center.
         */
        public void glCenter(Model model){

            model.add(new Vertex( minX, midY, midZ)); //(X)
            model.add(new Vertex( maxX, midY, midZ));

            model.add(new Vertex( midX, minY, midZ)); //(Y)
            model.add(new Vertex( midX, maxY, midZ));

            model.add(new Vertex( midX, midY, minZ)); //(Z)
            model.add(new Vertex( midX, midY, maxZ));
        }
        public String toString(){
            return this.toString("","\n");
        }
        public String toString(String pr){
            return this.toString(pr,"\n");
        }
        public String toString(String pr, String in){
            if (null == pr)
                pr = "";
            if (null == in)
                in = "";

            return String.format("%s%30.26f %30.26f%s%s%30.26f %30.26f %30.26f%s%s%30.26f %30.26f %30.26f%s%s%30.26f %30.26f %30.26f", 
                                 pr, diameter, radius,
                                 in, pr, minX, minY, minZ, 
                                 in, pr, midX, midY, midZ, 
                                 in, pr, maxX, maxY, maxZ);
        }
    }

}
