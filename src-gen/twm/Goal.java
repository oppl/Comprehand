package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE concept.ftl **/


import mane.metamodel.Concept;

/**
 * Goal represents a concrete Model Element (layer M1) of a 
 * twm model.
 * 
 */
public class Goal extends Concept {

	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public Goal(mane.metamodel.Manager manager){
		super("unknown","Goal", manager);
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager
	 */
	public Goal(String name, mane.metamodel.Manager manager){
		super("unknown","Goal", manager);
		this.setName(name);
	}
}
