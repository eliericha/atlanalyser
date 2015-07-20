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
 * A representation of the model object '<em><b>Attribute</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.Attribute#getName <em>Name</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.Attribute#getInitExpression <em>Init Expression</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.Attribute#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getAttribute()
 * @model
 * @generated
 */
public interface Attribute extends OclFeature {
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
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getAttribute_Name()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.String" required="true" ordered="false"
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.Attribute#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Init Expression</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getOwningAttribute <em>Owning Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Init Expression</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Init Expression</em>' containment reference.
     * @see #setInitExpression(OclExpression)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getAttribute_InitExpression()
     * @see fr.tpt.atlanalyser.atl.OCL.OclExpression#getOwningAttribute
     * @model opposite="owningAttribute" containment="true" required="true" ordered="false"
     * @generated
     */
    OclExpression getInitExpression();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.Attribute#getInitExpression <em>Init Expression</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Init Expression</em>' containment reference.
     * @see #getInitExpression()
     * @generated
     */
    void setInitExpression(OclExpression value);

    /**
     * Returns the value of the '<em><b>Type</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclType#getAttribute <em>Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' containment reference.
     * @see #setType(OclType)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getAttribute_Type()
     * @see fr.tpt.atlanalyser.atl.OCL.OclType#getAttribute
     * @model opposite="attribute" containment="true" required="true" ordered="false"
     * @generated
     */
    OclType getType();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.Attribute#getType <em>Type</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' containment reference.
     * @see #getType()
     * @generated
     */
    void setType(OclType value);

} // Attribute
