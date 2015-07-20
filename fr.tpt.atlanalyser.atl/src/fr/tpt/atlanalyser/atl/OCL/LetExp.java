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
 * A representation of the model object '<em><b>Let Exp</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.LetExp#getVariable <em>Variable</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.LetExp#getIn_ <em>In </em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getLetExp()
 * @model
 * @generated
 */
public interface LetExp extends OclExpression {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Variable</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.VariableDeclaration#getLetExp <em>Let Exp</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Variable</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Variable</em>' containment reference.
     * @see #setVariable(VariableDeclaration)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getLetExp_Variable()
     * @see fr.tpt.atlanalyser.atl.OCL.VariableDeclaration#getLetExp
     * @model opposite="letExp" containment="true" required="true" ordered="false"
     * @generated
     */
    VariableDeclaration getVariable();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.LetExp#getVariable <em>Variable</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Variable</em>' containment reference.
     * @see #getVariable()
     * @generated
     */
    void setVariable(VariableDeclaration value);

    /**
     * Returns the value of the '<em><b>In </b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getLetExp <em>Let Exp</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>In </em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>In </em>' containment reference.
     * @see #setIn_(OclExpression)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getLetExp_In_()
     * @see fr.tpt.atlanalyser.atl.OCL.OclExpression#getLetExp
     * @model opposite="letExp" containment="true" required="true" ordered="false"
     * @generated
     */
    OclExpression getIn_();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.LetExp#getIn_ <em>In </em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>In </em>' containment reference.
     * @see #getIn_()
     * @generated
     */
    void setIn_(OclExpression value);

} // LetExp
