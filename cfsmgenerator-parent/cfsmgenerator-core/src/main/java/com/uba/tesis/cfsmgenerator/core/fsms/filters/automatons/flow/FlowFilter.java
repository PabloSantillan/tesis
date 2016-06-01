package com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.flow;

import java.util.function.Predicate;

import org.objectweb.asm.Opcodes;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.CommonInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.JumpInstruction;
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
public class FlowFilter extends Filter{

	private String label;
	private Integer opcode;

	public FlowFilter(MethodBuilder builder) {
		super(builder);
	}
	
	@Override
	protected void doAction(MethodBuilder builder) {
		System.out.println("***** Agrego un Flow ******");			
		builder.addFlow(label, opcode);
	};
	
	@Override
	public void defineManagers() {
		createJumpFlowManager();
		createReturnFlowManager();
	}
	
	private void createJumpFlowManager() {
		SuccessRule success = new SuccessRule();		
		Rule rule1 = new Rule(readJump(), this::setProperties, success);
		this.managers.add(new RuleManager(rule1));
	}	

	private void createReturnFlowManager() {
		SuccessRule success = new SuccessRule();		
		Rule rule1 = new Rule(readReturn(), this::setReturnProperties, success);
		this.managers.add(new RuleManager(rule1));
	}	
	
	private Predicate<Instruction> readJump(){
		return insn-> insn instanceof JumpInstruction;
	}

	private Predicate<Instruction> readReturn(){
		return insn-> insn instanceof CommonInstruction &&
				      ((CommonInstruction) insn).getOpCode() == Opcodes.RETURN;
	}
	
	private void setProperties(Instruction instruction) {
		if (instruction instanceof JumpInstruction){
			this.label = ((JumpInstruction) instruction).getLabel();			
			this.opcode = ((JumpInstruction) instruction).getOpCode();			
		}		
	}
	
	private void setReturnProperties(Instruction instruction) {
		if (instruction instanceof CommonInstruction){
			this.label = "";			
			this.opcode = ((CommonInstruction) instruction).getOpCode();			
		}		
	}	
	
}
