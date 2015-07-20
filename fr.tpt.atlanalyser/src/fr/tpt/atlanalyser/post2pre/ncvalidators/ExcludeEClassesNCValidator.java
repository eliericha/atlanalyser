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
package fr.tpt.atlanalyser.post2pre.ncvalidators;

import java.util.HashSet;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public class ExcludeEClassesNCValidator implements NestedConditionValidator {

    private HashSet<EClass> excludedEClasses;

    public ExcludeEClassesNCValidator(List<EClass> toExclude) {
        this.excludedEClasses = Sets.newHashSet(toExclude);
    }

    @Override
    public boolean isValid(NestedCondition nc) {
        Graph conclusion = nc.getConclusion();
        for (Node n : conclusion.getNodes()) {
            if (excludedEClasses.contains(n.getType())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Exclude EClasses: ["
                + Joiner.on(", ").join(
                        excludedEClasses.stream().map(c -> c.getName())
                                .toArray(String[]::new)) + "]";
    }
}
