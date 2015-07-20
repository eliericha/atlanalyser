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
 * A representation of the model object '<em><b>Tuple Exp</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.TupleExp#getTuplePart <em>Tuple Part</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getTupleExp()
 * @model
 * @generated
 */
public interface TupleExp extends OclExpression {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Tuple Part</b></em>' containment reference list.
     * The list contents are of type {@link fr.tpt.atlanalyser.atl.OCL.TuplePart}.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.TuplePart#getTuple <em>Tuple</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Tuple Part</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Tuple Part</em>' containment reference list.
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getTupleExp_TuplePart()
     * @see fr.tpt.atlanalyser.atl.OCL.TuplePart#getTuple
     * @model opposite="tuple" containment="true"
     * @generated
     */
    EList<TuplePart> getTuplePart();

} // TupleExp
