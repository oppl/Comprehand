package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * InvolesConsults represents a concrete Association (layer M1) 
 * between defined model element of a twm model.
 * 
 */
public class InvolesConsults extends Association {

	/**
	 * default Constructor
	 *
	 */
	public InvolesConsults(){
		super();
		this.setType("InvolesConsults");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public InvolesConsults(mane.metamodel.Manager manager){
		super(manager);
		this.setType("InvolesConsults");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
