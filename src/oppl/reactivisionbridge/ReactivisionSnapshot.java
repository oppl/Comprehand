package oppl.reactivisionbridge;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



public class ReactivisionSnapshot {

	protected Map<Integer, Block> basicBuildingBlocks;	
	protected Map<Integer, Connection> connections;
	protected boolean major;
	
	public boolean isMajor() {
		return major;
	}

	public void setMajor(boolean major) {
		this.major = major;
	}

	public ReactivisionSnapshot() {
		basicBuildingBlocks = Collections
				.synchronizedMap(new HashMap<Integer, Block>());
		connections = Collections
		.synchronizedMap(new HashMap<Integer, Connection>());
		major = false;
	}

	public ReactivisionSnapshot(Map<Integer, Block> bbbs, Map<Integer, Connection> conns) {
		basicBuildingBlocks = Collections
				.synchronizedMap(new HashMap<Integer, Block>());
		connections = Collections
		.synchronizedMap(new HashMap<Integer, Connection>());
		synchronized (bbbs) {
			for (Block b : bbbs.values()) {
				basicBuildingBlocks.put(new Integer(b.getId()),
						b.clone());
			}
		}
		synchronized (conns) {
			for (Connection c : conns.values()) {
				connections.put(new Integer(c.getID()), c.clone());
			}
		}	
		major = false;
	}
	
	public Map<Integer, Block> getBasicBuildingBlocks() {
		Map<Integer, Block> bbbs = Collections
				.synchronizedMap(new HashMap<Integer, Block>());
		synchronized (basicBuildingBlocks) {
			for (Block b : basicBuildingBlocks.values()) {
				bbbs
						.put(new Integer(b.getId()), b
								.clone());
			}
		}
		return bbbs;
	}

	public Map<Integer, Connection> getConnections() {
		Map<Integer, Connection> conns = Collections
				.synchronizedMap(new HashMap<Integer, Connection>());
		synchronized (basicBuildingBlocks) {
			for (Connection c : connections.values()) {
				conns
						.put(new Integer(c.getID()), c.clone());
			}
		}
		return conns;
	}
	
}
