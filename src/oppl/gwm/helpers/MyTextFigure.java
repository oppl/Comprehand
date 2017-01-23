package oppl.gwm.helpers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;

import oppl.gwm.elements.BasicBuildingBlock;

import org.jhotdraw.figures.TextFigure;
import org.jhotdraw.framework.Figure;
import org.jhotdraw.framework.FigureAttributeConstant;

public class MyTextFigure extends TextFigure {

	protected Figure container;
	
	transient protected int fHeight;

	// cache of the TextFigure's size
	transient protected boolean fSizeIsDirty = true;
	transient protected int fWidth;
	protected String text2 = null;
		
	public MyTextFigure(Figure container) {
		super();
		this.container = container;
	}
	
	@Override
	public Rectangle displayBox() {
		if (container instanceof BasicBuildingBlock && 
			((BasicBuildingBlock)container).onlyCaption() &&
			this.text2 != null) {			
			Rectangle r = super.displayBox();
			r.y += 30;
			return r;
		}
		else {
			return super.displayBox();
		}
	}
	
	@Override
	public void drawFrame(Graphics g) {

			g.setFont(getFont());
			g.setColor((Color) getAttribute(FigureAttributeConstant.TEXT_COLOR));
			FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
			Rectangle r = displayBox();
			if (text2!=null) {
				int offsetLine2 = - metrics.stringWidth(text2)/2;
				int offsetLine1 = - metrics.stringWidth(super.getText())/2;
				g.drawString(super.getText(), r.x+offsetLine1, r.y - metrics.getAscent()/2);
				g.drawString(text2, r.x+offsetLine2, r.y - metrics.getAscent()/2 + Math.round(metrics.getHeight()*1.2f));
			}
			else {
				int offsetLine1 = metrics.stringWidth(super.getText())/2;
				g.drawString(super.getText(), r.x-offsetLine1, r.y + metrics.getAscent()/2);
				
			}
	}
	
	@Override
	public String getText() {
		if (text2 == null) return super.getText();
		return new String(super.getText()+" "+text2);
	}
	
	public void repairTextPosition() {
		updateLocation();
	}

	
	@Override
	public void setText(String newText) {
		container.willChange();	
		if (newText == null) return;
		if (newText.indexOf(" ")==-1) {
			super.setText(newText);
			text2 = null;
		}
		else {
			int middle = newText.length()/2;
			int pos = newText.indexOf(" ", middle);
			int pos2 = newText.substring(0, middle).lastIndexOf(" ");
			if (pos == -1) pos = pos2;
			else if ((pos2 != -1) && (pos - middle > middle - pos2)) pos = pos2;
			super.setText(newText.substring(0, pos));
			text2 = newText.substring(pos+1, newText.length());
		}
		container.changed();
	}
	
	@Override
	protected void markDirty() {
		super.markDirty();
		this.fSizeIsDirty = true;
	}
	
	
	@Override
	protected Dimension textExtent() {
		if (text2 == null) return super.textExtent();

		if (!fSizeIsDirty) {
			return new Dimension(fWidth, fHeight);
		}
		FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
		fWidth = metrics.stringWidth(super.getText());
		int width2 = metrics.stringWidth(text2);
		fWidth = Math.max(fWidth, width2);
		fHeight = Math.round(metrics.getHeight()*2.2f);
		fSizeIsDirty = false;
		return new Dimension(fWidth, fHeight);
	}
	

	/**
	 * Updates the location relative to the connected figure.
	 * The TextFigure is centered around the located point.
	 */
	@Override
	protected void updateLocation() {
		if (getLocator() != null) {
			Point p = getLocator().locate(getObservedFigure());

			p.x -= size().width / 2 + displayBox().x;
			p.y -= size().height / 2 + displayBox().y;
			if (p.x != 0 || p.y != 0) {
				//willChange();
				basicMoveBy(p.x, p.y);
				//changed();
			}
		}
	}

}
