package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE concept.ftl **/


import mane.metamodel.Concept;

/**
 * Role represents a concrete Model Element (layer M1) of a 
 * twm model.
 * 
 */
public class Role extends Concept {

	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public Role(mane.metamodel.Manager manager){
		super("unknown","Role", manager);
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager
	 */
	public Role(String name, mane.metamodel.Manager manager){
		super("unknown","Role", manager);
		this.setName(name);
	}
}
