package com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.uba.tesis.cfsmgenerator.core.fsms.fsm.FsmComponent;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.transitions.Transition;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class Block {
	private Boolean visited = false;
	private String label;
	private Integer lineNumber;
	private Map<String, Block> successors;
	private List<FsmComponent> fsmComponents;
	
	public Block(){
		this.successors = new HashMap<String, Block>();
		this.fsmComponents = new ArrayList<FsmComponent>();
	}
	
	@Override
	public String toString(){
		return String.format("%s - Line: %s", label, lineNumber);
	}

	public String getComponentString(){
		String componentsStr = "";
		for (FsmComponent fsmComponent : fsmComponents) {
			componentsStr += fsmComponent.toString();
			componentsStr += " \n";
			componentsStr += " ----------";
			componentsStr += " \n";			
		}
		
		return componentsStr;
	}

	public void addSuccessor(String metadata, Block block) {
		this.successors.put(metadata, block);		
	}
	
	public Transition getTransition(){
		Optional<FsmComponent> component = this.getFsmComponents().stream().filter(c -> c instanceof Transition).findFirst();
		if (component.isPresent()){
			return (Transition)component.get();
		} else {
			return null;
		}
	}
	
	public void addFsmComponent(FsmComponent fsmComponent) {
		if (fsmComponent instanceof Transition){
			Transition actual = getTransition(); 
			if (actual != null){
				this.fsmComponents.remove(actual);
				// throw new RuntimeException("El bloque ya contiene una transicion. Estamos al horno."); TODO:pgs:revisar si tengo que tirar exception
			}
		}

		this.fsmComponents.add(fsmComponent);					
	}

	public void removeFsmComponent(FsmComponent fsmComponent) {
		this.fsmComponents.remove(fsmComponent);		
	}
	
	// getters and setters

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public Map<String, Block> getSuccessors() {
		return successors;
	}
	
	public Boolean getVisited() {
		return visited;
	}

	public void setVisited(Boolean visited) {
		this.visited = visited;
	}
	
	public List<FsmComponent> getFsmComponents(){
		return this.fsmComponents;
	}
}
