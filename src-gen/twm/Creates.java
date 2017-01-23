package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * Creates represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class Creates extends Association {

	/**
	 * default Constructor
	 *
	 */
	public Creates(){
		super();
		this.setType("Creates");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public Creates(mane.metamodel.Manager manager){
		super(manager);
		this.setType("Creates");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
