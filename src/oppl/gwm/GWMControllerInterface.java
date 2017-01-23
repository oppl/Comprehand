package oppl.gwm;

import java.awt.event.KeyListener;

import oppl.reactivisionbridge.Artifact;
import oppl.reactivisionbridge.Block;

public interface GWMControllerInterface {

	public static final int OFFSET_X = 1;
	public static final int OFFSET_Y = 2;
	public static final int SCALE_X = 3;
	public static final int SCALE_Y = 4;
	
	public abstract void activateHistoryMode(long session_id);

	public abstract void addBasicBuildingBlock(long session, Block b,
			Object caption);

	public abstract void addOrAlterConnection(int end1_id, int end2_id,
			int direction, int id, String caption, boolean flash);

	public abstract void closeBlock(int block_id);

	public abstract boolean createArtifact(Artifact a);

	public abstract void deactivateHistoryMode(long session_id);
	
	public abstract void defineSemantics(String type, String meaning);

	public abstract void drawAddSign(int id);

	public abstract void drawMoveSign(int id);

	public abstract void drawRemoveSign(int id);

	public abstract void gotoNextSnapshot();

	public abstract void gotoPreviousSnapshot();

	public abstract void moveBasicBuildingBlock(int id, int x, int y, int rot);

	public abstract void openArtifact(int id);
	
	public abstract void closeArtifact(int id);

	public abstract void openBlock(int id);

	public abstract boolean putArtifactIntoBlock(int artifact_id, int block_id);

	public abstract void registerKeyListener(KeyListener k);
	
	public abstract void removeBasicBuildingBlock(int id);

	public abstract void removeConnection(int id);

	public abstract void removeAllConnections();

	public abstract void removeGlass();

	public abstract void removeMarker(int id);

	public abstract void reset();
	
	public abstract void restoreCurrentSnapshot();
	
	public abstract void saveImplicitSnapshot();
	
	public abstract void saveSnapshot();
	
	public abstract void toggleNamingMarker(int id, int state);
	
	public abstract boolean updateCaption(int id, Object caption);
	
	public abstract void updateConnection(int id);
	
	public abstract void calibrate(int parameter, int value);
	
	public abstract void eraseMode(boolean on);
	
	public abstract void cleanUpAfterRestore();

}