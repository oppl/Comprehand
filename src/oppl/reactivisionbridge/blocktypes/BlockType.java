package oppl.reactivisionbridge.blocktypes;

public abstract class BlockType {
	
	protected String typeName; 
	
	public BlockType() {
		this.typeName = new String("none");
	}
	
	public BlockType(String name) {
		this.typeName = name;
	}
	
	public abstract boolean isOfType(int id);

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	
}
