package oppl.gwm.elements;

import java.awt.image.BufferedImage;
import java.io.File;

import org.jhotdraw.framework.Figure;

public class PictureArtifact extends Artifact {

	protected BufferedImage resource = null;

	
	public PictureArtifact() {
		super();
	}
	
	public PictureArtifact(BasicBuildingBlock representedElement) {
		super(representedElement);
		this.representedElement = representedElement;
	}

	public PictureArtifact(BasicBuildingBlock representedElement, BasicBuildingBlock container) {
		super(representedElement, container);
		this.representedElement = representedElement;
	}

	public BufferedImage getImage() {
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
			if (resource != null) ((BasicBuildingBlock) representedElement).setCaption("Image");
			else ((BasicBuildingBlock) representedElement).setCaption("Artifact "+((BasicBuildingBlock) representedElement).getId());
		}
	}

	public void setImage(BufferedImage resource) {
		this.resource = resource;
	}
	
}
