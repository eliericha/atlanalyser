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
 * A representation of the model object '<em><b>Out Pattern Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.OutPatternElement#getOutPattern <em>Out Pattern</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.OutPatternElement#getSourceElement <em>Source Element</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.OutPatternElement#getBindings <em>Bindings</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.OutPatternElement#getModel <em>Model</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getOutPatternElement()
 * @model abstract="true"
 * @generated
 */
public interface OutPatternElement extends PatternElement {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Out Pattern</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.OutPattern#getElements <em>Elements</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Out Pattern</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Out Pattern</em>' container reference.
     * @see #setOutPattern(OutPattern)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getOutPatternElement_OutPattern()
     * @see fr.tpt.atlanalyser.atl.ATL.OutPattern#getElements
     * @model opposite="elements" required="true" transient="false" ordered="false"
     * @generated
     */
    OutPattern getOutPattern();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.OutPatternElement#getOutPattern <em>Out Pattern</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Out Pattern</em>' container reference.
     * @see #getOutPattern()
     * @generated
     */
    void setOutPattern(OutPattern value);

    /**
     * Returns the value of the '<em><b>Source Element</b></em>' reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.InPatternElement#getMapsTo <em>Maps To</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Source Element</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Source Element</em>' reference.
     * @see #setSourceElement(InPatternElement)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getOutPatternElement_SourceElement()
     * @see fr.tpt.atlanalyser.atl.ATL.InPatternElement#getMapsTo
     * @model opposite="mapsTo" ordered="false"
     * @generated
     */
    InPatternElement getSourceElement();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.OutPatternElement#getSourceElement <em>Source Element</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Source Element</em>' reference.
     * @see #getSourceElement()
     * @generated
     */
    void setSourceElement(InPatternElement value);

    /**
     * Returns the value of the '<em><b>Bindings</b></em>' containment reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.ATL.Binding}.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.Binding#getOutPatternElement <em>Out Pattern Element</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Bindings</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Bindings</em>' containment reference list.
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getOutPatternElement_Bindings()
     * @see fr.tpt.atlanalyser.atl.ATL.Binding#getOutPatternElement
     * @model opposite="outPatternElement" containment="true"
     * @generated
     */
    EList<Binding> getBindings();

    /**
     * Returns the value of the '<em><b>Model</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Model</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Model</em>' reference.
     * @see #setModel(OclModel)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getOutPatternElement_Model()
     * @model ordered="false"
     * @generated
     */
    OclModel getModel();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.OutPatternElement#getModel <em>Model</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Model</em>' reference.
     * @see #getModel()
     * @generated
     */
    void setModel(OclModel value);

} // OutPatternElement
