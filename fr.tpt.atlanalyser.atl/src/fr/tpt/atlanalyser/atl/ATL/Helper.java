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

import fr.tpt.atlanalyser.atl.OCL.OclFeatureDefinition;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Helper</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.Helper#getQuery <em>Query</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.Helper#getLibrary <em>Library</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.Helper#getDefinition <em>Definition</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getHelper()
 * @model
 * @generated
 */
public interface Helper extends ModuleElement {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Query</b></em>' reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.Query#getHelpers <em>Helpers</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Query</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Query</em>' reference.
     * @see #setQuery(Query)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getHelper_Query()
     * @see fr.tpt.atlanalyser.atl.ATL.Query#getHelpers
     * @model opposite="helpers" ordered="false"
     * @generated
     */
    Query getQuery();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.Helper#getQuery <em>Query</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Query</em>' reference.
     * @see #getQuery()
     * @generated
     */
    void setQuery(Query value);

    /**
     * Returns the value of the '<em><b>Library</b></em>' reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.Library#getHelpers <em>Helpers</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Library</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Library</em>' reference.
     * @see #setLibrary(Library)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getHelper_Library()
     * @see fr.tpt.atlanalyser.atl.ATL.Library#getHelpers
     * @model opposite="helpers" ordered="false"
     * @generated
     */
    Library getLibrary();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.Helper#getLibrary <em>Library</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Library</em>' reference.
     * @see #getLibrary()
     * @generated
     */
    void setLibrary(Library value);

    /**
     * Returns the value of the '<em><b>Definition</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Definition</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Definition</em>' containment reference.
     * @see #setDefinition(OclFeatureDefinition)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getHelper_Definition()
     * @model containment="true" required="true" ordered="false"
     * @generated
     */
    OclFeatureDefinition getDefinition();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.Helper#getDefinition <em>Definition</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Definition</em>' containment reference.
     * @see #getDefinition()
     * @generated
     */
    void setDefinition(OclFeatureDefinition value);

} // Helper
