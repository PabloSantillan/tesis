package com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class FieldInstruction extends Instruction {
	private String name;
	private String owner;
	private String desc;

	public FieldInstruction(int opcode){
		super(opcode);
	}
	
	public FieldInstruction(int opcode, String name, String owner, String desc){
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

}
