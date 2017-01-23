package oppl.gwm.remote;

import java.awt.event.KeyListener;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashSet;

import oppl.gwm.GWMControllerInterface;
import oppl.reactivisionbridge.Block;

public class AppletDataDispatcher implements GWMControllerInterface {

	Collection<RemoteGWMControllerInterface> clients = new HashSet<RemoteGWMControllerInterface>();
	
	public void register(RemoteGWMControllerInterface rdv) {
		clients.add(rdv);
	}
	
	public void deregister(RemoteGWMControllerInterface rdv) {
		clients.remove(rdv);
	}
	
	public void cleanUpAfterRestore() {
	}

	public void activateHistoryMode(long session_id) {
		// TODO Auto-generated method stub
		for (RemoteGWMControllerInterface client: clients) {
			try {
				// make call to applet here:
				//client.activateHistoryMode(session_id);
			}
			catch (Exception e) {
				this.deregister(client);
			}
		}
	}

	public void defineSemantics(String type, String meaning) {
		
	}
	 
	public void addBasicBuildingBlock(long session, Block b, Object caption) {
		// TODO Auto-generated method stub
		
	}

	 
	public void addOrAlterConnection(int end1_id, int end2_id, int direction,
			int id, String caption, boolean flash) {
		// TODO Auto-generated method stub
		
	}

	 
	public void calibrate(int parameter, int value) {
		// TODO Auto-generated method stub
		
	}

	 
	public void closeBlock(int block_id) {
		// TODO Auto-generated method stub
		
	}

	 
	public boolean createArtifact(oppl.reactivisionbridge.Artifact a) {
		// TODO Auto-generated method stub
		return false;
	}

	 
	public void deactivateHistoryMode(long session_id) {
		// TODO Auto-generated method stub
		
	}

	 
	public void drawAddSign(int id) {
		// TODO Auto-generated method stub
		
	}

	 
	public void drawMoveSign(int id) {
		// TODO Auto-generated method stub
		
	}

	 
	public void drawRemoveSign(int id) {
		// TODO Auto-generated method stub
		
	}

	 
	public void eraseMode(boolean on) {
		// TODO Auto-generated method stub
		
	}

	 
	public void gotoNextSnapshot() {
		// TODO Auto-generated method stub
		
	}

	 
	public void gotoPreviousSnapshot() {
		// TODO Auto-generated method stub
		
	}

	 
	public void moveBasicBuildingBlock(int id, int x, int y, int rot) {
		// TODO Auto-generated method stub
		
	}

	 
	public void openArtifact(int id) {
		// TODO Auto-generated method stub
		
	}

	public void closeArtifact(int id) {
		
	}

	 
	public void openBlock(int id) {
		// TODO Auto-generated method stub
		
	}

	 
	public boolean putArtifactIntoBlock(int artifact_id, int block_id) {
		// TODO Auto-generated method stub
		return false;
	}

	 
	public void registerKeyListener(KeyListener k) {
		// TODO Auto-generated method stub
		
	}

	 
	public void removeAllConnections() {
		// TODO Auto-generated method stub
		
	}

	 
	public void removeBasicBuildingBlock(int id) {
		// TODO Auto-generated method stub
		
	}

	 
	public void removeConnection(int id) {
		// TODO Auto-generated method stub
		
	}

	 
	public void removeGlass() {
		// TODO Auto-generated method stub
		
	}

	 
	public void removeMarker(int id) {
		// TODO Auto-generated method stub
		
	}

	 
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	 
	public void restoreCurrentSnapshot() {
		// TODO Auto-generated method stub
		
	}

	 
	public void saveImplicitSnapshot() {
		// TODO Auto-generated method stub
		
	}

	 
	public void saveSnapshot() {
		// TODO Auto-generated method stub
		
	}

	 
	public void toggleNamingMarker(int id, int state) {
		// TODO Auto-generated method stub
		
	}

	 
	public boolean updateCaption(int id, Object caption) {
		// TODO Auto-generated method stub
		return false;
	}

	 
	public void updateConnection(int id) {
		// TODO Auto-generated method stub
		
	}
}
