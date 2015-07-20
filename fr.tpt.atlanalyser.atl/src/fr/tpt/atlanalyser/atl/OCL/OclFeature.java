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
 * A representation of the model object '<em><b>Ocl Feature</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclFeature#getDefinition <em>Definition</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclFeature()
 * @model abstract="true"
 * @generated
 */
public interface OclFeature extends LocatedElement {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Definition</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclFeatureDefinition#getFeature <em>Feature</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Definition</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Definition</em>' container reference.
     * @see #setDefinition(OclFeatureDefinition)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclFeature_Definition()
     * @see fr.tpt.atlanalyser.atl.OCL.OclFeatureDefinition#getFeature
     * @model opposite="feature" transient="false" ordered="false"
     * @generated
     */
    OclFeatureDefinition getDefinition();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclFeature#getDefinition <em>Definition</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Definition</em>' container reference.
     * @see #getDefinition()
     * @generated
     */
    void setDefinition(OclFeatureDefinition value);

} // OclFeature
