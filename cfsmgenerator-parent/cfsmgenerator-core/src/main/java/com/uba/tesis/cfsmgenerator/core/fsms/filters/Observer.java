package com.uba.tesis.cfsmgenerator.core.fsms.filters;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;

/**
 * 
 * @author Pablo Santillan
 *
 */
public interface Observer {
	
	public void notify(Instruction intruction);
}
