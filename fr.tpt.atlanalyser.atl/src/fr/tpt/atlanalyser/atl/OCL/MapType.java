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
 * A representation of the model object '<em><b>Map Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.MapType#getValueType <em>Value Type</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.MapType#getKeyType <em>Key Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getMapType()
 * @model
 * @generated
 */
public interface MapType extends OclType {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Value Type</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclType#getMapType2 <em>Map Type2</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Value Type</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Value Type</em>' containment reference.
     * @see #setValueType(OclType)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getMapType_ValueType()
     * @see fr.tpt.atlanalyser.atl.OCL.OclType#getMapType2
     * @model opposite="mapType2" containment="true" required="true" ordered="false"
     * @generated
     */
    OclType getValueType();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.MapType#getValueType <em>Value Type</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Value Type</em>' containment reference.
     * @see #getValueType()
     * @generated
     */
    void setValueType(OclType value);

    /**
     * Returns the value of the '<em><b>Key Type</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclType#getMapType <em>Map Type</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Key Type</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Key Type</em>' containment reference.
     * @see #setKeyType(OclType)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getMapType_KeyType()
     * @see fr.tpt.atlanalyser.atl.OCL.OclType#getMapType
     * @model opposite="mapType" containment="true" required="true" ordered="false"
     * @generated
     */
    OclType getKeyType();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.MapType#getKeyType <em>Key Type</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Key Type</em>' containment reference.
     * @see #getKeyType()
     * @generated
     */
    void setKeyType(OclType value);

} // MapType
