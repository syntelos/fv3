/*
 * fv3.ds
 * Copyright (c) 2009 John Pritchard, all rights reserved.
 * Copyright (C) 1996-2008 by Jan Eric Kyprianidis, all rights reserved.
 *     
 * This program is free  software: you can redistribute it and/or modify 
 * it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program  is  distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU Lesser General Public License for more details.
 * 
 * You should  have received a copy of the GNU Lesser General Public License
 * along with  this program; If not, see <http://www.gnu.org/licenses/>. 
 */
/*
 * Based on the work of Jan Eric Kyprianidis,  Martin van Velsen, Robin
 * Feroq, Jimm Pitts, Mats Byggmästar, and Josh DeFord.
 */
package fv3.ds;

import fv3.Fv3Exception;

/**
 * Animation frame nodes.
 */
public abstract class Node
    extends Object
    implements Comparable
{

    public static enum Flags {

        HIDDEN(0x000800),
        SHOW_PATH(0x010000),
        SMOOTHING(0x020000),
        MOTION_BLUR(0x100000),
        MORPH_MATERIALS(0x400000);

        public final int flag; 

        private Flags(int flag){
            this.flag = flag;
        }
    }
    public static enum Type {

        AMBIENT_COLOR("Ambient",0),
        MESH_INSTANCE("Mesh",1),
        CAMERA("Camera",2),
        CAMERA_TARGET("Camera Target",3),
        OMNILIGHT("Omnilight",4),
        SPOTLIGHT("Spotlight",5),
        SPOTLIGHT_TARGET("Spotlight Target",6);

        public final String label;
        public final int type;

        private Type(String label, int type){
            this.label = label;
            this.type = type;
        }
    }
    public static class List {

        public final static Node[] Empty = {};

        public final static Node[] Add(Node[] list, Node item){
            if (null != item){
                if (null == list || 0 == list.length)
                    return new Node[]{item};
                else {
                    int len = list.length;
                    Node[] copier = new Node[len+1];
                    System.arraycopy(list,0,copier,0,len);
                    copier[len] = item;
                    return copier;
                }
            }
            else
                return list;
        }
    }


    public final Type type;
    public int        user_id = 65535;
    public Object     user_ptr;
    public Node       next;
    public Node       childs;
    public Node       parent;
    public int        node_id = 65535;
    public String     name;
    public int        flags;
    public float[][]  matrix = new float[4][4];


    protected Node(Type type){
        super();
        if (null != type)
            this.type = type;
        else
            throw new Fv3Exception();
    }


    public final void read (Model model, Reader r, Chunk cp)
        throws Fv3Exception
    {
        while (cp.in()){
            Chunk cp1 = r.next(cp);
            switch (cp1.id){
            case Chunk.NODE_ID:
                this.node_id = r.readU16(cp1);
                break;
            case Chunk.NODE_HDR:
                this.name = r.readString(cp1);
                this.flags = r.readU16(cp1);
                this.flags |= (r.readU16(cp1) << 16);
                this.user_id = r.readU16(cp1);
                break;
            case Chunk.PIVOT:
                if (this.type == Node.Type.MESH_INSTANCE) {
                    MeshInstanceNode n = (MeshInstanceNode)this;
                    r.readVector(cp1, n.pivot);
                } 
                break;
            case Chunk.INSTANCE_NAME:
                if (this.type == Node.Type.MESH_INSTANCE) {
                    MeshInstanceNode n = (MeshInstanceNode)this;
                    n.instance_name = r.readString(cp1);
                }
                break;
            case Chunk.BOUNDBOX:
                if (this.type == Node.Type.MESH_INSTANCE) {
                    MeshInstanceNode n = (MeshInstanceNode)this;
                    r.readVector(cp1, n.bbox_min);
                    r.readVector(cp1, n.bbox_max);
                }
                break;
            case Chunk.COL_TRACK_TAG: {
                Track track = null;
                switch (this.type) {
                case AMBIENT_COLOR: {
                    AmbientColorNode n = (AmbientColorNode)this;
                    track = n.color_track;
                    break;
                }              
                case OMNILIGHT: {
                    OmnilightNode n = (OmnilightNode)this;
                    track = n.color_track;
                    break;
                }
                case SPOTLIGHT: {
                    SpotlightNode n = (SpotlightNode)this;
                    track = n.color_track;
                    break;
                }
                }
                if (null != track)
                    track.read(model,r,cp1);
                break;
            }
            case Chunk.POS_TRACK_TAG: {
                Track track = null;
                switch (this.type) {
                case MESH_INSTANCE: {
                    MeshInstanceNode n = (MeshInstanceNode)this;
                    track = n.pos_track;
                    break;
                }
                case CAMERA: {
                    CameraNode n = (CameraNode)this;
                    track = n.pos_track;
                    break;
                }
                case CAMERA_TARGET: {
                    TargetNode n = (TargetNode)this;
                    track = n.pos_track;
                    break;
                }
                case OMNILIGHT: {
                    OmnilightNode n = (OmnilightNode)this;
                    track = n.pos_track;
                    break;
                }
                case SPOTLIGHT: {
                    SpotlightNode n = (SpotlightNode)this;
                    track = n.pos_track;
                    break;
                }
                case SPOTLIGHT_TARGET: {
                    TargetNode n = (TargetNode)this;
                    track = n.pos_track;
                    break;
                }
                }
                if (null != track)
                    track.read(model,r,cp1);
                break;
            }
            case Chunk.ROT_TRACK_TAG:
                if (this.type == Node.Type.MESH_INSTANCE) {
                    MeshInstanceNode n = (MeshInstanceNode)this;
                    n.rot_track.read(model,r,cp1);
                } 
                break;
            case Chunk.SCL_TRACK_TAG:
                if (this.type == Node.Type.MESH_INSTANCE) {
                    MeshInstanceNode n = (MeshInstanceNode)this;
                    n.scl_track.read(model,r,cp1);
                } 
                break;
            case Chunk.FOV_TRACK_TAG:
                if (this.type == Node.Type.CAMERA) {
                    CameraNode n = (CameraNode)this;
                    n.fov_track.read(model,r,cp1);
                }
                break;
            case Chunk.HOT_TRACK_TAG:
                if (this.type == Node.Type.SPOTLIGHT) {
                    SpotlightNode n = (SpotlightNode)this;
                    n.hotspot_track.read(model,r,cp1);
                }
                break;
            case Chunk.FALL_TRACK_TAG:
                if (this.type == Node.Type.SPOTLIGHT) {
                    SpotlightNode n = (SpotlightNode)this;
                    n.falloff_track.read(model,r,cp1);
                }
                break;
            case Chunk.ROLL_TRACK_TAG:
                switch (this.type) {
                case CAMERA: {
                    CameraNode n = (CameraNode)this;
                    n.roll_track.read(model,r,cp1);
                    break;
                }
                case SPOTLIGHT: {
                    SpotlightNode n = (SpotlightNode)this;
                    n.roll_track.read(model,r,cp1);
                    break;
                }
                }
                break;
            case Chunk.HIDE_TRACK_TAG:
                if (this.type == Node.Type.MESH_INSTANCE) {
                    MeshInstanceNode n = (MeshInstanceNode)this;
                    n.hide_track.read(model,r,cp1);
                }
                break;
            case Chunk.MORPH_SMOOTH:
                if (this.type == Node.Type.MESH_INSTANCE) {
                    MeshInstanceNode n = (MeshInstanceNode)this;
                    n.morph_smooth = r.readFloat(cp1);
                }
                break;

                /*
                  case LIB3DS_MORPH_TRACK_TAG: {
                   if (this.type == Node.Type.MESH_INSTANCE) {
                    MeshInstanceNode n = (MeshInstanceNode)this;
                    n.morph_track = new Track(Track.Type.MORPH);
                    n.morph_track.read();
                   }
                  }
                  break;
                */
            }
        }
    }

    public final Node[] list(){
        return this.list(Node.List.Empty);
    }
    private final Node[] list(Node[] list){
        list = Node.List.Add(list,this);
        if (null != this.next)
            return this.next.list(list);
        else
            return list;
    }
    public final Node[] list(Node.Type type){
        return this.list(Node.List.Empty,type);
    }
    private final Node[] list(Node[] list, Node.Type type){
        if (type == this.type)
            list = Node.List.Add(list,this);
        if (null != this.next)
            return this.next.list(list,type);
        else
            return list;
    }
    public final Node first(Node.Type type){
        if (type == this.type)
            return this;
        else if (null != this.next)
            return this.next.first(type);
        else
            return null;
    }
    public final int compareTo(Object arg){
        if (arg instanceof Node){
            Node that = (Node)arg;
            int thisId = this.node_id;
            int thatId = that.node_id;
            if (thisId == thatId)
                return 0;
            else if (thisId < thatId)
                return -1;
            else
                return +1;
        }
        else {
            int thisId = this.node_id;
            int thatId = ((Number)arg).intValue();
            if (thisId == thatId)
                return 0;
            else if (thisId < thatId)
                return -1;
            else
                return +1;
        }
    }
}
