package oppl.gwm.elements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import oppl.gwm.helpers.MyTextFigure;
import oppl.gwm.helpers.ThicknessLineConnection;

import org.jhotdraw.figures.ArrowTip;
import org.jhotdraw.figures.GroupFigure;
import org.jhotdraw.figures.LineDecoration;
import org.jhotdraw.figures.PolyLineFigure;
import org.jhotdraw.framework.Connector;
import org.jhotdraw.framework.Figure;
import org.jhotdraw.framework.FigureAttributeConstant;
import org.jhotdraw.standard.CompositeFigure;
import org.jhotdraw.standard.TextHolder;

public class Connection extends ThicknessLineConnection implements Cloneable {
	public static int ARROW_BOTH = 3;
	public static int ARROW_END = 2;
	public static int ARROW_NONE = 0;
	public static int ARROW_START = 1;
	
	protected int dir;
	
	protected Color baseColor;
	
	BasicBuildingBlock from, to;
	int id;
	MyTextFigure t;
	
	public Connection(int id) {
		this("", id);
	}
	
	public boolean isDirected() {
		return (dir>0);
	}
	
	public Connection(String caption, BasicBuildingBlock from, BasicBuildingBlock to, int id) {
		super();
		dir = Connection.ARROW_NONE;
		connect(from, to);
		this.id = id;
		this.t = new MyTextFigure(this);
		this.t.setText(caption);
		if (from != null && from.onlyCaption()) this.bigWhiteText(); 
		baseColor = null;
	}

	private void bigWhiteText() {
		t.setAttribute(FigureAttributeConstant.FONT_SIZE, new Integer(20));
		t.setAttribute(FigureAttributeConstant.FONT_STYLE, Font.BOLD);
		t.setAttribute(FigureAttributeConstant.TEXT_COLOR, Color.WHITE);
	}
	public Connection(String caption, int id) {
		this(caption,null, null, id);
	}
	
	public void toggleMarker(int state) {
		if (baseColor == null) baseColor = (Color) this.getAttribute(FigureAttributeConstant.FRAME_COLOR);
		if (state == 1) {
			baseColor = (Color) this.getAttribute(FigureAttributeConstant.FRAME_COLOR);
			this.setAttribute(FigureAttributeConstant.FRAME_COLOR, Color.GREEN);
		}
		else {
	//		System.out.println(baseColor);
			this.setAttribute(FigureAttributeConstant.FRAME_COLOR, baseColor);			
		}
	}
	
	public void circleArrowDirection() {
		dir = (dir+1) % 4;
		setArrowDirection(dir);
	}

	@Override
	public Object clone() {
		Connection newCon = null;
		if (t != null) newCon= new Connection(t.getText(), this.id);
/*		c= new Connection("", this.id);
		c.setArrowDirection(this.dir);
		c.setFrom(from);
		c.setTo(to);
		c.setThickness(thickness);
		c.setAttribute(FigureAttributeConstant.FRAME_COLOR, this.getAttribute(FigureAttributeConstant.FRAME_COLOR));
		c.fPoints = new ArrayList<Point>();
		c.fPoints.add(this.fPoints.get(0));
		c.fPoints.add(this.fPoints.get(1));*/
		if (this.isDirected() == false)
			newCon = new UndirectedConnection("", this.getFrom(),
					this.getTo(), this.getId());
		else
			newCon = new DirectedConnection("", this.getFrom(),
					this.getTo(), this.getId());
		if (newCon != null) {
			newCon
					.setAttribute(
							FigureAttributeConstant.FRAME_COLOR,
							this.getAttribute(FigureAttributeConstant.FRAME_COLOR));
			newCon.setThickness(this.getThickness());
		}
		return newCon;
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		Rectangle box = super.displayBox();
		Rectangle tbox = t.displayBox();
		int x = box.x + box.width / 2;
		int y = box.y + box.height / 2;
		if (box.width < box.height) x = x + tbox.width / 2 + 5;
		else y = y + tbox.height / 2 + 5;
		t.displayBox(new Point(x,y), new Point(x+tbox.width, y+tbox.height));
		t.draw(g);
	}
	
	public Rectangle displayBox() {
		Rectangle r = super.displayBox();
		r.add(t.displayBox());
		return r;
	}
	
	public GroupFigure getEquivalentLine() {
		GroupFigure c = new GroupFigure();
		PolyLineFigure p = new PolyLineFigure();
		Iterator<Point> i = this.points();
		while (i.hasNext()) {
			Point point = i.next();
			p.addPoint(point.x, point.y);
		}
		p.setStartDecoration(this.getStartDecoration());
		p.setEndDecoration(this.getEndDecoration());
		p.setAttribute(
				FigureAttributeConstant.FRAME_COLOR,
				this.getAttribute(FigureAttributeConstant.FRAME_COLOR));
		c.add(p);
		MyTextFigure newT = new MyTextFigure(p);
		newT.setText(t.getText());
		newT.displayBox(t.displayBox());
		c.add(newT);
		return c;
	}
	
	public void connect(BasicBuildingBlock from, BasicBuildingBlock to) {
		this.from = from;
		this.to = to;
/*		if (from != null && to != null) {
			this.connectStart(from.connectorAt(from.center().x,from.center().y));
			this.connectEnd(to.connectorAt(to.center().x, to.center().y));
			this.updateConnection();
		}
		this.changed();*/
		if (from != null && to != null) {
			int sx = from.displayBox().x;
			int sy = from.displayBox().y;
			int ex = to.displayBox().x;
			int ey = to.displayBox().y;
			connectStart(findConnector(sx, sy, from));
			connectEnd(findConnector(ex, ey, to));
			if (getStartConnector() != null && getEndConnector() != null) {
				this.startPoint(sx, sy);
				this.endPoint(ex, ey);
//				setAddedFigure(view().add(getConnection()));
//				this.changed();
			}
		}

	}
	
	public void delete() {
		this.disconnectStart();
		this.disconnectEnd();
//		this.startPoint(-1, -1);
//		this.endPoint(-1, -1);
		this.updateConnection();
//		this.changed();
	}

	public BasicBuildingBlock getFrom() {
		return from;
	}
	
	public int getId() {
		return id;
//		return (new String(Integer.toString(from.getId())+Integer.toString(to.getId()))).hashCode();
	}

	@Override
	public TextHolder getTextHolder() {
		return t;
	}
	
	public BasicBuildingBlock getTo() {
		return to;
	}

	public void setArrowDirection(int dir) {
		LineDecoration start = null, end = null;
		if (dir == ARROW_START || dir == ARROW_BOTH) start = new MyArrowTip();
		if (dir == ARROW_END || dir == ARROW_BOTH) end = new MyArrowTip();	
		setStartDecoration(start);
		setEndDecoration(end);
		this.dir = dir;
		this.changed();
	}

	public int getArrowDirection() {
		return dir;
	}
	
	public void setCaption(String caption) {
		t.setText(caption);
	}
	
	public void setFrom(BasicBuildingBlock from) {
		this.from = from;
		if (from != null && from.onlyCaption()) this.bigWhiteText();

	}

	public void setTo(BasicBuildingBlock to) {
		this.to = to;
		if (to != null && to.onlyCaption()) this.bigWhiteText();

	}

	protected Connector findConnector(int x, int y, Figure f) {
		return f.connectorAt(x, y);
	}

	private class MyArrowTip extends ArrowTip {
		
		public MyArrowTip() {
			super(0.40, 14, 14);
		}
	}
}
