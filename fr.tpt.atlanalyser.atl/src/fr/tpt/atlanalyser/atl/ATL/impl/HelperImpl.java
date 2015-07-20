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
import fr.tpt.atlanalyser.atl.ATL.Helper;
import fr.tpt.atlanalyser.atl.ATL.Library;
import fr.tpt.atlanalyser.atl.ATL.Query;

import fr.tpt.atlanalyser.atl.OCL.OclFeatureDefinition;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Helper</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.impl.HelperImpl#getQuery <em>Query</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.impl.HelperImpl#getLibrary <em>Library</em>}</li>
 *   <li>{@link fr.tpt.atlanalyser.atl.ATL.impl.HelperImpl#getDefinition <em>Definition</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class HelperImpl extends ModuleElementImpl implements Helper {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * The cached value of the '{@link #getQuery() <em>Query</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getQuery()
     * @generated
     * @ordered
     */
    protected Query query;

    /**
     * The cached value of the '{@link #getLibrary() <em>Library</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLibrary()
     * @generated
     * @ordered
     */
    protected Library library;

    /**
     * The cached value of the '{@link #getDefinition() <em>Definition</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDefinition()
     * @generated
     * @ordered
     */
    protected OclFeatureDefinition definition;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected HelperImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ATLPackage.Literals.HELPER;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Query getQuery() {
        if (query != null && query.eIsProxy()) {
            InternalEObject oldQuery = (InternalEObject)query;
            query = (Query)eResolveProxy(oldQuery);
            if (query != oldQuery) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, ATLPackage.HELPER__QUERY, oldQuery, query));
            }
        }
        return query;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Query basicGetQuery() {
        return query;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetQuery(Query newQuery, NotificationChain msgs) {
        Query oldQuery = query;
        query = newQuery;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ATLPackage.HELPER__QUERY, oldQuery, newQuery);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setQuery(Query newQuery) {
        if (newQuery != query) {
            NotificationChain msgs = null;
            if (query != null)
                msgs = ((InternalEObject)query).eInverseRemove(this, ATLPackage.QUERY__HELPERS, Query.class, msgs);
            if (newQuery != null)
                msgs = ((InternalEObject)newQuery).eInverseAdd(this, ATLPackage.QUERY__HELPERS, Query.class, msgs);
            msgs = basicSetQuery(newQuery, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ATLPackage.HELPER__QUERY, newQuery, newQuery));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Library getLibrary() {
        if (library != null && library.eIsProxy()) {
            InternalEObject oldLibrary = (InternalEObject)library;
            library = (Library)eResolveProxy(oldLibrary);
            if (library != oldLibrary) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, ATLPackage.HELPER__LIBRARY, oldLibrary, library));
            }
        }
        return library;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Library basicGetLibrary() {
        return library;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetLibrary(Library newLibrary, NotificationChain msgs) {
        Library oldLibrary = library;
        library = newLibrary;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ATLPackage.HELPER__LIBRARY, oldLibrary, newLibrary);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLibrary(Library newLibrary) {
        if (newLibrary != library) {
            NotificationChain msgs = null;
            if (library != null)
                msgs = ((InternalEObject)library).eInverseRemove(this, ATLPackage.LIBRARY__HELPERS, Library.class, msgs);
            if (newLibrary != null)
                msgs = ((InternalEObject)newLibrary).eInverseAdd(this, ATLPackage.LIBRARY__HELPERS, Library.class, msgs);
            msgs = basicSetLibrary(newLibrary, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ATLPackage.HELPER__LIBRARY, newLibrary, newLibrary));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public OclFeatureDefinition getDefinition() {
        return definition;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetDefinition(OclFeatureDefinition newDefinition, NotificationChain msgs) {
        OclFeatureDefinition oldDefinition = definition;
        definition = newDefinition;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ATLPackage.HELPER__DEFINITION, oldDefinition, newDefinition);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDefinition(OclFeatureDefinition newDefinition) {
        if (newDefinition != definition) {
            NotificationChain msgs = null;
            if (definition != null)
                msgs = ((InternalEObject)definition).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ATLPackage.HELPER__DEFINITION, null, msgs);
            if (newDefinition != null)
                msgs = ((InternalEObject)newDefinition).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ATLPackage.HELPER__DEFINITION, null, msgs);
            msgs = basicSetDefinition(newDefinition, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ATLPackage.HELPER__DEFINITION, newDefinition, newDefinition));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case ATLPackage.HELPER__QUERY:
                if (query != null)
                    msgs = ((InternalEObject)query).eInverseRemove(this, ATLPackage.QUERY__HELPERS, Query.class, msgs);
                return basicSetQuery((Query)otherEnd, msgs);
            case ATLPackage.HELPER__LIBRARY:
                if (library != null)
                    msgs = ((InternalEObject)library).eInverseRemove(this, ATLPackage.LIBRARY__HELPERS, Library.class, msgs);
                return basicSetLibrary((Library)otherEnd, msgs);
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
            case ATLPackage.HELPER__QUERY:
                return basicSetQuery(null, msgs);
            case ATLPackage.HELPER__LIBRARY:
                return basicSetLibrary(null, msgs);
            case ATLPackage.HELPER__DEFINITION:
                return basicSetDefinition(null, msgs);
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
            case ATLPackage.HELPER__QUERY:
                if (resolve) return getQuery();
                return basicGetQuery();
            case ATLPackage.HELPER__LIBRARY:
                if (resolve) return getLibrary();
                return basicGetLibrary();
            case ATLPackage.HELPER__DEFINITION:
                return getDefinition();
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
            case ATLPackage.HELPER__QUERY:
                setQuery((Query)newValue);
                return;
            case ATLPackage.HELPER__LIBRARY:
                setLibrary((Library)newValue);
                return;
            case ATLPackage.HELPER__DEFINITION:
                setDefinition((OclFeatureDefinition)newValue);
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
            case ATLPackage.HELPER__QUERY:
                setQuery((Query)null);
                return;
            case ATLPackage.HELPER__LIBRARY:
                setLibrary((Library)null);
                return;
            case ATLPackage.HELPER__DEFINITION:
                setDefinition((OclFeatureDefinition)null);
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
            case ATLPackage.HELPER__QUERY:
                return query != null;
            case ATLPackage.HELPER__LIBRARY:
                return library != null;
            case ATLPackage.HELPER__DEFINITION:
                return definition != null;
        }
        return super.eIsSet(featureID);
    }

} //HelperImpl
