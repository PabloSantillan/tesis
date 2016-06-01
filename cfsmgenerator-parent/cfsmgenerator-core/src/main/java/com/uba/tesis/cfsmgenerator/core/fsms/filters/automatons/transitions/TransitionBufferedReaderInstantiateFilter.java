package com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.transitions;

import java.util.function.Predicate;

import org.objectweb.asm.Opcodes;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.FieldInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.MethodInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.VariableInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.methods.MethodBuilder;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.Filter;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.Rule;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.RuleManager;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.SuccessRule;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators.InstanceReader;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.transitions.LambdaTransition;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.transitions.Transition;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class TransitionBufferedReaderInstantiateFilter extends Filter{
	
	private String varReaderName;
	private Integer varIndexStored;
	
	public TransitionBufferedReaderInstantiateFilter(MethodBuilder builder) {
		super(builder);
	}
	
	@Override
	protected void doAction(MethodBuilder builder) {
		System.out.println("***** Add Read Transition (BufferedReader) ******");
		
		InstanceReader instance = new InstanceReader();
		instance.setVarReaderName(varReaderName);
		instance.setVarIndexStored(varIndexStored); // representa el string donde se guardo el mensaje leido.
				
		builder.addInstanceReader(instance);	
		
		//create new transition
		Transition transition = new LambdaTransition(); // es una transicion lambda. Solo se usa para identificar que aca se hizo un readLine();
		builder.addTransition(transition);
	};
	
	@Override
	public void defineManagers() {
		readFromFieldBufferedReaderManager();
		readFromVariableBufferedReaderManager();
	}
	
	private void readFromFieldBufferedReaderManager() {
		SuccessRule success = new SuccessRule();
		Rule rule0 = new Rule(storeInVar(), this::setVarIndexStored, success);
		Rule rule1 = new Rule(invokeReadLine(), rule0);
		Rule rule2 = new Rule(readFieldBuffered(), this::setFieldVariable, rule1);
		this.managers.add(new RuleManager(rule2));
	}

	private void readFromVariableBufferedReaderManager() {
		SuccessRule success = new SuccessRule();
		Rule rule0 = new Rule(storeInVar(), this::setVarIndexStored, success);
		Rule rule1 = new Rule(invokeReadLine(), rule0);
		Rule rule2 = new Rule(readVariableBuffered(), this::setVariable, rule1);
		this.managers.add(new RuleManager(rule2));
	}
	
	private Predicate<Instruction> storeInVar() {
		return insn -> insn instanceof VariableInstruction &&
			       ((VariableInstruction)insn).getOpCode() == Opcodes.ASTORE;
	}
	
	private Predicate<Instruction> readFieldBuffered(){
		return insn -> insn instanceof FieldInstruction &&
				       ((FieldInstruction)insn).getDesc().contains("java/io/BufferedReader");
	}

	private Predicate<Instruction> readVariableBuffered(){
		return insn -> insn instanceof VariableInstruction &&
				       ((VariableInstruction)insn).getOpCode() == Opcodes.ALOAD;
	}
	
	private Predicate<Instruction> invokeReadLine(){
		return insn -> insn instanceof MethodInstruction &&
				       ((MethodInstruction)insn).getOpCode() == Opcodes.INVOKEVIRTUAL &&
				       ((MethodInstruction)insn).getOwner().equals("java/io/BufferedReader") &&
					   ((MethodInstruction)insn).getName().equals("readLine");
	}
	
	private void setFieldVariable(Instruction instruction) {
		if (instruction instanceof FieldInstruction){
			this.varReaderName = ((FieldInstruction) instruction).getName();			
		}		
	}

	private void setVariable(Instruction instruction) {
		if (instruction instanceof VariableInstruction){
			this.varReaderName = ((VariableInstruction) instruction).getVar().toString();			
		}		
	}
	
	private void setVarIndexStored(Instruction instruction) {
		if (instruction instanceof VariableInstruction){
			this.varIndexStored = ((VariableInstruction) instruction).getVar();			
		}		
	}
	
}
