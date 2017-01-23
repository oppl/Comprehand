package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * IsInputFor represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class IsInputFor extends Association {

	/**
	 * default Constructor
	 *
	 */
	public IsInputFor(){
		super();
		this.setType("IsInputFor");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public IsInputFor(mane.metamodel.Manager manager){
		super(manager);
		this.setType("IsInputFor");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
