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
public class Glyph<Font extends fv3.font.Font, Path extends fv3.font.Path>
    extends Object
    implements Iterable<Path>
{

    protected Font font;

    protected fv3.font.Path[] list;

    protected double[] points;


    protected Glyph(Font font){
        super();
        if (null != font)
            this.font = font;
        else
            throw new IllegalArgumentException();
    }


    public void destroy(){
        this.font = null;
        fv3.font.Path[] paths = this.list;
        if (null != paths){
            this.list = null;
            for (fv3.font.Path p : paths){
                p.destroy();
            }
        }
    }
    public void init(FontOptions opts) {
        if (null != opts){
            Font font = this.font;
            if (null != font){
                for (Path p: this){
                    p.init(font,this,opts);
                }
            }
            else 
                throw new IllegalStateException("Destroyed");
        }
        else
            throw new IllegalArgumentException();
    }
    public final double[] points(){
        double[] points = this.points;
        if (null == points){
            fv3.font.Path[] paths = this.list;
            if (null != paths){
                fv3.font.Path path;
                double[] pointset;
                for (int cc = 0, count = paths.length; cc < count; cc++){
                    path = paths[cc];
                    pointset = path.points();
                    if (null == pointset)
                        continue;
                    else if (null == points)
                        points = pointset;
                    else {
                        int pointsl = points.length;
                        int pointsly = (pointsl - 1);
                        int pointslx = (pointsly - 1);
                        if (points[pointslx] == pointset[0] &&
                            points[pointsly] == pointset[1])
                        {
                            int pslen = (pointset.length - 2);
                            double[] copier = new double[pointsl + pslen];
                            System.arraycopy(points,0,copier,0,pointsl);
                            System.arraycopy(pointset,2,copier,pointsl,pslen);
                            points = copier;
                        }
                        else {
                            int pslen = (pointset.length);
                            double[] copier = new double[pointsl + pslen];
                            System.arraycopy(points,0,copier,0,pointsl);
                            System.arraycopy(pointset,0,copier,pointsl,pslen);
                            points = copier;
                        }
                    }
                }
                this.points = points;
            }
        }
        return points;
    }
    public final Font getFont() {
        return this.font;
    }
    public final int getLength(){
        fv3.font.Path[] list = this.list;
        if (null == list)
            return 0;
        else
            return list.length;
    }
    public final Path get(int idx){
        fv3.font.Path[] list = this.list;
        if (null == list)
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx));

        else if (-1 < idx && idx < list.length)
            return (Path)list[idx];
        else
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    public final Path first(){
        fv3.font.Path[] list = this.list;
        if (null == list)
            return null;
        else
            return (Path)list[0];
    }
    public final Path last(){
        fv3.font.Path[] list = this.list;
        if (null == list)
            return null;
        else
            return (Path)list[list.length-1];
    }
    protected final void add(Path path){
        if (null != path){
            fv3.font.Path[] list = this.list;
            if (null == list)
                this.list = new fv3.font.Path[]{path};
            else {
                int len = list.length;
                fv3.font.Path[] copier = new fv3.font.Path[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = path;
                this.list = copier;
            }
        }
    }
    public String toString(){
        return this.toString("; ");
    }
    public String toString(String infix){
        return this.toString("Glyph(","; ",")");
    }
    public final String toString(String prefix, String infix, String suffix){
        StringBuilder string = new StringBuilder();
        if (null != prefix)
            string.append(prefix);
        fv3.font.Path[] list = this.list;
        if (null != list){
            for (int cc = 0, count = list.length; cc < count; cc++){
                if (0 != cc)
                    string.append(infix);
                string.append(list[cc]);
            }
        }
        if (null != suffix)
            string.append(suffix);
        return string.toString();
    }
    public java.util.Iterator<Path> iterator(){
        return new fv3.font.Path.Iterator<Path>(this.list);
    }

    public final static class Iterator<Glyph extends fv3.font.Glyph>
        extends Object
        implements java.util.Iterator<Glyph>
    {

        private final fv3.font.Glyph[] list;
        private final int count;
        private int index;

        public Iterator(fv3.font.Glyph[] list){
            super();
            this.list = list;
            this.count = ((null != list)?(list.length):(0));
        }

        public boolean hasNext(){
            return (this.index < this.count);
        }
        public Glyph next(){
            if (this.index < this.count)
                return (Glyph)this.list[this.index++];
            else
                throw new java.util.NoSuchElementException();
        }
        public void remove(){
            throw new UnsupportedOperationException();
        }
    }
}
