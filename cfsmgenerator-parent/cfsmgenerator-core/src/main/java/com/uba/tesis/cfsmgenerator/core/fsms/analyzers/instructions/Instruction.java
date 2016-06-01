package com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions;

/**
 * 
 * @author Pablo Santillan
 *
 */
public abstract class Instruction {	
	protected int opCode;
	
	public Instruction(){
		
	}
	
	public Instruction(int opCode){
		this.opCode = opCode;
	}
	
	public int getOpCode(){
		return this.opCode;
	}
}
