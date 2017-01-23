package oppl.gwm.elements;



public class UndirectedConnection extends Connection {

	public UndirectedConnection(int id) {
		this("", id);
	}
	
	public UndirectedConnection(String caption, BasicBuildingBlock from, BasicBuildingBlock to, int id) {
		super(caption, from, to,id);
		super.setArrowDirection(Connection.ARROW_NONE);
	}

	public UndirectedConnection(String caption, int id) {
		this(caption,null,null,id);
	}

	@Override
	public void circleArrowDirection() {
	
	}
	
	@Override
	public void setArrowDirection(int dir) {
		
	}
	
/*	public Object clone() {
		Connection c =  new UndirectedConnection(t.getText(),(BasicBuildingBlock) from.clone(),(BasicBuildingBlock) to.clone());
		return c;
	}*/


}
