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

import fr.tpt.atlanalyser.atl.OCL.OclModel;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Module</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.Module#isIsRefining <em>Is Refining</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.Module#getInModels <em>In Models</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.Module#getOutModels <em>Out Models</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.Module#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getModule()
 * @model
 * @generated
 */
public interface Module extends Unit {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Is Refining</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Is Refining</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Is Refining</em>' attribute.
     * @see #setIsRefining(boolean)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getModule_IsRefining()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.Boolean" required="true" ordered="false"
     * @generated
     */
    boolean isIsRefining();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.Module#isIsRefining <em>Is Refining</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Is Refining</em>' attribute.
     * @see #isIsRefining()
     * @generated
     */
    void setIsRefining(boolean value);

    /**
     * Returns the value of the '<em><b>In Models</b></em>' containment reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.OCL.OclModel}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>In Models</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>In Models</em>' containment reference list.
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getModule_InModels()
     * @model containment="true" required="true"
     * @generated
     */
    EList<OclModel> getInModels();

    /**
     * Returns the value of the '<em><b>Out Models</b></em>' containment reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.OCL.OclModel}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Out Models</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Out Models</em>' containment reference list.
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getModule_OutModels()
     * @model containment="true" required="true" ordered="false"
     * @generated
     */
    EList<OclModel> getOutModels();

    /**
     * Returns the value of the '<em><b>Elements</b></em>' containment reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.ATL.ModuleElement}.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.ModuleElement#getModule <em>Module</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Elements</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Elements</em>' containment reference list.
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getModule_Elements()
     * @see fr.tpt.atlanalyser.atl.ATL.ModuleElement#getModule
     * @model opposite="module" containment="true"
     * @generated
     */
    EList<ModuleElement> getElements();

} // Module
