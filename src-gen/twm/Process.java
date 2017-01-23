package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE concept.ftl **/


import mane.metamodel.Concept;

/**
 * Process represents a concrete Model Element (layer M1) of a 
 * twm model.
 * 
 */
public class Process extends Concept {

	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public Process(mane.metamodel.Manager manager){
		super("unknown","Process", manager);
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager
	 */
	public Process(String name, mane.metamodel.Manager manager){
		super("unknown","Process", manager);
		this.setName(name);
	}
}
