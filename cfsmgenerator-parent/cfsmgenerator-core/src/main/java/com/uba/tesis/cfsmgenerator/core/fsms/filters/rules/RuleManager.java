package com.uba.tesis.cfsmgenerator.core.fsms.filters.rules;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;

public class RuleManager {
	
	private BaseRule base;
	private BaseRule current;
	
	public RuleManager(Rule rule){
		this.base = rule;
		this.current = rule;
	}
	
	public void evaluate(Instruction instruction){
		boolean ok = this.current.evaluate(instruction);
		if (ok){
			this.current = this.current.getNext(); 
		} else {
			this.current = base;
		}
	}
	
	public Boolean isSuccess(){
		Boolean success = this.current.isSuccess();		
		if (success){
			this.current = base;
		}
		
		return success;
	}
}
