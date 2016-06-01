package com.uba.tesis.cfsmgenerator.core.fsms.fsm.states;

import java.util.ArrayList;
import java.util.List;

import com.uba.tesis.cfsmgenerator.core.fsms.fsm.FsmComponent;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.transitions.Transition;

/**
 * 
 * @author Pablo Santillan
 *
 */
public abstract class State extends FsmComponent {
	
	protected String host;
	protected Integer port;
	
	// represent the variable name related to State
	protected String varName;
	// represent the variable index related to State
	protected Integer varIndex;
	// represent the socket name
	protected String socketName;
		
	private Integer key;
	
	private List<Transition> transitions;
	private List<Transition> incomingTransitions;
	
	public State(){
		this.transitions = new ArrayList<Transition>();
		this.incomingTransitions = new ArrayList<Transition>();
	}
	
	public void addTransition(Transition transition){
		this.transitions.add(transition);
	}

	public void addIncomingTransition(Transition transition){
		this.incomingTransitions.add(transition);
	}

//	@Override
//	public String toString(){
//		return String.format("{ host: %s - port: %s }", this.host, this.port);
//	}
	
	/// getters and setters
	
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}	
	public Integer getVarIndex() {
		return varIndex;
	}

	public void setVarIndex(Integer varIndex) {
		this.varIndex = varIndex;
	}	
	
	public List<Transition> getTransitions(){
		return this.transitions;
	}

	public void setTransitions(List<Transition> transitions){
		this.transitions = transitions;
	}

	public List<Transition> getIncomingTransitions() {
		return incomingTransitions;
	}

	public void setIncomingTransitions(List<Transition> incommingTransitions) {
		this.incomingTransitions = incommingTransitions;
	}

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	public String getSocketName() {
		return socketName;
	}

	public void setSocketName(String socketName) {
		this.socketName = socketName;
	}
}
