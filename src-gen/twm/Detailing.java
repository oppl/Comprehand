package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * Detailing represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class Detailing extends Association {

	/**
	 * default Constructor
	 *
	 */
	public Detailing(){
		super();
		this.setType("Detailing");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public Detailing(mane.metamodel.Manager manager){
		super(manager);
		this.setType("Detailing");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
