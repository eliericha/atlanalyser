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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Loop Exp</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.LoopExp#getBody <em>Body</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.LoopExp#getIterators <em>Iterators</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getLoopExp()
 * @model abstract="true"
 * @generated
 */
public interface LoopExp extends PropertyCallExp {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Body</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getLoopExp <em>Loop Exp</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Body</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Body</em>' containment reference.
     * @see #setBody(OclExpression)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getLoopExp_Body()
     * @see fr.tpt.atlanalyser.atl.OCL.OclExpression#getLoopExp
     * @model opposite="loopExp" containment="true" required="true" ordered="false"
     * @generated
     */
    OclExpression getBody();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.LoopExp#getBody <em>Body</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Body</em>' containment reference.
     * @see #getBody()
     * @generated
     */
    void setBody(OclExpression value);

    /**
     * Returns the value of the '<em><b>Iterators</b></em>' containment reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.OCL.Iterator}.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.Iterator#getLoopExpr <em>Loop Expr</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Iterators</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Iterators</em>' containment reference list.
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getLoopExp_Iterators()
     * @see fr.tpt.atlanalyser.atl.OCL.Iterator#getLoopExpr
     * @model opposite="loopExpr" containment="true" required="true" ordered="false"
     * @generated
     */
    EList<Iterator> getIterators();

} // LoopExp
