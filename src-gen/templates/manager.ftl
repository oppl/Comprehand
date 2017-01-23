<@pp.dropOutputFile />
<@pp.changeOutputFile name="Manager.java" />
package ${doc.model.@name?lower_case};
/** [${date}] **/
/** GENERATED FROM TEMPLATE manager.ftl **/

import mane.metamodel.Concept;
import mane.metamodel.Association;
import mane.metamodel.ValidElementCreator;

import java.util.Set;

/**
 * Manager represents a Manager for ${doc.model.@name?lower_case}  
 * models. Model elements and associations can be defined for 
 * ${doc.model.@name?lower_case} models	with this Manager. Furthermore
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
		configXML = "${genDir}"+fileSeparator+"${doc.model.@name?lower_case}"+fileSeparator+"xmlSpec"+fileSeparator+"${xmlFileName}"+".xml";
		configXMLName = "${xmlFileName}";
		packageName = "${doc.model.@name?lower_case}";
		
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
		super("${doc.model.@name?lower_case}", "${doc.model.@version}", tmManager);
		configXML = "."+fileSeparator+"${genDir}"+fileSeparator+"${doc.model.@name?lower_case}"+fileSeparator+"xmlSpec"+fileSeparator+"${xmlFileName}"+".xml";
		configXMLName = "${xmlFileName}";
		packageName = "${doc.model.@name?lower_case}";
	}
	
	<#list doc.model.concept as concept>
	/**
	 * generat a new ${concept.@name} object
	 *
	 * @param name name of ${concept.@name}
	 * @return a new ${concept.@name} object
	 */
	public ${concept.@name} generate${concept.@name}(String name){
		${concept.@name} c = new ${concept.@name}(name, this);
		this.addConcept(c);
		return c;
	}
	
	/**
	 * generat a new ${concept.@name} object
	 *
	 * @return a new ${concept.@name} object
	 */
	public ${concept.@name} generate${concept.@name}(){
		${concept.@name} c = new ${concept.@name}(this);
		this.addConcept(c);
		return c;
	}
	
	/**
	 * get existing  model elements of type ${concept.@name}
	 *
	 * @return a set of ${concept.@name} objects
	 */
	public Set<Concept> get${concept.@name}Elements(){
		return this.getConcepts().get("${concept.@name}");
	}
	
	</#list>
	<#list doc.model.association as assoc>
	/**
	 * get existing Associations of type
	 * ${assoc.@name} 
	 *
	 * @return a set of ${assoc.@name} Associations
	 */
	public Set<Association> get${assoc.@name}_Assocs(){
		return this.getAssociations().get("${assoc.@name}");
	}
	
	</#list>
	
}
