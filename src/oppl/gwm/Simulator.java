package oppl.gwm;

import java.util.HashMap;
import java.util.Map;

import oppl.reactivisionbridge.ReactivisionConstants;
import oppl.reactivisionbridge.UserInputCollector;

public class Simulator {
	
	protected UserInputCollector target = null;
	protected int session;
	private Map<Integer, Block> blocks = new HashMap<Integer, Block>();
	
	public Simulator(UserInputCollector uic) {
		target = uic;
		session = 0;
	}
	
	public void run() {
		this.delay(100);
		this.placeBlock(0, 200, 200);
		this.delay(100);
		this.moveBlock(0, 300, 350, false, 1000);
		this.delay(100);
		this.placeBlock(1, 700, 700);
		this.delay(100);
		this.takeSnapshot();
		this.delay(100);
		this.putFinger(300, 350);
		this.delay(100);
		this.putFinger(700, 700);
		this.delay(100);
		this.removeFinger(session-1);
		this.removeFinger(session);
		this.delay(100);
		this.moveBlockRelative(0, 1, 1, true, 0);
		this.moveBlockRelative(1, 1, 1, true, 0);
		this.delay(100);
		this.takeSnapshot();
		this.delay(100);
		this.placeBlock(2, 700, 200);
		this.delay(100);
		this.takeSnapshot();
		this.delay(50);
		this.moveBlock(2, 700, 650, false, 3000);
		this.moveBlock(2, 800, 300, false, 2000);
		this.delay(500);
		this.historyActivate();
		this.delay(100);
		this.historyBack();
		this.delay(100);
		this.historyBack();
		this.delay(100);
		this.historyForth();
		this.delay(200);
		this.historyDeactivate();
		this.delay(200);
		this.saveToArtifact();
		this.delay(200);
		this.blockRemove(0);
		this.blockRemove(1);
		this.blockRemove(2);
		this.delay(200);
		this.placeBlock(3, 200, 200);
		this.delay(200);
		this.embedArtifactIntoBlock(3);
	}
	
	private void takeSnapshot() {
		this.blockAdd(ReactivisionConstants.SNAPSHOT);
		this.delay(100);
		this.blockRemove(ReactivisionConstants.SNAPSHOT);
	}
	
	private void saveToArtifact() {
		this.blockAdd(30);
		this.delay(100);
		this.blockRemove(30);
	}
	
	private void embedArtifactIntoBlock(int block) {
		this.blockAdd(ReactivisionConstants.OPEN_BLOCK);
		Block opened = this.blocks.get(new Integer(block));
		this.moveBlock(ReactivisionConstants.OPEN_BLOCK, opened.x, opened.y, true, 0);
		this.delay(100);
		this.placeBlock(30, 200, 200);
		this.delay(100);
		this.blockRemove(30);
		this.delay(100);
		this.blockRemove(ReactivisionConstants.OPEN_BLOCK);

	}
	
	private void historyActivate() {
		this.blockAdd(ReactivisionConstants.HISTORY);
	}

	private void historyDeactivate() {
		this.blockRemove(ReactivisionConstants.HISTORY);
	}
	
	private void historyForth() {
		for (int i=0; i<35; i++) this.blockRotateClockwise(ReactivisionConstants.HISTORY);
	} 
	
	private void historyBack() {
		for (int i=0; i<35; i++) this.blockRotateCounterclockwise(ReactivisionConstants.HISTORY);
	} 

	private void placeBlock(int fid, int x, int y) {
		this.blockAdd(fid);
		this.blockMove(fid, x, y);
	}

	private void moveBlock(int fid, int to_x, int to_y, boolean lifted, int duration) {
		if (lifted) {
			this.blockRemove(fid);
			this.delay(duration);
			this.blockAdd(fid);
			this.blockMove(fid, to_x, to_y);
		}
		else {
			Block b = blocks.get(new Integer(fid));
			int dx = b.x - to_x;
			int dy = b.y - to_y;
			int step_x = dx * 100 / duration;
			int step_y = dy * 100 / duration;
			for (int i = 0; i < duration/100; i++) {
				this.blockMove(fid, b.x-step_x, b.y-step_y);
				this.delay(100);
			}
			this.blockMove(fid, to_x, to_y);
		}
	}
	
	private void moveBlockRelative(int fid, int rel_x, int rel_y, boolean lifted, int duration) {
		Block b = blocks.get(new Integer(fid));
		if (b == null) {
			b = new Block();
			blocks.put(new Integer(fid), b);
		}
		this.moveBlock(fid, b.x + rel_x, b.y + rel_y, lifted, duration);
	}
	
	private void moveBlockTowards(int fid, int fid_to, boolean lifted, int duration) {
		Block b = blocks.get(new Integer(fid_to));
		if (b == null) return;
		this.moveBlock(fid, b.x, b.y, lifted, duration);
		
	}
	
	private void delay(long time) {
		try {
			Thread.sleep(time);
		}
		catch (Exception e) {
			
		}
	}
	
	private void blockAdd(int fid) {
		target.addTuioObj(0, fid);
		blocks.remove(new Integer(fid));
		blocks.put(new Integer(fid), new Block());
	}
	
	private void blockMove(int fid, int x, int y) {
		float x_pos = (float) 1.0 * x / 1000;
		float y_pos = (float) 1.0 * y / 1000;
		float rot = 0;
		Block b = blocks.get(new Integer(fid));
		if (b == null) {
			b = new Block();
			blocks.put(new Integer(fid), b);
		}
		rot = (float) (-1.0f * Math.toRadians(b.rot));
		target.updateTuioObj(0, fid, x_pos, y_pos, rot, 0, 0, 0, 0, 0);
		b.x = x;
		b.y = y;
	}
	
	private void blockRotate(int fid, int r) {
		float rot = (float) (-1.0f * Math.toRadians(r));
		Block b = blocks.get(new Integer(fid));
		if (b == null) {
			b = new Block();
			blocks.put(new Integer(fid), b);
		}
		float x_pos = (float) 1.0 * b.x / 1000;
		float y_pos = (float) 1.0 * b.y / 1000;
		target.updateTuioObj(0, fid, x_pos, y_pos, rot, 0, 0, 0, 0, 0);
		b.rot = r;
	}
	
	private void blockRotateClockwise(int fid) {
		Block b = blocks.get(new Integer(fid));
		if (b == null) {
			b = new Block();
			blocks.put(new Integer(fid), b);
		}
		b.rot++;
		float rot = (float) (-1.0f * Math.toRadians(b.rot));
		float x_pos = (float) 1.0 * b.x / 1000;
		float y_pos = (float) 1.0 * b.y / 1000;
		target.updateTuioObj(0, fid, x_pos, y_pos, rot, 0, 0, 0, 0, 0);		
	}
	
	private void blockRotateCounterclockwise(int fid) {
		Block b = blocks.get(new Integer(fid));
		if (b == null) {
			b = new Block();
			blocks.put(new Integer(fid), b);
		}
		b.rot--;
		float rot = (float) (-1.0f * Math.toRadians(b.rot));
		float x_pos = (float) 1.0 * b.x / 1000;
		float y_pos = (float) 1.0 * b.y / 1000;
		target.updateTuioObj(0, fid, x_pos, y_pos, rot, 0, 0, 0, 0, 0);		
	}

	private void blockRemove(int fid) {
		target.removeTuioObj(0, fid);
	}
	 
	
	private int putFinger(int x, int y) {
		float x_pos = (float) 1.0 * x / 1000;
		float y_pos = (float) 1.0 * y / 1000;
		session++;
		target.addTuioCur(session);
		target.updateTuioCur(session, x_pos, y_pos, 0, 0, 0);
		return session;
	}
	
	private void removeFinger(int id) {
		target.removeTuioCur(id);
	}
	
	private class Block {
		int x;
		int y;
		int rot;
		
		public Block() {
			x = 0;
			y = 0;
			rot = 270;
		}
	}
}
