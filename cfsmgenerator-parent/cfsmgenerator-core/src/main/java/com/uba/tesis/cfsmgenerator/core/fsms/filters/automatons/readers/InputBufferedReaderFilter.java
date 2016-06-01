package com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.readers;

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
public class InputBufferedReaderFilter extends Filter{
		
	private String name;
	private String socketVarName;
	private Integer socketVarIndex;
	private String serverSocket = "java/net/ServerSocket";
	private String socket = "java/net/Socket";
	
	public InputBufferedReaderFilter(MethodBuilder builder) {
		super(builder);
	}
	
	@Override
	protected void doAction(MethodBuilder builder) {
		System.out.println("***** Add Reader (BufferedReader) ******");
		builder.addReader(socketVarName, name, socketVarIndex);	
	};
	
	@Override
	public void defineManagers() {
		manager1();
		manager2();
		createBufferedReaderFromLocalSocketManager();
	}	
	
	private void manager2() {
		SuccessRule succes = new SuccessRule();		
		Rule rule1 = new Rule(assignFieldVariable(), this::setPrinterVariable, succes);
		Rule rule2 = new Rule(initBufferedReader(), rule1);
		Rule rule3 = new Rule(initInputStream(), rule2);
		Rule rule4 = new Rule(getInputStream(), rule3);
		Rule rule5 = new Rule(readSocketOwner(), this::setSocketOwner, rule4);
		Rule rule6 = new Rule(commonInsn(), rule5);
		Rule rule7 = new Rule(newInputStream(), rule6);
		Rule rule8 = new Rule(commonInsn(), rule7);
		Rule rule9 = new Rule(newBufferedReader(), rule8);
		this.managers.add(new RuleManager(rule9));
	}

	private void manager1() {
		SuccessRule succes = new SuccessRule();		
		Rule rule1 = new Rule(assignFieldVariable(), this::setPrinterVariable, succes);
		Rule rule2 = new Rule(initBufferedReader(), rule1);
		Rule rule3 = new Rule(initInputStream(), rule2);
		Rule rule4 = new Rule(getInputStream(), rule3);
		Rule rule5 = new Rule(readSocketOwner(), this::setSocketOwner, rule4);
		Rule rule6 = new Rule(varInsn(), rule5);
		Rule rule7 = new Rule(commonInsn(), rule6);
		Rule rule8 = new Rule(newInputStream(), rule7);
		Rule rule9 = new Rule(commonInsn(), rule8);
		Rule rule10 = new Rule(newBufferedReader(), rule9);
		this.managers.add(new RuleManager(rule10));
	}

	/**
	 * Crea un BufferedReader de un socket que es una variable local.
	 */
	private void createBufferedReaderFromLocalSocketManager() {
		SuccessRule succes = new SuccessRule();		
		Rule rule1 = new Rule(assignVariable(), this::setVariable, succes);
		Rule rule2 = new Rule(initBufferedReader(), rule1);
		Rule rule3 = new Rule(initInputStream(), rule2);
		Rule rule4 = new Rule(getInputStream(), rule3);
		Rule rule6 = new Rule(varInsn(), this::setSocketVarIndex, rule4);
		Rule rule7 = new Rule(commonInsn(), rule6);
		Rule rule8 = new Rule(newInputStream(), rule7);
		Rule rule9 = new Rule(commonInsn(), rule8);
		Rule rule10 = new Rule(newBufferedReader(), rule9);
		this.managers.add(new RuleManager(rule10));
	}
	
	private Predicate<Instruction> newBufferedReader(){
		return insn -> insn instanceof TypeInstruction &&
				       ((TypeInstruction)insn).getType().equals("java/io/BufferedReader") &&
				       ((TypeInstruction)insn).getOpCode() == Opcodes.NEW;				
	}
	
	private Predicate<Instruction> commonInsn(){
		return insn -> insn instanceof CommonInstruction;
	}
	
	private Predicate<Instruction> varInsn(){
		return insn -> insn instanceof VariableInstruction;
	}
	
	private Predicate<Instruction> newInputStream(){
		return insn -> insn instanceof TypeInstruction &&
				       ((TypeInstruction)insn).getType().equals("java/io/InputStreamReader") &&
				       ((TypeInstruction)insn).getOpCode() == Opcodes.NEW;				
	}
	
	private Predicate<Instruction> readSocketOwner(){
		return insn -> insn instanceof FieldInstruction &&  
				   (((FieldInstruction)insn).getDesc().contains(socket) || ((FieldInstruction)insn).getDesc().contains(serverSocket));
	}
	
	private Predicate<Instruction> getInputStream(){
		return insn -> insn instanceof MethodInstruction &&
					   (((MethodInstruction)insn).getOwner().equals(socket) || ((MethodInstruction)insn).getOwner().equals(serverSocket)) &&
					   ((MethodInstruction)insn).getName().equals("getInputStream") &&
					   ((MethodInstruction)insn).getOpCode() == Opcodes.INVOKEVIRTUAL;
	}
	
	private Predicate<Instruction> initInputStream(){
		return insn -> insn instanceof MethodInstruction &&
					   ((MethodInstruction)insn).getOwner().equals("java/io/InputStreamReader") &&
					   ((MethodInstruction)insn).getName().equals("<init>") &&
					   ((MethodInstruction)insn).getOpCode() == Opcodes.INVOKESPECIAL;
	}

	private Predicate<Instruction> initBufferedReader(){
		return insn -> insn instanceof MethodInstruction &&
					   ((MethodInstruction)insn).getOwner().equals("java/io/BufferedReader") &&
					   ((MethodInstruction)insn).getName().equals("<init>") &&
					   ((MethodInstruction)insn).getOpCode() == Opcodes.INVOKESPECIAL;
	}
	
	private Predicate<Instruction> assignFieldVariable(){
		return insn -> insn instanceof FieldInstruction &&
				       ((FieldInstruction)insn).getDesc().contains("java/io/BufferedReader");
	}	

	private Predicate<Instruction> assignVariable(){
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
			this.name = ((VariableInstruction)instruction).getVar().toString();
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
