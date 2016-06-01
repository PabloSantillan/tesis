package com.uba.tesis.cfsmgenerator.core.fsms.fsm.transitions;

import com.uba.tesis.cfsmgenerator.core.fsms.fsm.FsmComponent;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.State;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.TemporalState;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class Transition extends FsmComponent{
	
	protected State from;
	protected State to;
		
	protected String message;
	private Integer varIndex;
		
	public Transition(){
		super();
		setFrom(new TemporalState());
		setTo(new TemporalState());
	}
	
	public Transition(Transition other) {
		super();
		this.setFrom(other.getFrom());
		this.setTo(other.getTo());
		this.setMessage(other.getMessage());
		this.setBlock(other.block);
	}

	@Override
	public String toString(){
		String fromStr = from == null? "?" : from.toString();
		String toStr = to == null? "?" : to.toString();
		return String.format("Trans[from: %s - msg: %s - to: %s]", fromStr, message.toUpperCase(), toStr);
	}
	
	// getters and setters

	public State getFrom() {
		return from;
	}

	public void setFrom(State from) {
		if (from != null){
			from.addTransition(this);			
		}		
		this.from = from;
	}

	public State getTo() {
		return to;
	}

	public void setTo(State to) {
		if (to != null){
			to.addIncomingTransition(this);;
		} 
		
		this.to = to;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setVarIndex(Integer varIndex) {
		this.varIndex = varIndex;		
	}
	
	public Integer getVarIndex(){
		return this.varIndex;
	}
}
