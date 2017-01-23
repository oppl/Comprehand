package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * Responsible represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class Responsible extends Association {

	/**
	 * default Constructor
	 *
	 */
	public Responsible(){
		super();
		this.setType("Responsible");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public Responsible(mane.metamodel.Manager manager){
		super(manager);
		this.setType("Responsible");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
