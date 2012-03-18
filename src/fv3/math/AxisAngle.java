/*
 * fv3.math
 * Copyright (C) 2012, John Pritchard, all rights reserved.
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
package fv3.math;

/**
 * A rotation specification in axis and angle of rotation.  
 * 
 * The angle of rotation is in radians.
 * 
 * @see Quat
 * @author jdp
 */
public class AxisAngle
    extends Vector
{
    public final static class Axis {
        final static float[] X = {1f,0f,0f};
        final static float[] Y = {0f,1f,0f};
        final static float[] Z = {0f,0f,1f};

        public final static float[] X(){
            return X.clone();
        }
        public final static float[] Y(){
            return Y.clone();
        }
        public final static float[] Z(){
            return Z.clone();
        }
        public final static AxisAngle X(float angle){
            return new AxisAngle(X.clone(),angle);
        }
        public final static AxisAngle Y(float angle){
            return new AxisAngle(Y.clone(),angle);
        }
        public final static AxisAngle Z(float angle){
            return new AxisAngle(Z.clone(),angle);
        }
        public final static class Degrees {

            public final static AxisAngle X(float angle){
                return new AxisAngle(Axis.X.clone(),AxisAngle.Degrees(angle));
            }
            public final static AxisAngle Y(float angle){
                return new AxisAngle(Axis.Y.clone(),AxisAngle.Degrees(angle));
            }
            public final static AxisAngle Z(float angle){
                return new AxisAngle(Axis.Z.clone(),AxisAngle.Degrees(angle));
            }
        }
    }



    private volatile float angle;


    public AxisAngle(float[] vector, float angle){
        super(vector);
        this.angle = angle;
    }


    public final float angle(){
        return this.angle;
    }
    public final float getAngle(){
        return this.angle;
    }
    public final Vector angle(float angle){
        this.angle = angle;
        return this;
    }
    public final Vector setAngle(float angle){
        this.angle = angle;
        return this;
    }

    public String toString(){
        float[] v = this.array();
        float a = this.angle;
        return String.format("%30.26f %30.26f %30.26f %30.26f", v[0], v[1], v[2], a);
    }

}
