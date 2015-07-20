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
package fr.tpt.atlanalyser.atl.ATL;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.Rule#getOutPattern <em>Out Pattern</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.Rule#getActionBlock <em>Action Block</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.Rule#getVariables <em>Variables</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.Rule#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getRule()
 * @model abstract="true"
 * @generated
 */
public interface Rule extends ModuleElement {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Out Pattern</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.OutPattern#getRule <em>Rule</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Out Pattern</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Out Pattern</em>' containment reference.
     * @see #setOutPattern(OutPattern)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getRule_OutPattern()
     * @see fr.tpt.atlanalyser.atl.ATL.OutPattern#getRule
     * @model opposite="rule" containment="true" ordered="false"
     * @generated
     */
    OutPattern getOutPattern();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.Rule#getOutPattern <em>Out Pattern</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Out Pattern</em>' containment reference.
     * @see #getOutPattern()
     * @generated
     */
    void setOutPattern(OutPattern value);

    /**
     * Returns the value of the '<em><b>Action Block</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.ActionBlock#getRule <em>Rule</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Action Block</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Action Block</em>' containment reference.
     * @see #setActionBlock(ActionBlock)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getRule_ActionBlock()
     * @see fr.tpt.atlanalyser.atl.ATL.ActionBlock#getRule
     * @model opposite="rule" containment="true" ordered="false"
     * @generated
     */
    ActionBlock getActionBlock();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.Rule#getActionBlock <em>Action Block</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Action Block</em>' containment reference.
     * @see #getActionBlock()
     * @generated
     */
    void setActionBlock(ActionBlock value);

    /**
     * Returns the value of the '<em><b>Variables</b></em>' containment reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.ATL.RuleVariableDeclaration}.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.RuleVariableDeclaration#getRule <em>Rule</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Variables</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Variables</em>' containment reference list.
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getRule_Variables()
     * @see fr.tpt.atlanalyser.atl.ATL.RuleVariableDeclaration#getRule
     * @model opposite="rule" containment="true"
     * @generated
     */
    EList<RuleVariableDeclaration> getVariables();

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getRule_Name()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.String" required="true" ordered="false"
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.Rule#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

} // Rule
