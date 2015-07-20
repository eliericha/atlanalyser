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
 * A representation of the model object '<em><b>Map Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.MapElement#getMap <em>Map</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.MapElement#getKey <em>Key</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.MapElement#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getMapElement()
 * @model
 * @generated
 */
public interface MapElement extends LocatedElement {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Map</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.MapExp#getElements <em>Elements</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Map</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Map</em>' container reference.
     * @see #setMap(MapExp)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getMapElement_Map()
     * @see fr.tpt.atlanalyser.atl.OCL.MapExp#getElements
     * @model opposite="elements" required="true" transient="false" ordered="false"
     * @generated
     */
    MapExp getMap();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.MapElement#getMap <em>Map</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Map</em>' container reference.
     * @see #getMap()
     * @generated
     */
    void setMap(MapExp value);

    /**
     * Returns the value of the '<em><b>Key</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Key</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Key</em>' containment reference.
     * @see #setKey(OclExpression)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getMapElement_Key()
     * @model containment="true" required="true" ordered="false"
     * @generated
     */
    OclExpression getKey();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.MapElement#getKey <em>Key</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Key</em>' containment reference.
     * @see #getKey()
     * @generated
     */
    void setKey(OclExpression value);

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
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getMapElement_Value()
     * @model containment="true" required="true" ordered="false"
     * @generated
     */
    OclExpression getValue();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.MapElement#getValue <em>Value</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Value</em>' containment reference.
     * @see #getValue()
     * @generated
     */
    void setValue(OclExpression value);

} // MapElement
