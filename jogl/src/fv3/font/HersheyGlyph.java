/*
 * fv3
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
package fv3.font;

/**
 * 
 * @author John Pritchard
 */
public class HersheyGlyph
    extends Glyph<HersheyFont,HersheyPath>
{

    private double sp;


    protected HersheyGlyph(HersheyFont font, HersheyPath path){
        super(Type.Lines,(path.nlines<<1),font);
        this.add(path);
    }


    public HersheyGlyph clone(){
        return (HersheyGlyph)super.clone();
    }
    public boolean isSpace(){
        return (0.0 < this.sp);
    }
    public double getSpaceHorizontal(){
        return this.sp;
    }
    protected HersheyGlyph setSpaceHorizontal(double x){
        this.sp = x;
        return this;
    }
    public double getPathMinX(){
        final HersheyPath path = (HersheyPath)this.list[0];
        return path.minX;
    }
    public double getPathMidX(){
        final HersheyPath path = (HersheyPath)this.list[0];
        return path.midX;
    }
    public double getPathMaxX(){
        final HersheyPath path = (HersheyPath)this.list[0];
        return path.maxX;
    }
    public double getPathMinY(){
        final HersheyPath path = (HersheyPath)this.list[0];
        return path.minY;
    }
    public double getPathMidY(){
        final HersheyPath path = (HersheyPath)this.list[0];
        return path.midY;
    }
    public double getPathMaxY(){
        final HersheyPath path = (HersheyPath)this.list[0];
        return path.maxY;
    }
    public double getPathMinZ(){
        final HersheyPath path = (HersheyPath)this.list[0];
        return path.minZ;
    }
    public double getPathMidZ(){
        final HersheyPath path = (HersheyPath)this.list[0];
        return path.midZ;
    }
    public double getPathMaxZ(){
        final HersheyPath path = (HersheyPath)this.list[0];
        return path.maxZ;
    }
}
