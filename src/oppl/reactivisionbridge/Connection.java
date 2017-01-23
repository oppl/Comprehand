/**
 * 
 */
package oppl.reactivisionbridge;

public class Connection {
	public static int ARROW_BOTH = 3;
	public static int ARROW_END = 2;
	public static int ARROW_NONE = 0;
	public static int ARROW_START = 1;
	public static int id_counter = 200;
	
	int direction;
	String caption;
	
	public static int getNextID() {
		id_counter++;
		return id_counter;
	}
	
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	int from, to;
	int uid;
	
	public Connection() {
		this.from = -1;
		this.to = -1;
		this.direction = Connection.ARROW_NONE;
		this.uid = Connection.getNextID(); //(int) System.currentTimeMillis() % Integer.MAX_VALUE;
	}
	
	public Connection(int from, int to, int direction) {
		this();
		this.from = from;
		this.to = to;
		this.direction = direction;
	}

	@Override
	public Connection clone() {
		Connection c = new Connection(this.getFrom(), this.getTo(), this.direction);
		c.uid = this.uid;
		if (this.caption != null) c.caption = new String(this.caption);
		return c;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof Connection && ((Connection) o).getID() == this.uid);
	}

	public int getFrom() {
		return from;
	}

	public int getID() {
		return this.uid;
	}
	
	public int getTo() {
		return to;
	}
	
	public void setFrom(int from) {
		this.from = from;
	}
	
	public void setTo(int to) {
		this.to = to;
	}

}