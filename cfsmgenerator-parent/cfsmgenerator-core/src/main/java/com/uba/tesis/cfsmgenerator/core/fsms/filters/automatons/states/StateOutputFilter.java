package com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.states;

import java.util.function.Predicate;

import org.objectweb.asm.Opcodes;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.CommonInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.FieldInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.IntInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.LdcInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.MethodInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.TypeInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.VariableInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.methods.MethodBuilder;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.Filter;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.Rule;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.RuleManager;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.SuccessRule;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.OutputState;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class StateOutputFilter extends Filter{
	
	private String socketClass = "java/net/Socket";
	private Integer port;
	private String host;
	// used  when i've a socket field.
	private String varName;
	// used when i've a socket variable.
	private Integer varIndex;

	public StateOutputFilter(MethodBuilder builder) {
		super(builder);
	}
	
	@Override
	protected void doAction(MethodBuilder builder) {
		System.out.println("***** Agrego un OutputState ******");			
		builder.addOutputState(new OutputState(host, port, varName, varIndex));		
	};

	@Override
	public void defineManagers() {
		fieldManager();
		variableManager();
		parameterManager();
	}	

	/**
	 * Maneja la creacion de un socket y asigacion a un field.
	 */
	private void fieldManager() {
		SuccessRule success = new SuccessRule();		
		Rule rule1 = new Rule(associateFieldVariable(), this::setFieldVariable, success);
		Rule rule2 = new Rule(invokeInit(), rule1);
		Rule rule3 = new Rule(definePort(), this::setPort, rule2);
		Rule rule4 = new Rule(defineHost(), this::setHost, rule3);
		Rule rule5 = new Rule(commonInsn(), rule4);
		Rule rule6 = new Rule(createSocket(), rule5);
		this.managers.add(new RuleManager(rule6));
	}
	
	/**
	 * Maneja la creacion de un socket y asigacion a una variable.
	 */
	private void variableManager() {
		SuccessRule success = new SuccessRule();		
		Rule rule1 = new Rule(associateVariable(), this::setVariable, success);
		Rule rule2 = new Rule(invokeInit(), rule1);
		Rule rule3 = new Rule(definePort(), this::setPort, rule2);
		Rule rule4 = new Rule(defineHost(), this::setHost, rule3);
		Rule rule5 = new Rule(commonInsn(), rule4);
		Rule rule6 = new Rule(createSocket(), rule5);
		this.managers.add(new RuleManager(rule6));
	}

	/**
	 * Maneja la creacion de un socket a partir del pasaje por parametro
	 */
	private void parameterManager() {
		SuccessRule success = new SuccessRule();		
		Rule rule1 = new Rule(assignToField(), this::setFieldVariable, success);
		Rule rule2 = new Rule(readVar(), rule1);
		Rule rule3 = new Rule(readVar(), rule2);
		this.managers.add(new RuleManager(rule3));
	}
	
	private Predicate<Instruction> createSocket(){
		return insn-> insn instanceof TypeInstruction && 
					 ((TypeInstruction)insn).getOpCode() == Opcodes.NEW && 
					 ((TypeInstruction)insn).getType().equals(socketClass);
	}

	private Predicate<Instruction> commonInsn(){
		return insn-> insn instanceof CommonInstruction;
	}

	private Predicate<Instruction> defineHost(){
		return  insn-> insn instanceof LdcInstruction;
	}

	private Predicate<Instruction> definePort(){
		return insn -> insn instanceof IntInstruction;
	}

	private Predicate<Instruction> invokeInit(){
		return insn-> insn instanceof MethodInstruction && 
				  	  ((MethodInstruction)insn).getOpCode() == Opcodes.INVOKESPECIAL &&
				  	  ((MethodInstruction)insn).getName().equals("<init>") &&
				  	  ((MethodInstruction)insn).getOwner().equals(socketClass);
	}

	private Predicate<Instruction> associateVariable(){
		return insn-> insn instanceof VariableInstruction && 
					   ((VariableInstruction)insn).getOpCode() == Opcodes.ASTORE;
	}
	
	private Predicate<Instruction> associateFieldVariable(){
		return insn-> insn instanceof FieldInstruction && 
				  	  ((FieldInstruction)insn).getOpCode() == Opcodes.PUTSTATIC &&
				  	  ((FieldInstruction)insn).getDesc().contains(socketClass);
	}
	
	private Predicate<Instruction> readVar(){
		return insn-> insn instanceof VariableInstruction && 
				  	  ((VariableInstruction)insn).getOpCode() == Opcodes.ALOAD;
	}

	private Predicate<Instruction> assignToField(){
		return insn-> insn instanceof FieldInstruction && 
				  	  ((FieldInstruction)insn).getOpCode() == Opcodes.PUTFIELD &&
				  	  ((FieldInstruction)insn).getDesc().contains(socketClass);
	}
	
	private void setPort(Instruction instruction) {
		if (instruction instanceof IntInstruction){
			this.port = ((IntInstruction) instruction).getValue();			
		}		
	}	

	private void setHost(Instruction instruction) {
		if (instruction instanceof LdcInstruction){
			this.host = ((LdcInstruction) instruction).getConstant();			
		}		
	}
	
	private void setVariable(Instruction instruction) {
		if (instruction instanceof VariableInstruction){
			this.varIndex = ((VariableInstruction) instruction).getVar();			
		}		
	}
	
	private void setFieldVariable(Instruction instruction) {
		if (instruction instanceof FieldInstruction){
			this.varName = ((FieldInstruction) instruction).getName();			
		}		
	}

	public Integer getVarIndex() {
		return varIndex;
	}

	public void setVarIndex(Integer varIndex) {
		this.varIndex = varIndex;
	}		
}
