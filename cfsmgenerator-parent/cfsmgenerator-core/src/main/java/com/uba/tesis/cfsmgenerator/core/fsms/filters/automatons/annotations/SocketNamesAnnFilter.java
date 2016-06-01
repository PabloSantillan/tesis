package com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons.annotations;

import java.util.function.Predicate;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.AnnotationValueInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;
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
public class SocketNamesAnnFilter extends Filter{
	
	private String socketAlias;
	private String varName;
	
	public SocketNamesAnnFilter(MethodBuilder builder) {
		super(builder);
	}
	
	@Override
	protected void doAction(MethodBuilder builder) {
		System.out.println("***** Agrego un SocketName ******");
		builder.addSocketName(socketAlias, varName);	
	};
	
	@Override
	public void defineManagers() {
		SuccessRule success = new SuccessRule();		
		Rule rule1 = new Rule(isVariableNameAnnotation(), this::setVariableName, success);
		Rule rule2 = new Rule(isSocketAliasAnnotation(), this::setSocketName, rule1);
		this.managers.add(new RuleManager(rule2));
	}	
	
	
	private Predicate<Instruction> isVariableNameAnnotation(){
		return insn -> insn instanceof AnnotationValueInstruction && 
					   (((AnnotationValueInstruction)insn).getName().equals("variableName")); 
	}

	private Predicate<Instruction> isSocketAliasAnnotation(){
		return insn -> insn instanceof AnnotationValueInstruction && 
					   (((AnnotationValueInstruction)insn).getName().equals("socketName")); 
	}

	private void setSocketName(Instruction instruction) {
		if (instruction instanceof AnnotationValueInstruction){
			this.socketAlias = ((AnnotationValueInstruction) instruction).getValue().toString();			
		}		
	}

	private void setVariableName(Instruction instruction) {
		if (instruction instanceof AnnotationValueInstruction){
			this.varName = ((AnnotationValueInstruction) instruction).getValue().toString();			
		}		
	}

	public String getVarName() {
		return this.varName;
	}

	public String getSocketAlias() {
		return this.socketAlias;
	}
}
