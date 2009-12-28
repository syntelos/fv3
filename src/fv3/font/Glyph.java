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
 * This class may be subclassed with a {@link Font} subclass as for
 * implementing a text editor.
 * 
 * @author John Pritchard
 */
public class Glyph
    extends Object
{

    protected Font font;

    protected Path[] list;


    protected Glyph(Font font){
        super();
        this.font = font;
    }


    public void destroy(){
        this.font = null;
        Path[] paths = this.list;
        if (null != paths){
            this.list = null;
            for (Path p : paths){
                p.destroy();
            }
        }
    }
    public void init(FontOptions opts) {

    }
    protected void read(FontReader reader){

    }
    public final Font getFont() {
        return this.font;
    }
    public final int getLength(){
        Path[] list = this.list;
        if (null == list)
            return 0;
        else
            return list.length;
    }
    public final Path get(int idx){
        Path[] list = this.list;
        if (null == list)
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx));

        else if (-1 < idx && idx < list.length)
            return list[idx];
        else
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    protected final void add(Path path){
        if (null != path){
            Path[] list = this.list;
            if (null == list)
                this.list = new Path[]{path};
            else {
                int len = list.length;
                Path[] copier = new Path[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = path;
                this.list = copier;
            }
        }
    }
}
