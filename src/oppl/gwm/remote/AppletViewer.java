package oppl.gwm.remote;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import oppl.gwm.elements.BasicBuildingBlock;
import oppl.gwm.helpers.LabelingTool;

import org.jhotdraw.applet.DrawApplet;
import org.jhotdraw.framework.Drawing;
import org.jhotdraw.framework.DrawingView;
import org.jhotdraw.framework.Tool;

public class AppletViewer extends DrawApplet {

	private DrawingView dv = null;
	
	public DrawingView getDrawingView() {
		return dv;
	}

	public void reset() {
		dv.setDrawing(this.createDrawing());
	}

	@Override
	protected DrawingView createDrawingView() {
		dv = new AppletDrawingView(this, 1000, 700);
		dv.setDrawing(this.createDrawing());
		// notify listeners about created view when the view is added to the
		// desktop
		// fireViewCreatedEvent(newDrawingView);
		return dv;

	}
	
	@Override
	protected void createTools(JPanel palette) {
		super.createTools(palette);
		Tool tool = new LabelingTool(this, new BasicBuildingBlock());
		palette.add(createToolButton(IMAGES + "TEXT", "Text Tool", tool));
	}

}
