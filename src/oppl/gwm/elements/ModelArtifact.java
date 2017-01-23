package oppl.gwm.elements;

import java.io.File;

import oppl.gwm.Snapshot;

import org.jhotdraw.framework.Figure;

public class ModelArtifact extends Artifact {

//	protected Snapshot resource = null;

	
	public ModelArtifact() {
		super();
	}
	
	public ModelArtifact(BasicBuildingBlock representedElement) {
		super(representedElement);
		this.representedElement = representedElement;
	}

	public ModelArtifact(BasicBuildingBlock representedElement, BasicBuildingBlock container) {
		super(representedElement, container);
		this.representedElement = representedElement;
	}

	public Snapshot getSnapshot() {
		return (Snapshot) resource;
	}

	@Override
	public void setContainer(BasicBuildingBlock b) {
		super.setContainer(b);
		((BasicBuildingBlock) representedElement).setScale(0.7f); 
	}

	@Override
	public void setRepresentedElement(BasicBuildingBlock representedElement) {
		if (representedElement instanceof BasicBuildingBlock) {
			super.setRepresentedElement(representedElement);
			if (resource != null) ((BasicBuildingBlock) representedElement).setCaption("Details");
			else ((BasicBuildingBlock) representedElement).setCaption("Artifact "+((BasicBuildingBlock) representedElement).getId());
		}
	}

	public void setSnapshot(Snapshot resource) {
		this.resource = resource;
	}
	
}
