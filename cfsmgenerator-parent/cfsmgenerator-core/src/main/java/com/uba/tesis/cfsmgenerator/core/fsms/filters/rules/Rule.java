package com.uba.tesis.cfsmgenerator.core.fsms.filters.rules;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;

public class Rule extends BaseRule {
	
	private BaseRule next;

	public Rule(Predicate<Instruction> condition, Consumer<Instruction> action, BaseRule next){
		this.next = next;
		this.condition = condition;
		this.action = action;		
	}

	public Rule(Predicate<Instruction> condition, BaseRule next){
		this(condition, i -> {}, next);
	}
	
	@Override
	public Boolean evaluate(Instruction instruction){
		Boolean ok = condition.test(instruction);
		if (ok){
			action.accept(instruction);
		}		
		return ok;
	}
	
	@Override
	public Boolean isSuccess(){
		return false;
	}	
	
	public BaseRule getNext() {
		return next;
	}		
}
