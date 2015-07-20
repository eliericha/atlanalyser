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
 * A representation of the model object '<em><b>Iterator</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.Iterator#getLoopExpr <em>Loop Expr</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getIterator()
 * @model
 * @generated
 */
public interface Iterator extends VariableDeclaration {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Loop Expr</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.LoopExp#getIterators <em>Iterators</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Loop Expr</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Loop Expr</em>' container reference.
     * @see #setLoopExpr(LoopExp)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getIterator_LoopExpr()
     * @see fr.tpt.atlanalyser.atl.OCL.LoopExp#getIterators
     * @model opposite="iterators" transient="false" ordered="false"
     * @generated
     */
    LoopExp getLoopExpr();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.Iterator#getLoopExpr <em>Loop Expr</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Loop Expr</em>' container reference.
     * @see #getLoopExpr()
     * @generated
     */
    void setLoopExpr(LoopExp value);

} // Iterator
