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


public final class Viewport
    extends Object
{
    public final static int LAYOUT_MAX_VIEWS = 32;

    public int       layoutStyle;
    public int       layoutActive;
    public int       layoutSwap;
    public int       layoutSwapPrior;
    public int       layoutSwapView;
    public int[]     layoutPosition = {0,0};
    public int[]     layoutSize = {0,0};
    public View[]    layoutViews;
    public View.Type defaultType;
    public float[]   defaultPosition = new float[3];
    public float     defaultWidth;
    public float     defaultHorizAngle;
    public float     defaultVertAngle;
    public float     defaultRollAngle;
    public String    defaultCamera;


    public Viewport(Model model, Reader r, Chunk cp)
        throws Fv3Exception
    {
        super();
        this.read(model,r,cp);
    }


    public void read(Model model, Reader r, Chunk cp)
        throws Fv3Exception
    {
        switch (cp.id){
        case Chunk.VIEWPORT_LAYOUT: {
            int cur = 0;
            this.layoutStyle = r.readU16(cp);
            this.layoutActive = r.readS16(cp);
            cp.skip(4);
            this.layoutSwap = r.readS16(cp);
            cp.skip(4);
            this.layoutSwapPrior = r.readS16(cp);
            this.layoutSwapView = r.readS16(cp);
            while (cp.in()) {
                Chunk cp1 = r.next(cp);
                switch (cp1.id) {
                    case Chunk.VIEWPORT_SIZE: {
                        this.layoutPosition[0] = r.readU16(cp1);
                        this.layoutPosition[1] = r.readU16(cp1);
                        this.layoutSize[0] = r.readU16(cp1);
                        this.layoutSize[1] = r.readU16(cp1);
                        break;
                    }
                    case Chunk.VIEWPORT_DATA_3: {
                        if (cur < LAYOUT_MAX_VIEWS) {
                            cp1.skip(4);
                            this.layoutViews = View.Add(this.layoutViews,new View());
                            this.layoutViews[cur].axisLock = r.readU16(cp1);
                            this.layoutViews[cur].position[0] = r.readS16(cp1);
                            this.layoutViews[cur].position[1] = r.readS16(cp1);
                            this.layoutViews[cur].size[0] = r.readS16(cp1);
                            this.layoutViews[cur].size[1] = r.readS16(cp1);
                            this.layoutViews[cur].type = r.readU16(cp1);
                            this.layoutViews[cur].zoom = r.readFloat(cp1);
                            r.readVector(cp1, this.layoutViews[cur].center);
                            this.layoutViews[cur].horizAngle = r.readFloat(cp1);
                            this.layoutViews[cur].vertAngle = r.readFloat(cp1);
                            this.layoutViews[cur].camera = r.readString(cp1);
                            ++cur;
                        }
                        break;
                    }
                    case Chunk.VIEWPORT_DATA:
                        /* 3DS R2 & R3 chunk unsupported */
                        break;
                }
            }
            break;
        }

        case Chunk.DEFAULT_VIEW: {
            while (cp.in()) {
                Chunk cp1 = r.next(cp);
                switch (cp1.id) {
                    case Chunk.VIEW_TOP: {
                        this.defaultType = View.Type.TOP;
                        r.readVector(cp1, this.defaultPosition);
                        this.defaultWidth = r.readFloat(cp1);
                        break;
                    }
                    case Chunk.VIEW_BOTTOM: {
                        this.defaultType = View.Type.BOTTOM;
                        r.readVector(cp1, this.defaultPosition);
                        this.defaultWidth = r.readFloat(cp1);
                        break;
                    }
                    case Chunk.VIEW_LEFT: {
                        this.defaultType = View.Type.LEFT;
                        r.readVector(cp1, this.defaultPosition);
                        this.defaultWidth = r.readFloat(cp1);
                        break;
                    }
                    case Chunk.VIEW_RIGHT: {
                        this.defaultType = View.Type.RIGHT;
                        r.readVector(cp1, this.defaultPosition);
                        this.defaultWidth = r.readFloat(cp1);
                        break;
                    }
                    case Chunk.VIEW_FRONT: {
                        this.defaultType = View.Type.FRONT;
                        r.readVector(cp1, this.defaultPosition);
                        this.defaultWidth = r.readFloat(cp1);
                        break;
                    }
                    case Chunk.VIEW_BACK: {
                        this.defaultType = View.Type.BACK;
                        r.readVector(cp1, this.defaultPosition);
                        this.defaultWidth = r.readFloat(cp1);
                        break;
                    }
                    case Chunk.VIEW_USER: {
                        this.defaultType = View.Type.USER;
                        r.readVector(cp1, this.defaultPosition);
                        this.defaultWidth = r.readFloat(cp1);
                        this.defaultHorizAngle = r.readFloat(cp1);
                        this.defaultVertAngle = r.readFloat(cp1);
                        this.defaultRollAngle = r.readFloat(cp1);
                        break;
                    }
                    case Chunk.VIEW_CAMERA: {
                        this.defaultType = View.Type.CAMERA;
                        this.defaultCamera = r.readString(cp1);
                        break;
                    }
                }
            }
            break;
        }
        }
    }
}
