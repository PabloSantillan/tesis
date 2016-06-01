package com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators;

import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.OutputState;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class StateReader {
	private String varName;
	private String stateVarName;
	private Integer stateVarIndex;
	private OutputState state;

	public StateReader(){
		
	}
	
	public StateReader(StateReader another){
		this.varName = another.varName;
		this.stateVarName = another.stateVarName;
		this.stateVarIndex = another.stateVarIndex;
	}
	
	@Override
	public String toString(){
		return String.format("varName: %s. stateVarName: %s. stateVarIndex: %s", this.varName, this.stateVarName, this.stateVarIndex);
	}
	
	// getters and setters
	
	public OutputState getState() {
		return state;
	}

	public void setState(OutputState state) {
		this.state = state;
		this.state.setReader(this);
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public String getStateVarName() {
		return stateVarName;
	}

	public void setStateVarName(String stateVarName) {
		this.stateVarName = stateVarName;
	}

	public Integer getStateVarIndex() {
		return stateVarIndex;
	}

	public void setStateVarIndex(Integer stateVarIndex) {
		this.stateVarIndex = stateVarIndex;
	}
}
