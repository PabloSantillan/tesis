package com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class AnnotationValueInstruction extends Instruction {

	private String name;
	private Object value;
	
	public AnnotationValueInstruction(String name, Object value){
		this.name = name;
		this.value = value;
	}
	
	public String getName(){
		return this.name;
	}

	public Object getValue(){
		return this.value;
	}
}
