package oppl.reactivisionbridge;

public class ReactivisionConstants {

	public final static int ARTIFACTS_MAX_VALUE = 39;
	
	public final static String BASEPATH = "/Users/oppl/Desktop/gwm/";
	public final static int BLOCKS_MAX_VALUE = 29;
	public final static int CONNECTION_DIRECTED = 78;
	public final static int CONNECTION_ERASE = 77;
	public final static int HISTORY = 89;

	public final static int OPEN_BLOCK = 90;
	
	public final static int NAMING = 92;
	public final static int ARROW = 91;
	
	
	public final static int RESTORE_PREVIOUS_STATE = 79;
	public final static int SNAPSHOT = 88;
	
	public static boolean isArtifact(int id) {
		return (id > BLOCKS_MAX_VALUE && id <= ARTIFACTS_MAX_VALUE);
	}

	public static boolean isBasicBuildingBlock(int id) {
		return (id >= 0 && id <= BLOCKS_MAX_VALUE);
	}

}
