/*
 * fv3tk
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
package fv3.tk;

import javax.media.opengl.GL;

/**
 * GL version info to supplement the {@link javax.media.opengl.GLBase}
 * API, for example in the resolution of point versions like "1.1".
 * 
 * @author jdp
 */
public final class Fv3glv {

    private static volatile Fv3glv Instance;
    public static Fv3glv Instance(GL gl){
        if (null == Instance)
            Instance = new Fv3glv(gl);
        return Instance;
    }


    public final String string;
    public final int major, minor;

    /**
     * When "isKnown" is true, the subsequent "has*" booleans have
     * correctly classified the GL version.  When false, the
     * subsequent "has*" booleans are not correct.
     */
    public final boolean isKnown, isNotKnown;
    public final boolean has10;
    public final boolean has11;
    public final boolean has12;
    public final boolean has13;
    public final boolean has14;
    public final boolean has15;
    public final boolean has20;


    private Fv3glv(GL gl){
        super();
        String string = gl.glGetString(GL.GL_VERSION);
        this.string = string;
        java.util.StringTokenizer strtok = new java.util.StringTokenizer(string,"._-, ");
        String major = strtok.nextToken();
        this.major = Integer.parseInt(major);
        String minor = strtok.nextToken();
        this.minor = Integer.parseInt(minor);
        boolean isKnown = false;
        boolean has10 = false;
        boolean has11 = false;
        boolean has12 = false;
        boolean has13 = false;
        boolean has14 = false;
        boolean has15 = false;
        boolean has20 = false;

        switch (this.major){
        case 1:
            switch (this.minor){
            case 0:
                isKnown = true;
                has10 = true;
                System.out.println("GL Version: '"+string+"' has10");
                break;
            case 1:
                isKnown = true;
                has10 = true;
                has11 = true;
                System.out.println("GL Version: '"+string+"' has11");
                break;
            case 2:
                isKnown = true;
                has10 = true;
                has11 = true;
                has12 = true;
                System.out.println("GL Version: '"+string+"' has12");
                break;
            case 3:
                isKnown = true;
                has10 = true;
                has11 = true;
                has12 = true;
                has13 = true;
                System.out.println("GL Version: '"+string+"' has13");
                break;
            case 4:
                isKnown = true;
                has10 = true;
                has11 = true;
                has12 = true;
                has13 = true;
                has14 = true;
                System.out.println("GL Version: '"+string+"' has14");
                break;
            case 5:
                isKnown = true;
                has10 = true;
                has11 = true;
                has12 = true;
                has15 = true;
                System.out.println("GL Version: '"+string+"' has15");
                break;
            default:
                System.out.println("GL Version: '"+string+"' isNotKnown");
                break;
            }
            break;
        case 2:
            isKnown = true;
            has10 = true;
            has11 = true;
            has12 = true;
            has13 = true;
            has14 = true;
            has15 = true;
            has20 = true;
            System.out.println("GL Version: '"+string+"' has20");
            break;
        default:
            System.out.println("GL Version: '"+string+"' isNotKnown");
            break;
        }

        this.isKnown = isKnown;
        this.isNotKnown = (!isKnown);
        this.has10 = has10;
        this.has11 = has11;
        this.has12 = has12;
        this.has13 = has13;
        this.has14 = has14;
        this.has15 = has15;
        this.has20 = has20;
    }


    public int hashCode(){
        return (this.major << 16 | this.minor);
    }
    public String toString(){
        return this.string;
    }
    public int compareTo(int major, int minor){
        if (this.major == major){
            if (this.minor == minor)
                return 0;
            else if (this.minor < minor)
                return -1;
            else
                return 1;
        }
        else if (this.major < major)
            return -1;
        else
            return 1;
    }
    public boolean equalsOrNewer(int major, int minor){
        return (0 <= this.compareTo(major,minor));
    }
    public boolean equals(int major, int minor){
        return (0 == this.compareTo(major,minor));
    }
    public boolean equals(Object ano){
        if (ano == this)
            return true;
        else if (null == ano)
            return false;
        else
            return this.string.equals(ano.toString());
    }
}
