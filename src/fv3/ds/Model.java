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
package fv3.ds;

import fv3.Component;
import fv3.Fv3Exception;
import fv3.math.AxisAngle;
import fv3.math.Matrix;
import fv3.math.Vector;

import java.io.IOException;
import java.net.URL;
import java.nio.DoubleBuffer;
import java.util.Arrays;

import javax.media.opengl.GL2;


/**
 * 
 */
public class Model
    extends fv3.net.Model
    implements fv3.Bounds
{
    public String name;
    public Mesh[] mesh = new Mesh[0];
    public Camera[] camera = new Camera[0];
    public Material[] material = new Material[0];
    public Light[] light = new Light[0];
    public double[] constructionPlane = new double[3];
    public double[] ambient = new double[3];
    public int meshVersion;
    public double masterScale;
    public int segmentFrom, segmentTo;
    public int keyfRevision;
    public int frames, currentFrame;
    public Node nodes, last;
    public Background background;
    public Atmosphere atmosphere;
    public Viewport viewport;
    public Shadow shadow;

    private volatile double minX, maxX, minY, maxY, minZ, maxZ, midX, midY, midZ;

    private volatile boolean inited = true;

    private volatile int lid;


    public Model(URL source)
        throws IOException, Fv3Exception
    {
        super(source,false);
        this.download();
        Reader r = new Reader(this.target());
        this.name = r.name;
        try {
            this.read(r);
        }
        finally {
            r.close();
        }
        this.bounds();
    }
    public Model(String codebase, String path)
        throws IOException, Fv3Exception
    {
        super(codebase,path,false);
        this.download();
        Reader r = new Reader(this.target());
        this.name = r.name;
        try {
            this.read(r);
        }
        finally {
            r.close();
        }
        this.bounds();
    }
    public Model(String url)
        throws IOException, Fv3Exception
    {
        super(url,false);
        this.download();
        Reader r = new Reader(this.target());
        this.name = r.name;
        try {
            this.read(r);
        }
        finally {
            r.close();
        }
        this.bounds();
    }


    private void bounds(){
        Mesh[] mesh = this.mesh;
        double minX = 0.0, minY = 0.0, maxX = 0.0, maxY = 0.0, minZ = 0.0, maxZ = 0.0;
        for (int cc = 0, count = mesh.length; cc < count; ++cc) {
            Mesh m = mesh[cc];
            if (0 == cc){
                minX = m.minX;
                maxX = m.maxX;
                minY = m.minY;
                maxY = m.maxY;
                minZ = m.minZ;
                maxZ = m.maxZ;
            }
            else {
                minX = Math.min(minX, m.minX);
                maxX = Math.max(maxX, m.maxX);
                minY = Math.min(minY, m.minY);
                maxY = Math.max(maxY, m.maxY);
                minZ = Math.min(minZ, m.minZ);
                maxZ = Math.max(maxZ, m.maxZ);
            }
        }
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;

        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;

        this.midX = (minX + maxX)/2.0;
        this.midY = (minY + maxY)/2.0;
        this.midZ = (minZ + maxZ)/2.0;
    }
    public int getGlListCount(){
        if (this.inited)
            return 1;
        else
            return 0;
    }
    public int getGlListId(int idx)
        throws java.lang.ArrayIndexOutOfBoundsException
    {
        if (this.inited && 0 == idx)
            return this.lid;
        else
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    public final boolean hasFv3Bounds(){
        return true;
    }
    public final boolean hasNotFv3Bounds(){
        return false;
    }
    public final fv3.Bounds getFv3Bounds(){
        return this;
    }
    public double getBoundsMinX(){
        return this.minX;
    }
    public double getBoundsMidX(){
        return this.midX;
    }
    public double getBoundsMaxX(){
        return this.maxX;
    }
    public double getBoundsMinY(){
        return this.minY;
    }
    public double getBoundsMidY(){
        return this.midY;
    }
    public double getBoundsMaxY(){
        return this.maxY;
    }
    public double getBoundsMinZ(){
        return this.minZ;
    }
    public double getBoundsMidZ(){
        return this.midZ;
    }
    public double getBoundsMaxZ(){
        return this.maxZ;
    }
    protected void add(Mesh mesh) {
        if (null != mesh){
            Mesh[] list = this.mesh;
            if (0 == list.length)
                this.mesh = new Mesh[]{mesh};
            else {
                int len = list.length;
                Mesh[] copier = new Mesh[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = mesh;
                this.mesh = copier;
            }
        }
    }
    protected void add(Camera camera) {
        if (null != camera){
            Camera[] list = this.camera;
            if (0 == list.length)
                this.camera = new Camera[]{camera};
            else {
                int len = list.length;
                Camera[] copier = new Camera[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = camera;
                this.camera = copier;
            }
        }
    }
    protected void add(Material material) {
        if (null != material){
            Material[] list = this.material;
            if (0 == list.length)
                this.material = new Material[]{material};
            else {
                int len = list.length;
                Material[] copier = new Material[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = material;
                this.material = copier;
            }
        }
    }
    protected void add(Light light) {
        if (null != light){
            Light[] list = this.light;
            if (0 == list.length)
                this.light = new Light[]{light};
            else {
                int len = list.length;
                Light[] copier = new Light[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = light;
                this.light = copier;
            }
        }
    }
    public int countMesh(){
        return this.mesh.length;
    }
    public boolean hasMesh(int idx){
        return (-1 < idx && idx < this.mesh.length);
    }
    public Mesh getMesh(int idx){
        Mesh[] mesh = this.mesh;
        if (-1 < idx && idx < mesh.length)
            return mesh[idx];
        else
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx)+'/'+String.valueOf(mesh.length));
    }
    public Mesh firstMesh(){
        Mesh[] mesh = this.mesh;
        if (0 != mesh.length)
            return mesh[0];
        else
            return null;
    }
    public Mesh getMeshForName(String name){
        for (int cc = 0, count = this.mesh.length; cc < count; cc++) {
            Mesh mesh = this.mesh[cc];
            if (name.equals(mesh.name))
                return mesh;
        }
        return null;
    }
    public int indexOfMeshForName(String name){
        for (int cc = 0, count = this.mesh.length; cc < count; cc++) {
            Mesh mesh = this.mesh[cc];
            if (name.equals(mesh.name))
                return cc;
        }
        return -1;
    }
    public int countCamera(){
        return this.camera.length;
    }
    public boolean hasCamera(int idx){
        return (-1 < idx && idx < this.camera.length);
    }
    public Camera getCamera(int idx){
        Camera[] camera = this.camera;
        if (-1 < idx && idx < camera.length)
            return camera[idx];
        else
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx)+'/'+String.valueOf(camera.length));
    }
    public Camera firstCamera(){
        Camera[] camera = this.camera;
        if (0 != camera.length)
            return camera[0];
        else
            return null;
    }
    public Camera getCameraForName(String name){
        for (int cc = 0, count = this.camera.length; cc < count; cc++) {
            Camera camera = this.camera[cc];
            if (name.equals(camera.name))
                return camera;
        }
        return null;
    }
    public int indexOfCameraForName(String name){
        for (int cc = 0, count = this.camera.length; cc < count; cc++) {
            Camera camera = this.camera[cc];
            if (name.equals(camera.name))
                return cc;
        }
        return -1;
    }
    public int countMaterial(){
        return this.material.length;
    }
    public boolean hasMaterial(int idx){
        return (-1 < idx && idx < this.material.length);
    }
    public Material getMaterial(int idx){
        Material[] material = this.material;
        if (-1 < idx && idx < material.length)
            return material[idx];
        else
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx)+'/'+String.valueOf(material.length));
    }
    public Material firstMaterial(){
        Material[] material = this.material;
        if (0 != material.length)
            return material[0];
        else
            return null;
    }
    public Material getMaterialForName(String name){
        for (int cc = 0, count = this.material.length; cc < count; cc++) {
            Material material = this.material[cc];
            if (name.equals(material.name))
                return material;
        }
        return null;
    }
    public int indexOfMaterialForName(String name){
        for (int cc = 0, count = this.material.length; cc < count; cc++) {
            Material material = this.material[cc];
            if (name.equals(material.name))
                return cc;
        }
        return -1;
    }
    public int countLight(){
        return this.light.length;
    }
    public boolean hasLight(int idx){
        return (-1 < idx && idx < this.light.length);
    }
    public Light getLight(int idx){
        Light[] light = this.light;
        if (-1 < idx && idx < light.length)
            return light[idx];
        else
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx)+'/'+String.valueOf(light.length));
    }
    public Light firstLight(){
        Light[] light = this.light;
        if (0 != light.length)
            return light[0];
        else
            return null;
    }
    public Light getLightForName(String name){
        for (int cc = 0, count = this.light.length; cc < count; cc++) {
            Light light = this.light[cc];
            if (name.equals(light.name))
                return light;
        }
        return null;
    }
    public int indexOfLightForName(String name){
        for (int cc = 0, count = this.light.length; cc < count; cc++) {
            Light light = this.light[cc];
            if (name.equals(light.name))
                return cc;
        }
        return -1;
    }
    public Node[] listNodes(){
        if (null != this.nodes)
            return this.nodes.list();
        else
            return Node.List.Empty;
    }
    public Node[] listNodes(Node.Type type){
        if (null != this.nodes)
            return this.nodes.list(type);
        else
            return Node.List.Empty;
    }
    public Node firstNode(Node.Type type){
        if (null != this.nodes)
            return this.nodes.first(type);
        else
            return null;
    }

    public void read(Reader r) throws Fv3Exception {
        Chunk cp0 = r.start();
        if (Chunk.M3DMAGIC == cp0.id){
            while (cp0.in()){
                Chunk cp1 = r.next(cp0);
                switch (cp1.id) {
                case Chunk.MDATA:
                    while (cp1.in()) {
                        Chunk cp2 = r.next(cp1);
                        switch (cp2.id) {
                        case Chunk.MESH_VERSION: {
                            this.meshVersion = r.readS32(cp2);
                            break;
                        }
                        case Chunk.MASTER_SCALE: {
                            this.masterScale = r.readFloat(cp2);
                            break;
                        }
                        case Chunk.SHADOW_MAP_SIZE:
                        case Chunk.LO_SHADOW_BIAS:
                        case Chunk.HI_SHADOW_BIAS:
                        case Chunk.SHADOW_SAMPLES:
                        case Chunk.SHADOW_RANGE:
                        case Chunk.SHADOW_FILTER:
                        case Chunk.RAY_BIAS: {
                            this.shadow = new Shadow(this,r,cp2);
                            break;
                        }
                        case Chunk.VIEWPORT_LAYOUT:
                        case Chunk.DEFAULT_VIEW: {
                            this.viewport = new Viewport(this,r,cp2);
                            break;
                        }
                        case Chunk.O_CONSTS: {
                            r.readVector(cp2,this.constructionPlane);
                            break;
                        }
                        case Chunk.AMBIENT_LIGHT: {
                            boolean lin = false;
                            while (cp2.in()){
                                Chunk cp3 = r.next(cp2);
                                switch (cp3.id){
                                case Chunk.LIN_COLOR_F:{
                                    lin = true;
                                    r.readVector(cp3,this.ambient);
                                }
                                case Chunk.COLOR_F:{
                                    if (!lin)
                                        r.readVector(cp3,this.ambient);
                                }
                                }
                            }
                            break;
                        }
                        case Chunk.BIT_MAP:
                        case Chunk.SOLID_BGND:
                        case Chunk.V_GRADIENT:
                        case Chunk.USE_BIT_MAP:
                        case Chunk.USE_SOLID_BGND:
                        case Chunk.USE_V_GRADIENT: {
                            this.background = new Background(this,r,cp2);
                            break;
                        }
                        case Chunk.FOG:
                        case Chunk.LAYER_FOG:
                        case Chunk.DISTANCE_CUE:
                        case Chunk.USE_FOG:
                        case Chunk.USE_LAYER_FOG:
                        case Chunk.USE_DISTANCE_CUE: {
                            this.atmosphere = new Atmosphere(this,r,cp2);
                            break;
                        }
                        case Chunk.MAT_ENTRY:
                            this.add(new Material(this,r,cp2));
                            break;
                        case Chunk.NAMED_OBJECT:{
                            String name = r.readString(cp2);
                            while (cp2.in()) {
                                Chunk cp3 = r.next(cp2);
                                switch (cp3.id) {
                                case Chunk.N_TRI_OBJECT:
                                    this.add(new Mesh(this,r,cp3,name));
                                    break;
                                case Chunk.N_CAMERA:
                                    this.add(new Camera(this,r,cp3,name));
                                    break;
                                }
                            }
                            break;
                        }
                        }
                    }
                    break;
                case Chunk.KFDATA:{
                    int numNodes = 0;

                    while (cp1.in()) {
                        Chunk cp2 = r.next(cp1);
                        switch (cp2.id) {
                        case Chunk.KFHDR: {
                            this.keyfRevision = r.readU16(cp2);
                            this.name = r.readString(cp2);
                            this.frames = r.readS32(cp2);
                            break;
                        }
                        case Chunk.KFSEG: {
                            this.segmentFrom = r.readS32(cp2);
                            this.segmentTo = r.readS32(cp2);
                            break;
                        }
                        case Chunk.KFCURTIME: {
                            this.currentFrame = r.readS32(cp2);
                            break;
                        }
                        case Chunk.VIEWPORT_LAYOUT:
                        case Chunk.DEFAULT_VIEW: {
                            this.viewport = new Viewport(this,r,cp2);
                            break;
                        }
                        case Chunk.AMBIENT_NODE_TAG: 
                        case Chunk.OBJECT_NODE_TAG: 
                        case Chunk.CAMERA_NODE_TAG: 
                        case Chunk.TARGET_NODE_TAG: 
                        case Chunk.LIGHT_NODE_TAG: 
                        case Chunk.SPOTLIGHT_NODE_TAG: 
                        case Chunk.L_TARGET_NODE_TAG: {

                            Node node = null;
                            switch (cp2.id) {
                            case Chunk.AMBIENT_NODE_TAG: 
                                node = new AmbientColorNode();
                                break;
                            case Chunk.OBJECT_NODE_TAG: 
                                node = new MeshInstanceNode();
                                break;
                            case Chunk.CAMERA_NODE_TAG: 
                                node = new CameraNode();
                                break;
                            case Chunk.TARGET_NODE_TAG: 
                                node = new TargetNode(Node.Type.CAMERA_TARGET);
                                break;
                            case Chunk.LIGHT_NODE_TAG: 
                                node = new OmnilightNode();
                                break;
                            case Chunk.SPOTLIGHT_NODE_TAG: 
                                node = new SpotlightNode();
                                break;
                            case Chunk.L_TARGET_NODE_TAG:
                                node = new TargetNode(Node.Type.SPOTLIGHT_TARGET);
                                break;
                            default:
                                throw new Fv3Exception();
                            }
                            node.node_id = numNodes++;

                            if (null != this.last) {
                                this.last.next = node;
                            } else {
                                this.nodes = node;
                            }
                            node.user_ptr = this.last;
                            this.last = node;

                            node.read(this,r,cp2);
                            break;
                        }
                        }
                    }
                    /*
                     */
                    Node q, p, parent, nodes[] = this.listNodes();
                    int parentIdx;
                    Arrays.sort(nodes);
                    p = this.last;
                    while (null != p) {
                        q = (Node)p.user_ptr;
                        if (p.user_id != 65535) {
                            parentIdx = Arrays.binarySearch(nodes,p.user_id);
                            if (-1 < parentIdx) {
                                parent = nodes[parentIdx];
                                q.next = p.next;
                                p.next = parent.childs;
                                p.parent = parent;
                                parent.childs = p;
                            } 
                        }
                        p.user_id = 0;
                        p.user_ptr = null;
                        p = q;
                    }
                    break;
                }
                }
            }
        }
        else
            throw new Fv3Exception("Bad header (magic) not a 3DS file, '"+r.name+"'.");
    }

}
