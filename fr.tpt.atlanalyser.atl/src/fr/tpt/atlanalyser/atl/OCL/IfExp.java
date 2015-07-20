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


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>If Exp</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.IfExp#getThenExpression <em>Then Expression</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.IfExp#getCondition <em>Condition</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.IfExp#getElseExpression <em>Else Expression</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getIfExp()
 * @model
 * @generated
 */
public interface IfExp extends OclExpression {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Then Expression</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getIfExp2 <em>If Exp2</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Then Expression</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Then Expression</em>' containment reference.
     * @see #setThenExpression(OclExpression)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getIfExp_ThenExpression()
     * @see fr.tpt.atlanalyser.atl.OCL.OclExpression#getIfExp2
     * @model opposite="ifExp2" containment="true" required="true" ordered="false"
     * @generated
     */
    OclExpression getThenExpression();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.IfExp#getThenExpression <em>Then Expression</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Then Expression</em>' containment reference.
     * @see #getThenExpression()
     * @generated
     */
    void setThenExpression(OclExpression value);

    /**
     * Returns the value of the '<em><b>Condition</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getIfExp1 <em>If Exp1</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Condition</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Condition</em>' containment reference.
     * @see #setCondition(OclExpression)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getIfExp_Condition()
     * @see fr.tpt.atlanalyser.atl.OCL.OclExpression#getIfExp1
     * @model opposite="ifExp1" containment="true" required="true" ordered="false"
     * @generated
     */
    OclExpression getCondition();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.IfExp#getCondition <em>Condition</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Condition</em>' containment reference.
     * @see #getCondition()
     * @generated
     */
    void setCondition(OclExpression value);

    /**
     * Returns the value of the '<em><b>Else Expression</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getIfExp3 <em>If Exp3</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Else Expression</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Else Expression</em>' containment reference.
     * @see #setElseExpression(OclExpression)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getIfExp_ElseExpression()
     * @see fr.tpt.atlanalyser.atl.OCL.OclExpression#getIfExp3
     * @model opposite="ifExp3" containment="true" required="true" ordered="false"
     * @generated
     */
    OclExpression getElseExpression();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.IfExp#getElseExpression <em>Else Expression</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Else Expression</em>' containment reference.
     * @see #getElseExpression()
     * @generated
     */
    void setElseExpression(OclExpression value);

} // IfExp
