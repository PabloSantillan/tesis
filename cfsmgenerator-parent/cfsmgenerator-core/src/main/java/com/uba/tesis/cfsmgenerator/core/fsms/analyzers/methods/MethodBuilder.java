package com.uba.tesis.cfsmgenerator.core.fsms.analyzers.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.objectweb.asm.Opcodes;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.OpCodesString;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators.Block;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators.HandlerInputState;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators.InstanceReader;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators.StateReader;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators.StateWriter;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.InputState;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.OutputState;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.ReferenceState;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.transitions.Transition;
import com.uba.tesis.cfsmgenerator.core.util.LocalVariableType;
import com.uba.tesis.cfsmgenerator.core.util.Variable;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class MethodBuilder {
	
	private String methodName;
	private List<OutputState> outputStates;
	private List<InputState> inputStates;
	private List<ReferenceState> referenceStates;
	private List<HandlerInputState> handleInputs;
	private List<StateWriter> writers;
	private List<StateReader> readers;
	
	private List<Transition> transitions;
	private Map<Integer, String> instanceReaders;

	private List<Variable> variables;
	private List<Block> blocks;
	
	private Map<String, String> socketNames;

	private Block currentBlock = null;
	
	public MethodBuilder(String methodName){
		this.methodName = methodName;
		this.outputStates = new ArrayList<OutputState>();
		this.writers = new ArrayList<StateWriter>();
		this.readers = new ArrayList<StateReader>();

		this.inputStates = new ArrayList<InputState>();
		this.handleInputs = new ArrayList<HandlerInputState>();
		
		this.referenceStates = new ArrayList<ReferenceState>();
		
		this.transitions = new ArrayList<Transition>();
		this.instanceReaders = new HashMap<Integer, String>();
		this.variables = new ArrayList<Variable>();
		this.blocks = new ArrayList<Block>();
		
		this.socketNames = new HashMap<String, String>();		
	}
	
	public void addOutputState(OutputState state){
		state.setBlock(currentBlock);
		this.outputStates.add(state);
	}	
	
	public void addInputState(InputState state){
		state.setBlock(currentBlock);
		
		this.inputStates.add(state);
	}

	public void addReferenceState(ReferenceState state) {
		state.setBlock(currentBlock);
		this.referenceStates.add(state);
		
	}
	
	public void addHandletInput(HandlerInputState handler) {		
		this.handleInputs.add(handler);
	}
	
	/**
	 * 
	 * @param stateVarName: representa el nombre de la variable del socket duenio del writer.
	 * @param varName: representa el nombre del writer que se esta invocando.
	 */
	public void addWriter(String stateVarName, String varName, Integer stateVarIndex) {
		StateWriter writer = new StateWriter();
		writer.setVarName(varName);
		writer.setStateVarName(stateVarName);
		writer.setStateVarIndex(stateVarIndex);
		
		this.writers.add(writer);
	}

	public void addReader(String stateVarName, String varName, Integer stateVarIndex) {
		StateReader reader = new StateReader();
		reader.setVarName(varName);
		reader.setStateVarName(stateVarName);
		reader.setStateVarIndex(stateVarIndex);
		
		this.readers.add(reader);
	}
	
	public void addTransition(Transition transition) {
		// transition.getVarIndex() == null: indica que es una transicion de un writer. No hay que buscar el indice del reader.
		// la otra parte: indica que hay que buscar el reader.
		if (transition.getVarIndex() == null || this.instanceReaders.get(transition.getVarIndex()) != null){
			transition.setBlock(currentBlock);
			this.transitions.add(transition);		
		}
	}
	
	public void addInstanceReader(InstanceReader instance) {
		this.instanceReaders.put(instance.getVarIndexStored(), instance.getVarReaderName());		
	}
	
	public void addLocalVariableIndex(String name, Integer index, LocalVariableType type) {
		this.variables.add(Variable.from(name, index, type));
	}
		
	public void addBlock(String label, Integer lineNumber) {
		Block block = getBlockByLabel(label);
		// if block exist, then update the line number.
		if (block != null){
			block.setLineNumber(lineNumber);			
		} else {
			// if block do not exist, then add it.
			block = new Block();
			block.setLabel(label);
			block.setLineNumber(lineNumber);
			this.blocks.add(block);					
		}
		
		// if exist a current block, then add new block as successor of them.
		if (this.getCurrentBlock() != null){
			this.getCurrentBlock().addSuccessor("", block);
		}
		
		// set the current block for fsm components.
		this.setCurrentBlock(block);
	}
	
	public void addFlow(String blockToLabel, Integer opcode) {
		if (opcode == Opcodes.RETURN){
			this.currentBlock = null;
			return;
		}
		
		Block block = getBlockByLabel(blockToLabel);
		if (block == null){
			block = new Block();
			block.setLabel(blockToLabel);
			this.blocks.add(block);			
		}
		
		if (this.currentBlock != null){
			this.currentBlock.addSuccessor(OpCodesString.getValue(opcode), block);
		}
		
		// if is GOTO, then remove the current block because do not have common successor
		if (opcode == Opcodes.GOTO){
			this.currentBlock = null;
		}
	}
	
	public void addSocketName(String varName, String socketAlias) {
		this.socketNames.put(varName, socketAlias);		
	}
	
	@Override
	public String toString(){
		return String.format("method name: %s", this.methodName);
	}
	
	// private function(s)
	
	private Block getBlockByLabel(String label){
		Block block = null;
		Optional<Block> existent = this.blocks.stream().filter(b -> b.getLabel().equals(label)).findFirst();
		if (existent.isPresent()){
			block = existent.get();
		}
		return block;
	}
	
	// getters and setters
	
	public List<HandlerInputState> getHandleInputs() {
		return handleInputs;
	}

	public void setHandleInputs(List<HandlerInputState> handleInputs) {
		this.handleInputs = handleInputs;
	}

	public List<InputState> getInputStates() {
		return inputStates;
	}

	public void setInputStates(List<InputState> inputStates) {
		this.inputStates = inputStates;
	}

	public List<StateReader> getReaders() {
		return readers;
	}

	public void setReaders(List<StateReader> readers) {
		this.readers = readers;
	}

	public List<OutputState> getOutputStates() {
		return outputStates;
	}

	public void setOutputStates(List<OutputState> outputStates) {
		this.outputStates = outputStates;
	}

	public List<StateWriter> getWriters() {
		return writers;
	}

	public void setWriters(List<StateWriter> writers) {
		this.writers = writers;
	}

	public List<Transition> getTransitions() {
		return transitions;
	}

	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}

	public List<Variable> getVariables() {
		return variables;
	}

	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	public Block getCurrentBlock() {
		return currentBlock;
	}

	public void setCurrentBlock(Block currentBlock) {
		this.currentBlock = currentBlock;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}

	public Map<String, String> getSocketNames() {
		return socketNames;
	}

	public void setSocketNames(Map<String, String> socketNames) {
		this.socketNames = socketNames;
	}

	public Map<Integer, String> getInstanceReaders() {
		return instanceReaders;
	}

	public void setInstanceReaders(Map<Integer, String> instanceReaders) {
		this.instanceReaders = instanceReaders;
	}

	public List<ReferenceState> getReferenceStates() {
		return referenceStates;
	}

	public void setReferenceStates(List<ReferenceState> referenceStates) {
		this.referenceStates = referenceStates;
	}
}
