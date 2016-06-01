package com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class AnnotationInstruction extends Instruction {

	private String name;
	private String desc;
	
	public AnnotationInstruction(String name, String desc){
		this.name = name;
		this.desc = desc;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getDesc(){
		return this.desc;
	}
}
