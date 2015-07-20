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

import fr.tpt.atlanalyser.atl.OCL.OclExpression;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Query</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.Query#getBody <em>Body</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.Query#getHelpers <em>Helpers</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getQuery()
 * @model
 * @generated
 */
public interface Query extends Unit {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Body</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Body</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Body</em>' containment reference.
     * @see #setBody(OclExpression)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getQuery_Body()
     * @model containment="true" required="true" ordered="false"
     * @generated
     */
    OclExpression getBody();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.Query#getBody <em>Body</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Body</em>' containment reference.
     * @see #getBody()
     * @generated
     */
    void setBody(OclExpression value);

    /**
     * Returns the value of the '<em><b>Helpers</b></em>' reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.ATL.Helper}.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.Helper#getQuery <em>Query</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Helpers</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Helpers</em>' reference list.
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getQuery_Helpers()
     * @see fr.tpt.atlanalyser.atl.ATL.Helper#getQuery
     * @model opposite="query"
     * @generated
     */
    EList<Helper> getHelpers();

} // Query
