package oppl.gwm.elements;


public class DirectedConnection extends Connection {

	protected int dir;

	public DirectedConnection(int id) {
		this("", id);
	}
	
	public DirectedConnection(String caption, BasicBuildingBlock from, BasicBuildingBlock to, int id) {
		super(caption, from, to, id);
		setArrowDirection(Connection.ARROW_END);
	}

	public DirectedConnection(String caption, int id) {
		this(caption,null,null, id);
//		this(caption,null,null);
	}

	@Override
	public void circleArrowDirection() {
		dir = ((dir+1) % 3) + 1;
		setArrowDirection(dir);
	}
	
	@Override
	public void setArrowDirection(int dir) {
		if (dir != 0) super.setArrowDirection(dir);
	}

/*	public Object clone() {
		Connection c =  new DirectedConnection(t.getText(),(BasicBuildingBlock) from.clone(),(BasicBuildingBlock) to.clone());
		c.setArrowDirection(this.dir);
		return c;
	}*/
	
}
