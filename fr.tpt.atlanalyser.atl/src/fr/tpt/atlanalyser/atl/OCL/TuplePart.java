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
 * A representation of the model object '<em><b>Tuple Part</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.TuplePart#getTuple <em>Tuple</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getTuplePart()
 * @model
 * @generated
 */
public interface TuplePart extends VariableDeclaration {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Tuple</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.TupleExp#getTuplePart <em>Tuple Part</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Tuple</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Tuple</em>' container reference.
     * @see #setTuple(TupleExp)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getTuplePart_Tuple()
     * @see fr.tpt.atlanalyser.atl.OCL.TupleExp#getTuplePart
     * @model opposite="tuplePart" required="true" transient="false" ordered="false"
     * @generated
     */
    TupleExp getTuple();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.TuplePart#getTuple <em>Tuple</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Tuple</em>' container reference.
     * @see #getTuple()
     * @generated
     */
    void setTuple(TupleExp value);

} // TuplePart
