package com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class JumpInstruction extends Instruction {

	private String label;
	
	public JumpInstruction(Integer opcode, String label){
		this.label = label;
		this.opCode = opcode;
	}
	
	public String getLabel(){
		return this.label;
	}	
}
