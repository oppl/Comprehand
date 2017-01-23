package oppl.gwm.elements;

import java.io.File;

import org.jhotdraw.framework.Figure;

public class FileArtifact extends Artifact {

	protected File resource = null;

	
	public FileArtifact() {
		super();
	}
	
	public FileArtifact(BasicBuildingBlock representedElement) {
		super(representedElement);
		this.representedElement = representedElement;
	}

	public FileArtifact(BasicBuildingBlock representedElement, BasicBuildingBlock container) {
		super(representedElement, container);
		this.representedElement = representedElement;
	}

	public File getFile() {
		return resource;
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
			if (resource != null) ((BasicBuildingBlock) representedElement).setCaption("File");
			else ((BasicBuildingBlock) representedElement).setCaption("Artifact "+((BasicBuildingBlock) representedElement).getId());
		}
	}

	public void setFile(File resource) {
		this.resource = resource;
	}
	
}
