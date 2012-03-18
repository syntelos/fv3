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
/*
 * This document is licensed under the SGI Free Software B License Version
 * 2.0. For details, see http://oss.sgi.com/projects/FreeB/ .
 * 
 * Copyright (c) 2008-2009 The Khronos Group Inc.
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fv3;

/**
 *
 * @version $Revision: 16803 $ on $Date:: 2012-02-02 09:49:18 -0800 #$
 */
public interface GLES2 {

    public final static int GL_DEPTH_BUFFER_BIT               0x00000100;
    public final static int GL_STENCIL_BUFFER_BIT             0x00000400;
    public final static int GL_COLOR_BUFFER_BIT               0x00004000;
    public final static int GL_FALSE                          0;
    public final static int GL_TRUE                           1;
    public final static int GL_POINTS                         0x0000;
    public final static int GL_LINES                          0x0001;
    public final static int GL_LINE_LOOP                      0x0002;
    public final static int GL_LINE_STRIP                     0x0003;
    public final static int GL_TRIANGLES                      0x0004;
    public final static int GL_TRIANGLE_STRIP                 0x0005;
    public final static int GL_TRIANGLE_FAN                   0x0006;
    public final static int GL_ZERO                           0;
    public final static int GL_ONE                            1;
    public final static int GL_SRC_COLOR                      0x0300;
    public final static int GL_ONE_MINUS_SRC_COLOR            0x0301;
    public final static int GL_SRC_ALPHA                      0x0302;
    public final static int GL_ONE_MINUS_SRC_ALPHA            0x0303;
    public final static int GL_DST_ALPHA                      0x0304;
    public final static int GL_ONE_MINUS_DST_ALPHA            0x0305;
    public final static int GL_DST_COLOR                      0x0306;
    public final static int GL_ONE_MINUS_DST_COLOR            0x0307;
    public final static int GL_SRC_ALPHA_SATURATE             0x0308;
    public final static int GL_FUNC_ADD                       0x8006;
    public final static int GL_BLEND_EQUATION                 0x8009;
    public final static int GL_BLEND_EQUATION_RGB             0x8009;    /* same as BLEND_EQUATION */
    public final static int GL_BLEND_EQUATION_ALPHA           0x883D;
    public final static int GL_FUNC_SUBTRACT                  0x800A;
    public final static int GL_FUNC_REVERSE_SUBTRACT          0x800B;
    public final static int GL_BLEND_DST_RGB                  0x80C8;
    public final static int GL_BLEND_SRC_RGB                  0x80C9;
    public final static int GL_BLEND_DST_ALPHA                0x80CA;
    public final static int GL_BLEND_SRC_ALPHA                0x80CB;
    public final static int GL_CONSTANT_COLOR                 0x8001;
    public final static int GL_ONE_MINUS_CONSTANT_COLOR       0x8002;
    public final static int GL_CONSTANT_ALPHA                 0x8003;
    public final static int GL_ONE_MINUS_CONSTANT_ALPHA       0x8004;
    public final static int GL_BLEND_COLOR                    0x8005;
    public final static int GL_ARRAY_BUFFER                   0x8892;
    public final static int GL_ELEMENT_ARRAY_BUFFER           0x8893;
    public final static int GL_ARRAY_BUFFER_BINDING           0x8894;
    public final static int GL_ELEMENT_ARRAY_BUFFER_BINDING   0x8895;
    public final static int GL_STREAM_DRAW                    0x88E0;
    public final static int GL_STATIC_DRAW                    0x88E4;
    public final static int GL_DYNAMIC_DRAW                   0x88E8;
    public final static int GL_BUFFER_SIZE                    0x8764;
    public final static int GL_BUFFER_USAGE                   0x8765;
    public final static int GL_CURRENT_VERTEX_ATTRIB          0x8626;
    public final static int GL_FRONT                          0x0404;
    public final static int GL_BACK                           0x0405;
    public final static int GL_FRONT_AND_BACK                 0x0408;
    public final static int GL_TEXTURE_2D                     0x0DE1;
    public final static int GL_CULL_FACE                      0x0B44;
    public final static int GL_BLEND                          0x0BE2;
    public final static int GL_DITHER                         0x0BD0;
    public final static int GL_STENCIL_TEST                   0x0B90;
    public final static int GL_DEPTH_TEST                     0x0B71;
    public final static int GL_SCISSOR_TEST                   0x0C11;
    public final static int GL_POLYGON_OFFSET_FILL            0x8037;
    public final static int GL_SAMPLE_ALPHA_TO_COVERAGE       0x809E;
    public final static int GL_SAMPLE_COVERAGE                0x80A0;
    public final static int GL_NO_ERROR                       0;
    public final static int GL_INVALID_ENUM                   0x0500;
    public final static int GL_INVALID_VALUE                  0x0501;
    public final static int GL_INVALID_OPERATION              0x0502;
    public final static int GL_OUT_OF_MEMORY                  0x0505;
    public final static int GL_CW                             0x0900;
    public final static int GL_CCW                            0x0901;
    public final static int GL_LINE_WIDTH                     0x0B21;
    public final static int GL_ALIASED_POINT_SIZE_RANGE       0x846D;
    public final static int GL_ALIASED_LINE_WIDTH_RANGE       0x846E;
    public final static int GL_CULL_FACE_MODE                 0x0B45;
    public final static int GL_FRONT_FACE                     0x0B46;
    public final static int GL_DEPTH_RANGE                    0x0B70;
    public final static int GL_DEPTH_WRITEMASK                0x0B72;
    public final static int GL_DEPTH_CLEAR_VALUE              0x0B73;
    public final static int GL_DEPTH_FUNC                     0x0B74;
    public final static int GL_STENCIL_CLEAR_VALUE            0x0B91;
    public final static int GL_STENCIL_FUNC                   0x0B92;
    public final static int GL_STENCIL_FAIL                   0x0B94;
    public final static int GL_STENCIL_PASS_DEPTH_FAIL        0x0B95;
    public final static int GL_STENCIL_PASS_DEPTH_PASS        0x0B96;
    public final static int GL_STENCIL_REF                    0x0B97;
    public final static int GL_STENCIL_VALUE_MASK             0x0B93;
    public final static int GL_STENCIL_WRITEMASK              0x0B98;
    public final static int GL_STENCIL_BACK_FUNC              0x8800;
    public final static int GL_STENCIL_BACK_FAIL              0x8801;
    public final static int GL_STENCIL_BACK_PASS_DEPTH_FAIL   0x8802;
    public final static int GL_STENCIL_BACK_PASS_DEPTH_PASS   0x8803;
    public final static int GL_STENCIL_BACK_REF               0x8CA3;
    public final static int GL_STENCIL_BACK_VALUE_MASK        0x8CA4;
    public final static int GL_STENCIL_BACK_WRITEMASK         0x8CA5;
    public final static int GL_VIEWPORT                       0x0BA2;
    public final static int GL_SCISSOR_BOX                    0x0C10;
    public final static int GL_COLOR_CLEAR_VALUE              0x0C22;
    public final static int GL_COLOR_WRITEMASK                0x0C23;
    public final static int GL_UNPACK_ALIGNMENT               0x0CF5;
    public final static int GL_PACK_ALIGNMENT                 0x0D05;
    public final static int GL_MAX_TEXTURE_SIZE               0x0D33;
    public final static int GL_MAX_VIEWPORT_DIMS              0x0D3A;
    public final static int GL_SUBPIXEL_BITS                  0x0D50;
    public final static int GL_RED_BITS                       0x0D52;
    public final static int GL_GREEN_BITS                     0x0D53;
    public final static int GL_BLUE_BITS                      0x0D54;
    public final static int GL_ALPHA_BITS                     0x0D55;
    public final static int GL_DEPTH_BITS                     0x0D56;
    public final static int GL_STENCIL_BITS                   0x0D57;
    public final static int GL_POLYGON_OFFSET_UNITS           0x2A00;
    public final static int GL_POLYGON_OFFSET_FACTOR          0x8038;
    public final static int GL_TEXTURE_BINDING_2D             0x8069;
    public final static int GL_SAMPLE_BUFFERS                 0x80A8;
    public final static int GL_SAMPLES                        0x80A9;
    public final static int GL_SAMPLE_COVERAGE_VALUE          0x80AA;
    public final static int GL_SAMPLE_COVERAGE_INVERT         0x80AB;
    public final static int GL_NUM_COMPRESSED_TEXTURE_FORMATS 0x86A2;
    public final static int GL_COMPRESSED_TEXTURE_FORMATS     0x86A3;
    public final static int GL_DONT_CARE                      0x1100;
    public final static int GL_FASTEST                        0x1101;
    public final static int GL_NICEST                         0x1102;
    public final static int GL_GENERATE_MIPMAP_HINT            0x8192;
    public final static int GL_BYTE                           0x1400;
    public final static int GL_UNSIGNED_BYTE                  0x1401;
    public final static int GL_SHORT                          0x1402;
    public final static int GL_UNSIGNED_SHORT                 0x1403;
    public final static int GL_INT                            0x1404;
    public final static int GL_UNSIGNED_INT                   0x1405;
    public final static int GL_FLOAT                          0x1406;
    public final static int GL_FIXED                          0x140C;
    public final static int GL_DEPTH_COMPONENT                0x1902;
    public final static int GL_ALPHA                          0x1906;
    public final static int GL_RGB                            0x1907;
    public final static int GL_RGBA                           0x1908;
    public final static int GL_LUMINANCE                      0x1909;
    public final static int GL_LUMINANCE_ALPHA                0x190A;
    public final static int GL_UNSIGNED_SHORT_4_4_4_4         0x8033;
    public final static int GL_UNSIGNED_SHORT_5_5_5_1         0x8034;
    public final static int GL_UNSIGNED_SHORT_5_6_5           0x8363;
    public final static int GL_FRAGMENT_SHADER                  0x8B30;
    public final static int GL_VERTEX_SHADER                    0x8B31;
    public final static int GL_MAX_VERTEX_ATTRIBS               0x8869;
    public final static int GL_MAX_VERTEX_UNIFORM_VECTORS       0x8DFB;
    public final static int GL_MAX_VARYING_VECTORS              0x8DFC;
    public final static int GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS 0x8B4D;
    public final static int GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS   0x8B4C;
    public final static int GL_MAX_TEXTURE_IMAGE_UNITS          0x8872;
    public final static int GL_MAX_FRAGMENT_UNIFORM_VECTORS     0x8DFD;
    public final static int GL_SHADER_TYPE                      0x8B4F;
    public final static int GL_DELETE_STATUS                    0x8B80;
    public final static int GL_LINK_STATUS                      0x8B82;
    public final static int GL_VALIDATE_STATUS                  0x8B83;
    public final static int GL_ATTACHED_SHADERS                 0x8B85;
    public final static int GL_ACTIVE_UNIFORMS                  0x8B86;
    public final static int GL_ACTIVE_UNIFORM_MAX_LENGTH        0x8B87;
    public final static int GL_ACTIVE_ATTRIBUTES                0x8B89;
    public final static int GL_ACTIVE_ATTRIBUTE_MAX_LENGTH      0x8B8A;
    public final static int GL_SHADING_LANGUAGE_VERSION         0x8B8C;
    public final static int GL_CURRENT_PROGRAM                  0x8B8D;
    public final static int GL_NEVER                          0x0200;
    public final static int GL_LESS                           0x0201;
    public final static int GL_EQUAL                          0x0202;
    public final static int GL_LEQUAL                         0x0203;
    public final static int GL_GREATER                        0x0204;
    public final static int GL_NOTEQUAL                       0x0205;
    public final static int GL_GEQUAL                         0x0206;
    public final static int GL_ALWAYS                         0x0207;
    public final static int GL_KEEP                           0x1E00;
    public final static int GL_REPLACE                        0x1E01;
    public final static int GL_INCR                           0x1E02;
    public final static int GL_DECR                           0x1E03;
    public final static int GL_INVERT                         0x150A;
    public final static int GL_INCR_WRAP                      0x8507;
    public final static int GL_DECR_WRAP                      0x8508;
    public final static int GL_VENDOR                         0x1F00;
    public final static int GL_RENDERER                       0x1F01;
    public final static int GL_VERSION                        0x1F02;
    public final static int GL_EXTENSIONS                     0x1F03;
    public final static int GL_NEAREST                        0x2600;
    public final static int GL_LINEAR                         0x2601;
    public final static int GL_NEAREST_MIPMAP_NEAREST         0x2700;
    public final static int GL_LINEAR_MIPMAP_NEAREST          0x2701;
    public final static int GL_NEAREST_MIPMAP_LINEAR          0x2702;
    public final static int GL_LINEAR_MIPMAP_LINEAR           0x2703;
    public final static int GL_TEXTURE_MAG_FILTER             0x2800;
    public final static int GL_TEXTURE_MIN_FILTER             0x2801;
    public final static int GL_TEXTURE_WRAP_S                 0x2802;
    public final static int GL_TEXTURE_WRAP_T                 0x2803;
    public final static int GL_TEXTURE                        0x1702;
    public final static int GL_TEXTURE_CUBE_MAP               0x8513;
    public final static int GL_TEXTURE_BINDING_CUBE_MAP       0x8514;
    public final static int GL_TEXTURE_CUBE_MAP_POSITIVE_X    0x8515;
    public final static int GL_TEXTURE_CUBE_MAP_NEGATIVE_X    0x8516;
    public final static int GL_TEXTURE_CUBE_MAP_POSITIVE_Y    0x8517;
    public final static int GL_TEXTURE_CUBE_MAP_NEGATIVE_Y    0x8518;
    public final static int GL_TEXTURE_CUBE_MAP_POSITIVE_Z    0x8519;
    public final static int GL_TEXTURE_CUBE_MAP_NEGATIVE_Z    0x851A;
    public final static int GL_MAX_CUBE_MAP_TEXTURE_SIZE      0x851C;
    public final static int GL_TEXTURE0                       0x84C0;
    public final static int GL_TEXTURE1                       0x84C1;
    public final static int GL_TEXTURE2                       0x84C2;
    public final static int GL_TEXTURE3                       0x84C3;
    public final static int GL_TEXTURE4                       0x84C4;
    public final static int GL_TEXTURE5                       0x84C5;
    public final static int GL_TEXTURE6                       0x84C6;
    public final static int GL_TEXTURE7                       0x84C7;
    public final static int GL_TEXTURE8                       0x84C8;
    public final static int GL_TEXTURE9                       0x84C9;
    public final static int GL_TEXTURE10                      0x84CA;
    public final static int GL_TEXTURE11                      0x84CB;
    public final static int GL_TEXTURE12                      0x84CC;
    public final static int GL_TEXTURE13                      0x84CD;
    public final static int GL_TEXTURE14                      0x84CE;
    public final static int GL_TEXTURE15                      0x84CF;
    public final static int GL_TEXTURE16                      0x84D0;
    public final static int GL_TEXTURE17                      0x84D1;
    public final static int GL_TEXTURE18                      0x84D2;
    public final static int GL_TEXTURE19                      0x84D3;
    public final static int GL_TEXTURE20                      0x84D4;
    public final static int GL_TEXTURE21                      0x84D5;
    public final static int GL_TEXTURE22                      0x84D6;
    public final static int GL_TEXTURE23                      0x84D7;
    public final static int GL_TEXTURE24                      0x84D8;
    public final static int GL_TEXTURE25                      0x84D9;
    public final static int GL_TEXTURE26                      0x84DA;
    public final static int GL_TEXTURE27                      0x84DB;
    public final static int GL_TEXTURE28                      0x84DC;
    public final static int GL_TEXTURE29                      0x84DD;
    public final static int GL_TEXTURE30                      0x84DE;
    public final static int GL_TEXTURE31                      0x84DF;
    public final static int GL_ACTIVE_TEXTURE                 0x84E0;
    public final static int GL_REPEAT                         0x2901;
    public final static int GL_CLAMP_TO_EDGE                  0x812F;
    public final static int GL_MIRRORED_REPEAT                0x8370;
    public final static int GL_FLOAT_VEC2                     0x8B50;
    public final static int GL_FLOAT_VEC3                     0x8B51;
    public final static int GL_FLOAT_VEC4                     0x8B52;
    public final static int GL_INT_VEC2                       0x8B53;
    public final static int GL_INT_VEC3                       0x8B54;
    public final static int GL_INT_VEC4                       0x8B55;
    public final static int GL_BOOL                           0x8B56;
    public final static int GL_BOOL_VEC2                      0x8B57;
    public final static int GL_BOOL_VEC3                      0x8B58;
    public final static int GL_BOOL_VEC4                      0x8B59;
    public final static int GL_FLOAT_MAT2                     0x8B5A;
    public final static int GL_FLOAT_MAT3                     0x8B5B;
    public final static int GL_FLOAT_MAT4                     0x8B5C;
    public final static int GL_SAMPLER_2D                     0x8B5E;
    public final static int GL_SAMPLER_CUBE                   0x8B60;
    public final static int GL_VERTEX_ATTRIB_ARRAY_ENABLED        0x8622;
    public final static int GL_VERTEX_ATTRIB_ARRAY_SIZE           0x8623;
    public final static int GL_VERTEX_ATTRIB_ARRAY_STRIDE         0x8624;
    public final static int GL_VERTEX_ATTRIB_ARRAY_TYPE           0x8625;
    public final static int GL_VERTEX_ATTRIB_ARRAY_NORMALIZED     0x886A;
    public final static int GL_VERTEX_ATTRIB_ARRAY_POINTER        0x8645;
    public final static int GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING 0x889F;
    public final static int GL_IMPLEMENTATION_COLOR_READ_TYPE   0x8B9A;
    public final static int GL_IMPLEMENTATION_COLOR_READ_FORMAT 0x8B9B;
    public final static int GL_COMPILE_STATUS                 0x8B81;
    public final static int GL_INFO_LOG_LENGTH                0x8B84;
    public final static int GL_SHADER_SOURCE_LENGTH           0x8B88;
    public final static int GL_SHADER_COMPILER                0x8DFA;
    public final static int GL_SHADER_BINARY_FORMATS          0x8DF8;
    public final static int GL_NUM_SHADER_BINARY_FORMATS      0x8DF9;
    public final static int GL_LOW_FLOAT                      0x8DF0;
    public final static int GL_MEDIUM_FLOAT                   0x8DF1;
    public final static int GL_HIGH_FLOAT                     0x8DF2;
    public final static int GL_LOW_INT                        0x8DF3;
    public final static int GL_MEDIUM_INT                     0x8DF4;
    public final static int GL_HIGH_INT                       0x8DF5;
    public final static int GL_FRAMEBUFFER                    0x8D40;
    public final static int GL_RENDERBUFFER                   0x8D41;
    public final static int GL_RGBA4                          0x8056;
    public final static int GL_RGB5_A1                        0x8057;
    public final static int GL_RGB565                         0x8D62;
    public final static int GL_DEPTH_COMPONENT16              0x81A5;
    public final static int GL_STENCIL_INDEX8                 0x8D48;
    public final static int GL_RENDERBUFFER_WIDTH             0x8D42;
    public final static int GL_RENDERBUFFER_HEIGHT            0x8D43;
    public final static int GL_RENDERBUFFER_INTERNAL_FORMAT   0x8D44;
    public final static int GL_RENDERBUFFER_RED_SIZE          0x8D50;
    public final static int GL_RENDERBUFFER_GREEN_SIZE        0x8D51;
    public final static int GL_RENDERBUFFER_BLUE_SIZE         0x8D52;
    public final static int GL_RENDERBUFFER_ALPHA_SIZE        0x8D53;
    public final static int GL_RENDERBUFFER_DEPTH_SIZE        0x8D54;
    public final static int GL_RENDERBUFFER_STENCIL_SIZE      0x8D55;
    public final static int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE           0x8CD0;
    public final static int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME           0x8CD1;
    public final static int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL         0x8CD2;
    public final static int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE 0x8CD3;
    public final static int GL_COLOR_ATTACHMENT0              0x8CE0;
    public final static int GL_DEPTH_ATTACHMENT               0x8D00;
    public final static int GL_STENCIL_ATTACHMENT             0x8D20;
    public final static int GL_NONE                           0;
    public final static int GL_FRAMEBUFFER_COMPLETE                      0x8CD5;
    public final static int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT         0x8CD6;
    public final static int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT 0x8CD7;
    public final static int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS         0x8CD9;
    public final static int GL_FRAMEBUFFER_UNSUPPORTED                   0x8CDD;
    public final static int GL_FRAMEBUFFER_BINDING            0x8CA6;
    public final static int GL_RENDERBUFFER_BINDING           0x8CA7;
    public final static int GL_MAX_RENDERBUFFER_SIZE          0x84E8;
    public final static int GL_INVALID_FRAMEBUFFER_OPERATION  0x0506;
}
