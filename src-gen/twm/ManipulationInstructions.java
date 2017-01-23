package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * ManipulationInstructions represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class ManipulationInstructions extends Association {

	/**
	 * default Constructor
	 *
	 */
	public ManipulationInstructions(){
		super();
		this.setType("ManipulationInstructions");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public ManipulationInstructions(mane.metamodel.Manager manager){
		super(manager);
		this.setType("ManipulationInstructions");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
