package oppl.gwm;

import java.awt.Color;
import java.util.Map;
import java.util.Vector;

import oppl.gwm.elements.BasicBuildingBlock;
import oppl.gwm.elements.Connection;

public class History {

	private int current;
	private Vector<Snapshot> history;
	
	public History() {
		history = new Vector<Snapshot>();
		current = 0;
		history.add(new Snapshot());
		markCurrentSnapshotMajor();
	}
	
	public void addSnapshot(Map<Integer, BasicBuildingBlock> bbbs, Map<Integer, Connection> cons, boolean explicitlyTriggered) {
		history.add(new Snapshot(bbbs,cons,explicitlyTriggered));
	}
	
	public Color getBackgroundColor() {
		int i=0;
		i = 255 - (history.size()-current) * Math.min(10, 128/history.size());
		Color c = new Color(i,i,i);
		return c;
	}
	
	public Snapshot getCurrentSnapshot() {
		return history.get(current);
	}
	
	public Snapshot getNextSnapshot() {
		goToNextSnapshot();
		return history.get(current);
	}

	public Snapshot getPreviousSnapshot() {
		goToPreviousSnapshot();
		return history.get(current);
	}

	public void goToEarliestSnapshot() {
		current = 0;
	}
	
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
			history.add(new Snapshot());			
		}
	}
	
	public int getNrOfSnapshots() {
		return history.size();
	}
	
	public int getPositionOfSnapshot(Snapshot s) {
		return history.indexOf(s);
	}
	
	public void markCurrentSnapshotMajor() {
		this.getCurrentSnapshot().setMajor(true);
	}
	
	public Vector<Snapshot> getAllSnapshots() {
		return history;
	}
}
