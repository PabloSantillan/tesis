package com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class LineNumberInstruction extends Instruction {

	private Integer number;
	
	public LineNumberInstruction(Integer number){
		this.number = number;
	}
	
	public Integer getNumber(){
		return this.number;
	}
}
