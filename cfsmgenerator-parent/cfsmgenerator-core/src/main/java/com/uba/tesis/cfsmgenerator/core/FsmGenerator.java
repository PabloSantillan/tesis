package com.uba.tesis.cfsmgenerator.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.objectweb.asm.Opcodes;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.clases.ClassAnalyzer;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.OpCodesString;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.methods.MethodBuilder;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.FsmComponent;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators.Block;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators.HandlerInputState;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators.StateReader;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.collaborators.StateWriter;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.FinalState;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.InputState;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.OutputState;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.OwnerState;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.ReferenceState;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.State;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.TemporalState;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.transitions.LambdaTransition;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.transitions.Transition;
import com.uba.tesis.cfsmgenerator.core.util.Fsm;
import com.uba.tesis.cfsmgenerator.core.util.LocalVariableType;

/**
 * A java class that generate a list of {@link Fsm} entities from class passed as parameter.
 * 
 * @author Pablo Santillan
 */
public class FsmGenerator {
	private List<ClassAnalyzer> clases;

	private FsmGenerator() {
	}
	
	/**
	 * Constructs a new {@link FsmGenerator} object.
	 * 
	 * @param clases is 
	 * 					a list of classes to analyze
	 * 
	 * @return the new object 		
	 */
	public static FsmGenerator from(List<ClassAnalyzer> clases) {
		FsmGenerator generator = new FsmGenerator();
		generator.setClases(clases);		
		return generator;
	}
	
	/**
	 * Use ASM to analyze classes and generate CFSMs
	 * 
	 * @return list of FSMS generated
	 */
	public List<Fsm> generate() {
		associateWritersToOutputStates();
		associateReadersToOutputStates();
		associateHandlersToInputStates();
		associateTransitionsToStates();
	
		buildGraphsFromFlows();
		
		List<Fsm> fsms = generateFsm();
		
		return fsms;
	}

	/**
	 * Render transitions associated to classes. Must be used only for test.
	 * 
	 * @return the map that contain name of class and graphic
	 */
	public Map<String, String> renderTransitions(){
		Map<String, String> graphics = new HashMap<String, String>();
		
		for (ClassAnalyzer classAnalyzer : clases) {
			for (MethodBuilder methodBuilder : classAnalyzer.getMethodsBuilder()) {
				
				String classMethodName = String.format("%s_%s", classAnalyzer.getClassName(), methodBuilder.getMethodName());
		        final StringBuilder result = new StringBuilder(String.format("digraph \"%s()\" {\n", classMethodName));
		        
		        enumerateVertices(methodBuilder, result);        
		        enumerateEdges(methodBuilder, result);
		        
		        result.append("}");
		        
		        String graphic = result.toString();
		        graphics.put(classMethodName, graphic);
			}
		}
		
		return graphics;		
	}
	
	/// private function(s)
	
	/**
	 * For each <code>class<code>, generate FSM.
	 * 
	 */
	private void buildGraphsFromFlows() {
		for (ClassAnalyzer classAnalyzer : clases) {			
			for (MethodBuilder methodBuilder : classAnalyzer.getMethodsBuilder()) {
				Block firstBlock = methodBuilder.getBlocks().get(0); 
				doGenerateFsm(firstBlock, null, new ArrayList<Integer>());				        		        				
			}
		}		
	}
	
	private List<Fsm> generateFsm() {
		List<Fsm> fsms = new ArrayList<Fsm>();
		
		for (ClassAnalyzer classAnalyzer : clases) {			
			Predicate<MethodBuilder> toAnalyze = m -> /*m.getMethodName().contains("main") || */m.getMethodName().contains("run"); 
			Optional<MethodBuilder> methodBuilder = classAnalyzer.getMethodsBuilder().stream().filter(toAnalyze).findFirst();
			if (!methodBuilder.isPresent()){
				continue;
			}
			Block firstBlock = methodBuilder.get().getBlocks().get(0); 				        		        
	        Transition firstTrans = getFirstTransition(firstBlock);
	        
			completeReferencesStates(firstTrans.getFrom(), new ArrayList<State>(), methodBuilder.get());	
			
	        unificareSameDestinyLambdaTransitions(firstTrans, new ArrayList<State>());	
	        
	        List<Transition> transitions = removeTemporalStates(firstTrans.getFrom(), new ArrayList<State>());	  
	        
	        firstTrans = getFirstTransition(firstBlock);
	        
	        removeLambdaTransitions(firstTrans.getFrom(), new ArrayList<State>(), methodBuilder.get());

	        transitions = getTransitions(firstTrans.getFrom(), new ArrayList<State>());

	        List<State> states = defineStates(transitions, methodBuilder.get());		
	        				   
			Fsm fsm = new Fsm(methodBuilder.get().getMethodName(), classAnalyzer.getClassName(), transitions, states);
	        fsms.add(fsm);				
		}		
		
		return fsms;
	}
	
	private List<Transition> getTransitions(State from, List<State> visited) {
		List<Transition> result = new ArrayList<Transition>();
		if (visited.contains(from)){
			return result;
		}
		visited.add(from);
		for (Transition transition : from.getTransitions()) {
			result.add(transition);
			List<Transition> trans = getTransitions(transition.getTo(), visited);
			result.addAll(trans);
		}
		return result;
	}

	private void removeLambdaTransitions(State from, List<State> visited, MethodBuilder builder) {		
		if (visited.contains(from)){
			return;
		}		
		visited.add(from);
		
		// get lambda transitions outgoing from current state..
		List<Transition> lambdaTrans = from.getTransitions().stream().filter(t -> t instanceof LambdaTransition).collect(Collectors.toList());
		
		// for each outgoing transition
		for (Transition lambdaTran : lambdaTrans) {
			// for each incoming transition
			for (Transition incomingTran : from.getIncomingTransitions()) {
				Transition tran;
				try {			
					tran = incomingTran.getClass().newInstance();
					tran.setFrom(incomingTran.getFrom());
					tran.setTo(lambdaTran.getTo());
					tran.setBlock(incomingTran.getBlock());
					tran.setVarIndex(incomingTran.getVarIndex());
					tran.setMessage(incomingTran.getMessage());				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		if (!lambdaTrans.isEmpty()){
//			// remove incoming transition
			for (Transition incomingTran : from.getIncomingTransitions()) {
				incomingTran.getFrom().getTransitions().remove(incomingTran);				
			}
			from.setIncomingTransitions(null);			
		}
		
		List<State> toStates = from.getTransitions().stream().map(t -> t.getTo()).collect(Collectors.toList());
		for (Transition lambdaTran : lambdaTrans) {
			lambdaTran.getTo().getIncomingTransitions().remove(lambdaTran);
			from.getTransitions().remove(lambdaTran);			
		}

		for (Transition transition : from.getTransitions()) {
			String fromAlias = getStateAliasName(from, builder);
			from.setSocketName(fromAlias);

			String toAlias = getStateAliasName(transition.getTo(), builder);
			transition.getTo().setSocketName(fromAlias);
			
			String msg = String.format("%s%s!%s", fromAlias, toAlias, transition.getMessage());
			transition.setMessage(msg);
		}
		
		for (State toState : toStates) {
			removeLambdaTransitions(toState, visited, builder);
		}
	}

	private void completeReferencesStates(State state, List<State> visited, MethodBuilder methodBuilder) {

		if (visited.contains(state)){
			return;
		}
		visited.add(state);
		
		List<State> toStates = state.getTransitions().stream().map(t -> t.getTo()).collect(Collectors.toList());
		
		if (state instanceof ReferenceState){
			ReferenceState rstate = (ReferenceState)state;			
			
			Transition firstRefTran = null;
			
			// get referenced graph.
			ClassAnalyzer clazz = getClassByName(rstate.getOwner());
			if (clazz != null){
				MethodBuilder rbuilder = getMethodByName(clazz, rstate.getMethodReferenced());
				if (rbuilder != null){
					// encontre la invocacion. Ahora tengo que obtener realmente el methodo.
					
					ReferenceState realReference = rbuilder.getReferenceStates().get(0);
					ClassAnalyzer realClass = getClassByName(realReference.getOwner());
					if (realClass != null){
						MethodBuilder realBuilder = getMethodByName(realClass, realReference.getMethodReferenced());
						if (realBuilder != null){
							methodBuilder.getSocketNames().putAll(realBuilder.getSocketNames());
							Block firstBlock = realBuilder.getBlocks().get(0); 				        		        
					        firstRefTran = getFirstTransition(firstBlock);							
						}
					}
				}
			}
			
			if (firstRefTran != null){
				// por cada transicion entrate a el state, apunto esas transiciones a la primer transicion del grafo referenciado
				for (Transition incomingTran : state.getIncomingTransitions()) {
					Transition tran;
					try {
						tran = incomingTran.getClass().newInstance();
						tran.setFrom(incomingTran.getFrom());
						tran.setTo(firstRefTran.getFrom());
						tran.setBlock(incomingTran.getBlock());
						tran.setVarIndex(incomingTran.getVarIndex());
						tran.setMessage(incomingTran.getMessage());								
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				// obtengo las ultimas transiciones del grafo referenciado
				List<State> lastStates = getLastStates(firstRefTran.getTo());
				
				// por cada una de ellas, apunto el estado final a el primer estado de mis transiciones.
				for (State lastState : lastStates) {
					for (Transition outgoingTran : state.getTransitions()) {
						Transition tran;
						try {
							tran = outgoingTran.getClass().newInstance();
							tran.setFrom(lastState);
							tran.setTo(outgoingTran.getTo());
							tran.setBlock(outgoingTran.getBlock());
							tran.setVarIndex(outgoingTran.getVarIndex());
							tran.setMessage(outgoingTran.getMessage());								
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
				clearStateTransitions(state);
			} else {
				// si no encuentro un grafo por reemplazar, quito el estado.

				// para cada transicion entrante, tengo que apuntarlas a las transiciones salientes.
				for (Transition incomingTran : state.getIncomingTransitions()) {
					for (Transition outgoingTran : state.getTransitions()) {
						incomingTran.setTo(outgoingTran.getTo());						
					}
				}
				//TODO:pgs: creo que arriba tengo que clonar las transiciones si es que hay mas de 1, sino se pierde la referencia.				
			}		
		}
		
		for (State toState : toStates) {
			completeReferencesStates(toState, visited, methodBuilder);
		}		
	}
	
	private List<State> getLastStates(State state) {
		List<State> states = doGetLastStates(state, state, new ArrayList<State>());
		states = states.stream().distinct().collect(Collectors.toList());
		return states;
	}

	private List<State> doGetLastStates(State firstState, State currentState, List<State> visited) {
		List<State> result = new ArrayList<State>();
		
		if (visited.contains(currentState)){
			if (currentState.equals(firstState)){
				result.add(currentState);
			}
			
			return result;
		}
		
		if (currentState.getTransitions().isEmpty()){
			result.add(currentState);
		} else {
			for (Transition transition : currentState.getTransitions()) {
				List<State> finals = doGetLastStates(firstState, transition.getTo(), visited);
				result.addAll(finals);
			}
		}
				
		return result;
	}
	
	private ClassAnalyzer getClassByName(String className){
		Optional<ClassAnalyzer> clazz = this.clases.stream().filter(c -> c.getClassName().equals(className)).findFirst();
		if (clazz.isPresent()){
			return clazz.get();
		}
		
		return null;
	}

	private MethodBuilder getMethodByName(ClassAnalyzer clazz, String methodName){
		Optional<MethodBuilder> rbuilder = clazz.getMethodsBuilder().stream().filter(m -> m.getMethodName().equals(methodName)).findFirst();
		if (rbuilder.isPresent()){
			return rbuilder.get();
		}
		
		return null;
	}
	
	private List<State> defineStates(List<Transition> transitions, MethodBuilder methodBuilder) {
		List<State> states = new ArrayList<State>();
		states.addAll(transitions.stream().map(t -> t.getFrom()).collect(Collectors.toList()));
		states.addAll(transitions.stream().map(t -> t.getTo()).collect(Collectors.toList()));
		states = states.stream().distinct().collect(Collectors.toList());
		
        defineStateSocketNames(states, methodBuilder);
		
		return states;
	}
	
	private void associateHandlersToInputStates() {
		this.clases.forEach(clazz -> {
						
			for (MethodBuilder builder : clazz.getMethodsBuilder()) {
				for (InputState inputState : builder.getInputStates()) {					
					// associate varName to inputState
					if (inputState.getVarName() == null){
						String varName = builder.getVariables().stream()
								.filter(v -> v.getType() == LocalVariableType.SOCKET && v.getIndex().equals(inputState.getVarIndex()))
								.findFirst()
								.get()
								.getName();
						inputState.setVarName(varName);
					}
					
					// associate handler to inputState
					Optional<HandlerInputState> handler = builder.getHandleInputs().stream().filter(h -> h.getVarIndex().equals(inputState.getVarIndex())).findFirst();
					if (handler.isPresent()){
						handler.get().setState(inputState);
					} 
				}
			}			
		});		
	}

	private void associateReadersToOutputStates() {
		this.clases.forEach(clazz -> {
			List<MethodBuilder> builders = clazz.getMethodsBuilder();
			BiFunction<List<StateReader>, List<StateReader>, List<StateReader>> addAll = (rec, actual) -> {
				actual.forEach(e -> rec.add(e));
				return rec;
			};
			
			List<StateReader> readers = builders.stream().map(builder -> builder.getReaders()).reduce(new ArrayList<StateReader>(), (rec, actual) -> addAll.apply(rec, actual));
			
			readers.forEach(reader -> {
				for (MethodBuilder builder : builders) {
					
					// complete output state varName if is not present.
					Function<OutputState, String> getVar = o -> builder.getVariables().stream()
							.filter(v -> v.getType() == LocalVariableType.SOCKET && v.getIndex().equals(o.getVarIndex()))
							.findFirst()
							.get()
							.getName();
					
					builder.getOutputStates().stream().filter(o -> o.getVarName() == null).forEach(o -> o.setVarName(getVar.apply(o)));

					Predicate<OutputState> sameVar = os -> (os.getVarName() != null && os.getVarName().equals(reader.getStateVarName())) || 
                            (os.getVarIndex() != null && os.getVarIndex().equals(reader.getStateVarIndex()));					
					Optional<OutputState> outputState = builder.getOutputStates().stream().filter(sameVar).findFirst();
					outputState.ifPresent(output -> reader.setState(output));
				}				
			});
		});		
	}

	private void associateWritersToOutputStates() {
		this.clases.forEach(clazz -> {
			List<MethodBuilder> builders = clazz.getMethodsBuilder();

			BiFunction<List<StateWriter>, List<StateWriter>, List<StateWriter>> addAll = (rec, actual) -> {
				actual.forEach(e -> rec.add(e));
				return rec;
			};
			
			// get writers of all method builders.
			List<StateWriter> writers = builders.stream().map(builder -> builder.getWriters()).reduce(new ArrayList<StateWriter>(), (rec, actual) -> addAll.apply(rec, actual));
			
			writers.forEach(writer -> {
				for (MethodBuilder builder : builders) {
					
					// complete output state varName if is not present.
					Function<OutputState, String> getVar = o -> builder.getVariables().stream()
							.filter(v -> v.getType() == LocalVariableType.SOCKET && v.getIndex().equals(o.getVarIndex()))
							.findFirst()
							.get()
							.getName();
					
					builder.getOutputStates().stream().filter(o -> o.getVarName() == null).forEach(o -> o.setVarName(getVar.apply(o)));
										
					// associate writer to output state.
					Predicate<OutputState> sameVar = os -> (os.getVarName() != null && os.getVarName().equals(writer.getStateVarName())) || 
							                               (os.getVarIndex() != null && os.getVarIndex().equals(writer.getStateVarIndex()));					
					Optional<OutputState> outputState = builder.getOutputStates().stream().filter(sameVar).findFirst();
					outputState.ifPresent(output -> writer.setState(output));
				}				
			});			
		});		
	}

	private void associateTransitionsToStates() {
		this.clases.forEach(clazz -> {
			List<MethodBuilder> builders = clazz.getMethodsBuilder();

			BiFunction<List<Transition>, List<Transition>, List<Transition>> addAll = (rec, actual) -> {
				actual.forEach(e -> rec.add(e));
				return rec;
			};
			
			// get transitions of all builders.
			List<Transition> trans = builders.stream().map(builder -> builder.getTransitions()).reduce(new ArrayList<Transition>(), (rec, actual) -> addAll.apply(rec, actual));
			
			//TODO:pgs: revisar esto. puede estar asociando a estados de otros metodos. Deberia empezar por el mismo builder al que pertenece la transicion.
			
			// me quedo con los builders que tienen readers.
			List<MethodBuilder> buildersWithReader = builders.stream().filter(b -> !b.getReaders().isEmpty()).collect(Collectors.toList());
			trans.forEach(tran -> {
				for (MethodBuilder builder : buildersWithReader) {
			
					// instance reader es un mapa que contiene: clave: indice de la variable donde se deja el mensaje. valor: nombre del reader del cual viene
					String varName = builder.getInstanceReaders().get(tran.getVarIndex());
					
					// analyze the from
					Optional<StateReader> reader = builder.getReaders().stream().filter(r -> r.getVarName().equals(varName)).findFirst();
					reader.ifPresent(re -> {
						OutputState clone = new OutputState(re.getState()); 
						tran.setFrom(clone);
					} );					
				}				
			});
			
			// me quedo con los builders que tienen writers.
			List<MethodBuilder> buildersWithWriter = builders.stream().filter(b -> !b.getWriters().isEmpty()).collect(Collectors.toList());			
			trans.forEach(tran -> {
				for (MethodBuilder builder : buildersWithWriter) {					
					String varName = builder.getInstanceReaders().get(tran.getVarIndex());
					
					// analyze the to
					Optional<StateWriter> writer = Optional.empty();
					if (tran.getTo() instanceof OutputState && ((OutputState)tran.getTo()).getVirtual()){
						StateWriter toWriter = ((OutputState)tran.getTo()).getWriter();
						writer = builder.getWriters().stream().filter(w -> (w.getVarName() != null && (w.getVarName().equals(toWriter.getVarName())) || 
								                                           (toWriter.getVarIndex() != null && w.getVarName().equals(toWriter.getVarIndex().toString())))).findFirst();
					} else {
						writer = builder.getWriters().stream().filter(w -> w.getVarName().equals(varName)).findFirst();
					}
					writer.ifPresent(wr -> {
						OutputState clone = new OutputState(wr.getState()); 
						tran.setTo(clone);
					});											
				}				
			});			
		});		
	}
	
	private List<Transition> removeTemporalStates(State state, List<State> visited) {
		List<Transition> finalTrans = new ArrayList<Transition>();
		
		if (visited.contains(state)){
			return finalTrans;
		}
		visited.add(state);
		
		// if is a temporal state then
		if (state instanceof TemporalState){
			// remove cycles
			List<Transition> cycleTransitions = state.getTransitions().stream().filter(t -> t.getTo().equals(state)).collect(Collectors.toList());
			for (Transition transition : cycleTransitions) {				
				state.getTransitions().remove(transition);
				if (transition.getBlock() != null){
					transition.getBlock().removeFsmComponent(transition);
				}
			}
			
			// agarro todas las transiciones que me apuntas a mi y las apunto a mis destinos
			Iterator<Transition> it = state.getIncomingTransitions().iterator();
			while (it.hasNext()){
				Transition incomingTran = it.next();
				// if this change of transition produce a cycle then ignore this transition.
				if (incomingTran.getFrom().equals(state)){
					continue;
				}
				Iterator<Transition> outit = state.getTransitions().iterator();
				while (outit.hasNext()) {
					Transition outgoingTrans = outit.next();								
					Transition tran;
					try {
						tran = incomingTran.getClass().newInstance();
						tran.setFrom(incomingTran.getFrom());
						tran.setTo(outgoingTrans.getTo());
						tran.setBlock(incomingTran.getBlock());
						tran.setVarIndex(incomingTran.getVarIndex());
						tran.setMessage(incomingTran.getMessage());	
						
						if (outgoingTrans.getTo() instanceof TemporalState && state instanceof TemporalState && incomingTran.getFrom() instanceof OwnerState){
							outgoingTrans.getTo().setVarIndex(state.getVarIndex());
							outgoingTrans.getTo().setVarName(state.getVarName());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} 	
				
		// hago recursion sobre mis destinos. Los obtengo antes de eliminar transiciones.
		List<State> toStates = state.getTransitions().stream().map(t -> t.getTo()).collect(Collectors.toList());
		// limpio las transiciones
		if (state instanceof TemporalState){
			clearStateTransitions(state);
		}
		
		// apply recursion
		for (State toState : toStates) {
			List<Transition> rec = removeTemporalStates(toState, visited);
			finalTrans.addAll(rec);
		}		

		if (!(state instanceof TemporalState)){
			finalTrans.addAll(state.getTransitions());
		}
		return finalTrans;
	}

	private void clearStateTransitions(State state) {
		// saco las transiciones de llegada de los estados a los que voy.
		for (Transition trans : state.getTransitions()) {
			trans.getTo().getIncomingTransitions().remove(trans);
			if (trans.getBlock() != null){
				trans.getBlock().removeFsmComponent(trans);
			}
		}
		// saco las transiciones de salida de los estados de los que vengo
		for (Transition trans : state.getIncomingTransitions()) {
			trans.getFrom().getTransitions().remove(trans);
			if (trans.getBlock() != null){
				trans.getBlock().removeFsmComponent(trans);
			}
		}
		state.setTransitions(null);
		state.setIncomingTransitions(null);
		if (state.getBlock() != null){
			state.getBlock().removeFsmComponent(state);
		}
	}

	private void unificareSameDestinyLambdaTransitions(Transition trans, List<State> visited) {
		
		if (visited.contains(trans.getTo())){
			return;
		}
		
		visited.add(trans.getTo());
		
		// agrupo las transiciones salientes del mismo por varName, que sean de tipo transicion lambda
		Map<String, List<Transition>> groupedTrans = trans.getTo().getTransitions().stream().filter(t -> t instanceof LambdaTransition).collect(Collectors.groupingBy(t -> ((Transition)t).getTo().getVarName()));
		 
		Iterator<Entry<String, List<Transition>>> it = groupedTrans.entrySet().iterator();
		 // por cada grupo hago lo siguiente:
		while (it.hasNext()){
			Entry<String, List<Transition>> groupItem = it.next();
			
			// si el grupo tiene mas de 1 elemento, entonces unifico los elementos del grupo
			if (groupItem.getValue().size() > 1){
				// tomo la primer transicion del grupo
				Transition firstTran = groupItem.getValue().get(0);
				
				// obtengo las demas transiciones del grupo
				List<Transition> others = groupItem.getValue().stream().filter(t -> !t.equals(firstTran)).collect(Collectors.toList());

				// paso todas las transiciones salientes del estado destino de estas transiciones al primer estado
				others.stream().map(t -> t.getTo().getTransitions()).forEach(lt -> {
					lt.forEach(t -> t.setFrom(firstTran.getTo()));
				});
				
				// paso todas las transiciones entrantes al estado destino de estas transiciones al primer estado
				others.stream().map(t -> t.getTo().getIncomingTransitions()).forEach(lt -> {
					lt.forEach(t -> t.setTo(firstTran.getTo()));
				});
				
				// remuevo todas las demas transiciones de la lista
				for (Transition transition : others) {
					transition.getFrom().getTransitions().remove(transition);
					transition.getTo().getIncomingTransitions().remove(transition);
				}
				trans.getTo().getTransitions().removeAll(others);
			}
		}
		// por cada nodo destino
		for (Transition transition : trans.getTo().getTransitions()) {
			// aplico recursion sobre las transiciones del mismo
			unificareSameDestinyLambdaTransitions(transition, visited);
		}		
	}

	private void defineStateSocketNames(List<State> states, MethodBuilder builder) {
		states.forEach(state -> state.setSocketName(getStateAliasName(state, builder)));
	}
	
	private String getStateAliasName(State state, MethodBuilder builder) {
		String alias = builder.getSocketNames().get(state.getVarName());
		
		return alias == null? "" : alias;
	}

	private void doGenerateFsm(Block block, Transition currentTransition, List<Integer> readerVars) {
		
		// si el bloque ya fue visitado, solo tengo que unir la ultima transicion con la primera del bloque
		if (block.getVisited()){
			
			//TODO:pgs: aca tengo que hacer lo siguiente:
			// 1. si tengo una transicion y no hay interferencia con los readerVars, entonces la devuelvo.
			// 2. si no tengo transicion, entonces busco en los hijos.
			// 3. si tengo transicion y hay interferencia, entonces busco en los hijos que no tengan interferencia.
			
			// indico que el estado final de la transicion es el primer estado de la primer transicion del bloque visitado.
			if (currentTransition != null){				
				Transition first = getFirstTransition(block);
				
				if (first != null){
					// es como una transicion lambda
					Transition newTrans = new LambdaTransition();				
					newTrans.setFrom(currentTransition.getTo());					
					newTrans.setTo(first.getFrom());
				}
			}
			return;
		}
						
		// get transition of current block.
		Transition blockTransition = block.getTransition();
		
		if (blockTransition == null || blockTransition.getVarIndex() == null || readerVars.stream().noneMatch( t-> t.equals(blockTransition.getVarIndex()))){
			// mark the block as visited
			block.setVisited(true);			
		}
		
		// if there isn't successors, then go to final state.
		if (block.getSuccessors().entrySet().isEmpty()){
			
			// if block has transition, then add a lambda transition from this to a new final state.
			if (blockTransition != null){
				FinalState finalstate = new FinalState();
				Transition newTrans = new LambdaTransition();
				newTrans.setFrom(blockTransition.getTo());
				newTrans.setTo(finalstate);							
			}
		} else {
			// apply recursion for each successor.
			for (Entry<String, Block> successor : block.getSuccessors().entrySet()) {
				
				// if this successor is a false conditional successor then					
				if (successor.getKey().equals(OpCodesString.getValue(Opcodes.IFEQ)) || blockTransition == null){
					// if exist some reference state in this block, then add a lambda transition.						
					Optional<FsmComponent> reference = block.getFsmComponents().stream().filter(c -> c instanceof ReferenceState).findFirst();
					if (reference.isPresent()){
						ReferenceState rstate = (ReferenceState)reference.get();
						
						LambdaTransition transition = new LambdaTransition();
						transition.setTo(rstate);
						if (currentTransition != null){
							transition.setFrom(currentTransition.getTo());
						}
						
						// call recursion with the new currentTransition
						doGenerateFsm(successor.getValue(), transition, readerVars);							
					} else {
						// call recursion with the currentTransition
						doGenerateFsm(successor.getValue(), currentTransition, readerVars);																		
					}						
				} else {					
					// this successor is a true conditional successor									
					if (blockTransition != null){
						Integer blockReaderVar = blockTransition.getVarIndex();
						
						// si no existe variables ya usadas en las variables del bloque, entonces hago recursion
						if (blockReaderVar == null ||readerVars.stream().noneMatch(p -> p.equals(blockReaderVar))){
							
							List<Integer> newReaderVars = new ArrayList<Integer>(readerVars);
							if (blockReaderVar != null){
								newReaderVars.add(blockReaderVar);
							}
							
							// call recursion with the last transition of this block
							doGenerateFsm(successor.getValue(), blockTransition, newReaderVars);	
							
							if (currentTransition == null){
								currentTransition = blockTransition;
							} else {
								// creo una transicion lambda entre el current transition y el block transition
								Transition lambda = new LambdaTransition();
								lambda.setFrom(currentTransition.getTo());					
								lambda.setTo(blockTransition.getFrom());															
							}
						}
					}
				}
			}	
		}						
	}

	// analizar que pasa si un bloque no tiene transiciones, y tiene mas de un sucesor inmediato con transiciones.
	// creo que este metodo deveria devolver una lista de primeras transiciones, donde la lista debe contener la primer transicion de cada bloque.
	private Transition getFirstTransition(Block block) {
		return doGetFirstTransition(block, new ArrayList<Block>());
	}

	private Transition doGetFirstTransition(Block block, List<Block> visited) {
		Transition toReturn = null;
		// si el bloque tiene transiciones, devuelvo la primera
		if (block.getTransition() != null){
			toReturn = block.getTransition();
		} else {
			visited.add(block);
			// si no tiene transiciones, hago recursion con el primer sucesor del bloque.
			for (Block successor : block.getSuccessors().values()) {
				if (!visited.contains(successor)){
					Transition firstSuccessor = doGetFirstTransition(successor, visited);
					if (firstSuccessor != null){
						toReturn = firstSuccessor;
						break;
					}
				}				
			}
		}
		
		return toReturn;
	}
		
	private void enumerateVertices(MethodBuilder methodBuilder, StringBuilder result) {
		methodBuilder.getBlocks().forEach(block -> {

            String shape = "shape=ellipse";
            String label = String.format("label=\" %s \n %s \"", block.toString(), block.getComponentString());
//            String label = String.format("label=\" %s \"", block.toString());
            label = label.replace("\n" , "\\l");
                        
            result.append( "    " + block.getLabel() + " [" + shape + "," + label + "]\n" );			
		});
		
	}

	private void enumerateEdges(MethodBuilder methodBuilder, StringBuilder result) {
		methodBuilder.getBlocks().forEach(block -> {
			block.getSuccessors().forEach((metadata, successor) -> {
                String style = String.format("[label=\"%s\"]", metadata);     
                result.append( "    " + block.getLabel() + " -> " + successor.getLabel()  + " " + style + "\n" );				
			});
		});
		
	}
	
	// getters and setters
	
	public List<ClassAnalyzer> getClases() {
		return clases;
	}

	public void setClases(List<ClassAnalyzer> clases) {
		this.clases = clases;
	}
	
}
