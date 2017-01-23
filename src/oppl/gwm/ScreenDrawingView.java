package oppl.gwm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import oppl.gwm.elements.Artifact;
import oppl.gwm.elements.FileArtifact;
import oppl.gwm.elements.BasicBuildingBlock;
import oppl.gwm.elements.Blue;
import oppl.gwm.elements.Connection;
import oppl.gwm.elements.DirectedConnection;
import oppl.gwm.elements.ModelArtifact;
import oppl.gwm.elements.PictureArtifact;
import oppl.gwm.elements.Red;
import oppl.gwm.elements.UndirectedConnection;
import oppl.gwm.elements.Yellow;
import oppl.gwm.helpers.DummyDrawingEditor;
import oppl.gwm.helpers.ThicknessEllipseFigure;
import oppl.gwm.helpers.ThicknessLineFigure;
import oppl.reactivisionbridge.Block;

import org.jhotdraw.contrib.zoom.ZoomDrawingView;
import org.jhotdraw.figures.ArrowTip;
import org.jhotdraw.figures.RectangleFigure;
import org.jhotdraw.figures.RoundRectangleFigure;
import org.jhotdraw.figures.TextFigure;
import org.jhotdraw.framework.DrawingEditor;
import org.jhotdraw.framework.Figure;
import org.jhotdraw.framework.FigureAttributeConstant;
import org.jhotdraw.framework.FigureEnumeration;
import org.jhotdraw.standard.StandardDrawing;
import org.jhotdraw.standard.StandardDrawingView;
import org.jhotdraw.util.ColorMap;

public class ScreenDrawingView extends ZoomDrawingView implements GWMControllerInterface {

	protected Map<Integer, Artifact> artifacts;
	protected Map<Integer, BasicBuildingBlock> basicBuildingBlocks;
	protected Map<Integer, Connection> connections;
	protected Color CONNECTOR_COLOR;
	protected int CONNECTOR_THICKNESS;

	protected float currentScale;
	protected History history;
//	protected RestoreTask currentRestoreTask;
	protected long historyEnablingSession;

	protected boolean historyMode;
	
	protected int markerTolerance;
	protected Color originalSurfaceColor = null;
	protected Color eraseModeSurfaceColor = null;

	protected Map<Integer, BasicBuildingBlock> removedBlockHistory = Collections
	.synchronizedMap(new HashMap<Integer, BasicBuildingBlock>());

	protected boolean restoreMode;
	
	protected Map<String, String> semantics;

	
	protected int SCALE_X;
	protected int SCALE_Y;
	protected int OFFSET_X;
	protected int OFFSET_Y;

	
	public ScreenDrawingView(DrawingEditor editor) {
		super(editor);
		init();
	}

	public ScreenDrawingView(DrawingEditor editor, int width, int height) {
		super(editor, width, height);
		init();
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#activateHistoryMode(long)
	 */
	public void activateHistoryMode(long session_id) {
		if (historyMode)
			return;
		historyMode = true;
		historyEnablingSession = session_id;
		history.goToLatestSnapshot();
		originalSurfaceColor = this.getBackground();
		this.displaySnapshot(history.getCurrentSnapshot());
	}
	
	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#addBasicBuildingBlock(long, int, java.lang.Object)
	 */
	public void addBasicBuildingBlock(long session, Block b, Object caption) {
		if (removedBlockHistory.containsKey(new Integer(b.getId()))
				&& removedBlockHistory.get(new Integer(b.getId())) != null) {
			BasicBuildingBlock returned = removedBlockHistory.get(new Integer(
					b.getId()));
			removedBlockHistory.remove(new Integer(b.getId()));
			addOrAlterBasicBuildingBlock(returned);
		//	checkForPendingConnections(returned);
			System.out.println("Block " + b.getId() + " returned");
		} else {
			BasicBuildingBlock bbb = null;
			if (b.getId() % 3 == 0)
				bbb = new Red(b, caption, this);
			if (b.getId() % 3 == 1)
				bbb = new Blue(b, caption, this);
			if (b.getId() % 3 == 2)
				bbb = new Yellow(b, caption, this);
			if (!basicBuildingBlocks.containsKey(new Integer(b.getId())) || basicBuildingBlocks.get(b.getId()).getBuildingSession() == session) {
				addOrAlterBasicBuildingBlock(bbb);
				bbb.setBuildingSession(session);
			}
		}
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#addOrAlterConnection(int, int, boolean, int)
	 */
	public void addOrAlterConnection(int end1_id, int end2_id, int direction, int id, String caption, boolean flash) {
		Connection newCon = null;
		BasicBuildingBlock end1 = basicBuildingBlocks
		.get(new Integer(end1_id));
		BasicBuildingBlock end2 = basicBuildingBlocks
		.get(new Integer(end2_id));
		if (end1 == null || end2 == null) return;
		if (direction == Connection.ARROW_NONE)
			newCon = new UndirectedConnection(caption, end1,
					end2, id);
		if (direction == Connection.ARROW_END)
			newCon = new DirectedConnection(caption, end1,
					end2, id);
		if (direction == Connection.ARROW_START)
			newCon = new DirectedConnection(caption, end2,
					end1, id);
		if (direction == Connection.ARROW_BOTH) {
			newCon = new DirectedConnection(caption, end1,
					end2, id);
			newCon.setArrowDirection(Connection.ARROW_BOTH);
		}
		if (newCon != null) {
			newCon
					.setAttribute(
							FigureAttributeConstant.FRAME_COLOR,
							this.CONNECTOR_COLOR);
			newCon.setThickness(CONNECTOR_THICKNESS);
			addOrAlterConnection(newCon);
			if (flash) this.areaFlash(end1.getDrawingPosition().x, end1.getDrawingPosition().y, end2.getDrawingPosition().x, end2.getDrawingPosition().y);
			System.out.println("added connector "+id+" "+newCon.getId());
		}
	}

	public void calibrate(int parameter, int value) {
		switch (parameter) {
		case GWMControllerInterface.OFFSET_X: this.OFFSET_X = this.OFFSET_X + value; break;
		case GWMControllerInterface.OFFSET_Y: this.OFFSET_Y = this.OFFSET_Y + value; break;
		case GWMControllerInterface.SCALE_X: this.SCALE_X = this.SCALE_X + value; break;
		case GWMControllerInterface.SCALE_Y: this.SCALE_Y = this.SCALE_Y + value; break;
		}
		for (BasicBuildingBlock b: basicBuildingBlocks.values()) {
			b.update();
		}
		System.out.println("OX:"+ this.OFFSET_X+", OY:"+this.OFFSET_Y+", SX:"+this.SCALE_X+", SY:"+this.SCALE_Y);
	}
	
	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#closeBlock(int)
	 */
	public void closeBlock(int block_id) {
		this.unfreezeView();
		BasicBuildingBlock b = basicBuildingBlocks
				.get(new Integer(block_id));
		if (b != null) {
			b.close();
			this.redraw();
		}
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#createArtifact(int)
	 */
	public boolean createArtifact(oppl.reactivisionbridge.Artifact a) {
		Artifact artifact = null;
		if (a.getType() == 0) {
			artifact = new ModelArtifact();			
			((ModelArtifact) artifact).setSnapshot(new Snapshot(basicBuildingBlocks,connections,true));
			artifact.setRepresentedElement(new Red(new Block(a.getId()), "", this));
			flash();
		}
		if (a.getType() == 1) {
			artifact = new FileArtifact();			
			((FileArtifact) artifact).setFile((File) a.getRepresentedObject());
			artifact.setRepresentedElement(new Yellow(new Block(a.getId()), "", this));
		}
		if (a.getType() == 2) {
			artifact = new PictureArtifact();			
			((PictureArtifact) artifact).setImage((BufferedImage) a.getRepresentedObject());
			artifact.setRepresentedElement(new Blue(new Block(a.getId()), "", this));
		}
		
		artifacts.put(new Integer(a.getId()), artifact);
		return true;

	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#deactivateHistoryMode(long)
	 */
	public void deactivateHistoryMode(long session_id) {
		/*if (session_id != historyEnablingSession)
			return;*/
		historyMode = false;
		if (!restoreMode) {
			this.removeAll();
			this.addAll(basicBuildingBlocks.values());
			this.addAll(connections.values());
			this.setBackground(originalSurfaceColor);
			this.redraw();
		}
	}
	
	
	public void defineSemantics(String type, String meaning) {
		semantics.put(type, meaning);
	}

	public void drawAddSign(int id) {
		this.removeAll();
		this.repaintAll();
		this.addAll(basicBuildingBlocks.values());
		BasicBuildingBlock b = history.getCurrentSnapshot().basicBuildingBlocks.get(new Integer(id));
		if (b == null) return;
		ThicknessEllipseFigure f = new ThicknessEllipseFigure(new Point(b
				.displayBox().x
				- b.displayBox().width / 2, b.displayBox().y
				- b.displayBox().height / 2), new Point(b.displayBox().x
				+ b.displayBox().width * 3 / 2, b.displayBox().y
				+ b.displayBox().height * 3 / 2));
		f.setThickness(2);
		f.setAttribute(FigureAttributeConstant.FILL_COLOR, ColorMap
				.color("None"));
		f.setAttribute(FigureAttributeConstant.FRAME_COLOR, Color.green);
		b.addMarker(f);
		this.add(b);
		redraw();
		System.out.println("Add Sign added for " + b.getId());
	}
	
	public void drawMoveSign(int id) {
		this.removeAll();
		this.repaintAll();
		this.addAll(basicBuildingBlocks.values());

		BasicBuildingBlock target = history.getCurrentSnapshot().basicBuildingBlocks.get(new Integer(id));
        BasicBuildingBlock b = basicBuildingBlocks.get(new Integer(id));
		if (b == null) return;
		if (target == null) return;
		ThicknessLineFigure l = new ThicknessLineFigure();
/*		ThicknessEllipseFigure f = new ThicknessEllipseFigure(new Point(adapted_x
				- markerTolerance / 2, adapted_y - markerTolerance / 2), new Point(adapted_x + markerTolerance
				/ 2, adapted_y + markerTolerance / 2));
*/		ThicknessEllipseFigure f = new ThicknessEllipseFigure(new Point(target.displayBox().x,target.displayBox().y),
				new Point(target.displayBox().x+target.displayBox().width,target.displayBox().y+target.displayBox().height));
		l.setThickness(2);
		l.setAttribute(FigureAttributeConstant.FRAME_COLOR, Color.orange);
		l.startPoint(b.getDrawingPosition().x, b.getDrawingPosition().y);
		l.endPoint(target.displayBox().x+target.displayBox().width/2, target.displayBox().y+target.displayBox().height/2);
		l.setEndDecoration(new ArrowTip());

		add(f);
		add(l);
		redraw();
		System.out.println("Move Sign added for " + b.getId());
	}

	public void drawRemoveSign(int id) {
		this.removeAll();
		this.repaintAll();
		this.addAll(basicBuildingBlocks.values());
		BasicBuildingBlock b = basicBuildingBlocks.get(new Integer(id));
		if (b == null) return;
		ThicknessEllipseFigure f = new ThicknessEllipseFigure(new Point(b
				.displayBox().x
				- b.displayBox().width / 2, b.displayBox().y
				- b.displayBox().height / 2), new Point(b.displayBox().x
				+ b.displayBox().width * 3 / 2, b.displayBox().y
				+ b.displayBox().height * 3 / 2));
		f.setThickness(2);
		f.setAttribute(FigureAttributeConstant.FILL_COLOR, ColorMap
				.color("None"));
		f.setAttribute(FigureAttributeConstant.FRAME_COLOR, Color.red);
		b.addMarker(f);
		remove(b);
		add(b);
		redraw();
		System.out.println("Remove Sign added for " + b.getId());
	}

	public void cleanUpAfterRestore() {
		this.removeAll();
		this.repaintAll();
		this.addAll(basicBuildingBlocks.values());		
	}
	
	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#getBasicBuildingBlocks()
	 */
	public BasicBuildingBlock[] getBasicBuildingBlocks() {
		return basicBuildingBlocks.values().toArray(new BasicBuildingBlock[0]);
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#gotoNextSnapshot()
	 */
	public void gotoNextSnapshot() {
		history.goToNextSnapshot();
		this.displaySnapshot(history.getCurrentSnapshot());
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#gotoPreviousSnapshot()
	 */
	public void gotoPreviousSnapshot() {
		history.goToPreviousSnapshot();
		this.displaySnapshot(history.getCurrentSnapshot());
	}
	
	public void registerKeyListener(KeyListener k) {
		this.addKeyListener(k);
		System.out.println("Registered KeyListener!");
	}
	
	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#moveBasicBuildingBlock(int, int, int, int)
	 */
	public void moveBasicBuildingBlock(int id, int x, int y, int rot) {
		if (basicBuildingBlocks.containsKey(new Integer(id))) {
			this.freezeView();
			BasicBuildingBlock b = getBasicBuildingBlock(id);
			if (b == null)
				return;
			b.setPosition(adaptX(x), adaptY(y));
			b.setRotation(rot);
			if (!historyMode || restoreMode)
				redraw();
			this.unfreezeView();
		}
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#openArtifact(int)
	 */
	public void openArtifact(int id) {
		Artifact a = artifacts.get(new Integer(id));
		if (a instanceof FileArtifact) {
			File f = null;
			f = ((FileArtifact) a).getFile();
			if (f == null)
				return;
			try {
				Runtime.getRuntime().exec("open " + f.getAbsolutePath());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "File " + f.getName()
						+ " is of unknown type.", "Unkonwn File Type",
						JOptionPane.WARNING_MESSAGE);
			}
		}
		if (a instanceof PictureArtifact) {
			BufferedImage img = ((PictureArtifact) a).getImage();
			if (img == null) return;
			Graphics g = img.createGraphics(); g.setColor(Color.RED); g.fillOval(50, 50, 50, 50);
			ImageIcon icon = new ImageIcon(); icon.setImage(img); JOptionPane.showMessageDialog(null, icon);
		}
		if (a instanceof ModelArtifact) {
			historyMode = true;
			this.displaySnapshot(((ModelArtifact) a).getSnapshot());
		}
	}

	public void closeArtifact(int id) {
		Artifact a = artifacts.get(new Integer(id));
		if (a instanceof ModelArtifact) {
			this.deactivateHistoryMode(0);
		}	
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#openBlock(int)
	 */
	public void openBlock(int id) {
		showContents(id);
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#putArtifactIntoBlock(int, int)
	 */
	public boolean putArtifactIntoBlock(int artifact_id, int block_id) {
		BasicBuildingBlock nearest = basicBuildingBlocks.get(new Integer(block_id));
		if (nearest == null) return false;
		Artifact a = artifacts.get(artifact_id);
		if (a == null) return false;
		a.setContainer(nearest);
		if (nearest.isOpen()) {
			nearest.close();
			nearest.open();
		}		
		return true;
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#redraw()
	 */
	public void redraw() {
		this.checkDamage();
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#removeAll()
	 */
	@Override
	public void removeAll() {
		FigureEnumeration fe = this.drawing().figures();
		while (fe.hasNextFigure()) {
			remove(fe.nextFigure());
		}
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#removeBasicBuildingBlock(int)
	 */
	public void removeBasicBuildingBlock(int id) {
		BasicBuildingBlock block = getBasicBuildingBlock(id);
		if (block != null && !block.isOpen()) {
			if (!historyMode || restoreMode) {
				this.remove(block);
				redraw();
			}
			basicBuildingBlocks.remove(new Integer(id));
			block.removeMarker();
			removedBlockHistory.put(new Integer(id), block);
		}
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#removeConnection(int)
	 */
	public void removeConnection(int id) {
		this.freezeView();
		System.out.print("searching for connection "+id+" ... ");
		Connection con = getConnection(id);
		if (con != null) {
//			if (!historyMode) 
				this.remove(con);
			con.delete();
			connections.remove(new Integer(id));
			System.out.println("found and removed");
		}
		this.unfreezeView();
		if (!historyMode)
			redraw();
	}
	
	public void removeAllConnections() {
		for (Connection con: connections.values()) {
			this.remove(con);
			con.delete();
		}
		connections.clear();
		redraw();
	}

	public void removeGlass() {
		this.removeAll();
		this.repaintAll();
		this.addAll(basicBuildingBlocks.values());

		restoreMode = false;
		TextFigure notify = new TextFigure();
		notify.setText("Finished - Remove Glass of Time!");
		notify.moveBy(this.getBounds().width / 2 - notify.displayBox().width
				/ 2, this.getBounds().height / 2);
		notify.setAttribute(FigureAttributeConstant.FONT_SIZE, 25);
		notify.setAttribute(FigureAttributeConstant.TEXT_COLOR, Color.black);
		this.add(notify);
		redraw();
	}

	public void removeMarker(int id) {
		BasicBuildingBlock b = basicBuildingBlocks.get(new Integer(id));
		if (b==null) return;
		b.removeMarker();
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#repaintAll()
	 */
	public void repaintAll() {
		repaint(0, 0, this.size().width, this.size().height);
	}
	
	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#reset()
	 */
	public void reset() {
		this.removeAll();
		init();
		System.out.println("Drawing resetted!");
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#restoreCurrentSnapshot()
	 */
	public void restoreCurrentSnapshot() {
		if (restoreMode)
			return;
		restoreMode = true;
		System.out.println("Activated Restore Mode");
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#saveImplicitSnapshot()
	 */
	public void saveImplicitSnapshot() {
		System.out.println("Implicit Snapshot taken!");
		history.addSnapshot(basicBuildingBlocks, connections, false);
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#saveSnapshot()
	 */
	public void saveSnapshot() {
		history.addSnapshot(basicBuildingBlocks, connections, true);
		this.flash();
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#toggleConnectionCandidate(int, boolean)
	 */
	public void toggleNamingMarker(int id, int state) {
		if (id < 100 && id > -1) {
			BasicBuildingBlock b = basicBuildingBlocks
			.get(new Integer(id));
			if (b == null) return;
			b.toggleNamingMarker(state);
		}
		else {
			Connection c = connections.get(new Integer(id));
			if (c == null) return;
			c.toggleMarker(state);
//			System.out.println("Switching connection "+id+" to "+state);
		}
		this.redraw();
	}

/*	protected void watchTable() {
		TextFigure notify = new TextFigure();
		notify.setText("Now working in Restore Mode - Watch Table!");
		notify.moveBy(this.getBounds().width / 2 - notify.displayBox().width
				/ 2, this.getBounds().height / 3);
		notify.setAttribute(FigureAttributeConstant.FONT_SIZE, 25);
		notify.setAttribute(FigureAttributeConstant.TEXT_COLOR, Color.black);
		this.add(notify);
	}*/

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#updateCaption(int, java.lang.Object)
	 */
	public boolean updateCaption(int id, Object caption) {
		if (caption == null) return false;
		BasicBuildingBlock b = basicBuildingBlocks.get(new Integer(id));
		if (b != null) b.setCaption(caption);
		else {
			Connection c = connections.get(new Integer(id));
			if (c!= null) c.setCaption(caption.toString());
			c.toggleMarker(0);
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#updateConnection(int)
	 */
	public void updateConnection(int id) {
		Connection c = connections.get(new Integer(id));
		if (c != null) c.updateConnection();
	}

	private void addOrAlterConnection(Connection c) {
		this.freezeView();
		if (connections.containsKey(new Integer(c.getId())))
			this.remove(getConnection(c.getId()));
		if (!historyMode) {
			this.add(c);
			redraw();
		}
		this.unfreezeView();
		connections.put(new Integer(c.getId()), c);
		redraw();
	}

	public int adaptX(int x) {
		return (x * SCALE_X / 1000) - OFFSET_X;
	}

	public int adaptY(int y) {
		return (y * SCALE_Y / 1000) - OFFSET_Y;	
	}
	
	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#addOrAlterBasicBuildingBlock(oppl.gwm.elements.BasicBuildingBlock)
	 */
	protected void addOrAlterBasicBuildingBlock(BasicBuildingBlock b) {

		if (b == null) return;
	//	b.setScale(currentScale);
		if (!historyMode || restoreMode) {
			if (basicBuildingBlocks.containsKey(new Integer(b.getId()))) {
				this.remove(getBasicBuildingBlock(b.getId()));
				this.add(b);
			} else {
				this.freezeView();
				this.add(b);
				this.redraw();
				this.unfreezeView();
				basicBuildingBlocks.put(new Integer(b.getId()), b);
			}
		}

	}

	public void eraseMode(boolean on) {
		if (on) {
			eraseModeSurfaceColor = this.getBackground();
			this.setBackground(new Color(128, 0, 0));
		}
		else {
			if (eraseModeSurfaceColor != null) this.setBackground(eraseModeSurfaceColor);
		}
	}
	
	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#artifactAvailable(int)
	 */
	protected boolean artifactAvailable(int id) {
		Artifact a = artifacts.get(new Integer(id));
		if (a == null) return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#delay(long)
	 */
	protected void delay(long ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
		}
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#displaySnapshot(java.util.Collection)
	 */
	protected void displaySnapshot(Snapshot snapshot) {
		if (!historyMode)
			return;

		this.removeAll();
		this.addAll(snapshot.getSnapshot());
		
		if (history.getPositionOfSnapshot(snapshot) != -1) {
			System.out.println("Displaying history time line");
			int length = this.getWidth();
			int hOffset = length / 10;
			length = length * 8 / 10;
			int vOffset = this.getHeight() * 9 / 10;
			int stepwidth = length;
			if (history.getNrOfSnapshots() > 1) stepwidth = stepwidth / (history.getNrOfSnapshots() - 1);

			System.out.println(length+" "+hOffset+" "+vOffset+" "+stepwidth);

			RoundRectangleFigure frame = new RoundRectangleFigure(new Point(hOffset, vOffset), new Point(hOffset+length, vOffset+20));
			frame.setAttribute(FigureAttributeConstant.FILL_COLOR, Color.white);
			frame.setAttribute(FigureAttributeConstant.FRAME_COLOR, Color.black);
			
			RoundRectangleFigure fill = new RoundRectangleFigure(new Point(hOffset, vOffset), new Point(hOffset+stepwidth*history.getPositionOfSnapshot(snapshot), vOffset+20));
			fill.setAttribute(FigureAttributeConstant.FILL_COLOR, Color.green);
			fill.setAttribute(FigureAttributeConstant.FRAME_COLOR, Color.black);
			
			this.add(frame);
			this.add(fill);
			
		}
		this.redraw();
	}
	
	public Vector<ScreenDrawingView> getHistoryImages() {
		Vector<ScreenDrawingView> drawings = new Vector<ScreenDrawingView>();
		Vector<Snapshot> snapshots = history.getAllSnapshots();
		System.out.println(this.getWidth() + " "+ this.getHeight());
		for (Snapshot snapshot: snapshots) {

			ScreenDrawingView sdv = new ScreenDrawingView(new DummyDrawingEditor(),this.getWidth(), this.getHeight());
			StandardDrawing sd = new StandardDrawing();
			sdv.setDrawing(sd);
			sdv.setBackground(Color.white);
			sdv.addAll(snapshot.getSnapshot());
			sdv.setBounds(0,0, this.getWidth(), this.getHeight());
			drawings.add(sdv);
		}
		return drawings;
	}

	public Vector<ScreenDrawingView> getEmbeddedImages() {
		Vector<ScreenDrawingView> drawings = new Vector<ScreenDrawingView>();
		System.out.println(this.getWidth() + " "+ this.getHeight());
		drawings.add(this);
		FigureEnumeration fe = this.drawing().figures();
		HashSet<BasicBuildingBlock> blocks = new HashSet<BasicBuildingBlock>();
		while (fe.hasNextFigure()) {
			Figure f = fe.nextFigure();
			if (f instanceof BasicBuildingBlock) blocks.add((BasicBuildingBlock) f);
		}
		drawings.addAll(getRecursiveEmbeddedImages(blocks,1));
		
		return drawings;
	}
	
	private Vector<ScreenDrawingView> getRecursiveEmbeddedImages(Collection elements, int layer) {
		Vector<ScreenDrawingView> drawings = new Vector<ScreenDrawingView>();	
		for (Object o: elements) {
			if (o instanceof BasicBuildingBlock) {
				BasicBuildingBlock b = (BasicBuildingBlock) o;
				if (b.getArtifacts().size() > 0) {
					for (Artifact a: b.getArtifacts()) {
						if (a instanceof ModelArtifact) {
							Snapshot snapshot = ((ModelArtifact) a).getSnapshot();
							ScreenDrawingView sdv = new ScreenDrawingView(new DummyDrawingEditor(),this.getWidth(), this.getHeight());
							StandardDrawing sd = new StandardDrawing();
							sdv.setDrawing(sd);
							sdv.setBackground(new Color(255-20*layer,255-20*layer,255-20*layer));
							sdv.addAll(snapshot.getSnapshot());
							BasicBuildingBlock designator = (BasicBuildingBlock) b.clone();
							designator.setDrawingPosition(75,60);
							RectangleFigure designatorBG = new RectangleFigure(new Point(0,20),new Point(150,100));
							designatorBG.setAttribute(FigureAttributeConstant.FILL_COLOR, new Color(255-20*(layer-1),255-20*(layer-1),255-20*(layer-1)));
							designatorBG.setAttribute(FigureAttributeConstant.FRAME_COLOR, new Color(255-20*(layer-1),255-20*(layer-1),255-20*(layer-1)));
							sdv.add(designatorBG);
							sdv.add(designator);
							sdv.setBounds(0,0, this.getWidth(), this.getHeight());
							drawings.add(sdv);
							drawings.addAll(getRecursiveEmbeddedImages(snapshot.getSnapshot(), layer+1));
						}
					}
				}
			}
		}
		return drawings;
	}

	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#flash()
	 */
	protected void flash() {
		Color c = this.getBackground();
		for (int i = 0; i < 256; i = i + 5) {
			this.setBackground(new Color(255, 255 - i, 255 - i));
			this.delay(1);
		}
		this.delay(50);
		this.setBackground(c);
	}
	
	protected void areaFlash(int x1, int y1, int x2, int y2) {
		ThicknessLineFigure l = new ThicknessLineFigure();
		l.startPoint(x1, y1);
		l.endPoint(x2, y2);

		for (int i = 0; i < 256; i = i + 5) {
			l.setThickness((256-i)/10);
			l.setAttribute(FigureAttributeConstant.FRAME_COLOR, (new Color(255 - i, 255, 255 - i)));
			this.add(l);
			this.delay(5);
		}
		this.remove(l);
	}
	
	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#getBasicBuildingBlock(int)
	 */
	protected BasicBuildingBlock getBasicBuildingBlock(int id) {
		return basicBuildingBlocks.get(new Integer(id));
	}
	
	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#getConnection(int)
	 */
	protected Connection getConnection(int id) {
		return connections.get(new Integer(id));
	}
	
	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#getConnections()
	 */
	protected Connection[] getConnections() {
		return connections.values().toArray(new Connection[0]);
	}
	
	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#gotoLatestSnapshot()
	 */
	protected void gotoLatestSnapshot() {
		history.goToLatestSnapshot();
		this.displaySnapshot(history.getCurrentSnapshot());
	}
	
	protected void init() {
		basicBuildingBlocks = Collections
				.synchronizedMap(new HashMap<Integer, BasicBuildingBlock>());
		connections = Collections
				.synchronizedMap(new HashMap<Integer, Connection>());
		artifacts = Collections
		.synchronizedMap(new HashMap<Integer, Artifact>());
		removedBlockHistory = Collections
		.synchronizedMap(new HashMap<Integer, BasicBuildingBlock>());
		historyMode = false;
		restoreMode = false;
		history = new History();
		currentScale = 1.0f;
		markerTolerance = 40;
		
		semantics = new HashMap<String, String>();
		
		OFFSET_X = 10;
		OFFSET_Y = 50;
		SCALE_X = 830;
		SCALE_Y = 820;
		CONNECTOR_THICKNESS = 2;
		CONNECTOR_COLOR = Color.black;
		
	}
	
	/* (non-Javadoc)
	 * @see oppl.gwm.GWMViewer#nextRestoreTask()
	 */
/*	protected void nextRestoreTask() {
		if (!restoreMode)
			return;
		this.removeAll();
		this.repaintAll();
		this.addAll(basicBuildingBlocks.values());
//		watchTable();
		Vector<BasicBuildingBlock> blocksToRemove = new Vector<BasicBuildingBlock>();
		synchronized (basicBuildingBlocks) {
			for (BasicBuildingBlock b : basicBuildingBlocks.values())
				blocksToRemove.add((BasicBuildingBlock) b.clone());
		}
		blocksToRemove.removeAll(history.getCurrentSnapshot()
				.getBasicBuildingBlocks().values());
		System.out.println("Searching for Blocks to remove ...");
		if (blocksToRemove.size() != 0) {
			System.out.println("Found Block to remove!");
			Collections.sort(blocksToRemove, new Comparator<BasicBuildingBlock>() {
			    public int compare(BasicBuildingBlock o1, BasicBuildingBlock o2) {
			        int id1 = o1.getId();
			        int id2 = o2.getId();
			        return id1 - id2;
			    }
			});
			BasicBuildingBlock blockToRemove = (BasicBuildingBlock) blocksToRemove
					.iterator().next();
			currentRestoreTask = new RestoreTask(blockToRemove,
					RestoreTask.REMOVE);
			drawRemoveSign(blockToRemove);
		} else {
			Vector<BasicBuildingBlock> blocksToMove = new Vector<BasicBuildingBlock>();
			synchronized (basicBuildingBlocks) {
				for (BasicBuildingBlock b : basicBuildingBlocks.values())
					blocksToMove.add((BasicBuildingBlock) b.clone());
			}
			for (BasicBuildingBlock b : blocksToMove) {
				if (!history.getCurrentSnapshot().getBasicBuildingBlocks()
						.containsKey(new Integer(b.getId())))
					blocksToMove.remove(b);
			}
			System.out.println("Searching for Blocks to move ...");
			BasicBuildingBlock blockToMove = null;
			int goalX = 0;
			int goalY = 0;
			Collections.sort(blocksToMove, new Comparator<BasicBuildingBlock>() {
			    public int compare(BasicBuildingBlock o1, BasicBuildingBlock o2) {
			        int id1 = o1.getId();
			        int id2 = o2.getId();
			        return id1 - id2;
			    }
			});

			synchronized (blocksToMove) {
				Iterator<BasicBuildingBlock> bi = blocksToMove.iterator();
				while (bi.hasNext()) {
					blockToMove = bi.next();
					BasicBuildingBlock desiredState = history
							.getCurrentSnapshot().getBasicBuildingBlocks().get(
									new Integer(blockToMove.getId()));
					goalX = desiredState.getPosition().x;
					goalY = desiredState.getPosition().y;
					int curX = blockToMove.displayBox().x
							+ blockToMove.displayBox().width / 2;
					int curY = blockToMove.displayBox().y
							+ blockToMove.displayBox().height / 2;
					int varX = markerTolerance;
					int varY = markerTolerance;
					if (!(curX > goalX - varX && curX < goalX + varX
							&& curY > goalY - varY && curY < goalY + varY))
						break;
					else
						blockToMove = null;
				}
			}
			if (blockToMove != null) {
				System.out.println("Found Block to move!");
				currentRestoreTask = new RestoreTask(blockToMove, goalX, goalY);
				drawMoveSign(blockToMove, goalX, goalY);
			} else {
				System.out.println("Searching for Blocks to add ...");
				Vector<BasicBuildingBlock> blocksToAdd = new Vector(history.getCurrentSnapshot()
						.getBasicBuildingBlocks().values());
				blocksToAdd.removeAll(basicBuildingBlocks.values());
				if (blocksToAdd.size() != 0) {
					System.out.println("Found Block to add!");
					Collections.sort(blocksToAdd, new Comparator<BasicBuildingBlock>() {
					    public int compare(BasicBuildingBlock o1, BasicBuildingBlock o2) {
					        int id1 = o1.getId();
					        int id2 = o2.getId();
					        return id1 - id2;
					    }
					});

					BasicBuildingBlock blockToAdd = (BasicBuildingBlock) blocksToAdd
							.iterator().next();
					currentRestoreTask = new RestoreTask(blockToAdd,
							RestoreTask.ADD);
					drawAddSign(blockToAdd);
				} else {
					System.out.println("Restore finished");
					restoreMode = false;
					renewConnections(history.getCurrentSnapshot()
							.getConnections());
					if (!historyMode) {
						this.removeAll();
						this.addAll(basicBuildingBlocks.values());
						this.addAll(connections.values());
						this.setBackground(originalSurfaceColor);
					} else {
						removeGlass();
					}
				}
			}
		}
		this.redraw();
	}
*/
	protected void renewConnections(Map<Integer, Connection> stored) {
		connections = Collections
				.synchronizedMap(new HashMap<Integer, Connection>());
		synchronized (stored) {
			for (Connection c : stored.values()) {
				Connection newC = new UndirectedConnection("", c.getFrom(), c
						.getTo(),c.getId());
				newC.setThickness(c.getThickness());
				newC.setAttribute(FigureAttributeConstant.FRAME_COLOR, c
						.getAttribute(FigureAttributeConstant.FRAME_COLOR));
				connections.put(new Integer(newC.getId()), newC);
			}
		}
	}

	protected void showContents(BasicBuildingBlock b) {
		if (b == null || b.isOpen()) return;
		b.open();
		this.redraw();
		this.freezeView();
	}

	protected void showContents(int block_id) {
		BasicBuildingBlock b = basicBuildingBlocks
		.get(new Integer(block_id));
		if (b == null || b.isOpen()) return;
		b.open();
		this.redraw();
		this.freezeView();
	}
	
/*	protected class RestoreTask {

		protected static final int REMOVE = 0;
		protected static final int MOVE = 1;
		protected static final int ADD = 2;

		protected int removeTask;

		protected BasicBuildingBlock block;
		protected int x;
		protected int y;

		public RestoreTask(BasicBuildingBlock block, int type) {
			if (type == REMOVE)
				removeTask = REMOVE;
			if (type == ADD)
				removeTask = ADD;
			this.block = block;
		}

		public RestoreTask(BasicBuildingBlock block, int x, int y) {
			removeTask = MOVE;
			this.block = block;
			this.x = x;
			this.y = y;
		}

		public boolean completed() {
			if (removeTask == REMOVE) {
				if (!basicBuildingBlocks
						.containsKey(new Integer(block.getId())))
					return true;
			}
			if (removeTask == MOVE) {
				BasicBuildingBlock currentState = basicBuildingBlocks
						.get(new Integer(block.getId()));
				if (currentState == null)
					return false;
				int curX = currentState.displayBox().x
						+ currentState.displayBox().width / 2 - markerTolerance / 2;
				int curY = currentState.displayBox().y
						+ currentState.displayBox().height / 2 - markerTolerance / 2;
				int varX = markerTolerance;// currentState.displayBox().width/2;
				int varY = markerTolerance;// currentState.displayBox().height/2;
				if (curX > x - varX && curX < x + varX && curY > y - varY
						&& curY < y + varY)
					return true;
			}
			if (removeTask == ADD) {
				if (basicBuildingBlocks.containsKey(new Integer(block.getId())))
					return true;
			}
			return false;
		}
	}*/
	
	protected KeyListener createKeyListener() {
		return new StandardDrawingView.DrawingViewKeyListener() {
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
			}
		};
	}

}
