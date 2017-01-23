package oppl.gwm;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import oppl.gwm.elements.BasicBuildingBlock;
import oppl.gwm.elements.Connection;

import org.jhotdraw.figures.GroupFigure;
import org.jhotdraw.figures.PolyLineFigure;
import org.jhotdraw.figures.TextFigure;
import org.jhotdraw.framework.Figure;

public class Snapshot {

	protected Map<Integer, BasicBuildingBlock> basicBuildingBlocks;
	protected Map<Integer, GroupFigure> connections;
	protected boolean explicitlyTriggered;
	protected TextFigure tf;
	protected long timestamp;
	protected boolean major;

	
	public Snapshot() {
		basicBuildingBlocks = Collections
				.synchronizedMap(new HashMap<Integer, BasicBuildingBlock>());
		connections = Collections
				.synchronizedMap(new HashMap<Integer, GroupFigure>());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		tf = new TextFigure();
		tf.setText("Started modelling at " + df.format(new Date()));
		tf.moveBy(-tf.displayBox().x + 5, -tf.displayBox().y + 5);
		explicitlyTriggered = false;
		timestamp = System.currentTimeMillis();
		major = false;
	}

	public Snapshot(Map<Integer, BasicBuildingBlock> bbbs,
			Map<Integer, Connection> conns, boolean explicitlyTriggered) {
		basicBuildingBlocks = Collections
				.synchronizedMap(new HashMap<Integer, BasicBuildingBlock>());
		connections = Collections
				.synchronizedMap(new HashMap<Integer, GroupFigure>());
		synchronized (bbbs) {
			for (BasicBuildingBlock b : bbbs.values()) {
				basicBuildingBlocks.put(new Integer(b.getId()),
						(BasicBuildingBlock) b.clone());
				BasicBuildingBlock t = basicBuildingBlocks.get(new Integer(b
						.getId()));
/*				System.out.println("history added " + t.getId() + " at "
						+ t.getDrawingPosition().x + " " + t.getDrawingPosition().y
						+ " (original: " + b.getDrawingPosition().x + " "
						+ b.getDrawingPosition().y + ")");
*/			}
		}
		synchronized (conns) {
			for (Connection c : conns.values()) {

				connections.put(new Integer(c.getId()), (GroupFigure) c.getEquivalentLine());
			}
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		tf = new TextFigure();
		if (explicitlyTriggered) tf.setText("Snapshot taken " + df.format(new Date()));
		else tf.setText("Stable state saved at " + df.format(new Date()));
		tf.moveBy(-tf.displayBox().x + 5, -tf.displayBox().y + 5);
		this.explicitlyTriggered = explicitlyTriggered;
		timestamp = System.currentTimeMillis();
		major = false;
	}

	public boolean isMajor() {
		return major;
	}

	public void setMajor(boolean major) {
		this.major = major;
	}

	public Map<Integer, BasicBuildingBlock> getBasicBuildingBlocks() {
		Map<Integer, BasicBuildingBlock> bbbs = Collections
				.synchronizedMap(new HashMap<Integer, BasicBuildingBlock>());
		synchronized (basicBuildingBlocks) {
			for (BasicBuildingBlock b : basicBuildingBlocks.values()) {
				bbbs
						.put(new Integer(b.getId()), (BasicBuildingBlock) b
								.clone());
			}
		}
		return bbbs;
	}

/*	public Map<Integer, PolyLineFigure> getConnections() {
		Map<Integer, PolyLineFigure> conns = Collections
				.synchronizedMap(new HashMap<Integer, PolyLineFigure>());
		synchronized (connections) {
			for (PolyLineFigure c : connections.values()) {

				conns.put(new Integer(c.getId()), (PolyLineFigure) c.clone());
			}
		}
		return conns;
	}*/
	
	public Set<Figure> getSnapshot() {
		Set<Figure> snapshot = new HashSet<Figure>();
		snapshot.addAll(basicBuildingBlocks.values());
		snapshot.addAll(connections.values());
		snapshot.add(tf);
		return snapshot;
	}

	public boolean wasExplicitlyTriggered() {
		return this.explicitlyTriggered;
	}

	public boolean equals(Object o) {
		if (!(o instanceof Snapshot)) return false;
		Snapshot s = (Snapshot) o;
		if (this.timestamp == s.getTimestamp()) return true;
		return false;
	}

	public long getTimestamp() {
		return timestamp;
	}
}
