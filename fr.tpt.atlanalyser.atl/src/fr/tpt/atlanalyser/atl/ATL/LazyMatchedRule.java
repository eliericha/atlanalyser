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


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Lazy Matched Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.LazyMatchedRule#isIsUnique <em>Is Unique</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getLazyMatchedRule()
 * @model
 * @generated
 */
public interface LazyMatchedRule extends MatchedRule {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Is Unique</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Is Unique</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Is Unique</em>' attribute.
     * @see #setIsUnique(boolean)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getLazyMatchedRule_IsUnique()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.Boolean" required="true" ordered="false"
     * @generated
     */
    boolean isIsUnique();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.LazyMatchedRule#isIsUnique <em>Is Unique</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Is Unique</em>' attribute.
     * @see #isIsUnique()
     * @generated
     */
    void setIsUnique(boolean value);

} // LazyMatchedRule
