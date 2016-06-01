package com.uba.tesis.cfsmgenerator.core.fsms.fsm.states;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class ReferenceState extends State {
	private String methodReferenced;
	private String owner;
	
	public ReferenceState(String name, String owner){
		this.setMethodReferenced(name);
		this.setOwner(owner);
	}

	@Override
	public String toString(){			
		return String.format("reference. methodName: %s. owner: %s. ", methodReferenced, owner);
	}
	
	public String getVarName() {
		if (this.varName != null){
			return this.varName;
		}
		return "reference";
	}	
	/// getters and setters

	public String getMethodReferenced() {
		return methodReferenced;
	}

	public void setMethodReferenced(String methodReferenced) {
		this.methodReferenced = methodReferenced;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}	
}
