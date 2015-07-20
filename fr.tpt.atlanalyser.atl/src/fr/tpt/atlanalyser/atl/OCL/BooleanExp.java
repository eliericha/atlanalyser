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
 * A representation of the model object '<em><b>Boolean Exp</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.BooleanExp#isBooleanSymbol <em>Boolean Symbol</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getBooleanExp()
 * @model
 * @generated
 */
public interface BooleanExp extends PrimitiveExp {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Boolean Symbol</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Boolean Symbol</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Boolean Symbol</em>' attribute.
     * @see #setBooleanSymbol(boolean)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getBooleanExp_BooleanSymbol()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.Boolean" required="true" ordered="false"
     * @generated
     */
    boolean isBooleanSymbol();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.BooleanExp#isBooleanSymbol <em>Boolean Symbol</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Boolean Symbol</em>' attribute.
     * @see #isBooleanSymbol()
     * @generated
     */
    void setBooleanSymbol(boolean value);

} // BooleanExp
