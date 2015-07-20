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
package fr.tpt.atlanalyser.atl.PrimitiveTypes.impl;

import fr.tpt.atlanalyser.atl.ATL.ATLPackage;

import fr.tpt.atlanalyser.atl.ATL.impl.ATLPackageImpl;

import fr.tpt.atlanalyser.atl.OCL.OCLPackage;

import fr.tpt.atlanalyser.atl.OCL.impl.OCLPackageImpl;

import fr.tpt.atlanalyser.atl.PrimitiveTypes.PrimitiveTypesFactory;
import fr.tpt.atlanalyser.atl.PrimitiveTypes.PrimitiveTypesPackage;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PrimitiveTypesPackageImpl extends EPackageImpl implements PrimitiveTypesPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType booleanEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType doubleEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType integerEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType stringEDataType = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see fr.tpt.atlanalyser.atl.PrimitiveTypes.PrimitiveTypesPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private PrimitiveTypesPackageImpl() {
        super(eNS_URI, PrimitiveTypesFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     * 
     * <p>This method is used to initialize {@link PrimitiveTypesPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static PrimitiveTypesPackage init() {
        if (isInited) return (PrimitiveTypesPackage)EPackage.Registry.INSTANCE.getEPackage(PrimitiveTypesPackage.eNS_URI);

        // Obtain or create and register package
        PrimitiveTypesPackageImpl thePrimitiveTypesPackage = (PrimitiveTypesPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof PrimitiveTypesPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new PrimitiveTypesPackageImpl());

        isInited = true;

        // Obtain or create and register interdependencies
        ATLPackageImpl theATLPackage = (ATLPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ATLPackage.eNS_URI) instanceof ATLPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ATLPackage.eNS_URI) : ATLPackage.eINSTANCE);
        OCLPackageImpl theOCLPackage = (OCLPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(OCLPackage.eNS_URI) instanceof OCLPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(OCLPackage.eNS_URI) : OCLPackage.eINSTANCE);

        // Create package meta-data objects
        thePrimitiveTypesPackage.createPackageContents();
        theATLPackage.createPackageContents();
        theOCLPackage.createPackageContents();

        // Initialize created meta-data
        thePrimitiveTypesPackage.initializePackageContents();
        theATLPackage.initializePackageContents();
        theOCLPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        thePrimitiveTypesPackage.freeze();

  
        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(PrimitiveTypesPackage.eNS_URI, thePrimitiveTypesPackage);
        return thePrimitiveTypesPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getBoolean() {
        return booleanEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getDouble() {
        return doubleEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getInteger() {
        return integerEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getString() {
        return stringEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public PrimitiveTypesFactory getPrimitiveTypesFactory() {
        return (PrimitiveTypesFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create data types
        booleanEDataType = createEDataType(BOOLEAN);
        doubleEDataType = createEDataType(DOUBLE);
        integerEDataType = createEDataType(INTEGER);
        stringEDataType = createEDataType(STRING);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Initialize data types
        initEDataType(booleanEDataType, boolean.class, "Boolean", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
        initEDataType(doubleEDataType, double.class, "Double", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
        initEDataType(integerEDataType, int.class, "Integer", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
        initEDataType(stringEDataType, String.class, "String", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

        // Create resource
        createResource(eNS_URI);
    }

} //PrimitiveTypesPackageImpl
