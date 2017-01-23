package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * ProcessInstructions represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class ProcessInstructions extends Association {

	/**
	 * default Constructor
	 *
	 */
	public ProcessInstructions(){
		super();
		this.setType("ProcessInstructions");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public ProcessInstructions(mane.metamodel.Manager manager){
		super(manager);
		this.setType("ProcessInstructions");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
