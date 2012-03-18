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
package fv3.font;

/**
 * For text layout, X, Y and Z can be set to starting points.  In this
 * case, initialize and then use any combination of append and newline
 * to layout text.  The init step is only necessary when setting X, Y
 * or Z to non zero (non default) values.
 * 
 * Optionally fit the completed glyph vector to coordinates and
 * dimensions.
 * 
 * Glyph vectors can be constructed from and added into each other.
 * Constructing a new glyph vector with a new font from another will
 * pick up the current typographic position of the first.  
 * 
 * Adding a composed glyph vector to another will combine the two
 * products, which is convenient for subsequently "fitting" the
 * combined product of multiple fonts (see {@link
 * fv3.math.VertexArray#fit}).  However, combining font products
 * precludes the option to differentiate rendering color.
 * 
 * @author John Pritchard
 */
public class GlyphVector
    extends fv3.math.VertexArray
{

    protected final Font font;

    protected final float em, lineheight;

    protected float x, y, z, xp, yp, zp;


    public GlyphVector(Font font){
        super(font.getGlyphVectorType(),0);
        if (null != font){
            this.font = font;
            this.em = font.getEm();
            this.lineheight = this.font.getAscent()+this.font.getDescent()+this.font.getLeading();
        }
        else
            throw new IllegalArgumentException();
    }
    public GlyphVector(Font font, String text){
        this(font);
        this.append(text);
    }
    public GlyphVector(GlyphVector state, Font font){
        this(font);
        this.x = state.xp;
        this.y = state.yp;
        this.z = state.zp;
        this.xp = state.xp;
        this.yp = state.yp;
        this.zp = state.zp;
    }


    public final float getX(){
        return this.x;
    }
    public final float getXp(){
        return this.xp;
    }
    public final GlyphVector setX(float x){
        this.x = x;
        return this;
    }
    public final float getY(){
        return this.y;
    }
    public final float getYp(){
        return this.yp;
    }
    public final GlyphVector setY(float y){
        this.y = y;
        return this;
    }
    public final float getZ(){
        return this.z;
    }
    public final float getZp(){
        return this.zp;
    }
    public final GlyphVector setZ(float z){
        this.z = z;
        return this;
    }
    public final GlyphVector init(float x, float y, float z){

        this.x = x;
        this.y = y;
        this.z = z;

        this.xp = this.x;
        this.yp = this.y;
        this.zp = this.z;

        return this;
    }
    public final GlyphVector init(){

        this.xp = this.x;
        this.yp = this.y;
        this.zp = this.z;

        return this;
    }
    public final GlyphVector clear(){
        this.bounds = null;
        this.xp = this.x;
        this.yp = this.y;
        this.zp = this.z;
        this.countVertices(0);
        return this;
    }
    public final GlyphVector newline(){
        this.bounds = null;
        this.xp = this.x;
        this.yp -= this.lineheight;
        this.zp = this.z;
        return this;
    }
    public final GlyphVector append(String text){

        if (null != text){
            this.bounds = null;

            final char[] cary = text.toCharArray();
            final int term = (cary.length-1);

            char ch;
            Glyph gl;

            for (int cc = 0; ; cc++){

                ch = cary[cc];

                gl = this.font.clone(ch);

                if (! gl.isSpace()){

                    this.add(gl.translate(this.xp,this.yp,this.zp));

                    if (cc < term)

                        this.xp += font.spacing(ch,cary[cc+1]);
                    else
                        break;
                }
                else if (cc < term)

                    this.xp += gl.getSpaceHorizontal();
                else
                    break;
            }
        }
        return this;
    }
    public final GlyphVector println(String text){
        this.append(text);
        return this.newline();
    }
    public final GlyphVector println(){
        return this.newline();
    }
}
