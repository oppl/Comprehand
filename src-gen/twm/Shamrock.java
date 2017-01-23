package twm;
/** [20.03.08 13:24] **/
/** GENERATED FROM TEMPLATE concept.ftl **/


import mane.metamodel.Concept;

/**
 * Shamrock represents a concrete Model Element (layer M1) of a 
 * twm model.
 * 
 */
public class Shamrock extends Concept {

	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public Shamrock(mane.metamodel.Manager manager){
		super("unknown","Shamrock", manager);
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager
	 */
	public Shamrock(String name, mane.metamodel.Manager manager){
		super("unknown","Shamrock", manager);
		this.setName(name);
	}
}
