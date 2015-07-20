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
package fr.tpt.atlanalyser.atl.ATL.impl;

import fr.tpt.atlanalyser.atl.ATL.ATLPackage;
import fr.tpt.atlanalyser.atl.ATL.CalledRule;

import fr.tpt.atlanalyser.atl.OCL.Parameter;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Called Rule</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.impl.CalledRuleImpl#getParameters <em>Parameters</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.impl.CalledRuleImpl#isIsEntrypoint <em>Is Entrypoint</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.impl.CalledRuleImpl#isIsEndpoint <em>Is Endpoint</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CalledRuleImpl extends RuleImpl implements CalledRule {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * The cached value of the '{@link #getParameters() <em>Parameters</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getParameters()
     * @generated
     * @ordered
     */
    protected EList<Parameter> parameters;

    /**
     * The default value of the '{@link #isIsEntrypoint() <em>Is Entrypoint</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIsEntrypoint()
     * @generated
     * @ordered
     */
    protected static final boolean IS_ENTRYPOINT_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isIsEntrypoint() <em>Is Entrypoint</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIsEntrypoint()
     * @generated
     * @ordered
     */
    protected boolean isEntrypoint = IS_ENTRYPOINT_EDEFAULT;

    /**
     * The default value of the '{@link #isIsEndpoint() <em>Is Endpoint</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIsEndpoint()
     * @generated
     * @ordered
     */
    protected static final boolean IS_ENDPOINT_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isIsEndpoint() <em>Is Endpoint</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIsEndpoint()
     * @generated
     * @ordered
     */
    protected boolean isEndpoint = IS_ENDPOINT_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected CalledRuleImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ATLPackage.Literals.CALLED_RULE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Parameter> getParameters() {
        if (parameters == null) {
            parameters = new EObjectResolvingEList<Parameter>(Parameter.class, this, ATLPackage.CALLED_RULE__PARAMETERS);
        }
        return parameters;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isIsEntrypoint() {
        return isEntrypoint;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIsEntrypoint(boolean newIsEntrypoint) {
        boolean oldIsEntrypoint = isEntrypoint;
        isEntrypoint = newIsEntrypoint;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ATLPackage.CALLED_RULE__IS_ENTRYPOINT, oldIsEntrypoint, isEntrypoint));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isIsEndpoint() {
        return isEndpoint;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIsEndpoint(boolean newIsEndpoint) {
        boolean oldIsEndpoint = isEndpoint;
        isEndpoint = newIsEndpoint;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ATLPackage.CALLED_RULE__IS_ENDPOINT, oldIsEndpoint, isEndpoint));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ATLPackage.CALLED_RULE__PARAMETERS:
                return getParameters();
            case ATLPackage.CALLED_RULE__IS_ENTRYPOINT:
                return isIsEntrypoint();
            case ATLPackage.CALLED_RULE__IS_ENDPOINT:
                return isIsEndpoint();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case ATLPackage.CALLED_RULE__PARAMETERS:
                getParameters().clear();
                getParameters().addAll((Collection<? extends Parameter>)newValue);
                return;
            case ATLPackage.CALLED_RULE__IS_ENTRYPOINT:
                setIsEntrypoint((Boolean)newValue);
                return;
            case ATLPackage.CALLED_RULE__IS_ENDPOINT:
                setIsEndpoint((Boolean)newValue);
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
            case ATLPackage.CALLED_RULE__PARAMETERS:
                getParameters().clear();
                return;
            case ATLPackage.CALLED_RULE__IS_ENTRYPOINT:
                setIsEntrypoint(IS_ENTRYPOINT_EDEFAULT);
                return;
            case ATLPackage.CALLED_RULE__IS_ENDPOINT:
                setIsEndpoint(IS_ENDPOINT_EDEFAULT);
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
            case ATLPackage.CALLED_RULE__PARAMETERS:
                return parameters != null && !parameters.isEmpty();
            case ATLPackage.CALLED_RULE__IS_ENTRYPOINT:
                return isEntrypoint != IS_ENTRYPOINT_EDEFAULT;
            case ATLPackage.CALLED_RULE__IS_ENDPOINT:
                return isEndpoint != IS_ENDPOINT_EDEFAULT;
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (isEntrypoint: ");
        result.append(isEntrypoint);
        result.append(", isEndpoint: ");
        result.append(isEndpoint);
        result.append(')');
        return result.toString();
    }

} //CalledRuleImpl
