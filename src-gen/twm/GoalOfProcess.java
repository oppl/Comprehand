package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * GoalOfProcess represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class GoalOfProcess extends Association {

	/**
	 * default Constructor
	 *
	 */
	public GoalOfProcess(){
		super();
		this.setType("GoalOfProcess");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public GoalOfProcess(mane.metamodel.Manager manager){
		super(manager);
		this.setType("GoalOfProcess");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
