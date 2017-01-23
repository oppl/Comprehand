package oppl.reactivisionbridge.blocktypes.generic;

import oppl.reactivisionbridge.blocktypes.BlockType;

public class BlueBlockType extends BlockType {

	public BlueBlockType() {
		this.typeName = new String("blue");
	}

	@Override
	public boolean isOfType(int id) {
		if ((id < 30) && (id % 3 == 1)) return true;
		return false;
	}

}
