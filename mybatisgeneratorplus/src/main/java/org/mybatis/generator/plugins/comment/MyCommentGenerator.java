package org.mybatis.generator.plugins.comment;

import java.util.Properties;
import java.util.Set;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;

public class MyCommentGenerator implements CommentGenerator {

	public void addConfigurationProperties(Properties properties) {
		// TODO Auto-generated method stub
		
	}

	public void addFieldComment(Field field,
			IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		// TODO Auto-generated method stub
	    if (introspectedColumn.getRemarks() != null && !introspectedColumn.getRemarks().equals("")) {
	        field.addJavaDocLine("/**");
	        field.addJavaDocLine(" * " + introspectedColumn.getRemarks());
	       // addJavadocTag(field, false);
	        field.addJavaDocLine(" */");
	    }
	}

	public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
		// TODO Auto-generated method stub

		
	}

	public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

	}

	public void addClassComment(InnerClass innerClass,
			IntrospectedTable introspectedTable) {
		// TODO Auto-generated method stub
		
	}

	public void addClassComment(InnerClass innerClass,
			IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
		// TODO Auto-generated method stub
		
	}

	public void addEnumComment(InnerEnum innerEnum,
			IntrospectedTable introspectedTable) {
		// TODO Auto-generated method stub
		
	}

	public void addGetterComment(Method method,
			IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		// TODO Auto-generated method stub
		
	}

	public void addSetterComment(Method method,
			IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		// TODO Auto-generated method stub
		
	}

	public void addGeneralMethodComment(Method method,
			IntrospectedTable introspectedTable) {
		// TODO Auto-generated method stub
		
	}

	public void addJavaFileComment(CompilationUnit compilationUnit) {
		// TODO Auto-generated method stub
		
	}

	public void addComment(XmlElement xmlElement) {
		// TODO Auto-generated method stub
		
	}

	public void addRootComment(XmlElement rootElement) {
		// TODO Auto-generated method stub
		
	}

	public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

	}

	public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {

	}

	public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

	}

	public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {

	}

	public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

	}
}