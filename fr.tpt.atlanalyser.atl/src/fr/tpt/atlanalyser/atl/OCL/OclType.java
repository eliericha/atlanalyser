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


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ocl Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclType#getName <em>Name</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclType#getDefinitions <em>Definitions</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclType#getOclExpression <em>Ocl Expression</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclType#getOperation <em>Operation</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclType#getMapType2 <em>Map Type2</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclType#getAttribute <em>Attribute</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclType#getMapType <em>Map Type</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclType#getCollectionTypes <em>Collection Types</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclType#getTupleTypeAttribute <em>Tuple Type Attribute</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.OclType#getVariableDeclaration <em>Variable Declaration</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclType()
 * @model
 * @generated
 */
public interface OclType extends OclExpression {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclType_Name()
     * @model unique="false" dataType="fr.tpt.atlanalyser.atl.PrimitiveTypes.String" required="true" ordered="false"
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclType#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Definitions</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclContextDefinition#getContext_ <em>Context </em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Definitions</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Definitions</em>' container reference.
     * @see #setDefinitions(OclContextDefinition)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclType_Definitions()
     * @see fr.tpt.atlanalyser.atl.OCL.OclContextDefinition#getContext_
     * @model opposite="context_" transient="false" ordered="false"
     * @generated
     */
    OclContextDefinition getDefinitions();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclType#getDefinitions <em>Definitions</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Definitions</em>' container reference.
     * @see #getDefinitions()
     * @generated
     */
    void setDefinitions(OclContextDefinition value);

    /**
     * Returns the value of the '<em><b>Ocl Expression</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.OclExpression#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Ocl Expression</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Ocl Expression</em>' container reference.
     * @see #setOclExpression(OclExpression)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclType_OclExpression()
     * @see fr.tpt.atlanalyser.atl.OCL.OclExpression#getType
     * @model opposite="type" transient="false" ordered="false"
     * @generated
     */
    OclExpression getOclExpression();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclType#getOclExpression <em>Ocl Expression</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Ocl Expression</em>' container reference.
     * @see #getOclExpression()
     * @generated
     */
    void setOclExpression(OclExpression value);

    /**
     * Returns the value of the '<em><b>Operation</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.Operation#getReturnType <em>Return Type</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Operation</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Operation</em>' container reference.
     * @see #setOperation(Operation)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclType_Operation()
     * @see fr.tpt.atlanalyser.atl.OCL.Operation#getReturnType
     * @model opposite="returnType" transient="false" ordered="false"
     * @generated
     */
    Operation getOperation();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclType#getOperation <em>Operation</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Operation</em>' container reference.
     * @see #getOperation()
     * @generated
     */
    void setOperation(Operation value);

    /**
     * Returns the value of the '<em><b>Map Type2</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.MapType#getValueType <em>Value Type</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Map Type2</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Map Type2</em>' container reference.
     * @see #setMapType2(MapType)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclType_MapType2()
     * @see fr.tpt.atlanalyser.atl.OCL.MapType#getValueType
     * @model opposite="valueType" transient="false" ordered="false"
     * @generated
     */
    MapType getMapType2();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclType#getMapType2 <em>Map Type2</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Map Type2</em>' container reference.
     * @see #getMapType2()
     * @generated
     */
    void setMapType2(MapType value);

    /**
     * Returns the value of the '<em><b>Attribute</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.Attribute#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Attribute</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Attribute</em>' container reference.
     * @see #setAttribute(Attribute)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclType_Attribute()
     * @see fr.tpt.atlanalyser.atl.OCL.Attribute#getType
     * @model opposite="type" transient="false" ordered="false"
     * @generated
     */
    Attribute getAttribute();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclType#getAttribute <em>Attribute</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Attribute</em>' container reference.
     * @see #getAttribute()
     * @generated
     */
    void setAttribute(Attribute value);

    /**
     * Returns the value of the '<em><b>Map Type</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.MapType#getKeyType <em>Key Type</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Map Type</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Map Type</em>' container reference.
     * @see #setMapType(MapType)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclType_MapType()
     * @see fr.tpt.atlanalyser.atl.OCL.MapType#getKeyType
     * @model opposite="keyType" transient="false" ordered="false"
     * @generated
     */
    MapType getMapType();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclType#getMapType <em>Map Type</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Map Type</em>' container reference.
     * @see #getMapType()
     * @generated
     */
    void setMapType(MapType value);

    /**
     * Returns the value of the '<em><b>Collection Types</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.CollectionType#getElementType <em>Element Type</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Collection Types</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Collection Types</em>' container reference.
     * @see #setCollectionTypes(CollectionType)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclType_CollectionTypes()
     * @see fr.tpt.atlanalyser.atl.OCL.CollectionType#getElementType
     * @model opposite="elementType" transient="false" ordered="false"
     * @generated
     */
    CollectionType getCollectionTypes();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclType#getCollectionTypes <em>Collection Types</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Collection Types</em>' container reference.
     * @see #getCollectionTypes()
     * @generated
     */
    void setCollectionTypes(CollectionType value);

    /**
     * Returns the value of the '<em><b>Tuple Type Attribute</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.TupleTypeAttribute#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Tuple Type Attribute</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Tuple Type Attribute</em>' container reference.
     * @see #setTupleTypeAttribute(TupleTypeAttribute)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclType_TupleTypeAttribute()
     * @see fr.tpt.atlanalyser.atl.OCL.TupleTypeAttribute#getType
     * @model opposite="type" transient="false" ordered="false"
     * @generated
     */
    TupleTypeAttribute getTupleTypeAttribute();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclType#getTupleTypeAttribute <em>Tuple Type Attribute</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Tuple Type Attribute</em>' container reference.
     * @see #getTupleTypeAttribute()
     * @generated
     */
    void setTupleTypeAttribute(TupleTypeAttribute value);

    /**
     * Returns the value of the '<em><b>Variable Declaration</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link fr.tpt.atlanalyser.atl.OCL.VariableDeclaration#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Variable Declaration</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Variable Declaration</em>' container reference.
     * @see #setVariableDeclaration(VariableDeclaration)
     * @see fr.tpt.atlanalyser.atl.OCL.OCLPackage#getOclType_VariableDeclaration()
     * @see fr.tpt.atlanalyser.atl.OCL.VariableDeclaration#getType
     * @model opposite="type" transient="false" ordered="false"
     * @generated
     */
    VariableDeclaration getVariableDeclaration();

    /**
     * Sets the value of the '{@link fr.tpt.atlanalyser.atl.OCL.OclType#getVariableDeclaration <em>Variable Declaration</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Variable Declaration</em>' container reference.
     * @see #getVariableDeclaration()
     * @generated
     */
    void setVariableDeclaration(VariableDeclaration value);

} // OclType
