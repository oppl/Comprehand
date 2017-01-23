<#-- Kommentar: im Template fehlt momentan noch das seztend er Attribute author, owner -->
<@pp.dropOutputFile />
<#list doc.model.concept as concept>
<@pp.changeOutputFile name=concept.@name+".java" />
package ${doc.model.@name?lower_case};
/** [${date}] **/
/** GENERATED FROM TEMPLATE concept.ftl **/


import mane.metamodel.Concept;

/**
 * ${concept.@name} represents a concrete Model Element (layer M1) of a 
 * ${doc.model.@name?lower_case} model.
 * 
 */
public class ${concept.@name} extends Concept {

	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public ${concept.@name}(mane.metamodel.Manager manager){
		super("unknown","${concept.@name}", manager);
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager
	 */
	public ${concept.@name}(String name, mane.metamodel.Manager manager){
		super("unknown","${concept.@name}", manager);
		this.setName(name);
	}
}
</#list>