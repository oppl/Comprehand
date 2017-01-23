package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * Supporter represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class Supporter extends Association {

	/**
	 * default Constructor
	 *
	 */
	public Supporter(){
		super();
		this.setType("Supporter");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public Supporter(mane.metamodel.Manager manager){
		super(manager);
		this.setType("Supporter");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
