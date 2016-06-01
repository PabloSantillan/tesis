package com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class OpCodesString {
	private Map<Integer, String> map;

	private OpCodesString(){
		map = new HashMap<Integer, String>();
		map.put(4 , "(ICONST_1)");			
		map.put(17 , "(SIPUSH)");			
		map.put(21 , "(ILOAD)");			
		map.put(25 , "(ALOAD)");	
		map.put(54 , "(ISTORE)");					
		map.put(58 , "(ASTORE)");			
		map.put(89 , "(DUP)"); 		
		map.put(153 , "(IFEQ)"); 		
		map.put(167 , "(GOTO)"); 		
		map.put(172 , "(IRETURN)"); 		
		map.put(173 , "(LRETURN)"); 		
		map.put(174 , "(FRETURN)"); 		
		map.put(175 , "(DRETURN)"); 		
		map.put(176 , "(ARETURN)"); 		
		map.put(177 , "(RETURN)"); 		
		map.put(178 , "(GETSTATIC)"); 		
		map.put(179 , "(PUTSTATIC)"); 		
		map.put(180, "(GETFIELD)");
		map.put(181, "(PUTFIELD)");
		map.put(182, "(INVOKEVIRTUAL)");
		map.put(183, "(INVOKESPECIAL)");		
		map.put(184, "(INVOKESTATIC)");		
		map.put(187, "(NEW)");
		map.put(191, "(ATHROW)");
		map.put(198, "(IFNULL)"); 
		map.put(199, "(IFNONNULL)"); 
	}
	
	public static String getValue(Integer key){
		OpCodesString ops = new OpCodesString();
		if (ops.map.containsKey(key)){
			return ops.map.get(key);
		} else {
			return "(???????)";
		}
	}
}
