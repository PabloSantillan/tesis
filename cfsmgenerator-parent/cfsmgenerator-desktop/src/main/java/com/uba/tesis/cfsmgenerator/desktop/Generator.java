package com.uba.tesis.cfsmgenerator.desktop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.uba.tesis.cfsmgenerator.core.FsmManager;

/**
 * Command-line application to generate a CFSM (in Graphviz DOT format) for a given class.
 * <p>
 * Just run this class without any arguments to see the available command-line
 * options.
 * </p>
 * 
 * @author Pablo Santillan
 */
public class Generator 
{
	private File outputDir;
	private String classToAnalyze;	
	private File[] classPathEntries;
	private boolean verboseOutput;	
	
    public static void main( String[] args )
    {
		final Generator gen = new Generator();
		try {
			applyArgs(gen, args);
			gen.validateArgs();
			FsmManager manager = FsmManager.from(gen.classToAnalyze, gen.classPathEntries, gen.verboseOutput);
			Map<String, String> graphics = manager.buildCFSM();
			gen.renderMaps(graphics);				
		} catch (Exception e) {
			e.printStackTrace();
			printUsage();
		}    	
    }
        
	// private function(s)
    
	private static void applyArgs(Generator main, String[] args) throws Exception {
		for (int i = 0; i < args.length; i++) {
			final String arg = args[i];
			try {
				switch (arg) {
				case "-v":
					main.verboseOutput = true;
					break;
				case "-dir":
					main.outputDir = new File(args[i + 1]);
					i++;
					break;
				case "-search":
					final String pathEntries = args[i + 1];
					main.classPathEntries = new File[1];
					main.classPathEntries[0] = new File(pathEntries);					
					break;
				case "-debug":
					break;
				case "-constructors":
					break;
				case "-match":
					i++;
					break;
				default:
					main.classToAnalyze = arg;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new RuntimeException("Syntax error, failed to access required parameters for option '" + arg + "'", e);
			}
		}
	}
    
	private static void printUsage() {
		System.out
				.println("\n\nUsage: [-debug] [-v] [-constructors] [-search <classpath entries>] [-match <regex>] -dir <directory> <CLASS NAME>\n\n"
						+ "[-debug] => enable debug output\n"
						+ "[-v] => enable verbose output\n"
						+ "[-search <classpath entries> => Substitute for JVM -classpath option since that one does not work with self-executable JARs\n"
						+ "-dir <directory> => outputs .dot files to this directory\n"
						+ "[-constructors] => include constructors in flow analysis\n"
						+ "[-match <regex>] => only analyze methods whose name matches this regex\n"
						+ "<CLASS NAME> => name of class to analyze\n\n");
	}
	
    private void validateArgs() {
		if (outputDir == null) {
			throw new IllegalStateException("No output directory set");
		}
		logVerbose("Output directory: " + outputDir.getAbsolutePath());
		
		if (StringUtils.isBlank(classToAnalyze)) {
			throw new IllegalStateException("Class name not set");
		}		
	}
	
	/**
	 * Render maps in graph form.
	 * 
	 * @param graphics
	 */
	private void renderMaps(Map<String, String> graphics) throws FileNotFoundException{
		graphics.forEach((name, graphic) -> {
			final File outputFile;
			if (outputDir != null) {
				outputFile = new File(outputDir, toFilename(name) + ".dot");
			} else {
				outputFile = new File(toFilename(name) + ".dot");
			}

			logVerbose("Writing " + outputFile.getAbsolutePath());

			if (!outputFile.getParentFile().exists()) {
				outputFile.getParentFile().mkdirs();
			}

			PrintWriter writer = null;
			try {
				writer = new PrintWriter(outputFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			writer.write(graphic);
			writer.close();
		});
	}
	
	private String toFilename(String result) {
		result = result.replace("<", "");
		result = result.replace(">", "");
		result = result.replace("(", "_");
		result = result.replace(")", "_");
		result = result.replace("/", "_");
		result = result.replace(";", "");
		return result;
	}  
	
	private void logVerbose(String s) {
		if (verboseOutput) {
			System.out.println(s);
		}
	}	
}
