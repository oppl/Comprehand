package oppl.gwm.elements;

import java.awt.Point;
import java.io.File;

import javax.imageio.ImageIO;

import oppl.gwm.ScreenDrawingView;
import oppl.reactivisionbridge.Block;
import oppl.reactivisionbridge.ReactivisionConstants;

import org.jhotdraw.figures.ImageFigure;

public class Blue extends BasicBuildingBlock {

	public Blue() {
		this(new Block(-1), "none",null);
	}

	public Blue(Block b, Object caption, ScreenDrawingView container) {
		super(b, caption, container);
	}
	
	protected void setTypeImage() {
		i = null;
		img = null;
		try {
			img = ImageIO.read(new File(
					ReactivisionConstants.BASEPATH+"blue.gif"));
			i = new ImageFigure(img,
					ReactivisionConstants.BASEPATH+"blue.gif", new Point(0, 0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
