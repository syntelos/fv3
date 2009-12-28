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
 * The general strategy is that applications embed fonts; fonts are
 * scaled and rendered for size and resolution.  Following the Fv3
 * model, whether this happens at run time or design time is a
 * variable.
 * 
 * <h3>Extension</h3>
 * 
 * This font class may be subclassed to implement specialized glyph
 * subclasses.  A text editor may employ a glyph subclass as an
 * integrated component of its internal data structures.
 * 
 * @author John Pritchard
 */
public class Font
    extends Object
    implements Cloneable
{

    private final String name;

    private final FontOptions options;

    private Glyph[] glyph;


    public Font(String name, FontReader reader) {
        this(name,reader,(new FontOptions()));
    }
    public Font(String name, FontReader reader, FontOptions opts) {
        super();
        if (null != name && null != reader && null != opts){
            this.name = name;
            this.options = opts;

            Glyph[] list = null;
            Glyph glyp = reader.read(this);

            while (null != glyp){

                glyp.init(opts);

                if (null == list)
                    list = new Glyph[]{glyp};
                else {
                    int len = list.length;
                    Glyph[] copier = new Glyph[len+1];
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
        Glyph[] glyphs = this.glyph;
        if (null != glyphs){
            this.glyph = null;
            for (Glyph gly : glyphs){
                gly.destroy();
            }
        }
    }
    public final String getName(){
        return this.name;
    }
    public final FontOptions getOptions(){
        return this.options;
    }
    protected Glyph createGlyph(){

        return new Glyph(this);
    }
    public final int getLength(){
        Glyph[] list = this.glyph;
        if (null == list)
            return 0;
        else
            return list.length;
    }
    public final Glyph get(int idx){
        Glyph[] list = this.glyph;
        if (null == list)
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx));

        else if (-1 < idx && idx < list.length)
            return list[idx];
        else
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    public Font clone(){
        try {
            return (Font)super.clone();
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
        else if (!(o instanceof Font))
            return false;
        else 
            return (((Font)o).name.equals(name));
    }
}
