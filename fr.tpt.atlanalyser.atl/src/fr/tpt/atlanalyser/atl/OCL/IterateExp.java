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
 * A representation of the model object '<em><b>Iterate Exp</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.IterateExp#getResult <em>Result</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getIterateExp()
 * @model
 * @generated
 */
public interface IterateExp extends LoopExp {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Result</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.VariableDeclaration#getBaseExp <em>Base Exp</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Result</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Result</em>' containment reference.
     * @see #setResult(VariableDeclaration)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getIterateExp_Result()
     * @see fr.tpt.atlanalyser.atl.OCL.VariableDeclaration#getBaseExp
     * @model opposite="baseExp" containment="true" required="true" ordered="false"
     * @generated
     */
    VariableDeclaration getResult();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.IterateExp#getResult <em>Result</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Result</em>' containment reference.
     * @see #getResult()
     * @generated
     */
    void setResult(VariableDeclaration value);

} // IterateExp