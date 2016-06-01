package com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class LabelInstruction extends Instruction {

	private String label;
	
	public LabelInstruction(String label){
		this.label = label;
	}
	
	public String getLabel(){
		return this.label;
	}
}
