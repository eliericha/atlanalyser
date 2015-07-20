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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>In Pattern</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.InPattern#getElements <em>Elements</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.InPattern#getRule <em>Rule</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.InPattern#getFilter <em>Filter</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getInPattern()
 * @model
 * @generated
 */
public interface InPattern extends LocatedElement {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Elements</b></em>' containment reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.ATL.InPatternElement}.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.InPatternElement#getInPattern <em>In Pattern</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Elements</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Elements</em>' containment reference list.
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getInPattern_Elements()
     * @see fr.tpt.atlanalyser.atl.ATL.InPatternElement#getInPattern
     * @model opposite="inPattern" containment="true" required="true"
     * @generated
     */
    EList<InPatternElement> getElements();

    /**
     * Returns the value of the '<em><b>Rule</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.MatchedRule#getInPattern <em>In Pattern</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Rule</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Rule</em>' container reference.
     * @see #setRule(MatchedRule)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getInPattern_Rule()
     * @see fr.tpt.atlanalyser.atl.ATL.MatchedRule#getInPattern
     * @model opposite="inPattern" required="true" transient="false" ordered="false"
     * @generated
     */
    MatchedRule getRule();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.InPattern#getRule <em>Rule</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Rule</em>' container reference.
     * @see #getRule()
     * @generated
     */
    void setRule(MatchedRule value);

    /**
     * Returns the value of the '<em><b>Filter</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Filter</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Filter</em>' containment reference.
     * @see #setFilter(OclExpression)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getInPattern_Filter()
     * @model containment="true" ordered="false"
     * @generated
     */
    OclExpression getFilter();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.InPattern#getFilter <em>Filter</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Filter</em>' containment reference.
     * @see #getFilter()
     * @generated
     */
    void setFilter(OclExpression value);

} // InPattern
