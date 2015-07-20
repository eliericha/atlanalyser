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
package fr.tpt.atlanalyser.atl.OCL.impl;

import fr.tpt.atlanalyser.atl.OCL.MapType;
import fr.tpt.atlanalyser.atl.OCL.OCLPackage;
import fr.tpt.atlanalyser.atl.OCL.OclType;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Map Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.impl.MapTypeImpl#getValueType <em>Value Type</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.impl.MapTypeImpl#getKeyType <em>Key Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MapTypeImpl extends OclTypeImpl implements MapType {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * The cached value of the '{@link #getValueType() <em>Value Type</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getValueType()
     * @generated
     * @ordered
     */
    protected OclType valueType;

    /**
     * The cached value of the '{@link #getKeyType() <em>Key Type</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getKeyType()
     * @generated
     * @ordered
     */
    protected OclType keyType;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected MapTypeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return OCLPackage.Literals.MAP_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public OclType getValueType() {
        return valueType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetValueType(OclType newValueType, NotificationChain msgs) {
        OclType oldValueType = valueType;
        valueType = newValueType;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OCLPackage.MAP_TYPE__VALUE_TYPE, oldValueType, newValueType);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setValueType(OclType newValueType) {
        if (newValueType != valueType) {
            NotificationChain msgs = null;
            if (valueType != null)
                msgs = ((InternalEObject)valueType).eInverseRemove(this, OCLPackage.OCL_TYPE__MAP_TYPE2, OclType.class, msgs);
            if (newValueType != null)
                msgs = ((InternalEObject)newValueType).eInverseAdd(this, OCLPackage.OCL_TYPE__MAP_TYPE2, OclType.class, msgs);
            msgs = basicSetValueType(newValueType, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, OCLPackage.MAP_TYPE__VALUE_TYPE, newValueType, newValueType));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public OclType getKeyType() {
        return keyType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetKeyType(OclType newKeyType, NotificationChain msgs) {
        OclType oldKeyType = keyType;
        keyType = newKeyType;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OCLPackage.MAP_TYPE__KEY_TYPE, oldKeyType, newKeyType);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setKeyType(OclType newKeyType) {
        if (newKeyType != keyType) {
            NotificationChain msgs = null;
            if (keyType != null)
                msgs = ((InternalEObject)keyType).eInverseRemove(this, OCLPackage.OCL_TYPE__MAP_TYPE, OclType.class, msgs);
            if (newKeyType != null)
                msgs = ((InternalEObject)newKeyType).eInverseAdd(this, OCLPackage.OCL_TYPE__MAP_TYPE, OclType.class, msgs);
            msgs = basicSetKeyType(newKeyType, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, OCLPackage.MAP_TYPE__KEY_TYPE, newKeyType, newKeyType));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case OCLPackage.MAP_TYPE__VALUE_TYPE:
                if (valueType != null)
                    msgs = ((InternalEObject)valueType).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OCLPackage.MAP_TYPE__VALUE_TYPE, null, msgs);
                return basicSetValueType((OclType)otherEnd, msgs);
            case OCLPackage.MAP_TYPE__KEY_TYPE:
                if (keyType != null)
                    msgs = ((InternalEObject)keyType).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OCLPackage.MAP_TYPE__KEY_TYPE, null, msgs);
                return basicSetKeyType((OclType)otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case OCLPackage.MAP_TYPE__VALUE_TYPE:
                return basicSetValueType(null, msgs);
            case OCLPackage.MAP_TYPE__KEY_TYPE:
                return basicSetKeyType(null, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case OCLPackage.MAP_TYPE__VALUE_TYPE:
                return getValueType();
            case OCLPackage.MAP_TYPE__KEY_TYPE:
                return getKeyType();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case OCLPackage.MAP_TYPE__VALUE_TYPE:
                setValueType((OclType)newValue);
                return;
            case OCLPackage.MAP_TYPE__KEY_TYPE:
                setKeyType((OclType)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case OCLPackage.MAP_TYPE__VALUE_TYPE:
                setValueType((OclType)null);
                return;
            case OCLPackage.MAP_TYPE__KEY_TYPE:
                setKeyType((OclType)null);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case OCLPackage.MAP_TYPE__VALUE_TYPE:
                return valueType != null;
            case OCLPackage.MAP_TYPE__KEY_TYPE:
                return keyType != null;
        }
        return super.eIsSet(featureID);
    }

} //MapTypeImpl
