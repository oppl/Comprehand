package oppl.reactivisionbridge.blocktypes;

import java.util.Vector;

public class BlockTypes {
	protected Vector<BlockType> types;
	
	public BlockType getBlockType(int id) {
		for (BlockType t: types) {
			if (t.isOfType(id)) return t;
		}
		return null;
	}
	
}
