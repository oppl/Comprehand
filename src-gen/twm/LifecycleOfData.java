package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * LifecycleOfData represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class LifecycleOfData extends Association {

	/**
	 * default Constructor
	 *
	 */
	public LifecycleOfData(){
		super();
		this.setType("LifecycleOfData");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public LifecycleOfData(mane.metamodel.Manager manager){
		super(manager);
		this.setType("LifecycleOfData");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
