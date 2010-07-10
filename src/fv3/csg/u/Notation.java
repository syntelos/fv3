/*
 * Fv3 CSG
 * Copyright (C) 2010  John Pritchard, jdp@syntelos.org
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

public interface Notation
    extends fv3.math.Notation
{
    /**
     * State of components 
     */
    public abstract static class State
    {

        public enum Vertex {
            Unknown, Inside, Outside, Boundary, Superfluous;

            public final static State.Face ToFace(State.Vertex state){
                switch (state){
                case Unknown:
                    return State.Face.Unknown;
                case Inside:
                    return State.Face.Inside;
                case Outside:
                    return State.Face.Outside;
                case Boundary:
                    return State.Face.Same;
                case Superfluous:
                    return State.Face.Opposite;
                default:
                    throw new IllegalArgumentException(state.toString());
                }
            }
            public String toString(){
                return String.format("%11s",this.name());
            }
        }
        public enum Face {
            Unknown, Inside, Outside, Same, Opposite;

            public final static State.Vertex ToVertex(State.Face state){
                switch (state){
                case Unknown:
                    return State.Vertex.Unknown;
                case Inside:
                    return State.Vertex.Inside;
                case Outside:
                    return State.Vertex.Outside;
                case Same:
                    return State.Vertex.Boundary;
                case Opposite:
                    return State.Vertex.Superfluous;
                default:
                    throw new IllegalArgumentException(state.toString());
                }
            }
            public String toString(){
                return String.format("%8s",this.name());
            }
        }

    }
}
