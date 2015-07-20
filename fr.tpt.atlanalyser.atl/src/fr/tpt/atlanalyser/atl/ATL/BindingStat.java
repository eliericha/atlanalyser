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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Binding Stat</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.BindingStat#getSource <em>Source</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.BindingStat#getPropertyName <em>Property Name</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.BindingStat#isIsAssignment <em>Is Assignment</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.BindingStat#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getBindingStat()
 * @model
 * @generated
 */
public interface BindingStat extends Statement {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Source</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Source</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Source</em>' containment reference.
     * @see #setSource(OclExpression)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getBindingStat_Source()
     * @model containment="true" required="true" ordered="false"
     * @generated
     */
    OclExpression getSource();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.BindingStat#getSource <em>Source</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Source</em>' containment reference.
     * @see #getSource()
     * @generated
     */
    void setSource(OclExpression value);

    /**
     * Returns the value of the '<em><b>Property Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Property Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Property Name</em>' attribute.
     * @see #setPropertyName(String)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getBindingStat_PropertyName()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.String" required="true" ordered="false"
     * @generated
     */
    String getPropertyName();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.BindingStat#getPropertyName <em>Property Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Property Name</em>' attribute.
     * @see #getPropertyName()
     * @generated
     */
    void setPropertyName(String value);

    /**
     * Returns the value of the '<em><b>Is Assignment</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Is Assignment</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Is Assignment</em>' attribute.
     * @see #setIsAssignment(boolean)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getBindingStat_IsAssignment()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.Boolean" required="true" ordered="false"
     * @generated
     */
    boolean isIsAssignment();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.BindingStat#isIsAssignment <em>Is Assignment</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Is Assignment</em>' attribute.
     * @see #isIsAssignment()
     * @generated
     */
    void setIsAssignment(boolean value);

    /**
     * Returns the value of the '<em><b>Value</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Value</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Value</em>' containment reference.
     * @see #setValue(OclExpression)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getBindingStat_Value()
     * @model containment="true" required="true" ordered="false"
     * @generated
     */
    OclExpression getValue();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.BindingStat#getValue <em>Value</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Value</em>' containment reference.
     * @see #getValue()
     * @generated
     */
    void setValue(OclExpression value);

} // BindingStat
