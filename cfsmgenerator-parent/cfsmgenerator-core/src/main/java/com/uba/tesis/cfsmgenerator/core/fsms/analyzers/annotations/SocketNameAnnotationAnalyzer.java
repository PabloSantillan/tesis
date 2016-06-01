package com.uba.tesis.cfsmgenerator.core.fsms.analyzers.annotations;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.AnnotationInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.AnnotationValueInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.methods.MethodAnalyzer;

/**
 * 
 * @author Pablo Santillan
 *
 */
public class SocketNameAnnotationAnalyzer extends AnnotationVisitor {

	private MethodAnalyzer methodAnalyzer;
	
    public SocketNameAnnotationAnalyzer(MethodAnalyzer methodAnalyzer) {
		super(Opcodes.ASM5);
		this.methodAnalyzer = methodAnalyzer;
	
    }


	/**
     * Visits a primitive value of the annotation.
     * 
     * @param name
     *            the value name.
     * @param value
     *            the actual value, whose type must be {@link Byte},
     *            {@link Boolean}, {@link Character}, {@link Short},
     *            {@link Integer} , {@link Long}, {@link Float}, {@link Double},
     *            {@link String} or {@link Type} or OBJECT or ARRAY sort. This
     *            value can also be an array of byte, boolean, short, char, int,
     *            long, float or double values (this is equivalent to using
     *            {@link #visitArray visitArray} and visiting each array element
     *            in turn, but is more convenient).
     */
    public void visit(String name, Object value) {
    	System.out.println(String.format("	Ann. Visit. name: %s - value: %s", name, value));
    	this.methodAnalyzer.notifyObservers(new AnnotationValueInstruction(name, value));
    }

    /**
     * Visits an enumeration value of the annotation.
     * 
     * @param name
     *            the value name.
     * @param desc
     *            the class descriptor of the enumeration class.
     * @param value
     *            the actual enumeration value.
     */
    public void visitEnum(String name, String desc, String value) {
    	System.out.println(String.format("	Ann: visit enum. Desc: %s - value: %s", desc, value));
    }

    /**
     * Visits a nested annotation value of the annotation.
     * 
     * @param name
     *            the value name.
     * @param desc
     *            the class descriptor of the nested annotation class.
     * @return a visitor to visit the actual nested annotation value, or
     *         <tt>null</tt> if this visitor is not interested in visiting this
     *         nested annotation. <i>The nested annotation value must be fully
     *         visited before calling other methods on this annotation
     *         visitor</i>.
     */
    public AnnotationVisitor visitAnnotation(String name, String desc) {
    	System.out.println(String.format("	Ann: visit annotation. name: %s - desc: %s", name, desc));

    	this.methodAnalyzer.notifyObservers(new AnnotationInstruction(name, desc));
    	return this;
    }

    /**
     * Visits an array value of the annotation. Note that arrays of primitive
     * types (such as byte, boolean, short, char, int, long, float or double)
     * can be passed as value to {@link #visit visit}. This is what
     * {@link ClassReader} does.
     * 
     * @param name
     *            the value name.
     * @return a visitor to visit the actual array value elements, or
     *         <tt>null</tt> if this visitor is not interested in visiting these
     *         values. The 'name' parameters passed to the methods of this
     *         visitor are ignored. <i>All the array values must be visited
     *         before calling other methods on this annotation visitor</i>.
     */
    public AnnotationVisitor visitArray(String name) {
    	System.out.println(String.format("	Ann: visit aaray. Name: %s", name));
    	return this;
    }

    /**
     * Visits the end of the annotation.
     */
    public void visitEnd() {
    	System.out.println(String.format("	Ann: visit end,"));
    }
}
