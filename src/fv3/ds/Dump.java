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

import fv3.ds.* ;

import java.io.File;

/**
 * A clone of 3dsdump for a comparison tool between lib3ds and fv3.ds.
 */
public class Dump
    extends java.io.PrintStream
{
    private static void help(){
        System.err.print("FV3DS Dump\n"+
                         "Copyright (C) 2009 by John Pritchard\n"+
                         "Copyright (C) 1996-2007 by Jan Eric Kyprianidis\n"+
                         "All rights reserved.\n"+
                         "\n"+
                         "Syntax: Dump [options] filename [options]\n"+
                         "\n"+
                         "Options:\n"+
                         "  -h           This help\n"+
                         "  -d=level     Set log level (0=ERROR, 1=WARN, 2=INFO, 3=DEBUG)\n"+
                         "  -m           Dump materials\n"+
                         "  -t           Dump trimeshes\n"+
                         "  -i           Dump instances\n"+
                         "  -c           Dump cameras\n"+
                         "  -l           Dump lights\n"+
                         "  -n           Dump node hierarchy\n"+
                         "\n");
        System.exit(1);
    }
    private final static int DUMP_MATERIALS  = 0x0004;
    private final static int DUMP_TRIMESHES  = 0x0008;
    private final static int DUMP_INSTANCES  = 0x0010;
    private final static int DUMP_CAMERAS    = 0x0020;
    private final static int DUMP_LIGHTS     = 0x0040;
    private final static int DUMP_NODES      = 0x0080;

    private final static int LOG_ERROR     = 0;
    private final static int LOG_WARN      = 1;
    private final static int LOG_INFO      = 2;
    private final static int LOG_DEBUG     = 3;

    private final static char[][] argv(String[] args){
        int len = args.length;
        char[][] re = new char[len][];
        for (int cc = 0; cc < len; cc++){
            re[cc] = args[cc].toCharArray();
        }
        return re;
    }
    public static void main(String[] args) {
        char[][] argv = argv(args);
        int flags = 0;
        int log_level = LOG_INFO;
        String filename = null;
        
        for (int i = 0, argc = argv.length ; i < argc; ++i) {
            if (argv[i][0] == '-') {
                if ("-h".equals(args[i]) || "--help".equals(args[i])){
                    help();
                } else if ((argv[i][1] == 'd') && (argv[i][2] == '='))  {
                    log_level =  (argv[i][3] - '0');
                } else if (argv[i][1] == 'm') {
                    flags |= DUMP_MATERIALS;
                } else if (argv[i][1] == 't') {
                    flags |= DUMP_TRIMESHES;
                } else if (argv[i][1] == 'i') {
                    flags |= DUMP_INSTANCES;
                } else if (argv[i][1] == 'c') {
                    flags |= DUMP_CAMERAS;
                } else if (argv[i][1] == 'l') {
                    flags |= DUMP_LIGHTS;
                } else if (argv[i][1] == 'n') {
                    flags |= DUMP_NODES;
                } else {
                    help();
                }
            } else {
                if (null != filename) {
                    help();
                }
                else {
                    filename = args[i];
                }
            }
        }
        if (null == filename) {
            help();
        }
        else {
            try {
                Model model = new Model(filename);

                Dump dump = new Dump(System.out);

                if (0 != (flags & DUMP_MATERIALS)){
                    dump.printf("Dumping materials:\n");
                    for (int i = 0, z = model.material.length; i < z; ++i) 
                        dump.dump(model.material[i]);
                    dump.printf("\n");
                }
                if (0 != (flags & DUMP_TRIMESHES)){
                    dump.printf("Dumping meshes:\n");
                    for (int i = 0, z = model.mesh.length; i < z; ++i) 
                        dump.dump(model.mesh[i]);
                    dump.printf("\n");
                }
                if (0 != (flags & DUMP_INSTANCES)){
                    dump.printf("Dumping instances:\n");
                    for (Node p = model.nodes; null != p; p = p.next) {
                        dump.dump(p, "");
                    }
                    dump.printf("\n");
                }
                if (0 != (flags & DUMP_CAMERAS)){
                    dump.printf("Dumping cameras:\n");
                    for (int i = 0, z = model.camera.length; i < z; ++i) 
                        dump.dump(model.camera[i]);
                    dump.printf("\n");
                }
                if (0 != (flags & DUMP_LIGHTS)){
                    dump.printf("Dumping lights:\n");
                    for (int i = 0, z = model.light.length; i < z; ++i)
                        dump.dump(model.light[i]);
                    dump.printf("\n");
                }
                if (0 != (flags & DUMP_NODES)){
                    dump.printf("Dumping node hierarchy:\n");
                    for (Node p = model.nodes; null != p; p = p.next) {
                        dump.dump(p, 1);
                    }
                    dump.printf("\n");
                }

            }
            catch (Exception exc){
                exc.printStackTrace();
                System.exit(1);
            }
        }
    }


    private static String[] LevelSpace = {
    };
    public final static String Level(int level){
        int count = (2*level);
        if (count >= LevelSpace.length){
            int term = (count+1);
            StringBuilder strbuf = new StringBuilder();
            if (0 < LevelSpace.length)
                strbuf.append(LevelSpace[LevelSpace.length-1]);

            String[] copier = new String[term];
            for (int cc = LevelSpace.length; cc < term; cc++){
                strbuf.append(' ');
                copier[cc] = strbuf.toString();
            }
            LevelSpace = copier;
        }
        return LevelSpace[count];
    }


    public Dump(java.io.OutputStream os){
        super(os);
    }


    public void dump(float matrix[][]) {
        int i, j;
        for (i = 0; i < 4; ++i) {
            for (j = 0; j < 4; ++j) {
                printf("%f ", matrix[j][i]);
            }
            printf("\n");
        }
    }
    public void dump(Viewport vp) {

        printf("  viewport:\n");
        printf("    layout:\n");
        printf("      style:       %d\n", vp.layoutStyle);
        printf("      active:      %d\n", vp.layoutActive);
        printf("      swap:        %d\n", vp.layoutSwap);
        printf("      swap_prior:  %d\n", vp.layoutSwapPrior);
        printf("      position:    %d,%d\n", vp.layoutPosition[0], vp.layoutPosition[1]);
        printf("      size:        %d,%d\n", vp.layoutSize[0], vp.layoutSize[1]);
        printf("      views:       %d\n", vp.layoutViews.length);

        View view;
        for (int i = 0, z = vp.layoutViews.length; i < z; ++i) {
            view = vp.layoutViews[i];

            printf("        view %d:\n", i);
            printf("          type:         %d\n", view.type);
            printf("          axis_lock:    %d\n", view.axisLock);
            printf("          position:     (%d,%d)\n", view.position[0], view.position[1]);
            printf("          size:         (%d,%d)\n", view.size[0], view.size[1]);
            printf("          zoom:         %g\n", view.zoom);
            printf("          center:       (%g,%g,%g)\n", view.center[0], view.center[1], view.center[2]);
            printf("          horiz_angle:  %g\n", view.horizAngle);
            printf("          vert_angle:   %g\n", view.vertAngle);
            printf("          camera:       %s\n", view.camera);
        }

        printf("    default:\n");
        printf(" type:         %d\n", vp.defaultType);
        printf(" position:     (%g,%g,%g)\n", vp.defaultPosition[0], vp.defaultPosition[1], vp.defaultPosition[2]);
        printf(" width:        %g\n", vp.defaultWidth);
        printf(" horiz_angle:  %g\n", vp.defaultHorizAngle);
        printf(" vert_angle:   %g\n", vp.defaultVertAngle);
        printf(" roll_angle:   %g\n", vp.defaultRollAngle);
        printf(" camera:       %s\n", vp.defaultCamera);
    }
    public void dump(String maptype, TextureMap texture) {

        if (null != texture.name && 0 != texture.name.length()) {
            printf("  %s:\n", maptype);
            printf("    name:          %s\n", texture.name);
            printf("    flags:         %X\n", texture.flags);//(unsigned)
            printf("    percent:       %f\n", texture.percent);
            printf("    blur:          %f\n", texture.blur);
            printf("    scale:         (%f, %f)\n", texture.scale[0], texture.scale[1]);
            printf("    offset:        (%f, %f)\n", texture.offset[0], texture.offset[1]);
            printf("    rotation:      %f\n", texture.rotation);
            printf("    tint_1:        (%f, %f, %f)\n",
                   texture.tint_1[0], texture.tint_1[1], texture.tint_1[2]);
            printf("    tint_2:        (%f, %f, %f)\n",
                   texture.tint_2[0], texture.tint_2[1], texture.tint_2[2]);
            printf("    tint_r:        (%f, %f, %f)\n",
                   texture.tint_r[0], texture.tint_r[1], texture.tint_r[2]);
            printf("    tint_g:        (%f, %f, %f)\n",
                   texture.tint_g[0], texture.tint_g[1], texture.tint_g[2]);
            printf("    tint_b:        (%f, %f, %f)\n",
                   texture.tint_b[0], texture.tint_b[1], texture.tint_b[2]);
        }
    }
    public void dump(Material material) {

        printf("  name:            %s\n", material.name);
        printf("  ambient:         (%f, %f, %f)\n",
               material.ambient[0], material.ambient[1], material.ambient[2]);
        printf("  diffuse:         (%f, %f, %f)\n",
               material.diffuse[0], material.diffuse[1], material.diffuse[2]);
        printf("  specular:        (%f, %f, %f)\n",
               material.specular[0], material.specular[1], material.specular[2]);
        printf("  shininess:       %f\n", material.shininess);
        printf("  shin_strength:   %f\n", material.shinStrength);
        printf("  use_blur:        %s\n", material.useBlur ? "yes" : "no");
        printf("  blur:            %f\n", material.blur);
        printf("  falloff:         %f\n", material.falloff);
        printf("  is_additive:     %s\n", material.isAdditive ? "yes" : "no");
        printf("  use_falloff:     %s\n", material.useFalloff ? "yes" : "no");
        printf("  self_illum_flag: %s\n", material.selfIllumFlag ? "yes" : "no");
        printf("  self_illum:      %f\n", material.selfIllum);
        printf("  shading:         %d\n", material.shading);
        printf("  soften:          %s\n", material.soften ? "yes" : "no");
        printf("  face_map:        %s\n", material.faceMap ? "yes" : "no");
        printf("  two_sided:       %s\n", material.twoSided ? "yes" : "no");
        printf("  map_decal:       %s\n", material.mapDecal ? "yes" : "no");
        printf("  use_wire:        %s\n", material.useWire ? "yes" : "no");
        printf("  use_wire_abs:    %s\n", material.useWireAbs ? "yes" : "no");
        printf("  wire_size:       %f\n", material.wireSize);
        dump("texture1_map", material.texture1Map);
        dump("texture1_mask", material.texture1Mask);
        dump("texture2_map", material.texture2Map);
        dump("texture2_mask", material.texture2Mask);
        dump("opacity_map", material.opacityMap);
        dump("opacity_mask", material.opacityMask);
        dump("bump_map", material.bumpMap);
        dump("bump_mask", material.bumpMask);
        dump("specular_map", material.specularMap);
        dump("specular_mask", material.specularMask);
        dump("shininess_map", material.shininessMap);
        dump("shininess_mask", material.shininessMask);
        dump("self_illum_map", material.selfIllumMap);
        dump("self_illum_mask", material.selfIllumMask);
        dump("reflection_map", material.reflectionMap);
        dump("reflection_mask", material.reflectionMask);
        printf("  autorefl_map:\n");
        printf("    flags          %X\n", material.autoreflMapFlags);//(unsigned)
        printf("    level          %d\n", material.autoreflMapAntiAlias);
        printf("    size           %d\n", material.autoreflMapSize);
        printf("    frame_step     %d\n", material.autoreflMapFrameStep);
        printf("\n");
    }
    public void dump(Camera camera) {

        printf("  name:       %s\n", camera.name);
        printf("  position:   (%f, %f, %f)\n",
               camera.position[0], camera.position[1], camera.position[2]);
        printf("  target      (%f, %f, %f)\n",
               camera.target[0], camera.target[1], camera.target[2]);
        printf("  roll:       %f\n", camera.roll);
        printf("  fov:        %f\n", camera.fov);
        printf("  see_cone:   %s\n", camera.seeCone ? "yes" : "no");
        printf("  near_range: %f\n", camera.nearRange);
        printf("  far_range:  %f\n", camera.farRange);
        printf("\n");
    }
    public void dump(Light light) {

        printf("  name:             %s\n", light.name);
        printf("  spot_light:       %s\n", light.spot_light ? "yes" : "no");
        printf("  see_cone:         %s\n", light.see_cone ? "yes" : "no");
        printf("  color:            (%f, %f, %f)\n",
               light.color[0], light.color[1], light.color[2]);
        printf("  position          (%f, %f, %f)\n",
               light.position[0], light.position[1], light.position[2]);
        printf("  target              (%f, %f, %f)\n",
               light.target[0], light.target[1], light.target[2]);
        printf("  roll:             %f\n", light.roll);
        printf("  off:              %s\n", light.off ? "yes" : "no");
        printf("  outer_range:      %f\n", light.outer_range);
        printf("  inner_range:      %f\n", light.inner_range);
        printf("  multiplier:       %f\n", light.multiplier);
        printf("  attenuation:      %f\n", light.attenuation);
        printf("  rectangular_spot: %s\n", light.rectangular_spot ? "yes" : "no");
        printf("  shadowed:         %s\n", light.shadowed ? "yes" : "no");
        printf("  shadow_bias:      %f\n", light.shadow_bias);
        printf("  shadow_filter:    %f\n", light.shadow_filter);
        printf("  shadow_size:      %d\n", light.shadow_size);
        printf("  spot_aspect:      %f\n", light.spot_aspect);
        printf("  use_projector:    %s\n", light.use_projector ? "yes" : "no");
        printf("  projector:        %s\n", light.projector);
        printf("  spot_overshoot:   %d\n", light.spot_overshoot ? "yes" : "no");
        printf("  ray_shadows:      %s\n", light.ray_shadows ? "yes" : "no");
        printf("  ray_bias:         %f\n", light.ray_bias);
        printf("  hotspot:         %f\n", light.hotspot);
        printf("  falloff:         %f\n", light.falloff);
        printf("\n");
    }
    public void dump(Mesh mesh) {

        printf("  %s vertices=%d faces=%d\n",
               mesh.name,
               mesh.vertices.length,
               mesh.faces.length);
        printf("  matrix:\n");
        dump(mesh.matrix);
        printf("  vertices (x, y, z, u, v):\n");

        float[] p;
        for (int i = 0, z = mesh.vertices.length; i < z; ++i) {
            p = mesh.vertices[i];
            printf("    %10.5f %10.5f %10.5f", p[0], p[1], p[2]);
            if (null != mesh.texcos && 0 != mesh.texcos.length) {
                printf("%10.5f %10.5f", mesh.texcos[i][0], mesh.texcos[i][1]);
            }
            printf("\n");
        }
        printf("  facelist:\n");
        for (int i = 0, z = mesh.faces.length; i < z; ++i) {
            printf("    %4d %4d %4d  flags:%X  smoothing:%X  material:\"%d\"\n",
                   mesh.faces[i].index[0],
                   mesh.faces[i].index[1],
                   mesh.faces[i].index[2],
                   mesh.faces[i].flags,
                   mesh.faces[i].smoothing_group,
                   mesh.faces[i].material
                   );
        }
    }
    public void dump(Node node, String parent) {

        String name = parent+'.'+node.name;

        if (node.type == Node.Type.MESH_INSTANCE) {
            MeshInstanceNode n = (MeshInstanceNode)node;
            String in = n.instance_name;
            if (null == in)
                printf("  %s : \n", name);
            else
                printf("  %s : \n", name, in);
        }
        for (Node p = node.childs; null != p; p = p.next) {
            dump(p, parent);
        }
    }
    public void dump(Node node, int level) {

        String l = Level(level);

        if (node.type == Node.Type.MESH_INSTANCE) {
            MeshInstanceNode n = (MeshInstanceNode)node; 
            String in = n.instance_name;
            if (null == in)
                printf("%s%s [] (%s)\n",
                       l,
                       node.name,
                       node.type.label
                       );
            else
                printf("%s%s [%s] (%s)\n",
                       l,
                       node.name,
                       n.instance_name,
                       node.type.label
                       );
        } else {
            printf("%s%s (%s)\n",
                   l,
                   node.name,
                   node.type.label
                   );
        }

        for (Node p = node.childs; null != p; p = p.next) {
            dump(p, level + 1);
        }
    }

}
