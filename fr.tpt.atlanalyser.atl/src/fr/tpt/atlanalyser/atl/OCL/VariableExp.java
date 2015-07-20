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
 * A representation of the model object '<em><b>Variable Exp</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.VariableExp#getReferredVariable <em>Referred Variable</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getVariableExp()
 * @model
 * @generated
 */
public interface VariableExp extends OclExpression {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Referred Variable</b></em>' reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.VariableDeclaration#getVariableExp <em>Variable Exp</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Referred Variable</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Referred Variable</em>' reference.
     * @see #setReferredVariable(VariableDeclaration)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getVariableExp_ReferredVariable()
     * @see fr.tpt.atlanalyser.atl.OCL.VariableDeclaration#getVariableExp
     * @model opposite="variableExp" required="true" ordered="false"
     * @generated
     */
    VariableDeclaration getReferredVariable();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.VariableExp#getReferredVariable <em>Referred Variable</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Referred Variable</em>' reference.
     * @see #getReferredVariable()
     * @generated
     */
    void setReferredVariable(VariableDeclaration value);

} // VariableExp
