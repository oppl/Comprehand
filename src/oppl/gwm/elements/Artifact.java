package oppl.gwm.elements;

import java.io.File;

import org.jhotdraw.framework.Figure;

public class Artifact {
	
	protected BasicBuildingBlock container;
	protected BasicBuildingBlock representedElement = null;
	protected Object resource;
	
	public Artifact() {	
	}
	
	public Artifact(BasicBuildingBlock representedElement) {
		this.representedElement = representedElement;
	}
	
	public Artifact(BasicBuildingBlock representedElement, BasicBuildingBlock container) {
		this(representedElement);
		this.container = container;
	}

	public int getContainerID() {
		if (container == null) return -1;
		return container.getId();
	}
	
	public BasicBuildingBlock getRepresentedElement() {
		return representedElement;
	}

	public void setContainer(BasicBuildingBlock b) {
		this.container = b;
		b.addArtifact(this);
	}
	
	public void setRepresentedElement(BasicBuildingBlock representedElement) {
		this.representedElement = representedElement;
	}

	public void setResource(Object resource) {
		this.resource = resource;
	}
	
	public Object getResource() {
		return this.resource;
	}
	
	public Object clone() {
		Artifact a = null;
		if (this instanceof FileArtifact) a = new FileArtifact((BasicBuildingBlock) this.representedElement.clone(),null);
		if (this instanceof PictureArtifact) a = new PictureArtifact((BasicBuildingBlock) this.representedElement.clone(),null);
		if (this instanceof ModelArtifact) a = new ModelArtifact((BasicBuildingBlock) this.representedElement.clone(),null);
		a.setResource(this.getResource());
		return a;
	}
}
