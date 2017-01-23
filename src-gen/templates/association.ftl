<@pp.dropOutputFile />
<#list doc.model.association as assoc>
<@pp.changeOutputFile name=assoc.@name+".java" />
package ${doc.model.@name?lower_case};
/** [${date}] **/
/** GENERATED FROM TEMPLATE association.ftl **/

import mane.metamodel.Association;

/**
 * ${assoc.@name} represents a concrete Association (layer M1) 
 * between defined model element of a ${doc.model.@name?lower_case} model.
 * 
 */
public class ${assoc.@name} extends Association {

	/**
	 * default Constructor
	 *
	 */
	public ${assoc.@name}(){
		super();
		this.setType("${assoc.@name}");
	}
	
	/**
	 * Constructor
	 *
	 * @param manager model Manager 
	 */
	public ${assoc.@name}(mane.metamodel.Manager manager){
		super(manager);
		this.setType("${assoc.@name}");
		
		Association vA = this.mmManager.getValidAssociation(this.getType());
		this.setValidRootRoles(vA.getValidRootRoles());
		this.setValidCombinations(vA.getValidCombinations());
	}
}
</#list>