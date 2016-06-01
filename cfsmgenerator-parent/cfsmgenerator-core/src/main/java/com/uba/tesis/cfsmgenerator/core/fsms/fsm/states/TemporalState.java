package com.uba.tesis.cfsmgenerator.core.fsms.fsm.states;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class TemporalState extends State{
			
	@Override
	public String toString(){
		return String.format("temporal");
	}
	
	public String getVarName() {
		if (this.varName != null){
			return this.varName;
		}
		return "temp";
	}	
}
