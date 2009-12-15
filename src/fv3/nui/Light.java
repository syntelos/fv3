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


    public Light(){
        super();
    }
    public Light(int num, float[] pos){
        super();
        this.setNumber(num);
        this.setPosition(pos);
    }


    public void init(GL2 gl){
        gl.glEnable(GL2.GL_LIGHTING);
        float[] pos = this.position;
        if (null != pos){
            switch (this.number){
            case 0:
                gl.glEnable(GL2.GL_LIGHT0);
                gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0);
                return;
            case 1:
                gl.glEnable(GL2.GL_LIGHT1);
                gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, pos, 0);
                return;
            case 2:
                gl.glEnable(GL2.GL_LIGHT2);
                gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, pos, 0);
                return;
            case 3:
                gl.glEnable(GL2.GL_LIGHT3);
                gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_POSITION, pos, 0);
                return;
            case 4:
                gl.glEnable(GL2.GL_LIGHT4);
                gl.glLightfv(GL2.GL_LIGHT4, GL2.GL_POSITION, pos, 0);
                return;
            case 5:
                gl.glEnable(GL2.GL_LIGHT5);
                gl.glLightfv(GL2.GL_LIGHT5, GL2.GL_POSITION, pos, 0);
                return;
            case 6:
                gl.glEnable(GL2.GL_LIGHT6);
                gl.glLightfv(GL2.GL_LIGHT6, GL2.GL_POSITION, pos, 0);
                return;
            case 7:
                gl.glEnable(GL2.GL_LIGHT7);
                gl.glLightfv(GL2.GL_LIGHT7, GL2.GL_POSITION, pos, 0);
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
