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
package fv3.nui;

import javax.media.opengl.GL2;

public class Light 
    extends Component
{

    protected volatile int number;

    protected volatile float position[];


    /**
     * Nominal Z infinite light number zero
     */
    public Light(){
        this(0,0f,0f,1f);
    }
    /**
     * @param num Positive light number from zero
     * @param pos Homogeneous light position coordinates (homogeneous
     * scale zero indicates an infinite light having parallel rays,
     * rather than a local light having angular rays).
     */
    public Light(int num, float[] pos){
        super();
        this.setNumber(num);
        this.setPosition(pos);
    }
    /**
     * @param num Positive light number from zero
     * @param x Homogeneous light position coordinates
     * @param y Homogeneous light position coordinates
     * @param z Homogeneous light position coordinates
     * @param w Homogeneous light position scale (value zero indicates
     * an infinite light having parallel rays, rather than a local
     * light having angular rays).
     */
    public Light(int num, float x, float y, float z, float w){
        this(num, new float[]{x,y,z,w});
    }
    /**
     * @param num Positive light number from zero
     * @param x Infinite light position coordinates
     * @param y Infinite light position coordinates
     * @param z Infinite light position coordinates
     */
    public Light(int num, float x, float y, float z){
        this(num, x, y, z, 0.0f);
    }
    /**
     * @param num Positive light number from zero
     * @param x Infinite light position coordinates
     * @param y Infinite light position coordinates
     * @param z Infinite light position coordinates
     */
    public Light(int num, double x, double y, double z){
        this(num, (float)x, (float)y, (float)z);
    }


    public void init(GL2 gl){
        float[] pos = this.position;
        if (null != pos){
            switch (this.number){
            case 0:
                gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0);
                gl.glEnable(GL2.GL_LIGHTING);
                gl.glEnable(GL2.GL_LIGHT0);
                return;
            case 1:
                gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, pos, 0);
                gl.glEnable(GL2.GL_LIGHTING);
                gl.glEnable(GL2.GL_LIGHT1);
                return;
            case 2:
                gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, pos, 0);
                gl.glEnable(GL2.GL_LIGHTING);
                gl.glEnable(GL2.GL_LIGHT2);
                return;
            case 3:
                gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_POSITION, pos, 0);
                gl.glEnable(GL2.GL_LIGHTING);
                gl.glEnable(GL2.GL_LIGHT3);
                return;
            case 4:
                gl.glLightfv(GL2.GL_LIGHT4, GL2.GL_POSITION, pos, 0);
                gl.glEnable(GL2.GL_LIGHTING);
                gl.glEnable(GL2.GL_LIGHT4);
                return;
            case 5:
                gl.glLightfv(GL2.GL_LIGHT5, GL2.GL_POSITION, pos, 0);
                gl.glEnable(GL2.GL_LIGHTING);
                gl.glEnable(GL2.GL_LIGHT5);
                return;
            case 6:
                gl.glLightfv(GL2.GL_LIGHT6, GL2.GL_POSITION, pos, 0);
                gl.glEnable(GL2.GL_LIGHTING);
                gl.glEnable(GL2.GL_LIGHT6);
                return;
            case 7:
                gl.glLightfv(GL2.GL_LIGHT7, GL2.GL_POSITION, pos, 0);
                gl.glEnable(GL2.GL_LIGHTING);
                gl.glEnable(GL2.GL_LIGHT7);
                return;
            }
        }
    }
    public final int getNumber(){
        return this.number;
    }
    public final void setNumber(int num){
        if (-1 < num && 8 > num)
            this.number = num;
        else
            throw new IllegalArgumentException();
    }
    public final float[] getPosition(){
        return this.position;
    }
    public final void setPosition(float[] pos){
        if (null == pos || 4 == pos.length)
            this.position = pos;
        else
            throw new IllegalArgumentException();
    }

}
