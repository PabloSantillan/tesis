package com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class IntInstruction extends Instruction {

	private int operand;
	
	public IntInstruction(int opCode){
		super(opCode);
	}
	
	public IntInstruction(int opCode, int operand){
		super(opCode);
		this.operand = operand;
	}
	
	public Integer getValue() {
		return operand;
	}
}
