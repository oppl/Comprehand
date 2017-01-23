package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * Interdependent represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class Interdependent extends Association {

	/**
	 * default Constructor
	 *
	 */
	public Interdependent(){
		super();
		this.setType("Interdependent");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public Interdependent(mane.metamodel.Manager manager){
		super(manager);
		this.setType("Interdependent");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
