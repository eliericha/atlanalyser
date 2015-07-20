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
 * A representation of the model object '<em><b>Integer Exp</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.IntegerExp#getIntegerSymbol <em>Integer Symbol</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getIntegerExp()
 * @model
 * @generated
 */
public interface IntegerExp extends NumericExp {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Integer Symbol</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Integer Symbol</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Integer Symbol</em>' attribute.
     * @see #setIntegerSymbol(int)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getIntegerExp_IntegerSymbol()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.Integer" required="true" ordered="false"
     * @generated
     */
    int getIntegerSymbol();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.IntegerExp#getIntegerSymbol <em>Integer Symbol</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Integer Symbol</em>' attribute.
     * @see #getIntegerSymbol()
     * @generated
     */
    void setIntegerSymbol(int value);

} // IntegerExp
