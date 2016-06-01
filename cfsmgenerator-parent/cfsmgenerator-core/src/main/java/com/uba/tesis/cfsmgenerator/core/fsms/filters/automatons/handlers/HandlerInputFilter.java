package com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.handlers;

import java.util.function.Predicate;

import org.objectweb.asm.Opcodes;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.MethodInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.VariableInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.methods.MethodBuilder;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.Filter;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.Rule;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.RuleManager;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.SuccessRule;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators.HandlerInputState;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class HandlerInputFilter extends Filter{

	private String owner;
	private Integer varIndex;

	public HandlerInputFilter(MethodBuilder builder) {
		super(builder);
	}
	
	@Override
	protected void doAction(MethodBuilder builder) {
		System.out.println("***** Agrego un Handler input ******");
		HandlerInputState handler = new HandlerInputState();
		handler.setOwner(owner);
		handler.setVarIndex(varIndex);
		builder.addHandletInput(handler);	
	};
	
	@Override
	public void defineManagers() {
		createThreadManager();
	}

	private void createThreadManager() {
		SuccessRule succes = new SuccessRule();		
		Rule rule1 = new Rule(startThread(), this::setOwner, succes);
		Rule rule2 = new Rule(initThread(), rule1);
		Rule rule3 = new Rule(acceptServerSocket(), rule2);
		Rule rule4 = new Rule(readVariable(), this::setVariable, rule3);
		this.managers.add(new RuleManager(rule4));
	}

	private Predicate<Instruction> readVariable() {
		return insn -> insn instanceof VariableInstruction &&
			       ((VariableInstruction)insn).getOpCode() == Opcodes.ALOAD;
	}
	
	private Predicate<Instruction> startThread() {
		return insn -> insn instanceof MethodInstruction &&
			       ((MethodInstruction)insn).getOpCode() == Opcodes.INVOKEVIRTUAL &&
			       ((MethodInstruction)insn).getName().equals("start");
	}

	private Predicate<Instruction> initThread() {
		return insn -> insn instanceof MethodInstruction &&
			       ((MethodInstruction)insn).getOpCode() == Opcodes.INVOKESPECIAL &&
				   ((MethodInstruction)insn).getDesc().contains("java/net/Socket") &&
			       ((MethodInstruction)insn).getName().equals("<init>");
	}

	private Predicate<Instruction> acceptServerSocket() {
		return insn -> insn instanceof MethodInstruction &&
			       ((MethodInstruction)insn).getOpCode() == Opcodes.INVOKEVIRTUAL &&
				   ((MethodInstruction)insn).getOwner().equals("java/net/ServerSocket") &&
				   ((MethodInstruction)insn).getDesc().contains("java/net/Socket") &&
			       ((MethodInstruction)insn).getName().equals("accept");
	}

	private void setOwner(Instruction instruction) {
		if (instruction instanceof MethodInstruction){
			this.owner = ((MethodInstruction)instruction).getOwner();
		}		
	}

	private void setVariable(Instruction instruction) {
		if (instruction instanceof VariableInstruction){
			this.setVarIndex(((VariableInstruction)instruction).getVar());
		}		
	}

	public Integer getVarIndex() {
		return varIndex;
	}

	public void setVarIndex(Integer varIndex) {
		this.varIndex = varIndex;
	}	
}
