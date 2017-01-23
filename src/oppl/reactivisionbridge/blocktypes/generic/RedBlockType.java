package oppl.reactivisionbridge.blocktypes.generic;

import oppl.reactivisionbridge.blocktypes.BlockType;

public class RedBlockType extends BlockType {

	public RedBlockType() {
		this.typeName = new String("red");
	}

	@Override
	public boolean isOfType(int id) {
		if ((id < 30) && (id % 3 == 0)) return true;
		return false;
	}

}
