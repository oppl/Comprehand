package oppl.reactivisionbridge;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import oppl.gwm.ScreenApplication;
import oppl.gwm.GWMControllerInterface;
import oppl.gwm.ProjectorApplication;
import oppl.gwm.elements.Blue;
import oppl.reactivisionbridge.Connection;
import oppl.gwm.elements.Red;
import oppl.gwm.elements.Yellow;
import reactivision.tuio.TuioClient;
import reactivision.tuio.TuioListener;

public class UserInputCollector implements TuioListener, KeyListener {

	protected Map<Integer, Block> blocks = Collections
	.synchronizedMap(new HashMap<Integer, Block>());

	protected boolean askForSemantics;
	protected Map<Integer, Artifact> artifacts = Collections
	.synchronizedMap(new HashMap<Integer, Artifact>());
	
	protected ExplicitInterface ei;
	
	protected Set<ConnectionRemoveCandidate> connectionRemoveCandidates = Collections
			.synchronizedSet(new HashSet<ConnectionRemoveCandidate>());
	
	protected Map<Integer, Connection> connections = Collections
	.synchronizedMap(new HashMap<Integer, Connection>());
	protected Color CONNECTOR_COLOR = Color.black;
	protected int CONNECTOR_THICKNESS = 2;
	protected boolean connectorEraseMode = false;
	protected Map<Long, Connector> connectors = Collections
			.synchronizedMap(new HashMap<Long, Connector>());
	protected RestoreTask currentRestoreTask;
	
	protected boolean directedConnectors = false;
	protected ReactivisionHistory history = new ReactivisionHistory();
	protected boolean historyMode = false;
	protected float lastAngle = 1000;
	protected long lastTimeChanged;
	protected long lastSnapshot;
	protected int latestAddedFigure = -1;
	protected Map<String, String> semantics;
	protected ReactivisionSnapshot artifactSnapshot = null;
	
	protected boolean namingMarkerValid = true;
	protected boolean arrowMarkerValid = true;
	
	protected boolean dialogOpen = false;

	protected Map<Long, Integer> openBlocks = Collections
			.synchronizedMap(new HashMap<Long, Integer>());

	protected Map<Integer, BlockRemoveCandidate> removeCandidates = Collections
			.synchronizedMap(new HashMap<Integer, BlockRemoveCandidate>());
	protected boolean restoreMode = false;
	protected ReactivisionSnapshot restoreState = null;
	
	protected boolean snapshotAlreadyCreated;

	protected Timer t = null;

	protected Vector<GWMControllerInterface> viewers;
	protected int currentKeyTarget;
	protected boolean calibrationMode; 
	protected int currentlySelectedBlock;
	
	public UserInputCollector(GWMControllerInterface viewer, ExplicitInterface ei) {
		lastTimeChanged = System.currentTimeMillis();
		snapshotAlreadyCreated = true;
		
		t = new Timer();
		t.schedule(new BlockRemover(), 0, 300);
		t.schedule(new ConnectionRemover(), 0, 5000);
//		t.schedule(new ConnectorWatchdog(), 0, 300);
		t.schedule(new LastChangeChecker(), 0, 3000);
		viewers = new Vector<GWMControllerInterface>();
		viewers.add(viewer);
		viewer.registerKeyListener(this);
		currentKeyTarget = 0;
		
		this.ei = ei;
		ei.setParent(this);
		calibrationMode = false;
		currentlySelectedBlock = -1;
		lastSnapshot = 0;
		
		semantics = new HashMap<String, String>();
		semantics.put("red", null);
		semantics.put("blue", null);
		semantics.put("yellow", null);
		askForSemantics = true;

	}

	public void addTuioCur(long session_id) {
		// TODO Auto-generated method stub
		// System.out.println("connector found");
/*		Connector c = connectors.get(new Long(session_id));
		if (c == null) {
			c = new Connector(session_id);
//			if (reflectionBug == null)
//				reflectionBug = c;
//			else
				connectors.put(new Long(session_id), c);
		}*/
	}
	
	public void addTuioObj(long session_id, int fiducial_id) {
		if (blocks.get(new Integer(fiducial_id)) != null) {
			removeCandidates.remove(new Integer(fiducial_id));
			checkForPendingConnections(fiducial_id);
			return;			
		}
		if (fiducial_id == ReactivisionConstants.SNAPSHOT)
			saveSnapshot();
		if (fiducial_id == ReactivisionConstants.HISTORY)
			if (!historyMode) activateHistoryMode(session_id);
		if (fiducial_id == ReactivisionConstants.RESTORE_PREVIOUS_STATE)
			if (!restoreMode) restorePreviousState();
		if (fiducial_id == ReactivisionConstants.CONNECTION_DIRECTED)
			directedConnectors = true;
/*		if (fiducial_id == ReactivisionConstants.CONNECTION_ERASE) {
			connectorEraseMode = true;
			for (GWMControllerInterface viewer: viewers) {
				viewer.eraseMode(true);
			}			
		}*/
		if (ReactivisionConstants.isBasicBuildingBlock(fiducial_id)) {
			createBasicBuildingBlock(session_id, fiducial_id);
			checkForPendingConnections(fiducial_id);
/*			if (restoreMode && currentRestoreTask != null
					&& currentRestoreTask.completed())
				nextRestoreTask();
*/		}  
		
		if (ReactivisionConstants.isArtifact(fiducial_id)) {
			createArtifact(fiducial_id);
		}
	}

	public void addViewer(GWMControllerInterface viewer) {
		if (viewer == null) return;
		viewers.add(viewer);
		viewer.registerKeyListener(this);
	}

	public void askForSemantics(int id) {
		String blockType = "unknown";
		String current = null;
		if (askForSemantics) {
			switch (id % 3) {
			case 0: blockType = "red"; break;
			case 1: blockType = "blue"; break;
			case 2: blockType = "yellow"; break;		
			}
			current = semantics.get(blockType);
			StringBuffer query = new StringBuffer();
			query.append("Please specify the meaning of\n" + blockType + " blocks");
			if (current != null) query.append(" (current meaning: "+current+")");
			query.append(":");
			String s = (String) JOptionPane.showInputDialog(null,
					query,
					"Specify Semantics", JOptionPane.QUESTION_MESSAGE, null,
					null, null);
			if (s != null) {
				semantics.put(blockType, s);
				for (GWMControllerInterface viewer: viewers) {
					viewer.defineSemantics(blockType, s);
				}				
			}
			else {
				Object[] options = { "Later", "Never ask" };
				int n = JOptionPane
						.showOptionDialog(
								null,
								"You chose 'Cancel'. \n Do you want to specify this block's meaning later \n or do you prefer not to be asked anymore \n and leave all blocks unspecified ",
								"Cancel chosen", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, 
								options, // the titles of buttons
								options[0]); // default button title
				if (n == JOptionPane.NO_OPTION)
					askForSemantics = false;
			}
		}
	}
	
	public int getLatestBlock() {
		return latestAddedFigure;
	}
	
	public void keyPressed(KeyEvent e) {
		int parameter = 0;
		int value = 0;
		
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE: {
			calibrationMode = !calibrationMode;
			System.out.println("Calibration Mode: "+ calibrationMode);
			return;
		}
		case KeyEvent.VK_SPACE: {
			currentKeyTarget = (++currentKeyTarget)%viewers.size();
			System.out.println("Current Key Target: "+ currentKeyTarget);		
			break;
		}
		case KeyEvent.VK_LEFT: {
			parameter = GWMControllerInterface.OFFSET_X;
			value = 5;
			break;
		}
		case KeyEvent.VK_RIGHT: {
			parameter = GWMControllerInterface.OFFSET_X;
			value = -5;
			break;
		}
		case KeyEvent.VK_UP: {
			parameter = GWMControllerInterface.OFFSET_Y;
			value = 5;
			break;
		}
		case KeyEvent.VK_DOWN: {
			parameter = GWMControllerInterface.OFFSET_Y;
			value = -5;
			break;
		}
		case KeyEvent.VK_A: {
			parameter = GWMControllerInterface.SCALE_X;
			value = -5;
			break;
		}
		case KeyEvent.VK_D: {
			parameter = GWMControllerInterface.SCALE_X;
			value = 5;
			break;
		}
		case KeyEvent.VK_W: {
			parameter = GWMControllerInterface.SCALE_Y;
			value = -5;
			break;
		}
		case KeyEvent.VK_S: {
			parameter = GWMControllerInterface.SCALE_Y;
			value = 5;
			break;
		}

		}
		if (calibrationMode) viewers.get(currentKeyTarget).calibrate(parameter, value);
		else {
			if (dialogOpen) return;
			int targetBlock = latestAddedFigure;
			System.out.println("Capturing text for block "+targetBlock);
			String msg = "";
			if (currentlySelectedBlock != -1) {
				targetBlock = currentlySelectedBlock;
				currentlySelectedBlock = -1;
			}
			if (targetBlock == -1) return;
			if (targetBlock < 100 && targetBlock > -1) msg = "Enter caption for block " + targetBlock +":"; 
			else msg = "Enter caption for connection:";
			dialogOpen = true;
			// todo: alte caption laden
			String s = (String) JOptionPane.showInputDialog((Component) viewers.get(0),
					msg,
					"Enter caption", JOptionPane.QUESTION_MESSAGE, null,
					null, KeyEvent.getKeyText(e.getKeyCode()));
			dialogOpen = false;
			if (s == null) return;
			for (GWMControllerInterface viewer: viewers) {
				viewer.updateCaption(targetBlock, s);
			}
			if (targetBlock > 100) {
				Connection c = connections.get(new Integer(targetBlock));
				if (c != null) c.setCaption(s);
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent e) {
	}

	public void refresh() {
	}

	public void removeTuioCur(long session_id) {
		// TODO Auto-generated method stub
		// System.out.println("finger removed");
/*		Connector c = connectors.get(new Long(session_id));
		if (c != null) {
			connectors.remove(new Long(session_id));
		}*/
	}

	public void removeTuioObj(long session_id, int fiducial_id) {
		if (fiducial_id == ReactivisionConstants.HISTORY) {
			deactivateHistoryMode(session_id);
		} 		
		else if (fiducial_id == ReactivisionConstants.CONNECTION_DIRECTED)
			directedConnectors = false;
		else if (fiducial_id == ReactivisionConstants.NAMING) {
			namingMarkerValid = true;
		}	
		else if (fiducial_id == ReactivisionConstants.ARROW) {
			arrowMarkerValid = true;
		}
		else if (fiducial_id == ReactivisionConstants.CONNECTION_ERASE) {
			connectorEraseMode = false;
			for (GWMControllerInterface viewer: viewers) {
				viewer.eraseMode(false);
			}	
		}
		else if (ReactivisionConstants.isBasicBuildingBlock(fiducial_id)) {
			addRemoveCandidate(fiducial_id);
		} else if (fiducial_id == ReactivisionConstants.OPEN_BLOCK) {
			closeBlock(session_id);
		} else if (ReactivisionConstants.isArtifact(fiducial_id)) {
			closeArtifact(fiducial_id);
		}
	}

	public void closeArtifact(int fiducial_id) {
		if (fiducial_id % 3 == 0) {
			artifactSnapshot = null;
		}
		
		for (GWMControllerInterface viewer: viewers) {
			viewer.closeArtifact(fiducial_id);
		}			
	}
	
	public void reset() {
		System.out.println("Resetting application");
		for (GWMControllerInterface viewer: viewers) {
			viewer.reset();
		}
		ei.reset();
		restoreMode = false;
		restoreState = null;
		historyMode = false;
		artifactSnapshot = null;
		directedConnectors = false;
		connectorEraseMode = false;
		latestAddedFigure = -1;
		connectors = Collections
				.synchronizedMap(new HashMap<Long, Connector>());
		removeCandidates = Collections
				.synchronizedMap(new HashMap<Integer, BlockRemoveCandidate>());
		connectionRemoveCandidates = Collections
				.synchronizedSet(new HashSet<ConnectionRemoveCandidate>());
		openBlocks = Collections
				.synchronizedMap(new HashMap<Long, Integer>());
		connections = Collections
		.synchronizedMap(new HashMap<Integer, Connection>());
		blocks = Collections
		.synchronizedMap(new HashMap<Integer, Block>());

		artifacts = Collections
		.synchronizedMap(new HashMap<Integer, Artifact>());

		lastAngle = 1000;			
		t.cancel();
		t.purge();
		t = new Timer();
		t.schedule(new BlockRemover(), 0, 300);
		t.schedule(new ConnectionRemover(), 0, 5000);
		t.schedule(new ConnectorWatchdog(), 0, 300);
		t.schedule(new LastChangeChecker(), 0, 3000);
		lastTimeChanged = System.currentTimeMillis();
		snapshotAlreadyCreated = true;
		history = new ReactivisionHistory();	
		
		calibrationMode = false;
		currentlySelectedBlock = -1;
		lastSnapshot = 0;
		
		semantics = new HashMap<String, String>();
		semantics.put("red", null);
		semantics.put("blue", null);
		semantics.put("yellow", null);
		askForSemantics = true;


	}

	public void updateCaption(int fiducial_id) {
		Object c = getCaption(fiducial_id);
		if (c != null) {
			for (GWMControllerInterface viewer: viewers) {
				viewer.updateCaption(fiducial_id, c);
			}
		
		}
	}

	public void updateTuioCur(long session_id, float xpos, float ypos,
			float x_speed, float y_speed, float m_accel) {

/*		Connector c = connectors.get(new Long(session_id));
		if (c != null && !c.isExpired()) {
			int x, y;
			x = Math.round(xpos * 1000);
			y = Math.round(ypos * 1000);
			c.setLocation(x, y);
		}*/
	}

	public void updateTuioObj(long session_id, int fiducial_id, float xpos,
			float ypos, float angle, float x_speed, float y_speed,
			float r_speed, float m_accel, float r_accel) {
		int x, y, rot;
	//	if (blocks.get(new Integer(fiducial_id)) == null) addTuioObj(session_id, fiducial_id);
			
		x = Math.round(xpos * 1000);
		y = Math.round(ypos * 1000);
		rot = Math.round(-1.0f * Math.round(Math.toDegrees(angle)));
		if (fiducial_id == ReactivisionConstants.HISTORY) {
			controlHistoryMode(angle);
		} else if (fiducial_id == ReactivisionConstants.OPEN_BLOCK)
			openBlock(session_id, x, y);
		else if (fiducial_id == ReactivisionConstants.CONNECTION_ERASE)
			eraseConnection(x, y);
		else if (fiducial_id == ReactivisionConstants.NAMING) {
			nameBlock(x,y);
		}	
		else if (fiducial_id == ReactivisionConstants.ARROW) {
			createArrowTip(x,y);
		}
		else if (ReactivisionConstants.isArtifact(fiducial_id))
			processArtifact(fiducial_id, x, y);
		else if (ReactivisionConstants.isBasicBuildingBlock(fiducial_id)) {
			processBasicBuidlingBlock(session_id, fiducial_id, x, y, rot);
			if (restoreMode && currentRestoreTask != null
					&& currentRestoreTask.completed())
				nextRestoreTask();

		}
	}

	protected void nameBlock(int x, int y) {
		if (namingMarkerValid) {
				System.out.println("Block naming "+x+" "+y);
				int selectionCandidate = getNearestElement(x, y);
				if (currentlySelectedBlock != -1) {
					for (GWMControllerInterface viewer: viewers) {
						viewer.toggleNamingMarker(currentlySelectedBlock,0);
					}
				}
				if (selectionCandidate != currentlySelectedBlock) {
					currentlySelectedBlock = selectionCandidate;
					for (GWMControllerInterface viewer: viewers) {
						viewer.toggleNamingMarker(currentlySelectedBlock,1);
					}
				}
				else {
					currentlySelectedBlock = -1;
				}
			namingMarkerValid = false;
		}
	}
	
	protected void createArrowTip(int x, int y) {
		if (arrowMarkerValid) {
			System.out.println("creating arrow tip");
		int arrowTip = -1;
		double minDist = 100000000;
		boolean fromTo = false;
		for (Connection c: connections.values()) {
			if (blocks.get(c.getFrom()) == null || blocks.get(c.getTo()) == null) continue;
			int x1, y1, x2, y2;
			x1 = blocks.get(c.getFrom()).getX();
			y1 = blocks.get(c.getFrom()).getY();
			x2 = blocks.get(c.getTo()).getX();
			y2 = blocks.get(c.getTo()).getY();
			double dist = Line2D.ptLineDist(x1,y1,x2,y2,x,y);
			if (dist < 20) {
				if (dist < minDist) {
					minDist = dist;
					arrowTip = c.getID();
					double dist1 = Point2D.distance(x1,y1,x,y);
					double dist2 = Point2D.distance(x2,y2,x,y);
					if (dist1 < dist2) fromTo = false;
					else fromTo = true; 
				}
			}
		}
		if (arrowTip != -1) { 
			Connection alter = connections.get(new Integer(arrowTip));
			if (fromTo == false && alter.getDirection() == Connection.ARROW_NONE) alter.setDirection(Connection.ARROW_START);
			else if (fromTo == false && alter.getDirection() == Connection.ARROW_END) alter.setDirection(Connection.ARROW_BOTH);
			else if (fromTo == false && alter.getDirection() == Connection.ARROW_START) alter.setDirection(Connection.ARROW_NONE);
			else if (fromTo == false && alter.getDirection() == Connection.ARROW_BOTH) alter.setDirection(Connection.ARROW_END);
			else if (fromTo == true && alter.getDirection() == Connection.ARROW_NONE) alter.setDirection(Connection.ARROW_END);
			else if (fromTo == true && alter.getDirection() == Connection.ARROW_START) alter.setDirection(Connection.ARROW_BOTH);
			else if (fromTo == true && alter.getDirection() == Connection.ARROW_END) alter.setDirection(Connection.ARROW_NONE);
			else if (fromTo == true && alter.getDirection() == Connection.ARROW_BOTH) alter.setDirection(Connection.ARROW_START);
			for (GWMControllerInterface viewer: viewers) {
				viewer.addOrAlterConnection(alter.getFrom(), alter.getTo(), alter.getDirection(), alter.getID(), alter.getCaption(), true);
			}
			arrowMarkerValid = false;
		}
		}
	}
	
	protected void eraseConnection (int x, int y) {
		System.out.println("checking for removable connections");
		HashSet<Integer> toBeRemoved = new HashSet<Integer>();
		for (Connection c: connections.values()) {
			if (blocks.get(c.getFrom()) == null || blocks.get(c.getTo()) == null) continue;
			int x1, y1, x2, y2;
			x1 = blocks.get(c.getFrom()).getX();
			y1 = blocks.get(c.getFrom()).getY();
			x2 = blocks.get(c.getTo()).getX();
			y2 = blocks.get(c.getTo()).getY();
			double dist = Line2D.ptLineDist(x1,y1,x2,y2,x,y);
			if (dist < 20) {
				toBeRemoved.add(new Integer(c.getID()));
			}
		}
		for (Integer remove: toBeRemoved) {
			connections.remove(remove);
			for (GWMControllerInterface viewer: viewers) {
				viewer.removeConnection(remove);
			}
			modelChanged();
			break;
		}
	}
	
	protected void activateHistoryMode(long session_id) {
		//history.addSnapshot(blocks, connections);
/*		for (GWMControllerInterface viewer: viewers) {
			viewer.saveImplicitSnapshot();
		}*/
		if (currentlySelectedBlock != -1) {
			for (GWMControllerInterface viewer: viewers) {
				viewer.toggleNamingMarker(currentlySelectedBlock,0);
			}
		}
		currentlySelectedBlock = -1;
		lastSnapshot = System.currentTimeMillis();
		historyMode = true;
		history.goToLatestSnapshot();
		for (GWMControllerInterface viewer: viewers) {
			viewer.activateHistoryMode(session_id);
		}
	}

	protected void addRemoveCandidate(int fiducial_id) {
		System.out.println("adding Remove Candidate " + fiducial_id + " ...");
		removeCandidates.put(new Integer(fiducial_id),
				new BlockRemoveCandidate(fiducial_id));
	}

	protected void checkForPendingConnections(int returned) {
		System.out.println("checking for pending connections ...");
		Set<ConnectionRemoveCandidate> reanimatedConnections = Collections.synchronizedSet(new HashSet<ConnectionRemoveCandidate>());
		try {
			for (ConnectionRemoveCandidate c : connectionRemoveCandidates) {
				if (!c.isExpired() && c.causeReturned(returned)) {
					Connection con = c.getConnection();
					//con.connect(con.getFrom(), con.getTo());
					for (GWMControllerInterface viewer: viewers) {
						viewer.addOrAlterConnection(con.from, con.to, con.getDirection(), con.getID(), con.getCaption(), false);
						viewer.updateCaption(con.getID(), con.getCaption());
					}
					reanimatedConnections.add(c);
					System.out.println("reanimated connection "
							+ c.getConnection().getID());
				}
			}
		} catch (ConcurrentModificationException cme) {
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (ConnectionRemoveCandidate reanimated : reanimatedConnections) {
			connectionRemoveCandidates.remove(reanimated);
		}
	}

	protected void closeBlock(long id) {
		// System.out.println("Block closed: "+id);
		if (openBlocks.get(new Long(id)) == null) return;
 		int bb_id = openBlocks.get(new Long(id)).intValue();
		openBlocks.remove(new Long(id));
		Block b = blocks.get(new Integer(bb_id));
		if (b == null) return;
		b.close();
		for (GWMControllerInterface viewer: viewers) {
			viewer.closeBlock(bb_id);
		}
	}

	protected void controlHistoryMode(float angle) {
		if (!historyMode || restoreMode) return;
		if (lastAngle == 1000)
			lastAngle = angle;
		if ((angle - lastAngle > Math.PI / 6 && angle - lastAngle < 3)
				|| (lastAngle > 5.8 && angle < 0.5)) {
			history.goToPreviousSnapshot();
			for (GWMControllerInterface viewer: viewers) {
				viewer.gotoPreviousSnapshot();
			}
			lastAngle = angle;
		} else if ((lastAngle - angle > Math.PI / 6 && lastAngle - angle < 3)
				|| (angle > 5.8 && lastAngle < 0.5)) {
			history.goToNextSnapshot();
			for (GWMControllerInterface viewer: viewers) {
				viewer.gotoNextSnapshot();
			}
			lastAngle = angle;
		}
	}

	protected void createArtifact(int fiducial_id) {
		Artifact a = artifacts.get(new Integer(fiducial_id));
		if (a == null) {
			a = new Artifact(fiducial_id);	
			a.setType(fiducial_id % 3);
			if (fiducial_id % 3 == 0) {
				a.setRepresentedObject(new ReactivisionSnapshot(blocks,connections));
			}
			if (fiducial_id % 3 == 1) {
				JFileChooser fc = new JFileChooser();
				fc.showDialog(null, "Bind to Artifact");
				File selFile = fc.getSelectedFile();
				a.setRepresentedObject(selFile);
			}
			if (fiducial_id % 3 == 2) {
				a.setRepresentedObject(ei.takePicture());
			}
			artifacts.put(new Integer(fiducial_id), a);
			for (GWMControllerInterface viewer: viewers) {
				viewer.createArtifact(a);
			}
			
		}
		else {
			if ((a.getContainer_id() == -1 && openBlocks.size() == 0) ||
					a.getContainer_id() != -1) {
				if (fiducial_id % 3 == 0) {
					artifactSnapshot = (ReactivisionSnapshot) a.getRepresentedObject();
				}
				for (GWMControllerInterface viewer: viewers) {
					viewer.openArtifact(a.getId());
				}
			}
			
		}
	}

	protected void createBasicBuildingBlock(long session_id, int fiducial_id) {
		Object caption = getCaption(fiducial_id);
		//latestAddedFigure = fiducial_id;
		Block b = new Block(fiducial_id);
		blocks.put(new Integer(fiducial_id), b);
		for (GWMControllerInterface viewer: viewers) {
			viewer.addBasicBuildingBlock(session_id, b, caption);
		} 
		modelChanged();
		System.out.println("created basic building block "+fiducial_id);
	}

	protected void deactivateHistoryMode(long session_id) {
		historyMode = false;
		for (GWMControllerInterface viewer: viewers) {
			viewer.deactivateHistoryMode(session_id);
		}
		lastAngle = 1000;
	}

	protected Object getCaption(int fiducial_id) {
		Object caption = null;
		if (ei != null
				&& (caption == null || caption.equals("") || caption
						.equals(new String("" + fiducial_id))))
			caption = ei.getCaption(fiducial_id);
		return caption;
	}

	protected int getNearestElement(int x, int y) {
		int nearestBlock = -1;
		double nearestBlockDistance = 1000000000.0;
		int nearestConnector = -1;
		double nearestConnectorDistance = 1000000000.0;
		if (x != -1) {
			Point p = new Point(x, y);
				try {
				for (Block b : blocks.values()) {
					double distance = Math.abs(p.distance(new Point(b.getX(),b.getY())));
					if (distance < Math.min(150,
							nearestBlockDistance)) {
						nearestBlockDistance = distance;
						nearestBlock = b.getId();
					}
				}
			} catch (ConcurrentModificationException e) {
				e.printStackTrace();
			}
		}

		for (Connection c: connections.values()) {
			if (blocks.get(c.getFrom()) == null || blocks.get(c.getTo()) == null) continue;
			int x1, y1, x2, y2;
			x1 = blocks.get(c.getFrom()).getX();
			y1 = blocks.get(c.getFrom()).getY();
			x2 = blocks.get(c.getTo()).getX();
			y2 = blocks.get(c.getTo()).getY();
			double dist = Line2D.ptLineDist(x1,y1,x2,y2,x,y);
			if (dist < 30) {
				if (dist < Math.min(150,nearestConnectorDistance)) {
					nearestConnectorDistance = dist;
					nearestConnector = c.getID();
				}
			}
		}

		int nearest = -1;
		if (nearestBlockDistance < 100) nearest = nearestBlock;
		else nearest = nearestConnector;
		System.out.println(nearest+" "+nearestBlock+" "+nearestBlockDistance+" "+nearestConnector+" "+nearestConnectorDistance);
		return nearest;				
	}
	
	protected int getNearestBlock(int x, int y) {
		int nearest = -1;
		if (x != -1) {
			Point p = new Point(x, y);
			double nearest_distance = 1000000000.0;
			try {
				for (Block b : blocks.values()) {
					double distance = Math.abs(p.distance(new Point(b.getX(),b.getY())));
					if (distance < Math.min(150,
							nearest_distance)) {
						nearest_distance = distance;
						nearest = b.getId();
					}
				}
			} catch (ConcurrentModificationException e) {
				e.printStackTrace();
			}
		}
		return nearest;		
	}

	protected int getNearestBlock(int x, int y, int id) {
		int nearest = -1;
		if (x != -1) {
			Point p = new Point(x, y);
			double nearest_distance = 1000000000.0;
			try {
				for (Block b : blocks.values()) {
					if (b.getId() != id) {
						double distance = Math.abs(p.distance(new Point(b.getX(),b.getY())));
						if (distance < Math.min(150,
								nearest_distance)) {
							nearest_distance = distance;
							nearest = b.getId();
						}
					}
				}
			} catch (ConcurrentModificationException e) {
				e.printStackTrace();
			}
		}
		return nearest;		
	}

	protected String getSemantics(String blockType) {
		return semantics.get(blockType);
	}
	
	protected void modelChanged() {
		lastTimeChanged = System.currentTimeMillis();
		snapshotAlreadyCreated = false;
		//System.out.println("Model changed");
	}

	protected void nextRestoreTask() {
		if (!restoreMode)
			return;
		Collection<Block> targetState = restoreState.getBasicBuildingBlocks().values();
		Vector<Block> blocksToRemove = new Vector<Block>();
		for (Block b : blocks.values()) {
			boolean mayStay = false;
			for (Block c : targetState) {
				if (b.getId() == c.getId()) mayStay = true;
			}
			if (!mayStay) blocksToRemove.add(b);
		}
		System.out.println("Searching for Blocks to remove ...");
		if (blocksToRemove.size() != 0) {
			System.out.println("Found Block to remove!");
			Collections.sort(blocksToRemove, new Comparator<Block>() {
			    public int compare(Block o1, Block o2) {
			        int id1 = o1.getId();
			        int id2 = o2.getId();
			        return id1 - id2;
			    }
			});
			Block blockToRemove = blocksToRemove
					.iterator().next();
			System.out.println(blockToRemove.getId());
			currentRestoreTask = new RestoreTask(blockToRemove,
					RestoreTask.REMOVE, 0, 0);
			for (GWMControllerInterface viewer: viewers) {
				viewer.drawRemoveSign(blockToRemove.getId());
			}

//			drawRemoveSign(blockToRemove);
		} else {
			Vector<Block> blocksToMove = new Vector<Block>();
			for (Block b : blocks.values()) {
				boolean mayStay = false;
				for (Block c : targetState) {
					if (b.getId() == c.getId()) mayStay = true;
				}
				if (mayStay) blocksToMove.add(b);
			}
			System.out.println("Searching for Blocks to move ...");
			Block blockToMove = null;
			int goalX = 0;
			int goalY = 0;
			Collections.sort(blocksToMove, new Comparator<Block>() {
			    public int compare(Block o1, Block o2) {
			        int id1 = o1.getId();
			        int id2 = o2.getId();
			        return id1 - id2;
			    }
			});

			synchronized (blocksToMove) {
				Iterator<Block> bi = blocksToMove.iterator();
				while (bi.hasNext()) {
					blockToMove = bi.next();
					Block desiredState = restoreState.getBasicBuildingBlocks().get(
									new Integer(blockToMove.getId()));
					goalX = desiredState.getX();
					goalY = desiredState.getY();
					int curX = blockToMove.getX()-30;
					int curY = blockToMove.getY()-30;
					int varX = 60;
					int varY = 60;
					if (!(curX > goalX - varX && curX < goalX + varX
							&& curY > goalY - varY && curY < goalY + varY))
						break;
					else
						blockToMove = null;
				}
			}
			if (blockToMove != null) {
				System.out.println("Found Block to move!");
				currentRestoreTask = new RestoreTask(blockToMove, RestoreTask.MOVE, goalX, goalY);
				for (GWMControllerInterface viewer: viewers) {
					viewer.drawMoveSign(blockToMove.getId());
				}
			} else {
				System.out.println("Searching for Blocks to add ...");
				Vector<Block> blocksToAdd = new Vector<Block>();
				for (Block b : targetState) {
					boolean isMissing = true;
					for (Block c : blocks.values()) {
						if (b.getId() == c.getId()) isMissing = false;
					}
					if (isMissing) blocksToAdd.add(b);
				}
				if (blocksToAdd.size() != 0) {
					System.out.println("Found Block to add!");
					Collections.sort(blocksToAdd, new Comparator<Block>() {
					    public int compare(Block o1, Block o2) {
					        int id1 = o1.getId();
					        int id2 = o2.getId();
					        return id1 - id2;
					    }
					});

					Block blockToAdd = blocksToAdd
							.iterator().next();
					currentRestoreTask = new RestoreTask(blockToAdd,
							RestoreTask.ADD, blockToAdd.getX(), blockToAdd.getY());
					for (GWMControllerInterface viewer: viewers) {
						viewer.drawAddSign(blockToAdd.getId());
					}

				} else {
					System.out.println("Restore finished");
					for (GWMControllerInterface viewer: viewers) {
						viewer.cleanUpAfterRestore();
					}					
					renewConnections(restoreState
							.getConnections());
					if (historyMode) {
						for (GWMControllerInterface viewer: viewers) {
							viewer.removeGlass();
						}
					}
					restoreMode = false;
					restoreState = null;
				}
			}
		}
	}

	
	protected void openBlock(long session_id, int x, int y) {
		if (openBlocks.containsKey(new Long(session_id)))
			return;
		int nearest = getNearestBlock(x, y);
		if (nearest != -1
				&& !openBlocks.containsValue(new Integer(nearest))) {
			blocks.get(new Integer(nearest)).open();
			openBlocks.put(new Long(session_id), new Integer(nearest));
			for (GWMControllerInterface viewer: viewers) {
				viewer.openBlock(nearest);
			}
		}
	}

	protected void processArtifact(int fiducial_id, int x, int y) {
		if (artifacts.get(new Integer(fiducial_id)).getContainer_id() != -1) return;
		System.out.println("Processing artifact " + fiducial_id);
		int block_id = -1;
		if (x == -1 && y == -1) {
			if (openBlocks.size() == 0) return;
			if (openBlocks.size() == 1) block_id = openBlocks.values().iterator().next().intValue();
			if (openBlocks.size() > 1) {
				Vector<String> titles = new Vector<String>();
				for (Integer i: openBlocks.values()) {
					titles.add("Block "+blocks.get(i).getId());
				}
				titles.toArray();
				Object o = JOptionPane.showInputDialog(null,"Please select block to put artifact in:", "Please select ...", JOptionPane.QUESTION_MESSAGE, null, titles.toArray(new String[0]),titles.get(0));
				if (o == null) block_id = -1;
				else {
					StringTokenizer st = new StringTokenizer((String) o);
					st.nextElement();
					block_id = Integer.parseInt((String) st.nextElement());
				}
			}
		}
		else block_id = getNearestBlock(x,y);
		if (block_id == -1) return;
		for (GWMControllerInterface viewer: viewers) {
			viewer.putArtifactIntoBlock(fiducial_id, block_id);
		}
		artifacts.get(new Integer(fiducial_id)).setContainer_id(block_id);
	}

	protected void processBasicBuidlingBlock(long session_id, int fiducial_id,
			int x, int y, int rot) {
		Block b = blocks.get(new Integer(fiducial_id));
		if (b == null) return;
		boolean firstOccurrence = false;
		if (b.getX() == -1) firstOccurrence = true;
		b.setX(x);
		b.setY(y);
		b.setRot(rot);
		if (b.positionChangedBy(10)) {
			modelChanged();
		}
		for (GWMControllerInterface viewer: viewers) {
			viewer.moveBasicBuildingBlock(fiducial_id, x, y, rot);
		}
		synchronized (connections) {
			for (Connection c : connections.values()) {
				if ((c.getFrom() != -1 && c.getFrom() == fiducial_id)
						|| (c.getTo() != -1 && c.getTo() == fiducial_id)) {
					for (GWMControllerInterface viewer: viewers) {
						viewer.updateConnection(c.getID());
					}
				}
			}
		}
//		 auto-connect by proximity
		int nearestID = getNearestBlock(x,y, fiducial_id);
		if (nearestID != -1) {
			Block nearestBlock = blocks.get(new Integer(nearestID));
			double distance = Math.abs((new Point(x,y)).distance(new Point(nearestBlock.getX(),nearestBlock.getY())));
			if (distance <  100.0) {
				int end1 = fiducial_id;
				int end2 = nearestID;
				boolean alreadyConnected = false;
				for (Connection cx: connections.values()) {
					if ((end1 == cx.getFrom() && end2 == cx.getTo()) || (end2 == cx.getFrom() && end1 == cx.getTo())) alreadyConnected = true;
				}
				if (!alreadyConnected) {
					Connection c = new Connection(end1, end2, Connection.ARROW_NONE);
					connections.put(new Integer(c.getID()), c);
					if (currentlySelectedBlock != -1) {
						for (GWMControllerInterface viewer: viewers) {
							viewer.toggleNamingMarker(currentlySelectedBlock, 0);
						}						
					}
					for (GWMControllerInterface viewer: viewers) {
						viewer.addOrAlterConnection(end1, end2, Connection.ARROW_NONE, c.getID(), "", true);
						viewer.toggleNamingMarker(c.getID(), 1);
					}
					currentlySelectedBlock = c.getID();
					latestAddedFigure = c.getID();
					modelChanged();
					System.out.println("connected " + end1
							+ " with " + end2);
				}
			}
		}
// intial naming marker
		if (firstOccurrence) {
			if (currentlySelectedBlock != -1) {
				for (GWMControllerInterface viewer: viewers) {
					viewer.toggleNamingMarker(currentlySelectedBlock,0);
				}			
			}
			currentlySelectedBlock = fiducial_id;
			for (GWMControllerInterface viewer: viewers) {
				viewer.toggleNamingMarker(currentlySelectedBlock,1);
			}			
		}
	}

	protected void removeBlock(int fiducial_id) {
		if (!blocks.containsKey(new Integer(fiducial_id)))
			return;
		blocks.remove(new Integer(fiducial_id));
		for (GWMControllerInterface viewer: viewers) {
			viewer.removeBasicBuildingBlock(fiducial_id);
		}
		System.out.println("removed block "+fiducial_id);
		Set<Connection> connectionsToBeRemoved = Collections.synchronizedSet(new HashSet<Connection>());
		for (Connection c : connections.values()) {
			System.out.println("checking connection " + c.getID());
			if ((c.getFrom() != -1 && c.getFrom() == fiducial_id)
					|| (c.getTo() != -1 && c.getTo() == fiducial_id)) {
				System.out.println("removed connection " + c.getID());
				connectionsToBeRemoved.add(c);
				connectionRemoveCandidates.add(new ConnectionRemoveCandidate(c,
						fiducial_id));
			}
		}
		for (Connection remove : connectionsToBeRemoved) {
			for (GWMControllerInterface viewer: viewers) {
				viewer.removeConnection(remove.getID());
			}			
		}
		if (restoreMode && currentRestoreTask != null
				&& currentRestoreTask.completed())
			nextRestoreTask();
	}

	protected void renewConnections(Map<Integer, Connection> stored) {

		for (GWMControllerInterface viewer: viewers) {
			viewer.removeAllConnections();
		}
		for (Connection con: stored.values()) {
			for (GWMControllerInterface viewer: viewers) {
				viewer.addOrAlterConnection(con.from, con.to, con.getDirection(), con.getID(), con.getCaption(), false);
				viewer.updateCaption(con.getID(), con.getCaption());
			}	
		}
		connections = stored;		
	}
	
	protected void restorePreviousState() {
		if (!historyMode && artifactSnapshot == null) return;
		restoreMode = true;
		if (artifactSnapshot != null) restoreState = artifactSnapshot;
		else {
			restoreState = history.getCurrentSnapshot();
			System.out.println("restoring snapshot "+history.getNumberOfCurrentSnapshot());
		}
		for (GWMControllerInterface viewer: viewers) {
			viewer.restoreCurrentSnapshot();
		}
		this.nextRestoreTask();
	}
	
	protected void saveSnapshot() {
		if (System.currentTimeMillis() - lastSnapshot < 5000 || historyMode || restoreMode) return;
		history.addSnapshot(blocks, connections);
		for (GWMControllerInterface viewer: viewers) {
			viewer.saveSnapshot();
		}
		lastSnapshot = System.currentTimeMillis();
	}

	protected class BlockRemoveCandidate {
		protected int id;
		protected long timestamp;

		public BlockRemoveCandidate(int id) {
			this.id = id;
			this.timestamp = System.currentTimeMillis();
		}

		@Override
		public boolean equals(Object o) {
			return (o instanceof BlockRemoveCandidate && ((BlockRemoveCandidate) o).id == this.id);
		}

		public int getId() {
			return id;
		}

		public boolean isExpired() {
			return (System.currentTimeMillis() - this.timestamp > 500);
		}
	}

	protected class BlockRemover extends TimerTask {

		@Override
		public void run() {
			Set<Integer> removedCandidates = Collections.synchronizedSet(new HashSet<Integer>());
			try {
				synchronized (removeCandidates) {
					for (BlockRemoveCandidate r: removeCandidates.values()) {
						if (r.isExpired()) {
							removedCandidates.add(r.getId());
							removeBlock(r.getId());
							if (r.getId() == currentlySelectedBlock) currentlySelectedBlock = -1;
						}
					}
				}
			} catch (ConcurrentModificationException cme) {
				try {
					Thread.sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (Integer removed: removedCandidates) {
				synchronized (removeCandidates) {
					removeCandidates.remove(removed);
					modelChanged();
				}
			}
		}
	}

	protected class ConnectionRemoveCandidate {
		protected int blockIDCausingRemove;
		protected Connection c;
		protected long timestamp;

		public ConnectionRemoveCandidate(Connection c, int cause) {
			this.c = c;
			this.blockIDCausingRemove = cause;
			this.timestamp = System.currentTimeMillis();
		}

		public boolean causeReturned(int id) {
			return (id == blockIDCausingRemove);
		}

		public Connection getConnection() {
			return c;
		}

		public boolean isExpired() {
			return (System.currentTimeMillis() - this.timestamp > 8000);
		}

	}


	
	protected class ConnectionRemover extends TimerTask {

		@Override
		public void run() {
			Set<ConnectionRemoveCandidate> removedCandidates = Collections.synchronizedSet(new HashSet<ConnectionRemoveCandidate>());
			try {
				synchronized (connectionRemoveCandidates) {
					for (ConnectionRemoveCandidate c : connectionRemoveCandidates) {
						if (c.isExpired()) {
							removedCandidates.add(c);
							connections.remove(new Integer(c.c.getID()));
						}
					}
				}
			} catch (ConcurrentModificationException cme) {
				try {
					Thread.sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (ConnectionRemoveCandidate removed: removedCandidates) {
				synchronized (connectionRemoveCandidates) {
					connectionRemoveCandidates.remove(removed);
				}
			}
		}
	}
	
	protected class Connector extends Point {

		int connectedTo = -1;
		long sid;
		long timestamp;
		public boolean hasArrowTip;

		public Connector(long sid) {
			this.sid = sid;
			this.x = -1;
			this.y = -1;
			this.timestamp = System.currentTimeMillis();
			this.hasArrowTip = false;
		}

		public boolean isExpired() {
			return (System.currentTimeMillis() - this.timestamp > 5000);
		}

		public boolean isStable() {
			return (System.currentTimeMillis() - this.timestamp > 300);
		}

	}
	
	protected class ConnectorWatchdog extends TimerTask {

		private Connector oneEnd = null;
		private Connector otherEnd = null;
		private long commitDelay = 0;

		@Override
		public void run() {
			try {
				if (oneEnd != null && currentlySelectedBlock == -1)
					oneEnd = null;
				if (oneEnd != null && oneEnd.isExpired()) {
					for (GWMControllerInterface viewer: viewers) {
						viewer.toggleNamingMarker(oneEnd.connectedTo,0);
						currentlySelectedBlock = -1;
					}
					oneEnd = null;
				}
				if (otherEnd != null && otherEnd.isExpired()) {
					for (GWMControllerInterface viewer: viewers) {
						viewer.toggleNamingMarker(otherEnd.connectedTo,0);
						currentlySelectedBlock = -1;
					}
					otherEnd = null;
				}
				synchronized (connectors) {
					for (Connector candidate : connectors.values()) {
						if (candidate.isExpired()) {
							if (candidate == oneEnd) {
								for (GWMControllerInterface viewer: viewers) {
									viewer.toggleNamingMarker(oneEnd.connectedTo,0);
									currentlySelectedBlock = -1;
								}
								oneEnd = null;
							}
							if (candidate == otherEnd) {
								for (GWMControllerInterface viewer: viewers) {
									viewer.toggleNamingMarker(otherEnd.connectedTo,0);
									currentlySelectedBlock = -1;
								}
								otherEnd = null;
							}
							connectors.remove(new Long(candidate.sid));
							continue;
						}
						if (candidate.isStable() && candidate != oneEnd) {
							int connectedBlock = getNearestBlock(candidate.x, candidate.y);
						/*	if ((connectedBlock == -1 && oneEnd == null && otherEnd == null)) {
								connectors.remove(candidate.connectedTo);
								continue;
							}
							else {*/
								candidate.connectedTo = connectedBlock;
								System.out.println("Checking candidate "+candidate.sid+" (next to block "+connectedBlock+", "+candidate.x+" "+candidate.y+")");
								if (oneEnd == null) {
									oneEnd = candidate;
									for (GWMControllerInterface viewer: viewers) {
										viewer.toggleNamingMarker(oneEnd.connectedTo,1);
									}
									currentlySelectedBlock = oneEnd.connectedTo;
									System.out.println("Start found!");
									connectors.remove(new Long(candidate.sid));
									continue;
								} else {
									if (/*oneEnd.connectedTo == candidate.connectedTo &&*/ oneEnd.distance(new Point(candidate.x, candidate.y)) < 40) {
										oneEnd.hasArrowTip = true;
										for (GWMControllerInterface viewer: viewers) {
											viewer.toggleNamingMarker(oneEnd.connectedTo,2);
										}
										System.out.println("Start marked directed!");
										connectors.remove(new Long(candidate.sid));
										continue;
										}
									if (otherEnd != null && otherEnd != candidate /*&& otherEnd.connectedTo == candidate.connectedTo*/  && otherEnd.distance(new Point(candidate.x, candidate.y)) < 40) {
										otherEnd.hasArrowTip = true;
										for (GWMControllerInterface viewer: viewers) {
											viewer.toggleNamingMarker(otherEnd.connectedTo,2);
										}
										System.out.println("End marked directed!");
										connectors.remove(new Long(candidate.sid));
										continue;
										
									}
									if ((oneEnd.connectedTo != candidate.connectedTo) && (otherEnd == null)) {
								
										otherEnd = candidate;
										for (GWMControllerInterface viewer: viewers) {
											viewer.toggleNamingMarker(otherEnd.connectedTo,1);
										}
										System.out.println("End found!");
										commitDelay = System.currentTimeMillis();
										connectors.remove(new Long(candidate.sid));
										continue;
										}
								}
							//}
						}
					}
					if (oneEnd != null && otherEnd != null && System.currentTimeMillis()-commitDelay>300) {
						int end1 = oneEnd.connectedTo;
						int end2 = otherEnd.connectedTo;
						for (GWMControllerInterface viewer: viewers) {
							viewer.toggleNamingMarker(end1,0);
							viewer.toggleNamingMarker(end2,0);
						}
						connectors.clear();
						boolean removed = false;
						if (connectorEraseMode == true) {
							for (Connection con : connections.values()) {
								if (con != null) {
									if ((con.getFrom() == end1 && con.getTo() == end2)
											|| (con.getFrom() == end2 && con.getTo() == end1)) {
										connections.remove(new Integer(con.getID()));
										System.out.println("removing connection "+con.getID());
										for (GWMControllerInterface viewer: viewers) {
											viewer.removeConnection(con.getID());
										}
										modelChanged();
										removed = true;
										System.out
										.println("removed connection between "
												+ end1
												+ " and "
												+ end2);
										connectors.remove(new Long(oneEnd.sid));
										connectors.remove(otherEnd.sid);
									}
								}
							}
						}
						if (!removed) {
							int direction = Connection.ARROW_NONE;
							if (oneEnd.hasArrowTip && !otherEnd.hasArrowTip) direction = Connection.ARROW_START;
							if (!oneEnd.hasArrowTip && otherEnd.hasArrowTip) direction = Connection.ARROW_END;
							if (oneEnd.hasArrowTip && otherEnd.hasArrowTip) direction = Connection.ARROW_BOTH;
							
							if (directedConnectors) direction = Connection.ARROW_END;
							
							Connection c = new Connection(end1, end2, direction);
							connections.put(new Integer(c.getID()), c);
							for (GWMControllerInterface viewer: viewers) {
								viewer.addOrAlterConnection(end1, end2, direction, c.getID(), "", true);
							}
							latestAddedFigure = c.getID();
							modelChanged();
							System.out.println("connected " + end1
									+ " with " + end2);
						}
						oneEnd = null;
						otherEnd = null;
						currentlySelectedBlock = -1;
					}
				}

			} catch (ConcurrentModificationException cme) {
				try {
					Thread.sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected class LastChangeChecker extends TimerTask {

		@Override
		public void run() {
			if (System.currentTimeMillis() - lastTimeChanged > 3000 && !snapshotAlreadyCreated && !historyMode && !restoreMode) {				
				history.addSnapshot(blocks, connections);
				for (GWMControllerInterface viewer: viewers) {
					viewer.saveImplicitSnapshot();
				}
				snapshotAlreadyCreated = true;
			}
		}
	}
	

	// -- main -----------------------------------------------------------

	/*
	 * Command lines to start:
	 * 
	 * cd ~/Documents/Dokumente/Studium/Diss/System/temp
	 * /Users/oppl/Desktop/gwm/explicit.app/Contents/MacOS/reactivision -i x -p
	 * 3334 cd ~/Documents/Dokumente/Studium/Diss/System/Prototyp\
	 * II/reacTIVision2/macosx/build/Development/reacTIVision.app/Contents/MacOS
	 * ./reacTIVision -i x -t ../../../all2.trees -f 22
	 * 
	 * 
	 */
	
	protected class RestoreTask {

		protected static final int ADD = 2;
		protected static final int MOVE = 1;
		protected static final int REMOVE = 0;

		protected Block block;

		protected int removeTask;
		protected int x;
		protected int y;

		public RestoreTask(Block block, int type, int x, int y) {
			if (type == REMOVE)
				removeTask = REMOVE;
			if (type == ADD)
				removeTask = ADD;
			if (type == MOVE)
				removeTask = MOVE;
			this.block = block;
			this.x = x;
			this.y = y;
		}

		public boolean completed() {
			boolean finished = false;
			if (removeTask == REMOVE) {
				if (!blocks
						.containsKey(new Integer(block.getId()))) 
					finished = true;
			}
			if (removeTask == MOVE || removeTask == ADD) {
				if (!blocks.containsKey(new Integer(block.getId())))
					finished = false;
				else {
					Block currentState = blocks.get(new Integer(block.getId()));					
					int curX = currentState.getX() - 30;
					int curY = currentState.getY() - 30;
					int varX = 60;// currentState.displayBox().width/2;
					int varY = 60;// currentState.displayBox().height/2;
					if (curX > x - varX && curX < x + varX && curY > y - varY
							&& curY < y + varY)
						finished = true;
				}
			}
			if (finished) {
				for (GWMControllerInterface viewer: viewers) {
					viewer.removeMarker(block.getId());
				}				
			}
			return finished;
		}
	}
}
