package oppl.gwm.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import oppl.reactivisionbridge.Block;
import oppl.reactivisionbridge.ReactivisionConstants;
import oppl.gwm.ScreenDrawingView;
import oppl.gwm.helpers.MyPictureTextFigure;
import oppl.gwm.helpers.ThicknessEllipseFigure;

import org.jhotdraw.figures.GroupFigure;
import org.jhotdraw.figures.ImageFigure;
import org.jhotdraw.figures.TextFigure;
import org.jhotdraw.framework.Figure;
import org.jhotdraw.framework.FigureAttributeConstant;
import org.jhotdraw.framework.FigureChangeEvent;
import org.jhotdraw.standard.TextHolder;
import org.jhotdraw.util.ColorMap;

public class BasicBuildingBlock extends GroupFigure implements Cloneable {

	public final static float CLOSED_SCALE_FACTOR = 0.5f;
	public final static float OPEN_SCALE_FACTOR = 2.0f;

	private List        fConnectors;
	private boolean     fConnectorsVisible;
	protected long buildingSession;
	protected boolean connectionCandidate;
	protected Set<Artifact> containedArtifacts = Collections
			.synchronizedSet(new HashSet<Artifact>());
	protected boolean displayBackgroundImage = true;
	
	protected ScreenDrawingView container;
	protected ImageFigure i;

	protected Block block;
//	protected int id;

	// protected boolean visible;

	protected BufferedImage img;
	protected Figure marker;
	
	protected boolean open;
	protected Object originalFontSize;

	protected double rotation;
	protected float scale;
	
	public ImageFigure dummy;
	protected BufferedImage dummyImg;
	
	protected MyPictureTextFigure t;
	protected TextFigure containsArtifacts;
	
	public BasicBuildingBlock() {
		this(new Block(-1), "none",null);
	}

	public BasicBuildingBlock(Block b, Object caption, ScreenDrawingView container) {
		super();
		this.container = container;
		this.block = b;
		open = false;
		connectionCandidate = false;
		buildingSession = 0;
		dummy = null;
		BufferedImage dummyImg = null;
		try {
			dummyImg = ImageIO.read(new File(
					ReactivisionConstants.BASEPATH+"dummy.gif"));
			dummy = new ImageFigure(dummyImg,
					ReactivisionConstants.BASEPATH+"dummy.gif", new Point(0, 0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		setTypeImage();
		this.add(i);
		t = new MyPictureTextFigure(this);
		if (caption == null) {
			caption = new String("" + b.getId());
		}
		this.setCaption(caption);
		t.setReadOnly(false);
		this.add(t);
		update();
		this.setScale(CLOSED_SCALE_FACTOR);
		this.containsArtifacts = new TextFigure();
		this.containsArtifacts.setText("+");
	}
			
	protected void setTypeImage() {
		i = null;
		img = null;
		try {
			img = ImageIO.read(new File(
					ReactivisionConstants.BASEPATH+"dummy.gif"));
			i = new ImageFigure(img,
					ReactivisionConstants.BASEPATH+"dummy.gif", new Point(0, 0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void addArtifact(Artifact a) {
		containedArtifacts.add(a);
		Rectangle r = i.displayBox();
		r.x = r.x + 10;
		r.y = r.y + 5;
		r.width = containsArtifacts.displayBox().width;
		r.height = containsArtifacts.displayBox().height;
		containsArtifacts.displayBox(r);
		containsArtifacts.displayBox(r);
		this.add(containsArtifacts);
		this.bringToFront(containsArtifacts);
	}

	public void addImageFigure(ImageFigure i) {
		if (i == null)
			return;
		this.remove(this.i);
		this.i = i;
		this.add(i);
		this.sendToBack(i);
		Rectangle r = i.displayBox();
		r.x = r.x + r.width / 2;
		r.y = r.y + r.height / 2;
		t.displayBox(r);
		if (t.isImage())
			this.bringToFront(t);
		this.changed();
	}

	public void addMarker(Figure f) {
		if (marker != null) {
			this.remove(marker);
		}
		this.add(f);
		marker = f;
		this.changed();
	}
	
	@Override
	public Object clone() {
		BasicBuildingBlock b;
		if (this instanceof Red)
			b = new Red(this.block, t.getText(), this.container);// ,displayBox().x,displayBox().y,0);
		else if (this instanceof Blue)
			b = new Blue(this.block, t.getText(), this.container);// ,displayBox().x,displayBox().y,0);
		else if (this instanceof Yellow)
			b = new Yellow(this.block, t.getText(), this.container);// ,displayBox().x,displayBox().y,0);
		else
			b = new BasicBuildingBlock(this.block, t.getText(), this.container);// ,displayBox().x,displayBox().y,0);
		b.setScale(this.scale);
		b.setRotation(this.rotation);
		for (Artifact a: this.containedArtifacts) {
			Artifact newA = (Artifact) a.clone();
			newA.setContainer(b);
			b.addArtifact(newA);
		}
		if (t.isImage())
			b.setImageCaption(t.getImage());
		else
			b.setCaption(t.getText());
		if (displayBackgroundImage)
			b.addImageFigure(new ImageFigure(img, null, new Point(i
					.displayBox().x, i.displayBox().y)));
		else
			b.displayCaptionOnly();
		if (open)
			b.open();
		b.setScale(1/this.scale);
		b.setDrawingPosition(this.getDrawingPosition().x, this.getDrawingPosition().y);
		return b;
	}

	public void close() {
		if (!open || !displayBackgroundImage)
			return;
		System.out.println("Closing block " + this.getId());
		this.open = false;
		setScale(CLOSED_SCALE_FACTOR / OPEN_SCALE_FACTOR);
		synchronized (containedArtifacts) {
			for (Artifact a : containedArtifacts) {
				this.remove(a.getRepresentedElement());
				System.out.println(" Hiding artifact "
						+ ((BasicBuildingBlock) a.getRepresentedElement())
								.getId());

			}
		}
//		this.changed();
	}

	public boolean containesArtifact(Artifact a) {
		return containedArtifacts.contains(a);
	}

	public Set<Artifact> getArtifacts() {
		return containedArtifacts;
	}

	public void displayCaptionOnly() {
		displayBackgroundImage = false;
//		this.removeAll();
		this.remove(i);
		this.add(dummy);
//		this.add(t);
		this.sendToBack(dummy);
		Rectangle r = dummy.displayBox();
		r.x = r.x + r.width / 2;
		r.y = r.y + r.height + r.height/4;
		r.width = t.displayBox().width;
		r.height = t.displayBox().height;
		t.displayBox(r);
		if (t.isImage())
			this.bringToFront(t);
		this.changed();
		
		
		originalFontSize = t.getAttribute(FigureAttributeConstant.FONT_SIZE);
		t.setAttribute(FigureAttributeConstant.FONT_SIZE, new Integer(20));
		t.setAttribute(FigureAttributeConstant.FONT_STYLE, Font.BOLD);
		t.setAttribute(FigureAttributeConstant.TEXT_COLOR, Color.WHITE);

		t.repairTextPosition();
		this.changed();
	}
	
	public void displayElementWithImage() {
		displayBackgroundImage = true;
		t.setAttribute(FigureAttributeConstant.FONT_SIZE, originalFontSize);
		t.setAttribute(FigureAttributeConstant.TEXT_COLOR, Color.BLACK);
		this.addImageFigure(i);
	}

	public void displayTextUppermost() {
		this.bringToFront(this.t);
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.rotate(this.rotation,this.getDrawingPosition().x,this.getDrawingPosition().y);
		int w = this.displayBox().width;
		int h = this.displayBox().height;
		int x = this.getDrawingPosition().x;
		int y = this.getDrawingPosition().y;
		int diag = (int) Math.round(Math.sqrt(w*w+h*h));
		if (onlyCaption()) diag = diag * 2;
		Shape s = g2.getClip();
		g2.setClip(x-diag/2, y-diag/2, diag, diag);
		super.draw(g2);
		g2.setClip(s);
		g2.rotate(-this.rotation,this.getDrawingPosition().x,this.getDrawingPosition().y);
		if (listener() != null) {
			listener().figureChanged(new FigureChangeEvent(this));
		}		
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof BasicBuildingBlock && ((BasicBuildingBlock) o)
				.getId() == this.block.getId());
	}

	public long getBuildingSession() {
		return buildingSession;
	}

	public int getId() {
		return block.getId();
	}

	public Point getDrawingPosition() {
		Rectangle box = this.displayBox();
		return new Point((box.x + box.width / 2), (box.y + box.height / 2));
	}

	public String getTextCaption() {
		return t.getText();
	}

	@Override
	public TextHolder getTextHolder() {
		return t;
	}

	public boolean isOpen() {
		return open;
	}

	public boolean onlyCaption() {
		return !displayBackgroundImage;
	}

	public void open() {
		if (open || !displayBackgroundImage)
			return;
		this.toggleNamingMarker(0);
		System.out.println("Opening block " + this.getId());
		this.open = true;
		setScale(OPEN_SCALE_FACTOR / CLOSED_SCALE_FACTOR);
		Rectangle r = i.displayBox();
		r.x = r.x + r.width / 2;
		r.y = r.y + r.height + 10;
		t.displayBox(r);
		synchronized (containedArtifacts) {
			int nrOfArtifacts = containedArtifacts.size();
			int hOffset = i.displayBox().width / (Math.min(nrOfArtifacts, 3) + 1); 
			int vOffset = i.displayBox().height / (((nrOfArtifacts-1) / 3) + 2);
			System.out.println(nrOfArtifacts+" "+i.displayBox().width+" "+i.displayBox().height+" "+hOffset+" "+vOffset);
			int cnt = 0;
			for (Artifact a : containedArtifacts) {
				Rectangle box = a.getRepresentedElement().displayBox();
				int x = i.displayBox().x + hOffset * (1+cnt%3) - box.width/2;
				int y = i.displayBox().y + vOffset * (1+cnt/3) - box.height/2;
	 			this.add(a.getRepresentedElement());
				a.getRepresentedElement().moveBy(x - box.x, y - box.y);		
				a.getRepresentedElement().setRotation(0.0);
				System.out.println(" Showing artifact "
						+ ((BasicBuildingBlock) a.getRepresentedElement())
								.getId() + " at " + x + "," + y + "("+i.displayBox().x+", "+i.displayBox().y+")");
				cnt++;
			}
		}
	}

	public void removeArtifact(Artifact a) {
		containedArtifacts.remove(a);
		if (containedArtifacts.size()==0) this.remove(containsArtifacts);
	}

	public void removeMarker() {
		if (marker != null) {
			this.remove(marker);
			this.changed();
			marker = null;
		}
	}

	public void setBuildingSession(long buildingSession) {
		this.buildingSession = buildingSession;
	}

	public void setCaption(Object o) {
		if (o == null) return;
		if (o instanceof BufferedImage) setImageCaption((BufferedImage) o);
		else setCaption(o.toString());
	}

	public void setCaption(String caption) {
		this.removeMarker();
		t.setText(caption);
		this.bringToFront(t);
	}

	public void setContainedArtifacts(Artifact[] containedArtifacts) {
		this.containedArtifacts = Collections.synchronizedSet(new HashSet<Artifact>());
		for (Artifact a: containedArtifacts) {
			this.containedArtifacts.add(a);
		}
	}

	public void setImageCaption(BufferedImage img) {
		t.setImage(img);
		// this.sendToBack(t);
	}

	public void setPosition(int x, int y) {
		int adapted_x, adapted_y;
		
		if (container != null) {
			adapted_x = container.adaptX(x);
			adapted_y = container.adaptY(y);
		}
		else {
			adapted_x = -100;
			adapted_y = -100;
		}
		setDrawingPosition(adapted_x, adapted_y);
	}
	
	public void setDrawingPosition(int x, int y) {
		
		this.willChange();
		Rectangle box = this.displayBox();
		int dx = x - (box.x + box.width / 2);
		int dy = y - (box.y + box.height / 2);

		this.moveBy(dx, dy);

		this.changed();
		
	}
	
	public void update() {
		setPosition(block.getX(), block.getY());
		setRotation(block.getRot());
	}

	public void setRotation(double rot) {
		this.rotation = rot;
	}

	public void setRotation(int rot) {
		this.rotation = calcRad(rot+90);
		this.changed();
	}

	public void setScale(float scale) {
		this.scale = scale;
		if (i != null) {
			Rectangle r = i.displayBox();
			AffineTransform tx = new AffineTransform();
			tx.scale(scale, scale);
			AffineTransformOp op = new AffineTransformOp(tx,
					AffineTransformOp.TYPE_BILINEAR);
			img = op.filter(img, null);
			this.remove(i);
			i = new ImageFigure(img, null, new Point(r.x, r.y));
			if (displayBackgroundImage)
				this.addImageFigure(i);
		}
		t.repairTextPosition();
		this.changed();
	}
	
	public void toggleNamingMarker(int newState) {
		if (newState>0) {

			this.connectionCandidate = true;
			ThicknessEllipseFigure f = null;
			if (displayBackgroundImage) f = new ThicknessEllipseFigure(new Point(i.displayBox().x
					- i.displayBox().width / 2, i.displayBox().y
					- i.displayBox().height / 2), new Point(i.displayBox().x
					+ i.displayBox().width * 3 / 2, i.displayBox().y
					+ i.displayBox().height * 3 / 2));
			else /*f = new ThicknessEllipseFigure(new Point(dummy.displayBox().x-70*2,dummy.displayBox().y-70),
					new Point(dummy.displayBox().x+70*2,dummy.displayBox().y+70));*/
				f = new ThicknessEllipseFigure(new Point(dummy.displayBox().x
						, dummy.displayBox().y
						), new Point(dummy.displayBox().x
						+ dummy.displayBox().width * 2 / 2, dummy.displayBox().y
						+ dummy.displayBox().height * 2 / 2));
			f.setThickness(2);
			f.setAttribute(FigureAttributeConstant.FILL_COLOR, ColorMap
					.color("None"));
			if (newState == 1) f.setAttribute(FigureAttributeConstant.FRAME_COLOR, Color.green);
			if (newState == 2) f.setAttribute(FigureAttributeConstant.FRAME_COLOR, Color.yellow);
			addMarker(f);
		}
		if (connectionCandidate && newState == 0) {
			this.connectionCandidate = false;
			removeMarker();
		}
	}

	private double calcRad(int rot) {
		return (1.0*rot / 180) * Math.PI;
	}

	@Override
	protected Rectangle invalidateRectangle(Rectangle r) {
		if (r.width > r.height) r.height = r.width;
		if (r.height > r.width) r.width = r.height;
		r.grow(Math.round(scale*30), Math.round(scale*30));
 		return super.invalidateRectangle(r);
	}

}
