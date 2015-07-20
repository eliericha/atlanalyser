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


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Library Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.LibraryRef#getUnit <em>Unit</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.LibraryRef#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getLibraryRef()
 * @model
 * @generated
 */
public interface LibraryRef extends LocatedElement {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Unit</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.Unit#getLibraries <em>Libraries</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Unit</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Unit</em>' container reference.
     * @see #setUnit(Unit)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getLibraryRef_Unit()
     * @see fr.tpt.atlanalyser.atl.ATL.Unit#getLibraries
     * @model opposite="libraries" required="true" transient="false" ordered="false"
     * @generated
     */
    Unit getUnit();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.LibraryRef#getUnit <em>Unit</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Unit</em>' container reference.
     * @see #getUnit()
     * @generated
     */
    void setUnit(Unit value);

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
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getLibraryRef_Name()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.String" required="true" ordered="false"
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.LibraryRef#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

} // LibraryRef
