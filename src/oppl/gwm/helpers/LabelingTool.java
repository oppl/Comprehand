package oppl.gwm.helpers;

import java.awt.event.MouseEvent;

import oppl.gwm.elements.BasicBuildingBlock;

import org.jhotdraw.figures.TextTool;
import org.jhotdraw.framework.DrawingEditor;
import org.jhotdraw.framework.DrawingView;
import org.jhotdraw.framework.Figure;
import org.jhotdraw.framework.FigureEnumeration;
import org.jhotdraw.standard.TextHolder;

public class LabelingTool extends TextTool {

	public LabelingTool(DrawingEditor newDrawingEditor, Figure prototype) {
		super(newDrawingEditor, prototype);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void mouseDown(MouseEvent e, int x, int y)
	{
		setView((DrawingView)e.getSource());

		if (getTypingTarget() != null) {
			editor().toolDone();
			return;
		}

		TextHolder textHolder = null;
		
		FigureEnumeration fe = drawing().figures();
		Figure pressedFigure =  null;
		while (fe.hasNextFigure()) {
			pressedFigure = fe.nextFigure();
			if (pressedFigure.containsPoint(x, y)) break;
		}
		
		if (pressedFigure != null) {
			textHolder = pressedFigure.getTextHolder();
		}

		if ((textHolder != null) && textHolder.acceptsTyping()) {
			// do not create a new TextFigure but edit existing one
			beginEdit(textHolder);
			if (pressedFigure instanceof BasicBuildingBlock) ((BasicBuildingBlock)pressedFigure).displayTextUppermost();
		}
/*		else {
			super.mouseDown(e, x, y);
			// update view so the created figure is drawn before the floating text
			// figure is overlaid. (Note, fDamage should be null in StandardDrawingView
			// when the overlay figure is drawn because a JTextField cannot be scrolled)
			view().checkDamage();
			beginEdit(getCreatedFigure().getTextHolder());
		}*/
	}
	
}
