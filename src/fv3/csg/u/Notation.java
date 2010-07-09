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

import lxl.List;
import lxl.Map;

public interface Notation
    extends fv3.math.Notation
{
    /**
     * Overloaded name "State" serves two roles: state of solid
     * datastructure and state of components namespace.
     */
    public final static class State
        extends lxl.ArrayList<fv3.csg.u.Face>
        implements java.util.Comparator<fv3.csg.u.Face>
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
        }


        public Map<fv3.csg.u.Vertex,fv3.csg.u.Vertex> vertices;

        private Bound bound;

        private State prev;


        public State(int v){
            super();
            super.setComparator(this);
            this.vertices = new Map<fv3.csg.u.Vertex,fv3.csg.u.Vertex>(v);
        }


        public int countVertices(){
            return (3*this.size());
        }
        public Bound getBound(){
            Bound bound = this.bound;
            if (null == bound){
                bound = new Bound(this);
                this.bound = bound;
            }
            return bound;
        }
        public State push(){
            if (null == this.prev){
                State clone = (State)super.clone();
                clone.vertices = this.vertices.clone();
                clone.prev = this;
                return clone;
            }
            else
                throw new IllegalStateException();
        }
        public State pop(){
            State prev = this.prev;
            if (null != prev){
                super.clear();
                this.vertices.clear();
                return prev;
            }
            else
                throw new IllegalStateException();
        }
        public void destroy(){
            super.clear();
            this.vertices.clear();
        }
        public boolean equals(Object that){
            return (this == that);
        }
        public int compare(fv3.csg.u.Face a, fv3.csg.u.Face b){
            return a.compareTo(b);
        }
    }
}
