package com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class VariableInstruction extends Instruction {
	private Integer var;
	
	public VariableInstruction(Integer var, Integer opCode){
		super(opCode);
		this.setVar(var);
	}

	public Integer getVar() {
		return var;
	}

	public void setVar(Integer var) {
		this.var = var;
	}
}
