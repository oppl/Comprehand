package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * OwnerResponsible represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class OwnerResponsible extends Association {

	/**
	 * default Constructor
	 *
	 */
	public OwnerResponsible(){
		super();
		this.setType("OwnerResponsible");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public OwnerResponsible(mane.metamodel.Manager manager){
		super(manager);
		this.setType("OwnerResponsible");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
