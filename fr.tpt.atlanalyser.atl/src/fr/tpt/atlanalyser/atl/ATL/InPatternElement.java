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
 * A representation of the model object '<em><b>In Pattern Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.InPatternElement#getMapsTo <em>Maps To</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.InPatternElement#getInPattern <em>In Pattern</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.InPatternElement#getModels <em>Models</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getInPatternElement()
 * @model abstract="true"
 * @generated
 */
public interface InPatternElement extends PatternElement {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Maps To</b></em>' reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.OutPatternElement#getSourceElement <em>Source Element</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Maps To</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Maps To</em>' reference.
     * @see #setMapsTo(OutPatternElement)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getInPatternElement_MapsTo()
     * @see fr.tpt.atlanalyser.atl.ATL.OutPatternElement#getSourceElement
     * @model opposite="sourceElement" required="true" ordered="false"
     * @generated
     */
    OutPatternElement getMapsTo();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.InPatternElement#getMapsTo <em>Maps To</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Maps To</em>' reference.
     * @see #getMapsTo()
     * @generated
     */
    void setMapsTo(OutPatternElement value);

    /**
     * Returns the value of the '<em><b>In Pattern</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.InPattern#getElements <em>Elements</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>In Pattern</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>In Pattern</em>' container reference.
     * @see #setInPattern(InPattern)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getInPatternElement_InPattern()
     * @see fr.tpt.atlanalyser.atl.ATL.InPattern#getElements
     * @model opposite="elements" required="true" transient="false" ordered="false"
     * @generated
     */
    InPattern getInPattern();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.InPatternElement#getInPattern <em>In Pattern</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>In Pattern</em>' container reference.
     * @see #getInPattern()
     * @generated
     */
    void setInPattern(InPattern value);

    /**
     * Returns the value of the '<em><b>Models</b></em>' reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.OCL.OclModel}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Models</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Models</em>' reference list.
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getInPatternElement_Models()
     * @model ordered="false"
     * @generated
     */
    EList<OclModel> getModels();

} // InPatternElement
