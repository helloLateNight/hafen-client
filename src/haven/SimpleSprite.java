package haven;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class SimpleSprite extends SimpleDrawable {
	Sprite spr;
	
	public SimpleSprite(Gob gob, String res) {
		super(gob, res);
		spr = Resource.loadsprite(res);
	}
	
	public SimpleSprite(Gob gob, Sprite spr) {
		super(gob, "");
		this.spr = spr;
	}
	
	public Coord getoffset() {
		return(spr.cc);
	}
	
	public boolean checkhit(Coord c) {
		int cl = spr.img.getRGB(c.x, c.y);
		return(Utils.rgbm.getAlpha(cl) >= 128);
	}
	
	public Coord getsize() {
		return(Utils.imgsz(spr.img));
	}
	
	public void draw(Graphics g, Coord sc) {
		g.drawImage(spr.img, sc.x - spr.cc.x, sc.y - spr.cc.y, null);
	}
	
	public void draw2(BufferedImage t, Coord sc) {
		if(spr.isgay)
			Utils.drawgay(t, spr.img, sc.add(spr.cc.inv()));
		else
			draw(t.getGraphics(), sc);
	}
	
	public Sprite shadow() {
		return(spr.shadow);
	}
}
