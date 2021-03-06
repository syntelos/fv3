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
 * Part of a {@link Glyph}.
 * 
 * @author John Pritchard
 */
public interface Path<Font extends fv3.font.Font, Glyph extends fv3.font.Glyph>
{

    public void destroy();

    public void init(Font font, Glyph glyph, FontOptions opts);


    public final static class Iterator<Path extends fv3.font.Path>
        extends Object
        implements java.util.Iterator<Path>
    {

        private final fv3.font.Path[] list;
        private final int count;
        private int index;

        public Iterator(fv3.font.Path[] list){
            super();
            this.list = list;
            this.count = ((null != list)?(list.length):(0));
        }

        public boolean hasNext(){
            return (this.index < this.count);
        }
        public Path next(){
            if (this.index < this.count)
                return (Path)this.list[this.index++];
            else
                throw new java.util.NoSuchElementException();
        }
        public void remove(){
            throw new UnsupportedOperationException();
        }
    }
}
