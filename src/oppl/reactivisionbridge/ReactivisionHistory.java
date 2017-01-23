package oppl.reactivisionbridge;

import java.awt.Color;
import java.util.Map;
import java.util.Vector;



public class ReactivisionHistory {

	private int current;
	private Vector<ReactivisionSnapshot> history;
	
	private static final int MAJOR_OFFSET = 500;
	
	public ReactivisionHistory() {
		history = new Vector<ReactivisionSnapshot>();
		current = 0;
		history.add(new ReactivisionSnapshot());
		markCurrentSnapshotMajor();
	}
	
	public void addSnapshot(Map<Integer, Block> bbbs, Map<Integer, Connection> conns) {
		System.out.println(bbbs.size() + " blocks and "+ conns.size() +" connections.");
		history.add(new ReactivisionSnapshot(bbbs,conns));
	}
	
	public Vector<Integer> checkForMajorSnapshots() {
		/*
		 * influence factors on delta score: 
		 * 	added and removed blocks (high score) 
		 * 	moved block (low score, depending on moved distance?) 
		 *  added connection (low score) 
		 *  explicit snapshot (always major?)
		 * 
		 * candidate for major snapshot: first snapshot that follows a peak of
		 * high delta scores (first rather stable snapshot)
		 */
		Vector<Integer> majorSnapshots = new Vector<Integer>();
		
		
		
		return majorSnapshots;
	}
	
	public Color getBackgroundColor() {
		int i=0;
		i = 255 - (history.size()-current) * Math.min(10, 128/history.size());
		Color c = new Color(i,i,i);
		return c;
	}
	
	public ReactivisionSnapshot getCurrentSnapshot() {
		return history.get(current);
	}
	
	public int getNumberOfCurrentSnapshot() {
		return current;
	}

	public ReactivisionSnapshot getNextSnapshot() {
		goToNextSnapshot();
		return history.get(current);
	}

	public ReactivisionSnapshot getPreviousSnapshot() {
		goToPreviousSnapshot();
		return history.get(current);
	}

/*	public void goToEarliestSnapshot() {
		current = 0;
	}
*/	
	public void goToLatestSnapshot() {
		current = history.size()-1;
	}
	
	public void goToNextSnapshot() {
		if (current < history.size()-1) current++;
	}
	
	public void goToPreviousSnapshot() {
		if (current != 0) current --;		
	}
	
	public void goToNextMajorSnapshot() {
		do {
			if (current < history.size()-1) current++;
		}
		while (!getCurrentSnapshot().isMajor());
	}
	
	public void goToPreviousMajorSnapshot() {
		do {
			if (current != 0) current --;		
		}
		while (!getCurrentSnapshot().isMajor());
	}

	public void removeCurrentSnapshot() {
		history.remove(current);
		if (current == history.size()) current--;
		if (history.size() == 0) {
			current = 0;
			history.add(new ReactivisionSnapshot());			
		}
	}
	
	public void markCurrentSnapshotMajor() {
		this.getCurrentSnapshot().setMajor(true);
	}
}
