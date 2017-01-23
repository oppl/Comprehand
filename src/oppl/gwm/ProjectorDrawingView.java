package oppl.gwm;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;

import oppl.gwm.elements.BasicBuildingBlock;
import oppl.gwm.elements.Red;
import oppl.gwm.helpers.ThicknessEllipseFigure;
import oppl.gwm.helpers.ThicknessLineFigure;

import org.jhotdraw.figures.ArrowTip;
import org.jhotdraw.figures.GroupFigure;
import org.jhotdraw.figures.TextFigure;
import org.jhotdraw.framework.DrawingEditor;
import org.jhotdraw.framework.FigureAttributeConstant;
import org.jhotdraw.util.ColorMap;

public class ProjectorDrawingView extends ScreenDrawingView {

	public ProjectorDrawingView(DrawingEditor editor) {
		super(editor);
		// TODO Auto-generated constructor stub
	}

	public ProjectorDrawingView(DrawingEditor editor, int width, int height) {
		super(editor, width, height);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addOrAlterBasicBuildingBlock(BasicBuildingBlock b) {
		// TODO Auto-generated method stub
		if (!b.onlyCaption()) b.displayCaptionOnly();
		super.addOrAlterBasicBuildingBlock(b);
	}

	@Override
	public boolean createArtifact(oppl.reactivisionbridge.Artifact a) {
		return false;
	}

	@Override
	public void displaySnapshot(Snapshot snapshot) {
	}

@Override
	public void drawAddSign(int id) {
		this.removeAll();
		this.repaintAll();
		this.addAll(basicBuildingBlocks.values());

		BasicBuildingBlock b = history.getCurrentSnapshot().basicBuildingBlocks.get(new Integer(id));
		if (b == null) return;

		ThicknessEllipseFigure f = new ThicknessEllipseFigure(new Point(b.dummy.displayBox().x,b.dummy.displayBox().y),
				new Point(b.dummy.displayBox().x+b.dummy.displayBox().width,b.dummy.displayBox().y+b.dummy.displayBox().height));
		f.setThickness(5);
		f.setAttribute(FigureAttributeConstant.FILL_COLOR, ColorMap.color("None"));
		f.setAttribute(FigureAttributeConstant.FRAME_COLOR, Color.green);

		TextFigure t = new TextFigure();
		t.setText(b.getTextCaption());
		t.setAttribute(FigureAttributeConstant.FONT_SIZE, new Integer(30));
		t.setAttribute(FigureAttributeConstant.FONT_STYLE, Font.BOLD);
		t.setAttribute(FigureAttributeConstant.TEXT_COLOR, Color.WHITE);
		
		GroupFigure g = new GroupFigure();
		g.add(f);
		g.add(t);

		Rectangle r = f.displayBox();
		r.x = r.x + r.width / 2 - t.displayBox().width/2;
		r.y = r.y + r.height / 2 - t.displayBox().height/2;
		t.displayBox(r);
		g.bringToFront(t);
		
		add(g);
		redraw();
		System.out.println("PD - Add Sign added for "+b.getId());
	}

	@Override
	public void drawMoveSign(int id) {
		this.removeAll();
		this.repaintAll();
		this.addAll(basicBuildingBlocks.values());

		BasicBuildingBlock target = history.getCurrentSnapshot().basicBuildingBlocks.get(new Integer(id));
		BasicBuildingBlock b = basicBuildingBlocks.get(new Integer(id));
		if (b == null) return;

		ThicknessLineFigure l = new ThicknessLineFigure();
		ThicknessEllipseFigure f = new ThicknessEllipseFigure(new Point(target.displayBox().x,target.displayBox().y),
				new Point(target.displayBox().x+target.displayBox().width,target.displayBox().y+target.displayBox().height));

//		ThicknessEllipseFigure f = new ThicknessEllipseFigure(new Point(adapted_x-markerTolerance/2,adapted_y-markerTolerance/2),
//				new Point(adapted_x+markerTolerance/2,adapted_y+markerTolerance/2));
		l.setThickness(5);
		l.setAttribute(FigureAttributeConstant.FRAME_COLOR, Color.red);
		l.startPoint(b.getDrawingPosition().x,b.getDrawingPosition().y);
		l.endPoint(target.displayBox().x+target.displayBox().width/2, target.displayBox().y+target.displayBox().height/2);
		l.setEndDecoration(new ArrowTip());
		add(f);
		add(l);
		redraw();
		System.out.println("PD - Move Sign added for "+b.getId());
	}

	/*	@Override
	protected void watchTable() {
	}
*/
	@Override
	public void drawRemoveSign(int id) {
		this.removeAll();
		this.repaintAll();
		this.addAll(basicBuildingBlocks.values());

		BasicBuildingBlock b = basicBuildingBlocks.get(new Integer(id));
		if (b == null) return; // insert b.dummy.displayBox().width / height below !
		ThicknessEllipseFigure f = new ThicknessEllipseFigure(new Point(b.dummy.displayBox().x,b.dummy.displayBox().y),
				new Point(b.dummy.displayBox().x+b.dummy.displayBox().width,b.dummy.displayBox().y+b.dummy.displayBox().height));
//		ThicknessEllipseFigure f = new ThicknessEllipseFigure(new Point(b.displayBox().x-b.displayBox().width*5,b.displayBox().y-b.displayBox().width*3),
	//										new Point(b.displayBox().x+b.displayBox().width*5,b.displayBox().y+b.displayBox().width*3));
		f.setThickness(5);
		f.setAttribute(FigureAttributeConstant.FILL_COLOR, ColorMap.color("None"));
		f.setAttribute(FigureAttributeConstant.FRAME_COLOR, Color.red);
		remove(b);
		b.displayCaptionOnly();
		b.addMarker(f);
		add(b);
		redraw();
		System.out.println("PD - Remove Sign added for "+b.getId());
	}

	@Override
	public void openArtifact(int id) {
	}

	@Override
	public void closeArtifact(int id) {
	}

	@Override
	public void removeGlass() {
		this.removeAll();
		this.repaintAll();
		this.addAll(basicBuildingBlocks.values());

		restoreMode = false;
		TextFigure notify = new TextFigure();
		notify.setText("Finished - Remove Glass of Time!");
		notify.moveBy(this.getBounds().width/2-notify.displayBox().width/2, this.getBounds().height/2);
		notify.setAttribute(FigureAttributeConstant.FONT_SIZE, 25);
		notify.setAttribute(FigureAttributeConstant.TEXT_COLOR, Color.white);
		this.add(notify);
		redraw();		
	}
	
	@Override
	protected void init() {
		super.init();
		markerTolerance = 70;

		OFFSET_X = 70;
		OFFSET_Y = 75;
		SCALE_X = 1110;
		SCALE_Y = 955;
		CONNECTOR_THICKNESS = 4;
		CONNECTOR_COLOR = Color.white;
	}

}
