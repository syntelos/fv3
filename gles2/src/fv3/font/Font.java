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

import fv3.math.VertexArray;

/**
 * The general strategy is that applications embed fonts; fonts are
 * scaled and rendered for size and resolution.  Following the Fv3
 * model, whether this happens at run time or design time is a
 * variable.
 * 
 * 
 * @author John Pritchard
 */
public abstract class Font<Glyph extends fv3.font.Glyph>
    extends Object
    implements Cloneable,
               Iterable<Glyph>
{
    /**
     * Single threaded cache
     */
    private final static Cache CACHE = new Cache();

    /**
     * @return Null for not found
     */
    public final static Font For(String name, double w, double h, double d){
        return Font.For(new Font.Key(name,(new FontOptions(w,h,d))));
    }
    /**
     * @return Null for not found
     */
    public final static Font For(String name, double w, double h){
        return Font.For(new Font.Key(name,(new FontOptions(w,h))));
    }
    /**
     * @return Null for not found
     */
    public final static Font For(String name){
        return Font.For(new Font.Key(name));
    }
    /**
     * @return Null for not found
     */
    public final static Font For(Font.Key key){
        Font font = CACHE.get(key);
        if (null == font){
            try {
                font = new HersheyFont(key);
                CACHE.put(key,font);
            }
            catch (java.io.IOException notfound){
                try {
                    font = new TTFFont(key);
                    CACHE.put(key,font);
                }
                catch (java.io.IOException notfound2){
                }
            }
        }
        return font;
    }
    public final static Font Drop(Font.Key key){
        return CACHE.remove(key);
    }

    public final static class Key
        extends java.lang.Object
        implements java.lang.Comparable<Key>
    {

        public final String name;
        public final FontOptions opts;
        public final int hashCode;

        public Key(String name){
            this(name,FontOptions.Default);
        }
        public Key(String name, FontOptions opts){
            super();
            this.name = name;
            this.opts = opts;
            this.hashCode = (name.hashCode()^opts.hashCode());
        }

        public int hashCode(){
            return this.hashCode;
        }
        public boolean equals(Object that){
            if (this == that)
                return true;
            else if (that instanceof Key)
                return this.equals( (Key)that);
            else
                return false;
        }
        public boolean equals(Key that){
            return (this.name.equals(that.name) && this.opts.equals(that.opts));
        }
        public int compareTo(Key that){
            if (this.equals(that))
                return 0;
            else {
                final int t = this.name.compareTo(that.name);
                if (0 == t)
                    return this.opts.compareTo(that.opts);
                else
                    return t;
            }
        }
    }

    public final String name;

    public final FontOptions options;

    private fv3.font.Glyph[] list;


    public Font(String name, FontReader reader) {
        this(name,reader,(new FontOptions()));
    }
    public Font(String name, FontReader reader, FontOptions opts) {
        super();
        if (null != name && null != reader && null != opts){
            this.name = Clean(name);
            this.options = opts;
        }
        else
            throw new IllegalArgumentException();
    }


    public abstract VertexArray.Type getGlyphVectorType();

    public final boolean alive(){
        return (null != this.list);
    }
    public void destroy(){
        fv3.font.Glyph[] glyphs = this.list;
        if (null != glyphs){
            this.list = null;
            for (fv3.font.Glyph gly : glyphs){
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
    public final int getLength(){
        fv3.font.Glyph[] list = this.list;
        if (null == list)
            return 0;
        else
            return list.length;
    }
    public final Glyph get(int idx){
        fv3.font.Glyph[] list = this.list;
        if (null == list)
            return null;

        else if (-1 < idx && idx < list.length)
            return (Glyph)list[idx];
        else
            return null;
    }
    public final Glyph clone(int idx){
        return (Glyph)this.get(idx).clone();
    }
    public abstract Glyph get(char ch);

    public abstract Glyph clone(char ch);

    public abstract double getEm();

    public abstract double getAscent();

    public abstract double getDescent();

    public abstract double getLeading();

    public abstract double spacing(char previous, char next);

    public final GlyphVector toString(String string){
        return new GlyphVector(this,string);
    }

    protected final Font add(Glyph glyph){
        if (null != glyph){

            fv3.font.Glyph[] list = this.list;
            if (null == list)
                this.list = new fv3.font.Glyph[]{glyph};
            else {
                int len = list.length;
                fv3.font.Glyph[] copier = new fv3.font.Glyph[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = glyph;
                this.list = copier;
            }
        }
        return this;
    }
    public Font clone(){
        try {
            Font clone = (Font)super.clone();
            if (null != this.list)
                clone.list = clone.list.clone();
            return clone;
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
    public java.util.Iterator<Glyph> iterator(){
        return new fv3.font.Glyph.Iterator<Glyph>(this.list);
    }
    private final static String Clean(String name){
        if (null != name){
            int idx = name.lastIndexOf('.');
            if (-1 != idx)
                name = name.substring(0,idx);
        }
        return name;
    }
}
