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

import fr.tpt.atlanalyser.atl.OCL.Iterator;
import fr.tpt.atlanalyser.atl.OCL.OclExpression;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>For Stat</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.ForStat#getIterator <em>Iterator</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.ForStat#getCollection <em>Collection</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.ForStat#getStatements <em>Statements</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getForStat()
 * @model
 * @generated
 */
public interface ForStat extends Statement {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Iterator</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Iterator</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Iterator</em>' containment reference.
     * @see #setIterator(Iterator)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getForStat_Iterator()
     * @model containment="true" required="true" ordered="false"
     * @generated
     */
    Iterator getIterator();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.ForStat#getIterator <em>Iterator</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Iterator</em>' containment reference.
     * @see #getIterator()
     * @generated
     */
    void setIterator(Iterator value);

    /**
     * Returns the value of the '<em><b>Collection</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Collection</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Collection</em>' containment reference.
     * @see #setCollection(OclExpression)
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getForStat_Collection()
     * @model containment="true" required="true" ordered="false"
     * @generated
     */
    OclExpression getCollection();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.ATL.ForStat#getCollection <em>Collection</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Collection</em>' containment reference.
     * @see #getCollection()
     * @generated
     */
    void setCollection(OclExpression value);

    /**
     * Returns the value of the '<em><b>Statements</b></em>' containment reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.ATL.Statement}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Statements</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Statements</em>' containment reference list.
     * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage#getForStat_Statements()
     * @model containment="true"
     * @generated
     */
    EList<Statement> getStatements();

} // ForStat
