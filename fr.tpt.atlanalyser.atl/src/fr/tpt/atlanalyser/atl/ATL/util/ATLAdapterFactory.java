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
package fr.tpt.atlanalyser.atl.ATL.util;

import fr.tpt.atlanalyser.atl.ATL.*;

import fr.tpt.atlanalyser.atl.OCL.VariableDeclaration;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see fr.tpt.atlanalyser.atl.ATL.ATLPackage
 * @generated
 */
public class ATLAdapterFactory extends AdapterFactoryImpl {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Elie Richa - initial implementation";

    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static ATLPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ATLAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = ATLPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject)object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ATLSwitch<Adapter> modelSwitch =
        new ATLSwitch<Adapter>() {
            @Override
            public Adapter caseLocatedElement(LocatedElement object) {
                return createLocatedElementAdapter();
            }
            @Override
            public Adapter caseUnit(Unit object) {
                return createUnitAdapter();
            }
            @Override
            public Adapter caseLibrary(Library object) {
                return createLibraryAdapter();
            }
            @Override
            public Adapter caseQuery(Query object) {
                return createQueryAdapter();
            }
            @Override
            public Adapter caseModule(Module object) {
                return createModuleAdapter();
            }
            @Override
            public Adapter caseModuleElement(ModuleElement object) {
                return createModuleElementAdapter();
            }
            @Override
            public Adapter caseHelper(Helper object) {
                return createHelperAdapter();
            }
            @Override
            public Adapter caseRule(Rule object) {
                return createRuleAdapter();
            }
            @Override
            public Adapter caseMatchedRule(MatchedRule object) {
                return createMatchedRuleAdapter();
            }
            @Override
            public Adapter caseLazyMatchedRule(LazyMatchedRule object) {
                return createLazyMatchedRuleAdapter();
            }
            @Override
            public Adapter caseCalledRule(CalledRule object) {
                return createCalledRuleAdapter();
            }
            @Override
            public Adapter caseInPattern(InPattern object) {
                return createInPatternAdapter();
            }
            @Override
            public Adapter caseOutPattern(OutPattern object) {
                return createOutPatternAdapter();
            }
            @Override
            public Adapter caseDropPattern(DropPattern object) {
                return createDropPatternAdapter();
            }
            @Override
            public Adapter casePatternElement(PatternElement object) {
                return createPatternElementAdapter();
            }
            @Override
            public Adapter caseInPatternElement(InPatternElement object) {
                return createInPatternElementAdapter();
            }
            @Override
            public Adapter caseSimpleInPatternElement(SimpleInPatternElement object) {
                return createSimpleInPatternElementAdapter();
            }
            @Override
            public Adapter caseOutPatternElement(OutPatternElement object) {
                return createOutPatternElementAdapter();
            }
            @Override
            public Adapter caseSimpleOutPatternElement(SimpleOutPatternElement object) {
                return createSimpleOutPatternElementAdapter();
            }
            @Override
            public Adapter caseForEachOutPatternElement(ForEachOutPatternElement object) {
                return createForEachOutPatternElementAdapter();
            }
            @Override
            public Adapter caseBinding(Binding object) {
                return createBindingAdapter();
            }
            @Override
            public Adapter caseRuleVariableDeclaration(RuleVariableDeclaration object) {
                return createRuleVariableDeclarationAdapter();
            }
            @Override
            public Adapter caseLibraryRef(LibraryRef object) {
                return createLibraryRefAdapter();
            }
            @Override
            public Adapter caseActionBlock(ActionBlock object) {
                return createActionBlockAdapter();
            }
            @Override
            public Adapter caseStatement(Statement object) {
                return createStatementAdapter();
            }
            @Override
            public Adapter caseExpressionStat(ExpressionStat object) {
                return createExpressionStatAdapter();
            }
            @Override
            public Adapter caseBindingStat(BindingStat object) {
                return createBindingStatAdapter();
            }
            @Override
            public Adapter caseIfStat(IfStat object) {
                return createIfStatAdapter();
            }
            @Override
            public Adapter caseForStat(ForStat object) {
                return createForStatAdapter();
            }
            @Override
            public Adapter caseVariableDeclaration(VariableDeclaration object) {
                return createVariableDeclarationAdapter();
            }
            @Override
            public Adapter defaultCase(EObject object) {
                return createEObjectAdapter();
            }
        };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return modelSwitch.doSwitch((EObject)target);
    }


    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.LocatedElement <em>Located Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.LocatedElement
     * @generated
     */
    public Adapter createLocatedElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.Unit <em>Unit</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.Unit
     * @generated
     */
    public Adapter createUnitAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.Library <em>Library</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.Library
     * @generated
     */
    public Adapter createLibraryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.Query <em>Query</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.Query
     * @generated
     */
    public Adapter createQueryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.Module <em>Module</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.Module
     * @generated
     */
    public Adapter createModuleAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.ModuleElement <em>Module Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.ModuleElement
     * @generated
     */
    public Adapter createModuleElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.Helper <em>Helper</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.Helper
     * @generated
     */
    public Adapter createHelperAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.Rule <em>Rule</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.Rule
     * @generated
     */
    public Adapter createRuleAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.MatchedRule <em>Matched Rule</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.MatchedRule
     * @generated
     */
    public Adapter createMatchedRuleAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.LazyMatchedRule <em>Lazy Matched Rule</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.LazyMatchedRule
     * @generated
     */
    public Adapter createLazyMatchedRuleAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.CalledRule <em>Called Rule</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.CalledRule
     * @generated
     */
    public Adapter createCalledRuleAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.InPattern <em>In Pattern</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.InPattern
     * @generated
     */
    public Adapter createInPatternAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.OutPattern <em>Out Pattern</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.OutPattern
     * @generated
     */
    public Adapter createOutPatternAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.DropPattern <em>Drop Pattern</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.DropPattern
     * @generated
     */
    public Adapter createDropPatternAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.PatternElement <em>Pattern Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.PatternElement
     * @generated
     */
    public Adapter createPatternElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.InPatternElement <em>In Pattern Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.InPatternElement
     * @generated
     */
    public Adapter createInPatternElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.SimpleInPatternElement <em>Simple In Pattern Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.SimpleInPatternElement
     * @generated
     */
    public Adapter createSimpleInPatternElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.OutPatternElement <em>Out Pattern Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.OutPatternElement
     * @generated
     */
    public Adapter createOutPatternElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.SimpleOutPatternElement <em>Simple Out Pattern Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.SimpleOutPatternElement
     * @generated
     */
    public Adapter createSimpleOutPatternElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.ForEachOutPatternElement <em>For Each Out Pattern Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.ForEachOutPatternElement
     * @generated
     */
    public Adapter createForEachOutPatternElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.Binding <em>Binding</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.Binding
     * @generated
     */
    public Adapter createBindingAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.RuleVariableDeclaration <em>Rule Variable Declaration</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.RuleVariableDeclaration
     * @generated
     */
    public Adapter createRuleVariableDeclarationAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.LibraryRef <em>Library Ref</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.LibraryRef
     * @generated
     */
    public Adapter createLibraryRefAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.ActionBlock <em>Action Block</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.ActionBlock
     * @generated
     */
    public Adapter createActionBlockAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.Statement <em>Statement</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.Statement
     * @generated
     */
    public Adapter createStatementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.ExpressionStat <em>Expression Stat</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.ExpressionStat
     * @generated
     */
    public Adapter createExpressionStatAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.BindingStat <em>Binding Stat</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.BindingStat
     * @generated
     */
    public Adapter createBindingStatAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.IfStat <em>If Stat</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.IfStat
     * @generated
     */
    public Adapter createIfStatAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.ATL.ForStat <em>For Stat</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.ATL.ForStat
     * @generated
     */
    public Adapter createForStatAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link fr.tpt.atlanalyser.atl.OCL.VariableDeclaration <em>Variable Declaration</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see fr.tpt.atlanalyser.atl.OCL.VariableDeclaration
     * @generated
     */
    public Adapter createVariableDeclarationAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} //ATLAdapterFactory
