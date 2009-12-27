/*
 * fv3.ds
 * Copyright (c) 2009 John Pritchard, all rights reserved.
 * Copyright (C) 1996-2008 by Jan Eric Kyprianidis, all rights reserved.
 *     
 * This program is free  software: you can redistribute it and/or modify 
 * it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program  is  distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU Lesser General Public License for more details.
 * 
 * You should  have received a copy of the GNU Lesser General Public License
 * along with  this program; If not, see <http://www.gnu.org/licenses/>. 
 */
/*
 * Based on the work of Jan Eric Kyprianidis,  Martin van Velsen, Robin
 * Feroq, Jimm Pitts, Mats Byggmästar, and Josh DeFord.
 */
package fv3.ds;

import fv3.Fv3Exception;


public final class Camera {
    private final static double EPSILON = 1e-5;

    public final String name;
    public int         user_id;
    public Object      user_ptr;
    public int         object_flags; /*< @see ObjectFlags */ 
    public double[]     position = new double[3];
    public double[]     target = new double[3];
    public double       roll;
    public double       fov = 45f;
    public boolean     seeCone;
    public double       nearRange;
    public double       farRange;


    public Camera(Model model, Reader r, Chunk cp, String name){
        super();
        this.name = name;
        this.read(model,r,cp);
    }


    public boolean hasRanges(){
        return (0f != this.nearRange || 0f != this.farRange);
    }
    public void read(Model model, Reader r, Chunk cp)
        throws Fv3Exception
    {
        r.readVector(cp,this.position);
        r.readVector(cp,this.target);
        this.roll = r.readFloat(cp);
        double s = r.readFloat(cp);
        if (EPSILON > Math.abs(s))
            this.fov = 45f;
        else
            this.fov = (2400f / s);

        while (cp.in()){
            Chunk cp2 = r.next(cp);
            switch (cp2.id){
            case Chunk.CAM_SEE_CONE: {
                this.seeCone = true;
                break;
            }
            case Chunk.CAM_RANGES: {
                this.nearRange = r.readFloat(cp2);
                this.farRange = r.readFloat(cp2);
                break;
            }
            }
        }
    }
}
