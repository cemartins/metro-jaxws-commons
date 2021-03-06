/**
 * Copyright (c) 2006-2007, Magnetosoft, LLC
 * All rights reserved.
 * 
 * Licensed under the Magnetosoft License. You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.magnetosoft.ru/LICENSE
 *
 * file: ASMResponseBeanGenerator.java
 */

package org.jvnet.jax_ws_commons.beans_generator.ambassador.impl.asm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jvnet.jax_ws_commons.beans_generator.ContextJAXWSUtils;
import org.jvnet.jax_ws_commons.beans_generator.ambassador.impl.asm.ASMAmbassadorGenerator.MethodInfo;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;


/**
 * Created: 09.06.2007
 * @author Malyshkin Fedor (fedor.malyshkin@magnetosoft.ru)
 * @version $Revision: 240 $
 */
public class ASMResponseBeanGenerator {
    private ClassWriter writer = null;

    private MethodInfo methodInfo = null;

    public byte[] generate() {
	ClassNode tree = new ClassNode();

	tree.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, methodInfo.getResponseBeanClassName().replace(".", "/"), null, Type.getInternalName(Object.class), new String[0]);

	generateNoArgsConstructor();
	generateAnnotations();
	generateFields();

	tree.visitEnd();
	tree.accept(writer);
	return writer.toByteArray();
    }

    /**
     * @param writer
     * @param methodInfo
     */
    public ASMResponseBeanGenerator(ClassWriter writer, MethodInfo methodInfo) {
	super();
	this.writer = writer;
	this.methodInfo = methodInfo;
    }

    /**
     * @param info
     */
    public ASMResponseBeanGenerator(MethodInfo info) {
	this(ASMUtil.createClassWriter(), info);
    }

    private void generateNoArgsConstructor() {
	MethodVisitor mv =
		writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
	mv.visitCode();
	mv.visitVarInsn(Opcodes.ALOAD, 0);
	mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
	mv.visitInsn(Opcodes.RETURN);
	mv.visitMaxs(1, 1);
	mv.visitEnd();
    }

    private void generateAnnotations() {
	AnnotationVisitor av =
		writer.visitAnnotation(Type.getDescriptor(XmlRootElement.class), true);
	//av = ASMUtil.wrapIntoCheckedVisitor(av);
	av.visit("name", ContextJAXWSUtils.mapXMLNameToJavaName(methodInfo.getResponseName()));
	av.visit("namespace", methodInfo.getResponseNS());
	av.visitEnd();

	av = writer.visitAnnotation(Type.getDescriptor(XmlType.class), true);
	//av = ASMUtil.wrapIntoCheckedVisitor(av);
	av.visit("name", ContextJAXWSUtils.mapXMLNameToJavaName(methodInfo.getResponseName()));
	av.visit("namespace", methodInfo.getResponseNS());
	av.visitEnd();

	av =
		writer.visitAnnotation(Type.getDescriptor(XmlAccessorType.class), true);
	//av = ASMUtil.wrapIntoCheckedVisitor(av);
	av.visitEnum("value", Type.getDescriptor(XmlAccessType.class), XmlAccessType.FIELD.toString());
	av.visitEnd();
    }

    private void generateFields() {
	if (methodInfo.getReturnType().getSort() != Type.VOID) {
	    FieldVisitor fv =
		    writer.visitField(Opcodes.ACC_PUBLIC, "_return", methodInfo.getReturnType().getDescriptor(), null, null);
	    AnnotationVisitor av =
		    fv.visitAnnotation(Type.getDescriptor(XmlElement.class), true);
	    av.visit("name", "return");
	    av.visit("namespace", "");
	    av.visitEnd();
	    fv.visitEnd();
	}

    }

}
