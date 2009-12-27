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

/**
 * Chunk pointer for nested chunking on the function stack.
 * 
 * @see Reader
 * @author jdp
 */
public final class Chunk {
    public final static int NULL_CHUNK             = 0x0000;
    public final static int M3DMAGIC               = 0x4D4D;    /*3DS file*/
    public final static int SMAGIC                 = 0x2D2D;    
    public final static int LMAGIC                 = 0x2D3D;    
    public final static int MLIBMAGIC              = 0x3DAA;    /*MLI file*/
    public final static int MATMAGIC               = 0x3DFF;    
    public final static int CMAGIC                 = 0xC23D;    /*PRJ file*/
    public final static int M3D_VERSION            = 0x0002;
    public final static int M3D_KFVERSION          = 0x0005;
    public final static int COLOR_F                = 0x0010;
    public final static int COLOR_24               = 0x0011;
    public final static int LIN_COLOR_24           = 0x0012;
    public final static int LIN_COLOR_F            = 0x0013;
    public final static int INT_PERCENTAGE         = 0x0030;
    public final static int FLOAT_PERCENTAGE       = 0x0031;
    public final static int MDATA                  = 0x3D3D;
    public final static int MESH_VERSION           = 0x3D3E;
    public final static int MASTER_SCALE           = 0x0100;
    public final static int LO_SHADOW_BIAS         = 0x1400;
    public final static int HI_SHADOW_BIAS         = 0x1410;
    public final static int SHADOW_MAP_SIZE        = 0x1420;
    public final static int SHADOW_SAMPLES         = 0x1430;
    public final static int SHADOW_RANGE           = 0x1440;
    public final static int SHADOW_FILTER          = 0x1450;
    public final static int RAY_BIAS               = 0x1460;
    public final static int O_CONSTS               = 0x1500;
    public final static int AMBIENT_LIGHT          = 0x2100;
    public final static int BIT_MAP                = 0x1100;
    public final static int SOLID_BGND             = 0x1200;
    public final static int V_GRADIENT             = 0x1300;
    public final static int USE_BIT_MAP            = 0x1101;
    public final static int USE_SOLID_BGND         = 0x1201;
    public final static int USE_V_GRADIENT         = 0x1301;
    public final static int FOG                    = 0x2200;
    public final static int FOG_BGND               = 0x2210;
    public final static int LAYER_FOG              = 0x2302;
    public final static int DISTANCE_CUE           = 0x2300;
    public final static int DCUE_BGND              = 0x2310;
    public final static int USE_FOG                = 0x2201;
    public final static int USE_LAYER_FOG          = 0x2303;
    public final static int USE_DISTANCE_CUE       = 0x2301;
    public final static int MAT_ENTRY              = 0xAFFF;
    public final static int MAT_NAME               = 0xA000;
    public final static int MAT_AMBIENT            = 0xA010;
    public final static int MAT_DIFFUSE            = 0xA020;
    public final static int MAT_SPECULAR           = 0xA030;
    public final static int MAT_SHININESS          = 0xA040;
    public final static int MAT_SHIN2PCT           = 0xA041;
    public final static int MAT_TRANSPARENCY       = 0xA050;
    public final static int MAT_XPFALL             = 0xA052;
    public final static int MAT_USE_XPFALL         = 0xA240;
    public final static int MAT_REFBLUR            = 0xA053;
    public final static int MAT_SHADING            = 0xA100;
    public final static int MAT_USE_REFBLUR        = 0xA250;
    public final static int MAT_SELF_ILLUM         = 0xA080;
    public final static int MAT_TWO_SIDE           = 0xA081;
    public final static int MAT_DECAL              = 0xA082;
    public final static int MAT_ADDITIVE           = 0xA083;
    public final static int MAT_SELF_ILPCT         = 0xA084;
    public final static int MAT_WIRE               = 0xA085;
    public final static int MAT_FACEMAP            = 0xA088;
    public final static int MAT_PHONGSOFT          = 0xA08C;
    public final static int MAT_WIREABS            = 0xA08E;
    public final static int MAT_WIRE_SIZE          = 0xA087;
    public final static int MAT_TEXMAP             = 0xA200;
    public final static int MAT_SXP_TEXT_DATA      = 0xA320;
    public final static int MAT_TEXMASK            = 0xA33E;
    public final static int MAT_SXP_TEXTMASK_DATA  = 0xA32A;
    public final static int MAT_TEX2MAP            = 0xA33A;
    public final static int MAT_SXP_TEXT2_DATA     = 0xA321;
    public final static int MAT_TEX2MASK           = 0xA340;
    public final static int MAT_SXP_TEXT2MASK_DATA = 0xA32C;
    public final static int MAT_OPACMAP            = 0xA210;
    public final static int MAT_SXP_OPAC_DATA      = 0xA322;
    public final static int MAT_OPACMASK           = 0xA342;
    public final static int MAT_SXP_OPACMASK_DATA  = 0xA32E;
    public final static int MAT_BUMPMAP            = 0xA230;
    public final static int MAT_SXP_BUMP_DATA      = 0xA324;
    public final static int MAT_BUMPMASK           = 0xA344;
    public final static int MAT_SXP_BUMPMASK_DATA  = 0xA330;
    public final static int MAT_SPECMAP            = 0xA204;
    public final static int MAT_SXP_SPEC_DATA      = 0xA325;
    public final static int MAT_SPECMASK           = 0xA348;
    public final static int MAT_SXP_SPECMASK_DATA  = 0xA332;
    public final static int MAT_SHINMAP            = 0xA33C;
    public final static int MAT_SXP_SHIN_DATA      = 0xA326;
    public final static int MAT_SHINMASK           = 0xA346;
    public final static int MAT_SXP_SHINMASK_DATA  = 0xA334;
    public final static int MAT_SELFIMAP           = 0xA33D;
    public final static int MAT_SXP_SELFI_DATA     = 0xA328;
    public final static int MAT_SELFIMASK          = 0xA34A;
    public final static int MAT_SXP_SELFIMASK_DATA = 0xA336;
    public final static int MAT_REFLMAP            = 0xA220;
    public final static int MAT_REFLMASK           = 0xA34C;
    public final static int MAT_SXP_REFLMASK_DATA  = 0xA338;
    public final static int MAT_ACUBIC             = 0xA310;
    public final static int MAT_MAPNAME            = 0xA300;
    public final static int MAT_MAP_TILING         = 0xA351;
    public final static int MAT_MAP_TEXBLUR        = 0xA353;
    public final static int MAT_MAP_USCALE         = 0xA354;
    public final static int MAT_MAP_VSCALE         = 0xA356;
    public final static int MAT_MAP_UOFFSET        = 0xA358;
    public final static int MAT_MAP_VOFFSET        = 0xA35A;
    public final static int MAT_MAP_ANG            = 0xA35C;
    public final static int MAT_MAP_COL1           = 0xA360;
    public final static int MAT_MAP_COL2           = 0xA362;
    public final static int MAT_MAP_RCOL           = 0xA364;
    public final static int MAT_MAP_GCOL           = 0xA366;
    public final static int MAT_MAP_BCOL           = 0xA368;
    public final static int NAMED_OBJECT           = 0x4000;
    public final static int N_DIRECT_LIGHT         = 0x4600;
    public final static int DL_OFF                 = 0x4620;
    public final static int DL_OUTER_RANGE         = 0x465A;
    public final static int DL_INNER_RANGE         = 0x4659;
    public final static int DL_MULTIPLIER          = 0x465B;
    public final static int DL_EXCLUDE             = 0x4654;
    public final static int DL_ATTENUATE           = 0x4625;
    public final static int DL_SPOTLIGHT           = 0x4610;
    public final static int DL_SPOT_ROLL           = 0x4656;
    public final static int DL_SHADOWED            = 0x4630;
    public final static int DL_LOCAL_SHADOW2       = 0x4641;
    public final static int DL_SEE_CONE            = 0x4650;
    public final static int DL_SPOT_RECTANGULAR    = 0x4651;
    public final static int DL_SPOT_ASPECT         = 0x4657;
    public final static int DL_SPOT_PROJECTOR      = 0x4653;
    public final static int DL_SPOT_OVERSHOOT      = 0x4652;
    public final static int DL_RAY_BIAS            = 0x4658;
    public final static int DL_RAYSHAD             = 0x4627;
    public final static int N_CAMERA               = 0x4700;
    public final static int CAM_SEE_CONE           = 0x4710;
    public final static int CAM_RANGES             = 0x4720;
    public final static int OBJ_HIDDEN             = 0x4010;
    public final static int OBJ_VIS_LOFTER         = 0x4011;
    public final static int OBJ_DOESNT_CAST        = 0x4012;
    public final static int OBJ_DONT_RCVSHADOW     = 0x4017;
    public final static int OBJ_MATTE              = 0x4013;
    public final static int OBJ_FAST               = 0x4014;
    public final static int OBJ_PROCEDURAL         = 0x4015;
    public final static int OBJ_FROZEN             = 0x4016;
    public final static int N_TRI_OBJECT           = 0x4100;
    public final static int POINT_ARRAY            = 0x4110;
    public final static int POINT_FLAG_ARRAY       = 0x4111;
    public final static int FACE_ARRAY             = 0x4120;
    public final static int MSH_MAT_GROUP          = 0x4130;
    public final static int SMOOTH_GROUP           = 0x4150;
    public final static int MSH_BOXMAP             = 0x4190;
    public final static int TEX_VERTS              = 0x4140;
    public final static int MESH_MATRIX            = 0x4160;
    public final static int MESH_COLOR             = 0x4165;
    public final static int MESH_TEXTURE_INFO      = 0x4170;
    public final static int KFDATA                 = 0xB000;
    public final static int KFHDR                  = 0xB00A;
    public final static int KFSEG                  = 0xB008;
    public final static int KFCURTIME              = 0xB009;
    public final static int AMBIENT_NODE_TAG       = 0xB001;
    public final static int OBJECT_NODE_TAG        = 0xB002;
    public final static int CAMERA_NODE_TAG        = 0xB003;
    public final static int TARGET_NODE_TAG        = 0xB004;
    public final static int LIGHT_NODE_TAG         = 0xB005;
    public final static int L_TARGET_NODE_TAG      = 0xB006;
    public final static int SPOTLIGHT_NODE_TAG     = 0xB007;
    public final static int NODE_ID                = 0xB030;
    public final static int NODE_HDR               = 0xB010;
    public final static int PIVOT                  = 0xB013;
    public final static int INSTANCE_NAME          = 0xB011;
    public final static int MORPH_SMOOTH           = 0xB015;
    public final static int BOUNDBOX               = 0xB014;
    public final static int POS_TRACK_TAG          = 0xB020;
    public final static int COL_TRACK_TAG          = 0xB025;
    public final static int ROT_TRACK_TAG          = 0xB021;
    public final static int SCL_TRACK_TAG          = 0xB022;
    public final static int MORPH_TRACK_TAG        = 0xB026;
    public final static int FOV_TRACK_TAG          = 0xB023;
    public final static int ROLL_TRACK_TAG         = 0xB024;
    public final static int HOT_TRACK_TAG          = 0xB027;
    public final static int FALL_TRACK_TAG         = 0xB028;
    public final static int HIDE_TRACK_TAG         = 0xB029;
    public final static int POLY_2D                = 0x5000;
    public final static int SHAPE_OK               = 0x5010;
    public final static int SHAPE_NOT_OK           = 0x5011;
    public final static int SHAPE_HOOK             = 0x5020;
    public final static int PATH_3D                = 0x6000;
    public final static int PATH_MATRIX            = 0x6005;
    public final static int SHAPE_2D               = 0x6010;
    public final static int M_SCALE                = 0x6020;
    public final static int M_TWIST                = 0x6030;
    public final static int M_TEETER               = 0x6040;
    public final static int M_FIT                  = 0x6050;
    public final static int M_BEVEL                = 0x6060;
    public final static int XZ_CURVE               = 0x6070;
    public final static int YZ_CURVE               = 0x6080;
    public final static int INTERPCT               = 0x6090;
    public final static int DEFORM_LIMIT           = 0x60A0;
    public final static int USE_CONTOUR            = 0x6100;
    public final static int USE_TWEEN              = 0x6110;
    public final static int USE_SCALE              = 0x6120;
    public final static int USE_TWIST              = 0x6130;
    public final static int USE_TEETER             = 0x6140;
    public final static int USE_FIT                = 0x6150;
    public final static int USE_BEVEL              = 0x6160;
    public final static int DEFAULT_VIEW           = 0x3000;
    public final static int VIEW_TOP               = 0x3010;
    public final static int VIEW_BOTTOM            = 0x3020;
    public final static int VIEW_LEFT              = 0x3030;
    public final static int VIEW_RIGHT             = 0x3040;
    public final static int VIEW_FRONT             = 0x3050;
    public final static int VIEW_BACK              = 0x3060;
    public final static int VIEW_USER              = 0x3070;
    public final static int VIEW_CAMERA            = 0x3080;
    public final static int VIEW_WINDOW            = 0x3090;
    public final static int VIEWPORT_LAYOUT_OLD    = 0x7000;
    public final static int VIEWPORT_DATA_OLD      = 0x7010;
    public final static int VIEWPORT_LAYOUT        = 0x7001;
    public final static int VIEWPORT_DATA          = 0x7011;
    public final static int VIEWPORT_DATA_3        = 0x7012;
    public final static int VIEWPORT_SIZE          = 0x7020;
    public final static int NETWORK_VIEW           = 0x7030;


    /**
     * sizeof { Short, Int }
     */
    public final static int HeadSize = 6;

    public final int start, next;
    public final int id;
    public final int length, content;

    protected volatile int pos;
	
    Chunk (int st, int id, int ln) {
        super();
        this.start = st;
        this.id = id;
        this.length = ln;
        this.next = (st + ln);
        this.content = (ln - HeadSize);
        this.pos = (st + HeadSize);
    }
    Chunk (int len){
        super();
        this.start = 0;
        this.next = len;
        this.id = 0;
        this.length = len;
        this.content = (len - HeadSize);
        this.pos = 0;
    }

    public boolean in(){
        return (this.pos < this.next);
    }
    public void skip(int p){
        this.pos += p;
    }
}
