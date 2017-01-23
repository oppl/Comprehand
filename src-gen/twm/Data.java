package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE concept.ftl **/


import mane.metamodel.Concept;

/**
 * Data represents a concrete Model Element (layer M1) of a 
 * twm model.
 * 
 */
public class Data extends Concept {

	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public Data(mane.metamodel.Manager manager){
		super("unknown","Data", manager);
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager
	 */
	public Data(String name, mane.metamodel.Manager manager){
		super("unknown","Data", manager);
		this.setName(name);
	}
}
