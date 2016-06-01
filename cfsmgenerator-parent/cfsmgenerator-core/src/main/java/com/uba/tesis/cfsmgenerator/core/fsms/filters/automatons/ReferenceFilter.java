package com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons;

import java.util.function.Predicate;

import org.objectweb.asm.Opcodes;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.MethodInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.methods.MethodBuilder;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.Rule;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.RuleManager;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.SuccessRule;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.ReferenceState;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class ReferenceFilter extends Filter{
	
	private String name;
	private String owner;

	public ReferenceFilter(MethodBuilder builder) {
		super(builder);
	}
	
	@Override
	protected void doAction(MethodBuilder builder ) {
		System.out.println("***** Agrego un ReferenceState ******");
		builder.addReferenceState(new ReferenceState(name, owner));	
	};
	
	@Override
	public void defineManagers() {
		SuccessRule succes = new SuccessRule();		
		Rule rule1 = new Rule(isReference(), this::setNameAndOwner, succes);
		this.managers.add(new RuleManager(rule1));
	}	
	
	private Predicate<Instruction> isReference(){
		return insn -> insn instanceof MethodInstruction && ((MethodInstruction)insn).getOpCode() == Opcodes.INVOKESTATIC; 
	}
	
	private void setNameAndOwner(Instruction instruction) {
		if (instruction instanceof MethodInstruction){
			this.name = ((MethodInstruction) instruction).getName();
			this.owner = ((MethodInstruction) instruction).getOwner();
		}		
	}
}
