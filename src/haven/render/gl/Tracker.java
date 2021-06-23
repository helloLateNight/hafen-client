/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven.render.gl;

import com.jogamp.opengl.*;
import haven.Warning;

public class Tracker {
    public static final ThreadLocal<Tracker> track = new ThreadLocal<>();

    public GLBuffer ebo;
    public GLBuffer vbo;
    public GLBuffer pbo;

    public static void bindbuf(BGL.ID ob, int tgt) {
	Tracker tr = track.get();
	if((tr != null) && (ob instanceof GLBuffer)) {
	    GLBuffer buf = (GLBuffer)ob;
	    switch(tgt) {
	    case GL3.GL_ELEMENT_ARRAY_BUFFER: {tr.ebo = buf; break;}
	    case GL3.GL_ARRAY_BUFFER: {tr.vbo = buf; break;}
	    case GL3.GL_PIXEL_PACK_BUFFER: {tr.pbo = buf; break;}
	    }
	}
    }

    public static void bufdata(int tgt, long sz) {
	Tracker tr = track.get();
	if(tr != null) {
	    GLBuffer buf;
	    switch(tgt) {
	    case GL3.GL_ELEMENT_ARRAY_BUFFER: {buf = tr.ebo; break;}
	    case GL3.GL_ARRAY_BUFFER: {buf = tr.vbo; break;}
	    case GL3.GL_PIXEL_PACK_BUFFER: {buf = tr.pbo; break;}
	    default: {return;}
	    }
	    if(buf == null) {
		Warning.warn("data upload to unbound buffer target");
		return;
	    }
	    if(buf.state != 1) {
		Warning.warn("data upload to buffer in illegal state " + buf.state);
		return;
	    }
	    buf.track_sz = sz;
	}
    }

    public static void bufuse(int tgt, long sz) {
	Tracker tr = track.get();
	if(tr != null) {
	    GLBuffer buf;
	    switch(tgt) {
	    case GL3.GL_ELEMENT_ARRAY_BUFFER: {buf = tr.ebo; break;}
	    case GL3.GL_ARRAY_BUFFER: {buf = tr.vbo; break;}
	    case GL3.GL_PIXEL_PACK_BUFFER: {buf = tr.pbo; break;}
	    default: {return;}
	    }
	    if(buf == null) {
		Warning.warn("reference to unbound buffer target");
		return;
	    }
	    if(buf.state != 1) {
		Warning.warn("reference to buffer in illegal state " + buf.state);
		return;
	    }
	    if(buf.track_sz < 0)
		Warning.warn("reference to data-less buffer");
	    else if(buf.track_sz < sz)
		Warning.warn("reference to buffer with insufficent data (" + buf.track_sz + " < " + sz + ")");
	}
    }
}
