/**
 * 
 */
package oppl.reactivisionbridge;

import java.awt.Point;

public class Block {
	private int id;
	private boolean isOpen;
	private int old_x, old_y;
	private int x, y;
	private int rot;
	
	private String caption;
	
	public int getRot() {
		return rot;
	}

	public void setRot(int rot) {
		this.rot = rot;
	}

	public Block(int id) {
		this.id = id;
		x = -1;
		old_x = -1;
		y = -1;
		old_y = -1;
		isOpen = false;
		rot = 0;
		caption = "";
	}

	@Override
	public Block clone() {
		Block b = new Block(this.id);
		b.setX(this.getX());
		b.setY(this.getY());
		b.setRot(this.getRot());
		b.setCaption(this.getCaption());
		return b;		
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public void close() {
		isOpen = false;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof Block && ((Block) o)
				.getId() == this.id);
	}

	public int getId() {
		return id;
	}

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	
	public void open() {
		isOpen = true;
	}
	
	public boolean positionChangedBy(int distance) {
		Point old = new Point(old_x, old_y);
		Point current = new Point(x, y);
		if (current.distance(old) > distance) return true;
		return false;
	}
	
	public void setX(int x) {
		this.old_x = this.x;
		this.x = x;
	}
	
	public void setY(int y) {
		this.old_y = this.y;
		this.y = y;
	}

	
}