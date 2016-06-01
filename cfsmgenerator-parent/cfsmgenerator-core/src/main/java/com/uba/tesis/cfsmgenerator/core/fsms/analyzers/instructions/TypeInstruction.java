package com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class TypeInstruction extends Instruction {
	private String type;
	
	public TypeInstruction(int opcode){
		super(opcode);
	}
	
	public TypeInstruction(int opcode, String type){
		super(opcode);
		this.type = type;
	}

	public String getType(){
		return this.type;
	}
}
