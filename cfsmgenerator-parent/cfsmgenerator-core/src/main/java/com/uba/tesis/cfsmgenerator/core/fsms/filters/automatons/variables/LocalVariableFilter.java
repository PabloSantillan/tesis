package com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.variables;

import java.util.function.Predicate;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.LocalVariableInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.methods.MethodBuilder;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.Filter;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.Rule;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.RuleManager;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.SuccessRule;
import com.uba.tesis.cfsmgenerator.core.util.LocalVariableType;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class LocalVariableFilter extends Filter{
	
	private String name;
	private Integer index;
	private LocalVariableType type;
	private String serverSocket = "java/net/ServerSocket";
	private String socket = "java/net/Socket";
	private String reader = "java/io/BufferedReader";
	private String writer = "java/io/PrintWriter";
	
	public LocalVariableFilter(MethodBuilder builder) {
		super(builder);
	}
	
	@Override
	protected void doAction(MethodBuilder builder) {
		System.out.println(String.format("***** Agrego un LocalVariableIndex. Type: %s ******", type));
		builder.addLocalVariableIndex(name, index, type);	
	};
	
	@Override
	public void defineManagers() {
		createManagerSockets();
		createManagerReaders();
		createManagerWriters();
	}	
	
	private void createManagerWriters() {
		SuccessRule succes = new SuccessRule();		
		Rule rule1 = new Rule(isLocalVariableSocketIndex(), this::setSocketValues, succes);
		this.managers.add(new RuleManager(rule1));
	}

	private void createManagerReaders() {
		SuccessRule succes = new SuccessRule();		
		Rule rule1 = new Rule(isLocalVariableReaderIndex(), this::setReaderValues, succes);
		this.managers.add(new RuleManager(rule1));
	}

	private void createManagerSockets() {
		SuccessRule succes = new SuccessRule();		
		Rule rule1 = new Rule(isLocalVariableWriterIndex(), this::setWriterValues, succes);
		this.managers.add(new RuleManager(rule1));
	}

	private Predicate<Instruction> isLocalVariableSocketIndex(){
		return insn -> insn instanceof LocalVariableInstruction && 
					   (((LocalVariableInstruction)insn).getDesc().contains(serverSocket)||
					    ((LocalVariableInstruction)insn).getDesc().contains(socket)); 
	}

	private Predicate<Instruction> isLocalVariableReaderIndex(){
		return insn -> insn instanceof LocalVariableInstruction && (((LocalVariableInstruction)insn).getDesc().contains(reader)); 
	}

	private Predicate<Instruction> isLocalVariableWriterIndex(){
		return insn -> insn instanceof LocalVariableInstruction && (((LocalVariableInstruction)insn).getDesc().contains(writer)); 
	}

	private void setSocketValues(Instruction instruction) {
		if (instruction instanceof LocalVariableInstruction){
			setCommonValues((LocalVariableInstruction) instruction);
			this.type = LocalVariableType.SOCKET;
		}		
	}

	private void setReaderValues(Instruction instruction) {
		if (instruction instanceof LocalVariableInstruction){
			setCommonValues((LocalVariableInstruction) instruction);
			this.type = LocalVariableType.READER;
		}		
	}

	private void setWriterValues(Instruction instruction) {
		if (instruction instanceof LocalVariableInstruction){
			setCommonValues((LocalVariableInstruction) instruction);
			this.type = LocalVariableType.WRITER;
		}		
	}

	private void setCommonValues(LocalVariableInstruction instruction) {
		this.name = instruction.getName();			
		this.index = instruction.getIndex();
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
}
