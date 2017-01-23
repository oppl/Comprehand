package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * AssignmentOfRole represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class AssignmentOfRole extends Association {

	/**
	 * default Constructor
	 *
	 */
	public AssignmentOfRole(){
		super();
		this.setType("AssignmentOfRole");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public AssignmentOfRole(mane.metamodel.Manager manager){
		super(manager);
		this.setType("AssignmentOfRole");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
