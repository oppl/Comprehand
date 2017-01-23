package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * Strictlybefore represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class Strictlybefore extends Association {

	/**
	 * default Constructor
	 *
	 */
	public Strictlybefore(){
		super();
		this.setType("Strictlybefore");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public Strictlybefore(mane.metamodel.Manager manager){
		super(manager);
		this.setType("Strictlybefore");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
