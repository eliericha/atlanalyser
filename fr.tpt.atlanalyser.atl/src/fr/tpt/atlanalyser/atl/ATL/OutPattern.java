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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Out Pattern</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.OutPattern#getRule <em>Rule</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.OutPattern#getDropPattern <em>Drop Pattern</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.OutPattern#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getOutPattern()
 * @model
 * @generated
 */
public interface OutPattern extends LocatedElement {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Rule</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.Rule#getOutPattern <em>Out Pattern</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Rule</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Rule</em>' container reference.
     * @see #setRule(Rule)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getOutPattern_Rule()
     * @see fr.tpt.atlanalyser.atl.ATL.Rule#getOutPattern
     * @model opposite="outPattern" required="true" transient="false" ordered="false"
     * @generated
     */
    Rule getRule();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.OutPattern#getRule <em>Rule</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Rule</em>' container reference.
     * @see #getRule()
     * @generated
     */
    void setRule(Rule value);

    /**
     * Returns the value of the '<em><b>Drop Pattern</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.DropPattern#getOutPattern <em>Out Pattern</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Drop Pattern</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Drop Pattern</em>' containment reference.
     * @see #setDropPattern(DropPattern)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getOutPattern_DropPattern()
     * @see fr.tpt.atlanalyser.atl.ATL.DropPattern#getOutPattern
     * @model opposite="outPattern" containment="true" ordered="false"
     * @generated
     */
    DropPattern getDropPattern();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.OutPattern#getDropPattern <em>Drop Pattern</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Drop Pattern</em>' containment reference.
     * @see #getDropPattern()
     * @generated
     */
    void setDropPattern(DropPattern value);

    /**
     * Returns the value of the '<em><b>Elements</b></em>' containment reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.ATL.OutPatternElement}.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.OutPatternElement#getOutPattern <em>Out Pattern</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Elements</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Elements</em>' containment reference list.
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getOutPattern_Elements()
     * @see fr.tpt.atlanalyser.atl.ATL.OutPatternElement#getOutPattern
     * @model opposite="outPattern" containment="true" required="true"
     * @generated
     */
    EList<OutPatternElement> getElements();

} // OutPattern
