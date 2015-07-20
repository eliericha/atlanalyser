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
package fr.tpt.atlanalyser.atl.PrimitiveTypes;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see fr.tpt.atlanalyser.atl.PrimitiveTypes.PrimitiveTypesFactory
 * @model kind="package"
 * @generated
 */
public interface PrimitiveTypesPackage extends EPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "PrimitiveTypes";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.eclipse.org/gmt/2005/ATL-PrimitiveTypes";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "ptypes";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    PrimitiveTypesPackage eINSTANCE = fr.tpt.atlanalyser.atl.PrimitiveTypes.impl.PrimitiveTypesPackageImpl.init();

    /**
     * The meta object id for the '<em>Boolean</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see fr.tpt.atlanalyser.atl.PrimitiveTypes.impl.PrimitiveTypesPackageImpl#getBoolean()
     * @generated
     */
    int BOOLEAN = 0;

    /**
     * The meta object id for the '<em>Double</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see fr.tpt.atlanalyser.atl.PrimitiveTypes.impl.PrimitiveTypesPackageImpl#getDouble()
     * @generated
     */
    int DOUBLE = 1;

    /**
     * The meta object id for the '<em>Integer</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see fr.tpt.atlanalyser.atl.PrimitiveTypes.impl.PrimitiveTypesPackageImpl#getInteger()
     * @generated
     */
    int INTEGER = 2;

    /**
     * The meta object id for the '<em>String</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.String
     * @see fr.tpt.atlanalyser.atl.PrimitiveTypes.impl.PrimitiveTypesPackageImpl#getString()
     * @generated
     */
    int STRING = 3;


    /**
     * Returns the meta object for data type '<em>Boolean</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Boolean</em>'.
     * @model instanceClass="boolean"
     * @generated
     */
    EDataType getBoolean();

    /**
     * Returns the meta object for data type '<em>Double</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Double</em>'.
     * @model instanceClass="double"
     * @generated
     */
    EDataType getDouble();

    /**
     * Returns the meta object for data type '<em>Integer</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Integer</em>'.
     * @model instanceClass="int"
     * @generated
     */
    EDataType getInteger();

    /**
     * Returns the meta object for data type '{@link java.lang.String <em>String</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>String</em>'.
     * @see java.lang.String
     * @model instanceClass="java.lang.String"
     * @generated
     */
    EDataType getString();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    PrimitiveTypesFactory getPrimitiveTypesFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each operation of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '<em>Boolean</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see fr.tpt.atlanalyser.atl.PrimitiveTypes.impl.PrimitiveTypesPackageImpl#getBoolean()
         * @generated
         */
        EDataType BOOLEAN = eINSTANCE.getBoolean();

        /**
         * The meta object literal for the '<em>Double</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see fr.tpt.atlanalyser.atl.PrimitiveTypes.impl.PrimitiveTypesPackageImpl#getDouble()
         * @generated
         */
        EDataType DOUBLE = eINSTANCE.getDouble();

        /**
         * The meta object literal for the '<em>Integer</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see fr.tpt.atlanalyser.atl.PrimitiveTypes.impl.PrimitiveTypesPackageImpl#getInteger()
         * @generated
         */
        EDataType INTEGER = eINSTANCE.getInteger();

        /**
         * The meta object literal for the '<em>String</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.lang.String
         * @see fr.tpt.atlanalyser.atl.PrimitiveTypes.impl.PrimitiveTypesPackageImpl#getString()
         * @generated
         */
        EDataType STRING = eINSTANCE.getString();

    }

} //PrimitiveTypesPackage
