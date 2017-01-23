package oppl.gwm.elements;

import java.awt.Point;
import java.io.File;

import javax.imageio.ImageIO;

import oppl.gwm.ScreenDrawingView;
import oppl.reactivisionbridge.Block;
import oppl.reactivisionbridge.ReactivisionConstants;

import org.jhotdraw.figures.ImageFigure;

public class Yellow extends BasicBuildingBlock {

	public Yellow() {
		this(new Block(-1), "none",null);
	}

	public Yellow(Block b, Object caption, ScreenDrawingView container) {
		super(b, caption, container);
	}
			
	protected void setTypeImage() {
		i = null;
		img = null;
		try {
			img = ImageIO.read(new File(
					ReactivisionConstants.BASEPATH+"yellow.gif"));
			i = new ImageFigure(img,
					ReactivisionConstants.BASEPATH+"yellow.gif", new Point(0, 0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
