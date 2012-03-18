/*
 * fv3 CSG
 * Copyright (C) 2012  John Pritchard, all rights reserved.
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
package fv3.csg.u;

/** 
 * Common terms
 * @see fv3.math.Notation
 */
public interface Notation
    extends fv3.math.Notation
{

    public enum State {

        Unknown, Inside, Outside, Boundary;

        public boolean isInsideOrOutside(){
            switch (this){
            case Inside:
            case Outside:
                return true;
            default:
                return false;
            }
        }
        public String toString(){
            return String.format("%11s",this.name());
        }
        public final static State Invert(State state){
            switch (state){
            case Inside:
                return Outside;
            case Outside:
                return Inside;
            default:
                return Unknown;
            }
        }
        public final static State Classify(int s){
            switch (s){
            case -1:
                return Inside;
            case 0:
                return Boundary;
            case 1:
                return Outside;
            default:
                throw new IllegalArgumentException(String.valueOf(s));
            }
        }
    }
}
