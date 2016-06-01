package com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.flow;

import java.util.function.Predicate;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.LabelInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.LineNumberInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.methods.MethodBuilder;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.Filter;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.Rule;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.RuleManager;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.SuccessRule;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class BlockFilter extends Filter{

	private String label;
	private Integer insnNumber;

	public BlockFilter(MethodBuilder builder) {
		super(builder);
	}
	
	@Override
	protected void doAction(MethodBuilder builder) {
		System.out.println("***** Agrego un Block ******");			
		builder.addBlock(label, insnNumber);
	};
	
	@Override
	public void defineManagers() {
		createLabelManagerWithLineNumber();
		createLabelManagerWithoutLineNumber();
	}
	
	private void createLabelManagerWithLineNumber() {
		SuccessRule success = new SuccessRule();		
		Rule rule1 = new Rule(readLineNumber(), this::setLine, success);
		Rule rule2 = new Rule(readLabel(), this::setLabel, rule1);
		this.managers.add(new RuleManager(rule2));
	}
	
	private void createLabelManagerWithoutLineNumber() {
		SuccessRule success = new SuccessRule();		
		Rule rule1 = new Rule(notLineNumber(), success);
		Rule rule2 = new Rule(readLabel(), this::setLabel, rule1);
		this.managers.add(new RuleManager(rule2));
	}
	

	private Predicate<Instruction> readLabel(){
		return insn-> insn instanceof LabelInstruction;
	}

	private Predicate<Instruction> readLineNumber(){
		return insn-> insn instanceof LineNumberInstruction;
	}

	private Predicate<Instruction> notLineNumber(){
		return insn-> !(insn instanceof LineNumberInstruction);
	}
	
	private void setLabel(Instruction instruction) {
		if (instruction instanceof LabelInstruction){
			this.label = ((LabelInstruction) instruction).getLabel();			
		}		
	}
	
	private void setLine(Instruction instruction) {
		if (instruction instanceof LineNumberInstruction){
			this.insnNumber = ((LineNumberInstruction) instruction).getNumber();			
		}		
	}		
}
