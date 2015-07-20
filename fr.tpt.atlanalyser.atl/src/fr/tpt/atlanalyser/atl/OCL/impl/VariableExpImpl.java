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

import fr.tpt.atlanalyser.atl.OCL.OCLPackage;
import fr.tpt.atlanalyser.atl.OCL.VariableDeclaration;
import fr.tpt.atlanalyser.atl.OCL.VariableExp;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Variable Exp</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.impl.VariableExpImpl#getReferredVariable <em>Referred Variable</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class VariableExpImpl extends OclExpressionImpl implements VariableExp {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * The cached value of the '{@link #getReferredVariable() <em>Referred Variable</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReferredVariable()
     * @generated
     * @ordered
     */
    protected VariableDeclaration referredVariable;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected VariableExpImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return OCLPackage.Literals.VARIABLE_EXP;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public VariableDeclaration getReferredVariable() {
        if (referredVariable != null && referredVariable.eIsProxy()) {
            InternalEObject oldReferredVariable = (InternalEObject)referredVariable;
            referredVariable = (VariableDeclaration)eResolveProxy(oldReferredVariable);
            if (referredVariable != oldReferredVariable) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, OCLPackage.VARIABLE_EXP__REFERRED_VARIABLE, oldReferredVariable, referredVariable));
            }
        }
        return referredVariable;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public VariableDeclaration basicGetReferredVariable() {
        return referredVariable;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetReferredVariable(VariableDeclaration newReferredVariable, NotificationChain msgs) {
        VariableDeclaration oldReferredVariable = referredVariable;
        referredVariable = newReferredVariable;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OCLPackage.VARIABLE_EXP__REFERRED_VARIABLE, oldReferredVariable, newReferredVariable);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setReferredVariable(VariableDeclaration newReferredVariable) {
        if (newReferredVariable != referredVariable) {
            NotificationChain msgs = null;
            if (referredVariable != null)
                msgs = ((InternalEObject)referredVariable).eInverseRemove(this, OCLPackage.VARIABLE_DECLARATION__VARIABLE_EXP, VariableDeclaration.class, msgs);
            if (newReferredVariable != null)
                msgs = ((InternalEObject)newReferredVariable).eInverseAdd(this, OCLPackage.VARIABLE_DECLARATION__VARIABLE_EXP, VariableDeclaration.class, msgs);
            msgs = basicSetReferredVariable(newReferredVariable, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, OCLPackage.VARIABLE_EXP__REFERRED_VARIABLE, newReferredVariable, newReferredVariable));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case OCLPackage.VARIABLE_EXP__REFERRED_VARIABLE:
                if (referredVariable != null)
                    msgs = ((InternalEObject)referredVariable).eInverseRemove(this, OCLPackage.VARIABLE_DECLARATION__VARIABLE_EXP, VariableDeclaration.class, msgs);
                return basicSetReferredVariable((VariableDeclaration)otherEnd, msgs);
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
            case OCLPackage.VARIABLE_EXP__REFERRED_VARIABLE:
                return basicSetReferredVariable(null, msgs);
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
            case OCLPackage.VARIABLE_EXP__REFERRED_VARIABLE:
                if (resolve) return getReferredVariable();
                return basicGetReferredVariable();
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
            case OCLPackage.VARIABLE_EXP__REFERRED_VARIABLE:
                setReferredVariable((VariableDeclaration)newValue);
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
            case OCLPackage.VARIABLE_EXP__REFERRED_VARIABLE:
                setReferredVariable((VariableDeclaration)null);
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
            case OCLPackage.VARIABLE_EXP__REFERRED_VARIABLE:
                return referredVariable != null;
        }
        return super.eIsSet(featureID);
    }

} //VariableExpImpl
