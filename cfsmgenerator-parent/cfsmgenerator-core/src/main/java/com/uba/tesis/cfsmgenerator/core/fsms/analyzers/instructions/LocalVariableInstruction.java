package com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class LocalVariableInstruction extends Instruction {
	private String name;
	private String desc;
	private Integer index;
	
	public LocalVariableInstruction(Integer index, String name, String desc){
		this.name = name;
		this.setDesc(desc);
		this.setIndex(index);
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

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
}
