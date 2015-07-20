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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Located Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.LocatedElement#getLocation <em>Location</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.LocatedElement#getCommentsBefore <em>Comments Before</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.LocatedElement#getCommentsAfter <em>Comments After</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getLocatedElement()
 * @model abstract="true"
 * @generated
 */
public interface LocatedElement extends EObject {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Location</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Location</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Location</em>' attribute.
     * @see #setLocation(String)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getLocatedElement_Location()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.String" ordered="false"
     * @generated
     */
    String getLocation();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.LocatedElement#getLocation <em>Location</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Location</em>' attribute.
     * @see #getLocation()
     * @generated
     */
    void setLocation(String value);

    /**
     * Returns the value of the '<em><b>Comments Before</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Comments Before</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Comments Before</em>' attribute list.
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getLocatedElement_CommentsBefore()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.String"
     * @generated
     */
    EList<String> getCommentsBefore();

    /**
     * Returns the value of the '<em><b>Comments After</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Comments After</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Comments After</em>' attribute list.
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getLocatedElement_CommentsAfter()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.String"
     * @generated
     */
    EList<String> getCommentsAfter();

} // LocatedElement
