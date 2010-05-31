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

/**
 * Interface implemented by components having bounds.  All methods
 * must return correct information. 
 * 
 * @see Component
 */
public interface Bounds
{
    /**
     * Common bounds adaptor used by {@link fv3.Camera}.
     */
    public static class CircumSphere
        extends java.lang.Object
        implements Bounds
    {
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
    }

    public double getBoundsMinX();
    public double getBoundsMidX();
    public double getBoundsMaxX();

    public double getBoundsMinY();
    public double getBoundsMidY();
    public double getBoundsMaxY();

    public double getBoundsMinZ();
    public double getBoundsMidZ();
    public double getBoundsMaxZ();
}
