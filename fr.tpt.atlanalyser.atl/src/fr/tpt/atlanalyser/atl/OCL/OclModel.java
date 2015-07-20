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

import fr.tpt.atlanalyser.atl.ATL.LocatedElement;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ocl Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclModel#getName <em>Name</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclModel#getMetamodel <em>Metamodel</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclModel#getElements <em>Elements</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclModel#getModel <em>Model</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclModel()
 * @model
 * @generated
 */
public interface OclModel extends LocatedElement {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclModel_Name()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.String" required="true" ordered="false"
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclModel#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Metamodel</b></em>' reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclModel#getModel <em>Model</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Metamodel</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Metamodel</em>' reference.
     * @see #setMetamodel(OclModel)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclModel_Metamodel()
     * @see fr.tpt.atlanalyser.atl.OCL.OclModel#getModel
     * @model opposite="model" required="true" ordered="false"
     * @generated
     */
    OclModel getMetamodel();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclModel#getMetamodel <em>Metamodel</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Metamodel</em>' reference.
     * @see #getMetamodel()
     * @generated
     */
    void setMetamodel(OclModel value);

    /**
     * Returns the value of the '<em><b>Elements</b></em>' reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.OCL.OclModelElement}.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclModelElement#getModel <em>Model</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Elements</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Elements</em>' reference list.
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclModel_Elements()
     * @see fr.tpt.atlanalyser.atl.OCL.OclModelElement#getModel
     * @model opposite="model" ordered="false"
     * @generated
     */
    EList<OclModelElement> getElements();

    /**
     * Returns the value of the '<em><b>Model</b></em>' reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.OCL.OclModel}.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclModel#getMetamodel <em>Metamodel</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Model</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Model</em>' reference list.
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclModel_Model()
     * @see fr.tpt.atlanalyser.atl.OCL.OclModel#getMetamodel
     * @model opposite="metamodel" ordered="false"
     * @generated
     */
    EList<OclModel> getModel();

} // OclModel
