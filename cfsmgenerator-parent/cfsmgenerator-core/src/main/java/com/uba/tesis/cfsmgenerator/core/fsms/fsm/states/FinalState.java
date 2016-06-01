package com.uba.tesis.cfsmgenerator.core.fsms.fsm.states;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class FinalState extends State{
			
	@Override
	public String toString(){
		return String.format("final");
	}
	
	public String getVarName(){
		return "final";
	}
}
