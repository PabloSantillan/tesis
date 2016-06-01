package com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators;

import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.InputState;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class HandlerInputState {
	// class responsible for managing the server socket.
	private String owner;
	private InputState state;
	private Integer varIndex;

	// getters and setters
	
	public InputState getState() {
		return state;
	}

	public void setState(InputState state) {
		this.state = state;
		this.state.setHandler(this);
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Integer getVarIndex() {
		return varIndex;
	}

	public void setVarIndex(Integer varIndex) {
		this.varIndex = varIndex;
	}
}
