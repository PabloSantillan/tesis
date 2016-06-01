package com.uba.tesis.cfsmgenerator.core.util;

import java.util.List;

import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.State;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.transitions.Transition;

public class Fsm {
	private String builderName;
	private String className;
	private List<Transition> transitions;
	private List<State> states;
	
	
	public Fsm(String builderName, String className, List<Transition> transitions, List<State> states){
		this.builderName = builderName;
		this.className = className;
		this.transitions = transitions;
		this.states = states;
	}


	public String getBuilderName() {
		return builderName;
	}


	public void setBuilderName(String builderName) {
		this.builderName = builderName;
	}


	public String getClassName() {
		return className;
	}


	public void setClassName(String className) {
		this.className = className;
	}


	public List<Transition> getTransitions() {
		return transitions;
	}


	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}


	public List<State> getStates() {
		return states;
	}


	public void setStates(List<State> states) {
		this.states = states;
	}
}
