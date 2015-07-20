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

import fr.tpt.atlanalyser.atl.OCL.Parameter;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Called Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.CalledRule#getParameters <em>Parameters</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.CalledRule#isIsEntrypoint <em>Is Entrypoint</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.CalledRule#isIsEndpoint <em>Is Endpoint</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getCalledRule()
 * @model
 * @generated
 */
public interface CalledRule extends Rule {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Parameters</b></em>' reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.OCL.Parameter}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parameters</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parameters</em>' reference list.
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getCalledRule_Parameters()
     * @model ordered="false"
     * @generated
     */
    EList<Parameter> getParameters();

    /**
     * Returns the value of the '<em><b>Is Entrypoint</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Is Entrypoint</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Is Entrypoint</em>' attribute.
     * @see #setIsEntrypoint(boolean)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getCalledRule_IsEntrypoint()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.Boolean" required="true" ordered="false"
     * @generated
     */
    boolean isIsEntrypoint();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.CalledRule#isIsEntrypoint <em>Is Entrypoint</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Is Entrypoint</em>' attribute.
     * @see #isIsEntrypoint()
     * @generated
     */
    void setIsEntrypoint(boolean value);

    /**
     * Returns the value of the '<em><b>Is Endpoint</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Is Endpoint</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Is Endpoint</em>' attribute.
     * @see #setIsEndpoint(boolean)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getCalledRule_IsEndpoint()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.Boolean" required="true" ordered="false"
     * @generated
     */
    boolean isIsEndpoint();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.CalledRule#isIsEndpoint <em>Is Endpoint</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Is Endpoint</em>' attribute.
     * @see #isIsEndpoint()
     * @generated
     */
    void setIsEndpoint(boolean value);

} // CalledRule
