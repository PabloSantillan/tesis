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
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators.StateWriter;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.OutputState;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.OwnerState;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.transitions.Transition;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class TransitionPrintWriterSendFilter extends Filter{
	
	private String toVarWriterName;
	private Integer toVarWriterIndex;
	private String message;
	
	public TransitionPrintWriterSendFilter(MethodBuilder builder) {
		super(builder);
	}
	
	@Override
	protected void doAction(MethodBuilder builder) {
		System.out.println("***** Add Send Transition (PrintWriter) ******");
				
		// from state is an "owner state" because i'll send some message to some socket.
		OwnerState owner = new OwnerState();
		owner.setVarName("serverSocket"); // TODO:pgs: modificar esto cuando tenga mas de 1 socket. Ver como deducirlo. Por ahi se deduce del handler al que pertence.
	
		StateWriter writer = new StateWriter();
		writer.setVarName(toVarWriterName);
		writer.setVarIndex(toVarWriterIndex);
		
		OutputState to = new OutputState(null, null, null, null);
		to.setWriter(writer);
		to.setVirtual(true);
		
		//add a transition.
		Transition transition = new Transition();
		transition.setFrom(owner);
		transition.setTo(to);
		transition.setMessage(message);
		
		builder.addTransition(transition);	
	};
	
	@Override
	public void defineManagers() {
		writeToFieldVariableManager();
		writeToLocalVariableManager();
	}
	
	private void writeToFieldVariableManager() {
		SuccessRule success = new SuccessRule();
		Rule rule1 = new Rule(invokePrintLn(), success);
		Rule rule2 = new Rule(readMessage(), this::setMessage, rule1);
		Rule rule3 = new Rule(readFieldVariable(), this::setFieldVariable, rule2);
		this.managers.add(new RuleManager(rule3));
	}

	private void writeToLocalVariableManager() {
		SuccessRule success = new SuccessRule();
		Rule rule1 = new Rule(invokePrintLn(), success);
		Rule rule2 = new Rule(readMessage(), this::setMessage, rule1);
		Rule rule3 = new Rule(readVariable(), this::setVariable, rule2);
		this.managers.add(new RuleManager(rule3));
	}
	
	private Predicate<Instruction> readFieldVariable(){
		return insn -> insn instanceof FieldInstruction &&
				       ((FieldInstruction)insn).getDesc().contains("java/io/PrintWriter");
	}

	private Predicate<Instruction> readVariable(){
		return insn -> insn instanceof VariableInstruction &&
				       ((VariableInstruction)insn).getOpCode() == Opcodes.ALOAD;
	}
	
	private Predicate<Instruction> readMessage(){
		return insn -> insn instanceof LdcInstruction;
	}

	private Predicate<Instruction> invokePrintLn(){
		return insn -> insn instanceof MethodInstruction &&
				       ((MethodInstruction)insn).getOpCode() == Opcodes.INVOKEVIRTUAL &&
				       ((MethodInstruction)insn).getOwner().equals("java/io/PrintWriter") &&
					   ((MethodInstruction)insn).getName().equals("println");
	}
	
	private void setFieldVariable(Instruction instruction) {
		if (instruction instanceof FieldInstruction){
			this.toVarWriterName = ((FieldInstruction) instruction).getName();	
			this.toVarWriterIndex = null;
		}		
	}

	private void setVariable(Instruction instruction) {
		if (instruction instanceof VariableInstruction){
			this.toVarWriterIndex = ((VariableInstruction) instruction).getVar();
			this.toVarWriterName = null;
		}		
	}
	
	private void setMessage(Instruction instruction) {
		if (instruction instanceof LdcInstruction){
			this.message = ((LdcInstruction) instruction).getConstant();			
		}		
	}
}
