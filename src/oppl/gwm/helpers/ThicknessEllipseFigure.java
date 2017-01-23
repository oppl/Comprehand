package oppl.gwm.helpers;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import org.jhotdraw.figures.EllipseFigure;

public class ThicknessEllipseFigure extends EllipseFigure {

	private int thickness = 1;
	
	
	public ThicknessEllipseFigure() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ThicknessEllipseFigure(Point origin, Point corner) {
		super(origin, corner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void drawFrame(Graphics g) {
		g.setColor(getFrameColor());
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke( thickness,
				BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER ));
				Rectangle r = displayBox();
		g2.drawOval(r.x, r.y, r.width-1, r.height-1);
	}

	public int getThickness() {
		return thickness;
	}

	public void setThickness(int thickness) {
		this.thickness = thickness;
	}
	
	
}
