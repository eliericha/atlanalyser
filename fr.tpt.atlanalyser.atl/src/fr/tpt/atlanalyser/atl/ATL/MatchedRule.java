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
 * A representation of the model object '<em><b>Matched Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.MatchedRule#getInPattern <em>In Pattern</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.MatchedRule#getChildren <em>Children</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.MatchedRule#getSuperRule <em>Super Rule</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.MatchedRule#isIsAbstract <em>Is Abstract</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.MatchedRule#isIsRefining <em>Is Refining</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.MatchedRule#isIsNoDefault <em>Is No Default</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getMatchedRule()
 * @model
 * @generated
 */
public interface MatchedRule extends Rule {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>In Pattern</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.InPattern#getRule <em>Rule</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>In Pattern</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>In Pattern</em>' containment reference.
     * @see #setInPattern(InPattern)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getMatchedRule_InPattern()
     * @see fr.tpt.atlanalyser.atl.ATL.InPattern#getRule
     * @model opposite="rule" containment="true" ordered="false"
     * @generated
     */
    InPattern getInPattern();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.MatchedRule#getInPattern <em>In Pattern</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>In Pattern</em>' containment reference.
     * @see #getInPattern()
     * @generated
     */
    void setInPattern(InPattern value);

    /**
     * Returns the value of the '<em><b>Children</b></em>' reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.ATL.MatchedRule}.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.MatchedRule#getSuperRule <em>Super Rule</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Children</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Children</em>' reference list.
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getMatchedRule_Children()
     * @see fr.tpt.atlanalyser.atl.ATL.MatchedRule#getSuperRule
     * @model opposite="superRule" ordered="false"
     * @generated
     */
    EList<MatchedRule> getChildren();

    /**
     * Returns the value of the '<em><b>Super Rule</b></em>' reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.ATL.MatchedRule#getChildren <em>Children</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Super Rule</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Super Rule</em>' reference.
     * @see #setSuperRule(MatchedRule)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getMatchedRule_SuperRule()
     * @see fr.tpt.atlanalyser.atl.ATL.MatchedRule#getChildren
     * @model opposite="children" ordered="false"
     * @generated
     */
    MatchedRule getSuperRule();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.MatchedRule#getSuperRule <em>Super Rule</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Super Rule</em>' reference.
     * @see #getSuperRule()
     * @generated
     */
    void setSuperRule(MatchedRule value);

    /**
     * Returns the value of the '<em><b>Is Abstract</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Is Abstract</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Is Abstract</em>' attribute.
     * @see #setIsAbstract(boolean)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getMatchedRule_IsAbstract()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.Boolean" required="true" ordered="false"
     * @generated
     */
    boolean isIsAbstract();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.MatchedRule#isIsAbstract <em>Is Abstract</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Is Abstract</em>' attribute.
     * @see #isIsAbstract()
     * @generated
     */
    void setIsAbstract(boolean value);

    /**
     * Returns the value of the '<em><b>Is Refining</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Is Refining</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Is Refining</em>' attribute.
     * @see #setIsRefining(boolean)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getMatchedRule_IsRefining()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.Boolean" required="true" ordered="false"
     * @generated
     */
    boolean isIsRefining();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.MatchedRule#isIsRefining <em>Is Refining</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Is Refining</em>' attribute.
     * @see #isIsRefining()
     * @generated
     */
    void setIsRefining(boolean value);

    /**
     * Returns the value of the '<em><b>Is No Default</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Is No Default</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Is No Default</em>' attribute.
     * @see #setIsNoDefault(boolean)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getMatchedRule_IsNoDefault()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.Boolean" required="true" ordered="false"
     * @generated
     */
    boolean isIsNoDefault();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.MatchedRule#isIsNoDefault <em>Is No Default</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Is No Default</em>' attribute.
     * @see #isIsNoDefault()
     * @generated
     */
    void setIsNoDefault(boolean value);

} // MatchedRule
