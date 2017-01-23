package oppl.gwm.helpers;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jhotdraw.figures.LineFigure;

public class ThicknessLineFigure extends LineFigure {

	private int thickness = 1;
	
	@Override
	public void draw(Graphics g) {
		g.setColor(getFrameColor());
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke( thickness,
				BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER ));
		super.draw(g2);
	}

	public int getThickness() {
		return thickness;
	}

	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

}
