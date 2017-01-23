package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * Determines represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class Determines extends Association {

	/**
	 * default Constructor
	 *
	 */
	public Determines(){
		super();
		this.setType("Determines");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public Determines(mane.metamodel.Manager manager){
		super(manager);
		this.setType("Determines");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
