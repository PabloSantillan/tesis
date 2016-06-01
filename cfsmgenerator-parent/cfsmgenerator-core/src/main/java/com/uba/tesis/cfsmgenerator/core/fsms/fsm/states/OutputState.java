package com.uba.tesis.cfsmgenerator.core.fsms.fsm.states;

import java.util.function.Function;

import com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators.StateReader;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators.StateWriter;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class OutputState extends State{

	private StateReader reader;
	private StateWriter writer;
	private Boolean virtual = false;
	
	public OutputState(String host, Integer port, String varName, Integer varIndex){
		this.setHost(host);
		this.setPort(port);
		this.setVarName(varName);
		this.setVarIndex(varIndex);
	}

	public OutputState(OutputState another){
		this.host = another.host;
		this.port = another.port;
		this.block = another.block;
		this.varName = another.varName;
		
		if (another.reader != null){
			this.reader = new StateReader(another.reader);			
		}
		if (another.writer != null){
			this.writer = new StateWriter(another.writer);			
		}
	}
	
	@Override
	public String toString(){	
		String readerVarName = this.reader == null? "null" : isnullEmpty.apply(this.reader.getVarName());
		String writerVarNmae = this.writer == null? "null" : isnullEmpty.apply(this.writer.getVarName());
		String varName = this.varName == null? "null" : this.varName;
		String varIndex = this.varIndex == null? "null" : this.varIndex.toString();
		String socketName = this.socketName == null? "null" : this.socketName;
		
		return String.format("socket. varName: %s. varIndex: %s. socketName: %s \n %s -> %s", 
				varName, varIndex, socketName, readerVarName, writerVarNmae);
	}
	
	private Function<Object, String> isnullEmpty = s -> s == null? "" : s.toString();
	
	/// getters and setters
	
	public StateReader getReader() {
		return reader;
	}

	public void setReader(StateReader reader) {
		this.reader = reader;
	}

	public StateWriter getWriter() {
		return writer;
	}

	public void setWriter(StateWriter writer) {
		this.writer = writer;
	}

	public Boolean getVirtual() {
		return virtual;
	}

	public void setVirtual(Boolean virtual) {
		this.virtual = virtual;
	}
}
