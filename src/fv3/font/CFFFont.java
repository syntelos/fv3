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

import fv3.font.cff.CoordsConstants;
import fv3.font.cff.DisplayOptions;
import fv3.font.cff.InstructionStream;

/**
 * This font class may be subclassed to implement specialized glyph
 * subclasses.  A text editor may employ a glyph subclass as an
 * integrated component of its internal data structures.
 * 
 * To subclass {@link CFFGlyph}, a font subclass overrides the {@link
 * #create(fv3.font.cff.InstructionStream)} method to return the
 * specialized glyph subclass.
 * 
 * @author Tim Tyler
 * @author John Pritchard
 */
public class CFFFont
    extends Object
    implements CoordsConstants,
               Cloneable
{

    private final String name;

    private final DisplayOptions options;

    private CFFGlyph[] glyph;


    public CFFFont(String name, CFFFontReader reader) {
        this(name,reader,(new DisplayOptions()));
    }
    public CFFFont(String name, CFFFontReader reader, DisplayOptions opts) {
        super();
        if (null != name && null != reader && null != opts){
            this.name = name;
            this.options = opts;

            CFFGlyph[] list = null;
            CFFGlyph glyp = reader.read(this);

            while (null != glyp){

                glyp.init(opts);

                if (null == list)
                    list = new CFFGlyph[]{glyp};
                else {
                    int len = list.length;
                    CFFGlyph[] copier = new CFFGlyph[len+1];
                    System.arraycopy(list,0,copier,0,len);
                    copier[len] = glyp;
                    list = copier;
                }
                glyp = reader.read(this);
            }
            this.glyph = list;
        }
        else
            throw new IllegalArgumentException();
    }


    public final boolean alive(){
        return (null != this.glyph);
    }
    public void destroy(){
        CFFGlyph[] glyphs = this.glyph;
        if (null != glyphs){
            this.glyph = null;
            for (CFFGlyph gly : glyphs){
                gly.destroy();
            }
        }
    }
    protected CFFGlyph create(InstructionStream in){

        return new CFFGlyph(this,in);
    }
    public final String getName(){
        return this.name;
    }
    public final DisplayOptions getOptions(){
        return this.options;
    }
    public final int getLength(){
        CFFGlyph[] list = this.glyph;
        if (null == list)
            return 0;
        else
            return list.length;
    }
    public final CFFGlyph get(int idx){
        CFFGlyph[] list = this.glyph;
        if (null == list)
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx));

        else if (-1 < idx && idx < list.length)
            return list[idx];
        else
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    public final int getMinX() {
        int min_x = Integer.MAX_VALUE;

        for (int i = 0, count = this.getLength(); i < count; i++) {

            int min_this = glyph[i].getMinX();

            if (min_this < min_x) {
                min_x = min_this;
            }
        }
        return min_x;
    }
    public final int getMinY() {
        int min_y = Integer.MAX_VALUE;

        for (int i = 0, count = this.getLength(); i < count; i++) {

            int min_this = glyph[i].getMinY();

            if (min_this < min_y) {
                min_y = min_this;
            }
        }
        return min_y;
    }
    public final int getMaxX() {
        int max_x = Integer.MIN_VALUE;

        for (int i = 0, count = this.getLength(); i < count; i++) {

            int max_this = glyph[i].getMaxX();

            if (max_this > max_x) {
                max_x = max_this;
            }
        }
        return max_x;
    }
    public final int getMaxY() {
        int max_y = Integer.MIN_VALUE;

        for (int i = 0, count = this.getLength(); i < count; i++) {

            int max_this = glyph[i].getMaxY();

            if (max_this > max_y) {
                max_y = max_this;
            }
        }
        return max_y;
    }
    public CFFFont clone(){
        try {
            return (CFFFont)super.clone();
        }
        catch (CloneNotSupportedException exc){
            throw new InternalError();
        }
    }
    public int hashCode() {
        return (this.name.hashCode() ^ this.options.hashCode());
    }
    public boolean equals(Object o) {
        if (o == this)
            return true;
        else if (!(o instanceof CFFFont))
            return false;
        else 
            return (((CFFFont)o).name.equals(name));
    }
}
