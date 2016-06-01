package com.uba.tesis.cfsmgenerator.core.fsms.analyzers;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.Observer;

/**
 * 
 * @author Pablo Santillan
 *
 */
public interface Observable {
	void register(Observer observer);
	void notifyObservers(Instruction instruction);	
}
