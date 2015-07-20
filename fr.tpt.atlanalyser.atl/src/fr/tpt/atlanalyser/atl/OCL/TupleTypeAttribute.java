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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Tuple Type Attribute</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.TupleTypeAttribute#getType <em>Type</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.TupleTypeAttribute#getTupleType <em>Tuple Type</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.TupleTypeAttribute#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getTupleTypeAttribute()
 * @model
 * @generated
 */
public interface TupleTypeAttribute extends LocatedElement {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Type</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclType#getTupleTypeAttribute <em>Tuple Type Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' containment reference.
     * @see #setType(OclType)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getTupleTypeAttribute_Type()
     * @see fr.tpt.atlanalyser.atl.OCL.OclType#getTupleTypeAttribute
     * @model opposite="tupleTypeAttribute" containment="true" required="true" ordered="false"
     * @generated
     */
    OclType getType();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.TupleTypeAttribute#getType <em>Type</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' containment reference.
     * @see #getType()
     * @generated
     */
    void setType(OclType value);

    /**
     * Returns the value of the '<em><b>Tuple Type</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.TupleType#getAttributes <em>Attributes</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Tuple Type</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Tuple Type</em>' container reference.
     * @see #setTupleType(TupleType)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getTupleTypeAttribute_TupleType()
     * @see fr.tpt.atlanalyser.atl.OCL.TupleType#getAttributes
     * @model opposite="attributes" required="true" transient="false" ordered="false"
     * @generated
     */
    TupleType getTupleType();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.TupleTypeAttribute#getTupleType <em>Tuple Type</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Tuple Type</em>' container reference.
     * @see #getTupleType()
     * @generated
     */
    void setTupleType(TupleType value);

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
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getTupleTypeAttribute_Name()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.String" required="true" ordered="false"
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.TupleTypeAttribute#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

} // TupleTypeAttribute
