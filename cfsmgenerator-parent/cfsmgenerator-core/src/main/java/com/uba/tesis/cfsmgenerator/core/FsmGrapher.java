package com.uba.tesis.cfsmgenerator.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uba.tesis.cfsmgenerator.core.fsms.fsm.states.State;
import com.uba.tesis.cfsmgenerator.core.fsms.fsm.transitions.Transition;
import com.uba.tesis.cfsmgenerator.core.util.Fsm;

/**
 * A java class that convert a list of {@link Fsm} entities to a list of graphics
 * @author Pablo Santillan
 *
 */
public class FsmGrapher {
	private List<Fsm> fsms;
	
	private FsmGrapher(){		
	}
	
	/**
	 * Constructs a new {@link FsmGrapher} object.
	 * 
	 * @param fsms is
	 * 					a list of {@link Fsm} entities to be converted
	 * 
	 * @return the new object
	 */
	public static FsmGrapher from(List<Fsm> fsms) {
		FsmGrapher grapher = new FsmGrapher();
		grapher.setFsms(fsms);		
		return grapher;
	}
	
	/**
	 * Convert fsm list to <code>map<code> of (class name) -> graphic.
	 * 
	 * @return <code>map<code> of graphics
	 */
	public Map<String, String> render(){
		Map<String, String> graphics = new HashMap<String, String>();
		
		fsms.forEach(fsm -> {
			String classMethodName = String.format("fsm_%s_%s", fsm.getClassName(), fsm.getBuilderName());
	        final StringBuilder result = new StringBuilder(String.format("digraph \"%s()\" {\n", classMethodName));
	        		        
	        enumerateVertices(fsm.getStates(), result);        
	        enumerateEdges(fsm.getTransitions(), result);
			
	        result.append("}");
	        
	        String graphic = result.toString();
	        graphics.put(classMethodName, graphic);							
		});
		
		return graphics;
	}
	
	// private function(s)

	private void enumerateVertices(List<State> states, StringBuilder result) {
		int i = 0;
		for (State state : states) {
			i++;
			state.setKey(i);
            String shape = "shape=ellipse";
            String label = String.format("label=\" %s \"", state.getSocketName());
            label = label.replace("\n" , "\\l");
                        
            result.append( "    " + state.getKey() + " [" + shape + "," + label + "]\n" );						
		}
	}

	private void enumerateEdges(List<Transition> transitions, StringBuilder result) {
		transitions.forEach(trans -> {
            String style = String.format("[label=\"%s\"]", trans.getMessage());     
            result.append( "    " + trans.getFrom().getKey() + " -> " + trans.getTo().getKey()  + " " + style + "\n" );				
		});		
	}
	
	// getters and setters
	
	public List<Fsm> getFsms() {
		return fsms;
	}

	public void setFsms(List<Fsm> fsms) {
		this.fsms = fsms;
	}
}
