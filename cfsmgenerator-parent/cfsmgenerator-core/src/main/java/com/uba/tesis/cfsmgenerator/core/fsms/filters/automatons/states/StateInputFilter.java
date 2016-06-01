package com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.states;

import java.util.function.Predicate;

import org.objectweb.asm.Opcodes;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.CommonInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.IntInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.MethodInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.TypeInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.VariableInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.methods.MethodBuilder;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.Filter;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.Rule;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.RuleManager;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.SuccessRule;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.InputState;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class StateInputFilter extends Filter {
	
	private String serverSocketClass = "java/net/ServerSocket";
	private Integer port;
	private Integer varIndex;
	
	public StateInputFilter(MethodBuilder builder) {
		super(builder);
	}
	
	@Override
	protected void doAction(MethodBuilder builder) {
		System.out.println("***** Agrego un InputState ******");
		InputState state = new InputState();
		state.setPort(port);
		state.setVarIndex(varIndex);
		builder.addInputState(state);
	};

	@Override
	public void defineManagers() {
		SuccessRule success = new SuccessRule();		
		Rule rule0 = new Rule(assignVariable(), this::setVariable, success);
		Rule rule1 = new Rule(invokeInit(), rule0);
		Rule rule2 = new Rule(definePort(), this::setPort, rule1);
		Rule rule3 = new Rule(commonInsn(), rule2);
		Rule rule4 = new Rule(createServerSocket(), rule3);
		this.managers.add(new RuleManager(rule4));
	}	
	
	private Predicate<Instruction> createServerSocket(){
		return insn-> insn instanceof TypeInstruction && 
		        	  ((TypeInstruction)insn).getOpCode() == Opcodes.NEW && 
		        	  ((TypeInstruction)insn).getType().equals(serverSocketClass); 
	}
	
	private Predicate<Instruction> commonInsn(){
		return insn-> insn instanceof CommonInstruction;
	}

	private Predicate<Instruction> definePort(){
		return insn-> insn instanceof IntInstruction && ((IntInstruction)insn).getOpCode() == Opcodes.SIPUSH;
	}

	private Predicate<Instruction> invokeInit(){
		return insn-> insn instanceof MethodInstruction && 
					  ((MethodInstruction)insn).getOpCode() == Opcodes.INVOKESPECIAL &&
					  ((MethodInstruction)insn).getName().equals("<init>") &&
					  ((MethodInstruction)insn).getOwner().equals(serverSocketClass);		
	}

	private Predicate<Instruction> assignVariable(){
		return insn-> insn instanceof VariableInstruction && 
					  ((VariableInstruction)insn).getOpCode() == Opcodes.ASTORE;		
	}
	
	private void setPort(Instruction instruction) {
		if (instruction instanceof IntInstruction){
			this.port = ((IntInstruction) instruction).getValue();			
		}		
	}
	
	private void setVariable(Instruction instruction) {
		if (instruction instanceof VariableInstruction){
			this.varIndex = ((VariableInstruction) instruction).getVar();			
		}		
	}		
}
