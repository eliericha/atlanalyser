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

import fr.tpt.atlanalyser.atl.ATL.impl.LocatedElementImpl;

import fr.tpt.atlanalyser.atl.OCL.OCLPackage;
import fr.tpt.atlanalyser.atl.OCL.OclContextDefinition;
import fr.tpt.atlanalyser.atl.OCL.OclFeature;
import fr.tpt.atlanalyser.atl.OCL.OclFeatureDefinition;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Ocl Feature Definition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.impl.OclFeatureDefinitionImpl#getFeature <em>Feature</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.OCL.impl.OclFeatureDefinitionImpl#getContext_ <em>Context </em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OclFeatureDefinitionImpl extends LocatedElementImpl implements OclFeatureDefinition {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * The cached value of the '{@link #getFeature() <em>Feature</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFeature()
     * @generated
     * @ordered
     */
    protected OclFeature feature;

    /**
     * The cached value of the '{@link #getContext_() <em>Context </em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getContext_()
     * @generated
     * @ordered
     */
    protected OclContextDefinition context_;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected OclFeatureDefinitionImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return OCLPackage.Literals.OCL_FEATURE_DEFINITION;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public OclFeature getFeature() {
        return feature;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetFeature(OclFeature newFeature, NotificationChain msgs) {
        OclFeature oldFeature = feature;
        feature = newFeature;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OCLPackage.OCL_FEATURE_DEFINITION__FEATURE, oldFeature, newFeature);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFeature(OclFeature newFeature) {
        if (newFeature != feature) {
            NotificationChain msgs = null;
            if (feature != null)
                msgs = ((InternalEObject)feature).eInverseRemove(this, OCLPackage.OCL_FEATURE__DEFINITION, OclFeature.class, msgs);
            if (newFeature != null)
                msgs = ((InternalEObject)newFeature).eInverseAdd(this, OCLPackage.OCL_FEATURE__DEFINITION, OclFeature.class, msgs);
            msgs = basicSetFeature(newFeature, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, OCLPackage.OCL_FEATURE_DEFINITION__FEATURE, newFeature, newFeature));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public OclContextDefinition getContext_() {
        return context_;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetContext_(OclContextDefinition newContext_, NotificationChain msgs) {
        OclContextDefinition oldContext_ = context_;
        context_ = newContext_;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OCLPackage.OCL_FEATURE_DEFINITION__CONTEXT_, oldContext_, newContext_);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setContext_(OclContextDefinition newContext_) {
        if (newContext_ != context_) {
            NotificationChain msgs = null;
            if (context_ != null)
                msgs = ((InternalEObject)context_).eInverseRemove(this, OCLPackage.OCL_CONTEXT_DEFINITION__DEFINITION, OclContextDefinition.class, msgs);
            if (newContext_ != null)
                msgs = ((InternalEObject)newContext_).eInverseAdd(this, OCLPackage.OCL_CONTEXT_DEFINITION__DEFINITION, OclContextDefinition.class, msgs);
            msgs = basicSetContext_(newContext_, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, OCLPackage.OCL_FEATURE_DEFINITION__CONTEXT_, newContext_, newContext_));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case OCLPackage.OCL_FEATURE_DEFINITION__FEATURE:
                if (feature != null)
                    msgs = ((InternalEObject)feature).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OCLPackage.OCL_FEATURE_DEFINITION__FEATURE, null, msgs);
                return basicSetFeature((OclFeature)otherEnd, msgs);
            case OCLPackage.OCL_FEATURE_DEFINITION__CONTEXT_:
                if (context_ != null)
                    msgs = ((InternalEObject)context_).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OCLPackage.OCL_FEATURE_DEFINITION__CONTEXT_, null, msgs);
                return basicSetContext_((OclContextDefinition)otherEnd, msgs);
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
            case OCLPackage.OCL_FEATURE_DEFINITION__FEATURE:
                return basicSetFeature(null, msgs);
            case OCLPackage.OCL_FEATURE_DEFINITION__CONTEXT_:
                return basicSetContext_(null, msgs);
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
            case OCLPackage.OCL_FEATURE_DEFINITION__FEATURE:
                return getFeature();
            case OCLPackage.OCL_FEATURE_DEFINITION__CONTEXT_:
                return getContext_();
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
            case OCLPackage.OCL_FEATURE_DEFINITION__FEATURE:
                setFeature((OclFeature)newValue);
                return;
            case OCLPackage.OCL_FEATURE_DEFINITION__CONTEXT_:
                setContext_((OclContextDefinition)newValue);
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
            case OCLPackage.OCL_FEATURE_DEFINITION__FEATURE:
                setFeature((OclFeature)null);
                return;
            case OCLPackage.OCL_FEATURE_DEFINITION__CONTEXT_:
                setContext_((OclContextDefinition)null);
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
            case OCLPackage.OCL_FEATURE_DEFINITION__FEATURE:
                return feature != null;
            case OCLPackage.OCL_FEATURE_DEFINITION__CONTEXT_:
                return context_ != null;
        }
        return super.eIsSet(featureID);
    }

} //OclFeatureDefinitionImpl
