package com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.transitions;

import java.util.function.Predicate;

import org.objectweb.asm.Opcodes;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.FieldInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.LdcInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.MethodInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.VariableInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.methods.MethodBuilder;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.Filter;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.Rule;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.RuleManager;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.SuccessRule;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.transitions.Transition;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class TransitionBufferedReaderReceiveFilter extends Filter{
	
	private String message;
	private Integer varIndex;
	
	public TransitionBufferedReaderReceiveFilter(MethodBuilder builder) {
		super(builder);
	}
	
	@Override
	protected void doAction(MethodBuilder builder) {
		System.out.println("***** Add Receive Transition (BufferedReader) ******");
		
		//create new transition
		Transition transition = new Transition();
		transition.setMessage(message);
		transition.setVarIndex(varIndex); // representa el indice del string del cual hace el equal.
		
		builder.addTransition(transition);	
	};
	
	@Override
	public void defineManagers() {
		//readFromFieldBufferedReaderManager(); TODO:pgs:completar cuando vea como funciona con un field.
		readFromVariableBufferedReaderManager();
	}
	
	private void readFromFieldBufferedReaderManager() {
		//SuccessRule success = new SuccessRule();
		//Rule rule1 = new Rule(invokeReadLine(), success);
		//Rule rule2 = new Rule(readFieldBuffered(), this::setFieldVariable, rule1);
		//this.managers.add(new RuleManager(rule2));
	}

	private void readFromVariableBufferedReaderManager() {
		SuccessRule success = new SuccessRule();
		Rule rule1 = new Rule(visitEqual(), success);
		Rule rule2 = new Rule(readConstantMessage(), this::setMessage, rule1);
		Rule rule3 = new Rule(visitVarInstruction(), this::setVariable, rule2);
		this.managers.add(new RuleManager(rule3));
	}
	
	private Predicate<Instruction> readFieldBuffered(){
		return insn -> insn instanceof FieldInstruction &&
				       ((FieldInstruction)insn).getDesc().contains("java/io/BufferedReader");
	}

	private Predicate<Instruction> visitVarInstruction(){
		return insn -> insn instanceof VariableInstruction &&
				       ((VariableInstruction)insn).getOpCode() == Opcodes.ALOAD;
	}

	private Predicate<Instruction> readConstantMessage(){
		return insn -> insn instanceof LdcInstruction;
	}

	private Predicate<Instruction> visitEqual(){
		return insn -> insn instanceof MethodInstruction &&
				       ((MethodInstruction)insn).getOpCode() == Opcodes.INVOKEVIRTUAL &&
				       ((MethodInstruction)insn).getOwner().equals("java/lang/String") &&
					   ((MethodInstruction)insn).getName().equals("equals");
	}
	
	private void setFieldVariable(Instruction instruction) {
		if (instruction instanceof FieldInstruction){
	//		this.varReaderName = ((FieldInstruction) instruction).getName();			
		}		
	}

	private void setVariable(Instruction instruction) {
		if (instruction instanceof VariableInstruction){
			this.varIndex = ((VariableInstruction) instruction).getVar();			
		}		
	}
	
	private void setMessage(Instruction instruction) {
		if (instruction instanceof LdcInstruction){
			this.message = ((LdcInstruction) instruction).getConstant();			
		}		
	}	
}
