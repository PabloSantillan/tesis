package com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class LdcInstruction extends Instruction {

	private String constant;
	
	public LdcInstruction(int opCode){
		super(opCode);
	}
	
	public LdcInstruction(String constant){
		this.constant = constant;
	}
	
	public String getConstant() {
		return constant;
	}
}
