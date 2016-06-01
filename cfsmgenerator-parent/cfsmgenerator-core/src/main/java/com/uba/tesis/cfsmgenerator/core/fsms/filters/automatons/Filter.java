package com.uba.tesis.cfsmgenerator.core.fsms.filters.automatons;

import java.util.ArrayList;
import java.util.List;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.methods.MethodBuilder;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.Observer;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.rules.RuleManager;

/**
 * Filters are automatons that filter instructions and detect relevant concepts, 
 * like states, transitions, references, collaborators, etc.
 * 
 * @author Pablo Santillan
 *
 */
public abstract class Filter implements Observer {
	private MethodBuilder builder;
	protected List<RuleManager> managers = new ArrayList<RuleManager>();
	
	public Filter(MethodBuilder builder){
		this.builder = builder;
		defineManagers();
	}
	
	public abstract void defineManagers();
	
	@Override
	public void notify(Instruction instruction){
		managers.forEach(manager -> {
			manager.evaluate(instruction);
			if (manager.isSuccess()){
				doAction(this.builder);
			} 	
		});
	}
	
	protected void doAction(MethodBuilder builder){
	}
}
