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

import fr.tpt.atlanalyser.atl.OCL.OclExpression;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>If Stat</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.IfStat#getCondition <em>Condition</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.IfStat#getThenStatements <em>Then Statements</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.IfStat#getElseStatements <em>Else Statements</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getIfStat()
 * @model
 * @generated
 */
public interface IfStat extends Statement {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Condition</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Condition</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Condition</em>' containment reference.
     * @see #setCondition(OclExpression)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getIfStat_Condition()
     * @model containment="true" required="true" ordered="false"
     * @generated
     */
    OclExpression getCondition();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.IfStat#getCondition <em>Condition</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Condition</em>' containment reference.
     * @see #getCondition()
     * @generated
     */
    void setCondition(OclExpression value);

    /**
     * Returns the value of the '<em><b>Then Statements</b></em>' containment reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.ATL.Statement}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Then Statements</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Then Statements</em>' containment reference list.
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getIfStat_ThenStatements()
     * @model containment="true"
     * @generated
     */
    EList<Statement> getThenStatements();

    /**
     * Returns the value of the '<em><b>Else Statements</b></em>' containment reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.ATL.Statement}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Else Statements</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Else Statements</em>' containment reference list.
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getIfStat_ElseStatements()
     * @model containment="true"
     * @generated
     */
    EList<Statement> getElseStatements();

} // IfStat
