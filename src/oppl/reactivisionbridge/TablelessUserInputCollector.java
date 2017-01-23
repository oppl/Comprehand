package oppl.reactivisionbridge;

import oppl.gwm.GWMControllerInterface;

public class TablelessUserInputCollector extends UserInputCollector {

	public TablelessUserInputCollector(GWMControllerInterface viewer,
			ExplicitInterface ei) {
		super(viewer, ei);
		// TODO Auto-generated constructor stub
	}
	
	public void removeTuioObj(long session_id, int fiducial_id) {
		if (fiducial_id == ReactivisionConstants.HISTORY) {
			deactivateHistoryMode(session_id);
		} 		
		else if (fiducial_id == ReactivisionConstants.CONNECTION_DIRECTED)
			directedConnectors = false;
		else if (fiducial_id == ReactivisionConstants.CONNECTION_ERASE) {
			connectorEraseMode = false;
			for (GWMControllerInterface viewer: viewers) {
				viewer.eraseMode(false);
			}	
		} else if (fiducial_id == ReactivisionConstants.OPEN_BLOCK) {
			closeBlock(session_id);
		} else if (ReactivisionConstants.isArtifact(fiducial_id)) {
			closeArtifact(fiducial_id);
		}
	}

	public void updateTuioObj(long session_id, int fiducial_id, float xpos,
			float ypos, float angle, float x_speed, float y_speed,
			float r_speed, float m_accel, float r_accel) {
		int x, y, rot;
	//	if (blocks.get(new Integer(fiducial_id)) == null) addTuioObj(session_id, fiducial_id);
			
		x = Math.round(xpos * 1000);
		y = Math.round(ypos * 1000);
		System.out.println(x+" "+y);
		rot = Math.round(-1.0f * Math.round(Math.toDegrees(angle)));
		if (fiducial_id == ReactivisionConstants.HISTORY) {
			controlHistoryMode(angle);
		} else if (fiducial_id == ReactivisionConstants.OPEN_BLOCK)
			openBlock(session_id, x, y);
		else if (ReactivisionConstants.isArtifact(fiducial_id))
			processArtifact(fiducial_id, x, y);
		else if (ReactivisionConstants.isBasicBuildingBlock(fiducial_id)) {
			if (x < 50 ) addRemoveCandidate(fiducial_id);
			else {
				processBasicBuidlingBlock(session_id, fiducial_id, x, y, rot);
				if (restoreMode && currentRestoreTask != null
						&& currentRestoreTask.completed())
					nextRestoreTask();
			}

		}
	}

	protected void createBasicBuildingBlock(long session_id, int fiducial_id) {
		Object caption = getCaption(fiducial_id);
		latestAddedFigure = fiducial_id;
		Block b = new Block(fiducial_id);
		blocks.put(new Integer(fiducial_id), b);
		for (GWMControllerInterface viewer: viewers) {
			viewer.addBasicBuildingBlock(session_id, b, caption);
		} 
		modelChanged();
		System.out.println("created basic building block "+fiducial_id);
	}

}
