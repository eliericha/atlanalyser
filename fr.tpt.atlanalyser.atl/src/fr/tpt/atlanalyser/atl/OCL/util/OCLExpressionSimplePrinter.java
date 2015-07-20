/*******************************************************************************
 * Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Elie Richa - initial implementation
 *******************************************************************************/
package fr.tpt.atlanalyser.atl.OCL.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import fr.tpt.atlanalyser.atl.ATL.LocatedElement;
import fr.tpt.atlanalyser.atl.OCL.BooleanExp;
import fr.tpt.atlanalyser.atl.OCL.CollectionOperationCallExp;
import fr.tpt.atlanalyser.atl.OCL.IfExp;
import fr.tpt.atlanalyser.atl.OCL.IntegerExp;
import fr.tpt.atlanalyser.atl.OCL.IteratorExp;
import fr.tpt.atlanalyser.atl.OCL.NavigationOrAttributeCallExp;
import fr.tpt.atlanalyser.atl.OCL.OclExpression;
import fr.tpt.atlanalyser.atl.OCL.OclModelElement;
import fr.tpt.atlanalyser.atl.OCL.OclType;
import fr.tpt.atlanalyser.atl.OCL.OperationCallExp;
import fr.tpt.atlanalyser.atl.OCL.OperatorCallExp;
import fr.tpt.atlanalyser.atl.OCL.StringExp;
import fr.tpt.atlanalyser.atl.OCL.VariableDeclaration;
import fr.tpt.atlanalyser.atl.OCL.VariableExp;

public class OCLExpressionSimplePrinter extends OCLSwitch<String> {

    public OCLExpressionSimplePrinter() {
        super();
    }

    @Override
    public String caseNavigationOrAttributeCallExp(
            NavigationOrAttributeCallExp object) {
        return this.doSwitch(object.getSource()) + "." + object.getName();
    }

    @Override
    public String caseOperatorCallExp(OperatorCallExp object) {
        final String op = object.getOperationName();

        final EList<OclExpression> args = object.getArguments();
        switch (args.size()) {
        case 0:
            return String.format("%s (%s)", op,
                    this.doSwitch(object.getSource()));
        case 1:
            return String.format("(%s) %s (%s)",
                    this.doSwitch(object.getSource()), op,
                    this.doSwitch(args.get(0)));
        default:
            return "error";
        }
    }

    @Override
    public String caseOperationCallExp(OperationCallExp object) {

        String res = "(" + this.doSwitch(object.getSource()) + ")."
                + object.getOperationName() + "(";

        List<OclExpression> args = object.getArguments();
        List<String> argExps = new ArrayList<String>();
        for (OclExpression oclExpression : args) {
            argExps.add(this.doSwitch(oclExpression));
        }

        res += Joiner.on(", ").join(argExps);

        res += ")";

        return res;
    }

    @Override
    public String caseVariableExp(VariableExp object) {
        final String varName = object.getReferredVariable().getVarName();
        return varName;
    }

    @Override
    public String caseBooleanExp(BooleanExp object) {
        // booleanSymbol is a Boolean EAttribute that holds the value of
        // the boolean literal.
        return object.isBooleanSymbol() ? "true" : "false";
    }

    @Override
    public String caseCollectionOperationCallExp(
            CollectionOperationCallExp object) {
        String res = this.doSwitch(object.getSource()) + "->"
                + object.getOperationName() + "(";

        final Iterator<OclExpression> args = object.getArguments().iterator();

        while (args.hasNext()) {
            OclExpression arg = args.next();
            res += this.doSwitch(arg);

            if (args.hasNext()) {
                res += ", ";
            }
        }

        res += ")";

        return res;
    }

    @Override
    public String caseIteratorExp(IteratorExp object) {
        String res = this.doSwitch(object.getSource()) + "->"
                + object.getName() + "(";

        res += Joiner.on(", ").join(toStrings(object.getIterators()));

        res += " | " + doSwitch(object.getBody()) + ")";

        return res;
    }

    @Override
    public String caseVariableDeclaration(VariableDeclaration object) {
        OclType type = object.getType();
        return object.getVarName() + ((type != null) ? " : " + doSwitch(type) : "");
    }

    @Override
    public String caseStringExp(StringExp object) {
        return String.format("'%s'", object.getStringSymbol());
    }

    @Override
    public String caseIntegerExp(IntegerExp object) {
        return Integer.toString(object.getIntegerSymbol());
    }

    @Override
    public String caseIfExp(IfExp object) {
        return String.format("(if %s then %s else %s endif)",
                this.doSwitch(object.getCondition()),
                this.doSwitch(object.getThenExpression()),
                this.doSwitch(object.getElseExpression()));
    }

    @Override
    public String caseOclModelElement(OclModelElement object) {
        return String.format("%s!%s", object.getModel().getName(),
                object.getName());
    }

    public String print(OclExpression exp) {
        return this.doSwitch(exp);
    }

    public static String toString(OclExpression exp) {
        return new OCLExpressionSimplePrinter().print(exp);
    }

    private static List<String> toStrings(List<? extends LocatedElement> exps) {
        final OCLExpressionSimplePrinter printer = new OCLExpressionSimplePrinter();
        return Lists.newArrayList(Lists.transform(exps,
                new Function<LocatedElement, String>() {
                    @Override
                    public String apply(LocatedElement arg0) {
                        return printer.doSwitch(arg0);
                    }
                }));
    }

}