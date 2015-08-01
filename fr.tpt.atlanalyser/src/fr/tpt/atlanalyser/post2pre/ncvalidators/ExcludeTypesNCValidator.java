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

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class ExcludeTypesNCValidator implements NestedConditionValidator {

    private HashSet<ENamedElement> excludedTypes;

    public ExcludeTypesNCValidator(Collection<? extends ENamedElement> toExclude) {
        this.excludedTypes = Sets.newHashSet(toExclude);
    }

    @Override
    public boolean isValid(NestedCondition nc) {
        Graph conclusion = nc.getConclusion();

        SetView<ENamedElement> graphTypes = Sets.union(
                conclusion.getNodes().stream().map(Node::getType)
                        .collect(Collectors.toSet()),
                conclusion.getEdges().stream().map(Edge::getType)
                        .collect(Collectors.toSet()));

        if (graphTypes.stream().anyMatch(t1 -> excludedTypes.contains(t1))) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Exclude Types: ["
                + Joiner.on(", ").join(
                        excludedTypes.stream().map(c -> c.getName())
                                .toArray(String[]::new)) + "]";
    }
}
