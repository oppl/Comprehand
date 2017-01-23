package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * DescriptionOfRole represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class DescriptionOfRole extends Association {

	/**
	 * default Constructor
	 *
	 */
	public DescriptionOfRole(){
		super();
		this.setType("DescriptionOfRole");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public DescriptionOfRole(mane.metamodel.Manager manager){
		super(manager);
		this.setType("DescriptionOfRole");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
