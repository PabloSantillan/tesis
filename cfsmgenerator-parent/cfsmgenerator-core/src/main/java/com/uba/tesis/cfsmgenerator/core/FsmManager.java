package com.uba.tesis.cfsmgenerator.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.clases.ClassAnalyzer;
import com.uba.tesis.cfsmgenerator.core.util.ASMUtil;
import com.uba.tesis.cfsmgenerator.core.util.Fsm;

/**
 * A java class manager to build a FSM associated to class passed as parameter.
 * 
 * @author Pablo Santillan
 */
public class FsmManager {
	private File[] classPathEntries;
	private String classToAnalyze;	
	private boolean verboseOutput;
	private List<ClassAnalyzer> clases = new ArrayList<ClassAnalyzer>();

	private FsmManager() {
	}

	/**
	 * Constructs a new {@link FsmManager} object.
	 * 
	 * @param classToAnalyze is
	 * 							the class from with will generate the FSM.
	 * @param classPathEntries is
	 * 								the path where the classToAnalyze is located.
	 * @param verboseOutput 
	 * 						indicate if the process is logged.
	 * @return the new object
	 */
	public static FsmManager from(String classToAnalyze, File[] classPathEntries, boolean verboseOutput){
		FsmManager manager = new FsmManager();
		manager.classToAnalyze = classToAnalyze;
		manager.classPathEntries = classPathEntries;
		manager.verboseOutput = verboseOutput;		
		return manager;
	}
	
	/**
	 * Build CFSM for each method declared in the class.
	 * Invoke {@link ClassReader#accept(org.objectweb.asm.ClassVisitor, int)} to visit the class.
	 * 
	 * @throws IOException
	 * @throws AnalyzerException
	 * @throws FileNotFoundException
	 * 
	 * @see {@link ClassReader}
	 */
	public Map<String, String> buildCFSM() throws IOException, AnalyzerException, FileNotFoundException {

		// create a reader to class to analyze.
		final ClassReader classReader = ASMUtil.createClassReader(classToAnalyze, classPathEntries, this::logVerbose);
		
		final ClassAnalyzer classAnalyser = new ClassAnalyzer();
		classReader.accept(classAnalyser, 0);
		
		// add analyzed class to list of analyzed classes.
		this.clases.add(classAnalyser);

		// for each inner class, analyze the class and add to list of analyzed classes.
		for (String innerClass : classAnalyser.getInnerClasess()) {
			visitInnerClass(innerClass);
		}
		
		// generate FSMs.
		FsmGenerator fsmGenerator = FsmGenerator.from(this.clases); 
		List<Fsm> fsms = fsmGenerator.generate();
		
		// convert FSMs to graphics
		Map<String, String> graphics = FsmGrapher.from(fsms).render();		
		
		// this is only for preview flow.
		Map<String, String> previews = fsmGenerator.renderTransitions(); 		
		graphics.putAll(previews);
		
		return graphics;		
	}
	
	// private function(s)

	/**
	 * visit inner class associated to a <code>class<code>.
	 * @param innerClass
	 * @throws IOException
	 */
	private void visitInnerClass(String innerClass) throws IOException {
		// create a reader to class to analyze.
		final ClassReader classReader = ASMUtil.createClassReader(innerClass, classPathEntries, this::logVerbose);

		final ClassAnalyzer fsm = new ClassAnalyzer();
		classReader.accept(fsm, 0);
		
		// add inner class to analyze to list of analyzed classes
		this.clases.add(fsm);		
	}
		
	private void logVerbose(String s) {
		if (verboseOutput) {
			System.out.println(s);
		}
	}
}