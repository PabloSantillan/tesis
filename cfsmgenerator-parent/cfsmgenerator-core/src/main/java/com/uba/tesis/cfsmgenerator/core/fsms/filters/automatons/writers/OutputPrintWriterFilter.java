package com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.writers;

import java.util.function.Predicate;

import org.objectweb.asm.Opcodes;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.CommonInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.FieldInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.MethodInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.TypeInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.VariableInstruction;
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
public class OutputPrintWriterFilter extends Filter{
		
	private String name; // representa el nombre del writer que se esta invocando.
	private String socketVarName; // representa el nombre de la variable del socket duenio del writer.
	private Integer socketVarIndex; // representa el indice de la variable del socket duenio del writer.
	private String serverSocket = "java/net/ServerSocket";
	private String socket = "java/net/Socket";
	
	public OutputPrintWriterFilter(MethodBuilder builder) {
		super(builder);
	}
	
	@Override
	protected void doAction(MethodBuilder builder) {
		System.out.println("***** Add Writer (PrintWriter) ******");
		builder.addWriter(socketVarName, name, socketVarIndex);	
	};
	
	@Override
	public void defineManagers() {
		manager1();
		manager2();
		createPrintWriterFromLocalSocketManager();
	}	

	private void manager1() {
		SuccessRule succes = new SuccessRule();		
		Rule rule1 = new Rule(assignToFieldVariable(), this::setPrinterVariable, succes);
		Rule rule2 = new Rule(initPrintWriter(), rule1);
		Rule rule3 = new Rule(commonInsn(), rule2);
		Rule rule4 = new Rule(getOutputStream(), rule3);
		Rule rule5 = new Rule(readSocketOwner(), this::setSocketOwner, rule4);
		Rule rule6 = new Rule(varInsn(), rule5);
		Rule rule7 = new Rule(commonInsn(), rule6);
		Rule rule8 = new Rule(newPrinterWriter(), rule7);
		this.managers.add(new RuleManager(rule8));
	}
	
	private void manager2() {
		SuccessRule succes = new SuccessRule();		
		Rule rule1 = new Rule(assignToFieldVariable(), this::setPrinterVariable, succes);
		Rule rule2 = new Rule(initPrintWriter(), rule1);
		Rule rule3 = new Rule(commonInsn(), rule2);
		Rule rule4 = new Rule(getOutputStream(), rule3);
		Rule rule5 = new Rule(readSocketOwner(), this::setSocketOwner, rule4);
		Rule rule6 = new Rule(commonInsn(), rule5);
		Rule rule7 = new Rule(newPrinterWriter(), rule6);
		this.managers.add(new RuleManager(rule7));
	}

	/**
	 * Crea un PrintWriter de un socket que es una variable local
	 */
	private void createPrintWriterFromLocalSocketManager() {
		SuccessRule succes = new SuccessRule();		
		Rule rule1 = new Rule(assignToVariable(), this::setVariable, succes);
		Rule rule2 = new Rule(initPrintWriter(), rule1);
		Rule rule3 = new Rule(commonInsn(), rule2);
		Rule rule4 = new Rule(getOutputStream(), rule3);
		Rule rule5 = new Rule(varInsn(), this::setSocketVarIndex, rule4);
		Rule rule6 = new Rule(commonInsn(), rule5);
		Rule rule7 = new Rule(newPrinterWriter(), rule6);
		this.managers.add(new RuleManager(rule7));
	}
	
	private Predicate<Instruction> newPrinterWriter(){
		return insn -> insn instanceof TypeInstruction &&
				       ((TypeInstruction)insn).getType().equals("java/io/PrintWriter") &&
				       ((TypeInstruction)insn).getOpCode() == Opcodes.NEW;				
	}
	
	private Predicate<Instruction> commonInsn(){
		return insn -> insn instanceof CommonInstruction;
	}
	
	private Predicate<Instruction> varInsn(){
		return insn -> insn instanceof VariableInstruction;
	}
	
	private Predicate<Instruction> readSocketOwner(){
		return insn -> insn instanceof FieldInstruction &&  
				   (((FieldInstruction)insn).getDesc().contains(socket) || ((FieldInstruction)insn).getDesc().contains(serverSocket));
	}
	
	private Predicate<Instruction> getOutputStream(){
		return insn -> insn instanceof MethodInstruction &&
					   (((MethodInstruction)insn).getOwner().equals(socket) || ((MethodInstruction)insn).getOwner().equals(serverSocket)) &&
					   ((MethodInstruction)insn).getName().equals("getOutputStream") &&
					   ((MethodInstruction)insn).getOpCode() == Opcodes.INVOKEVIRTUAL;
	}
	
	private Predicate<Instruction> initPrintWriter(){
		return insn -> insn instanceof MethodInstruction &&
					   ((MethodInstruction)insn).getOwner().equals("java/io/PrintWriter") &&
					   ((MethodInstruction)insn).getName().equals("<init>") &&
					   ((MethodInstruction)insn).getOpCode() == Opcodes.INVOKESPECIAL;
	}
	
	private Predicate<Instruction> assignToFieldVariable(){
		return insn -> insn instanceof FieldInstruction &&
				       ((FieldInstruction)insn).getDesc().contains("java/io/PrintWriter");
	}	

	private Predicate<Instruction> assignToVariable(){
		return insn -> insn instanceof VariableInstruction &&
				       ((VariableInstruction)insn).getOpCode() == Opcodes.ASTORE;
	}	
	
	private void setPrinterVariable(Instruction instruction) {
		if (instruction instanceof FieldInstruction){
			this.name = ((FieldInstruction) instruction).getName();			
		}		
	}

	private void setVariable(Instruction instruction) {
		if (instruction instanceof VariableInstruction){
			this.name = ((VariableInstruction) instruction).getVar().toString();	
			this.socketVarName = null;
		}		
	}
	
	private void setSocketOwner(Instruction instruction) {
		if (instruction instanceof FieldInstruction){
			this.socketVarName = ((FieldInstruction) instruction).getName();
		}		
	}
	
	private void setSocketVarIndex(Instruction instruction) {
		if (instruction instanceof VariableInstruction){
			this.socketVarIndex = ((VariableInstruction) instruction).getVar();			
		}		
	}	
}
