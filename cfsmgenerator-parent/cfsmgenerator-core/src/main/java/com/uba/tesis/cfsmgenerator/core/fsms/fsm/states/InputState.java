package com.uba.tesis.cfsmgenerator.core.fsms.fsm.states;

import com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators.HandlerInputState;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class InputState extends State{

	private HandlerInputState handler;
				
	@Override
	public String toString(){
		return String.format("SSocket (%s). port: %s", this.varName, this.port);
	}
	
	/// getters and setters

	public HandlerInputState getHandler() {
		return handler;
	}

	public void setHandler(HandlerInputState handler) {
		this.handler = handler;
	}

}
