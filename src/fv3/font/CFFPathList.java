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
 * A list of paths.
 * Together these part compose a glyph.
 * @author Tim Tyler
 * @author John Pritchard
 */
public final class CFFPathList
    extends Object
{
    private static final int INCREMENT = 8;


    private int number = 0;
    private int number_of_paths = 0;
    private CFFPath[] paths = new CFFPath[number_of_paths];


    public CFFPathList(){
        super();
    }


    public boolean alive(){
        return (null != this.paths);
    }
    public void destroy(){
        this.paths = null;
    }
    public CFFPath add(CFFPath fep) {
        if (number >= number_of_paths) {
            CFFPath[] new_array = new CFFPath[number_of_paths + INCREMENT];
            System.arraycopy(paths, 0, new_array, 0, number_of_paths);
            paths = new_array;
            number_of_paths += INCREMENT;
        }
        paths[number] = fep;
        return paths[number++];
    }
    public CFFPath getPath(CFFPoint p) {
        for (int i = number; --i >= 0;) {
            CFFPath fep = paths[i];

            if (fep.contains(p)) {
                return fep;
            }
        }
        return null;
    }
    public void dump() {
        System.out.printf("Number of paths: %d\n", number);
        for (int i = number; --i >= 0;) {
            CFFPath fep = paths[i];

            fep.dump();
        }
        System.out.println("--- END ---");
    }
    public int getNumber() {
        return number;
    }
    public void setPaths(CFFPath[] paths) {
        this.paths = paths;
    }

    public CFFPath getPath(int i) {
        return paths[i];
    }
}
