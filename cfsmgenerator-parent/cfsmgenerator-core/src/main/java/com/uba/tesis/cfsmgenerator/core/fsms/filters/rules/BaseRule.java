package com.uba.tesis.cfsmgenerator.core.fsms.filters.rules;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;

public abstract class BaseRule {
	
	protected Predicate<Instruction> condition;
	protected Consumer<Instruction> action;
	
	public abstract Boolean evaluate(Instruction instruction);
	public abstract Boolean isSuccess();	
	public abstract BaseRule getNext();
}
