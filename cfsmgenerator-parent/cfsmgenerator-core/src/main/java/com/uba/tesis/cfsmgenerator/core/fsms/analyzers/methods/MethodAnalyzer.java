package com.uba.tesis.cfsmgenerator.core.fsms.analyzers.methods;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;

import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.Observable;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.annotations.SocketNameAnnotationAnalyzer;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.CommonInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.FieldInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.Instruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.IntInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.JumpInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.LabelInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.LdcInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.LineNumberInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.LocalVariableInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.MethodInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.OpCodesString;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.TypeInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.analyzers.instructions.VariableInstruction;
import com.uba.tesis.cfsmgenerator.core.fsms.filters.Observer;

/**
 * A java class that analyze methods and notify to observers for relevant instructions.
 * 
 * @author Pablo Santillan
 *
 */
public class MethodAnalyzer extends MethodVisitor implements Observable {

	private List<Observer> observers;
	
	public MethodAnalyzer() {
		super(Opcodes.ASM5);
		
		this.observers = new ArrayList<Observer>();
		
    	System.out.println(String.format("{"));
	}

    // -------------------------------------------------------------------------
    // Parameters, annotations and non standard attributes
    // -------------------------------------------------------------------------

    /**
     * Visits a parameter of this method.
     * 
     * @param name
     *            parameter name or null if none is provided.
     * @param access
     *            the parameter's access flags, only <tt>ACC_FINAL</tt>,
     *            <tt>ACC_SYNTHETIC</tt> or/and <tt>ACC_MANDATED</tt> are
     *            allowed (see {@link Opcodes}).
     */
    public void visitParameter(String name, int access) {
    	System.out.println(String.format("	visitParameter. Name: %s", name));
    }

    /**
     * Visits the default value of this annotation interface method.
     * 
     * @return a visitor to the visit the actual default value of this
     *         annotation interface method, or <tt>null</tt> if this visitor is
     *         not interested in visiting this default value. The 'name'
     *         parameters passed to the methods of this annotation visitor are
     *         ignored. Moreover, exacly one visit method must be called on this
     *         annotation visitor, followed by visitEnd.
     */
    public AnnotationVisitor visitAnnotationDefault() {
    	System.out.println(String.format("	visitAnnotationDefault."));
    	return null;
    }

    /**
     * Visits an annotation of this method.
     * 
     * @param desc
     *            the class descriptor of the annotation class.
     * @param visible
     *            <tt>true</tt> if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or <tt>null</tt> if
     *         this visitor is not interested in visiting this annotation.
     */
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    	System.out.println(String.format("	visitAnnotation. Desc: %s", desc));
    	
    	SocketNameAnnotationAnalyzer analizer = new SocketNameAnnotationAnalyzer(this);
    	return analizer;
    }

    /**
     * Visits an annotation on a type in the method signature.
     * 
     * @param typeRef
     *            a reference to the annotated type. The sort of this type
     *            reference must be {@link TypeReference#METHOD_TYPE_PARAMETER
     *            METHOD_TYPE_PARAMETER},
     *            {@link TypeReference#METHOD_TYPE_PARAMETER_BOUND
     *            METHOD_TYPE_PARAMETER_BOUND},
     *            {@link TypeReference#METHOD_RETURN METHOD_RETURN},
     *            {@link TypeReference#METHOD_RECEIVER METHOD_RECEIVER},
     *            {@link TypeReference#METHOD_FORMAL_PARAMETER
     *            METHOD_FORMAL_PARAMETER} or {@link TypeReference#THROWS
     *            THROWS}. See {@link TypeReference}.
     * @param typePath
     *            the path to the annotated type argument, wildcard bound, array
     *            element type, or static inner type within 'typeRef'. May be
     *            <tt>null</tt> if the annotation targets 'typeRef' as a whole.
     * @param desc
     *            the class descriptor of the annotation class.
     * @param visible
     *            <tt>true</tt> if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or <tt>null</tt> if
     *         this visitor is not interested in visiting this annotation.
     */
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
    	System.out.println(String.format("	visitTypeAnnotation. Desc: %s", desc));
        return null;
    }

    /**
     * Visits an annotation of a parameter this method.
     * 
     * @param parameter
     *            the parameter index.
     * @param desc
     *            the class descriptor of the annotation class.
     * @param visible
     *            <tt>true</tt> if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or <tt>null</tt> if
     *         this visitor is not interested in visiting this annotation.
     */
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
    	System.out.println(String.format("	visitParameterAnnotation. Desc: %s", desc));
        return null;
    }

    /**
     * Visits a non standard attribute of this method.
     * 
     * @param attr
     *            an attribute.
     */
    public void visitAttribute(Attribute attr) {
    	System.out.println(String.format("	visitAttribute."));
    }

    /**
     * Starts the visit of the method's code, if any (i.e. non abstract method).
     */
    public void visitCode() {
    	System.out.println(String.format("	visitCode."));
    }

    /**
     * Visits the current state of the local variables and operand stack
     * elements. This method must(*) be called <i>just before</i> any
     * instruction <b>i</b> that follows an unconditional branch instruction
     * such as GOTO or THROW, that is the target of a jump instruction, or that
     * starts an exception handler block. The visited types must describe the
     * values of the local variables and of the operand stack elements <i>just
     * before</i> <b>i</b> is executed.<br>
     * <br>
     * (*) this is mandatory only for classes whose version is greater than or
     * equal to {@link Opcodes#V1_6 V1_6}. <br>
     * <br>
     * The frames of a method must be given either in expanded form, or in
     * compressed form (all frames must use the same format, i.e. you must not
     * mix expanded and compressed frames within a single method):
     * <ul>
     * <li>In expanded form, all frames must have the F_NEW type.</li>
     * <li>In compressed form, frames are basically "deltas" from the state of
     * the previous frame:
     * <ul>
     * <li>{@link Opcodes#F_SAME} representing frame with exactly the same
     * locals as the previous frame and with the empty stack.</li>
     * <li>{@link Opcodes#F_SAME1} representing frame with exactly the same
     * locals as the previous frame and with single value on the stack (
     * <code>nStack</code> is 1 and <code>stack[0]</code> contains value for the
     * type of the stack item).</li>
     * <li>{@link Opcodes#F_APPEND} representing frame with current locals are
     * the same as the locals in the previous frame, except that additional
     * locals are defined (<code>nLocal</code> is 1, 2 or 3 and
     * <code>local</code> elements contains values representing added types).</li>
     * <li>{@link Opcodes#F_CHOP} representing frame with current locals are the
     * same as the locals in the previous frame, except that the last 1-3 locals
     * are absent and with the empty stack (<code>nLocals</code> is 1, 2 or 3).</li>
     * <li>{@link Opcodes#F_FULL} representing complete frame data.</li>
     * </ul>
     * </li>
     * </ul>
     * <br>
     * In both cases the first frame, corresponding to the method's parameters
     * and access flags, is implicit and must not be visited. Also, it is
     * illegal to visit two or more frames for the same code location (i.e., at
     * least one instruction must be visited between two calls to visitFrame).
     * 
     * @param type
     *            the type of this stack map frame. Must be
     *            {@link Opcodes#F_NEW} for expanded frames, or
     *            {@link Opcodes#F_FULL}, {@link Opcodes#F_APPEND},
     *            {@link Opcodes#F_CHOP}, {@link Opcodes#F_SAME} or
     *            {@link Opcodes#F_APPEND}, {@link Opcodes#F_SAME1} for
     *            compressed frames.
     * @param nLocal
     *            the number of local variables in the visited frame.
     * @param local
     *            the local variable types in this frame. This array must not be
     *            modified. Primitive types are represented by
     *            {@link Opcodes#TOP}, {@link Opcodes#INTEGER},
     *            {@link Opcodes#FLOAT}, {@link Opcodes#LONG},
     *            {@link Opcodes#DOUBLE},{@link Opcodes#NULL} or
     *            {@link Opcodes#UNINITIALIZED_THIS} (long and double are
     *            represented by a single element). Reference types are
     *            represented by String objects (representing internal names),
     *            and uninitialized types by Label objects (this label
     *            designates the NEW instruction that created this uninitialized
     *            value).
     * @param nStack
     *            the number of operand stack elements in the visited frame.
     * @param stack
     *            the operand stack types in this frame. This array must not be
     *            modified. Its content has the same format as the "local"
     *            array.
     * @throws IllegalStateException
     *             if a frame is visited just after another one, without any
     *             instruction between the two (unless this frame is a
     *             Opcodes#F_SAME frame, in which case it is silently ignored).
     */
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
    	String frameType = "";
    	switch (type) {
		case -1:			
			frameType = "F_NEW";//Opcodes.F_NEW
			break;
		case 0:			
			frameType = "F_FULL";//Opcodes.F_FULL
			break;
		case 1:			
			frameType = "F_APPEND";//Opcodes.F_APPEND
			break;
		case 2:			
			frameType = "F_CHOP";//Opcodes.F_CHOP
			break;
		case 3:			
			frameType = "F_SAME";//Opcodes.F_SAME
			break;
		case 4:
			frameType = "F_SAME1";//Opcodes.F_SAME1
			break;
		}
    	System.out.println(String.format("	visitFrame. Type: %s %s - # Local var visited: %s - local: %s - # operands Stack: %s - stack: %s", 
    			type, frameType, nLocal, local, nStack, stack));
    }

    // -------------------------------------------------------------------------
    // Normal instructions
    // -------------------------------------------------------------------------

    /**
     * Visits a zero operand instruction.
     * 
     * @param opcode
     *            the opcode of the instruction to be visited. This opcode is
     *            either NOP, ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1,
     *            ICONST_2, ICONST_3, ICONST_4, ICONST_5, LCONST_0, LCONST_1,
     *            FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1, IALOAD,
     *            LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD,
     *            IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE,
     *            SASTORE, POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1,
     *            DUP2_X2, SWAP, IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB,
     *            IMUL, LMUL, FMUL, DMUL, IDIV, LDIV, FDIV, DDIV, IREM, LREM,
     *            FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR,
     *            IUSHR, LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR, I2L, I2F, I2D,
     *            L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C, I2S,
     *            LCMP, FCMPL, FCMPG, DCMPL, DCMPG, IRETURN, LRETURN, FRETURN,
     *            DRETURN, ARETURN, RETURN, ARRAYLENGTH, ATHROW, MONITORENTER,
     *            or MONITOREXIT.
     */
    public void visitInsn(int opcode) {
    	System.out.println(String.format("	visitInsn. OpCode: %s %s", opcode, OpCodesString.getValue(opcode)));
    	this.notifyObservers(new CommonInstruction(opcode));
    	super.visitInsn(opcode);
    }

    /**
     * Visits an instruction with a single int operand.
     * 
     * @param opcode
     *            the opcode of the instruction to be visited. This opcode is
     *            either BIPUSH, SIPUSH or NEWARRAY.
     * @param operand
     *            the operand of the instruction to be visited.<br>
     *            When opcode is BIPUSH, operand value should be between
     *            Byte.MIN_VALUE and Byte.MAX_VALUE.<br>
     *            When opcode is SIPUSH, operand value should be between
     *            Short.MIN_VALUE and Short.MAX_VALUE.<br>
     *            When opcode is NEWARRAY, operand value should be one of
     *            {@link Opcodes#T_BOOLEAN}, {@link Opcodes#T_CHAR},
     *            {@link Opcodes#T_FLOAT}, {@link Opcodes#T_DOUBLE},
     *            {@link Opcodes#T_BYTE}, {@link Opcodes#T_SHORT},
     *            {@link Opcodes#T_INT} or {@link Opcodes#T_LONG}.
     */
    public void visitIntInsn(int opcode, int operand) {
    	System.out.println(String.format("	visitIntInsn. Operand: %s - OpCode: %s %s", operand, opcode, OpCodesString.getValue(opcode)));
    	this.notifyObservers(new IntInstruction(opcode, operand));
    	super.visitIntInsn(opcode, operand);
    }

    /**
     * Visits a local variable instruction. A local variable instruction is an
     * instruction that loads or stores the value of a local variable.
     * 
     * @param opcode
     *            the opcode of the local variable instruction to be visited.
     *            This opcode is either ILOAD, LLOAD, FLOAD, DLOAD, ALOAD,
     *            ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET.
     * @param var
     *            the operand of the instruction to be visited. This operand is
     *            the index of a local variable.
     */
    public void visitVarInsn(int opcode, int var) {
    	System.out.println(String.format("	visitVarInsn. Var: %s - Opcode: %s %s", var, opcode, OpCodesString.getValue(opcode)));
    	
    	this.notifyObservers(new VariableInstruction(var, opcode));
    	super.visitVarInsn(opcode, var);
    }

    /**
     * Visits a type instruction. A type instruction is an instruction that
     * takes the internal name of a class as parameter.
     * 
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either NEW, ANEWARRAY, CHECKCAST or INSTANCEOF.
     * @param type
     *            the operand of the instruction to be visited. This operand
     *            must be the internal name of an object or array class (see
     *            {@link Type#getInternalName() getInternalName}).
     */
    public void visitTypeInsn(int opcode, String type) {
    	System.out.println(String.format("	visitTypeInsn. Type: %s - OpCode: %s %s", type, opcode, OpCodesString.getValue(opcode)));    	
    	this.notifyObservers(new TypeInstruction(opcode, type));
    	super.visitTypeInsn(opcode, type);
    }

    /**
     * Visits a field instruction. A field instruction is an instruction that
     * loads or stores the value of a field of an object.
     * 
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
     * @param owner
     *            the internal name of the field's owner class (see
     *            {@link Type#getInternalName() getInternalName}).
     * @param name
     *            the field's name.
     * @param desc
     *            the field's descriptor (see {@link Type Type}).
     */
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
    	System.out.println(String.format("	visitFieldInsn. Name: %s - Desc: %s - Owner: %s - OpCode: %s %s", name, desc, owner, opcode, OpCodesString.getValue(opcode)));
    	
    	this.notifyObservers(new FieldInstruction(opcode, name, owner, desc));
    	super.visitFieldInsn(opcode, owner, name, desc);
    }

    /**
     * Visits a method instruction. A method instruction is an instruction that
     * invokes a method.
     * 
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or
     *            INVOKEINTERFACE.
     * @param owner
     *            the internal name of the method's owner class (see
     *            {@link Type#getInternalName() getInternalName}).
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link Type Type}).
     */
    @Deprecated
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
    	System.out.println(String.format("	visitMethodInsn. Name: %s - Desc: %s - Owner: %s - OpCode: %s", name, desc, owner, opcode));
    }

    /**
     * Visits a method instruction. A method instruction is an instruction that
     * invokes a method.
     * 
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or
     *            INVOKEINTERFACE.
     * @param owner
     *            the internal name of the method's owner class (see
     *            {@link Type#getInternalName() getInternalName}).
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link Type Type}).
     * @param itf
     *            if the method's owner class is an interface.
     */
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
    	System.out.println(String.format("	visitMethodInsn. Name: %s - Desc: %s - Owner: %s - OpCode: %s %s", name, desc, owner, opcode, OpCodesString.getValue(opcode)));
    	
    	this.notifyObservers(new MethodInstruction(opcode, name, owner, desc));    	
    	super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    /**
     * Visits an invokedynamic instruction.
     * 
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link Type Type}).
     * @param bsm
     *            the bootstrap method.
     * @param bsmArgs
     *            the bootstrap method constant arguments. Each argument must be
     *            an {@link Integer}, {@link Float}, {@link Long},
     *            {@link Double}, {@link String}, {@link Type} or {@link Handle}
     *            value. This method is allowed to modify the content of the
     *            array so a caller should expect that this array may change.
     */
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
    	System.out.println(String.format("	visitInvokeDynamicInsn. Name: %s - Desc: %s", name, desc));
    	super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    /**
     * Visits a jump instruction. A jump instruction is an instruction that may
     * jump to another instruction.
     * 
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ,
     *            IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE,
     *            IF_ACMPEQ, IF_ACMPNE, GOTO, JSR, IFNULL or IFNONNULL.
     * @param label
     *            the operand of the instruction to be visited. This operand is
     *            a label that designates the instruction to which the jump
     *            instruction may jump.
     */
    public void visitJumpInsn(int opcode, Label label) {
    	System.out.println(String.format("	visitJumpInsn. Opcode: %s %s - Label: %s", opcode, OpCodesString.getValue(opcode), label));
    	super.visitJumpInsn(opcode, label);
    	
    	this.notifyObservers(new JumpInstruction(opcode, label.toString()));
    }

    /**
     * Visits a label. A label designates the instruction that will be visited
     * just after it.
     * 
     * @param label
     *            a {@link Label Label} object.
     */
    public void visitLabel(Label label) {
    	System.out.println("\n");
    	System.out.println(String.format("	visitLabel. Label: %s", label.toString()));
    	
    	this.notifyObservers(new LabelInstruction(label.toString()));
    	super.visitLabel(label);
    }

    // -------------------------------------------------------------------------
    // Special instructions
    // -------------------------------------------------------------------------

    /**
     * Visits a LDC instruction. Note that new constant types may be added in
     * future versions of the Java Virtual Machine. To easily detect new
     * constant types, implementations of this method should check for
     * unexpected constant types, like this:
     * 
     * <pre>
     * if (cst instanceof Integer) {
     *     // ...
     * } else if (cst instanceof Float) {
     *     // ...
     * } else if (cst instanceof Long) {
     *     // ...
     * } else if (cst instanceof Double) {
     *     // ...
     * } else if (cst instanceof String) {
     *     // ...
     * } else if (cst instanceof Type) {
     *     int sort = ((Type) cst).getSort();
     *     if (sort == Type.OBJECT) {
     *         // ...
     *     } else if (sort == Type.ARRAY) {
     *         // ...
     *     } else if (sort == Type.METHOD) {
     *         // ...
     *     } else {
     *         // throw an exception
     *     }
     * } else if (cst instanceof Handle) {
     *     // ...
     * } else {
     *     // throw an exception
     * }
     * </pre>
     * 
     * @param cst
     *            the constant to be loaded on the stack. This parameter must be
     *            a non null {@link Integer}, a {@link Float}, a {@link Long}, a
     *            {@link Double}, a {@link String}, a {@link Type} of OBJECT or
     *            ARRAY sort for <tt>.class</tt> constants, for classes whose
     *            version is 49.0, a {@link Type} of METHOD sort or a
     *            {@link Handle} for MethodType and MethodHandle constants, for
     *            classes whose version is 51.0.
     */
    public void visitLdcInsn(Object cst) {
    	System.out.println(String.format("	visitLdcInsn. Cst: %s", cst.toString()));
    	
    	this.notifyObservers(new LdcInstruction(cst.toString()));
    	super.visitLdcInsn(cst);
    }

    /**
     * Visits an IINC instruction.
     * 
     * @param var
     *            index of the local variable to be incremented.
     * @param increment
     *            amount to increment the local variable by.
     */
    public void visitIincInsn(int var, int increment) {
    	System.out.println(String.format("	visitIincInsn."));
    	super.visitIincInsn(var, increment);
    }

    /**
     * Visits a TABLESWITCH instruction.
     * 
     * @param min
     *            the minimum key value.
     * @param max
     *            the maximum key value.
     * @param dflt
     *            beginning of the default handler block.
     * @param labels
     *            beginnings of the handler blocks. <tt>labels[i]</tt> is the
     *            beginning of the handler block for the <tt>min + i</tt> key.
     */
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
    	System.out.println(String.format("	visitTableSwitchInsn."));
    	super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    /**
     * Visits a LOOKUPSWITCH instruction.
     * 
     * @param dflt
     *            beginning of the default handler block.
     * @param keys
     *            the values of the keys.
     * @param labels
     *            beginnings of the handler blocks. <tt>labels[i]</tt> is the
     *            beginning of the handler block for the <tt>keys[i]</tt> key.
     */
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
    	System.out.println(String.format("	visitLookupSwitchInsn."));
    	super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    /**
     * Visits a MULTIANEWARRAY instruction.
     * 
     * @param desc
     *            an array type descriptor (see {@link Type Type}).
     * @param dims
     *            number of dimensions of the array to allocate.
     */
    public void visitMultiANewArrayInsn(String desc, int dims) {
    	System.out.println(String.format("	visitMultiANewArrayInsn. Desc: %s", desc));
    	super.visitMultiANewArrayInsn(desc, dims);
    }

    /**
     * Visits an annotation on an instruction. This method must be called just
     * <i>after</i> the annotated instruction. It can be called several times
     * for the same instruction.
     * 
     * @param typeRef
     *            a reference to the annotated type. The sort of this type
     *            reference must be {@link TypeReference#INSTANCEOF INSTANCEOF},
     *            {@link TypeReference#NEW NEW},
     *            {@link TypeReference#CONSTRUCTOR_REFERENCE
     *            CONSTRUCTOR_REFERENCE}, {@link TypeReference#METHOD_REFERENCE
     *            METHOD_REFERENCE}, {@link TypeReference#CAST CAST},
     *            {@link TypeReference#CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT
     *            CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT},
     *            {@link TypeReference#METHOD_INVOCATION_TYPE_ARGUMENT
     *            METHOD_INVOCATION_TYPE_ARGUMENT},
     *            {@link TypeReference#CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT
     *            CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT}, or
     *            {@link TypeReference#METHOD_REFERENCE_TYPE_ARGUMENT
     *            METHOD_REFERENCE_TYPE_ARGUMENT}. See {@link TypeReference}.
     * @param typePath
     *            the path to the annotated type argument, wildcard bound, array
     *            element type, or static inner type within 'typeRef'. May be
     *            <tt>null</tt> if the annotation targets 'typeRef' as a whole.
     * @param desc
     *            the class descriptor of the annotation class.
     * @param visible
     *            <tt>true</tt> if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or <tt>null</tt> if
     *         this visitor is not interested in visiting this annotation.
     */
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
    	System.out.println(String.format("	visitInsnAnnotation. Desc: %s", desc));
        return super.visitInsnAnnotation(typeRef, typePath, desc, visible);
    }

    // -------------------------------------------------------------------------
    // Exceptions table entries, debug information, max stack and max locals
    // -------------------------------------------------------------------------

    /**
     * Visits a try catch block.
     * 
     * @param start
     *            beginning of the exception handler's scope (inclusive).
     * @param end
     *            end of the exception handler's scope (exclusive).
     * @param handler
     *            beginning of the exception handler's code.
     * @param type
     *            internal name of the type of exceptions handled by the
     *            handler, or <tt>null</tt> to catch any exceptions (for
     *            "finally" blocks).
     * @throws IllegalArgumentException
     *             if one of the labels has already been visited by this visitor
     *             (by the {@link #visitLabel visitLabel} method).
     */
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
    	System.out.println(String.format("	visitTryCatchBlock."));
    }

    /**
     * Visits an annotation on an exception handler type. This method must be
     * called <i>after</i> the {@link #visitTryCatchBlock} for the annotated
     * exception handler. It can be called several times for the same exception
     * handler.
     * 
     * @param typeRef
     *            a reference to the annotated type. The sort of this type
     *            reference must be {@link TypeReference#EXCEPTION_PARAMETER
     *            EXCEPTION_PARAMETER}. See {@link TypeReference}.
     * @param typePath
     *            the path to the annotated type argument, wildcard bound, array
     *            element type, or static inner type within 'typeRef'. May be
     *            <tt>null</tt> if the annotation targets 'typeRef' as a whole.
     * @param desc
     *            the class descriptor of the annotation class.
     * @param visible
     *            <tt>true</tt> if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or <tt>null</tt> if
     *         this visitor is not interested in visiting this annotation.
     */
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
    	System.out.println(String.format("	visitTryCatchAnnotation. Desc: %s", desc));
    	return super.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
    }

    /**
     * Visits a local variable declaration.
     * 
     * @param name
     *            the name of a local variable.
     * @param desc
     *            the type descriptor of this local variable.
     * @param signature
     *            the type signature of this local variable. May be
     *            <tt>null</tt> if the local variable type does not use generic
     *            types.
     * @param start
     *            the first instruction corresponding to the scope of this local
     *            variable (inclusive).
     * @param end
     *            the last instruction corresponding to the scope of this local
     *            variable (exclusive).
     * @param index
     *            the local variable's index.
     * @throws IllegalArgumentException
     *             if one of the labels has not already been visited by this
     *             visitor (by the {@link #visitLabel visitLabel} method).
     */
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
    	System.out.println(String.format("	visitLocalVariable. Name: %s - Desc: %s - Signature: %s - Start: %s - End: %s - Index: %s", name, desc, signature, start.info, end.info, index));
    	
    	this.notifyObservers(new LocalVariableInstruction(index, name, desc));
    }

    /**
     * Visits an annotation on a local variable type.
     * 
     * @param typeRef
     *            a reference to the annotated type. The sort of this type
     *            reference must be {@link TypeReference#LOCAL_VARIABLE
     *            LOCAL_VARIABLE} or {@link TypeReference#RESOURCE_VARIABLE
     *            RESOURCE_VARIABLE}. See {@link TypeReference}.
     * @param typePath
     *            the path to the annotated type argument, wildcard bound, array
     *            element type, or static inner type within 'typeRef'. May be
     *            <tt>null</tt> if the annotation targets 'typeRef' as a whole.
     * @param start
     *            the fist instructions corresponding to the continuous ranges
     *            that make the scope of this local variable (inclusive).
     * @param end
     *            the last instructions corresponding to the continuous ranges
     *            that make the scope of this local variable (exclusive). This
     *            array must have the same size as the 'start' array.
     * @param index
     *            the local variable's index in each range. This array must have
     *            the same size as the 'start' array.
     * @param desc
     *            the class descriptor of the annotation class.
     * @param visible
     *            <tt>true</tt> if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or <tt>null</tt> if
     *         this visitor is not interested in visiting this annotation.
     */
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
    	System.out.println(String.format("	visitLocalVariableAnnotation."));
        return null;
    }

    /**
     * Visits a line number declaration.
     * 
     * @param line
     *            a line number. This number refers to the source file from
     *            which the class was compiled.
     * @param start
     *            the first instruction corresponding to this line number.
     * @throws IllegalArgumentException
     *             if <tt>start</tt> has not already been visited by this
     *             visitor (by the {@link #visitLabel visitLabel} method).
     */
    public void visitLineNumber(int line, Label start) {
    	System.out.println(String.format("	visitLineNumber. Line: %s - Label: %s", line, start.toString()));
    	this.notifyObservers(new LineNumberInstruction(line));
    }

    /**
     * Visits the maximum stack size and the maximum number of local variables
     * of the method.
     * 
     * @param maxStack
     *            maximum stack size of the method.
     * @param maxLocals
     *            maximum number of local variables for the method.
     */
    public void visitMaxs(int maxStack, int maxLocals) {
    	System.out.println(String.format("	visitMaxs."));
    }

    /**
     * Visits the end of the method. This method, which is the last one to be
     * called, is used to inform the visitor that all the annotations and
     * attributes of the method have been visited.
     */
    public void visitEnd() {	
    	System.out.println(String.format("	visitEnd."));
    	System.out.println(String.format("}"));
    }

	@Override
	public void notifyObservers(Instruction instruction) {
		for (Observer observer : observers) {
			observer.notify(instruction);
		}
	}

	@Override
	public void register(Observer observer) {
		this.observers.add(observer);		
	}
		
	/// getters and setters	
}
