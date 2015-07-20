/**
 * Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Elie Richa - initial implementation
 */
package fr.tpt.atlanalyser.atl.OCL;

import fr.tpt.atlanalyser.atl.ATL.LocatedElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ocl Expression</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getType <em>Type</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getIfExp3 <em>If Exp3</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getAppliedProperty <em>Applied Property</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getCollection <em>Collection</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getLetExp <em>Let Exp</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getLoopExp <em>Loop Exp</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getParentOperation <em>Parent Operation</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getInitializedVariable <em>Initialized Variable</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getIfExp2 <em>If Exp2</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getOwningOperation <em>Owning Operation</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getIfExp1 <em>If Exp1</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getOwningAttribute <em>Owning Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclExpression()
 * @model abstract="true"
 * @generated
 */
public interface OclExpression extends LocatedElement {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Type</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclType#getOclExpression <em>Ocl Expression</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' containment reference.
     * @see #setType(OclType)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclExpression_Type()
     * @see fr.tpt.atlanalyser.atl.OCL.OclType#getOclExpression
     * @model opposite="oclExpression" containment="true" ordered="false"
     * @generated
     */
    OclType getType();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getType <em>Type</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' containment reference.
     * @see #getType()
     * @generated
     */
    void setType(OclType value);

    /**
     * Returns the value of the '<em><b>If Exp3</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.IfExp#getElseExpression <em>Else Expression</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>If Exp3</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>If Exp3</em>' container reference.
     * @see #setIfExp3(IfExp)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclExpression_IfExp3()
     * @see fr.tpt.atlanalyser.atl.OCL.IfExp#getElseExpression
     * @model opposite="elseExpression" transient="false" ordered="false"
     * @generated
     */
    IfExp getIfExp3();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getIfExp3 <em>If Exp3</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>If Exp3</em>' container reference.
     * @see #getIfExp3()
     * @generated
     */
    void setIfExp3(IfExp value);

    /**
     * Returns the value of the '<em><b>Applied Property</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.PropertyCallExp#getSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Applied Property</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Applied Property</em>' container reference.
     * @see #setAppliedProperty(PropertyCallExp)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclExpression_AppliedProperty()
     * @see fr.tpt.atlanalyser.atl.OCL.PropertyCallExp#getSource
     * @model opposite="source" transient="false" ordered="false"
     * @generated
     */
    PropertyCallExp getAppliedProperty();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getAppliedProperty <em>Applied Property</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Applied Property</em>' container reference.
     * @see #getAppliedProperty()
     * @generated
     */
    void setAppliedProperty(PropertyCallExp value);

    /**
     * Returns the value of the '<em><b>Collection</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.CollectionExp#getElements <em>Elements</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Collection</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Collection</em>' container reference.
     * @see #setCollection(CollectionExp)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclExpression_Collection()
     * @see fr.tpt.atlanalyser.atl.OCL.CollectionExp#getElements
     * @model opposite="elements" transient="false" ordered="false"
     * @generated
     */
    CollectionExp getCollection();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getCollection <em>Collection</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Collection</em>' container reference.
     * @see #getCollection()
     * @generated
     */
    void setCollection(CollectionExp value);

    /**
     * Returns the value of the '<em><b>Let Exp</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.LetExp#getIn_ <em>In </em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Let Exp</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Let Exp</em>' container reference.
     * @see #setLetExp(LetExp)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclExpression_LetExp()
     * @see fr.tpt.atlanalyser.atl.OCL.LetExp#getIn_
     * @model opposite="in_" transient="false" ordered="false"
     * @generated
     */
    LetExp getLetExp();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getLetExp <em>Let Exp</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Let Exp</em>' container reference.
     * @see #getLetExp()
     * @generated
     */
    void setLetExp(LetExp value);

    /**
     * Returns the value of the '<em><b>Loop Exp</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.LoopExp#getBody <em>Body</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Loop Exp</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Loop Exp</em>' container reference.
     * @see #setLoopExp(LoopExp)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclExpression_LoopExp()
     * @see fr.tpt.atlanalyser.atl.OCL.LoopExp#getBody
     * @model opposite="body" transient="false" ordered="false"
     * @generated
     */
    LoopExp getLoopExp();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getLoopExp <em>Loop Exp</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Loop Exp</em>' container reference.
     * @see #getLoopExp()
     * @generated
     */
    void setLoopExp(LoopExp value);

    /**
     * Returns the value of the '<em><b>Parent Operation</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OperationCallExp#getArguments <em>Arguments</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parent Operation</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parent Operation</em>' container reference.
     * @see #setParentOperation(OperationCallExp)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclExpression_ParentOperation()
     * @see fr.tpt.atlanalyser.atl.OCL.OperationCallExp#getArguments
     * @model opposite="arguments" transient="false" ordered="false"
     * @generated
     */
    OperationCallExp getParentOperation();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getParentOperation <em>Parent Operation</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Parent Operation</em>' container reference.
     * @see #getParentOperation()
     * @generated
     */
    void setParentOperation(OperationCallExp value);

    /**
     * Returns the value of the '<em><b>Initialized Variable</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.VariableDeclaration#getInitExpression <em>Init Expression</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Initialized Variable</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Initialized Variable</em>' container reference.
     * @see #setInitializedVariable(VariableDeclaration)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclExpression_InitializedVariable()
     * @see fr.tpt.atlanalyser.atl.OCL.VariableDeclaration#getInitExpression
     * @model opposite="initExpression" transient="false" ordered="false"
     * @generated
     */
    VariableDeclaration getInitializedVariable();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getInitializedVariable <em>Initialized Variable</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Initialized Variable</em>' container reference.
     * @see #getInitializedVariable()
     * @generated
     */
    void setInitializedVariable(VariableDeclaration value);

    /**
     * Returns the value of the '<em><b>If Exp2</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.IfExp#getThenExpression <em>Then Expression</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>If Exp2</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>If Exp2</em>' container reference.
     * @see #setIfExp2(IfExp)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclExpression_IfExp2()
     * @see fr.tpt.atlanalyser.atl.OCL.IfExp#getThenExpression
     * @model opposite="thenExpression" transient="false" ordered="false"
     * @generated
     */
    IfExp getIfExp2();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getIfExp2 <em>If Exp2</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>If Exp2</em>' container reference.
     * @see #getIfExp2()
     * @generated
     */
    void setIfExp2(IfExp value);

    /**
     * Returns the value of the '<em><b>Owning Operation</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.Operation#getBody <em>Body</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Owning Operation</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Owning Operation</em>' container reference.
     * @see #setOwningOperation(Operation)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclExpression_OwningOperation()
     * @see fr.tpt.atlanalyser.atl.OCL.Operation#getBody
     * @model opposite="body" transient="false" ordered="false"
     * @generated
     */
    Operation getOwningOperation();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getOwningOperation <em>Owning Operation</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Owning Operation</em>' container reference.
     * @see #getOwningOperation()
     * @generated
     */
    void setOwningOperation(Operation value);

    /**
     * Returns the value of the '<em><b>If Exp1</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.IfExp#getCondition <em>Condition</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>If Exp1</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>If Exp1</em>' container reference.
     * @see #setIfExp1(IfExp)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclExpression_IfExp1()
     * @see fr.tpt.atlanalyser.atl.OCL.IfExp#getCondition
     * @model opposite="condition" transient="false" ordered="false"
     * @generated
     */
    IfExp getIfExp1();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getIfExp1 <em>If Exp1</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>If Exp1</em>' container reference.
     * @see #getIfExp1()
     * @generated
     */
    void setIfExp1(IfExp value);

    /**
     * Returns the value of the '<em><b>Owning Attribute</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.Attribute#getInitExpression <em>Init Expression</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Owning Attribute</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Owning Attribute</em>' container reference.
     * @see #setOwningAttribute(Attribute)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclExpression_OwningAttribute()
     * @see fr.tpt.atlanalyser.atl.OCL.Attribute#getInitExpression
     * @model opposite="initExpression" transient="false" ordered="false"
     * @generated
     */
    Attribute getOwningAttribute();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getOwningAttribute <em>Owning Attribute</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Owning Attribute</em>' container reference.
     * @see #getOwningAttribute()
     * @generated
     */
    void setOwningAttribute(Attribute value);

    /**
     * @generated NOT
     */
    public String print();
} // OclExpression
