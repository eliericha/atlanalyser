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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Operation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.Operation#getName <em>Name</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.Operation#getParameters <em>Parameters</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.Operation#getReturnType <em>Return Type</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.Operation#getBody <em>Body</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOperation()
 * @model
 * @generated
 */
public interface Operation extends OclFeature {
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
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOperation_Name()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.String" required="true" ordered="false"
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.Operation#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Parameters</b></em>' containment reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.OCL.Parameter}.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.Parameter#getOperation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parameters</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parameters</em>' containment reference list.
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOperation_Parameters()
     * @see fr.tpt.atlanalyser.atl.OCL.Parameter#getOperation
     * @model opposite="operation" containment="true"
     * @generated
     */
    EList<Parameter> getParameters();

    /**
     * Returns the value of the '<em><b>Return Type</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclType#getOperation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Return Type</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Return Type</em>' containment reference.
     * @see #setReturnType(OclType)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOperation_ReturnType()
     * @see fr.tpt.atlanalyser.atl.OCL.OclType#getOperation
     * @model opposite="operation" containment="true" required="true" ordered="false"
     * @generated
     */
    OclType getReturnType();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.Operation#getReturnType <em>Return Type</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Return Type</em>' containment reference.
     * @see #getReturnType()
     * @generated
     */
    void setReturnType(OclType value);

    /**
     * Returns the value of the '<em><b>Body</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getOwningOperation <em>Owning Operation</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Body</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Body</em>' containment reference.
     * @see #setBody(OclExpression)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOperation_Body()
     * @see fr.tpt.atlanalyser.atl.OCL.OclExpression#getOwningOperation
     * @model opposite="owningOperation" containment="true" required="true" ordered="false"
     * @generated
     */
    OclExpression getBody();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.Operation#getBody <em>Body</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Body</em>' containment reference.
     * @see #getBody()
     * @generated
     */
    void setBody(OclExpression value);

} // Operation
