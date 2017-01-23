package oppl.reactivisionbridge;

public class Artifact {
	
	public static int MODEL = 0;
	public static int FILE = 1;
	public static int PICTURE = 2;
	
	
	private int id;
	private int container_id;
	private Object representedObject;
	private int type;
	
	public Artifact(int id) {
		this.id = id;
		this.container_id = -1;
		this.representedObject = null;
	}
	
	public Artifact(int id, Object repesentedObject) {
		this.id = id;
		this.container_id = -1;
		this.representedObject = representedObject;		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getContainer_id() {
		return container_id;
	}
	public void setContainer_id(int container_id) {
		this.container_id = container_id;
	}
	public Object getRepresentedObject() {
		return representedObject;
	}
	public void setRepresentedObject(Object representedObject) {
		this.representedObject = representedObject;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
	
}
