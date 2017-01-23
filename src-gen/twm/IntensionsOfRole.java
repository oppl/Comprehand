package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * IntensionsOfRole represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class IntensionsOfRole extends Association {

	/**
	 * default Constructor
	 *
	 */
	public IntensionsOfRole(){
		super();
		this.setType("IntensionsOfRole");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public IntensionsOfRole(mane.metamodel.Manager manager){
		super(manager);
		this.setType("IntensionsOfRole");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
