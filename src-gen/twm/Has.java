package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * Has represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class Has extends Association {

	/**
	 * default Constructor
	 *
	 */
	public Has(){
		super();
		this.setType("Has");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public Has(mane.metamodel.Manager manager){
		super(manager);
		this.setType("Has");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
