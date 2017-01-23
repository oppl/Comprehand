package twm;
/** [05.09.08 11:11] **/
/** GENERATED FROM TEMPLATE manager.ftl **/

import mane.metamodel.Concept;
import mane.metamodel.Association;
import mane.metamodel.ValidElementCreator;

import java.util.Set;

/**
 * Manager represents a Manager for twm  
 * models. Model elements and associations can be defined for 
 * twm models	with this Manager. Furthermore
 * models can be mapped to and retrieved from Topic Maps
 *
 */
public class Manager extends mane.metamodel.Manager {
	
	/**
	 * default Constructor
	 *
	 */
	public Manager(){
		super();
		configXML = "src-gen/"+fileSeparator+"twm"+fileSeparator+"xmlSpec"+fileSeparator+"twm"+".xml";
		configXMLName = "twm";
		packageName = "twm";
		
		ValidElementCreator vEC = new ValidElementCreator(this);
		vEC.setValidModelElementsFromXML();
	}
	
	/**
	 * Constructor
	 * - retrieve a Manager (model) from a Topic Map which adheres to metamodel
	 *
	 * @param tmManager a Manager of a Topic Map
	 */
	public Manager(ce.tm4scholion.tm.Manager tmManager){
		super("twm", "1.0", tmManager);
		configXML = "."+fileSeparator+"src-gen/"+fileSeparator+"twm"+fileSeparator+"xmlSpec"+fileSeparator+"twm"+".xml";
		configXMLName = "twm";
		packageName = "twm";
	}
	
	/**
	 * generat a new Process object
	 *
	 * @param name name of Process
	 * @return a new Process object
	 */
	public Process generateProcess(String name){
		Process c = new Process(name, this);
		this.addConcept(c);
		return c;
	}
	
	/**
	 * generat a new Process object
	 *
	 * @return a new Process object
	 */
	public Process generateProcess(){
		Process c = new Process(this);
		this.addConcept(c);
		return c;
	}
	
	/**
	 * get existing  model elements of type Process
	 *
	 * @return a set of Process objects
	 */
	public Set<Concept> getProcessElements(){
		return this.getConcepts().get("Process");
	}
	
	/**
	 * generat a new Role object
	 *
	 * @param name name of Role
	 * @return a new Role object
	 */
	public Role generateRole(String name){
		Role c = new Role(name, this);
		this.addConcept(c);
		return c;
	}
	
	/**
	 * generat a new Role object
	 *
	 * @return a new Role object
	 */
	public Role generateRole(){
		Role c = new Role(this);
		this.addConcept(c);
		return c;
	}
	
	/**
	 * get existing  model elements of type Role
	 *
	 * @return a set of Role objects
	 */
	public Set<Concept> getRoleElements(){
		return this.getConcepts().get("Role");
	}
	
	/**
	 * generat a new Data object
	 *
	 * @param name name of Data
	 * @return a new Data object
	 */
	public Data generateData(String name){
		Data c = new Data(name, this);
		this.addConcept(c);
		return c;
	}
	
	/**
	 * generat a new Data object
	 *
	 * @return a new Data object
	 */
	public Data generateData(){
		Data c = new Data(this);
		this.addConcept(c);
		return c;
	}
	
	/**
	 * get existing  model elements of type Data
	 *
	 * @return a set of Data objects
	 */
	public Set<Concept> getDataElements(){
		return this.getConcepts().get("Data");
	}
	
	/**
	 * generat a new Goal object
	 *
	 * @param name name of Goal
	 * @return a new Goal object
	 */
	public Goal generateGoal(String name){
		Goal c = new Goal(name, this);
		this.addConcept(c);
		return c;
	}
	
	/**
	 * generat a new Goal object
	 *
	 * @return a new Goal object
	 */
	public Goal generateGoal(){
		Goal c = new Goal(this);
		this.addConcept(c);
		return c;
	}
	
	/**
	 * get existing  model elements of type Goal
	 *
	 * @return a set of Goal objects
	 */
	public Set<Concept> getGoalElements(){
		return this.getConcepts().get("Goal");
	}
	
	/**
	 * get existing Associations of type
	 * Strictlybefore 
	 *
	 * @return a set of Strictlybefore Associations
	 */
	public Set<Association> getStrictlybefore_Assocs(){
		return this.getAssociations().get("Strictlybefore");
	}
	
	/**
	 * get existing Associations of type
	 * Creates 
	 *
	 * @return a set of Creates Associations
	 */
	public Set<Association> getCreates_Assocs(){
		return this.getAssociations().get("Creates");
	}
	
	/**
	 * get existing Associations of type
	 * IsInputFor 
	 *
	 * @return a set of IsInputFor Associations
	 */
	public Set<Association> getIsInputFor_Assocs(){
		return this.getAssociations().get("IsInputFor");
	}
	
	/**
	 * get existing Associations of type
	 * Determines 
	 *
	 * @return a set of Determines Associations
	 */
	public Set<Association> getDetermines_Assocs(){
		return this.getAssociations().get("Determines");
	}
	
	/**
	 * get existing Associations of type
	 * Interdependent 
	 *
	 * @return a set of Interdependent Associations
	 */
	public Set<Association> getInterdependent_Assocs(){
		return this.getAssociations().get("Interdependent");
	}
	
	/**
	 * get existing Associations of type
	 * Has 
	 *
	 * @return a set of Has Associations
	 */
	public Set<Association> getHas_Assocs(){
		return this.getAssociations().get("Has");
	}
	
	/**
	 * get existing Associations of type
	 * Responsible 
	 *
	 * @return a set of Responsible Associations
	 */
	public Set<Association> getResponsible_Assocs(){
		return this.getAssociations().get("Responsible");
	}
	
	/**
	 * get existing Associations of type
	 * InvolesConsults 
	 *
	 * @return a set of InvolesConsults Associations
	 */
	public Set<Association> getInvolesConsults_Assocs(){
		return this.getAssociations().get("InvolesConsults");
	}
	
	/**
	 * get existing Associations of type
	 * ContainedIn 
	 *
	 * @return a set of ContainedIn Associations
	 */
	public Set<Association> getContainedIn_Assocs(){
		return this.getAssociations().get("ContainedIn");
	}
	
	/**
	 * get existing Associations of type
	 * AssignmentOfRole 
	 *
	 * @return a set of AssignmentOfRole Associations
	 */
	public Set<Association> getAssignmentOfRole_Assocs(){
		return this.getAssociations().get("AssignmentOfRole");
	}
	
	/**
	 * get existing Associations of type
	 * DescriptionOfRole 
	 *
	 * @return a set of DescriptionOfRole Associations
	 */
	public Set<Association> getDescriptionOfRole_Assocs(){
		return this.getAssociations().get("DescriptionOfRole");
	}
	
	/**
	 * get existing Associations of type
	 * IntensionsOfRole 
	 *
	 * @return a set of IntensionsOfRole Associations
	 */
	public Set<Association> getIntensionsOfRole_Assocs(){
		return this.getAssociations().get("IntensionsOfRole");
	}
	
	/**
	 * get existing Associations of type
	 * Supporter 
	 *
	 * @return a set of Supporter Associations
	 */
	public Set<Association> getSupporter_Assocs(){
		return this.getAssociations().get("Supporter");
	}
	
	/**
	 * get existing Associations of type
	 * Detailing 
	 *
	 * @return a set of Detailing Associations
	 */
	public Set<Association> getDetailing_Assocs(){
		return this.getAssociations().get("Detailing");
	}
	
	/**
	 * get existing Associations of type
	 * ProcessInstructions 
	 *
	 * @return a set of ProcessInstructions Associations
	 */
	public Set<Association> getProcessInstructions_Assocs(){
		return this.getAssociations().get("ProcessInstructions");
	}
	
	/**
	 * get existing Associations of type
	 * GoalOfProcess 
	 *
	 * @return a set of GoalOfProcess Associations
	 */
	public Set<Association> getGoalOfProcess_Assocs(){
		return this.getAssociations().get("GoalOfProcess");
	}
	
	/**
	 * get existing Associations of type
	 * OwnerResponsible 
	 *
	 * @return a set of OwnerResponsible Associations
	 */
	public Set<Association> getOwnerResponsible_Assocs(){
		return this.getAssociations().get("OwnerResponsible");
	}
	
	/**
	 * get existing Associations of type
	 * LifecycleOfData 
	 *
	 * @return a set of LifecycleOfData Associations
	 */
	public Set<Association> getLifecycleOfData_Assocs(){
		return this.getAssociations().get("LifecycleOfData");
	}
	
	/**
	 * get existing Associations of type
	 * ManipulationInstructions 
	 *
	 * @return a set of ManipulationInstructions Associations
	 */
	public Set<Association> getManipulationInstructions_Assocs(){
		return this.getAssociations().get("ManipulationInstructions");
	}
	
	
}
