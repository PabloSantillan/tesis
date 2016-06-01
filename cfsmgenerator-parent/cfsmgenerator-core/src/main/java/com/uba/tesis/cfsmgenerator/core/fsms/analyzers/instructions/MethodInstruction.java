package com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class MethodInstruction extends Instruction {
	private String name;
	private String owner;
	private String desc;

	public MethodInstruction(int opcode){
		super(opcode);
	}
	
	public MethodInstruction(int opcode, String name, String owner, String desc){
		super(opcode);
		this.name = name;
		this.owner = owner;
		this.desc = desc;
	}

	public String getOwner() {
		return owner;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
