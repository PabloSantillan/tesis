package com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators;

import com.uba.tesis.cfsmgenerator.core.fsms.fsm.FsmComponent;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class InstanceReader extends FsmComponent{
		
	protected String varReaderName;	
	private Integer varIndexStored;
		
	// getters and setters

	public String getVarReaderName() {
		return varReaderName;
	}

	public void setVarReaderName(String varReaderName) {
		this.varReaderName = varReaderName;
	}

	public Integer getVarIndexStored() {
		return varIndexStored;
	}

	public void setVarIndexStored(Integer varStored) {
		this.varIndexStored = varStored;
	}
}
