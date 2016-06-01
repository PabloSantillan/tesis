package com.uba.tesis.cfsmgenerator.core.fsms.filters.rules;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;

public class SuccessRule extends BaseRule {

	@Override
	public Boolean evaluate(Instruction instruction) {
		return true;
	}

	@Override
	public Boolean isSuccess() {
		return true;
	}
	
	public BaseRule getNext() {
		return this;
	}	
}
