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
package fv3;

/**
 * 
 */
public interface Model
    extends Component
{
    /**
     * 
     */
    public interface Element
        extends fv3.math.Notation
    {
        /**
         * 
         */
        public final static class Iterator
            extends java.lang.Object
            implements java.util.Iterator<Element>
        {

            public final int length;

            private final Element[] list;

            private int index;

            public Iterator(Element[] list){
                super();
                if (null == list){
                    this.list = null;
                    this.length = 0;
                }
                else {
                    this.list = list.clone();
                    this.length = this.list.length;
                }
            }

            public boolean hasNext(){
                return (this.index < this.length);
            }
            public Element next(){
                return this.list[this.index++];
            }
            public void remove(){
                throw new UnsupportedOperationException();
            }
        }
    }


    public Element get(int idx);

    public Model add(Element object);

    public Model add(Element[] list);

}
