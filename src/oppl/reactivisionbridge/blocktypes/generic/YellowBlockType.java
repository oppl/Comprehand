package oppl.reactivisionbridge.blocktypes.generic;

import oppl.reactivisionbridge.blocktypes.BlockType;

public class YellowBlockType extends BlockType {

	public YellowBlockType() {
		this.typeName = new String("yellow");
	}
	
	@Override
	public boolean isOfType(int id) {
		if ((id < 30) && (id % 3 == 2)) return true;
		return false;
	}

}
