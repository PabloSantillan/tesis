package com.uba.tesis.cfsmgenerator.core.fsms.fsm.states;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class OwnerState extends State{
				
	@Override
	public String toString(){	
		String varName = this.varName == null? "null" : this.varName;
		String varIndex = this.varIndex == null? "null" : this.varIndex.toString();
		String socketName = this.socketName == null? "null" : this.socketName;
		
		return String.format("Owner. varName: %s. varIndex: %s. socketName: %s", varName, varIndex, socketName);
	}
	
}
