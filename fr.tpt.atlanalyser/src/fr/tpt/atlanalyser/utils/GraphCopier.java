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
package fr.tpt.atlanalyser.utils;

import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.HenshinFactory;

public class GraphCopier {

    /**
     * Create a copy of graph g and return a morphism from g to its new copy.
     * 
     * @param g
     * @return
     */
    public static Morphism copy(Graph g) {
        Graph copy = HenshinFactory.eINSTANCE.createGraph();
        copy.setName(g.getName());
        copy.setDescription(g.getDescription());
        // Don't copy the formula
        // copy.setFormula(value);
        EcoreUtil.Copier copier = new EcoreUtil.Copier();
        copy.getNodes().addAll(copier.copyAll(g.getNodes()));
        copy.getEdges().addAll(copier.copyAll(g.getEdges()));
        copier.copyReferences();

        Morphism origToCopy = new Morphism(g, copy);
        origToCopy.add(copier);

        return origToCopy;
    }

    /**
     * Create a copy of graph g and return a morphism from the new copy to g.
     * 
     * @param g
     * @return
     */
    public static Morphism reverseCopy(Graph g) {
        Graph copy = HenshinFactory.eINSTANCE.createGraph();
        copy.setName(g.getName());
        copy.setDescription(g.getDescription());
        // Don't copy the formula
        // copy.setFormula(value);
        EcoreUtil.Copier copier = new EcoreUtil.Copier();
        copy.getNodes().addAll(copier.copyAll(g.getNodes()));
        copy.getEdges().addAll(copier.copyAll(g.getEdges()));
        copier.copyReferences();

        Morphism origToCopy = new Morphism(copy, g);

        Set<Entry<EObject, EObject>> entrySet = copier.entrySet();
        for (Entry<EObject, EObject> entry : entrySet) {
            if (entry.getKey() instanceof GraphElement) {
                origToCopy.add((GraphElement) entry.getValue(),
                        (GraphElement) entry.getKey());
            }
        }

        return origToCopy;
    }

}
