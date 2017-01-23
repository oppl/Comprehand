package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * ContainedIn represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class ContainedIn extends Association {

	/**
	 * default Constructor
	 *
	 */
	public ContainedIn(){
		super();
		this.setType("ContainedIn");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public ContainedIn(mane.metamodel.Manager manager){
		super(manager);
		this.setType("ContainedIn");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
