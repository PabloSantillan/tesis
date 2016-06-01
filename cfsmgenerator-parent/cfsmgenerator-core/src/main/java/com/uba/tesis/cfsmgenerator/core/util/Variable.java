package com.uba.tesis.cfsmgenerator.core.util;

public class Variable {
	private String name;
	private Integer index;
	private LocalVariableType type;
	
	public Variable(String name, Integer index, LocalVariableType type){
		this.name = name;
		this.index = index;
		this.type = type;
	}
	
	public static Variable from(String name, Integer index, LocalVariableType type){
		return new Variable(name, index, type);
	} 
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public LocalVariableType getType() {
		return type;
	}
	public void setType(LocalVariableType type) {
		this.type = type;
	}
}
